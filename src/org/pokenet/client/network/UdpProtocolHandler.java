package org.pokenet.client.network;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.pokenet.client.GameClient;

/**
 * Protocol handler for UDP
 * @author shadowkanji
 *
 */
public class UdpProtocolHandler extends IoHandlerAdapter {
	@SuppressWarnings("unused")
	private GameClient m_game;

	/**
	 * Constructor
	 * @param gameClient
	 */
	public UdpProtocolHandler(GameClient gc) {
		m_game = gc;
	}

	/**
	 * Called when a message is received
	 */
	public void messageReceived(IoSession session, Object m) {

	}
}
