/*
 * StatusEffect.java
 *
 * Created on December 16, 2006, 9:43 AM
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

package org.pokenet.server.battle.mechanics.statuses;

import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.PokemonType;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;

/**
 * This class represents a change in status of a pokemon.
 * @author Colin
 */
public abstract class StatusEffect implements Cloneable {

    /**
     * A pokemon can have only one of freeze, burn, sleep, paralysis, and
     * poison, so we protect against this by giving this class of effects
     * a designated lock called SPECIAL_EFFECT_LOCK.
     */
    public static final int SPECIAL_EFFECT_LOCK = 1;
    
    /**
     * There can be only be only weather effect in play.
     */
    public static final int WEATHER_EFFECT_LOCK = 2;
    
    /** States of a StatusEffect. */
    public static final int STATE_ACTIVE = 0;
    public static final int STATE_DEACTIVATED = 1;
    public static final int STATE_REMOVABLE = 2;
    
    private int m_state = STATE_ACTIVE;
    protected int m_lock = 0;
    private Pokemon m_inducer;
    
    /**
     * Set the pokemon who induced this effect.
     */
    public void setInducer(Pokemon p) {
        m_inducer = p;
    }
    
    /**
     * Get the pokemon who induced this effect.
     */
    public Pokemon getInducer() {
        return m_inducer;
    }
    
    /**
     * Get the lock of this effect.
     */
    public int getLock() {
        return m_lock;
    }
    
    /**
     * Get the name of this status effect.
     */
    public String getName() {
        return null;
    }
    
    /**
     * Return the total number of tiers. There will be six tiers eventually.
     * This could also differ based on the mechanics used.
     */
    public static final int getTierCount() {
        return 6;
    }
    
    /**
     * Can this status effect by baton passed? (Almost all can.)
     */
    public boolean isPassable() {
        return true;
    }

    /**
     * Does this effect allow the application of the given status effect to
     * a particular pokemon? This is called on the target pokemon.
     */
    public boolean allowsStatus(StatusEffect eff, Pokemon source, Pokemon target) {
        return true;
    }
    
    /**
     * Return whether this effect can coexist with another effect.
     * @param eff the effect to test exclusiveness with
     */
    public boolean isExclusiveWith(StatusEffect eff) {
        if (m_lock == 0) {
            return false;
        }
        return (m_lock == eff.m_lock);
    }
    
    /**
     * Disable this status effect (i.e. mark it as removable). This cannot
     * be undone.
     */
    public final void disable() {
        m_state = STATE_REMOVABLE;
    }
    
    /**
     * Deactivate this status effect, unless it is removable.
     */
    public final void deactivate() {
        if (m_state != STATE_REMOVABLE) {
            m_state = STATE_DEACTIVATED;
        }
    }
    
    /**
     * Activate this status effect, unless it is removable.
     */
    public final void activate() {
        if (m_state != STATE_REMOVABLE) {
            m_state = STATE_ACTIVE;
        }
    }
    
    /**
     * Is this status effect active?
     */
    public final boolean isActive() {
        return (m_state == STATE_ACTIVE);
    }
    
    /**
     * Is this status effect waiting to be removed?
     */
    public final boolean isRemovable() {
        return (m_state == STATE_REMOVABLE);
    }
    
    /**
     * Clone this status effect.
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
     * Return whether this effect immobilises the pokemon.
     */
    public boolean immobilises(Pokemon poke) {
        return false;
    }
    
    /**
     * Get a description of this status effect.
     */
    public String getDescription() {
        return null;
    }
    
    /**
     * Called each turn that this status effect is applied to a pokemon.
     * Returns whether the status was removed.
     */
    public boolean tick(Pokemon p) {
        return false;
    }
    
    /**
     * Remove the tier of the after-turn effect.
     */
    public int getTier() {
        return -1;
    }
    
    /**
     * Called when a pokemon with this status effect switches in.
     */
    public void switchIn(Pokemon p) {
        
    }
    
    /**
     * Called when a pokemon with this status effect switches out.
     * Returns true if the status effect should be removed.
     */
    public boolean switchOut(Pokemon p) {
        return true;
    }
    
    /**
     * Unapply this status effect.
     */
    public void unapply(Pokemon p) {
        
    }
    
    /**
     * The point of this method is to catch errors.
     */
    public final boolean apply(Pokemon source, Pokemon p) {
        throw new InternalError();
    }
    
    /**
     * Applies the initial effects of the status to a pokemon but does not add
     * the status to the list of statuses the pokemon has.
     */
    public boolean apply(Pokemon p) {
        return true;
    }
    
    /**
     * Return whether this status effect can apply statuses through a
     * substitute.
     */
    public boolean hitsThroughSubstitute() {
        return false;
    }
    
    /**
     * Does this status effect transform effectivenesses?
     */
    public boolean isEffectivenessTransformer(boolean enemy) {
        return false;
    }
    
    /**
     * This method is called when the pokemon to whom the status effect is
     * applied is just about to execute his turn.
     */
    public void executeTurn(Pokemon p, BattleTurn turn) {
        
    }
    
    /**
     * Get transformed effectiveness based on this status effect.
     * @param move type of the move
     * @param pokemon type of the pokemon
     * @param enemy whether the Pokemon using the move is an enemy
     */
    public final double getEffectiveness(
            PokemonType move, PokemonType pokemon, boolean enemy) {
        if (enemy) {
            return getEnemyTransformedEffectiveness(move, pokemon);
        }
        return getTransformedEffectiveness(move, pokemon);
    }
    
    protected double getTransformedEffectiveness(PokemonType move, PokemonType pokemon) {
        return move.getMultiplier(pokemon);
    }
    
    protected double getEnemyTransformedEffectiveness(PokemonType move, PokemonType pokemon) {
        return move.getMultiplier(pokemon);
    }
    
    /**
     * Transform a move based on this status effect.
     * @param move  the move to transform; the method is free to modify it
     *              although it may also return a new MoveListEntry
     * @param enemy whether the Pokemon p is an enemy
     * @return      the transformed move
     */
    public final MoveListEntry getMove(
            Pokemon p, MoveListEntry move, boolean enemy) {
        if (enemy) {
            return getEnemyTransformedMove(p, move);
        }
        return getTransformedMove(p, move);
    }
    
    protected MoveListEntry getEnemyTransformedMove(Pokemon p, MoveListEntry move) {
        return move;
    }
    
    protected MoveListEntry getTransformedMove(Pokemon p, MoveListEntry move) {
        return move;
    }
    
    /**
     * Returns true if this status effect is capable of transforming moves.
     * @param enemy whether this is an enemy move
     */
    public boolean isMoveTransformer(boolean enemy) {
        return false;
    }
    
    /**
     * Return whether this effect listens for damage.
     */
    public boolean isListener() {
        return false;
    }
    
    /**
     * React to damage.
     */
    public void informDamaged(Pokemon source, Pokemon target, MoveListEntry move, int damage) {
        
    }
    
    /**
     * Determine whether this effect deactivates a pokemon.
     */
    public boolean deactivates(Pokemon p) {
        return false;
    }
    
    /**
     * Determine whether two status effects are equal semantically.
     */
    public boolean equals(Object eff) {
        if (!getClass().equals(eff.getClass())) {
            // If they are different types of status effects then they are
            // not equal.
            return false;
        }
        // Otherwise they might be.
        return isSingleton();
    }
    
    /**
     * Determine whether this effect is a singleton -- i.e., whether only
     * a single copy of it can be present on a pokemon.
     */
    public boolean isSingleton() {
        return true;
    }
    
    /**
     * Inform that this effect was applied, unsuccessfully, a second time.
     */
    public void informDuplicateEffect(Pokemon p) {
        p.getField().showMessage("But it failed!");
    }
    
    /**
     * Return whether this status effect allows switching.
     */
    public boolean canSwitch(Pokemon p) {
        return true;
    }
    
    /**
     * This method catches errors.
     */
    public boolean canSwitch() {
        throw new InternalError();
    }
    
    /**
     * Returns whether this status effect vetoes the choice of a particular
     * move.
     */
    public boolean vetoesMove(Pokemon p, MoveListEntry entry) {
        return false;
    }
    
    /**
     * Begin ticking this effect.
     */
    public void beginTick() {
        
    }
}
