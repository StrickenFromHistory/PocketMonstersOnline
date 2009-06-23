package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * Informs the client a battle has started
 * @author shadowkanji
 *
 */
public class BattleInitMessage extends PokenetMessage{
	/**
	 * Constructor
	 * @param isWildBattle
	 * @param enemyPartySize
	 */
	public BattleInitMessage(boolean isWildBattle, int enemyPartySize) {
		m_message = "bi" + (isWildBattle ? "1" : "0") + enemyPartySize;
	}
}
