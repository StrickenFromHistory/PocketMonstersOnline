/*
 * MultipleHitMove.java
 *
 * Created on January 18, 2006, 8:57 PM
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
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.PokemonType;

/**
 * Creates a move that hits from 2-5 times.
 * @author Ben
 */
public class MultipleHitMove extends PokemonMove {
    
    /**
     * Creates a new instance of MultipleHitMove
     */
    public MultipleHitMove(PokemonType type, int power, double accuracy, int pp) {                
        super(type, power, accuracy, pp);
    }
    
    public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
        double random = mech.getRandom().nextDouble();
        int hits;
        if (random <= 0.375) {
           hits = 2;
        } else if (random <= 0.75) {
           hits = 3;
        } else if (random <= 0.875) {
           hits = 4;
        } else {
           hits = 5;
        }
        
        if (user.hasAbility("Skill Link")) hits = 5;
        int damage = 0;
        for (int i = 0; i < hits; ++i) {
            final int partial = mech.calculateDamage(this, user, target);
            target.changeHealth(-partial);
            damage += partial;
        }
        
        target.getField().showMessage("Hit " + hits + " time(s)!");
        return damage;
    }
}
