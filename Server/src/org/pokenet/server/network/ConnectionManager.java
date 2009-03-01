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
	
	public ConnectionManager(LoginManager login, LogoutManager logout) {
		m_loginManager = login;
		m_logoutManager = logout;
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
					break;
				case 'p':
					//Private chat
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
			GameServer.getServiceManager().getNetworkService().getLogoutManager().queuePlayer(p);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Logs out all players
	 */
	public void logoutAll() {
		LogoutManager l = GameServer.getServiceManager().getNetworkService().getLogoutManager();
		Iterator<PlayerChar> it = m_players.values().iterator();
		PlayerChar p;
		while(it.hasNext()) {
			p = it.next();
			l.queuePlayer(p);
		}
	}
	
	/**
	 * Returns the list of players
	 * @return
	 */
	public static HashMap<String, PlayerChar> getPlayers() {
		return m_players;
	}
}
