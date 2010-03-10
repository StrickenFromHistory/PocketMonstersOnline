package org.pokenet.client.network;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.pokenet.client.GameClient;
import org.pokenet.client.ui.Ui;

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
			details = message.substring(1).split(",");
			for (String friend : details){
				Ui.getInstance().getFriendsList().setFriendOnline(friend, true);
			}
			break;
		case 'F':
			// A friend logged on/off
			switch(message.charAt(1)) {
			case 'n': //Logged on
				GameClient.getInstance().getUi().getFriendsList().setFriendOnline(message.substring(2), true);
				break;
			case 'f': //Logged off
				GameClient.getInstance().getUi().getFriendsList().setFriendOnline(message.substring(2), false);
				break;
			}
			break;
		case 'a':
			//Friend added - aUSERNAME
			Ui.getInstance().getFriendsList().addFriend(message.substring(1));
			break;
		case 'r':
			//Friend removed - rUSERNAME
			Ui.getInstance().getFriendsList().removeFriend(message.substring(1));
			break;
		case 'j':
			//Joined chatroom - jROOMNUMBER,ROOMNAME
			details = message.substring(1).split(",");
			Ui.getInstance().getChat().addChat(details[1], false);
			break;
		case 'p':
			//Private chat - pUSERNAME,MESSAGE
			details = message.substring(1).split(",");
			Ui.getInstance().getChat().addChat(details[0], true);
			Ui.getInstance().getChat().addWhisperLine(details[0], details[1]);
			break;
		case 'c':
			//Chatroom chat - cROOMNUMBER,MESSAGE  (NOTE: MESSAGE = <Username> Hi guys!)
			details = message.substring(1).split(",");
			Ui.getInstance().getChat().addChatLine(Integer.parseInt(details[0]),
					details[1]);
			break;
		case 'C':
			//Chatroom could not be created
			break;
		case 'E':
			//Chatroom no longer exists
			break;
		case 'R':
			//Add room to list of available rooms - RROOMNUMBER,ROOMNAME
			details = message.substring(1).split(",");
			Ui.getInstance().getChat().addChat(details[1], false);
			break;
		case '!':
			//Announcement - !MESSAGE
			Ui.getInstance().getChat().addSystemMessage(
					message.substring(1));
			break;
		}
	}
}
