package org.pokenet.chat.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.pokenet.chat.server.User.Language;

/**
 * Represents a chat room
 * @author shadowkanji
 *
 */
public class ChatRoom implements Runnable {
	private int m_level;
	private String m_team;
	private HashMap<String, User> m_users;
	private Queue<String> m_messageQueue;
	private String m_name;
	private int m_id;
	private Language m_language;
	
	/**
	 * Constructor
	 * @param name
	 * @param id
	 * @param level
	 * @param l
	 */
	public ChatRoom(String name, int id, int level, Language l) {
		m_name = name;
		m_id = id;
		m_level = level;
		m_users = new HashMap<String, User>();
		m_messageQueue = new LinkedList<String>();
		m_language = l;
	}

	@Override
	public void run() {
		String message = null;
		while(true) {
			synchronized(m_messageQueue) {
				message = m_messageQueue.poll();
			}
			if(message != null) {
				//Send to everyone in room
				synchronized(m_users) {
					Iterator<User> it = m_users.values().iterator();
					while(it.hasNext()) {
						User u = it.next();
						if(u != null) {
							u.getSession().write("" + message);
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
	 * Queues a message to be sent to the entire chatroom
	 * @param u
	 * @param message
	 */
	public void queueMessage(User u, String message) {
		if(m_users.containsKey(u.getUsername())) {
			synchronized(m_messageQueue) {
				m_messageQueue.offer("<" + u.getUsername() + "> " + message);
			}
		}
	}
	
	/**
	 * Returns true if a user can join this chatroom
	 * @param u
	 * @return
	 */
	public boolean isJoinable(User u) {
		//Make sure they're of same language
		if(m_language != Language.NONE &&
				u.getLanguage() != m_language)
			return false;
		if(u.getLevel() >= m_level) {
			//If it's a team chatroom, only allow them if they're on team
			if(m_team != null) {
				if(u.getTeam().equalsIgnoreCase(m_team)) {
					return true;
				}
			} else {
				//Else, they can join
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a user to a chatroom. Returns false if they do
	 * not have appropriate permissions to join.
	 * @param u
	 */
	public boolean addUser(User u) {
		if(isJoinable(u)) {
			synchronized(m_users) {
				m_users.put(u.getUsername(), u);
			}
			//TODO: Tell user they joined
			return true;
		}
		return false;
	}
	
	/**
	 * Removes a user from the chatroom
	 * @param username
	 */
	public boolean removeUser(String username) {
		boolean found = false;
		synchronized(m_users) {
			if(m_users.remove(username) != null) {
				//TODO: Tell client they left
				found = true;
			}
		}
		return found;
	}
	
	/**
	 * Returns the id of the chatroom
	 * @return
	 */
	public int getId() {
		return m_id;
	}
	
	/**
	 * Returns the name of the chatroom
	 * @return
	 */
	public String getName() {
		return m_name;
	}
}
