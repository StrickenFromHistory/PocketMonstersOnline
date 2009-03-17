package org.pokenet.client.ui;

import java.util.ArrayList;

import org.pokenet.client.ui.base.ImageButton;
import org.pokenet.client.ui.frames.ChatDialog;
import org.pokenet.client.ui.frames.FriendListDialog;

import mdes.slick.sui.Button;
import mdes.slick.sui.Display;
import mdes.slick.sui.Frame;

/**
 * The main ui on screen
 * @author shadowkanji
 *
 */
public class Ui extends Frame {
	private FriendListDialog m_friendList;
	private ChatDialog m_localChat;
	private ArrayList<ChatDialog> m_privateChat;
	private ImageButton [] m_buttons;
	private Display m_display;
	
	/**
	 * Default constructor
	 */
	public Ui(Display display) {
		this.setSize(48, 160);
		this.setLocation(0, 0);
		
		m_display = display;
		
		m_localChat = new ChatDialog("Cl");
		m_privateChat = new ArrayList<ChatDialog>();
		
		m_buttons = new ImageButton[6];
		for(int i = 0; i < m_buttons.length; i++) {
			m_buttons[i] = new ImageButton();
			m_buttons[i].setSize(32, 32);
			m_buttons[i].setLocation(8, (32 * i + 1) + 4);
		}
		
		this.getTitleBar().setVisible(false);
		
		m_display.add(m_localChat);
		m_display.add(this);
	}
	
	/**
	 * Adds a message to its appropriate chat window
	 * @param m
	 */
	public void messageReceived(String m) {
		switch(m.charAt(0)) {
		case 'l':
			//Local Chat
			m_localChat.appendText(m.substring(1));
			break;
		case 'p':
			//Private Chat
			String [] details = m.substring(1).split(",");
			//Find the private chat and add the text to it
			for(int i = 0; i < m_privateChat.size(); i++) {
				if(m_privateChat.get(i).getName().equalsIgnoreCase(details[0])) {
					m_privateChat.get(i).appendText(details[1]);
					/*
					 * If the private chat is visible on screen, exit this method
					 * Else, add a popup notification about the new message
					 */
					if(m_privateChat.get(i).isVisible())
						return;
					else
						break;
				}
			}
			NotificationManager.addNotification("Message from " + details[0]);
			break;
		}
	}
	
	/**
	 * Sets all components visible/invisible
	 * @param b
	 */
	public void setAllVisible(boolean b) {
		this.setVisible(b);
		m_localChat.setVisible(b);
	}
	
	/**
	 * Returns the local chat
	 * @return
	 */
	public ChatDialog getLocalChat() {
		return m_localChat;
	}
}
