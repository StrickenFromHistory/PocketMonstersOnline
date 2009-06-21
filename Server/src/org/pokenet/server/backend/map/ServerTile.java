package org.pokenet.server.backend.map;

/**
 * A tile
 * @author shadowkanji
 *
 */
public class ServerTile {
	private int m_x;
	private int m_y;
	
	/**
	 * Constructor
	 * @param x
	 * @param y
	 */
	public ServerTile(int x, int y) {
		m_x = x;
		m_y = y;
	}
	
	/**
	 * Returns the x co-ordinate of the tile
	 * @return
	 */
	public int getX() {
		return m_x;
	}
	
	/**
	 * Returns the y co-ordinate of the tile
	 * @return
	 */
	public int getY() {
		return m_y;
	}
}
