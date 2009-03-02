package org.pokenet.client.backend.entity;

/**
 * Represents our player
 * @author shadowkanji
 *
 */
public class OurPlayer extends Player {
	private OurPokemon [] m_pokemon;
	private Item [] m_items;
	
	/**
	 * Default constructor
	 */
	public OurPlayer() {
		m_pokemon = new OurPokemon[6];
		m_items = new Item[6];
	}
	
	/**
	 * Constructor to be used if our player already exists
	 * @param original
	 */
	public OurPlayer(OurPlayer original) {
		m_pokemon = original.getPokemon();
		m_items = original.getItems();
		m_sprite = original.getSprite();
		m_username = original.getUsername();
		m_isAnimating = original.isAnimating();
	}
	
	/**
	 * Returns our player's party
	 * @return
	 */
	public OurPokemon[] getPokemon() {
		return m_pokemon;
	}
	
	/**
	 * Returns our player's bag
	 * @return
	 */
	public Item[] getItems() {
		return m_items;
	}
	
	/**
	 * Adds an item to this player's bag (automatically handles if its in the bag already)
	 * @param number
	 * @param quantity
	 */
	public void addItem(int number, int quantity) {
		for(int i = 0; i < m_items.length; i++) {
			if(m_items[i] != null && m_items[i].getNumber() == number) {
				m_items[i].setQuantity(m_items[i].getQuantity() + quantity);
				return;
			} else if(m_items[i] == null) {
				m_items[i] = new Item(number, quantity);
				return;
			}
		}
	}
	
	/**
	 * Removes an item from this player's bag
	 * @param number
	 * @param quantity
	 */
	public void removeItem(int number, int quantity) {
		for(int i = 0; i < m_items.length; i++) {
			if(m_items[i] != null && m_items[i].getNumber() == number) {
				if(m_items[i].getQuantity() - quantity > 0)
					m_items[i].setQuantity(m_items[i].getQuantity() - quantity);
				else
					m_items[i] = null;
				return;
			}
		}
	}
	
	/**
	 * Sets a pokemon in this player's party
	 * @param i
	 * @param information
	 */
	public void setPokemon(int i, String [] information) {
		
	}
}
