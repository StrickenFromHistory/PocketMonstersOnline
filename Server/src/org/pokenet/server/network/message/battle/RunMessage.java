package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * A message when running from a battle
 * @author shadowkanji
 *
 */
public class RunMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param success
	 */
	public RunMessage(boolean success) {
		m_message = "br" + (success ? "1" : "2");
	}
}
