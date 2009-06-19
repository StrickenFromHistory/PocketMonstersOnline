package org.pokenet.server.backend.item;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.pokenet.server.battle.DataService;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 * Stores a database of items that Pokemon can drop
 * @author shadowkanji
 *
 */
@Root
public class DropDatabase {
	@ElementMap
	private HashMap<String, ArrayList<DropData>> m_database;
	
	/**
	 * Adds an entry to the database
	 * @param pokemon
	 * @param items
	 */
	public void addEntry(String pokemon, ArrayList<DropData> items) {
		if(m_database == null)
			m_database = new HashMap<String, ArrayList<DropData>>();
		m_database.put(pokemon, items);
	}
	
	/**
	 * Removes an entry from the database
	 * @param pokemon
	 */
	public void deleteEntry(String pokemon) {
		if(m_database == null) {
			m_database = new HashMap<String, ArrayList<DropData>>();
			return;
		}
		m_database.remove(pokemon);
	}
	
	/**
	 * Returns a random item dropped by a Pokemon.
	 * Returns -1 if no item was dropped.
	 * @param pokemon
	 * @return
	 */
	public int getRandomItem(String pokemon) {
		pokemon = pokemon.toUpperCase();
		int size = m_database.get(pokemon).size();
		if(size > 0) {
			int r = 100;
			ArrayList<DropData> m_items = m_database.get(pokemon);
			ArrayList<Integer> m_result = new ArrayList<Integer>();
			for(int i = 0; i < m_items.size(); i++) {
				r = DataService.getBattleMechanics().getRandom().nextInt(100) + 1;
				if(r < m_items.get(i).getProbability())
					m_result.add(m_items.get(i).getItemNumber());
			}
			return m_result.size() > 0 ? 
					m_result.get(DataService.getBattleMechanics()
							.getRandom().nextInt(m_result.size())) : -1;
		}
		return -1;
	}
	
	/**
	 * Reinitialises the database
	 */
	public void reinitialise() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				m_database = new HashMap<String, ArrayList<DropData>>();
				try {
					/* Read all the data from the text file */
					Scanner s = new Scanner(new File("./res/itemdrops.txt"));
					String pokemon = "";
					ArrayList<DropData> drops = new ArrayList<DropData>();
					while(s.hasNextLine()) {
						pokemon = s.nextLine();
						drops = new ArrayList<DropData>();
						/* Parse the data in the form ITEM, PROBABILITY */
						StringTokenizer st = new StringTokenizer(pokemon);
						String pokeName = st.nextToken().toUpperCase();
						while(st.hasMoreTokens()) {
							int item = Integer.parseInt(st.nextToken());
							int p = Integer.parseInt(st.nextToken());
							DropData d = new DropData(item, p);
							drops.add(d);
						}
						addEntry(pokeName, drops);
					}
					s.close();
					System.out.println("INFO: Item Drop database reinitialised");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
}
