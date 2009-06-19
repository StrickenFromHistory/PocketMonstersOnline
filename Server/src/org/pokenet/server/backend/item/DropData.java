package org.pokenet.server.backend.item;

import org.simpleframework.xml.Element;

/**
 * Stores drop data for a Pokemon
 * @author shadowkanji
 *
 */
public class DropData {
	@Element
	private int m_item;
	@Element
	private int m_probability;
	
	/**
	 * Constructor
	 */
	public DropData() {}
	
	/**
	 * Alternative constructor
	 * @param item
	 * @param probability
	 */
	public DropData(int item, int probability) {
		m_item = item;
		m_probability = probability;
	}
	
	/**
	 * Returns the item number
	 * @return
	 */
	public int getItemNumber() {
		return m_item;
	}
	
	/**
	 * Returns the probability that the item is dropped
	 * @return
	 */
	public int getProbability() {
		return m_probability;
	}
	
	/**
	 * Sets the probability of the item being dropped
	 * @param p
	 */
	public void setProbability(int p) {
		m_probability = p;
	}
	
	/**
	 * Sets the item that is dropped
	 * @param i
	 */
	public void setItemNumber(int i) {
		m_item = i;
	}
}
