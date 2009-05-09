package org.pokenet.server.backend;

import java.util.HashMap;
import java.util.Iterator;

import org.pokenet.server.backend.entity.PlayerChar;

/**
 * An item class for using items and associating items with ids
 * NOTE: Only one instance of itemservice should ever exist
 * @author tom
 *
 */
public class ItemService {
	private static HashMap<String, Integer> m_items;
	
	/**
	 * Returns true if the player successfully used an item
	 * @param p
	 * @param id
	 * @return
	 */
	public static boolean useItem(PlayerChar p, int id) {
		switch(id) {
		
		}
		return false;
	}
	
	/**
	 * Constructor
	 */
	public ItemService() {
		m_items = new HashMap<String, Integer>();
	}
	/**
	 * Returns the id of an item based on its name
	 * @param name
	 * @return
	 */
	public static int getId(String name) {
		return m_items.get(name.toUpperCase());
	}
	
	/**
	 * Returns the name of an item based on its id
	 * @param id
	 * @return
	 */
	public static String getName(int id) {
		Iterator<String> it = m_items.keySet().iterator();
		String name = "";
		int i = -1;
		while(it.hasNext()) {
			name = it.next();
			i = m_items.get(it.next());
			if(i == id) {
				return name;
			}
		}
		return "";
	}
}
