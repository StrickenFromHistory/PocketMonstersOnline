package org.pokenet.client.ui;

import mdes.slick.sui.Display;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.BattleManager;
import org.pokenet.client.backend.entity.PlayerItem;
import org.pokenet.client.ui.base.HUDButtonFactory;
import org.pokenet.client.ui.base.ImageButton;
import org.pokenet.client.ui.base.ListBox;
import org.pokenet.client.ui.frames.BagDialog;
import org.pokenet.client.ui.frames.BigBagDialog;
import org.pokenet.client.ui.frames.ChatDialog;
import org.pokenet.client.ui.frames.FriendListDialog;
import org.pokenet.client.ui.frames.HelpWindow;
import org.pokenet.client.ui.frames.NPCSpeechFrame;
import org.pokenet.client.ui.frames.OptionsDialog;
import org.pokenet.client.ui.frames.PartyInfoDialog;
import org.pokenet.client.ui.frames.PokeStorageBoxFrame;
import org.pokenet.client.ui.frames.RequestDialog;
import org.pokenet.client.ui.frames.TownMap;
import org.pokenet.client.ui.frames.TradeDialog;

/**
 * The main ui on screen
 * @author shadowkanji
 * @author ZombieBear
 *
 */
public class Ui extends Frame {
	private FriendListDialog m_friendList;
	private ChatDialog m_chat;
	private ImageButton [] m_buttons;
	private Display m_display;
	private Label m_moneyLabel = new Label();
    private OptionsDialog m_optionsForm;
    private RequestDialog m_requestsForm;
    private HelpWindow m_helpForm;
    private Frame m_bagForm;
    private PartyInfoDialog m_teamInfo;
	private NPCSpeechFrame m_speechFrame;
	private BattleManager m_battleManager;
	private PokeStorageBoxFrame m_storageBox;
	private TownMap m_map;
    private boolean m_isOption;
    private static final int UI_WIDTH = 32*7;
	
	/**
	 * Default constructor
	 */
	public Ui(Display display) {
		this.setSize(48, 293);
		this.setLocation(0, -24);
		this.setBackground(new Color(0, 0, 0, 75));
		this.setResizable(false);
		this.setDraggable(false);
		
		m_battleManager = new BattleManager();
		
		m_display = display;
		
		m_chat = new ChatDialog("Chat: Local");
		
		m_map = new TownMap();
		m_map.setVisible(false);
		m_display.add(m_map);
		
		startButtons();

		m_moneyLabel.setText("$");
		m_moneyLabel.pack();
		m_moneyLabel.setLocation(4, 242);
		m_moneyLabel.setVisible(true);
		m_moneyLabel.setFont(GameClient.getFontSmall());
		m_moneyLabel.setForeground(new Color(255, 255, 255));
		this.add(m_moneyLabel);
		
		this.add(GameClient.getInstance().getTimeService());
		
		this.getTitleBar().setVisible(false);

		m_chat.setLocation(GameClient.getInstance().getDisplay().getWidth()
				- m_chat.getWidth(), 0);
		m_display.add(m_chat);
		m_display.add(this);
	}
	
	/**
	 * Returns the map
	 * @return the map
	 */
	public TownMap getMap() {
		return m_map;
	}

	/**
	 * Starts the HUD buttons
	 */
	public void startButtons(){
		m_buttons = new ImageButton[6];
		
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
        m_buttons[5] = HUDButtonFactory.getButton("map");
        m_buttons[5].addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		toggleMap();
        	}
        });
        
        for (int i = 0; i < m_buttons.length; i++){
        	m_buttons[i].pack();
        	getContentPane().add(m_buttons[i]);
        	m_buttons[i].setLocation(7, 22 + (32 * i) + (5 * i));
        }
	}
	
	/**
	 * Adds a message to its appropriate chat window
	 * @param m
	 */
	public void messageReceived(String m) {
		switch(m.charAt(0)) {
		case 'n':
			//NPC Speeech
			String [] speech = m.substring(1).split(",");
			String result = "";
			for(int i = 0; i < speech.length; i++) {
				result += GameClient.getInstance().getMapMatrix().getSpeech(Integer.parseInt(speech[i])) + "/n";
			}
			try {
				talkToNPC(result);
			} catch (SlickException e) {
				e.printStackTrace();
			}
			break;
		case 'l':
			//Local Chat
			m_chat.addChatLine("Local", m.substring(1));
			break;
		case 'p':
			//Private Chat
			String [] details = m.substring(1).split(",");
			m_chat.addChatLine(details[0], details[1]);
			break;
		}
	}
	
	/**
	 * Sets all components visible/invisible
	 * @param b
	 */
	public void setAllVisible(boolean b) {
		this.setVisible(b);
		m_chat.setVisible(b);
	}
	
    /**
     * Updates the data
     * @param p
     */
    public void update(){
    	m_moneyLabel.setText("$" + String.valueOf(GameClient.getInstance()
    			.getOurPlayer().getMoney()));
    	m_moneyLabel.pack();
    	m_teamInfo.update(GameClient.getInstance().getOurPlayer().getPokemon());
    }
    
    /**
     * Returns true if a pane is being shown
     * @return true if a pane is being shown
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
     * @return the options form
     */
    public OptionsDialog getOptionPane() {
            return m_optionsForm;
    }
    
    /**
     * Returns the request window
     * @return the request window
     */
    public RequestDialog getReqWindow() {
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
			m_requestsForm = new RequestDialog();
			m_requestsForm.setWidth(UI_WIDTH);
			m_requestsForm.setLocation(48, 0);
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
			m_bagForm.setTitle("     Bag");
			BagDialog pane = new BagDialog(
					GameClient.getInstance().getOurPlayer().getItems()) {
				public void itemClicked(PlayerItem item) {
					GameClient.getInstance().getPacketGenerator().write("u" + 
							item.getItem().getName());
				}
				public void cancelled() {
					getDisplay().remove(m_bagForm);
					m_bagForm = null;
				}
				public void loadBag() {
					getDisplay().remove(m_bagForm);
					m_bagForm = null;
					BigBagDialog bbg = new BigBagDialog();
					bbg.initGUI();
					getDisplay().add(bbg);
				}
			};
			pane.setSize(80, 246);
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
			m_teamInfo = new PartyInfoDialog(GameClient.getInstance().getOurPlayer()
					.getPokemon());
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
     * Toggles the Help Pane
     */
    public void toggleMap(){
    	if (m_map.isVisible()) {
			m_map.setVisible(false);
			hideHUD();
		} else {
			hideHUD();
			m_map.setLocation(48, 0);
			m_map.setVisible(true);
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
            if (m_map.isVisible()) m_map.setVisible(false);
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
	 */
	public void nullSpeechFrame() {
		getDisplay().remove(m_speechFrame);
		m_speechFrame = null;
	}
	
	/**
	 * Returns the NPC Speech Frame
	 * @return the NPC Speech Frame
	 */
	public NPCSpeechFrame getNPCSpeech(){
		return m_speechFrame;
	}

	/**
	 * Returns the Chat Dialog
	 * @return the Chat Dialog
	 */
	public ChatDialog getChat(){
		return m_chat;
	}	
	
	/**
	 * Pops up the trade dialog
	 * @param pokes
	 * @param player
	 */
	public void openTrade(int[] pokes, String player){
		new TradeDialog(pokes, player);
	}
	
    
    /**
     * Returns the Battle Manager
     * @return the Battle Manager
     */
    public BattleManager getBattleManager(){
    	return m_battleManager;
    }
    
    /**
     * Starts a Storage Box
     */
    public void useStorageBox(int[] data){
    	m_storageBox = new PokeStorageBoxFrame(data);
    	getDisplay().add(m_storageBox);
    }
    
    /**
     * Stops the Storage Box
     */
    public void stopUsingBox(){
    	getDisplay().remove(m_storageBox);
    	m_storageBox = null;
    }
    
    /**
     * Returns the Storage Box
     * @return the Storage Box
     */
    public PokeStorageBoxFrame getStorageBox(){
    	return m_storageBox;
    }

	/**
	 * Returns the Friends List
	 * @return the Friends List
	 */
	public FriendListDialog getFriendsList() {
		return m_friendList;
	}
	
	/**
	 * Sets the Friends List
	 * @param friends
	 */
	public void setFriendsList(String[] friends){
		m_friendList = new FriendListDialog(friends);
	}
	
	/**
     * Refreshes PokemonParty HUD
     */
    public void refreshParty(){
    	m_teamInfo.update(GameClient.getInstance().getOurPlayer().getPokemon());
    }
}
