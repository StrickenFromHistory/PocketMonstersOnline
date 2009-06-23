package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * A battle message which doesn't fit into other categories
 * @author shadowkanji
 *
 */
public class BattleMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param message
	 */
	public BattleMessage(String message) {
		m_message = "b!" + message;
	}
}
