/*
 * PokemonType.java
 *
 * Created on December 15, 2006, 1:50 PM
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

package org.pokenet.server.battle.mechanics;
import java.io.Serializable;
import java.util.ArrayList;

import org.simpleframework.xml.Element;

/**
 * This class represents the type of a pokemon or of a move.
 * @author Colin
 */
public class PokemonType implements Serializable {
    
    private static final long serialVersionUID = 328662720352042529L;
    
    @Element
    private int m_type;
    private static ArrayList<PokemonType> m_typeList = new ArrayList<PokemonType>();

    /**
     * Constants representing each of the types.
     */
    public static final PokemonType T_NORMAL = new PokemonType(0);
    public static final PokemonType T_FIRE = new PokemonType(1);
    public static final PokemonType T_WATER = new PokemonType(2);
    public static final PokemonType T_ELECTRIC = new PokemonType(3);
    public static final PokemonType T_GRASS = new PokemonType(4);
    public static final PokemonType T_ICE = new PokemonType(5);
    public static final PokemonType T_FIGHTING = new PokemonType(6);
    public static final PokemonType T_POISON = new PokemonType(7);
    public static final PokemonType T_GROUND = new PokemonType(8);
    public static final PokemonType T_FLYING = new PokemonType(9);
    public static final PokemonType T_PSYCHIC = new PokemonType(10);
    public static final PokemonType T_BUG = new PokemonType(11);
    public static final PokemonType T_ROCK = new PokemonType(12);
    public static final PokemonType T_GHOST = new PokemonType(13);
    public static final PokemonType T_DRAGON = new PokemonType(14);
    public static final PokemonType T_DARK = new PokemonType(15);
    public static final PokemonType T_STEEL = new PokemonType(16);
    public static final PokemonType T_TYPELESS = new PokemonType(17);
    
    private static final String m_types[] = new String[] {
        "Normal",
        "Fire",
        "Water",
        "Electric",
        "Grass",
        "Ice",
        "Fighting",
        "Poison",
        "Ground",
        "Flying",
        "Psychic",
        "Bug",
        "Rock",
        "Ghost",
        "Dragon",
        "Dark",
        "Steel",
        "Typeless"
    };
    
    private static final boolean m_special[] = new boolean[] {
        false,
        true,
        true,
        true,
        true,
        true,
        false,
        false,
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    };
    
    private static final double m_multiplier[][] = new double[][] {
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0.5, 0, 1, 1, 0.5, 1 },
	{ 1, 0.5, 0.5, 1, 2, 2, 1, 1, 1, 1, 1, 2, 0.5, 1, 0.5, 1, 2, 1 },
	{ 1, 2, 0.5, 1, 0.5, 1, 1, 1, 2, 1, 1, 1, 2, 1, 0.5, 1, 1, 1 },
	{ 1, 1, 2, 0.5, 0.5, 1, 1, 1, 0, 2, 1, 1, 1, 1, 0.5, 1, 1, 1 },
	{ 1, 0.5, 2, 1, 0.5, 1, 1, 0.5, 2, 0.5, 1, 0.5, 2, 1, 0.5, 1, 0.5, 1 },
	{ 1, 0.5, 0.5, 1, 2, 0.5, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 0.5, 1 },
	{ 2, 1, 1, 1, 1, 2, 1, 0.5, 1, 0.5, 0.5, 0.5, 2, 0, 1, 2, 2, 1 },
	{ 1, 1, 1, 1, 2, 1, 1, 0.5, 0.5, 1, 1, 1, 0.5, 0.5, 1, 1, 0, 1 },
	{ 1, 2, 1, 2, 0.5, 1, 1, 2, 1, 0, 1, 0.5, 2, 1, 1, 1, 2, 1 },
	{ 1, 1, 1, 0.5, 2, 1, 2, 1, 1, 1, 1, 2, 0.5, 1, 1, 1, 0.5, 1 },
	{ 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 0.5, 1, 1, 1, 1, 0, 0.5, 1 },
	{ 1, 0.5, 1, 1, 2, 1, 0.5, 0.5, 1, 0.5, 2, 1, 1, 0.5, 1, 2, 0.5, 1 },
	{ 1, 2, 1, 1, 1, 2, 0.5, 1, 0.5, 2, 1, 2, 1, 1, 1, 1, 0.5, 1 },
	{ 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 0.5, 0.5, 1 },
	{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 0.5, 1 },
	{ 1, 1, 1, 1, 1, 1, 0.5, 1, 1, 1, 2, 1, 1, 2, 1, 0.5, 0.5, 1 },
	{ 1, 0.5, 0.5, 0.5, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 0.5, 1 },
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
    };
    
    /**
     * Return the list of types.
     */
    public static PokemonType[] getTypes() {
        return (PokemonType[])m_typeList.toArray(new PokemonType[m_typeList.size()]);
    }
    
    /** Constructor used for serialization */
    public PokemonType() {}
    
    /**
     * Creates a new instance of PokemonType.
     * Note: this must be invoked in sequential order!
     */
    private PokemonType(int i) {
        m_type = i;
        m_typeList.add(i, this);
    }
    
    /**
     * Get a PokemonType object by its id.
     */
    public static PokemonType getType(int i) {
        return (PokemonType)m_typeList.get(i);
    }
    
    /**
     * Initialise the type by name.
     */
    public static PokemonType getType(String type) {
        for (int i = 0; i < m_types.length; ++i) {
            if (type.equalsIgnoreCase(m_types[i])) {
                return getType(i);
            }
        }
        return null;
    }
    
    /**
     * Return whether this type deals special damage.
     */
    public boolean isSpecial() {
        return m_special[m_type];
    }
    
    /**
     * Get the multiplier when attacking a pokemon of a given type.
     *
     * @param type the type of the defending pokemon
     */
    public double getMultiplier(PokemonType type) {
        return m_multiplier[m_type][type.m_type];
    }
    
    /**
     * Return whether this type is equal to the test type.
     */
    public boolean equals(Object type) {
        if (type == null) {
            return false;
        }
        try {
            PokemonType poketype = (PokemonType)type;
            return (poketype.m_type == m_type);
        } catch (ClassCastException e) {
            return false;
        }
    }
    
    /**
     * Get a textual representation of this type.
     */
    public String toString() {
        return m_types[m_type];
    }
    
    /**
     * Returns the Pokemon type's id
     * @return
     */
    public int getType() {
    	return m_type;
    }
}
