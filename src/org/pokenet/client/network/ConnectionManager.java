package org.pokenet.client.network;

import javax.swing.JOptionPane;

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
		m_game.reset();
		JOptionPane.showMessageDialog(null, "You have been disconnected\n" +
				"from the game server.");
	}
	
	public void sessionOpened(IoSession session) {
		System.out.println("Connected to game server.");
	}

	/**
	 * Once a message is received, this method is called
	 */
	public void messageReceived(IoSession session, Object m) {
		String message = (String) m;
		System.out.println("INFO: " + message);
		String [] details;
		switch(message.charAt(0)) {
		case 'm':
			//Map Information
			switch(message.charAt(1)) {
			case 'i':
				//Initialise players
				break;
			case 'a':
				//Add player
				break;
			case 'r':
				//Remove player
				break;
			case 's':
				//Set the map
				details = message.substring(2).split(",");
				m_game.setMap(Integer.parseInt(details[0]), Integer.parseInt(details[1]));
				break;
			}
			break;
		case 'l':
			//Login Information
			switch(message.charAt(1)) {
			case 's':
				//Sucessful login
				details = message.substring(2).split(",");
				m_game.getLoginScreen().setVisible(false);
				m_game.getLoadingScreen().setVisible(false);
				m_game.setPlayerId(Integer.parseInt(details[0]));
				break;
			case 'e':
				//Error
				JOptionPane.showMessageDialog(null, "An error occurred.\n " +
				"Make sure the username and password are correct.");
				m_game.getLoadingScreen().setVisible(false);
				break;
			}
			break;
		case 'r':
			switch(message.charAt(1)) {
			case 's':
				//Sucessful registration
				JOptionPane.showMessageDialog(null, "Successful registration. You may now login on any server.");
				m_game.getLoginScreen().showLogin();
				break;
			case 'e':
				//Error
				JOptionPane.showMessageDialog(null, "An error occurred.\n " +
						"Either the username already exists or the account server is offline.");
				break;
			}
			break;
		}
	}
}
