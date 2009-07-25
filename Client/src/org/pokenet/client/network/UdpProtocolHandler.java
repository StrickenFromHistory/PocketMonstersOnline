package org.pokenet.client.network;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Player;

/**
 * Protocol handler for UDP
 * @author shadowkanji
 *
 */
public class UdpProtocolHandler extends IoHandlerAdapter {
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
		String message = (String) m;
		Player p = null;
		String [] details = null;
		switch(message.charAt(0)) {
		case 'M':
			details = message.substring(1).split(",");
			p = m_game.getMapMatrix().getPlayer(Integer.parseInt(details[0]));
			p.setServerX(Integer.parseInt(details[1]));
			p.setServerY(Integer.parseInt(details[2]));
			break;
		}
	}
}
