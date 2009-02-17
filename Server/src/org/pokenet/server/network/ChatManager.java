package org.pokenet.server.network;

/**
 * Handles chat messages sent by players
 * @author shadowkanji
 *
 */
public class ChatManager implements Runnable {
	private Thread m_thread;
	
	/**
	 * Default Constructor
	 */
	public ChatManager() {
		m_thread = new Thread(this);
	}
	
	/**
	 * Called by m_thread.start()
	 */
	public void run() {
		
	}
	
	/**
	 * Start this chat manager
	 */
	public void start() {
		m_thread.start();
	}
	
	/**
	 * Stop this chat manager
	 */
	public void stop() {
		
	}

}
