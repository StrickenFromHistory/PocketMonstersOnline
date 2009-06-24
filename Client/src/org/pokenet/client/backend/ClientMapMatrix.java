package org.pokenet.client.backend;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Graphics;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Player;

/**
 * Represents the current map the player is on and its surrounding maps to be rendered on screen.
 * @author shadowkanji
 * @author ZombieBear
 *
 */
public class ClientMapMatrix {
	private ClientMap [][] m_mapMatrix;
	private ArrayList<Player> m_players;
	private ArrayList<String> m_speech;
	private HashMap<String, String> m_mapNames;
	
	/**
	 * Default constructor
	 */
	public ClientMapMatrix() {
		m_mapMatrix = new ClientMap[3][3];
		m_players = new ArrayList<Player>();
		m_speech = new ArrayList<String>();
		m_mapNames = new HashMap<String, String>();
		loadMapNames();
	}
	
	/**
	 * Loads the map with co-ordinates x,y and its surrounding maps
	 * @param x
	 * @param y
	 */
	public void loadMaps(int mapX, int mapY, Graphics g) {
		InputStream f;
		/*
		 * Loads the main map and surrounding maps
		 */
		if(mapX >= -30 && !GameClient.disableMaps()) {
			/*
			 * Exterior, load surrounding maps
			 */
			for(int x = -1; x < 2; x++) {
				for(int y = -1; y < 2; y++) {
					f = getClass().getResourceAsStream("/res/maps/" + (mapX + x) + "." + (mapY + y) + ".tmx");
					if(f != null) {
						try {
							m_mapMatrix[x + 1][y + 1] = new ClientMap("/res/maps/" + String.valueOf((mapX + x))
									+ "." + String.valueOf((mapY + y)) + ".tmx","/res/maps");
							m_mapMatrix[x + 1][y + 1].setMapMatrix(this);
							m_mapMatrix[x + 1][y + 1].setGraphics(g);
							m_mapMatrix[x + 1][y + 1].setMapX(x + 1);
							m_mapMatrix[x + 1][y + 1].setMapY(y + 1);
							m_mapMatrix[x + 1][y + 1].setCurrent(x == 0 && y == 0);
							System.out.println((mapX + x) + "." + (mapY + y) + ".tmx loaded " +
									"to MapMatrix[" + (x + 1) + "][" + (y + 1) + "]");
							m_mapMatrix[x + 1][y + 1].setName(getMapName(mapX, mapY));
						} catch (Exception e) {
							m_mapMatrix[x + 1][y + 1] = null;
						}
					} else {
						m_mapMatrix[x + 1][y + 1] = null;
					}
				}
			}
		} else {
			/*
			 * Interior, only load one map
			 */
			for(int x = -1; x < 2; x++) {
				for(int y = -1; y < 2; y++) {
					if(x == 0 && y == 0) {
						f = getClass().getResourceAsStream("/res/maps/" + (mapX + x) + "." + (mapY + y) + ".tmx");
						if(f != null) {
							try {
								m_mapMatrix[x + 1][y + 1] = new ClientMap("/res/maps/" + String.valueOf((mapX + x))
										+ "." + String.valueOf((mapY + y)) + ".tmx","/res/maps");
								m_mapMatrix[x + 1][y + 1].setMapMatrix(this);
								m_mapMatrix[x + 1][y + 1].setGraphics(g);
								m_mapMatrix[x + 1][y + 1].setMapX(x + 1);
								m_mapMatrix[x + 1][y + 1].setMapY(y + 1);
								m_mapMatrix[x + 1][y + 1].setCurrent(x == 0 && y == 0);
								System.out.println((mapX + x) + "." + (mapY + y) + ".tmx loaded " +
										"to MapMatrix[" + (x + 1) + "][" + (y + 1) + "]");
							} catch (Exception e) {
								m_mapMatrix[x + 1][y + 1] = null;
							}
						} else {
							m_mapMatrix[x + 1][y + 1] = null;
						}
					} else {
						m_mapMatrix[x + 1][y + 1] = null;
					}
				}
			}
		}
		
		/*
		 * Load speech for current map
		 */
		if(m_speech.size() > 0)
			m_speech.clear();
		try {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(
						"/res/language/" + GameClient.getLanguage() + "/NPC/" + mapX + "." + mapY + ".txt")));
						
				String line;
				while((line = reader.readLine()) != null) {
					m_speech.add(line);
				}
			} catch (NullPointerException e) { //In case of emergencies, load english!
				try{
					BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(
							"/res/language/english/NPC/" + mapX + "." + mapY + ".txt")));
							
					String line;
					while((line = reader.readLine()) != null) {
						m_speech.add(line);
					}
				}catch(Exception e2){
					m_speech.add("\n"); //If there's no english, display default line. 
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * Recalibrate the offsets
		 */
		this.recalibrate();
	}
	
	/**
	 * Returns the speech of line index or an empty string if it does not exist
	 * @param index
	 * @return
	 */
	public String getSpeech(int index) {
		return m_speech.size() > index ? 
				m_speech.get(index) != null && !m_speech.get(index).equalsIgnoreCase("") ?
						m_speech.get(index) : "Trainer Tip: Don't eat yellow snow!" : "Trainer Tip: Don't eat yellow snow!";
	}
	
	/**
	 * Recalibrates the offsets of this map
	 */
	public void recalibrate() {
		m_mapMatrix[1][1].setXOffset(m_mapMatrix[1][1].getXOffset(), true);
		m_mapMatrix[1][1].setYOffset(m_mapMatrix[1][1].getYOffset(), true);
	}
	
	/**
	 * Returns the current map
	 * @return
	 */
	public ClientMap getCurrentMap() {
		return m_mapMatrix[1][1];
	}
	
	/**
	 * Returns the arraylist of players on the current map
	 * @return
	 */
	public ArrayList<Player> getPlayers() {
		return m_players;
	}
	
	public Player getPlayer(int id) {
		for(int i = 0; i < m_players.size(); i++) {
			if(m_players.get(i).getId() == id)
				return m_players.get(i);
		}
		return null;
	}
	
	/**
	 * Adds a player to the list of players
	 * @param p
	 */
	public void addPlayer(Player p) {
		m_players.add(p);
	}
	
	/**
	 * Returns a player based on their id
	 * @param id
	 */
	public void removePlayer(int id) {
		for(int i = 0; i < m_players.size(); i++) {
			if(m_players.get(i).getId() == id) {
				m_players.remove(i);
				return;
			}
		}
	}
	
	/**
	 * Returns the map at x,y in the map matrix
	 * @param x
	 * @param y
	 * @return
	 */
	public ClientMap getMap(int x, int y) {
		if(x >= 0 && x <= 2 && y >= 0 && y <= 2)
			return m_mapMatrix[x][y];
		else
			return null;
	}
	
	/**
	 * Laods the map names
	 */
	private void loadMapNames() {
		try {
			BufferedReader reader;
			try{
				reader = FileLoader.loadTextFile("/res/language/" + GameClient.getLanguage()
						+ "/_MAPNAMES.txt");
			} catch (Exception e){
				reader = FileLoader.loadTextFile("/res/language/english/_MAPNAMES.txt");
			}
			
			String f;
			while ((f = reader.readLine()) != null) {
				if (f.charAt(0) != '*'){
					final String[] details = f.split(",");
					try{
						m_mapNames.put(details[0] + ", " + details[1], details[2]);
					} catch (Exception e) {System.out.println(details[0] + " " + details[1]);}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to load locations");
		}
	}
	
	/**
	 * Returns the map's name
	 * @param x
	 * @param y
	 * @return the map's name
	 */
	public String getMapName(int x, int y) {
		return m_mapNames.get(String.valueOf(x) + ", " + String.valueOf(y));
	}
}
