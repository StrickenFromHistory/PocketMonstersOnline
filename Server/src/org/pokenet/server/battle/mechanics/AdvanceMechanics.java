package org.pokenet.server.battle.mechanics;

import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.moves.MoveList;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;
import org.pokenet.server.battle.mechanics.statuses.StatChangeEffect;
import org.pokenet.server.battle.mechanics.statuses.field.FieldEffect;

/**
 * This class represents the mechanics in the advanced generation of pokemon.
 * @author Colin
 */
public class AdvanceMechanics extends BattleMechanics {
    
    private static final long serialVersionUID = -2238204671194997172L;
    @SuppressWarnings("unused")
	private static int m_log = 0;
    
    /** Creates a new instance of AdvanceMechanics */
    public AdvanceMechanics(int bytes) {
        super(bytes);
    }
    
    public int calculateStat(Pokemon p, int i) throws StatException {
        if ((i < 0) || (i > 5)) throw new StatException();
        int common =
                (int)((int)(((2.0 * p.getBase(i))
                + p.getIv(i)
                + (p.getEv(i) / 4.0)))
                * (p.getLevel() / 100.0));
        if (i == Pokemon.S_HP) {
            if (p.getSpeciesName().equals("Shedinja")) {
                // Shedinja always has 1 hp.
                return 1;
            } else {
                return common + 10 + p.getLevel();
            }
        }
        return (int)((common + 5) * p.getNature().getEffect(i));
    }
    
    /**
     * Return whether the move hit.
     */
    public boolean attemptHit(PokemonMove move, Pokemon user, Pokemon target) {
        BattleField field = user.getField();
        double accuracy = move.getAccuracy();
        boolean hit;
        if ((accuracy != 0.0)
                && (user.hasAbility("No Guard") || target.hasAbility("No Guard") ||
                       user.hasEffect(MoveList.LockOnEffect.class))
                ) {
            hit = true;
        } else {
            double effective = (accuracy
                    * user.getAccuracy().getMultiplier())
                    / target.getEvasion().getMultiplier();
            if (effective > 1.0) effective = 1.0;

            hit = (field.getRandom().nextDouble() <= effective);
        }
        if (!hit) {
            field.showMessage(user.getName() + "'s attack missed!");
        }
        return hit;
    }
    
    public boolean isCriticalHit(PokemonMove move, Pokemon user, Pokemon target) {
        if (target.isCriticalImmune()) {
            return false;
        }
        
        FieldEffect effect = user.getField().getEffectByType(MoveList.LuckyChantEffect.class);
        if (effect != null) {
            MoveList.LuckyChantEffect eff = (MoveList.LuckyChantEffect)effect;
            if (eff.isActive(target.getParty())) {
                return false;
            }
        }
        
        int moveFactor = 0;
        if (move.hasHighCriticalHitRate()) {
            moveFactor = (this instanceof JewelMechanics) ? 1 : 3;
        }
        
        int factor = user.getCriticalHitFactor()
            + (user.hasItem("Scope Lens") ? 1 : 0) /* TODO: + (FE/L * 1) */
            + moveFactor;
        double chance = 0.0;
        switch (factor) {
            case 1:
                chance = 0.0625;
                break;
            case 2:
                chance = 0.125;
                break;
            case 3:
                chance = 0.25;
                break;
            case 4:
                chance = 0.332;
                break;
            default:
                chance = 0.5;
                break;
        }
        return (user.getField().getRandom().nextDouble() <= chance);
    }
    
    /**
     * Return whether a given move deals special damage.
     */
    public boolean isMoveSpecial(PokemonMove move) {
        return move.getType().isSpecial();
    }
    
    public strictfp int calculateDamage(
            PokemonMove move,
            Pokemon attacker, Pokemon defender, boolean silent) {
        
        final BattleField field = attacker.getField();
        PokemonType moveType = move.getType();
        final boolean special = isMoveSpecial(move);
        
        boolean isCritical = move.canCriticalHit() &&
                isCriticalHit(move, attacker, defender);
        
        double attack = attacker.getStat(special
            ? Pokemon.S_SPATTACK
            : Pokemon.S_ATTACK);
        
        int defStat = special
            ? Pokemon.S_SPDEFENCE
            : Pokemon.S_DEFENCE;
        
        StatMultiplier mul = defender.getMultiplier(defStat);
        double defMultiplier = mul.getMultiplier();
        if (isCritical && (defMultiplier > 1.0)) {
            defMultiplier = mul.getSecondaryMultiplier();
        }
        double defence = defender.getStat(defStat, defMultiplier);
        
        final int random = field.getRandom().nextInt(16) + 85;
        
        double multiplier = move.getEffectiveness(attacker, defender);
        
        if (multiplier > 1.0) {
            if (!silent) {
                field.showMessage("It's super effective!");
            }
        } else if (multiplier == 0.0) {
            if (!silent) {
                field.showMessage("It doesn't affect " + defender.getName() + "...");
            }
            // Just return now to prevent a critical hit from occurring.
            return 0;
        } else if (multiplier < 1.0) {
            if (!silent) {
                field.showMessage("It's not very effective...");
            }
        }
        
        final boolean stab = attacker.isType(moveType);
        double stabFactor = attacker.hasAbility("Adaptability") ? 2.0 : 1.5;
        
        int damage = (int)(((int)((int)(((int)((2 * attacker.getLevel()) / 5.0 + 2.0)
        * attack
        * move.getPower())
        / defence)
        / 50.0)
        + 2)
        * (random / 100.0)
        * (stab ? stabFactor : 1.0)
        * multiplier);
        
        if (isCritical) {
            damage *= attacker.hasAbility("Sniper") ? 3 : 2;
            if (defender.hasAbility("Anger Point")) {
                if (!silent) {
                    field.showMessage(defender.getName()
                            + "'s Anger Point raised its attack!");
                }
                StatChangeEffect eff = new StatChangeEffect(
                        Pokemon.S_ATTACK,true, 12);
                eff.setDescription(null);
                defender.addStatus(defender, eff);
            }
            if (!silent) {
                field.showMessage("A critical hit!");
            }
        }
        
        return ((damage < 1) ? 1 : damage);
    }
    
    /**
     * There are several conditions to validate. The total number of effort
     * points must be less than or equal to 510. There can be no more than 255
     * effort points per stat. There can be no more than 31 individual
     * points per stat. The pokemon's level must be in the interval [1, 100].
     */
    public void validateHiddenStats(Pokemon p) throws ValidationException {
        int level = p.getLevel();
        if ((level < 1) || (level > 100))
            throw new ValidationException("Level must be between 1 and 100.");
        
        int evs = 0;
        for (int i = 0; i < 6; ++i) {
            int ev = p.getEv(i);
            evs += ev;
            if (ev > 255)
                throw new ValidationException(
                        "No stat can be allocated more than 255 EVs.");
            if (ev < 0) {
                throw new ValidationException("EVs cannot be negative.");
            }
                
            int iv = p.getIv(i);
            if (iv > 31)
                throw new ValidationException(
                        "No stat can be given more than 31 IVs.");
            if (iv < 0) {
                throw new ValidationException("IVs cannot be negative.");
            }
        }
        if (evs > 510) {
            throw new ValidationException(
                    "A pokemon cannot have more than 510 EVs in total.");
        }
    }
    
}
