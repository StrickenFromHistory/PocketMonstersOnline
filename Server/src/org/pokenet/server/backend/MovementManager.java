package org.pokenet.server.backend;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import org.pokenet.server.backend.entity.Char;
import org.pokenet.server.backend.entity.HMObject;
import org.pokenet.server.backend.entity.HMObject.objectType;

/**
 * Loops through all players and moves them if they request to be moved
 * @author shadowkanji
 *
 */
public class MovementManager implements Runnable {
	private Queue<Char> m_waiting;
	private Queue<Char> m_moved;
	private Thread m_thread;
	private boolean m_isRunning;
	private int m_pLoad = 0;
	/** Comparator for comparing chars */
	private Comparator<Char> m_comp;
	
	/**
	 * Default constructor.
	 */
	public MovementManager() {
		m_comp = new Comparator<Char>() {
			public int compare(Char arg0, Char arg1) {
				return arg0.getPriority() - arg1.getPriority();
			}};
		m_waiting = new PriorityQueue<Char>(11, m_comp);
		m_moved = new PriorityQueue<Char>(1, m_comp);
	}
	
	/**
	 * Adds a player to this movement service
	 * @param player
	 */
	public void addPlayer(Char player) {
		synchronized(m_waiting) {
			m_pLoad++;
			m_waiting.offer(player);
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
			Iterator<Char> it = m_waiting.iterator();
			while(it.hasNext()) {
				Char c = it.next();
				if(c.getName().equalsIgnoreCase(player)) {
					m_waiting.remove(c);
					m_pLoad--;
					return true;
				}
			}
		}
		/* Check moved list */
		synchronized(m_moved) {
			Iterator<Char> it = m_moved.iterator();
			while(it.hasNext()) {
				Char c = it.next();
				if(c.getName().equalsIgnoreCase(player)) {
					m_moved.remove(c);
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
		//ArrayList<Char> tmpArray = null;
		while(m_isRunning) {
			/* Pull char of highest priority */
			if(m_waiting != null && m_waiting.size() > 0) {
				synchronized(m_waiting) {
					tmp = m_waiting.poll();
				}
				/* Move character */
				tmp.move();
				/* Place him in moved array */
				synchronized(m_moved) {
					m_moved.offer(tmp);
				}
			}
			/* If waiting array is empty, swap arrays */
			synchronized(m_waiting) {
				if(m_waiting.size() == 0) {
					m_waiting = m_moved;
					m_moved = new PriorityQueue<Char>(1, m_comp);
				}
			}
			try {
				Thread.sleep(100);
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