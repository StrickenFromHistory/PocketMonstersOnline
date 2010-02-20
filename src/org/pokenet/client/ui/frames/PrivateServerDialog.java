package org.pokenet.client.ui.frames;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.PreferencesManager;


import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

/**
 * Shows 5 most recently used private servers
 * @author lprestonsegoiii
 *
 */
public class PrivateServerDialog extends Frame{
	private static final char MAX_PRIVATE_SERVERS = 5; 
	private static final char BUTTON_WIDTH = 160;
	private static final char BUTTON_HEIGHT = 24;
	private static final char SPACE_BETWEEN_BUTTONS = 8;
	private static final char MARGIN = 16;
	PreferencesManager prefs = PreferencesManager.getPreferencesManager();

	
	private String[] privateServers;
	private Button[] privateServerButtons;
	private Color m_black;
	
	/**
	 * Default constructor
	 */
	public PrivateServerDialog(){
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);	
		m_black = new Color (0, 0, 0);

		this.setSize((2 * MARGIN) + BUTTON_WIDTH, 
				(3 * MARGIN) + MAX_PRIVATE_SERVERS * (BUTTON_HEIGHT + SPACE_BETWEEN_BUTTONS));
		this.setLocation(
				316 - this.getWidth() - 10,
				280 - this.getHeight() * 4 / 3);
		this.setTitle("Private Servers");
		this.setBackground(new Color(0, 0, 0, 140));
		this.getTitleBar().setForeground(m_black);
		this.setDraggable(true);
		this.setResizable(false);
		this.getTitleBar().getCloseButton().setVisible(true);
		
		privateServers = new String[MAX_PRIVATE_SERVERS];
		privateServerButtons = new Button[MAX_PRIVATE_SERVERS];
		
		privateServers = prefs.getStringArrayForKey(prefs.PRIVATE_SERVERS_KEY_NAME);
		
		if(null == this.privateServers){
			this.privateServers = new String[MAX_PRIVATE_SERVERS];
		}
		
		setUpServerButtons();
		
	}
	
	private void setUpServerButtons() {
		char numberOfServers = 0;

		for (int i = 0; i < MAX_PRIVATE_SERVERS; i++){
			if (null != privateServers[i]) numberOfServers++;
		}
		
		
		// make buttons for all these servers
		for(char curButton = 0; curButton < numberOfServers; curButton++){
			privateServerButtons[curButton] = new Button(privateServers[curButton]);
			privateServerButtons[curButton].setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
			privateServerButtons[curButton].setLocation(MARGIN, 
					(BUTTON_HEIGHT + SPACE_BETWEEN_BUTTONS) * curButton + MARGIN);
			privateServerButtons[curButton].setVisible(true);
			this.add(privateServerButtons[curButton]);
		}
		
		if (null != this.privateServers) addActionListenersToButtons();
	}

	/**
	 * adds the server address to the file so it can be 
	 * loaded next time the program is launched
	 * 
	 * insert server onto the top of a list stored in a file.
	 * remove the bottom server 
	 * does not allow duplicates
	 * 
	 * @param serverAddress
	 */
	public void addServer(String serverAddress){
		ArrayList<String> servers = new ArrayList<String>();
		
		// read in the current list of servers
		this.privateServers = prefs.getStringArrayForKey(prefs.PRIVATE_SERVERS_KEY_NAME);
	
		if(null == this.privateServers){
			this.privateServers = new String[MAX_PRIVATE_SERVERS];
		}
		
		for (String server : this.privateServers) {
			servers.add(server);
		}
		
		
		// check for duplicates, removing them as needed
		// then adding to the top of the list
		if(servers.contains(serverAddress)){
			servers.remove(serverAddress);
		}
		servers.add(0, serverAddress);
		
		
		// if the list is more than MAX_PRIVATE_SERVERS
		// get rid of the old ones
		for (int i = MAX_PRIVATE_SERVERS; i < servers.size(); i++){
			servers.remove(i);
		}
		
		for (int i = 0; i < MAX_PRIVATE_SERVERS; i++) {
			this.privateServers[i] = servers.get(i);
		}
		
		//update the GUI incase we need to reshow the frame
		setUpServerButtons();
		
		// finally, write list to preferences
		prefs.setObjectForKey(this.privateServers, prefs.PRIVATE_SERVERS_KEY_NAME);
	}
	

	/**
	 *  adds the action listeners
		   unfortunately, actionlisteners will only accept 
		   final variables, so they can't be added back in the for loop...
		   that would make things TOO easy...
	 */
	private void addActionListenersToButtons() {
		
		if(null != privateServers[0]){
			privateServerButtons[0].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					GameClient.setHost(privateServers[0]);
				}
			});
		}
		
		if(null != privateServers[1]){
			privateServerButtons[1].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					GameClient.setHost(privateServers[1]);
				}
			});
		}
		
		if(null != privateServers[2]){
			privateServerButtons[2].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					GameClient.setHost(privateServers[2]);
				}
			});
		}
		
		if(null != privateServers[3]){
			privateServerButtons[3].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					GameClient.setHost(privateServers[3]);
				}
			});
		}
		
		if(null != privateServers[4]){
			privateServerButtons[4].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					GameClient.setHost(privateServers[4]);
				}
			});
		}
		
	}
}
