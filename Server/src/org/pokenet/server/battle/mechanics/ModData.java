/*
 * ModData.java
 *
 * Created on May 12, 2007, 5:31 PM
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

package org.pokenet.server.battle.mechanics;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;

import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.PokemonSpeciesData;
import org.pokenet.server.battle.mechanics.moves.MoveList;
import org.pokenet.server.battle.mechanics.moves.MoveSet;
import org.pokenet.server.battle.mechanics.moves.MoveSetData;
import org.pokenet.server.battle.mechanics.statuses.items.HoldItem;
import org.pokenet.server.battle.mechanics.statuses.items.HoldItemData;

/**
 * <p>This class encapsulates a complete set of "moddata". Mod data does not
 * just include a patch against the pokemon data. In fact, a ModData object
 * contains all of the following:
 * 
 * <ul>
 * <li>A UUID identifying the server the ModData came from
 * <li>A pokemon species database
 * <li>A move sets database
 * <li>An items database
 * <li>A list of moves
 * </ul>
 * 
 * @author Colin
 */
public class ModData {
    
    /**
     * The mod data file name on the server.
     */
    public static final File MOD_DATA_FILE = new File("moddata");
    
    /**
     * The registry key holding the storage location on the client.
     */
    private static final String REGISTRY_KEY = "storage_location";
    
    /**
     * Cache of mod data.
     */
    private static final Map<String, ModData> m_map = Collections.synchronizedMap(new HashMap<String, ModData>());
    private static final ModData m_default;
    
    /**
     * Length of the mod data file.
     */
    private long m_dataLength = 0;
    
    /**
     * The actual mod data.
     */
    private String m_uuid;
    private PokemonSpeciesData m_species;
    private MoveSetData m_moveSets;
    private HoldItemData m_items;
    private MoveList m_moves;
    
    static {
        m_default = new ModData();
        m_default.m_species = PokemonSpecies.getDefaultData();
        m_default.m_moveSets = MoveSet.getDefaultData();
        m_default.m_items = HoldItem.getDefaultData();
        m_default.m_moves = MoveList.getDefaultData();
        m_map.put(null, m_default);
    }
    
    /**
     * Get the name of the ModData.
     */
    public String getName() {
        return m_uuid;
    }
    
    /**
     * Return the ModData object corresponding to the given name, attempting
     * to load from disc if required.
     */
    public static ModData getModData(String name) {
        Object o = m_map.get(name);
        if (o != null) {
            return (ModData)o;
        }
        File f = new File(getStorageLocation() + name);
        if (!f.exists()) {
            return null;
        }
        return new ModData(f);
    }
    
    /**
     * Get the default ModData.
     */
    public static ModData getDefaultData() {
        return m_default;
    }
    
    /**
     * Get the species data.
     */
    public PokemonSpeciesData getSpeciesData() {
        return m_species;
    }
    
    /**
     * Get the move set data.
     */
    public MoveSetData getMoveSetData() {
        return m_moveSets;
    }
    
    /**
     * Get the hold item data.
     */
    public HoldItemData getHoldItemData() {
        return m_items;
    }
    
    /**
     * Get the move data.
     */
    public MoveList getMoveData() {
        return m_moves;
    }
    
    /**
     * Save mod data (species, abilities, move sets, and items) to a file
     * which can be opened by a client to allow for creating items that
     * will play on this mod server.
     */
    public void saveModData(OutputStream output) {
        try {
            m_moveSets.saveToFile(output);
            m_items.saveItemData(output);
            m_moves.saveMoveList(output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Set the length of the mod data file.
     */
    public void setModDataLength(long length) {
        m_dataLength = length;
    }
    
    /**
     * Get the length of the mod data file.
     */
    public long getModDataLength() {
        return m_dataLength;
    }
    
    /**
     * Private constructor.
     */
    private ModData() {
        
    }
    
    /**
     * Construct a new ModData object via a file.
     */
    public ModData(File f) {
        loadModData(f);
    }
    
    /**
     * Load mod data (@see saveModData) from a file.
     */
    public void loadModData(File f) {
        try {
            FileInputStream input = new FileInputStream(f);
            m_uuid = f.getName();
            m_species = new PokemonSpeciesData();
            m_species.loadSpeciesDatabase(input, false);
            m_moveSets = new MoveSetData();
            m_moveSets.loadFromFile(input);
            m_moveSets.pruneMoveSet(); // Slow, but avoids errors.
            m_items = new HoldItemData();
            m_items.loadItemData(input);
            m_moves = new MoveList(false);
            m_moves.loadMoveList(input);
            input.close();
            
            m_species.cacheMoveSets(m_moves, m_moveSets, false);
            m_map.put(m_uuid, this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Remove a set of items from an array.
     */
    private void removeMoves(String[][] haystack, String[] needles) {
        for (int i = 0; i < haystack.length; ++i) {
            String[] layer = haystack[i];
            for (int j = 0; j < layer.length; ++j) {
                String strand = layer[j];
                for (int k = 0; k < needles.length; ++k) {
                    if (needles[k].equalsIgnoreCase(strand)) {
                        layer[j] = null;
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Handle the moves from a mod data line.
     */
    private void handleMoves(int species, ArrayList<String> moves, int category) {
        MoveSet set = m_moveSets.getMoveSet(species);
        ArrayList<String> removals = new ArrayList<String>();
        ArrayList<String> additions = new ArrayList<String>();
        Iterator<String> i = moves.iterator();
        while (i.hasNext()) {
            String item = (String)i.next();
            char c = item.charAt(0);
            if (c == '-') {
                removals.add(item.substring(1).trim());
            } else {
                String move;
                if (c == '+') {
                    move = item.substring(1);
                } else {
                    move = item;
                }
                additions.add(move.trim());
            }
        }
        String[][] arr = set.getMoves();
        removeMoves(arr, (String[])removals.toArray(new String[removals.size()]));
        ArrayList<String> update = new ArrayList<String>(Arrays.asList(arr[category]));
        update.addAll(additions);
        arr[category] = (String[])update.toArray(new String[update.size()]);
    }
    
    /**
     * Handle the abilities in a mod data line.
     */
    private void handleAbilities(String species, ArrayList<String> abilities) {
        String arr[] = m_species.getAbilityNames(species);
        if (arr == null) {
            System.out.println("Warning: problematic abilities for " + species + ".");
            arr = new String[0];
        }
        ArrayList<String> names =
                new ArrayList<String>(Arrays.asList(arr));
        Iterator<String> i = abilities.iterator();
        while (i.hasNext()) {
            String item = (String)i.next();
            char c = item.charAt(1);
            String name = item.substring(2).trim();
            if (c == '+') {
                /**if (IntrinsicAbility.getInstance(name) == null) {
                    System.out.println("Warning: no such ability: " + name);
                }**/
                names.add(name);
            } else {
                names.remove(name);
            }
        }
    }
    
    /**
     * Handle the items in a mod data line.
     */
    private void handleItems(String species, ArrayList<String> items) {
        Iterator<String> i = items.iterator();
        while (i.hasNext()) {
            String item = (String)i.next();
            char c = item.charAt(1);
            String name = item.substring(2).trim();
            if (c == '+') {
                m_items.addExclusiveItem(name, species);
            } else {
                m_items.removeExclusiveItem(name, species);
            }
        }
    }
    
    /**
     *  Handle a stat modification.
     */
    private void modifyStat(int species, String part) {
        String[] parts = part.split(" *: *");
        String stat = parts[0].toLowerCase();
        final String[] stats = { "hp", "atk", "def", "spd", "satk", "sdef" };
        int value = Integer.valueOf(parts[1]).intValue();
        int i = 0;
        for (; i < stats.length; ++i) {
            if (stats[i].equalsIgnoreCase(stat)) {
                break;
            }
        }
        if (i != stats.length) {
            int[] base = m_species.getSpecies(species).getBaseStats();
            base[i] = value;
        } else {
            System.out.println("Could not identify stat: " + stat);
        }
    }
    
    /**
     * Handle an illegal moveset.
     */
    private void handleIllegalMoveset(int species, String moveset) {
        // To be done (tbd).
    }
    
    /**
     * Handle one line of a data patch file.
     */
    private void parsePatchLine(String line, int category) {
        // Collapse white space.
        line = line.replaceAll("[ \\n\\r\\t]+", " ");
        // Find the colon between the pokemon name and the data.
        int idx = line.indexOf(':');
        if (idx == -1) {
            System.out.println("Malformed patch file statement: " + line);
            return;
        }
        String species = line.substring(0, idx).trim();
        int id = m_species.getPokemonByName(species).getSpecies();
        if (id == -1) {
            System.out.println("Warning: no existing species of " + species + ".");
            return;
        }
        ArrayList<String> moves = new ArrayList<String>();
        ArrayList<String> abilities = new ArrayList<String>();
        ArrayList<String> items = new ArrayList<String>();
        String[] parts = line.substring(idx + 1).trim().split(" *, *");
        for (int i = 0; i < parts.length; ++i) {
            String part = parts[i];
            int length = part.length();
            if (length == 0) {
                //System.out.println("Warning: empty element: " + line);
                continue;
            }
            if (part.charAt(0) == '~') {
                handleIllegalMoveset(id, part.substring(1).trim());
                continue;
            }
            if (part.indexOf(':') != -1) {
                modifyStat(id, part.trim());
                continue;
            }
            char s = (length > 1) ? part.charAt(1) : '\0';
            boolean sign = (length > 1) ? ((s == '+') || (s == '-')) : false;
            switch (part.charAt(0)) {
                case 'a':
                    if (sign) {
                        // It's an ability.
                        abilities.add(part);
                        break;
                    }
                case 'i':
                    if (sign) {
                        // It's an item.
                        items.add(part);
                        break;
                    }
                default:
                    // It's a move.
                    moves.add(part);
                    break;
            }
        }
        handleMoves(id, moves, category);
        handleAbilities(species, abilities);
        handleItems(species, items);
    }
    
    public void applyPatch(InputStream stream) throws IOException {
        applyPatch(stream, 0);
    }
    
    /**
     * Apply a mod data patch to this ModData object.
     */
    public void applyPatch(InputStream stream, int category) throws IOException {
        DataInputStream in = new DataInputStream(stream);
        while (true) {
            StringBuffer buffer = new StringBuffer();
            while (true) {
                try {
                    byte c = in.readByte();
                    if (c == ';') {
                        break;
                    }
                    buffer.append((char)(c & 0xff));
                } catch (EOFException e) {
                    return;
                }
            }
            try {
                parsePatchLine(buffer.toString(), category);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get the location where mod data is stored on the hard disc.
     */
    public static String getStorageLocation() {
        Preferences prefs = Preferences.userRoot();
        return prefs.get(REGISTRY_KEY, null);
    }
    
    /**
     * Set the location where mod data is stored on the hard disc.
     */
    public static void setStorageLocation(String value) {
        Preferences prefs = Preferences.userRoot();
        value.replace('/', File.separatorChar);
        value.replace('\\', File.separatorChar);
        if (!value.endsWith(File.separator)) {
            value += File.separator;
        }
        prefs.put(REGISTRY_KEY, value);
    }
    
    /**
     * Add a server to the map of uuid -> server name.
     */
    public static void addServer(String uuid, String name) {
        Preferences.userRoot().put("map." + uuid, name);
    }
    
    /**
     * Find a server name given its uuid.
     */
    public static String getServerName(String uuid) {
        return Preferences.userRoot().get("map." + uuid, null);
    }
    
}
