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
	private int m_sprite, m_mapX, m_mapY, m_x, m_y, m_id;
	private boolean m_isVisible;
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
	 * Returns the sprite of this char.
	 */
	public int getSprite() {
		return m_sprite;
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
		//TODO: Notify old map that char has left
		m_map = map;
		m_mapX = map.getX();
		m_mapY = map.getY();
		//TODO: Notify new map of new player
		//TODO: Send map information to player
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
		if(m_nextMovement != null) {
			//Move the player
			if(m_facing != m_nextMovement) {
				//TODO: Send change direction packet to everyone on map
				switch(m_nextMovement) {
				case Up:
					m_facing = Direction.Up;
					break;
				case Down:
					m_facing = Direction.Down;
					break;
				case Left:
					m_facing = Direction.Left;
					break;
				case Right:
					m_facing = Direction.Right;
					break;
				}
			} else if(m_map.moveChar(this, m_nextMovement)) {
				switch(m_nextMovement) {
				case Up:
					m_y -= 32;
					m_facing = Direction.Up;
					break;
				case Down:
					m_y += 32;
					m_facing = Direction.Down;
					break;
				case Left:
					m_x -= 32;
					m_facing = Direction.Left;
					break;
				case Right:
					m_x += 32;
					m_facing = Direction.Right;
					break;
				}
				//Check if a wild battle occurred
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
}
