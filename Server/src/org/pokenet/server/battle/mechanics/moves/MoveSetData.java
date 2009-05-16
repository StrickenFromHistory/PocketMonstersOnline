/*
 * MoveSetData.java
 *
 * Created on May 19, 2007, 4:36 PM
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

/**
 *
 * @author Colin
 */
@Root
public class MoveSetData {
    
    /**
     * Set of all move sets.
     */
	@ElementArray
    private MoveSet[] m_movesets = null;
	
    /**
     * Save the move sets to a file.
     */
    public void saveToFile(File f) {
        try {
            FileOutputStream file = new FileOutputStream(f);
            saveToFile(file);
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Save the move sets to an arbitrary output stream.
     */
    public void saveToFile(OutputStream output) {
        try {
            ObjectOutputStream obj = new ObjectOutputStream(output);
            obj.writeObject(m_movesets);
            obj.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Prune the move set of null entries.
     * This is slow and shoddy.
     */
    public void pruneMoveSet() {
        for (int i = 0; i < m_movesets.length; ++i) {
            if (m_movesets[i] == null) {
                continue;
            }
            String[][] categories = m_movesets[i].getMoves();
            for (int j = 0; j < categories.length; ++j) {
                ArrayList<String> moves = new ArrayList<String>(Arrays.asList(categories[j]));
                Iterator<String> k = moves.iterator();
                while (k.hasNext()) {
                    if (k.next() == null) {
                        k.remove();
                    }
                }
                categories[j] = (String[])moves.toArray(new String[moves.size()]);
            }
        }
    }
    
    /**
     * Load the move sets in from a URL.
     */
    public void loadFromFile(URL url) {
        try {
            InputStream input = url.openStream();
            loadFromFile(input);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Load the move sets in from a file.
     */
    public void loadFromFile(String str) {
        try {
            File f = new File(str);
            InputStream file = new FileInputStream(f);
            loadFromFile(file);
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Load the move sets from an input stream.
     */
    public void loadFromFile(InputStream file) {
        try {
            ObjectInputStream obj = new ObjectInputStream(file);
            m_movesets = (MoveSet[])obj.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Return the number of move sets.
     */
    public int getMoveSetCount() {
        return m_movesets.length;
    }
    
    /**
     * Merge the full advance and d/p move databases together.
     */
    public static void main(String[] args) throws Exception {
        class Pair {
            @SuppressWarnings("unused")
			public String first, second;
            Pair(String f, String s) {
                first = f;
                second = s;
            }
        }
        
        /**Pair[] inheritance = {
            new Pair("Sneasel", "Weavile"),
            new Pair("Magneton", "Magnezone"),
            new Pair("Lickitung", "Lickilicky"),
            new Pair("Rhydon", "Rhyperior"),
            new Pair("Tangela", "Tangrowth"),
            new Pair("Electabuzz", "Electivire"),
            new Pair("Magmar", "Magmortar"),
            new Pair("Togetic", "Togekiss"),
            new Pair("Yanma", "Yanmega"),
            new Pair("Eevee", "Leafeon"),
            new Pair("Eevee", "Glaceon"),
            new Pair("Gligar", "Gliscor"),
            new Pair("Piloswine", "Mamoswine"),
            new Pair("Porygon2", "Porygonz"),
            new Pair("Kirlia", "Gallade"),
            new Pair("Nosepass", "Probopass"),
            new Pair("Dusclops", "Dusknoir"),
            new Pair("Budew", "Roselia"),
            new Pair("Roselia", "Roserade"),
            new Pair("Aipom", "Ambipom"),
            new Pair("Misdreavus", "Mismagius"),
            new Pair("Murkrow", "Honchkrow"),
            new Pair("Chingling", "Chimecho"),
            new Pair("Bonsly", "Sudowoodo"),
            new Pair("Mime Jr.", "Mr. Mime"),
            new Pair("Happiny", "Chansey"),
            new Pair("Chansey", "Blissey"),
            new Pair("Munchlax", "Snorlax"),
            new Pair("Mantyke", "Mantine"),
        };
        
        Properties props = shoddybattle.Main.getProperties("server.properties");
        shoddybattle.Main.initialise(props);
        shoddybattle.ModData data = shoddybattle.ModData.getDefaultData();
        data.applyPatch(new FileInputStream(new File("movesetfixes.patch")));
        shoddybattle.PokemonSpeciesData species = data.getSpeciesData();
        MoveSetData moves = data.getMoveSetData();
        
        for (int i = 0; i < inheritance.length; ++i) {
            Pair pair = inheritance[i];
            System.out.println(pair.second + " inherits from " + pair.first);
            MoveSet base = moves.getMoveSet(species.getPokemonByName(pair.first));
            MoveSet extension = moves.getMoveSet(species.getPokemonByName(pair.second));
            String[][] moveset = base.getMoves();
            for (int j = 0; j < 5; ++j) {
                extension.mergeMoves(j, moveset[j]);
            }
        }
        
        moves.saveToFile(new File("dpmovesets.db"));
        
        if (Math.abs(0) == 0)
            return;*/
        
        @SuppressWarnings("unused")
		Pair[] changes = {
            new Pair("Ancient Power", "Ancientpower"),
            new Pair("Bubble Beam", "Bubblebeam"),
            new Pair("Double Edge", "Double-edge"),
            new Pair("Self Destruct", "Selfdestruct"),
            new Pair("Sketch", null),
            new Pair("Snore Swagger", "Swagger"),
            new Pair("Will-O-Wisp", "Will-o-wisp"),
            new Pair("Lock-On", "Lock-on"),
            new Pair("X-scissor", "X-Scissor"),
            new Pair("Sand-attack", "Sand-Attack"),
            new Pair("Fly~ Surf + Reversal", null),
            new Pair("Roar of Time", "Roar Of Time"),
            new Pair("Mud-slap", "Mud-Slap")
        };
        
        /*Properties props = shoddybattle.Main.getProperties("server.properties");
        shoddybattle.Main.initialise(props);
        shoddybattle.ModData data = shoddybattle.ModData.getDefaultData();
        //data.getMoveSetData().loadFromFile("dpmovesets.db");
        data.applyPatch(new FileInputStream(new File("movesetfixes.patch")), 1);
        //data.applyPatch(new FileInputStream(new File("nypc.patch")));
        //data.applyPatch(new FileInputStream(new File("smeargle.mod")));
        //data.applyPatch(new FileInputStream(new File("magneton.patch")), 4);
        MoveSetData moves = data.getMoveSetData();
        MoveSetData dp = new MoveSetData();
        dp.loadFromFile("dpmovesets.db");
        moves.pruneMoveSet();
        dp.pruneMoveSet();
        data.getMoveSetData().pruneMoveSet();
        int count = data.getMoveSetData().getMoveSetCount();
        for (int i = 0; i < count; ++i) {
            MoveSet set = data.getMoveSetData().getMoveSet(i);
            //MoveSet advanceSet = moves.getMoveSet(i);
            for (int j = 0; j < 5; ++j) {
                if (j == 3) continue; // Do not pass HMs!
                //if (advanceSet != null) {
                    //set.mergeMoves(j, advanceSet.getMoves()[j]);
                    String[] strs = set.getMoves()[j];
                    if (strs == null)
                        continue;
                    for (int k = 0; k < strs.length; ++k) {
                        for (int l = 0; l < changes.length; ++l) {
                            if (strs[k].equals(changes[l].first)) {
                                strs[k] = changes[l].second;
                                break;
                            }
                        }
                    }
                //}
            }
        }
        data.getMoveSetData().saveToFile(new File("dpmovesets.db"));*/
    }
    
    /**
     * Get the move set identified by the parameter.
     */
    public MoveSet getMoveSet(int i) throws IllegalArgumentException {
        if ((i < 0) || (i >= m_movesets.length)) {
            throw new IllegalArgumentException("Index out of range.");
        }
        return m_movesets[i];
    }
    
}
