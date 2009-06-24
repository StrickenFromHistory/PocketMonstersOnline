package org.pokenet.server.network.message.shop;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * A packet for when an item is sold
 * @author ZombieBear
 *
 */
public class ShopSellMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param itemId
	 */
	public ShopSellMessage(int itemId) {
		m_message = "Ss" + itemId;
	}
}

