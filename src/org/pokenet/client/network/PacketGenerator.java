package org.pokenet.client.network;

import org.apache.mina.common.IoSession;

/**
 * Generates packets and sends them to the server
 * @author shadowkanji
 *
 */
public class PacketGenerator {
	private IoSession m_session;
	
	/**
	 * Default constructor
	 * @param session
	 */
	public PacketGenerator(IoSession session) {
		m_session = session;
	}
	
	/**
	 * Returns the connection
	 * @return
	 */
	public IoSession getSession() {
		return m_session;
	}
	
	/**
	 * Sends a login packet
	 * @param username
	 * @param password
	 */
	public void login(String username, String password) {
		
	}
	
	/**
	 * Sends a registration packet
	 * @param username
	 * @param password
	 * @param email
	 * @param dob
	 * @param starter
	 */
	public void register(String username, String password, String email, String dob, int starter) {
		
	}
}
