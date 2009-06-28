package org.pokenet.server.backend.entity;

import org.pokenet.server.backend.map.ServerMap;
import org.pokenet.server.network.message.MoveMessage;
import org.pokenet.server.network.message.SpriteChangeMessage;

/**
 * Base class for a character. Note: Originally this implemented Battleable but not all chars are battleable
 * @author shadowkanji
 *
 */
public class Char implements Positionable {
	protected Direction m_nextMovement = null;
	private Direction m_facing = Direction.Down;
	private long m_lastMovement = System.currentTimeMillis();
	protected int m_sprite, m_mapX, m_mapY, m_x, m_y, m_id;
	private boolean m_isVisible, m_isSurfing;
	protected String m_name;
	protected ServerMap m_map;
	
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
		if(m_map != null)
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
		//Inform everyone of sprite change
		if(m_map != null)
			m_map.sendToAll(new SpriteChangeMessage(m_id, this.getSprite()));
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
		return m_name.equalsIgnoreCase("SPRITER") ? "" : m_name;
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
	 * Returns true if the char was successfully moved
	 */
	public boolean move() {
		if(m_nextMovement != null && m_map != null) {
			//Move the player
			if(m_facing != m_nextMovement) {
				switch(m_nextMovement) {
				case Up:
					m_facing = Direction.Up;
					m_map.sendToAll(new MoveMessage(m_nextMovement, m_id, true));
					break;
				case Down:
					m_facing = Direction.Down;
					m_map.sendToAll(new MoveMessage(m_nextMovement, m_id, true));
					break;
				case Left:
					m_facing = Direction.Left;
					m_map.sendToAll(new MoveMessage(m_nextMovement, m_id, true));
					break;
				case Right:
					m_facing = Direction.Right;
					m_map.sendToAll(new MoveMessage(m_nextMovement, m_id, true));
					break;
				}
				m_nextMovement = null;
				m_lastMovement = System.currentTimeMillis();
				return true;
			} else if(m_map.moveChar(this, m_nextMovement)) {
				switch(m_nextMovement) {
				case Up:
					m_y -= 32;
					m_facing = Direction.Up;
					m_map.sendToAll(new MoveMessage(m_nextMovement, m_id, false));
					break;
				case Down:
					m_y += 32;
					m_facing = Direction.Down;
					m_map.sendToAll(new MoveMessage(m_nextMovement, m_id, false));
					break;
				case Left:
					m_x -= 32;
					m_facing = Direction.Left;
					m_map.sendToAll(new MoveMessage(m_nextMovement, m_id, false));
					break;
				case Right:
					m_x += 32;
					m_facing = Direction.Right;
					m_map.sendToAll(new MoveMessage(m_nextMovement, m_id, false));
					break;
				}
				m_nextMovement = null;
				m_lastMovement = System.currentTimeMillis();
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the char's next movement
	 */
	public void setNextMovement(Direction dir) {
		if(System.currentTimeMillis() - m_lastMovement > 50) {
			if(getFacing() == dir) {
				/* If we're facing the direction, queue the movement */
				m_nextMovement = dir;
			} else {
				/* Else, turn */
				m_nextMovement = null;
				setFacing(dir);
				m_lastMovement = System.currentTimeMillis();
			}
		}
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
			m_map.sendToAll(new SpriteChangeMessage(m_id, this.getSprite()));
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
	
	/**
	 * Changes the direction of the npc
	 * @param d
	 */
	public void setFacing(Direction d) {
		m_facing = d;
		if(m_map != null) {
			m_map.sendToAll(new MoveMessage(d, m_id, true));
		}
	}
}
