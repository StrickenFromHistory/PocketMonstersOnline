package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * Health change message
 * @author shadowkanji
 *
 */
public class HealthChangeMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param id
	 * @param healthChange
	 */
	public HealthChangeMessage(int id, int healthChange) {
		m_message = "bh" + id + "," + healthChange;
	}
}
