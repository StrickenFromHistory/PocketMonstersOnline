/*
 * PokemonMove.java
 *
 * Created on July 15, 2007, 5:23 AM
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Ugly hack for getting pokemon masses from a file separate from the species
 * data. Ugly indeed.
 * @author Colin
 */
public class PokemonMass {

    /**
     * Map containing the mass of each pokemon.
     */
    private static HashMap<String, Double> m_masses = new HashMap<String, Double>();
    
    static {
        // Assume the file is named masses.txt for now.
        try {
            loadMasses(new File("./res/masses.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Warning: could not load masses from masses.txt.");
        }
    }
    
    /**
     * Load the masses from a file.
     */
    public static void loadMasses(File f) throws FileNotFoundException {
        m_masses.clear();
        String line;
        BufferedReader input = new BufferedReader(new FileReader(f));
        try {
            while ((line = input.readLine()) != null) {
                int space = line.lastIndexOf(' ');
                double mass = Double.parseDouble(line.substring(space + 1));
                String species = line.substring(0, space);
                m_masses.put(species, new Double(mass));
            }
        } catch (IOException e) {
            
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                
            }
        }
    }
    
    /**
     * Get the mass of a given pokemon.
     */
    public static double getMass(Pokemon species) {
        return getMass(species.getSpeciesName());
    }
    
    /**
     * Get the mass of a given pokemon.
     */
    public static double getMass(String species) {
        Double mass = (Double)m_masses.get(species);
        if (mass == null) {
            System.out.println("Warning: no mass for " + species + ".");
            return 0.0;
        }
        return mass.doubleValue();
    }    

}
