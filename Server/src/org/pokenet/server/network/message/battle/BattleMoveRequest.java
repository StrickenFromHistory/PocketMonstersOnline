package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * A request to select a battle move
 * @author shadowkanji
 *
 */
public class BattleMoveRequest extends PokenetMessage {
	/**
	 * Constructor
	 */
	public BattleMoveRequest() {
		m_message = "bm";
	}
}
