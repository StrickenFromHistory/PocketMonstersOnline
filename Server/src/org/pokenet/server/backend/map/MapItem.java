package org.pokenet.server.backend.map;

/**
 * A map item
 * @author shadowkanji
 *
 */
public class MapItem {
	private int m_x;
	private int m_y;
	private int m_id;
	
	/**
	 * Constructor
	 * @param x
	 * @param y
	 * @param id
	 */
	public MapItem(int x, int y, int id) {
		m_x = x;
		m_y = y;
		m_id = id;
	}

	/**
	 * Sets the x co-ordinate of the item on map
	 * @param x
	 */
	public void setX(int x) {
		m_x = x;
	}
	
	/**
	 * Sets the y co-ordinate of the item on map
	 * @param y
	 */
	public void setY(int y) {
		m_y = y;
	}
	
	/**
	 * Sets the item id of this item
	 * @param id
	 */
	public void setId(int id) {
		m_id = id;
	}
	
	/**
	 * Returns the x co-ordinate of this item
	 * @return
	 */
	public int getX() {
		return m_x;
	}
	
	/**
	 * Returns the y co-ordinate of this item
	 * @return
	 */
	public int getY() {
		return m_y;
	}
	
	/**
	 * Returns the item id of this item
	 * @return
	 */
	public int getId() {
		return m_id;
	}
}
