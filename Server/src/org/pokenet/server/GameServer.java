package org.pokenet.server;

/**
 * Represents a game server.
 * @author shadowkanji
 *
 */
public class GameServer {
	private static ServiceManager m_serviceManager;
	
	/**
	 * If you don't know what this method does, you clearly don't know enough Java to be working on this.
	 * @param args
	 */
	public static void main(String [] args) {
		m_serviceManager = new ServiceManager();
		m_serviceManager.start();
	}
}
