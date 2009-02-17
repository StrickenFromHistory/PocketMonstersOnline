package org.pokenet.server.network;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

/**
 * Handles packets recieved from the player
 * @author shadowkanji
 *
 */
public class ConnectionManager extends IoHandlerAdapter {
	
	/**
	 * Handles any exceptions involving a player's session
	 */
	public void exceptionCaught(IoSession session, Throwable t)
	throws Exception {
		/*
		 * Attempt to disconnect and logout the player (save their data)
		 */
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
	}
}
