package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * A message for when a pokemon faints
 * @author shadowkanji
 *
 */
public class FaintMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param pokeName
	 */
	public FaintMessage(String pokeName) {
		m_message = "bF" + pokeName;
	}

}
