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
		m_session.write("l" + username + "," + (getPasswordHash(password)));
	}
	
	/**
	 * Sends a registration packet
	 * @param username
	 * @param password
	 * @param email
	 * @param dob
	 * @param starter
	 */
	public void register(String username, String password, String email, String dob, int starter, int sprite) {
        m_session.write("r" + username + "," + (getPasswordHash(password)) + "," + email + "," + dob + "," + starter + "," + sprite);
	}
	
	/**
	 * Returns the hashed password
	 * @param password
	 * @return
	 */
	private String getPasswordHash(String password) {
		Whirlpool hasher = new Whirlpool();
        hasher.NESSIEinit();

        // add the plaintext password to it
        hasher.NESSIEadd(password);

        // create an array to hold the hashed bytes
        byte[] hashed = new byte[64];

        // run the hash
        hasher.NESSIEfinalize(hashed);

        // this stuff basically turns the byte array into a hexstring
        java.math.BigInteger bi = new java.math.BigInteger(hashed);
        String hashedStr = bi.toString(16);            // 120ff0
        if (hashedStr.length() % 2 != 0) {
                // Pad with 0
                hashedStr = "0"+hashedStr;
        }
        return hashedStr;
	}
}
