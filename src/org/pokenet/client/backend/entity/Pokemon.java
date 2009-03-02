package org.pokenet.client.backend.entity;

import org.newdawn.slick.Image;

/**
 * Represents a pokemon
 * @author shadowkanji
 *
 */
public class Pokemon {
	private int m_speciesNumber;
	private int m_hp;
	private Image m_frontSprite;
	private Image m_icon;
	
	/**
	 * Default constructor
	 * @param speciesNumber
	 */
	public Pokemon(int speciesNumber) {
		m_speciesNumber = speciesNumber;
	}
	
	/**
	 * Returns this pokemon's front sprite
	 * @return
	 */
	public Image getFrontSprite() {
		return m_frontSprite;
	}
	
	/**
	 * Loads this pokemon's front sprite
	 */
	public void setFrontSprite() {
		
	}
	
	/**
	 * Returns this Pokemon's icon
	 * @return
	 */
	public Image getIcon() {
		return m_icon;
	}
	
	/**
	 * Sets this Pokemon's icon
	 */
	public void setIcon() {
		
	}
}
