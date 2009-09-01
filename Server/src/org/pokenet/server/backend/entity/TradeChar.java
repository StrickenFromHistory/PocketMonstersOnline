package org.pokenet.server.backend.entity;

/**
 * Represents an NPC that wants to trade Pokemon
 * @author shadowkanji
 *
 */
public class TradeChar extends NonPlayerChar {
	/*
	 * Requested Pokemon data
	 */
	private String m_requestedSpecies = "";
	private int m_requestedLevel = 0;
	private String m_requestedNature = "";
	/*
	 * Offered Pokemon data
	 */
	private String m_offeredSpecies = "";
	private int m_offeredLevel = 0;
	
	/**
	 * Constructor
	 */
	public TradeChar() {
		setBadge(-1);
		setHealer(false);
		setPartySize(0);
	}
	
	/**
	 * Sets the Pokemon the NPC wants
	 * @param species
	 * @param level
	 * @param nature
	 */
	public void setRequestedPokemon(String species, int level, String nature) {
		m_requestedSpecies = species;
		m_requestedLevel = level;
		m_requestedNature = nature;
	}
	
	/**
	 * Sets the Pokemon the NPC offers
	 * @param species
	 * @param level
	 */
	public void setOfferedSpecies(String species, int level) {
		m_offeredSpecies = species;
		m_offeredLevel = level;
	}
	
	@Override
	public void talkToPlayer(PlayerChar p) {
		
	}
}
