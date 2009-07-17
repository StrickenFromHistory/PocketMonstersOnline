package org.pokenet.server.network.message.shop;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * The player doesn't have the item he's trying to sell
 * @author ZombieBear
 *
 */
public class ShopNoItemMessage extends PokenetMessage{
	/**
	 * Constructor
	 */
	public ShopNoItemMessage(String item) {
		m_message = "Sd" + item;
	}
}
