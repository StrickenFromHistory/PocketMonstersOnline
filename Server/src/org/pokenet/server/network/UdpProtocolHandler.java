package org.pokenet.server.network;

import java.util.HashMap;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.network.message.PokenetMessage;

/**
 * Handles packets received from players over UDP
 * @author shadowkanji
 *
 */
public class UdpProtocolHandler extends IoHandlerAdapter {
	private static HashMap<Integer, PlayerChar> m_playerList;
	
	/**
	 * Default Constructor
	 */
	public UdpProtocolHandler() {
		m_playerList = new HashMap<Integer, PlayerChar>();
	}
	
	/**
	 * Called when an exception is caught
	 */
	public void exceptionCaught(IoSession session, Throwable t) throws Exception {
		t.printStackTrace();
	}
	
	@Override 
	public void messageReceived(IoSession session, Object o) throws Exception { 
		/* 
		 * Nothing is sent over udp from client, this class merely allows packets
		 * to be sent to client over udp.
		 */
	} 
	
	/**
	 * Adds a player to the udp player list
	 * @param p
	 */
	public static void addPlayer(PlayerChar p) {
		synchronized(m_playerList) {
			m_playerList.put(p.getId(), p);
		}
	}
	
	/**
	 * Removes a player from the udp player list
	 * @param p
	 */
	public static void removePlayer(PlayerChar p) {
		synchronized(m_playerList) {
			m_playerList.remove(p.getId());
		}
	}
	
	public static void writeMessage(IoSession s, PokenetMessage m) {
		try {
			if(s.isConnected())
				s.write(m.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
