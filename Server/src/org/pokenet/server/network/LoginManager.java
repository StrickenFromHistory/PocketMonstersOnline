package org.pokenet.server.network;

/**
 * Handles logging players in
 * @author shadowkanji
 *
 */
public class LoginManager implements Runnable {
	private LogoutManager m_logoutManager;
	private Thread m_thread;
	
	/**
	 * Default constructor. Requires a logout manager to be passed in so the server
	 * can check if player's data is not being saved as they are logging in.
	 * @param manager
	 */
	public LoginManager(LogoutManager manager) {
		m_logoutManager = manager;
		m_thread = new Thread(this);
	}

	/**
	 * Called by Thread.start()
	 */
	public void run() {
		
	}
	
	/**
	 * Starts the login manager
	 */
	public void start() {
		m_thread.start();
	}
	
	/**
	 * Stops the login manager
	 */
	public void stop() {
		
	}

}
