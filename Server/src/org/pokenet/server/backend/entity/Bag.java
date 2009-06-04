package org.pokenet.server.backend.entity;

/**
 * Represents a player's bag
 * @author shadowkanji
 *
 */
public class Bag {
	private BagItem[] m_items;
	private int m_memberId;
	
	/**
	 * Default constructor
	 */
	public Bag() {
		m_items = new BagItem[20];
	}
	
	/**
	 * Returns true if there is space in the bag for that item
	 * @param id
	 * @return
	 */
	public boolean hasSpace(int id) {
		for(int i = 0; i < m_items.length; i++) {
			if(m_items[i].getItemNumber() == id)
				return true;
			if(m_items[i] == null)
				return true;
		}
		return false;
	}
	
	/**
	 * Adds an item to the bag. Returns true on success
	 * @param itemNumber
	 * @param quantity
	 */
	public boolean addItem(int itemNumber, int quantity) {
		for(int i = 0; i < m_items.length; i++) {
			if(m_items[i].getItemNumber() == itemNumber) {
				m_items[i].setQuantity(m_items[i].getQuantity() + quantity);
				return true;
			} else if(m_items[i] == null) {
				m_items[i] = new BagItem(itemNumber, quantity);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes an item. Returns true on success.
	 * @param itemNumber
	 * @param quantity
	 * @return
	 */
	public boolean removeItem(int itemNumber, int quantity) {
		for(int i = 0; i < m_items.length; i++) {
			if(m_items[i].getItemNumber() == itemNumber) {
				if(m_items[i].getQuantity() - quantity > 0)
					m_items[i].setQuantity(m_items[i].getQuantity() - quantity);
				else
					m_items[i] = null;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns all the items in the bag
	 */
	public BagItem[] getItems() {
		return m_items;
	}
	
	/**
	 * Sets the member id of this bag
	 * @param id
	 */
	public void setMemberId(int id) {
		m_memberId = id;
	}
	
	/**
	 * Returns this bag's member id
	 * @return
	 */
	public int getMemberId() {
		return m_memberId;
	}
}
