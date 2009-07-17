/*
 * ChoiceBandItem.java
 *
 * Created on June 13, 2007, 9:45 PM
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

package org.pokenet.server.battle.mechanics.statuses.items;

import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;

/**
 * Choice Band: First attack is used repeatedly, but for 150% damage.
 * Switching resets the choice.
 */
public class ChoiceBandItem extends HoldItem {
    private MoveListEntry m_move;
    private boolean m_transform = true;
    private int m_stat;
    
    /**
     * @param special whether the boosted moves are special
     */
    public ChoiceBandItem(String name, int stat) {
        super(name);
        m_stat = stat;
    }
    
    public boolean apply(Pokemon p) {
        m_move = null;
        p.getMultiplier(m_stat).multiplyBy(1.5);
        return true;
    }
    
    public void unapply(Pokemon p) {
        p.getMultiplier(m_stat).divideBy(1.5);
        super.unapply(p);
    }
    
    public boolean vetoesMove(Pokemon p, MoveListEntry entry) {
        if (m_move == null) {
            return false;
        }
        return !m_move.equals(entry);
    }
    
    public boolean switchOut(Pokemon p) {
        m_move = null;
        return super.switchOut(p);
    }
    
    public boolean isMoveTransformer(boolean enemy) {
        return !enemy;
    }
    
    public void beginTick() {
        m_transform = true;
    }
    
    protected void transformChoice(Pokemon p, BattleMechanics mech, MoveListEntry entry) {
    }
    
    public void setChoice(Pokemon p, BattleMechanics mech, MoveListEntry entry) {
        m_move = entry;
        transformChoice(p, mech, entry);
    }
    
    public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
        if (m_move == null) {
            setChoice(p, p.getField().getMechanics(), entry);
        } else if (m_transform) {
            for (int i = 0; i < 4; ++i) {
                MoveListEntry move = p.getMove(i);
                if ((move != null) && move.equals(m_move)) {
                    if (p.getPp(i) == 0)
                        return entry;
                }
            }
            entry = m_move;
            m_transform = false;
        }
        return entry;
    }
}
