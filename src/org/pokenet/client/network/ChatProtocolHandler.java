package org.pokenet.client.network;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * Handles chat messages
 * @author shadowkanji
 *
 */
public class ChatProtocolHandler extends IoHandlerAdapter {
	/**
	 * Called when a message is received from chat server
	 */
	public void messageReceived(IoSession session, Object m) throws Exception {
		String message = (String) m;
		String [] details = null;
		switch(message.charAt(0)) {
		case 'l':
			switch(message.charAt(1)) {
			case 's':
				//Successful login - ls
				break;
			case 'e':
				//Invalid username or password - le
				break;
			case 'c':
				//Chat server couldn't connect to database - lc
				break;
			case 'r':
				//This user left a room - rROOMNUMBER
				break;
			}
			break;
		case 'f':
			//Friends online list - fUSERNAME,USERNAME,USERNAME,etc.
			break;
		case 'a':
			//Friend added - aUSERNAME
			break;
		case 'r':
			//Friend removed - rUSERNAME
			break;
		case 'j':
			//Joined chatroom - jROOMNUMBER,ROOMNAME
			break;
		case 'p':
			//Private chat - pUSERNAME,MESSAGE
			break;
		case 'c':
			//Chatroom chat - cROOMNUMBER,MESSAGE  (NOTE: MESSAGE = <Username> Hi guys!)
			break;
		case 'C':
			//Chatroom could not be created
			break;
		case 'E':
			//Chatroom no longer exists
			break;
		case 'R':
			//Add room to list of available rooms - RROOMNUMBER,ROOMNAME
			break;
		case '!':
			//Announcement - !MESSAGE
			break;
		}
	}
}
