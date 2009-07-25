package org.pokenet.client.network;

import org.apache.mina.core.session.IoSession;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Player.Direction;

/**
 * Generates packets and sends them to the server
 * @author shadowkanji
 *
 */
public class PacketGenerator {
	private IoSession m_tcpSession;
	private IoSession m_udpSession;
	private long m_lastMovement = 0;
	
	/**
	 * Sets the UDP session
	 * @param s
	 */
	public void setUdpSession(IoSession s) {
		m_udpSession = s;
	}
	
	/**
	 * Sets the TCP session
	 * @param s
	 */
	public void setTcpSession(IoSession s) {
		m_tcpSession = s;
	}
	
	/**
	 * Returns the UDP session
	 * @return
	 */
	public IoSession getUdpSession() {
		return m_udpSession;
	}
	
	/**
	 * Returns the TCP session
	 * @return
	 */
	public IoSession getTcpSession() {
		return m_tcpSession;
	}
	
	/**
	 * Sends a packet over TCP
	 * @param message
	 */
	public void writeTcpMessage(String message) {
		m_tcpSession.write(message);
	}
	
	/**
	 * Sends a packet over UDP
	 * @param message
	 */
	public void writeUdpMessage(String message) {
		m_udpSession.write(message);
	}
	
	/**
	 * Sends a login packet
	 * @param username
	 * @param password
	 */
	public void login(String username, String password) {
		char language = '0';
		if(GameClient.getLanguage().equalsIgnoreCase("english")) {
			language = '0';
		} else if(GameClient.getLanguage().equalsIgnoreCase("portuguese")) {
			language = '1';
		} else if(GameClient.getLanguage().equalsIgnoreCase("italian")) {
			language = '2';
		} else if(GameClient.getLanguage().equalsIgnoreCase("french")) {
			language = '3';
		} else if(GameClient.getLanguage().equalsIgnoreCase("finnish")) {
			language = '4';
		} else if(GameClient.getLanguage().equalsIgnoreCase("spanish")) {
			language = '5';
		} else if(GameClient.getLanguage().equalsIgnoreCase("dutch")) {
			language = '6';
		} else if(GameClient.getLanguage().equalsIgnoreCase("german")) {
			language = '7';
		}
		m_tcpSession.write("l" + language + username + "," + (getPasswordHash(password)));
	}
	
	/**
	 * Sends a registration packet
	 * @param username
	 * @param password
	 * @param email
	 * @param dob
	 * @param starter
	 */
	public void register(
			String username,
			String password,
			String email,
			String dob,
			int starter,
			int sprite) {
        m_tcpSession.write("r" + username + "," + (getPasswordHash(password)) + "," + email + "," + dob + "," + starter + "," + sprite);
	}
	
	/**
	 * Sends a movement packet
	 * @param d
	 */
	public void move(Direction d) {
		if(System.currentTimeMillis() - m_lastMovement > 30) {
			switch(d) {
			case Down:
				m_udpSession.write("D" + GameClient.UDPCODE + String.valueOf(GameClient.getInstance().getPlayerId()));
				break;
			case Up:
				m_udpSession.write("U" + GameClient.UDPCODE + String.valueOf(GameClient.getInstance().getPlayerId()));
				break;
			case Left:
				m_udpSession.write("L" + GameClient.UDPCODE + String.valueOf(GameClient.getInstance().getPlayerId()));
				break;
			case Right:
				m_udpSession.write("R" + GameClient.UDPCODE + String.valueOf(GameClient.getInstance().getPlayerId()));
				break;
			}
			m_lastMovement = System.currentTimeMillis();
		}
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
