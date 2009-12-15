package org.pokenet.client.backend;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.pokenet.client.backend.entity.Item;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * The item database
 * @author shadowkanji
 * @author Nushio
 */
@Root
public class ItemDatabase {
	@ElementMap
	private HashMap<Integer, Item> m_items;
	
	private static ItemDatabase m_instance;
	
	/**
	 * Adds an item to the database
	 * @param id
	 * @param i
	 */
	public void addItem(int id, Item i) {
		if(m_items == null)
			m_items = new HashMap<Integer, Item>();
		m_items.put(id, i);
	}
	
	/**
	 * Returns an item based on its id
	 * @param id
	 * @return
	 */
	public Item getItem(int id) {
		return m_items.get(id);
	}
	
	/**
	 * Returns an item based on its name
	 * @param name
	 * @return
	 */
	public Item getItem(String name) {
		Iterator<Item> it = m_items.values().iterator();
		Item i;
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
		try {
			String respath = System.getProperty("res.path");
			if(respath==null)
				respath="";
			InputStream source = FileLoader.loadFile(respath+"res/items/items.xml");
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
	/**
	 * Returns the instance of item database
	 * @return
	 */
	public static List<Item> getCategoryItems(String category) {
		List<Item> itemList = new ArrayList<Item>();
		for(int i=0;i<=m_instance.m_items.size();i++){
			try{
				Item item = m_instance.m_items.get(i);
				if(item.getCategory().equals(category))
					itemList.add(item);
			}catch(Exception e){}
		}
		return itemList;
	}
}
