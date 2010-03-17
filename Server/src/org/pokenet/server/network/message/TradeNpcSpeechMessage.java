package org.pokenet.server.network.message;

/**
 * NPC chat message
 * @author shadowkanji
 */
public class TradeNpcSpeechMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param m
	 */
	public TradeNpcSpeechMessage(String m) {
		m_message = "Ct" + m;
	}
}
