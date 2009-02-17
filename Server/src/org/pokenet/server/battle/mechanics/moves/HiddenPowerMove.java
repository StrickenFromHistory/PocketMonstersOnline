/*
 * HiddenPowerMove.java
 *
 * Created on August 12, 2007, 1:10 AM
 *
 * This file is a part of Shoddy Battle.
 * Copyright (C) 2007  Colin Fitzpatrick
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
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * The Free Software Foundation may be visited online at http://www.fsf.org.
 */

package org.pokenet.server.battle.mechanics.moves;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.PokemonType;

/**
 *
 * @author Colin
 */
public class HiddenPowerMove extends PokemonMove {

    /** Creates a new instance of HiddenPowerMove */
    public HiddenPowerMove() {
        super(PokemonType.T_NORMAL, 0, 1.0, 15);
    }

    public boolean isAttack() {
        return true;
    }
    public void beginTurn(BattleTurn[] turns, int index, Pokemon source) {
        switchIn(source);
    }
    public void switchIn(Pokemon source) {
        int power = 0;
        int type = 0;
        for (int i = 0; i < 6; ++i) {
            final int iv = source.getIv(i);
            final int increment = 1 << i;
            if (iv % 2 != 0) {
                type += increment;
            }
            if ((iv % 4 == 2) || (iv % 4 == 3)) {
                power += increment;
            }
        }
        power = (int)((double)power * 40.0 / 63.0 + 30.0);
        setPower(power);
        type = (int)((double)type * (15.0 / 63.0));

        PokemonType moveType = new PokemonType[] {
                PokemonType.T_FIGHTING,
                PokemonType.T_FLYING,
                PokemonType.T_POISON,
                PokemonType.T_GROUND,
                PokemonType.T_ROCK,
                PokemonType.T_BUG,
                PokemonType.T_GHOST,
                PokemonType.T_STEEL,
                PokemonType.T_FIRE,
                PokemonType.T_WATER,
                PokemonType.T_GRASS,
                PokemonType.T_ELECTRIC,
                PokemonType.T_PSYCHIC,
                PokemonType.T_ICE,
                PokemonType.T_DRAGON,
                PokemonType.T_DARK
            }[type];

        setType(moveType);
    }
    
}
