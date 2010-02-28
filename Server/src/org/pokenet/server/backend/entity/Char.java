package org.pokenet.server.backend.entity;

import java.util.LinkedList;
import java.util.Queue;

import org.pokenet.server.backend.map.ServerMap;
import org.pokenet.server.network.message.SpriteChangeMessage;

/**
 * Base class for a character. Note: Originally this implemented Battleable but not all chars are battleable
 * @author shadowkanji
 *
 */
public class Char implements Positionable {
	private Direction m_facing = Direction.Down;
	protected int m_sprite, m_mapX, m_mapY, m_x, m_y, m_id;
	private boolean m_isVisible, m_isSurfing;
	protected String m_name;
	protected ServerMap m_map;
	private boolean m_boostPriority = false;
	/*
	 * Stores a queue of movements for processing
	 */
	protected Queue<Direction> m_movementQueue = new LinkedList<Direction>();
	
	/**
	 * Returns the priority of this player to be move checked
	 * @return
	 */
	public int getPriority() {
		if(m_boostPriority) {
			m_boostPriority = false;
			return m_movementQueue.size() + 100;
		}
		return m_movementQueue.size();
	}
	
	/**
	 * Boost the char's movement priority
	 */
	public void boostPriority() {
		m_boostPriority = true;
	}
	
	/**
	 * Queues a movement to be checked
	 * @param d
	 */
	public void queueMovement(Direction d) {
		m_movementQueue.offer(d);
	}
	
	/**
	 * Returns next movement to be checked
	 * @return
	 */
	public Direction getNextMovement() {
		if(m_movementQueue.size() == 0)
			return null;
		return m_movementQueue.poll();
	}
	
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
	 * Returns the raw sprite (ignoring surf)
	 * @return
	 */
	public int getRawSprite() {
		return m_sprite;
	}

	/**
	 * Returns the sprite of this char. Will return the surf sprite if the char is surfing
	 */
	public int getSprite() {
		return m_isSurfing ? -1 : m_sprite;
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
	public void setMap(ServerMap map, Direction dir) {
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
	 * Processes and checks all movements queued
	 */
	public void move() {
		/* 
		 * Moves player until queue becomes empty, 
		 * collision encountered or pokemon encountered 
		 */
		while(move(getNextMovement())) {}
	}

	/**
	 * Returns true if the char was successfully moved in direction d
	 * @param d - Direction to be moved in
	 */
	public boolean move(Direction d) {
		if(d != null && m_map != null) {
			//Change direction if needs be
			if(m_facing != d) {
				setFacing(d);
				return true;
			}
			//Move the player
			if(m_map.moveChar(this, d)) {
				/*
				 * Update co-ordinates and inform other players of movement
				 */
				switch(d) {
				case Up:
					m_y -= 32;
					m_facing = Direction.Up;
					m_map.sendMovementToAll(d, this);
					break;
				case Down:
					m_y += 32;
					m_facing = Direction.Down;
					m_map.sendMovementToAll(d, this);
					break;
				case Left:
					m_x -= 32;
					m_facing = Direction.Left;
					m_map.sendMovementToAll(d, this);
					break;
				case Right:
					m_x += 32;
					m_facing = Direction.Right;
					m_map.sendMovementToAll(d, this);
					break;
				}
				return true;
			} else {
				//Invalid movement
				if(this instanceof PlayerChar) {
					//If its a player, resync them
					PlayerChar p = (PlayerChar) this;
					p.getTcpSession().write("U" + getX() + "," + getY());
					return false;
				}
			}
		}
		return false;
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
			m_map.sendMovementToAll(d, this);
		}
	}
}
