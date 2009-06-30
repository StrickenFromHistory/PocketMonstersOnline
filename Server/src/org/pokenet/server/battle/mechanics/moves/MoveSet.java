/*
 * MoveSet.java
 *
 * Created on January 2, 2007, 8:10 PM
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

package org.pokenet.server.battle.mechanics.moves;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

import org.simpleframework.xml.ElementArray;

/**
 *
 * @author Colin
 */
public class MoveSet implements Serializable {
    
    /**
     * Each index represents a type of move.
     * 0 - natural moves
     * 1 - learned by a move tutor
     * 2 - learned from a tm
     * 3 - learned from an hm
     * 4 - egg moves
     */
	@ElementArray
    private String[][] m_moves = null;
    
    /**
     * The level at which each natural move is learned. Each index corresponds
     * to a move in m_moves[].
     */
	@ElementArray
    private int[] m_level = null;
    
    /**
     * Constant version id so that this class can be modified without
     * rendering the moveset database obsolete.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Default MoveSetData object.
     */
    private static final MoveSetData m_default = new MoveSetData();
    
    /**
     * Merge in another move set's data.
     */
    public void mergeMoves(int category, String[] moves) {
        HashSet<String> set = new HashSet<String>(Arrays.asList(m_moves[category]));
        set.addAll(Arrays.asList(moves));
        m_moves[category] = (String[])set.toArray(new String[set.size()]);
    }
    
    /**
     * Get the default MoveSetData.
     */
    public static MoveSetData getDefaultData() {
        return m_default;
    }
    
    /**
     * Return the textual name of a category of moves.
     */
    public static String getMoveType(int i) {
        switch (i) {
            case 0: return "Level";
            case 1: return "Move Tutor";
            case 2: return "TM";
            case 3: return "HM";
            case 4: return "Egg Group";
        }
        return null;
    }
    
    /**
     * Get the moves that this pokemon can learn.
     */
    public String[][] getMoves() {
        return m_moves;
    }
    
    /**
     * Get the levels at which level up moves are learned.
     */
    public int[] getNaturalLevels() {
        return m_level;
    }
    
    /** Creates a new instance of MoveSet */
    public MoveSet(String[] natural,
            int[] level,
            String[] tutor,
            String[] tm,
            String[] hm,
            String[] egg) {
        m_level = level;
        m_moves = new String[][] { natural, tutor, tm, hm, egg };
    }
    
    /**
     * Returns true if a Pokemon can learn a move
     * @param moveName
     * @return
     */
    public boolean canLearn(String moveName) {
    	for(int i = 0; i < m_moves.length; i++) {
    		for(int j = 0; j < m_moves[i].length; j++) {
    			if(m_moves[i][j].equalsIgnoreCase(moveName))
    				return true;
    		}
    	}
    	return false;
    }
    
    /** Constructor used for serialization */
    public MoveSet() {}
}
