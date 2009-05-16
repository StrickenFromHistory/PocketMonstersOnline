/*
 * StatusMove.java
 *
 * Created on December 16, 2006, 1:25 PM
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, visit the Free Software Foundation, Inc.
 * online at http://gnu.org.
 */

package org.pokenet.server.battle.mechanics.moves;
import java.util.Random;

import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.PokemonType;
import org.pokenet.server.battle.mechanics.statuses.SleepEffect;
import org.pokenet.server.battle.mechanics.statuses.StatusEffect;

/**
 *
 * @author Colin
 */
public class StatusMove extends PokemonMove {
    
    private StatusEffect[] m_statuses;
    private double[] m_chances;
    private boolean[] m_attacker;   // Does this status affect the attacker?
    @SuppressWarnings("unused")
	private boolean m_bug = false;
    
    // Is this is a personal enhancement move?
    private boolean m_personal = true;
    
    /**
     * Creates a new instance of StatusMove
     */
    public StatusMove(PokemonType type,
            int power,
            double accuracy,
            int pp,
            StatusEffect[] statuses,
            boolean[] attacker,
            double[] chances) {
        
        super(type, power, accuracy, pp);
        m_statuses = statuses;
        m_chances = chances;
        m_attacker = attacker;
        
        for (int i = 0; i < attacker.length; ++i) {
            if (!attacker[i]) {
                m_personal = false;
                break;
            }
        }
        if (power != 0) {
            m_personal = false;
        }
    }
    
    /**
     * Attempt a hit. Personal enhancement moves never miss, but other status
     * moves can.
     */
    public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
        if (m_personal)
            return true;
        return super.attemptHit(mech, user, target);
    }
    
    public Object clone() {
        StatusMove ret = (StatusMove)super.clone();
        ret.m_statuses = (StatusEffect[])ret.m_statuses.clone();
        ret.m_chances = (double[])ret.m_chances.clone();
        ret.m_attacker = (boolean[])ret.m_attacker.clone();
        for (int i = 0; i < m_statuses.length; ++i) {
            ret.m_statuses[i] = (StatusEffect)ret.m_statuses[i].clone();
        }
        return ret;
    }
    
    public StatusEffect[] getEffects() {
        return m_statuses;
    }
    
    public boolean isAttack() {
        return !m_personal;
    }
    
    public boolean isDamaging() {
        return (m_power != 0);
    }
    
    public boolean isBuggy() {
        return ((m_statuses.length != m_chances.length)
            || (m_chances.length != m_attacker.length));
    }
    
    public boolean getAttacker(int i) {
        return m_attacker[i];
    }
    
    /**
     * Sets if a an effect affects the attacker.
     */
    public void setAttacker(int idx, boolean attacker) {
        if ((idx < 0) || (idx >= m_attacker.length)) return;
        m_attacker[idx] = attacker;
    }
    
    /**
     * Return whether this status effect can be successfully applied. This is
     * very random and not logical at all.
     */
    public boolean isEffective(Pokemon target) {
        for (int i = 0; i < m_statuses.length; ++i) {
            StatusEffect status = m_statuses[i];
            // Exception 1: Non-special effects ignore type immunities.
            if (status.getLock() != StatusEffect.SPECIAL_EFFECT_LOCK)
                continue;
            
            // Exception 2: Sleep ignores type immunities.
            if (status instanceof SleepEffect)
                continue;

            // Exception 3: Wonder Guard pokemon ignore type immunities.
            if (target.hasAbility("Wonder Guard"))
                continue;
                
            return false;
        }
        return true;
    }
    
    /**
     * The rules for whether this move can hit are elaborate. If the move does
     * damage then the normal type rules apply; if the move has no effect then
     * it will not inflict statuses either. However, if the move does not
     * do damage and does not inflict one of the SPECIAL_EFFECT_LOCK statuses
     * then it can hit even types normally immune to the move's type.
     * <br /><br />
     * Two intrinsic abilities also affect this method. If the user has
     * Serene Grace then the chance of each effect being applied is double. If
     * the target has Shield Dust then none of the effects can be applied to it
     * so long as the move does damage.
     */
    public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
        int damage = 0;
        boolean hasSubstitute = target.hasSubstitute();
        final boolean ineffective = (getEffectiveness(user, target) == 0.0);
        if (m_power != 0) {
            damage = mech.calculateDamage(this, user, target);
            target.changeHealth(-damage);
            if (ineffective) {
                return 0;
            }
        } else if (!m_personal && ineffective && !isEffective(target)) {
            user.getField().showMessage("It doesn't affect "
                + target.getName() + "...");
            return 0;
        }
        
        final boolean serene = user.hasAbility("Serene Grace");
        final boolean immune = (target.hasAbility("Shield Dust") && (m_power != 0));

        Random random = mech.getRandom();
        for (int i = 0; i < m_statuses.length; ++i) {
            if (!m_attacker[i] && immune) {
                continue;
            }
            double chance = m_chances[i];
            if (serene) {
                chance *= 2.0;
            }
            if (random.nextDouble() <= chance) {
                Pokemon affected = (m_attacker[i] ? user : target);
                if ((user != affected) && hasSubstitute && !m_statuses[i].hitsThroughSubstitute()) {
                    if (m_power == 0) {
                        user.getField().showMessage("But it failed!");
                    }
                    continue;
                }
                if (affected.addStatus(user, m_statuses[i]) == null) {
                    if (m_power == 0) {
                        // Only show the message if it is a primary effect.
                        m_statuses[i].informDuplicateEffect(affected);
                    }
                }
            }
        }
        return damage;
    }
    
}
