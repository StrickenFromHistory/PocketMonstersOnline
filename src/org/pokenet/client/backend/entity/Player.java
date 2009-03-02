package org.pokenet.client.backend.entity;

import org.newdawn.slick.Image;
import org.pokenet.client.backend.SpriteFactory;

/**
 * Represents a player
 * @author shadowkanji
 *
 */
public class Player {
	private static SpriteFactory m_spriteFactory;
	protected int m_x;
	protected int m_y;
	protected int m_svrX;
	protected int m_svrY;
	protected int m_id;
	protected String m_username;
	protected int m_sprite;
	protected Direction m_direction = Direction.Down;
	protected boolean m_isAnimating = false;
	protected Image m_currentImage;
	
	public enum Direction {Up, Down, Left, Right}
	
	/**
	 * Sets this player's x co-ordinate
	 * @param x
	 */
	public void setX(int x) {
		m_x = x;
	}
	
	/**
	 * Returns this player's x co-ordinate
	 * @return
	 */
	public int getX() {
		return m_x;
	}
	
	/**
	 * Sets this player's x co-ordinate on the server
	 * @param x
	 */
	public void setServerX(int x) {
		m_svrX = x;
	}
	
	/**
	 * Returns this player's x co-ordinate serverside
	 * @return
	 */
	public int getServerX() {
		return m_svrX;
	}
	
	/**
	 * Sets this player's y co-ordinate on the server
	 * @param y
	 */
	public void setServerY(int y) {
		m_svrY = y;
	}
	
	/**
	 * Returns this player's y co-ordinate serverside
	 * @return
	 */
	public int getServerY() {
		return m_svrY;
	}
	
	/**
	 * Sets this player's y co-ordinate
	 * @param y
	 */
	public void setY(int y) {
		m_y = y;
	}
	
	/**
	 * Returns this player's y co-ordinate
	 * @return
	 */
	public int getY() {
		return m_y;
	}
	
	/**
	 * Sets this player's username
	 * @param username
	 */
	public void setUsername(String username) {
		m_username = username;
	}
	
	/**
	 * Returns this player's username
	 * @return
	 */
	public String getUsername() {
		return m_username;
	}
	
	/**
	 * Sets this player's sprite
	 * @param sprite
	 */
	public void setSprite(int sprite) {
		m_sprite = sprite;
	}
	
	/**
	 * Returns this player's sprite
	 * @return
	 */
	public int getSprite() {
		return m_sprite;
	}
	
	/**
	 * Returns the sprite factory
	 * @return
	 */
	public static SpriteFactory getSpriteFactory() {
		return m_spriteFactory;
	}
	
	/**
	 * Returns true if this player is/should be animating
	 * @return
	 */
	public boolean isAnimating() {
		return m_isAnimating;
	}
	
	/**
	 * Sets this player's direction
	 * @param d
	 */
	public void setDirection(Direction d) {
		m_direction = d;
	}
	
	/**
	 * Returns this player's direction
	 * @return
	 */
	public Direction getDirection() {
		return m_direction;
	}
	
	/**
	 * Sets this player's id
	 * @param id
	 */
	public void setId(int id) {
		m_id = id;
	}
	
	/**
	 * Returns this player's id
	 * @return
	 */
	public int getId() {
		return m_id;
	}
	
	/**
	 * Returns the current image of this player's animation
	 * @return
	 */
	public Image getCurrentImage() {
		return m_currentImage;
	}
	
	/**
	 * Sets this player's current image
	 * @param i
	 */
	public void setCurrentImage(Image i) {
		m_currentImage = i;
	}
}
