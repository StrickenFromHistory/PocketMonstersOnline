package org.pokenet.server.network.message.shop;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * A packet for when an item is bought
 * @author shadowkanji
 *
 */
public class ShopBuyMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param itemId
	 */
	public ShopBuyMessage(int itemId) {
		m_message = "Sb" + itemId;
	}
}
