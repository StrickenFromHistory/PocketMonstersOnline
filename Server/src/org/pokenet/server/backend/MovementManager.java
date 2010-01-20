package org.pokenet.server.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private ArrayList<Char> m_waiting;
	private ArrayList<Char> m_moved;
	private Thread m_thread;
	private boolean m_isRunning;
	private int m_pLoad = 0;
	/** Comparator for comparing chars */
	private Comparator<Char> m_comp;
	
	/**
	 * Default constructor.
	 */
	public MovementManager() {
		m_waiting = new ArrayList<Char>();
		m_moved = new ArrayList<Char>();
		m_comp = new Comparator<Char>() {
			public int compare(Char arg0, Char arg1) {
				return arg0.getPriority() - arg1.getPriority();
			}};
	}
	
	/**
	 * Adds a player to this movement service
	 * @param player
	 */
	public void addPlayer(Char player) {
		synchronized(m_waiting) {
			m_pLoad++;
			m_waiting.add(player);
		}
	}
	
	public void addHMObject(HMObject obj){
		synchronized(m_waiting) {
			if (obj.getType() == objectType.STRENGTH_BOULDER){
				m_pLoad++;
				m_waiting.add(obj);
			}
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
		/* Check waiting list */
		synchronized(m_waiting) {
			for(int i = 0; i < m_waiting.size(); i++) {
				if(m_waiting.get(i).getName().equalsIgnoreCase(player)) {
					m_waiting.remove(i);
					m_waiting.trimToSize();
					m_pLoad--;
					return true;
				}
			}
		}
		/* Check moved list */
		synchronized(m_moved) {
			for(int i = 0; i < m_moved.size(); i++) {
				if(m_moved.get(i).getName().equalsIgnoreCase(player)) {
					m_moved.remove(i);
					m_moved.trimToSize();
					m_pLoad--;
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Called by m_thread.start(). Loops through all players calling PlayerChar.move() if the player requested to be moved.
	 */
	public void run() {
		Char tmp = null;
		ArrayList<Char> tmpArray = null;
		while(m_isRunning) {
			/* Pull char of highest priority */
			synchronized(m_waiting) {
				tmp = m_waiting.remove(0);
				m_waiting.trimToSize();
				Collections.sort(m_waiting, m_comp);
			}
			/* Move character */
			tmp.move();
			/* Place him in moved array */
			synchronized(m_moved) {
				m_moved.add(tmp);
			}
			/* If waiting array is empty, swap arrays */
			synchronized(m_waiting) {
				if(m_waiting.size() == 0) {
					m_waiting = m_moved;;
					m_moved = new ArrayList<Char>();
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
		m_moved.clear();
		m_waiting.clear();
		m_isRunning = false;
	}

}