package org.pokenet.server.feature;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 * Stores a database of pokemon caught by fishing
 * @author shadowkanji
 * @author Fshy
 *
 */
@Root
public class FishDatabase {
	@ElementMap
	private HashMap<String, ArrayList<Fish>> m_database;
	
	/**
	 * Adds an entry to the database
	 * @param pokemon
	 * @param fishes
	 */
	public void addEntry(String pokemon, ArrayList<Fish> fishes) {
		if(m_database == null)
			m_database = new HashMap<String, ArrayList<Fish>>();
		m_database.put(pokemon, fishes);
	}
	
	/**
	 * Removes an entry from the database
	 * @param pokemon
	 */
	public void deleteEntry(String pokemon) {
		if(m_database == null) {
			m_database = new HashMap<String, ArrayList<Fish>>();
			return;
		}
		m_database.remove(pokemon);
	}
	
	public Fish getFish(String pokemon) {
		pokemon = pokemon.toUpperCase();
		return m_database.get(pokemon).get(0);
		}
	
	/**
	 * Reinitialises the database
	 */
	public void reinitialise() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				m_database = new HashMap<String, ArrayList<Fish>>();
				try {
					/* Read all the data from the text file */
					Scanner s = new Scanner(new File("./res/fishing.txt"));
					String pokemon = "";
					ArrayList<Fish> fishies = new ArrayList<Fish>();
					while(s.hasNextLine()) {
						pokemon = s.nextLine();
						fishies = new ArrayList<Fish>();
						/* Parse the data in the order EXPERIENCE, LEVELREQ, RODREQ*/
						StringTokenizer st = new StringTokenizer(pokemon);
						String pokeName = st.nextToken().toUpperCase();
						while(st.hasMoreTokens()) {
							int levelreq = Integer.parseInt(st.nextToken());
							int exp = Integer.parseInt(st.nextToken());
							int rodreq = Integer.parseInt(st.nextToken());
							Fish d = new Fish(exp, levelreq, rodreq);
							fishies.add(d);
						}
						addEntry(pokeName, fishies);
					}
					s.close();
					System.out.println("INFO: Fishing database reinitialised");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
}
