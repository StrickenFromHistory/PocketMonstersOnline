package org.pokenet.server.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.pokenet.server.GameServer;
import org.pokenet.server.backend.entity.PlayerChar;

/**
 * Handles packets recieved from the player
 * @author shadowkanji
 *
 */
public class ConnectionManager extends IoHandlerAdapter {
	private static Map<String, PlayerChar> m_players;
	
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
	
	//Logs out all players
	public void logoutAll() {
		LogoutManager l = GameServer.getServiceManager().getNetworkService().getLogoutManager();
		Iterator<PlayerChar> it = m_players.values().iterator();
		PlayerChar p;
		while(it.hasNext()) {
			p = it.next();
			l.queuePlayer(p);
		}
	}
}
