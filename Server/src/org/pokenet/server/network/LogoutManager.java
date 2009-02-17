package org.pokenet.server.network;

import org.pokenet.server.backend.entity.PlayerChar;

/**
 * Handles logging players out
 * @author shadowkanji
 *
 */
public class LogoutManager implements Runnable {
	private Thread m_thread;
	
	/**
	 * Default constructor
	 */
	public LogoutManager() {
		m_thread = new Thread(this);
	}
	
	/**
	 * Attempts to logout a player by saving their data. Returns true on success
	 * @param player
	 */
	public boolean attemptLogout(PlayerChar player) {
		return true;
	}
	
	/**
	 * Queues a player to be logged out
	 * @param player
	 */
	public void queuePlayer(PlayerChar player) {
		
	}

	/**
	 * Called by m_thread.start()
	 */
	public void run() {
		
	}
	
	/**
	 * Start this logout manager
	 */
	public void start() {
		m_thread.start();
	}
	
	/**
	 * Stop this logout manager
	 */
	public void stop() {
		
	}
}
