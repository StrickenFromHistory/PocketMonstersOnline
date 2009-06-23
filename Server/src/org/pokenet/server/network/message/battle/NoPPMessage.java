package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * Informs client no pp is left
 * @author shadowkanji
 *
 */
public class NoPPMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param moveName
	 */
	public NoPPMessage(String moveName) {
		m_message = "bp" + moveName;
	}
}
