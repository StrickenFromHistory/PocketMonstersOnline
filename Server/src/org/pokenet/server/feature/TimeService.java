package org.pokenet.server.feature;

/**
 * Handles game time and weather
 * @author shadowkanji
 *
 */
public class TimeService implements Runnable {
	private Thread m_thread;
	
	/**
	 * Default constructor
	 */
	public TimeService() {
		m_thread = new Thread(this);
	}
	
	/**
	 * Called by m_thread.start()
	 */
	public void run() {
		
	}
	
	/**
	 * Starts this Time Service
	 */
	public void start() {
		m_thread.start();
	}

	/**
	 * Stops this Time Service
	 */
	public void stop() {
		
	}
}
