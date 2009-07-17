/*
 * HealthBasedMove.java
 *
 * Created on January 19, 2006, 5:16 PM
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
 * A move that does higher damage when the user has more Hp
 * @author Ben
 */
public class HealthBasedMove extends PokemonMove {

    private boolean m_highHp;
    
    public HealthBasedMove(PokemonType type, double accuracy, int pp, boolean highHp) {
        super(type, 0, accuracy, pp);
        m_highHp = highHp;
    }
    
    public boolean isAttack() {
        return true;
    }
    
    public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
        double healthPercent = ((double)user.getHealth() / (double)user.getStat(Pokemon.S_HP) * 100.0);  
        
        int power = 0;
        if (m_highHp) {
            power = (int)(healthPercent * 1.5);
        } else if (healthPercent <= 4.1) {
            power = 200;
        } else if (healthPercent <= 10.4) {
            power = 150;
        } else if (healthPercent <= 20.8) {
            power = 100;
        } else if (healthPercent <= 35.4) {
            power = 80;
        } else if (healthPercent <= 68.7) {
            power = 40;
        } else {
            power = 20;
        }
        
        setPower(power);
        int damage = mech.calculateDamage(this, user, target);
        target.changeHealth(-damage);
        return damage;
    }
}
    