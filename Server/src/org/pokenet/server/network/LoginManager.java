package org.pokenet.server.network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.common.IoSession;

/**
 * Handles logging players in
 * @author shadowkanji
 *
 */
public class LoginManager implements Runnable {
	private Queue<Object []> m_loginQueue;
	private LogoutManager m_logoutManager;
	private Thread m_thread;
	private boolean m_isRunning = true;
	private byte m_loginError;
	private byte m_errorCode;
	
	/**
	 * Default constructor. Requires a logout manager to be passed in so the server
	 * can check if player's data is not being saved as they are logging in.
	 * @param manager
	 */
	public LoginManager(LogoutManager manager) {
		m_logoutManager = manager;
		m_loginError = 0;
		m_errorCode = -128;
		m_loginQueue = new ConcurrentLinkedQueue<Object []>();
		m_thread = new Thread(this);
	}
	
	/**
	 * Attempts to login a player. Upon success, it sends a packet to the player to inform them they are logged in.
	 * @param session
	 * @param username
	 * @param password
	 */
	private void attemptLogin(IoSession session, String username, String password) {

	}
	
	/**
	 * Places a player in the login queue
	 * @param session
	 * @param username
	 * @param password
	 */
	public void queuePlayer(IoSession session, String username, String password) {
		if(!m_logoutManager.containsPlayer(username))
			m_loginQueue.add(new Object[] {session, username, password});
		else {
			//TODO: Informs the player that they are still being logged out 
		}
	}

	/**
	 * Called by Thread.start()
	 */
	public void run() {
		Object [] o;
		IoSession session;
		String username;
		String password;
		while(m_isRunning) {
			synchronized(m_loginQueue) {
				try {
					o = m_loginQueue.poll();
					session = (IoSession) o[0];
					username = (String) o[1];
					password = (String) o[2];
					this.attemptLogin(session, username, password);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(200);
				} catch (Exception e) {}
			}
		}
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
		m_isRunning = false;
	}

}
