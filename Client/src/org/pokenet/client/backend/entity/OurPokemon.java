package org.pokenet.client.backend.entity;

import org.newdawn.slick.Image;

/**
 * Represents one of our pokemon
 * @author shadowkanji
 *
 */
public class OurPokemon extends Pokemon {
	private Image m_backSprite;
	private String m_nickname = "";
	
	/**
	 * Default constructor
	 * @param speciesNumber
	 */
	public OurPokemon(int speciesNumber) {
		super(speciesNumber);
		
	}
	
	/**
	 * Returns this Pokemon's back sprite
	 * @return
	 */
	public Image getBackSprite() {
		return m_backSprite;
	}
	
	/**
	 * Loads this Pokemon's back sprite
	 */
	public void setBackSprite() {
		
	}
}
