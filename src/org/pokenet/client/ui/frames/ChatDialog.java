package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.HashMap;

import mdes.slick.sui.Frame;
import mdes.slick.sui.TextField;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;

import org.newdawn.slick.Color;
import org.newdawn.slick.gui.GUIContext;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.ModerationManager;
import org.pokenet.client.ui.base.ComboBox;

/**
 * Chat Dialog
 * @author ZombieBear
 * 
 */
public class ChatDialog extends Frame {
	private ArrayList<ArrayList<String>> m_chatList = new ArrayList<ArrayList<String>>();
	private HashMap<String, ArrayList<String>> m_availableChats = new HashMap<String, ArrayList<String>>();
	private Color m_backColor = new Color(0, 0, 0, 85);

	private String m_curChat = "";
	private Color m_foreColor = new Color(255, 255, 255);
	private TextField m_inputBox = new TextField();
	private ComboBox m_possibleChats = new ComboBox();
	private ChatWidget m_chatWidget = new ChatWidget();

	public ChatDialog() {
		initGUI();
		addChat("Local", false);
	}

	/**
	 * Sends the packet over to the server
	 * @param evt
	 */
	private void chatTypeActionPerformed(ActionEvent evt) {
		if (m_inputBox.getText() != null && m_inputBox.getText().length() != 0) {
			if (m_possibleChats.getSelected().equalsIgnoreCase("local")){
				if (m_inputBox.getText().charAt(0) == '/'){
					ModerationManager.parseLine(m_inputBox.getText().substring(1));
				} else {
					GameClient.getInstance().getPacketGenerator().writeTcpMessage("Cl" 
							+ m_inputBox.getText());
				}
			} else {
				if (m_inputBox.getText().charAt(0) == '/') {
					ModerationManager.parseLine(m_inputBox.getText().substring(1));
				} else {
					GameClient.getInstance().getPacketGenerator().writeTcpMessage(
							"Cp" + m_possibleChats.getSelected() + ","
							+ m_inputBox.getText());
					addWhisperLine(m_possibleChats.getSelected(), "<" + 
							GameClient.getInstance().getOurPlayer().getUsername() + "> " 
							+ m_inputBox.getText());
				}
			}
		}
		m_inputBox.setText("");
		m_inputBox.grabFocus();
	}

	/**
	 * Returns the chat box
	 * @return the chat box
	 */
	public TextField getChatBox() {
		return m_inputBox;
	}

	/**
	 * Initializes the user interface
	 */
	private void initGUI() {
		// Hack to properly align the conten pane in a slick frame
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		
		// Sets the frame's colors
		setBackground(m_backColor);
		setForeground(m_foreColor);

		// Chat Selection
		m_possibleChats.setForeground(m_foreColor);
		getContentPane().add(m_possibleChats);

		// Chat Widget
		m_chatWidget.setForeColor(m_foreColor);
		getContentPane().add(m_chatWidget);

		// Input box
		getContentPane().add(m_inputBox);
		m_inputBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chatTypeActionPerformed(evt);
			}
		});
		m_inputBox.grabFocus();

		// Repositions UI dynamically when the user resizes the window
		getResizer().addMouseListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				repositionUI();
			}
		});

		setMinimumSize(80, 80);
		setSize(400, 195);
	}

	/**
	 * Repositions UI elements
	 */
	public void repositionUI() {
		try {
			m_possibleChats.setSize(getWidth(), 15);
			m_possibleChats.setLocation(0, 0);

			m_chatWidget.setLocation(0, 15);
			m_chatWidget.setSize(getWidth(), getHeight()
					- getTitleBar().getHeight() - 40);

			m_inputBox.setSize(getWidth(), 25);
			m_inputBox.setLocation(0, getHeight() - m_inputBox.getHeight()
					- getTitleBar().getHeight());
		} catch (Exception e) {
		}
	}

	/**
	 * Drops focus
	 */
	public void dropFocus() {
		m_inputBox.releaseFocus();
	}

	/**
	 * Adds a line to a private chat,
	 * creates the private chat if it doesn't exist
	 * @param chat
	 * @param line
	 */
	public void addWhisperLine(String chat, String line) {
		if (m_availableChats.containsKey(chat)) {
			m_availableChats.get(chat).add(line);
			m_chatWidget.addLine();
		} else {
			addChat(chat, true);
			m_availableChats.get(chat).add(line);
			m_chatWidget.addLine();
		}
	}
	
	/**
	 * Adds a line to a chat channel,
	 * creates the channel if it doesn't exist
	 * @param chat
	 * @param line
	 */
	public void addChatLine(int chat, String line) {
		try{
			m_chatList.get(chat).add(line);
			m_chatWidget.addLine();
		} catch (Exception e){}
	}
	
	/**
	 * Adds a system message to your chat
	 * @param message
	 */
	public void addSystemMessage(String message){
		for (String s : m_availableChats.keySet())
			m_availableChats.get(s).add('*' + message);
		m_chatWidget.addLine();
	}
	
	/**
	 * Adds a server announcement
	 * @param message
	 */
	public void addAnnouncement(String message){
		for (String s : m_availableChats.keySet())
			m_availableChats.get(s).add('%' + message);
		m_chatWidget.addLine();
	}
	
	/**
	 * Creates a new private chat channel
	 * @param chat
	 */
	public void addChat(String chat, boolean isWhisper) {
		if (!m_availableChats.containsKey(chat)){
			m_availableChats.put(chat, new ArrayList<String>());
			m_possibleChats.addElement(chat);
			m_possibleChats.setSelected(chat);
			if (!isWhisper)
				m_chatList.add(m_availableChats.get(chat));
		} else {
			m_possibleChats.setSelected(chat);
		}
	}

	@Override
	public void setForeground(Color c){
		super.setForeground(c);
		try{
			m_chatWidget.setForeColor(c);
		} catch (Exception e) {}
	}
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		repositionUI();
	}

	@Override
	public void update(GUIContext container, int delta) {
		super.update(container, delta);
		if (!m_curChat.equalsIgnoreCase(m_possibleChats.getSelected())) {
			m_curChat = m_possibleChats.getSelected();
			m_chatWidget.setContents(m_availableChats.get(m_possibleChats
					.getSelected()));
			setTitle("Chat: " + m_possibleChats.getSelected());
		}
	}
}