package org.pokenet.client.backend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.util.Timer;
import org.newdawn.slick.Graphics;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.HMObject;
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
	private Timer m_calibrationTimer = new Timer();
	private char m_newMapPos;
	
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
	 * Loads a map
	 * @param mapX Map's X coordinate
	 * @param mapY Map's Y coordinate
	 * @param x Map's X within the map matrix
	 * @param y Map's Y within the map matrix
	 */
	public void loadMap (int mapX, int mapY, int x, int y){
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		try {
			System.out.println("Map: " + x + ", " + y);
			InputStream f = FileLoader.loadFile(respath+"res/maps/" + (mapX) + "." + (mapY) + ".tmx");
			if(f != null) {
				try {
					m_mapMatrix[x][y] = new ClientMap(respath+"res/maps/"+(mapX)+"."+(mapY)+".tmx");
					if(m_mapMatrix[x][y]==null) System.out.println("Client Map is null");
					m_mapMatrix[x][y].setMapMatrix(this);
					m_mapMatrix[x][y].setMapX(x);
					m_mapMatrix[x][y].setMapY(y);
					m_mapMatrix[x][y].m_x = mapX + x;
					m_mapMatrix[x][y].m_y = mapY + y;
					m_mapMatrix[x][y].setCurrent(x == 1 && y == 1);
					m_mapMatrix[x][y].setName(getMapName(mapX, mapY));
				} catch (Exception e) {
					m_mapMatrix[x][y] = null;
					System.out.println((mapX) + "." + (mapY) + ".tmx could not be loaded");
					e.printStackTrace();
				}
			} else {
				m_mapMatrix[x][y] = null;
				System.out.println(respath+(mapX) + "." + (mapY) + ".tmx could not be loaded");
			}
		} catch (FileNotFoundException e1) {
			m_mapMatrix[x][y] = null;
			System.out.println("File not found: "+respath+(mapX) + "." + (mapY) + ".tmx");
		}
		
	}

	/**
	 * Shifts a map in the map coordinate
	 * @param originalX
	 * @param originalY
	 * @param newX
	 * @param newY
	 */
	public void shiftMap(int originalX, int originalY, int newX, int newY){
		m_mapMatrix[newX][newY] = m_mapMatrix[originalX][originalY];
		if (m_mapMatrix[newX][newY] != null) {
			m_mapMatrix[newX][newY].setMapX(newX);
			m_mapMatrix[newX][newY].setMapY(newY);
			m_mapMatrix[newX][newY].setCurrent(newX == 1 && newY == 1);
			m_mapMatrix[newX][newY].reinitialize();
		}
	}
	
	/**
	 * Loads the map with co-ordinates x,y and its surrounding maps
	 * @param x
	 * @param y
	 */
	public void loadMaps(int mapX, int mapY, Graphics g) {
		/*
		 * Loads the main map and surrounding maps
		 */
		if(mapX >= -30 && !GameClient.disableMaps()) {
			/*
			 * Exterior, load surrounding maps
			 */
			if (getCurrentMap() != null) {
				switch (m_newMapPos){
				case 'u':
					// Moved Up
					// Shift current maps
					shiftMap(2, 1, 2, 2);
					shiftMap(1, 1, 1, 2);
					shiftMap(0, 1, 0, 2);
					shiftMap(2, 0, 2, 1);
					shiftMap(1, 0, 1, 1);
					shiftMap(0, 0, 0, 1);
					//Load other maps
					for (int i = 0; i < 3; i++) {
						loadMap(mapX-1+i, mapY - 1, i, 0);
						System.out.println("Load map( " + (mapX-1+i) + ", " + (mapY-1)+", "+i+", 0)");
					}
					break;
				case 'd':
					// Moved Down
					// Shift current maps
					shiftMap(0, 1, 0, 0);
					shiftMap(1, 1, 1, 0);
					shiftMap(2, 1, 2, 0);
					shiftMap(0, 2, 0, 1);
					shiftMap(1, 2, 1, 1);
					shiftMap(2, 2, 2, 1);
					//Load other maps
					for (int i = 0; i < 3; i++) {
						loadMap(mapX-1+i, mapY + 1, i, 2);
						System.out.println("Load map( " + (mapX-1+i) + ", " + (mapY+1)+", "+i+", 2)");
					}
					break;
				case 'r':
					// Moved Right
					// Shift current maps
					shiftMap(1, 0, 0, 0);
					shiftMap(1, 1, 0, 1);
					shiftMap(1, 2, 0, 2);
					shiftMap(2, 0, 1, 0);
					shiftMap(2, 1, 1, 1);
					shiftMap(2, 2, 1, 2);
					//Load other maps
					for (int i = 0; i < 3; i++) {
						loadMap(mapX + 1, mapY-1+i, 2, i);
						System.out.println("Load map( " + (mapX+1) + ", " + (mapY-1+i)+", 2, "+i+")");
					}
					break;
				case 'l':
					// Moved Left
					// Shift current maps
					shiftMap(1, 0, 2, 0);
					shiftMap(1, 1, 2, 1);
					shiftMap(1, 2, 2, 2);
					shiftMap(0, 0, 1, 0);
					shiftMap(0, 1, 1, 1);
					shiftMap(0, 2, 1, 2);
					//Load other maps
					for (int i = 0; i < 3; i++) {
						loadMap(mapX - 1, mapY-1+i, 0, i);
						System.out.println("Load map( " + (mapX-1) + ", " + (mapY-1+i)+", 0,"+ i+")");
					}
					break;
				case 'n':
					// Brand new map
					for(int x = -1; x < 2; x++) {
						for(int y = -1; y < 2; y++) {
							loadMap(mapX + x, mapY + y, x + 1, y + 1);
						}
					}
					break;
				}
			} else {
				// Brand new map
				for(int x = -1; x < 2; x++) {
					for(int y = -1; y < 2; y++) {
						loadMap(mapX + x, mapY + y, x + 1, y + 1);
					}
				}
			}
		} else {
			/*
			 * Interior, only load one map
			 */
			m_mapMatrix[0][0] = null;
			m_mapMatrix[0][1] = null;
			m_mapMatrix[0][2] = null;
			m_mapMatrix[1][0] = null;
			m_mapMatrix[1][2] = null;
			m_mapMatrix[2][0] = null;
			m_mapMatrix[2][1] = null;
			m_mapMatrix[2][2] = null;
			loadMap(mapX, mapY, 1, 1);
		}
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		/*
		 * Load speech for current map
		 */
		if(m_speech.size() > 0)
			m_speech.clear();
		try {
			try {
				BufferedReader reader = FileLoader.loadTextFile(respath+"res/language/" + GameClient.getLanguage()
						+ "/NPC/" + mapX + "." + mapY + ".txt");
				String line;
				while((line = reader.readLine()) != null) {
					m_speech.add(line);
				}
			} catch (NullPointerException e) { //In case of emergencies, load english!
				try{
					BufferedReader reader = FileLoader.loadTextFile(respath+"res/language/english/NPC/" + mapX + "."
							+ mapY + ".txt");
							
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
		for (m_calibrationTimer.reset(); m_calibrationTimer.getTime() < 2; Timer.tick());
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
	
	public ArrayList<HMObject> getHMObjects() {
		ArrayList<HMObject> temp = new ArrayList<HMObject>();
		for (Player p : m_players) {
			if(p.getType() == 2)
				temp.add((HMObject)p);
		}
		return temp;
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
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		try {
			BufferedReader reader;
			try{
				reader = FileLoader.loadTextFile(respath+"res/language/" + GameClient.getLanguage()
						+ "/_MAPNAMES.txt");
			} catch (Exception e){
				reader = FileLoader.loadTextFile(respath+"res/language/english/_MAPNAMES.txt");
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
	
	/**
	 * Sets the new map's location relative to the map matrix
	 * @param c
	 */
	public void setNewMapPos(char c) {
		m_newMapPos = c;
	}
}