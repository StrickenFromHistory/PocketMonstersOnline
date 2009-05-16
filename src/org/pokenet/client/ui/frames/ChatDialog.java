package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.TextField;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;
import mdes.slick.sui.skin.simple.SimpleArrowButton;

import org.newdawn.slick.Color;
import org.newdawn.slick.gui.GUIContext;
import org.pokenet.client.GameClient;
import org.pokenet.client.ui.base.ComboBox;

/**
 * Chat Dialog
 * @author ZombieBear
 *
 */
@SuppressWarnings("deprecation")
public class ChatDialog extends Frame {
	static final long serialVersionUID = 8126828445828668638L;

	private HashMap<String, ChatWidget> m_availableChats;
	
    private TextField m_chatType;
    private ComboBox m_possibleChats;
    private String m_currentChat;
    
    private Color m_backColor = new Color(0, 0, 0, 85);
    private Color m_foreColor = new Color(255, 255, 255);

    public TextField getChatBox() {
            return m_chatType;
    }

    /**
     * Default constructor
     * @param packet
     */
    public ChatDialog(String name) {
    	super();
    	this.setTitle(name);
    	this.setName(name);
    	initGUI();
    }

    /**
     * Initializes the user interface
     */
    private void initGUI() {
    	this.setMinimumSize(206, 160);
    	this.setLocation(48, 0);
    	try {
    		setBackground(m_backColor);
    		setForeground(m_foreColor);
    		
    		m_currentChat = "Local";
    		m_availableChats = new HashMap<String, ChatWidget>();
    		m_availableChats.put("Local", new ChatWidget(m_foreColor));
    		
    		m_possibleChats = new ComboBox();
    		m_possibleChats.addElement("Local");
    		m_possibleChats.setForeground(m_foreColor);
    		getContentPane().add(m_possibleChats);

    		getContentPane().add(m_availableChats.get("Local"));
    		
    		m_chatType = new TextField();
    		m_chatType.setName("chatType");
    		getContentPane().add(m_chatType);
    		m_chatType.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent evt) {
    				chatTypeActionPerformed(evt);
    			}
    		});

    		this.getResizer().addMouseListener(new MouseAdapter() {
    			public void mouseDragged(MouseEvent event) {
    				repositionUI();
    			}
    		});
    		m_possibleChats.setBackground(m_chatType.getBackground());
    		setSize(206, 320);
    		m_chatType.grabFocus();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    /**
     * Sends the packet over to the server
     * @param evt
     */
    private void chatTypeActionPerformed(ActionEvent evt) {
    	if (m_chatType.getText() != null && m_chatType.getText().length() != 0) {
    		if (m_currentChat.equalsIgnoreCase("local"))
    			GameClient.getInstance().getPacketGenerator().write("Cl" + m_chatType.getText());
    		else
    			GameClient.getInstance().getPacketGenerator().write("Cp" + m_currentChat + "," 
    					+ m_chatType.getText());
    	}
    	m_chatType.setText("");
    	m_chatType.grabFocus();
    }

    /**
     * Drops focus
     */
    public void dropFocus() {
    	m_chatType.releaseFocus();
    }

    /**
     * Repositions UI elements
     */
    public void repositionUI(){
    	m_possibleChats.setSize(getWidth(), 15);
    	m_possibleChats.setLocation(0, 0);
    	
    	m_availableChats.get(m_currentChat).setSize(getWidth(), getHeight() - m_chatType.getHeight()
    			- getTitleBar().getHeight() - m_possibleChats.getHeight());
    	m_availableChats.get(m_currentChat).setLocation(0, m_possibleChats.getHeight() + m_possibleChats.getY());

		m_chatType.setSize(getWidth(), 25);
		m_chatType.setLocation(0, getHeight() - m_chatType.getHeight() - getTitleBar().getHeight());
    }

    /**
     * Returns the foreground color
     * @return the foreground color
     */
    public Color getForeColor(){
    	return m_foreColor;
    }
    
    /**
     * Adds a line to a chat, creates the private chat if it doesn't exist
     * @param chat
     * @param line
     */
    public void addChatLine(String chat, String line){
    	try {
    		m_availableChats.get(chat).addLine(line);
    	} catch (Exception e) {
    		e.printStackTrace();
    		addChat(chat);
    		m_availableChats.get(chat).addLine(line);
    	}
    }
    
    /**
     * Creates a new Chat Widget
     * @param chat
     */
    public void addChat(String chat){
    	m_availableChats.put(chat, new ChatWidget(m_foreColor));
    	m_possibleChats.addElement(chat);
    }
    
    /**
     * Sets the current chat
     * @param chat
     */
    public void setCurrentChat(String chat){
    	m_currentChat = chat;
    	m_possibleChats.setSelected(chat);
    	setTitle("Chat: " + m_currentChat);
    }
    
    @Override
    public void update(GUIContext container, int delta){
    	super.update(container, delta);
    	if (m_currentChat != m_possibleChats.getSelected()){
    		getContentPane().remove(m_availableChats.get(m_currentChat));
    		m_currentChat = m_possibleChats.getSelected();
    		repositionUI();
    		m_availableChats.get(m_currentChat).setForeground(m_foreColor);
    		getContentPane().add(m_availableChats.get(m_currentChat));
    		setTitle("Chat: " + m_currentChat);
    	}
    }
    
    @Override
    public void setSize(float width, float height){
		super.setSize(width, height);
    	try{
    		repositionUI();
    	} catch (NullPointerException e) {}
    }
}

/**
 * The widget where the chat is actually displayed
 * @author ZombieBear
 *
 */
class ChatWidget extends Container{
    private List<String> m_contents;
	List<String> m_wrappedText = new ArrayList<String>();
	
    private int m_scrollIndex = 0; 
	public int m_maxLines;
    private Label[] m_chatShown;
	private Button m_up, m_down;
    private Color m_foreColor;
    private boolean m_canScroll = true;
	
    /**
     * Default Constructor
     */
    @SuppressWarnings("deprecation")
	public ChatWidget(Color foreColor){
    	m_foreColor = foreColor;
    	m_maxLines = (int)(getHeight() / GameClient.getFontSmall().getHeight("X"));
		m_chatShown = new Label[m_maxLines];
		m_contents = new ArrayList<String>();
		m_up = new SimpleArrowButton(SimpleArrowButton.FACE_UP);
		m_down = new SimpleArrowButton(SimpleArrowButton.FACE_DOWN);
		layoutScrollButtons();
    }
    
    /**
     * Adds a line to the chat
     * @param line
     */
    public void addLine(String line){
    	while (!m_canScroll);
    	m_canScroll = false;
    	m_contents.add(line);
    	if (m_maxLines >= m_contents.size()){
    		scroll(0);
    	} else {
    		scroll(1);
    	}
    }
    
    /**
     * Handles scrolling and text display
     * @param indexMod
     */
    public void scroll(int indexMod){
    	for (int i = 0; i < m_chatShown.length; i++){
    		try {
    			m_chatShown[i].setText("");
    			remove(m_chatShown[i]);
    		}
    		catch (NullPointerException e) {}
    		catch (Exception e) {e.printStackTrace();}
    	}
    	m_maxLines = (int)(getHeight() / GameClient.getFontSmall().getHeight("X") - 1);
		m_chatShown = new Label[m_maxLines];

		//Handles the buttons' availability
		layoutScrollButtons();
		m_scrollIndex = m_scrollIndex + indexMod;

		if (m_scrollIndex == 0)
			m_up.setEnabled(false);
		else
			m_up.setEnabled(true);

		if (m_scrollIndex + m_maxLines >= m_contents.size())
			m_down.setEnabled(false);
		else
			m_down.setEnabled(true);

		wrap();
		System.out.println(m_wrappedText.toString());
		
		//Handles Chat drawing
    	int y = 0;
    	
    	for (int i = 0; i < m_chatShown.length; i++){
			try {
				m_chatShown[i].setText(m_wrappedText.get(m_wrappedText.size() - m_chatShown.length + i));
			} catch (NullPointerException e) {
    			m_chatShown[i] = new Label(m_wrappedText.get(m_wrappedText.size() - m_chatShown.length + i));
    		} catch (Exception e) {
    			m_chatShown[i] = new Label();
    		}
    		m_chatShown[i].setFont(GameClient.getFontSmall());
    		m_chatShown[i].setForeground(m_foreColor);
    		m_chatShown[i].setLocation(0, y);
    		m_chatShown[i].pack();
    		add(m_chatShown[i]);

    		y += GameClient.getFontSmall().getHeight("X");
    	}
    	m_canScroll = true;
    }
    
    /**
     * Sets the foreground color
     * @param c
     */
    public void setForeColor(Color c) {
    	m_foreColor = c;
    	scroll(0);
    }
    
    /**
     * Lays out the scrolling buttons
     */
	public void layoutScrollButtons(){
		int buttonWidth = 16;
		m_up.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(-1);
			}
		});
		m_up.setEnabled(false);
		m_up.setSize(buttonWidth, buttonWidth);
		m_up.setLocation(getWidth() - buttonWidth, 0);
		
		m_down.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(1);
			}
		});
		m_down.setSize(buttonWidth, buttonWidth);
		m_down.setLocation((float)getWidth() - buttonWidth, (float)(getHeight() - 2.5 * buttonWidth));
		
		add(m_up);
		add(m_down);
		
		if (m_contents.size() < m_maxLines){
			m_up.setVisible(false);
			m_down.setVisible(false);
		} else {
			m_up.setVisible(true);
			m_down.setVisible(true);
		}
	}
    
	/**
	 * Returns a List<String> with the wrapped text for the chat labels.
	 * @return a List<String> with the wrapped text for the chat labels.
	 */
	public void wrap(){
		m_wrappedText.clear();
		
		for (int i = m_scrollIndex; i < (m_scrollIndex + m_maxLines); i++) {
			if (m_contents.size() != 0)
				try{
					if (GameClient.getFontSmall().getWidth(m_contents.get(i)) <= getWidth()){
						m_wrappedText.add(m_contents.get(i));
					} else {
						String loopLine = new String();
						ArrayList<String> loopList = new ArrayList<String>();
						loopLine = m_contents.get(i);
						loopList.add(m_contents.get(i));
						while (GameClient.getFontSmall().getWidth(loopLine) > getWidth()){
							int linesToDrop = 1;
							while (GameClient.getFontSmall().getWidth(loopList.get(
									loopList.size() - 1)) > getWidth()){
								loopList.add(loopLine.substring(0, loopLine.length() 
										- linesToDrop));
								linesToDrop++;
							}
							m_wrappedText.add(loopList.get(loopList.size() - 1));
							loopLine = loopLine.substring(loopList.get(
									loopList.size() - 1).length());
							loopList.add(loopLine);
						}
						m_wrappedText.add(loopLine);
					}
				} catch (IndexOutOfBoundsException e) {}
				catch (Exception e) {e.printStackTrace();}
		}
	}
	
    @Override
    public void setSize(float width, float height){
		super.setSize(width, height);
		m_maxLines = (int)(getHeight() / GameClient.getFontSmall().getHeight("X"));
		m_chatShown = new Label[m_maxLines];
		scroll(0);
    }
}