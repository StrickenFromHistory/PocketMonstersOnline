/*
 * PokemonMove.java
 *
 * Created on December 15, 2006, 3:42 PM
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
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.PokemonType;

/**
 * This class represents a move that a pokemon can use on its turn.
 * @author Colin
 */
public class PokemonMove implements Cloneable {
    
    protected PokemonType m_type;
    protected int m_power;
    protected double m_accuracy;
    protected int m_pp;
    protected MoveListEntry m_entry;
    
    /**
     * Initialise a typical attacking move.
     */
    public PokemonMove(PokemonType type, int power, double accuracy, int pp) {
        m_type = type;
        m_power = power;
        m_accuracy = accuracy;
        m_pp = pp;
    }
    
    /**
     * Set this move's entry in the move list.
     */
    /*package*/ void setMoveListEntry(MoveListEntry e) {
        m_entry = e;
    }
    
    /**
     * Return this move's entry in the move list.
     */
    public MoveListEntry getMoveListEntry() {
        return m_entry;
    }
    
    /**
     * Return whether this move should use special attack and defence.
     */
    public boolean isSpecial(BattleMechanics mech) {
        return mech.isMoveSpecial(this);
    }
    
    /**
     * Clone this move.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            /* unreachable */
            return null;
        }
    }
    
    /**
     * Attempt to hit an enemy.
     */
    public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
        return mech.attemptHit(this, user, target);
    }
    
    /**
     * Cause a pokemon to use this move on another pokemon.
     */
    public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
        int damage = mech.calculateDamage(this, user, target);
        target.changeHealth(-damage);
        return damage;
    }
    
    /**
     * Get PP.
     */
    public int getPp() {
        return m_pp;
    }
    
    /**
     * Return whether this move deals damage.
     */
    public boolean isDamaging() {
        return isAttack();
    }
    
    /**
     * Get accuracy.
     */
    public double getAccuracy() {
        return m_accuracy;
    }
    
    /**
     * Set the accuracy of this move.
     */
    public void setAccuracy(double accuracy) {
        if (accuracy > 1.0) {
            m_accuracy = 1.0;
        } else if (accuracy < 0.0) {
            m_accuracy = 0.0;
        } else {
            m_accuracy = accuracy;
        }
    }
    
    /**
     * Get the type of this move.
     */
    public PokemonType getType() {
        return m_type;
    }
    
    /**
     * Some moves can be used even if a status effect (e.g. sleep) would
     * normally prevent it. If this move can be used a such, the class
     * of the status effect is returned by this method. Otherwise, the method
     * returns null.
     */
    @SuppressWarnings("unchecked")
	public Class getStatusException() {
        return null;
    }
    
    /**
     * Get the effectiveness of this move against a denfending pokemon.
     */
    public double getEffectiveness(Pokemon user, Pokemon defender) {
        return getEffectiveness(m_type, user, defender);
    }
    
    /**
     * Get the effectiveness of one type of move against an arbitrary pokemon.
     */
    public static double getEffectiveness(PokemonType type, Pokemon user, Pokemon defender) {
        PokemonType[] defTypes = defender.getTypes();
        double multiplier = 1.0;
        for (int i = 0; i < defTypes.length; ++i) {
            double expected = type.getMultiplier(defTypes[i]);
            double factor;
            PokemonType def = defTypes[i];
            if (user != null) {
                factor = user.getEffectiveness(type, def, false);
                if (factor == expected) {
                    factor = defender.getEffectiveness(type, def, true);
                }
            } else {
                BattleField field = defender.getField();
                factor = field.getEffectiveness(type, def, false);
                if (factor == expected) {
                    factor = field.getEffectiveness(type, def, true);
                }
            }
            multiplier *= factor;
        }
        return multiplier;
    }
    
    /**
     * This method is called when a pokemon who has this move is switched into
     * the field.
     */
    public void switchIn(Pokemon p) {
        
    }
    
    /**
     * Return whether this move can strike critical.
     */
    public boolean canCriticalHit() {
        return true;
    }
    
    /**
     * Set the type of this move.
     */
    public void setType(PokemonType type) {
        m_type = type;
    }
    
    /**
     * Get the power of this move.
     */
    public int getPower() {
        return m_power;
    }
    
    /**
     * Set the power of this move.
     */
    public void setPower(int power) {
        m_power = power;
    }
    
    /**
     * Get the priority of this move. Priority determines when this move will
     * be used during the turn.
     */
    public int getPriority() {
        return 0;
    }
    
    /**
     * Determine whether this move has a high chance of striking a critical
     * hit.
     */
    public boolean hasHighCriticalHitRate() {
        return false;
    }
    
    /**
     * Returns whether this move is an attack. This method is shoddy and should 
     * be overridden by any exceptions.
     */
    public boolean isAttack() {
        return m_power != 0;
    }
    
    /**
     * This function is called at the beginning on a turn on which this move
     * is about to be used.
     * @param turn the moves about to be used on this turn
     * @param index the position of the source pokemon in the turn array
     * @param source the pokemon who is using the move
     */
    public void beginTurn(BattleTurn[] turn, int index, Pokemon source) {
        
    }
    
    /**
     * Return whether this move is buggy.
     */
    public boolean isBuggy() {
        return false;
    }
}
