package org.pokenet.server.backend;

import org.pokenet.server.backend.entity.Char;

/**
 * Stores all maps on the server in a 2D array
 * @author shadowkanji
 *
 */
public class ServerMapMatrix {
	private ServerMap[][] m_mapMatrix;
	
	/**
	 * Default constructor
	 */
	public ServerMapMatrix() {
		m_mapMatrix = new ServerMap[100][100];
	}
	
	/**
	 * Returns a server map based on its in-game position (some maps are named negatively, e.g. -50.-50.tmx)
	 * @param x
	 * @param y
	 * @return
	 */
	public ServerMap getMapByGamePosition(int x, int y) {
		return m_mapMatrix[x + 50][x + 50];
	}
	
	/**
	 * Returns a server map based on its actual position in the server map array.
	 * @param x
	 * @param y
	 * @return
	 */
	public ServerMap getMapByRealPosition(int x, int y) {
		return m_mapMatrix[x][y];
	}
	
	/**
	 * Moves a player between two maps
	 * @param c
	 * @param origin
	 * @param destination
	 */
	public void moveBetweenMaps(Char c, ServerMap origin, ServerMap destination) {
		
	}
}
