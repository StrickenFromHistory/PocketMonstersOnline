/*
 * TrappingAbility.java
 *
 * Created on July 28, 2007, 11:39 PM
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

package org.pokenet.server.battle.mechanics.statuses.abilities;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.moves.MoveList.TrappingEffect;

/**
 *
 * @author Colin
 */
public class TrappingAbility extends OpponentEffectAbility {
    
    /** Creates a new instance of TrappingAbility */
    public TrappingAbility(String name) {
        super(name);
    }
    
    public boolean isTrappable(Pokemon p) {
        return true;
    }
    
    public void applyToOpponent(Pokemon owner, Pokemon p) {
        p.addStatus(owner, new TrappingEffect(null) {
            public String getDescription() {
                return null;
            }
            public boolean canSwitch(Pokemon p) {
                return !isTrappable(p)
                    || (p.hasAbility("Shadow Tag") &&
                        p.getOpponent().hasAbility("Shadow Tag"))
                    || super.canSwitch(p);
            }
            public boolean isPassable() {
                return false;
            }
        });
    }

}
