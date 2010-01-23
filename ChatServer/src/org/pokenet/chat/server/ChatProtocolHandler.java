package org.pokenet.chat.server;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.pokenet.chat.server.User.Language;

/**
 * Handles packets from clients
 * @author shadowkanji
 *
 */
public class ChatProtocolHandler extends IoHandlerAdapter {
	private static HashMap<Integer, ChatRoom> m_chatrooms;
	private int m_roomCount = 12;
	private LoginManager m_loginManager;
	private static HashMap<String, User> m_users;
	
	/**
	 * Constructor
	 */
	public ChatProtocolHandler() {
		m_loginManager = new LoginManager();
		new Thread(m_loginManager).start();
		m_users = new HashMap<String, User>();
		m_chatrooms = new HashMap<Integer, ChatRoom>();
		m_chatrooms.put(0, new ChatRoom("Developer Channel", 0, 7, Language.NONE));
		m_chatrooms.put(1, new ChatRoom("Game Moderator Channel", 1, 1, Language.NONE));
		m_chatrooms.put(2, new ChatRoom("Chat Moderator Channel", 2, 2, Language.NONE));
		m_chatrooms.put(3, new ChatRoom("Server Administrator Channel", 3, 6, Language.NONE));
		m_chatrooms.put(4, new ChatRoom("Game Chat", 4, 0, Language.ENGLISH));
		m_chatrooms.put(5, new ChatRoom("Discussao do jogo", 5, 0, Language.PORTUGESE));
		m_chatrooms.put(6, new ChatRoom("Gioco di Discussione", 6, 0, Language.ITALIAN));
		m_chatrooms.put(7, new ChatRoom("Discussion sur le Jeu", 7, 0, Language.FRENCH));
		m_chatrooms.put(8, new ChatRoom("Peli Keskustelu", 8, 0, Language.FINNISH));
		m_chatrooms.put(9, new ChatRoom("Discusion del Juego", 9, 0, Language.SPANISH));
		m_chatrooms.put(10, new ChatRoom("Spel Discussie", 10, 0, Language.DUTCH));
		m_chatrooms.put(11, new ChatRoom("Spiel Diskussion", 11, 0, Language.GERMAN));
	}
	
	/**
	 * Called when client sends a message
	 */
	public void messageReceived(IoSession session, Object m) throws Exception {
		String message = (String) m;
		String [] details;
		if(session.getAttribute("user") == null) {
			/* Not logged in, only allow login */
			switch(message.charAt(0)) {
			case 'l':
				//Login - lLANGUAGEUSERNAME,PASSWORD
				details = message.substring(2).split(",");
				m_loginManager.queueLogin(session, details[0], details[1], message.charAt(1));
				break;
			}
		} else {
			User u = (User) session.getAttribute("user");
			ChatRoom r = null;
			/* Logged in */
			switch(message.charAt(0)) {
			case 'a':
				//Add a friend
				u.addFriend(MySqlManager.parseSQL(message.substring(1)));
				break;
			case 'r':
				//Remove a friend
				u.removeFriend(message.substring(1));
				break;
			case 'm':
				//Make a new chatroom
				while(m_chatrooms.containsKey(m_roomCount) && m_roomCount > 0) {
					m_roomCount = m_roomCount == Integer.MAX_VALUE ? 0 : m_roomCount + 1;
				}
				if(m_roomCount == 0) {
					//TODO: Tell client room could not be created
					return;
				}
				//Else, make room
				synchronized(m_chatrooms) {
					r = new ChatRoom(message.substring(1), 
							m_roomCount, u.getLevel(), 
							u.getLanguage());
					r.addUser(u);
				}
				break;
			case 'j':
				//Join chatroom
				r = m_chatrooms.get(Integer.parseInt(message.substring(1)));
				if(r != null) {
					r.addUser(u);
				}
				break;
			case 'l':
				//Leave chatroom
				r = m_chatrooms.get(Integer.parseInt(message.substring(1)));
				if(r != null) {
					r.removeUser(u.getUsername());
				}
				break;
			case 'p':
				//Private chat - pUSER,MESSAGE
				details = message.substring(1).split(",");
				synchronized(m_users) {
					m_users.get(details[0]).getSession().write("p" + u.getUsername() + "," + details[1]);
				}
				break;
			case 'c':
				//Normal Chat
				details = message.substring(1).split(",");
				r = m_chatrooms.get(Integer.parseInt(details[0]));
				if(r != null) {
					r.queueMessage(u, details[1]);
				} else {
					//TODO: Inform client chatroom no longer exists
				}
				break;
			case '!':
				//Announcement
				if(u.getLevel() >= 6) {
					synchronized(m_users) {
						Iterator<User> it = m_users.values().iterator();
						while(it.hasNext()) {
							it.next().getSession().write("!" + message.substring(1));
						}
					}
				}
				break;
			}
		}
	}
	
	/**
	 * Called when session is closed
	 */
	public void sessionClosed(IoSession session) throws Exception {
		if(session.getAttribute("user") != null) {
			//Remove user from all chatrooms
			User u = (User) session.getAttribute("user");
			Iterator<ChatRoom> it = m_chatrooms.values().iterator();
			while(it.hasNext()) {
				it.next().removeUser(u.getUsername());
			}
		}
	}
	
	/**
	 * Removes a chatroom
	 * @param chatroom
	 */
	public static void removeChatRoom(int chatroom) {
		synchronized(m_chatrooms) {
			m_chatrooms.remove(chatroom);
		}
	}
	
	/**
	 * Returns hashmap of users on the server
	 * @return
	 */
	public static void addUser(User u) {
		synchronized(m_chatrooms) {
			/* Send chat rooms to user */
			Iterator<Integer> it = m_chatrooms.keySet().iterator();
			ChatRoom r = null;
			while(it.hasNext()) {
				int i = it.next();
				r = m_chatrooms.get(i);
				if(r.isJoinable(u)) {
					//TODO: Send client info of room
				}
			}
		}
		synchronized(m_users) {
			//Find all friends
			Iterator<String> it = u.getFriends().iterator();
			while(it.hasNext()) {
				String s = it.next();
				if(m_users.containsKey(s)) {
					//TODO: Tell client friend is online
				}
			}
			//Add user
			m_users.put(u.getUsername(), u);
		}
	}
}
