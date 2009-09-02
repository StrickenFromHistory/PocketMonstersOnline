package org.pokenet.server.backend.entity;

/**
 * Data of something being traded
 * @author shadowkanji
 *
 */
public class TradeOffer {
	public enum TradeType { POKEMON, ITEM, MONEY }
	private int m_id;
	private int m_quantity;
	/* Stores Pokemon or Item name */
	private String m_information;
	private TradeType m_type;
	
	/**
	 * Returns information about this trade
	 * @return
	 */
	public String getInformation() {
		return m_information;
	}
	
	/**
	 * Sets information for this offer
	 * @param i
	 */
	public void setInformation(String i) {
		m_information = i;
	}
	
	/**
	 * Sets the quantity
	 * @param q
	 */
	public void setQuantity(int q) {
		m_quantity = q;
	}
	
	/**
	 * Sets the id of the object
	 * @param id
	 */
	public void setId(int id) {
		m_id = id;
	}
	
	/**
	 * Sets the trade type
	 * @param t
	 */
	public void setType(TradeType t) {
		m_type = t;
	}
	
	/**
	 * Returns the quantity
	 * @return
	 */
	public int getQuantity() {
		return m_quantity;
	}
	
	/**
	 * Returns the id
	 * @return
	 */
	public int getId() {
		return m_id;
	}
	
	/**
	 * Returns the type
	 * @return
	 */
	public TradeType getType() {
		return m_type;
	}
}
