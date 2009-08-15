package org.pokenet.server.feature;

import org.simpleframework.xml.Element;

/**
 * Stores exp and level information for a Fish Pokemon(one found by fishing)
 * @author Fshy
 *
 */
public class Fish {

	@Element
	private int m_experience;
	@Element
	private int m_levelReq;
	@Element
	private int m_rodReq;
	/**
	 * Constructor
	 */
	public Fish() {}
	
	/**
	 * Alternative constructor
	 * @param levelReq
	 * @param experience
	 */
	public Fish(int levelReq, int experience, int rodReq) {
		m_experience = experience;
		m_levelReq = levelReq;	
		m_rodReq = rodReq;
	}
	/**
	 * Returns the rod required to fish up this pogey
	 * 0 is old rod, 15 is good rod, 35 is great rod, 50 is ultra rod.
	 * @return
	 */
	public int getReqRod() {
		return m_rodReq;
	}
	
	/**
	 * Returns the level required to fish up this pogey
	 * @return
	 */
	public int getReqLevel() {
		return m_levelReq;
	}
	
	/**
	 * Returns the experience for fishing this pogey
	 * @return
	 */
	public int getExperience() {
		return m_experience;
	}
	
	/**
	 * Sets the experience gained from fishing this pogey
	 * @param p
	 */
	public void setExperience(int p) {
		m_experience = p;
	}
	
	/**
	 * Sets the level required to encounter/fish this pogey
	 * @param i
	 */
	public void setLevelReq(int s) {
		m_levelReq = s;
	}
}

