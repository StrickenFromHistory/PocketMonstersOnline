package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * A request to switch Pokemon
 * @author shadowkanji
 *
 */
public class SwitchRequest extends PokenetMessage {
	/**
	 * Constructor
	 */
	public SwitchRequest() {
		m_message = "bs";
	}
}
