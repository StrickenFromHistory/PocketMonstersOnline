package org.pokenet.server.trade;

import java.util.ArrayList;
import org.pokenet.server.backend.entity.PlayerChar;

/**
 * Handles a trade between two players
 * @author felty.wos
 */
public abstract class Trade {
	private ArrayList<PlayerChar> m_players = new ArrayList<PlayerChar>();
	private ArrayList<TradeOffer> m_offers = new ArrayList<TradeOffer>();
	private boolean[] m_confirmed = {false, false, false};
	private boolean[] m_offered = {false, false, false};
	private boolean m_isOver = false;
	
	/**
	 * Adds a trainer to the trade
	 * @param p
	 */
	public void addTrainer(PlayerChar p) {
		if (m_players.size() < 2) {
			m_players.add(p);
			m_offers.add(new TradeOffer());
		}
	}
	
	/**
	 * Adds an object this player wants to offer
	 * @param p
	 * @param t
	 */
	public void addOffer(PlayerChar p, Tradeable t) {
		if (!m_offered[2]) // Don't want trainers breaking promises
			m_offers.get(m_players.indexOf(p)).add(t);
	}
	
	/**
	 * Removes an object this player previously offered
	 * @param p
	 * @param t
	 */
	public void removeOffer(PlayerChar p, Tradeable t) {
		if (!m_offered[2]) // Don't want trainers breaking promises
			m_offers.get(m_players.indexOf(p)).remove(t);
	}
	
	/**
	 * Clears everything this player offered
	 * @param p
	 * @param t
	 */
	public void clearOffers(PlayerChar p) {
		if (!m_offered[2]) // Don't want trainers breaking promises
			m_offers.get(m_players.indexOf(p)).clear();
	}
	
	/**
	 * Returns true if p is trading
	 * @param p
	 */
	public boolean isParticipating(PlayerChar p) {
		return m_players.contains(p);
	}
	
	/**
	 * Return true if there are enough players ready to trade
	 * @return
	 */
	public boolean isReady() {
		return (m_players.size() == 2);
	}
	
	/**
	 * Returns true if both players have offered their selections
	 * @return
	 */
	public boolean isOffered() {
		return m_offered[0] && m_offered[1] && !m_offered[2];
	}
	
	/**
	 * Returns true if both players have confirmed the trade
	 * @return
	 */
	public boolean isConfirmed() {
		return m_confirmed[0] && m_confirmed[1] && !m_offered[2];
	}
	
	/**
	 * Returns true when the trade is over... can then be disposed of
	 * @return
	 */
	public boolean isEnded() {
		return m_isOver;
	}
	
	/**
	 * A player confirmed that they are okay with the trade
	 * @param p
	 */
	public void confirm(PlayerChar p) {
		m_confirmed[m_players.indexOf(p)] = true;
	}
	
	/**
	 * A player has said their offer is ready to send to the other player
	 * @param p
	 */
	public void offer(PlayerChar p) {
		m_offered[m_players.indexOf(p)] = true;
	}
	
	/**
	 * One of the players declined, trade is off
	 */
	public void decline() {
		// send packets telling both players that the trade is off
		m_isOver = true;
	}
	
	/**
	 * Start the trade between the two players
	 */
	public void startTrade() {
		// set trading status of both players to true
		// send packet to players telling them of start of trade
	}
	
	/**
	 * Sends the offer to the other player
	 */
	public void sendOffers() {
		m_offered[2] = true; // Only need to send the offers once
		for (Tradeable i : m_offers.get(0)) {
			// generate packet to send to other player
		}
		for (Tradeable i : m_offers.get(1)) {
			// generate packet to send to other player
		}
		// send packets
	}
	
	/**
	 * Exchanges the offered items
	 */
	public void executeTrade() {
		m_confirmed[2] = true; // Only need to execute the trade once
		for (Tradeable i : m_offers.get(0)) {
			// exchange the stuff here
		}
		for (Tradeable i : m_offers.get(1)) {
			// exchange the stuff here
		}
		m_isOver = true;
	}
	
	/**
	 * Destroy links to other objects
	 */
	public void dispose() {
		m_players = null;
		m_offers = null;
	}
}
