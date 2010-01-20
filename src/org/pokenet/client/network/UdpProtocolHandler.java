package org.pokenet.client.network;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Player;
import org.pokenet.client.backend.entity.Player.Direction;

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
			//Player movements
			//Mdirpid,dirpid
			details = message.substring(1).split(",");
			for(int i = 0; i < details.length; i++) {
				processMovement(Integer.parseInt(details[i].substring(1)), 
						details[i].charAt(0));
			}
			break;
		}
	}
	
	/**
	 * Processes movement changes
	 * @param player
	 * @param direction
	 */
	private void processMovement(int player, char direction) {
		Player p = m_game.getMapMatrix().getPlayer(player);
		switch(direction) {
		case 'D':
			p.queueMovement(Direction.Down);
			break;
		case 'U':
			p.queueMovement(Direction.Up);
			break;
		case 'L':
			p.queueMovement(Direction.Left);
			break;
		case 'R':
			p.queueMovement(Direction.Right);
			break;
		}
	}
}
