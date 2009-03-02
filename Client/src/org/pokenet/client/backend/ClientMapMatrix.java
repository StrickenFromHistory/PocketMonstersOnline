package org.pokenet.client.backend;

import java.util.ArrayList;

import org.pokenet.client.backend.entity.Player;

/**
 * Represents the current map the player is on and its surrounding maps to be rendered on screen.
 * @author shadowkanji
 *
 */
public class ClientMapMatrix {
	private ClientMap [][] m_mapMatrix;
	private ArrayList<Player> m_players;
	
	/**
	 * Default constructor
	 */
	public ClientMapMatrix() {
		m_mapMatrix = new ClientMap[3][3];
		m_players = new ArrayList<Player>();
	}
	
	/**
	 * Loads the map with co-ordinates x,y and its surrounding maps
	 * @param x
	 * @param y
	 */
	public void loadMaps(int x, int y) {
		
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
		return m_mapMatrix[x][y];
	}
}
