package org.pokenet.server.network.message;

/**
 * NPC chat message
 * @author shadowkanji
 */
public class NpcSpeechMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param m
	 */
	public NpcSpeechMessage(String m) {
		m_message = "Cn" + m;
	}
}
