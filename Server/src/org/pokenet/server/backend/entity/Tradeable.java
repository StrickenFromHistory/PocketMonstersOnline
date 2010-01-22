package org.pokenet.server.backend.entity;

import org.pokenet.server.battle.Pokemon;

/**
 * An interface for objects that can be traded with
 * @author shadowkanji
 *
 */
public interface Tradeable {
	/**
	 * Returns IP address of char
	 * @return
	 */
	public String getIpAddress();
	/**
	 * Gets the name of the tradeable char
	 * @return
	 */
	public String getName();
	/**
	 * Returns the money for this char
	 * @return
	 */
	public int getMoney();
	/**
	 * Sets the money for this char
	 * @param money
	 */
	public void setMoney(int money);
	/**
	 * Gets the party of the char
	 * @return
	 */
	public Pokemon [] getParty();
	/**
	 * Sets if the char accepted the other char's offer
	 * @param b
	 */
	public void setTradeAccepted(boolean b);
	/**
	 * Cancels the trade
	 */
	public void cancelTrade();
	/**
	 * Ends trading for this char
	 * NOTE: Called by cancelTrade and when the trade is completed
	 */
	public void finishTrading();
	/**
	 * Returns true if this char accepted the offer from the other char
	 * @return
	 */
	public boolean acceptedTradeOffer();
	/**
	 * Cancels this char's offer
	 */
	public void cancelTradeOffer();
	/**
	 * Handles receiving a trade offer
	 * @param o
	 */
	public void receiveTradeOffer(TradeOffer [] o);
	/**
	 * Handles the other player cancelling
	 */
	public void receiveTradeOfferCancelation();
	/**
	 * Sets the trade for this char
	 * @param t
	 */
	public void setTrade(Trade t);
	/**
	 * Returns the trade for this char
	 * @return
	 */
	public Trade getTrade();
	/**
	 * Returns true if this char is trading
	 * @return
	 */
	public boolean isTrading();
}