package org.pokenet.server.backend;

import java.util.HashMap;

import org.pokenet.server.backend.entity.Char;
import org.pokenet.server.backend.entity.HMObject;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.backend.entity.HMObject.objectType;

/**
 * Loops through all players and moves them if they request to be moved
 * @author shadowkanji
 *
 */
public class MovementManager implements Runnable {
	private HashMap<String, Char> m_players;
	private Thread m_thread;
	private boolean m_isRunning;
	private int m_pLoad = 0;
	
	/**
	 * Default constructor.
	 */
	public MovementManager() {
		m_players = new HashMap<String, Char>();
	}
	
	/**
	 * Adds a player to this movement service
	 * @param player
	 */
	public void addPlayer(PlayerChar player) {
		m_pLoad++;
		m_players.put(player.getName(), player);
	}
	
	public void addHMObject(HMObject obj){
		if (obj.getType() == objectType.STRENGTH_BOULDER){
			m_pLoad++;
			m_players.put(obj.getName() + obj.getId(), obj);
		}
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
		if(m_players.remove(player) != null) {
			m_pLoad--;
			return true;
		}
		return false;
	}
	
	/**
	 * Called by m_thread.start(). Loops through all players calling PlayerChar.move() if the player requested to be moved.
	 */
	public void run() {
		while(m_isRunning) {
			synchronized(m_players) {
				for(Char p : m_players.values()) {
					try {
						p.move();
					} catch (Exception e) {
						e.printStackTrace();
						removePlayer(p.getName());
						if (p instanceof PlayerChar){
							PlayerChar player = (PlayerChar) p;
							player.forceLogout();
						}
					}
				}
			}
			try {
				Thread.sleep(250);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Returns true if the movement manager is running
	 * @return
	 */
	public boolean isRunning() {
		return m_thread != null && m_thread.isAlive();
	}
	
	/**
	 * Starts the movement thread
	 */
	public void start() {
		m_thread = new Thread(this);
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