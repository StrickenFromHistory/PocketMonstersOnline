package org.pokenet.server.network;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.pokenet.server.backend.entity.PlayerChar;

/**
 * Handles logging players out
 * @author shadowkanji
 *
 */
public class LogoutManager implements Runnable {
	private Queue<PlayerChar> m_logoutQueue;
	private Thread m_thread;
	
	/**
	 * Default constructor
	 */
	public LogoutManager() {
		m_logoutQueue = new ConcurrentLinkedQueue<PlayerChar>();
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
	 * Returns true if a user is being logged out
	 * This is used during login. If a player is in the logout queue,
	 * the player must wait to be logged out before being logged back in again.
	 * @param username
	 * @return
	 */
	public boolean containsPlayer(String username) {
		Iterator<PlayerChar> it = m_logoutQueue.iterator();
		PlayerChar p;
		while(it.hasNext()) {
			p = it.next();
			if(p.getName().equalsIgnoreCase(username))
				return true;
		}
		return false;
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
