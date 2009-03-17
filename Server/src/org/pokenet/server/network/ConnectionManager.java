package org.pokenet.server.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.pokenet.server.GameServer;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.backend.entity.Positionable.Direction;

/**
 * Handles packets recieved from the player
 * @author shadowkanji
 *
 */
public class ConnectionManager extends IoHandlerAdapter {
	private static HashMap<String, PlayerChar> m_players;
	private LoginManager m_loginManager;
	private LogoutManager m_logoutManager;
	private RegistrationManager m_regManager;
	
	/**
	 * Constructor
	 * @param login
	 * @param logout
	 */
	public ConnectionManager(LoginManager login, LogoutManager logout) {
		m_loginManager = login;
		m_logoutManager = logout;
		m_regManager = new RegistrationManager();
		m_regManager.start();
	}
	
	static {
		m_players = new HashMap<String, PlayerChar>();
	}
	
	/**
	 * Handles any exceptions involving a player's session
	 */
	public void exceptionCaught(IoSession session, Throwable t)
	throws Exception {
		/*
		 * Attempt to disconnect and logout the player (save their data)
		 */
		try {
			PlayerChar p = (PlayerChar) session.getAttribute("player");
			//TODO: If player is battling, end the battle with them losing 
			GameServer.getServiceManager().getNetworkService().getLogoutManager().queuePlayer(p);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	    * Once the server receives a packet from the client, this method is run.
	    * @param IoSession session - A client session
	    * @param Object msg - The packet received from the client
		*/
	public void messageReceived(IoSession session, Object msg) throws Exception {
		String [] message;
		System.out.println(((String) msg));
		if(session.getAttribute("player") == null) {
			/*
			 * The player hasn't been logged in, only allow login and registration packets
			 */
			switch(((String) msg).charAt(0)) {
			case 'l':
				//Login packet
				message = ((String) msg).substring(1).split(",");
				m_loginManager.queuePlayer(session, message[0], message[1]);
				break;
			case 'r':
				//Registration packet
				m_regManager.queueRegistration(session, ((String) msg).substring(1));
				break;
			}
		} else {
			/*
			 * Player is logged in, allow interaction with their player object
			 */
			PlayerChar p = (PlayerChar) session.getAttribute("player");
			switch(((String) msg).charAt(0)) {
			case 'U':
				//Move up
				p.setNextMovement(Direction.Up);
				break;
			case 'D':
				//Move down
				p.setNextMovement(Direction.Down);
				break;
			case 'L':
				//Move left
				p.setNextMovement(Direction.Left);
				break;
			case 'R':
				//Move right
				p.setNextMovement(Direction.Right);
				break;
			case 'F':
				//Friend list
				switch(((String) msg).charAt(1)) {
				case 'a':
					//Add a friend
					break;
				case 'r':
					//Remove a friend
					break;
				}
			case 'C':
				//Chat/Interact
				switch(((String) msg).charAt(1)) {
				case 'l':
					//Local chat
					GameServer.getServiceManager().getNetworkService().getChatManager().
						queueLocalChatMessage("<" + p.getName() + "> " + ((String) msg).substring(2), p.getMapX(), p.getMapY());
					break;
				case 'p':
					//Private chat
					message = ((String) msg).substring(2).split(",");
					GameServer.getServiceManager().getNetworkService().getChatManager().
						queuePrivateMessage(message[1], m_players.get(message[0]).getSession(), p.getName());
					break;
				case 't':
					//Talk
					break;
				}
				break;
			}
		}
	}
	
	/**
	 * When a user disconnects voluntarily, this method is called
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		/*
		 * Attempt to save the player's data
		 */
		try {
			PlayerChar p = (PlayerChar) session.getAttribute("player");
			//TODO: If player is battling, end the battle with them losing 
			if(p != null) {
				GameServer.getServiceManager().getNetworkService().getLogoutManager().queuePlayer(p);
				GameServer.getServiceManager().getMovementService().removePlayer(p.getName());
				m_players.remove(p);
				session.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Logs out all players and stops login/logout/registration managers
	 */
	public void logoutAll() {
		m_regManager.stop();
		m_loginManager.stop();
		/*
		 * Queue all players to be saved
		 */
		Iterator<PlayerChar> it = m_players.values().iterator();
		PlayerChar p;
		while(it.hasNext()) {
			p = it.next();
			m_logoutManager.queuePlayer(p);
		}
		/*
		 * Since the method is called during a server shutdown, wait for all players to be logged out
		 */
		while(m_logoutManager.getPlayerAmount() > 0);
		m_logoutManager.stop();
	}
	
	/**
	 * Returns the list of players
	 * @return
	 */
	public static HashMap<String, PlayerChar> getPlayers() {
		return m_players;
	}
	
	/**
	 * Returns how many players are logged in
	 * @return
	 */
	public static int getPlayerCount() {
		return m_players.keySet().size();
	}
}
