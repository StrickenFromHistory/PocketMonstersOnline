package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * A message for when a Pokemon is using a move
 * @author shadowkanji
 *
 */
public class BattleMoveMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param speciesName
	 * @param moveName
	 */
	public BattleMoveMessage(String speciesName, String moveName) {
		m_message = "bM" + speciesName + "," + moveName;
	}
}
