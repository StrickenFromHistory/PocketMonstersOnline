/*
 * PokemonSpeciesData.java
 *
 * Created on May 19, 2007, 4:49 PM
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

package org.pokenet.server.battle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.pokenet.server.battle.mechanics.PokemonType;
import org.pokenet.server.battle.mechanics.moves.MoveList;
import org.pokenet.server.battle.mechanics.moves.MoveSet;
import org.pokenet.server.battle.mechanics.moves.MoveSetData;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

/**
 *
 * @author Colin
 */
@Root
public class PokemonSpeciesData {
    private TreeSet<String> m_unimplemented = new TreeSet<String>();
    private long m_lastModified;
    
    /**
     * A cache of movesets for each species where the origin of the move
     * has been removed, as this information is not relevant for the purpose
     * of validating pokemon. To speed the latter operation up, this cache
     * is useful. Call cacheMoveSets() to create this cache.
     */
    private TreeSet<?>[] m_movesets = null;
    
    /**
     * Database of all pokemon species.
     */
    @ElementArray
    private PokemonSpecies[] m_database;
    
    /**
     * Get the date when the database was last modified.
     */
    public long getLastModified() {
        return m_lastModified;
    }
    
    /**
     * Load a database of pokemon species in from a file, and require that
     * abilities have been implemented.
     */
    public void loadSpeciesDatabase(File file)
            throws IOException, FileNotFoundException {
        loadSpeciesDatabase(file, true);
    }
    
    /**
     * Load a database of pokemon species in from a file.
     */
    public void loadSpeciesDatabase(File file, boolean requireImplementation)
            throws IOException, FileNotFoundException {
        m_lastModified = file.lastModified();
        FileInputStream input = new FileInputStream(file);
        loadSpeciesDatabase(input, requireImplementation);
        input.close();
    }
    
    /**
     * Load a database of pokemon species in from an input stream.
     */
    @SuppressWarnings("unused")
    public void loadSpeciesDatabase(InputStream input,
            boolean requireImplementation) throws IOException {
        ObjectInputStream stream = new ObjectInputStream(input);
        int size = stream.readInt();
        m_unimplemented = new TreeSet<String>();
        m_database = new PokemonSpecies[size];
        for (int i = 0; i < size; ++i) {
            try {
                String name = (String)stream.readObject();
                try {
                    PokemonType[] type = (PokemonType[])stream.readObject();
                } catch(Exception e) {}
                int[] base = (int[])stream.readObject();
                int genders = stream.readInt();
				String[] ability = (String[])stream.readObject();
 
                m_database[i] = new PokemonSpecies(i, name, base, genders);
            } catch (ClassNotFoundException e) {
            	e.printStackTrace();
                throw new InternalError();
            }
        }
        
        Iterator<String> i = m_unimplemented.iterator();
        while (i.hasNext()) {
            String ability = (String)i.next();
            System.out.println("Unimplemented intrinsic ability: " + ability);
        }
        m_unimplemented = null;
    }
    
    /**
     * Cache the move sets of all of the pokemon.
     */
    public void cacheMoveSets(MoveList moveList,
            MoveSetData moveSet,
            boolean requireImplementation) {
        // This function will double as an error catching mechanism for
        // unimplemented moves.
        TreeSet<String> unimplemented = new TreeSet<String>();
        HashSet<String> implemented = new HashSet<String>();
        
        m_movesets = new TreeSet[m_database.length];
        for (int i = 0; i < m_database.length; ++i) {
            MoveSet set = null;
            try {
                set = moveSet.getMoveSet(i);
            } catch (IllegalArgumentException e) { }
            if (set == null) {
                m_movesets[i] = null;
                continue;
            }
            String[][] moves = set.getMoves();
            TreeSet<String> list = new TreeSet<String>();
            for (int j = 0; j < moves.length; ++j) {
                if (!requireImplementation) {
                    list.addAll(Arrays.asList(moves[j]));
                    continue;
                }
                String[] entries = moves[j];
                for (int k = 0; k < entries.length; ++k) {
                    // We enter these manually rather than using addAll()
                    // so that we can verify that the moves are actually
                    // implemented.
                    String move = entries[k];
                    if (moveList.getMove(move) != null) {
                        // It is actually implemented.
                        list.add(move);
                        implemented.add(move);
                    } else {
                        /**
                         * Add this to the list of unimplemented moves for the
                         * sole purpose of calculating the percent that are
                         * implemented.
                         */
                         if (move != null) {
                            unimplemented.add(move);
                         }
                         
                        /**
                         * Remove this unimplemented move from this list so
                         * that the server doesn't send the client a list of
                         * moves containing unimplemented entries.
                         */
                        entries[k] = null;
                    }
                }
            }
            m_movesets[i] = list;
        }
        
        // Print out moves that were unimplemented.
        Iterator<String> i = unimplemented.iterator();
        while (i.hasNext()) {
            String str = (String)i.next();
            System.out.println("Unimplemented move: " + str);
        }
        
        if (unimplemented.size() != 0) {
            System.out.println("There are "
                    + String.valueOf(unimplemented.size())
                    + " unimplemented moves.");
            double total = (double)(implemented.size() + unimplemented.size());
            double perc = ((double)implemented.size() / total) * 100.0;
            System.out.println("The move library is "
                    + String.valueOf(Math.round(perc))
                    + "% implemented.");
        }
    }
    
    /**
     * Return whether an ability is implemented.
     */
    public boolean isAbilityImplemented(String ability) {
        if (ability == null) {
            return false;
        }
        return !m_unimplemented.contains(ability);
    }
    
    /**
     * Return whether a pokemon can have a particular ability.
     */
    public boolean canUseAbility(String name, String ability) {
    	if (ability == null) {
            return false;
        }
    	String [] possibleAbilities =
    		getPokemonByName(name).getAbilities();
    	if(possibleAbilities == null)
    		return false;
    	for(int i = 0; i < possibleAbilities.length; i++) {
    		if(possibleAbilities[i].equalsIgnoreCase(ability))
    			return true;
    	}
    	return false;
    }
    
    /**
     * Return an array of the names of the possible abilities a given pokemon
     * can have. Note that this will return unimplemented abilities as well
     * as implemented ones.
     */
    public String[] getAbilityNames(String name) {
        return getPossibleAbilities(name);
    }
    
    /**
     * Return a TreeSet of possible abilities. This only includes abilities
     * that are actually implemented.
     */
    public String[] getPossibleAbilities(String name) {
        return getPokemonByName(name).getAbilities();
    }
    
    /**
     * Get the species database.
     */
    public PokemonSpecies[] getSpecies() {
        return m_database;
    }
    
    /**
     * Get a single species.
     */
    public PokemonSpecies getSpecies(int i) throws PokemonException {
    	if(i < 0)
    		return null;
        if (i >= m_database.length)
            return null;
        return m_database[i];
    }
    
    /**
     * Get the whole list of species names.
     * Note: this call is *expensive*.
     */
    public String[] getSpeciesNames() {
        String[] names = new String[m_database.length];
        for (int i = 0; i < names.length; ++i) {
            names[i] = m_database[i].m_name;
        }
        return names;
    }
    
    /**
     * Return the number of species.
     */
    public int getSpeciesCount() {
        return m_database.length;
    }
    
    /**
     * Find a pokemon by name.
     */
    public PokemonSpecies getPokemonByName(String name) {
        for (int i = 0; i < m_database.length; ++i) {
            if (m_database[i] != null && m_database[i].getName() != null && 
            		m_database[i].getName().equalsIgnoreCase(name)) {
                return m_database[i];
            }
        }
        return null;
    }
    

    
    /**
     * Return whether this species can learn a particular move.
     */
    public boolean canLearn(PokemonSpecies species, String move) {
    	try {
        	String [] moves = species.getStarterMoves();
        	for(int i = 0; i < moves.length; i++) {
        		if(moves[i] != null && moves[i].equalsIgnoreCase(move))
        			return true;
        	}
        	if(species.getLevelMoves().containsValue(move))
        		return true;
            moves = species.getTMMoves();
            for(int i = 0; i < moves.length; i++) {
        		if(moves[i] != null && moves[i].equalsIgnoreCase(move))
        			return true;
        	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return false;
    }
    
}
