package org.pokenet.server.backend;

import java.util.ArrayList;

import org.pokenet.server.backend.entity.PlayerChar;

/**
 * Loops through all players and moves them if they request to be moved
 * @author shadowkanji
 *
 */
public class MovementManager implements Runnable {
	private ArrayList<PlayerChar> m_players;
	private Thread m_thread;
	private boolean m_isRunning;
	private int m_pLoad = 0;
	
	/**
	 * Default constructor.
	 */
	public MovementManager() {
		m_players = new ArrayList<PlayerChar>();
		m_thread = new Thread(this);
	}
	
	/**
	 * Adds a player to this movement service
	 * @param player
	 */
	public void addPlayer(PlayerChar player) {
		m_pLoad++;
		m_players.add(player);
	}
	
	/**
	 * Returns how many players are in this thread (the processing load)
	 */
	public int getProcessingLoad() {
		return m_pLoad;
	}
	
	/**
	 * Removes a player from this movement service, returns true if the player was in the thread and was removed.
	 * Otherwise, returns false.
	 * @param player
	 */
	public boolean removePlayer(String player) {
		for(int i = 0; i < m_players.size(); i++) {
			if(m_players.get(i).getName().equalsIgnoreCase(player)) {
				m_players.remove(i);
				m_players.trimToSize();
				m_pLoad--;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Called by m_thread.start(). Loops through all players calling PlayerChar.move() if the player requested to be moved.
	 */
	public void run() {
		while(m_isRunning) {
			synchronized(m_players) {
				for(int i = 0; i < m_players.size(); i++) {
					try {
						m_players.get(i).move();
					} catch (Exception e) {
						e.printStackTrace();
						m_players.get(i).forceLogout();
					}
				}
			}
			try {
				Thread.sleep(250);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Starts the movement thread
	 */
	public void start() {
		m_isRunning = true;
		m_thread.start();
	}
	
	/**
	 * Stops the movement thread
	 */
	public void stop() {
		m_players.clear();
		m_isRunning = false;
	}

}