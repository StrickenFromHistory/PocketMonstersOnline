package org.pokenet.server.network.message.shop;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * For when the client has no money
 * @author shadowkanji
 *
 */
public class ShopNoMoneyMessage extends PokenetMessage {
	/**
	 * Constructor
	 */
	public ShopNoMoneyMessage() {
		m_message = "Sn";
	}
}
