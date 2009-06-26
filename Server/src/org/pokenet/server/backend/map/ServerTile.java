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
	
	/**
	 * Returns 0 if they are equal
	 * @param o
	 * @return
	 */
	public int compareTo(Object o) {
		if(o instanceof ServerTile) {
			ServerTile t = (ServerTile) o;
			return t.getX() == m_x && t.getY() == m_y ? 0 : -1;
		}
		return -1;
	}
	
	/**
	 * Returns true of objects are equal
	 */
	public boolean equals(Object obj) {
		if(obj instanceof ServerTile) {
			ServerTile t = (ServerTile) obj;
			return t.getX() == m_x && t.getY() == m_y;
		}
		return false;
	}
}
