package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * Level up during battle
 * @author shadowkanji
 *
 */
public class BattleLevelChangeMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param pokeName
	 * @param level
	 */
	public BattleLevelChangeMessage(String pokeName, int level) {
		m_message = "bl" + pokeName + "," + level;
	}
}
