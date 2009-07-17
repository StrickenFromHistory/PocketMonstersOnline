/*
 * HoldItemData.java
 *
 * Created on May 19, 2007, 4:33 PM
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Colin
 */
public class HoldItemData {
    
    /*package*/ TreeSet<Object> m_items = new TreeSet<Object>();
    /*package*/ HashMap<String, HashSet<String>> m_exclusives = new HashMap<String, HashSet<String>>();
    
    /**
     * Return whether the named species can use a particular item.
     */
    public boolean canUseItem(String species, String item) {
        if (m_items.contains(item)) {
            return true;
        }
        Object o = m_exclusives.get(species);
        if (o == null) {
            return false;
        }
        return ((HashSet<?>)o).contains(item);
    }
    
    /**
     * Get an item set corresponding to the named species.
     */
    @SuppressWarnings("unchecked")
	public SortedSet<Object> getItemSet(String species) {
        Object o = m_exclusives.get(species);
        if (o == null) {
            return m_items;
        }
        SortedSet<Object> items = (SortedSet<Object>)m_items.clone();
        items.addAll((Collection)o);
        return items;
    }
    
    /**
     * Write item data to an arbitrary output stream.
     */
    public void saveItemData(OutputStream output) throws IOException {
        ObjectOutputStream stream = new ObjectOutputStream(output);
        stream.writeObject(m_items);
        stream.writeObject(m_exclusives);
        stream.flush();
    }
    
    /**
     * Read item data in from an arbitrary input stream.
     * To be only only by the client - does not initialise for battles!
     */
    @SuppressWarnings("unchecked")
	public void loadItemData(InputStream input) throws IOException, FileNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(input);
        try {
            m_items = (TreeSet)stream.readObject();
            m_exclusives = (HashMap)stream.readObject();
        } catch (ClassNotFoundException e) {
            
        }
    }
    
    /**
     *  Remove an exclusive item from a pokemon.
     */
    @SuppressWarnings("unchecked")
	public void removeExclusiveItem(String name, String pokemon) {
        Object o = m_exclusives.get(pokemon);
        if (o == null) {
            return;
        }

        ((HashSet)o).remove(name);
    }
    
    /**
     * Add an exclusive item to a pokemon.
     */
    @SuppressWarnings("unchecked")
	public void addExclusiveItem(String name, String pokemon) {
        Object o = m_exclusives.get(pokemon);
        if (o == null) {
            HashSet<String> set = new HashSet<String>();
            set.add(name);
            m_exclusives.put(pokemon, set);
        } else {
            ((HashSet<String>)o).add(name);
        }
    }
}
