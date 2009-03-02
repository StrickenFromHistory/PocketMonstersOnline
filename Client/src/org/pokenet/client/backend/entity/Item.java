package org.pokenet.client.backend.entity;

/**
 * Represents an item
 * @author shadowkanji
 *
 */
public class Item {
	private int m_number;
	private int m_quantity;
	private String m_name;
	
	/**
	 * Default constructor
	 * @param number
	 * @param quantity
	 */
	public Item(int number, int quantity) {
		m_number = number;
		m_quantity = quantity;
		m_name = Item.getItemName(number);
	}
	
	/**
	 * Returns the name of the item based on its item number
	 * @param number
	 * @return
	 */
	public static String getItemName(int number) {
		return "";
	}
	
	/**
	 * Returns this item's name
	 * @return
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Returns this item's number
	 * @return
	 */
	public int getNumber() {
		return m_number;
	}
	
	/**
	 * Returns this item's quantity
	 * @return
	 */
	public int getQuantity() {
		return m_quantity;
	}
	
	/**
	 * Sets this item's quantity
	 * @param q
	 */
	public void setQuantity(int q) {
		m_quantity = q;
	}
}
