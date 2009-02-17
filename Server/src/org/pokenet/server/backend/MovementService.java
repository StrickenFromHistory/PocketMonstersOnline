package org.pokenet.server.backend;

import java.util.ArrayList;

import org.pokenet.server.backend.entity.PlayerChar;

/**
 * Loops through all players and moves them if they request to be moved
 * @author shadowkanji
 *
 */
public class MovementService implements Runnable {
	private ArrayList<PlayerChar> m_players;
	private Thread m_thread;
	
	/**
	 * Default constructor.
	 */
	public MovementService() {
		m_players = new ArrayList<PlayerChar>();
		m_thread = new Thread(this);
	}
	
	/**
	 * Adds a player to this movement service
	 * @param player
	 */
	public void addPlayer(PlayerChar player) {
		m_players.add(player);
	}
	
	/**
	 * Returns how many players are in this thread
	 */
	public int getPlayerAmount() {
		return m_players.size();
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
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Called by m_thread.start(). Loops through all players calling PlayerChar.move() if the player requested to be moved.
	 */
	public void run() {
		while(true) {
			for(int i = 0; i < m_players.size(); i++) {
				if(m_players.get(i).isMovementRequested())
					m_players.get(i).move();
			}
			try {
				Thread.sleep(200);
			} catch (Exception e) { e.printStackTrace(); }
		}
	}
	
	/**
	 * Starts the movement thread
	 */
	public void start() {
		m_thread.start();
		System.out.println("INFO: Movement Service started");
	}
	
	/**
	 * Stops the movement thread
	 */
	public void stop() {
		
	}

}
