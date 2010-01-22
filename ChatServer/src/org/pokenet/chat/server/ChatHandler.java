package org.pokenet.chat.server;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * Handles packets from clients
 * @author shadowkanji
 *
 */
public class ChatHandler extends IoHandlerAdapter {
	private HashMap<Integer, ChatRoom> m_chatrooms;
	private int m_roomCount = 0;
	
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
				//Login
				break;
			}
		} else {
			User u = (User) session.getAttribute("user");
			ChatRoom r = null;
			/* Logged in */
			switch(message.charAt(0)) {
			case 'm':
				//Make a new chatroom
				if(!m_chatrooms.containsKey(m_roomCount)) {
					synchronized(m_chatrooms) {
						r = new ChatRoom(message.substring(1), m_roomCount, u.getLevel());
						r.addUser(u);
					}
				}
				break;
			case 'j':
				//Join chatroom
				r = m_chatrooms.get(Integer.parseInt(message.substring(1)));
				if(r != null) {
					r.removeUser(u.getUsername());
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
				//Private chat
				break;
			case 'c':
				//Normal Chat
				details = message.substring(1).split(",");
				r = m_chatrooms.get(Integer.parseInt(details[0]));
				if(r != null) {
					r.queueMessage(u, details[1]);
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
}
