package org.pokenet.client.backend;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.pokenet.client.backend.entity.PlayerItem;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * The item database
 * @author shadowkanji
 */
@Root
public class ItemDatabase {
	@ElementMap
	private HashMap<Integer, PlayerItem> m_items;
	
	private static ItemDatabase m_instance;
	
	/**
	 * Adds an item to the database
	 * @param id
	 * @param i
	 */
	public void addItem(int id, PlayerItem i) {
		if(m_items == null)
			m_items = new HashMap<Integer, PlayerItem>();
		m_items.put(id, i);
	}
	
	/**
	 * Returns an item based on its id
	 * @param id
	 * @return
	 */
	public PlayerItem getItem(int id) {
		return m_items.get(id);
	}
	
	/**
	 * Returns an item based on its name
	 * @param name
	 * @return
	 */
	public PlayerItem getItem(String name) {
		Iterator<PlayerItem> it = m_items.values().iterator();
		PlayerItem i;
		while(it.hasNext()) {
			i = it.next();
			if(i.getName().equalsIgnoreCase(name))
				return i;
		}
		return null;
	}
	
	/**
	 * Reloads the database
	 */
	public void reinitialise() {
		Serializer serializer = new Persister();
		File source = new File("./res/items/items.xml");
		try {
			m_instance = serializer.read(ItemDatabase.class, source);
			System.out.println("INFO: Items database loaded.");
		} catch (Exception e) {
			System.err.println("ERROR: Item database could not be loaded.");
		}
	}
	
	/**
	 * Sets the instance
	 * @param i
	 */
	public void setInstance(ItemDatabase i) {
		m_instance = i;
	}
	
	/**
	 * Returns the instance of item database
	 * @return
	 */
	public static ItemDatabase getInstance() {
		return m_instance;
	}
}
