/*
 * HailEffect.java
 *
 * Created on May 6, 2007 8:15 PM
 *
 * This file is a part of Shoddy Battle.
 * Copyright (C) 2006  Colin Fitzpatrick
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.pokenet.server.battle.mechanics.statuses.field;

import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.JewelMechanics;
import org.pokenet.server.battle.mechanics.PokemonType;
import org.pokenet.server.battle.mechanics.StatMultiplier;
import org.pokenet.server.battle.mechanics.moves.MoveList;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;
import org.pokenet.server.battle.mechanics.moves.StatusMove;
import org.pokenet.server.battle.mechanics.statuses.ChargeEffect;
import org.pokenet.server.battle.mechanics.statuses.FreezeEffect;
import org.pokenet.server.battle.mechanics.statuses.PercentEffect;
import org.pokenet.server.battle.mechanics.statuses.StatusEffect;

/**
 * 1. Hurts all pokemon with 1/16 HP if they are not Ice types.
 * 2. Cuts the power of Solarbeam to 60.
 * 3. Makes Weather Ball a power 100 Ice-type move.
 * 4. Makes Moonlight and Morning Sun restore 1/4 of the user's max HP.
 * 5. Increases the evasion of a Pokemon with Snow Cloak by 20%
 *
 * @author Ben
 */
public class HailEffect extends WeatherEffect {
    
    /** Creates a new instance of HailEffect */
    public HailEffect(int turns) {
        super(turns);
    }
    
    public String getName() {
        return "Hail";
    }
    
    public HailEffect() {
        super(5);
    }
    
    public String getDescription() {
        return null;
    }
    
    public boolean immobilises(Pokemon p) {
        return false;
    }
    
    /**
     * Tick this effect for the whole field.
     */
    protected void tickWeather(BattleField field) {
        field.showMessage("The hail continues to fall.");
    }
    
    /**
     * Remove this effect from a field.
     */
    public void unapplyToField(BattleField field) {
        field.showMessage("The hail stopped.");
    }
    
    /**
     * Does 1/16 of a Pokemon's health worth of damage each turn if the Pokemon is not
     * Ice type.
     */ 
    public boolean tickPokemon(Pokemon p) {
        if (!hasEffects(p.getField()))
            return false;
        
        if (p.hasAbility("Ice Body")) {
            p.getField().showMessage(p.getName() + " absorbed the hail!");
            p.changeHealth(p.getStat(Pokemon.S_HP) / 16);
            return false;
        }
        if (p.isType(PokemonType.T_ICE))
            return false;
    
        int maximum = p.getStat(Pokemon.S_HP);
        int damage = maximum / 16;
        if (damage < 1) damage = 1;
        p.getField().showMessage(p.getName() + " is pelted by hail!");
        p.changeHealth(-damage, true);
        return false;
    }
    
    /**
     * Apply this effect to a field.
     */
    public boolean applyToField(BattleField field) {
        field.showMessage("Hail began to fall!");
        return true;
    }
    
    /**
     * 1. Hurts all pokemon with 1/16 HP if they are not Ice types.
     * 2. Cuts the power of Solarbeam to 60.
     * 3. Makes Weather Ball a power 100 Ice-type move.
     * 4. Makes Moonlight and Morning Sun restore 1/4 of the user's max HP.
     */
    public MoveListEntry getTransformedMove(Pokemon poke, MoveListEntry entry) {
        if (!hasEffects(poke.getField()))
            return entry;
        
        PokemonMove move = entry.getMove();
        String name = entry.getName();
        if (name.equals("Solarbeam")) {
            if (move instanceof StatusMove) {
                StatusMove statusMove = (StatusMove)move;
                ChargeEffect charge = (ChargeEffect)statusMove.getEffects()[0];
                charge.getMove().getMove().setPower(60);
            }
        } else if (name.equals("Weather Ball")) {
            move.setPower(100);
            move.setType(PokemonType.T_ICE);
        } else if (name.equals("Moonlight") || name.equals("Morning Sun") || name.equals("Synthesis")) {
            StatusMove statusMove = (StatusMove)move;
            // Assume that the first effect is the PercentEffect!
            PercentEffect perc = (PercentEffect)statusMove.getEffects()[0];
            perc.setPercent(1.0/3.0);
        } else if (name.equals("Blizzard")) {
            if (poke.getField().getMechanics() instanceof JewelMechanics)  {
                return new MoveListEntry("Blizzard", new StatusMove(
                    PokemonType.T_ICE, 120, 0.7, 5, new StatusEffect[] {
                        new FreezeEffect()
                        },
                    new boolean[] { false },
                    new double[] { 0.1 }
                    ) {
                        public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
                            return MoveList.PerfectAccuracyMove.isHit(mech, user, target);
                        }
                    });
            }
        }
        return entry;
    }
    
     public boolean apply(Pokemon p) {
        if (m_applied[p.getParty()] || !(m_applied[p.getParty()] = hasEffects(p.getField())))
            return true;
         
        if (p.hasAbility("Snow Cloak")) {
            StatMultiplier mul = p.getMultiplier(Pokemon.S_EVASION);
            mul.increaseMultiplier();
        }
        setTypes(p, new PokemonType[] { PokemonType.T_ICE }, true);
        return true;
    }
    
    public void unapply(Pokemon p) {
        if (!m_applied[p.getParty()])
            return;
        m_applied[p.getParty()] = false;
        if (p.hasAbility("Snow Cloak")) {
            StatMultiplier mul = p.getMultiplier(Pokemon.S_EVASION);
            mul.decreaseMultiplier();
        }
        setTypes(p, null, false);
    }
    
}
