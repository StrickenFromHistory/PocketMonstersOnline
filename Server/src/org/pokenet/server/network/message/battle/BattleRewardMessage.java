package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * A reward from a battle
 * @author shadowkanji
 *
 */
public class BattleRewardMessage extends PokenetMessage {
	public enum BattleRewardType { MONEY, ITEM };
	
	/**
	 * Constructor
	 * @param b
	 * @param i
	 */
	public BattleRewardMessage(BattleRewardType b, int i) {
		switch(b) {
		case MONEY:
			m_message = "b$" + i;
			break;
		case ITEM:
			m_message = "bI" + i;
			break;
		}
	}
}
