/*
 * PoisonEffect.java
 *
 * Created on January 6, 2007 2:43 PM
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
import org.pokenet.server.battle.mechanics.PokemonType;
import org.pokenet.server.battle.mechanics.StatException;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;

/**
 * This is an amazingly awesome effect!!
 * @author Ben
 */
public class AwesomeEffect extends StatusEffect {

    private int m_turns = 0;
    
    /** Creates a new instance of AwesomeEffect */
    public AwesomeEffect() {
        m_turns = 1;
    }
    
    public String getName() {
        return "Awesomeness";
    }
    
    /**
     * Awesome stays through switching out.
     */
    public boolean switchOut(Pokemon p) {
        p.getField().showMessage("God punishes " + p.getName() + " for leaving his presence!");
        p.changeHealth(-1000);
        return false;
    }
    
    /**
     * Awesome removes 1/8 max HP multiplied by the number of active turns
     * each round.
     */
    public boolean tick(Pokemon p) {
        try {
            double maximum = (double)p.getStat(Pokemon.S_HP);
            double loss = (maximum / 8.0) * (double)(m_turns);
            int floor = (int)loss;
            if (floor < 1) {
                floor = 1;
            }
            p.getField().showMessage(p.getName() + " is punished by God!");
            p.changeHealth(-floor);
        } catch (StatException e) {
            
        }
        return false;
    }
    
    /**
     * Awesome is in the 5th tier
     */
    public int getTier() {
        return 4;
    }
    
    public boolean apply(Pokemon p) {
        return true;
    }
    
    public void unapply(Pokemon p) {
    }
    
    public String getDescription() {
        return " insulted God out of anger!";
    }
    
    /**
     * Awesome immobilises 40% of the time.
     */
    public boolean immobilises(Pokemon poke) {
        BattleField field = poke.getField();
        
        if (++m_turns >= 4) {
            field.showMessage("God struck down " + poke.getName() + " with all his might!");
            poke.useMove(new PokemonMove(PokemonType.T_TYPELESS, 10000, 1.0, 1), poke);
            field.showMessage("God forgave " + poke.getName());
            poke.removeStatus(this);
            return true;
        }
        
        if (field.getRandom().nextDouble() <= 0.6) {
            return false;
        }
        
        field.showMessage(poke.getName() + " was too god-fearing to move!");
        return true;
    }
    
}
