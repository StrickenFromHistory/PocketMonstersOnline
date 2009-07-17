package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * Informs client a battle is over and if they won/lost
 * @author shadowkanji
 *
 */
public class BattleEndMessage extends PokenetMessage {
	public enum BattleEnd { WON, LOST, POKEBALL }
	/**
	 * Constructor
	 * @param won
	 */
	public BattleEndMessage(BattleEnd b) {
		switch(b) {
		case WON:
			m_message = "b@w";
			break;
		case LOST:
			m_message = "b@l";
			break;
		case POKEBALL:
			m_message = "b@p";
			break;
		}
	}
}
