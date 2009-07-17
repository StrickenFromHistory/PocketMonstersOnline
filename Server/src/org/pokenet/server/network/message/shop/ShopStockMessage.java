package org.pokenet.server.network.message.shop;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * A message containing a shop's stock
 * @author shadowkanji
 *
 */
public class ShopStockMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param stock
	 */
	public ShopStockMessage(String stock) {
		m_message = "Sl" + stock;
	}
}
