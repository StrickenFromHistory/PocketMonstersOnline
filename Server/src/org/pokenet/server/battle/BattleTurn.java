/*
 * BattleTurn.java
 *
 * Created on December 19, 2006, 4:34 PM
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

package org.pokenet.server.battle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.pokenet.server.battle.mechanics.moves.MoveListEntry;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;

/**
 * This class represents one half of a turn of a battle - the move made by
 * a single party.
 * @author Colin
 */
@SuppressWarnings("serial")
public class BattleTurn implements Serializable, Cloneable {
    
    protected boolean m_useMove = false;
    protected int m_id = -1;
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    
    /**
     * Unserialises a BattleTurn. This method creatively throws an IOException
     * if the move has invalid ids.
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (m_id < 0)
            throw new IOException();
        if (m_useMove) {
            if (m_id > 3)
                throw new IOException();
        } else {
            if (m_id > 5)
                throw new IOException();
        }
    }
    
    /**
     * Allows for the cloning of this move.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    
    private BattleTurn() {
        // Prevent this class from being instanced directly.
    }
    
    public int getId() {
        return m_id;
    }
    
    public boolean isMoveTurn() {
        return m_useMove;
    }
    
    /**
     * Get the PokemonMove that this object refers to.
     */
    public PokemonMove getMove(Pokemon poke) {
        if (!m_useMove) {
            return null;
        }
        MoveListEntry entry = poke.getMove(m_id);
        if (entry == null) {
            return null;
        }
        return entry.getMove();
    }
    
    /**
     * Get a BattleTurn objects that represents switching in the
     * identified pokemon.
     */
    public static BattleTurn getSwitchTurn(int i) {
        BattleTurn turn = new BattleTurn();
        turn.m_id = i;
        turn.m_useMove = false;
        return turn;
    }
    
    /**
     * Get a BattleTurn object that represents using the identified move.
     */
    public static BattleTurn getMoveTurn(int i) {
        BattleTurn turn = new BattleTurn();
        turn.m_id = i;
        turn.m_useMove = true;
        return turn;
    }

}
