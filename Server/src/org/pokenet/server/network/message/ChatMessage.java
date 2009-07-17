package org.pokenet.server.network.message;

/**
 * A chat message
 * @author shadowkanji
 *
 */
public class ChatMessage extends PokenetMessage {
	public enum ChatMessageType { LOCAL, PRIVATE, NPC }
	
	/**
	 * Constructor
	 * @param c
	 * @param message
	 */
	public ChatMessage(ChatMessageType c, String message) {
		switch(c) {
		case LOCAL:
			m_message = "Cl" + message;
			break;
		case PRIVATE:
			m_message = "Cp" + message;
			break;
		case NPC:
			m_message = "Cn" + message;
			break;
		}
	}
}
