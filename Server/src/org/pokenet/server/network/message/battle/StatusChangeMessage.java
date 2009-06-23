package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * Status change message (e.g. poison, sleep, etc.)
 * @author shadowkanji
 *
 */
public class StatusChangeMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param side
	 * @param pokeName
	 * @param effect
	 * @param removed
	 */
	public StatusChangeMessage(int side, String pokeName, String effect, boolean removed) {
		if(removed) {
			m_message = "bE" + side + "" + pokeName + "," + effect;
		} else {
			m_message = "be" + side + "" + pokeName + "," + effect;
		}
	}
}
