package org.pokenet.server.backend.entity;

import org.pokenet.server.backend.ServerMap;

/**
 * Base class for a character. Note: Originally this implemented Battleable but not all chars are battleable
 * @author shadowkanji
 *
 */
public class Char implements Positionable {
	private Direction m_nextMovement = null;
	private long m_lastMovement = System.currentTimeMillis();
	private int m_sprite, m_mapX, m_mapY, m_x, m_y;
	private boolean m_isVisible;
	
	/**
	 * Returns the direction this char is facing
	 */
	public Direction getFacing() {
		return null;
	}

	/**
	 * Returms the map this char is on
	 */
	public ServerMap getMap() {
		/*
		 * Maybe it'll be better to return the map via MapMatrix or whatever rather than storing a reference in the char.
		 */
		return null;
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
	 * Set the mapX and mapY based on a servermap
	 */
	public void setMap(ServerMap map) {
	}

	/**
	 * Set the sprite of this char
	 */
	public void setSprite(int sprite) {
	}

	/**
	 * Set if this char is visible
	 */
	public void setVisible(boolean visible) {
	}

	/**
	 * Returns the name of the char
	 */
	public String getName() {
		return null;
	}

	/**
	 * Returns if the player requested a movement
	 */
	public boolean isMovementRequested() {
		return m_nextMovement != null;
	}

	/**
	 * Moves the player if m_nextMovement != null
	 */
	public void move() {
		assert m_nextMovement != null;
		
		//Move the player
		m_lastMovement = System.currentTimeMillis();
	}

	/**
	 * Sets the player's next movement
	 */
	public void setNextMovement(Direction dir) {
		if(System.currentTimeMillis() - m_lastMovement > 100)
			m_nextMovement = dir;
	}

}
