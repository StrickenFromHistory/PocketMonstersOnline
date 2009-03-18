package org.pokenet.client.backend.entity;

/**
 * Represents our player
 * @author shadowkanji
 *
 */
public class OurPlayer extends Player {
	private OurPokemon [] m_pokemon;
	private Item [] m_items;
    private String[] m_badges = new String[0];
	private int m_money;
	
	/**
	 * Default constructor
	 */
	public OurPlayer() {
		m_pokemon = new OurPokemon[6];
		m_items = new Item[6];
		m_badges = new String[0];
		m_money = 0;
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
	
	public void set(Player p) {
		m_x = p.getX();
		m_y = p.getY();
		m_svrX = p.getServerX();
		m_svrY = p.getServerY();
		m_sprite = p.getSprite();
		m_direction = p.getDirection();
		m_username = p.getUsername();
		m_id = p.getId();
		m_ours = p.isOurPlayer();
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
	
	/**
	 * Returns the player's money
	 * @return
	 */
	public int getMoney(){
		return m_money;
	}
	
	/**
	 * Returns the player's pokemon team
	 */
	public OurPokemon [] getPokes(){
		return m_pokemon;
	}
	
	/**
	 * Returns the player's badges
	 */
	public String [] getBadges(){
		return m_badges;
	}
}
