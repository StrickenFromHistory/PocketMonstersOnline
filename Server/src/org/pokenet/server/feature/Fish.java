package org.pokenet.server.feature;

import org.simpleframework.xml.Element;

/**
 * Stores exp information for a Fish Pokemon
 * @author Fshy
 *
 */
public class Fish {

	@Element
	private int m_experience;
	@Element
	private int m_levelReq;
	
	/**
	 * Constructor
	 */
	public Fish() {}
	
	/**
	 * Alternative constructor
	 * @param item
	 * @param probability
	 */
	public Fish(int levelReq, int experience) {
		m_experience = experience;
		m_levelReq = levelReq;		
	}
	
	/**
	 * Returns the item number
	 * @return
	 */
	public int getReqLevel() {
		return m_levelReq;
	}
	
	/**
	 * Returns the probability that the item is dropped
	 * @return
	 */
	public int getExperience() {
		return m_experience;
	}
	
	/**
	 * Sets the probability of the item being dropped
	 * @param p
	 */
	public void setExperience(int p) {
		m_experience = p;
	}
	
	/**
	 * Sets the item that is dropped
	 * @param i
	 */
	public void setLevelReq(int s) {
		m_levelReq = s;
	}
}

