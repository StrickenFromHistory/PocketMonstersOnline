package org.pokenet.client.network;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.pokenet.client.GameClient;

/**
 * Handles packets received from the server
 * @author shadowkanji
 *
 */
public class ConnectionManager extends IoHandlerAdapter {
	private GameClient m_game;

	/**
	 * Default constructor
	 * @param gameClient
	 */
	public ConnectionManager(GameClient game) {
		m_game = game;
	}
	
	/**
	 * Called when we lose or close the connection
	 */
	public void sessionClosed(IoSession session) {
		
	}
	
	public void sessionOpened(IoSession session) {
		System.out.println("Connected to game server.");
	}

	/**
	 * Once a message is received, this method is called
	 */
	public void messageReceived(IoSession session, Object m) {
		String message = (String) m;
		String [] details;
		switch(message.charAt(0)) {
		case 'l':
			//Login Information
			switch(message.charAt(1)) {
			case 's':
				//Sucessful login
				details = message.substring(2).split(",");
				m_game.getLoginScreen().setVisible(true);
				m_game.getLoadingScreen().setVisible(true);
				break;
			}
			break;
		case 'r':
			break;
		}
	}
}
