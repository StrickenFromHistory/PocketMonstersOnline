package org.pokenet.client.ui;

import java.util.ArrayList;

import mdes.slick.sui.Display;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Item;
import org.pokenet.client.ui.base.HUDButtonFactory;
import org.pokenet.client.ui.base.ImageButton;
import org.pokenet.client.ui.base.ListBox;
import org.pokenet.client.ui.frames.BagDialog;
import org.pokenet.client.ui.frames.ChatDialog;
import org.pokenet.client.ui.frames.FriendListDialog;
import org.pokenet.client.ui.frames.HelpWindow;
import org.pokenet.client.ui.frames.NPCSpeechFrame;
import org.pokenet.client.ui.frames.OptionsDialog;
import org.pokenet.client.ui.frames.PartyInfo;
import org.pokenet.client.ui.frames.RequestWindow;
import org.pokenet.client.ui.frames.TradeDialog;

/**
 * The main ui on screen
 * @author shadowkanji
 * @author ZombieBear
 *
 */
public class Ui extends Frame {
	private FriendListDialog m_friendList;
	private ChatDialog m_localChat;
	private ArrayList<ChatDialog> m_privateChat;
	private ImageButton [] m_buttons;
	private Display m_display;
	private Label m_moneyLabel = new Label();
    private OptionsDialog m_optionsForm;
    private RequestWindow m_requestsForm;
    private HelpWindow m_helpForm;
    private Frame m_bagForm;
    private PartyInfo m_teamInfo;
	private NPCSpeechFrame m_speechFrame;
    private boolean m_isOption;
    private static final int UI_WIDTH = 32*7;
	
	/**
	 * Default constructor
	 */
	public Ui(Display display) {
		this.setSize(48, 256);
		this.setLocation(0, -24);
		this.setBackground(new Color(0, 0, 0, 75));
		this.setResizable(false);
		this.setDraggable(false);
		
		m_display = display;
		
		m_localChat = new ChatDialog("Cl", "Chat: Local");
		m_privateChat = new ArrayList<ChatDialog>();
		
		startButtons();

		m_moneyLabel.setText("$100");
		m_moneyLabel.pack();
		m_moneyLabel.setLocation(4, 205);
		m_moneyLabel.setVisible(true);
		m_moneyLabel.setFont(GameClient.getFontSmall());
		m_moneyLabel.setForeground(new Color(255, 255, 255));
		this.add(m_moneyLabel);
		
		this.add(GameClient.getInstance().getTimeService());
		
		this.getTitleBar().setVisible(false);

		m_localChat.setLocation(GameClient.getInstance().getDisplay().getWidth()
				- m_localChat.getWidth(), 0);
		m_display.add(m_localChat);
		m_display.add(this);
	}
	
	public void startButtons(){
		m_buttons = new ImageButton[5];
		
		m_buttons[0] = HUDButtonFactory.getButton("requests");
		m_buttons[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleRequests();
			}
		});

		m_buttons[1] = HUDButtonFactory.getButton("bag");
        m_buttons[1].addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		toggleBag();
        	}
        });
        getContentPane().add(m_buttons[1]);
        
        m_buttons[2] = HUDButtonFactory.getButton("pokemon");
        m_buttons[2].addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		togglePokemon();
        	}
        });
        
        m_buttons[3] = HUDButtonFactory.getButton("options");
        m_buttons[3].addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		toggleOptions();
        	}
        });
        
        m_buttons[4] = HUDButtonFactory.getButton("help");
        m_buttons[4].addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		toggleHelp();
        	}
        });
        
        for (int i = 0; i < m_buttons.length; i++){
        	m_buttons[i].pack();
        	getContentPane().add(m_buttons[i]);
        }
        
		m_buttons[0].setLocation(7, 22);
		m_buttons[1].setLocation(7, 22 + 32 + 5);
		m_buttons[2].setLocation(7, 22 + 64 + 10);
		m_buttons[3].setLocation(7, 22 + 96 + 15);
		m_buttons[4].setLocation(7, 22 + 128 + 20);
	}
	
	/**
	 * Adds a message to its appropriate chat window
	 * @param m
	 */
	public void messageReceived(String m) {
		switch(m.charAt(0)) {
		case 'n':
			//NPC speech stored as an array of strings
			String [] speech = m.substring(1).split(",");
			int [] sids = new int [speech.length];
			for(int i = 0; i < speech.length; i++) {
				sids[i] = Integer.parseInt(speech[i]);
			}
			for(int i = 0; i < sids.length; i++) {
				speech[i] = GameClient.getInstance().getMapMatrix().getSpeech(sids[i]);
			}
			//TODO: Queue npc speech in speech box, speech stores all speech
			break;
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
					else {
						NotificationManager.addNotification("Message from " + details[0]);
						return;
					}
				}
			}
			//If not found, open up a new chat window
			ChatDialog c = new ChatDialog("Cp" + details[0] + ",", "Chat: " + details[0]);
			m_privateChat.add(c);
			m_display.add(c);
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
		for(int i = 0; i < m_privateChat.size(); i++) {
			m_privateChat.get(i).setVisible(b);
		}
	}
	
	/**
	 * Opens up all private chats
	 */
	public void showPrivateChatWindows() {
		for(int i = 0; i < m_privateChat.size(); i++) {
			m_privateChat.get(i).setVisible(true);
		}
	}
	
	/**
	 * Returns the local chat
	 * @return
	 */
	public ChatDialog getLocalChat() {
		return m_localChat;
	}
	
    /**
     * Updates the data
     * @param p
     */
    public void update(){
    	m_moneyLabel.setText("$" + String.valueOf(GameClient.getInstance()
    			.getOurPlayer().getMoney()));
    	m_moneyLabel.pack();
    	m_teamInfo.update(GameClient.getInstance().getOurPlayer().getPokes());
    }
    
    /**
     * Returns true if a pane is being shown
     * @return
     */
    public boolean isOccupied() {
    	return ( (m_optionsForm != null && m_optionsForm.isVisible())
    			|| (m_bagForm != null && m_bagForm.isVisible()) ||
    			(m_teamInfo != null && m_teamInfo.isVisible()));
    }
    
    /**
     * ????
     * @return
     */
    public boolean isOption() {
            return m_isOption;
    }
    
    /**
     * Returns the options form
     * @return
     */
    public OptionsDialog getOptionPane() {
            return m_optionsForm;
    }
    
    /**
     * Returns the request window
     * @return
     */
    public RequestWindow getReqWindow() {
            return m_requestsForm;
    }
    
    /**
     * Toggles the Request Pane
     */
    public void toggleRequests(){
    	if (getDisplay().containsChild(m_requestsForm)) {
			getDisplay().remove(m_requestsForm);      
			hideHUD();
		} else {
			hideHUD();
			m_requestsForm = new RequestWindow();
			m_requestsForm.setWidth(UI_WIDTH);
			m_requestsForm.setLocation(48, 0);
			m_requestsForm.setPokeData(GameClient.getInstance().getOurPlayer()
					.getPokemon());
			m_requestsForm.setDraggable(false);
			getDisplay().add(m_requestsForm);
		}
    }
    
    /**
     * Toggles the Bag Pane
     */
    public void toggleBag(){
    	if (m_bagForm != null) {
			getDisplay().remove(m_bagForm);
			hideHUD();
		} else {
			hideHUD();
			m_bagForm = new Frame();
			m_bagForm.setBackground(new Color(0, 0, 0, 70));
			m_bagForm.setResizable(false);
			m_bagForm.setDraggable(false);
			BagDialog pane = new BagDialog(
					GameClient.getInstance().getOurPlayer().getItems()) {
				public void itemClicked(Item item) {
					GameClient.getInstance().getPacketGenerator().write("u" + 
							item.getName());
				}
				public void cancelled() {
					m_bagForm.setVisible(false);
				}
			};
			pane.setSize(UI_WIDTH, 300);
			pane.pack();
			
			ListBox badges = new ListBox(
					GameClient.getInstance().getOurPlayer().getBadges());
			badges.setSize(UI_WIDTH, 200);
			badges.pack();
			m_bagForm.getTitleBar().getCloseButton().setVisible(false);
			m_bagForm.getContentPane().add(badges);
			m_bagForm.getContentPane().add(pane);
			badges.setLocation(0, 300);
			m_bagForm.setSize(pane.getWidth(), 
					pane.getHeight() + badges.getHeight() + m_bagForm.getTitleBar().getHeight());
			getDisplay().add(m_bagForm);
			m_bagForm.setLocation(48, 0);
			m_bagForm.setDraggable(false);
		}
    }

    /**
     * Toggles the Pokemon Pane
     */
    public void togglePokemon(){
    	if (m_teamInfo != null) {
			getDisplay().remove(m_teamInfo);
			hideHUD();
		} else {
			hideHUD();
			m_teamInfo = new PartyInfo(GameClient.getInstance().getOurPlayer()
					.getPokes());
			m_teamInfo.setWidth(UI_WIDTH);
			m_teamInfo.setLocation(48, 0);
			m_teamInfo.setDraggable(false);
			getDisplay().add(m_teamInfo);
		}
    }
    
    /**
     * Toggles the Options Pane
     */
    public void toggleOptions(){
    	if (m_optionsForm != null) {
			getDisplay().remove(m_optionsForm);
			hideHUD();
		} else {
			hideHUD();
			m_isOption = true;
			m_optionsForm = new OptionsDialog();
			m_optionsForm.setWidth(UI_WIDTH);
			m_optionsForm.setLocation(48, 0);
			m_optionsForm.setDraggable(false);
			getDisplay().add(m_optionsForm);
		}
    }
    
    /**
     * Toggles the Help Pane
     */
    public void toggleHelp(){
    	if (m_helpForm != null) {
			getDisplay().remove(m_helpForm);
			hideHUD();
		} else {
			hideHUD();
			m_helpForm = new HelpWindow();
			m_helpForm.setWidth(UI_WIDTH);
			m_helpForm.setHeight(300);
			m_helpForm.setLocation(48, 0);
			getDisplay().add(m_helpForm);
		}
    }
    
    /**
     * Hides all HUD elements
     */
    private void hideHUD() {
            if (m_requestsForm != null) m_requestsForm.setVisible(false);
            m_requestsForm = null;
            if (m_bagForm != null) m_bagForm.setVisible(false);
            m_bagForm = null;
            if (m_teamInfo != null) m_teamInfo.setVisible(false);
            m_teamInfo = null;
            if (m_optionsForm != null) m_optionsForm.setVisible(false);
            m_optionsForm = null;
            if (m_helpForm != null) m_helpForm.setVisible(false);
            m_helpForm = null;
    }
    
    /**
     * Starts to talk to an NPC
     * @param speech
     * @throws SlickException
     */
    public void talkToNPC(String speech) throws SlickException {
		m_speechFrame = new NPCSpeechFrame(speech);
		getDisplay().add(m_speechFrame);
//		if (speech.startsWith("*"))
//			usePokeStorageBox("");
	}
    
    /**
	 * Nulls m_speechFrame
	 * @return
	 */
	public void nullSpeechFrame() {
		getDisplay().remove(m_speechFrame);
		m_speechFrame = null;
	}
	
	/**
	 * Returns the NPC Speech Frame
	 * @return
	 */
	public NPCSpeechFrame getNPCSpeech(){
		return m_speechFrame;
	}
	
	/**
	 * Pops up the trade dialog
	 * @param pokes
	 * @param player
	 */
	public void openTrade(int[] pokes, String player){
		new TradeDialog(pokes, player);
	}
}
