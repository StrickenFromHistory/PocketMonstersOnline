/*
 * MoveListEntry.java
 *
 * Created on December 17, 2006, 11:10 PM
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class represents an entry in a pokemon's move list.
 * @author Colin
 */
public class MoveListEntry implements Serializable, Cloneable {
    
    private static final long serialVersionUID = 873410794589044553L;
    
    private String m_name;
    transient private PokemonMove m_move;
    
    public Object clone() {
        try {
            MoveListEntry ret = (MoveListEntry)super.clone();
            if (ret.m_move != null) {
                ret.m_move = (PokemonMove)ret.m_move.clone();
                ret.m_move.setMoveListEntry(ret);
            }
            return ret;
        } catch (CloneNotSupportedException e) {
            /* unreachable */
            return null;
        }
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof MoveListEntry)) {
            return false;
        }
        return m_name.equals(((MoveListEntry)o).m_name);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        MoveListEntry entry = MoveList.getDefaultData().getMove(m_name);
        if (entry != null) {
            m_move = entry.getMove();
        } else {
            m_move = null;
        }
    }
    
    public MoveListEntry(String name, PokemonMove move) {
        m_name = name;
        m_move = move;
        if (m_move.getMoveListEntry() != null) {
            System.out.println(name + " is used by two MoveListEntries!");
        }
        m_move.setMoveListEntry(this);
        if (m_move.isBuggy()) {
            System.out.println(name + " is buggy.");
        }
    }
    
    public String getName() {
        return m_name;
    }
    
    public PokemonMove getMove() {
        return m_move;
    }
}
