/*
 * ConfuseEffect.java
 *
 * Created on December 23, 2006, 12:12 PM
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
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.PokemonType;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;

/**
 *
 * @author Colin
 */
public class ConfuseEffect extends StatusEffect {

    private int m_turns = 0;
    
    public String getName() {
        return "Confusion";
    }
    
    public boolean tick(Pokemon p) {
        return false;
    }
    
    public int getTier() {
        // Not applicable.
        return 1;
    }
    
    public boolean switchOut(Pokemon p) {
        return true;
    }
    
    public boolean apply(Pokemon p) {
        if (p.hasSubstitute()) {
            return false;
        }
        if (p.hasAbility("Own Tempo")) {
            return false;
        }
        if (p.hasAbility("Tangled Feet")) {
            p.getMultiplier(Pokemon.S_EVASION).increaseMultiplier();
        }
        m_turns = p.getField().getRandom().nextInt(4) + 2;
        return true;
    }
    
    public void unapply(Pokemon p) {
        if (p.hasAbility("Tangled Feet")) {
            p.getMultiplier(Pokemon.S_EVASION).decreaseMultiplier();
        }
    }
    
    public String getDescription() {
        return " became confused!";
    }
    
    /**
     * Confusion has a 50% chance of immobolising the afflicted pokemon.
     */
    public boolean immobilises(Pokemon poke) {
        if (poke.hasEffect(SleepEffect.class)) {
            return false;
        }
        
        if (--m_turns <= 0) {
            poke.removeStatus(this);
            poke.getField().showMessage(poke.getName()
                + " snapped out of confusion!");
            return false;
        }
        
        BattleField field = poke.getField();
        
        field.showMessage(poke.getName() + " is confused!");
        if (field.getRandom().nextDouble() <= 0.5) {
            return false;
        }
        
        field.showMessage("It hurt itself in its confusion!");
        poke.useMove(new PokemonMove(PokemonType.T_TYPELESS, 40, 1.0, 1) {
                public int use(BattleMechanics mech, Pokemon source, Pokemon target) {
                    int damage = mech.calculateDamage(this, source, target);
                    target.changeHealth(-damage, true);
                    return damage;
                }
                public boolean attemptHit(BattleMechanics mech,
                        Pokemon source, Pokemon target) {
                    return true;
                }
                public boolean canCriticalHit() {
                    return false;
                }
            }, poke);
        return true;
    }
    
    public void informDuplicateEffect(Pokemon p) {
        p.getField().showMessage(p.getName() + " is already confused!");
    }
}
