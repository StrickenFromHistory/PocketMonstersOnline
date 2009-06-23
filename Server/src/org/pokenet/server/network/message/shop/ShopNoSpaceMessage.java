package org.pokenet.server.network.message.shop;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * For when player has no bag space
 * @author shadowkanji
 *
 */
public class ShopNoSpaceMessage extends PokenetMessage {
	/**
	 * Constructor
	 */
	public ShopNoSpaceMessage() {
		m_message = "Sf";
	}
}
