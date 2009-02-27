package org.pokenet.server.backend.entity;

import org.pokenet.server.backend.ServerMap;

/**
 * Base class for a character. Note: Originally this implemented Battleable but not all chars are battleable
 * @author shadowkanji
 *
 */
public class Char implements Positionable {
	private Direction m_nextMovement = null;
	private Direction m_facing = Direction.Down;
	private long m_lastMovement = System.currentTimeMillis();
	protected int m_sprite, m_mapX, m_mapY, m_x, m_y, m_id;
	private boolean m_isVisible, m_isSurfing;
	private String m_name;
	private ServerMap m_map;
	
	/**
	 * Returns the direction this char is facing
	 */
	public Direction getFacing() {
		return m_facing;
	}

	/**
	 * Returms the map this char is on
	 */
	public ServerMap getMap() {
		return m_map;
	}

	/**
	 * Returns the mapX of this char
	 */
	public int getMapX() {
		return m_mapX;
	}

	/**
	 * Returns the mayY of this char
	 */
	public int getMapY() {
		return m_mapY;
	}

	/**
	 * Returns the sprite of this char. Will return the surf sprite if the char is surfing
	 */
	public int getSprite() {
		return m_isSurfing ? 0 : m_sprite;
	}

	/**
	 * Returns the x co-ordinate of this char
	 */
	public int getX() {
		return m_x;
	}

	/**
	 * Returns the y co-ordinate of this char
	 */
	public int getY() {
		return m_y;
	}

	/**
	 * Returns if this char is visible
	 */
	public boolean isVisible() {
		return m_isVisible;
	}

	/**
	 * Sets the map this player is and handles all networking to client that is involved with it
	 */
	public void setMap(ServerMap map) {
		//Remove the char from their old map
		m_map.removeChar(this);
		//Set their current map to the new map
		m_map = map;
		m_mapX = map.getX();
		m_mapY = map.getY();
		//Add the char to the map
		m_map.addChar(this);
	}

	/**
	 * Set the sprite of this char
	 */
	public void setSprite(int sprite) {
		m_sprite = sprite;
		//TODO: Send sprite change update to player
	}

	/**
	 * Set if this char is visible
	 */
	public void setVisible(boolean visible) {
		m_isVisible = visible;
	}

	/**
	 * Returns the name of the char
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Sets the name of this char
	 * NOTE: For PlayerChar's this is their username
	 * @param name
	 */
	public void setName(String name) {
		m_name = name;
	}

	/**
	 * Moves the char if m_nextMovement != null
	 */
	public void move() {
		if(m_nextMovement != null && m_map != null) {
			//Move the player
			if(m_facing != m_nextMovement) {
				switch(m_nextMovement) {
				case Up:
					m_facing = Direction.Up;
					m_map.sendToAll("cU" + m_id);
					break;
				case Down:
					m_facing = Direction.Down;
					m_map.sendToAll("cD" + m_id);
					break;
				case Left:
					m_facing = Direction.Left;
					m_map.sendToAll("cL" + m_id);
					break;
				case Right:
					m_facing = Direction.Right;
					m_map.sendToAll("cR" + m_id);
					break;
				}
			} else if(m_map.moveChar(this, m_nextMovement)) {
				switch(m_nextMovement) {
				case Up:
					m_y -= 32;
					m_facing = Direction.Up;
					m_map.sendToAll("U" + m_id);
					break;
				case Down:
					m_y += 32;
					m_facing = Direction.Down;
					m_map.sendToAll("D" + m_id);
					break;
				case Left:
					m_x -= 32;
					m_facing = Direction.Left;
					m_map.sendToAll("L" + m_id);
					break;
				case Right:
					m_x += 32;
					m_facing = Direction.Right;
					m_map.sendToAll("R" + m_id);
					break;
				}
			}
			m_nextMovement = null;
			m_lastMovement = System.currentTimeMillis();
		}
	}

	/**
	 * Sets the char's next movement
	 */
	public void setNextMovement(Direction dir) {
		if(System.currentTimeMillis() - m_lastMovement > 100)
			m_nextMovement = dir;
	}

	/**
	 * Returns this char's id
	 */
	public int getId() {
		return m_id;
	}
	
	/**
	 * Sets this char's id.
	 * NOTE: PlayerChars ids are permanent, given upon registration
	 * NOTE: NonPlayerChars ids are dynamic, based on how many other npcs are on the same map
	 */
	public void setId(int id) {
		m_id = id;
	}
	
	/**
	 * Returns if two chars are the same based on id
	 * @param c
	 * @return
	 */
	public boolean equals(Char c) {
		return m_id == c.getId();
	}

	/**
	 * Sets the x co-ordinate of this character on the map
	 */
	public void setX(int x) {
		m_x = x;
	}

	/**
	 * Sets the y co-ordinate of this character on the map
	 */
	public void setY(int y) {
		m_y = y;
	}
	
	/**
	 * Sets the map x co-ordinate
	 * @param x
	 */
	public void setMapX(int x) {
		m_mapX = x;
	}
	
	/**
	 * Sets the map y co-ordinate
	 * @param y
	 */
	public void setMapY(int y) {
		m_mapY = y;
	}
	
	/**
	 * Sets if this char is surfing or not and sends the sprite change information to everyone
	 * @param b
	 */
	public void setSurfing(boolean b) {
		m_isSurfing = b;
		if(m_map != null)
			m_map.sendToAll("ms" + m_id + "," + this.getSprite());
	}
	
	/**
	 * Returns true if this char is surfing
	 * @return
	 */
	public boolean isSurfing() {
		return m_isSurfing;
	}
	
	/**
	 * Disposes of this char
	 */
	public void dispose() {
		m_map = null;
	}
}
