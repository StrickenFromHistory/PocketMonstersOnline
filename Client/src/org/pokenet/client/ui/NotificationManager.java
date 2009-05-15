package org.pokenet.client.ui;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import mdes.slick.sui.Display;

import org.pokenet.client.ui.base.Notification;

/**
 * Manages notifications
 * @author shadowkanji
 *
 */
public class NotificationManager implements Runnable {
	@SuppressWarnings("unused")
	private Display m_display;
	private boolean m_isRunning;
	private Thread m_thread;
	private static Queue<Notification> m_notifications;
	
	/**
	 * Default constructor
	 * @param d
	 */
	public NotificationManager(Display d) {
		m_display = d;
	}
	
	/**
	 * Called when running
	 */
	public void run() {
		while(m_isRunning) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Adds a new notification
	 * @param n
	 */
	public static void addNotification(String n) {
		m_notifications.add(new Notification(n));
	}
	
	/**
	 * Starts the notification manager
	 */
	public void start() {
		m_notifications = new ConcurrentLinkedQueue<Notification>();
		m_isRunning = true;
		m_thread = new Thread(this);
		m_thread.start();
	}

	/**
	 * Stops the notification manager
	 */
	public void stop() {
		m_isRunning = false;
	}
}
