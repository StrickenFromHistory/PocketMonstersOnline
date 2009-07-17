package org.pokenet.server.network.message;

/**
 * A message to display the sprite selector
 * @author shadowkanji
 *
 */
public class SpriteSelectMessage extends PokenetMessage {
	/**
	 * Constructor
	 */
	public SpriteSelectMessage() {
		m_message = "SS";
	}
}
