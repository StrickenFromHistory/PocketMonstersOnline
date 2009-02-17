/*
 * SandstormEffect.java
 *
 * Created on May 6, 2007 8:27 PM
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
import org.pokenet.server.battle.mechanics.JewelMechanics;
import org.pokenet.server.battle.mechanics.PokemonType;
import org.pokenet.server.battle.mechanics.StatMultiplier;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;
import org.pokenet.server.battle.mechanics.moves.StatusMove;
import org.pokenet.server.battle.mechanics.statuses.ChargeEffect;
import org.pokenet.server.battle.mechanics.statuses.PercentEffect;

/**
 * 1. Hurts all pokemon with 1/16 HP unless they are Rock/Ground/Steel
 * types or have the ability Sand Veil.
 * 2. Cuts the power of Solarbeam to 60.
 * 3. Makes Weather Ball a power 100 Rock-type move.
 * 4. Increases the current effective evasion of pokemon with the Sand Veil
 * ability.
 * 5. Makes Moonlight and Morning Sun restore 1/4 of the user's max HP.
 *
 * @author Ben
 */
public class SandstormEffect extends WeatherEffect {
    
    /** Creates a new instance of SandstormEffect */
    public SandstormEffect(int turns) {
        super(turns);
    }
    
    public String getName() {
        return "Sandstorm";
    }
    
    public SandstormEffect() {
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
        field.showMessage("The sandstorm rages.");
    }
    
    /**
     * Remove this effect from a field.
     */
    public void unapplyToField(BattleField field) {
        field.showMessage("The sandstorm stopped.");
    }
    
    /**
     * Does 1/16 of a Pokemon's health worth of damage each turn if the Pokemon is not 
     * Ground, Rock, or Steel type, or has the sand veil ability.
     */    
    public boolean tickPokemon(Pokemon p) {
        if (!hasEffects(p.getField()))
            return false;
        if ((p.isType(PokemonType.T_GROUND)) || (p.isType(PokemonType.T_ROCK)) || 
            (p.isType(PokemonType.T_STEEL)) || (p.hasAbility("Sand Veil")))
            return false;
        
        int maximum = p.getStat(Pokemon.S_HP);
        int damage = maximum / 16;
        if (damage < 1) damage = 1;
        p.getField().showMessage(p.getName() + " is buffetted by the sandstorm!");
        p.changeHealth(-damage, true);
        return false;
    }
    
    /**
     * Increase the evasion of a Pokemon with the Sand Veil ability.
     */        
    public boolean apply(Pokemon p) {
        if (m_applied[p.getParty()] || !(m_applied[p.getParty()] = hasEffects(p.getField())))
            return true;
        if (p.hasAbility("Sand Veil")) {
            StatMultiplier mul = p.getMultiplier(Pokemon.S_EVASION);
            mul.increaseMultiplier();
        }
        if ((p.getField().getMechanics() instanceof JewelMechanics) && p.isType(PokemonType.T_ROCK)) {
            p.getMultiplier(Pokemon.S_SPDEFENCE).multiplyBy(1.5);
        }
        return true;
    }
    
    /**
     * Restores the evasion of a Pokemon with Sand Veil when the sandstorm ends.
     */    
    public void unapply(Pokemon p) {
        if (!m_applied[p.getParty()])
            return;
        m_applied[p.getParty()] = false;
        if (p.hasAbility("Sand Veil")) {
            StatMultiplier mul = p.getMultiplier(Pokemon.S_EVASION);
            mul.decreaseMultiplier();
        }
        if ((p.getField().getMechanics() instanceof JewelMechanics) && p.isType(PokemonType.T_ROCK)) {
            p.getMultiplier(Pokemon.S_SPDEFENCE).divideBy(1.5);
        }
    }
    
    /**
     * Apply this effect to a field.
     */
    public boolean applyToField(BattleField field) {
        field.showMessage("A sandstorm brewed!");
        return true;
    }
    
    /**
     * 1. Hurts all pokemon with 1/16 HP unless they are Rock/Ground/Steel
     * types or have the ability Sand Veil.
     * 2. Cuts the power of Solarbeam to 60.
     * 3. Makes Weather Ball a power 100 Rock-type move.
     * 4. Increases the current effective evasion of pokemon with the Sand Veil
     * ability.
     * 5. Makes Moonlight and Morning Sun restore 1/4 of the user's max HP.
     */
    public MoveListEntry getTransformedMove(Pokemon poke, MoveListEntry entry) {
        if (!hasEffects(poke.getField()))
            return entry;
        
        PokemonMove move = entry.getMove();
        String name = entry.getName();
        if (name.equals("Weather Ball")) {
            move.setType(PokemonType.T_ROCK);
            move.setPower(100);
        } else if (name.equals("Solarbeam")) {
            if (move instanceof StatusMove) {
                StatusMove statusMove = (StatusMove)move;
                ChargeEffect charge = (ChargeEffect)statusMove.getEffects()[0];
                charge.getMove().getMove().setPower(60);
            }
        } else if (name.equals("Moonlight") || name.equals("Morning Sun") || name.equals("Synthesis")) {
            StatusMove statusMove = (StatusMove)move;
            // Assume that the first effect is the PercentEffect!
            PercentEffect perc = (PercentEffect)statusMove.getEffects()[0];
            perc.setPercent(1.0/3.0);
        }
        return entry;
    }
    
}
