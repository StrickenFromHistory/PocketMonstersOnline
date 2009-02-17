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
	 * Removes a player from this movement service
	 * @param player
	 */
	public void removePlayer(PlayerChar player) {
		
	}
	
	/**
	 * Called by m_thread.start(). Loops through all players calling PlayerChar.move() if they need to be moved.
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
	 * Starts the movementservice
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
