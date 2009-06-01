package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.gui.GUIContext;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.Translator;

/**
 * Request dialog
 * @author ZombieBear
 *
 */
public class RequestDialog extends Frame{
	private HashMap<String, Button> m_offers = new HashMap<String, Button>();
	private Label m_noOffers = new Label("There are no offers");
	private List<Container> m_containers = new ArrayList<Container>();
	private List<String> m_offerUser = new ArrayList<String>();
	
    /**
     * Default Constructor
     */
    public RequestDialog(){
    	initGUI();
    }
    
    /**
     * Initializes the user interface
     */
    public void initGUI(){
    	List<String> translated = Translator.translate("_GUI");
        getTitleBar().getCloseButton().setVisible(false);
        setTitle(translated.get(33));
        setBackground(new Color(0, 0, 0, 85));
        setForeground(new Color(255, 255, 255));
        setHeight(getTitleBar().getHeight() + 25);
        m_noOffers.setFont(GameClient.getFontSmall());
        m_noOffers.setForeground(Color.white);
        m_noOffers.pack();
        getContentPane().add(m_noOffers);
        setResizable(false);
    }
    
    /**
     * Adds a request
     * @param username
     * @param request
     */
    public void addRequest(final String username, String request) {
    	if(request.charAt(0) == 'f') {
    		//TRADE
    		m_offerUser.add(username);
    		m_offers.put(username, new Button("Trade"));
    	}
    	else if(request.charAt(0) == 'a') {
    		m_offerUser.add(username);
    		m_offers.put(username, new Button("Battle"));
    	}
    }

    @Override
    public void update(GUIContext container, int delta) {
    	super.update(container, delta);
    	if (isVisible()) {
    		if (m_offerUser.size() != m_containers.size()){
    			for (int i = 0; i < m_containers.size(); i++) {
    				m_containers.get(i).removeAll();
    				try{
    					getContentPane().remove(m_containers.get(i));
    				} catch (Exception e) {}
    			}

    			if (m_offerUser.size() == 0){
    				setHeight(getTitleBar().getHeight() + 25);
    				m_containers.clear();
    				getContentPane().add(m_noOffers);
    			} else {
    				int y = 0;
    				if (getContentPane().containsChild(m_noOffers)){
    					getContentPane().remove(m_noOffers);
    				}
    				setHeight(getTitleBar().getHeight() + 25 * m_offers.size());
    				m_containers.clear();
    				for (int i = 0; i < m_offers.size(); i++) {
    					final int j = i;
    					final Label m_label = new Label(m_offerUser.get(i));
    					final Button m_cancel = new Button("Cancel");
    					m_cancel.pack();
    					m_cancel.setWidth(30);
    					m_offers.get(m_offerUser.get(i)).addActionListener(new ActionListener(){
    						public void actionPerformed(ActionEvent e) {
    							declineOffer(j);
    						}
    					});
    					m_label.setFont(GameClient.getFontSmall());
    					m_label.setForeground(Color.white);
    					m_label.pack();
    					m_offers.get(m_offerUser.get(i)).pack();
    					m_offers.get(m_offerUser.get(i)).setWidth(30);
    					m_offers.get(m_offerUser.get(i)).addActionListener(new ActionListener(){
    						public void actionPerformed(ActionEvent e) {
    							acceptOffer(j);
    						}
    					});
    					m_containers.add(new Container());
    					m_containers.get(i).setSize(getWidth(), 25);
    					m_containers.get(i).setLocation(0, y);
    					m_containers.get(i).add(m_label);
    					m_containers.add(m_offers.get(m_offerUser.get(i)));
    					m_offers.get(m_offerUser.get(i)).setX(getWidth() - 65);
    					m_cancel.setX(getWidth() - 32);
    					getContentPane().add(m_containers.get(i));
    				}
    			}
    		}
    	}
    }

    /**
     * An offer was accepted
     * @param userIndex
     */
    public void acceptOffer(int userIndex) {
    	//TODO: Add Packet: GameClient.getInstance().getPacketGenerator().write();
    	m_offers.remove(m_offerUser.get(userIndex));
    	m_offerUser.remove(userIndex);
    }
    
    /**
     * And offer was declined
     * @param userIndex
     */
    public void declineOffer(int userIndex) {
    	//TODO: Add Packet: GameClient.getInstance().getPacketGenerator().write();
    	m_offers.remove(m_offerUser.get(userIndex));
    	m_offerUser.remove(userIndex);
    }
    
    /**
     * Removes an offer
     * @param username
     */
    public void removeOffer(String username) {
    	m_offers.remove(username);
   		m_offerUser.remove(username);
    }
}
