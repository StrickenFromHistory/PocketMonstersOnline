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
	private boolean m_update = false;
	
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
    	getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
    	List<String> translated = Translator.translate("_GUI");
        getTitleBar().getCloseButton().setVisible(false);
        setTitle(translated.get(33));
        setBackground(new Color(0, 0, 0, 85));
        setForeground(new Color(255, 255, 255));
        setHeight(getTitleBar().getHeight() + 25);
        m_noOffers.setFont(GameClient.getFontSmall());
        m_noOffers.setForeground(Color.white);
        m_noOffers.pack();
        m_noOffers.setY((float)10 - m_noOffers.getTextHeight() / 2);
        getContentPane().add(m_noOffers);
        setResizable(false);
    }
    
    /**
     * Adds a request
     * @param username
     * @param request
     */
    public void addRequest(final String username, String request) {
    	if(request.equalsIgnoreCase("trade")) {
    		//TRADE
    		if (!m_offerUser.contains(username)){
    			m_offerUser.add(username);
    			m_offers.put(username, new Button("Trade"));
    			m_update = true;
    		}
    		GameClient.getInstance().getUi().getChat().addSystemMessage("*" + username + 
    				" sent you a trade request.");
    	}
    	else if(request.equalsIgnoreCase("battle")) {
    		if (!m_offerUser.contains(username)){
    			m_offerUser.add(username);
    			m_offers.put(username, new Button("Battle"));
    			m_update = true;
    		}
    		GameClient.getInstance().getUi().getChat().addSystemMessage("*" + username + 
				" would like to battle!");
    	}
    }

    @Override
    public void update(GUIContext container, int delta) {
    	super.update(container, delta);
    	if (isVisible()) {
    		if (m_update){
    			m_update = false;
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
    					final Button m_offerBtn = m_offers.get(m_offerUser.get(i)); 
    					final Button m_cancel = new Button("Cancel");
    					m_cancel.setHeight(25);
    					m_cancel.setWidth(45);
    					m_cancel.addActionListener(new ActionListener(){
    						public void actionPerformed(ActionEvent e) {
    							declineOffer(j);
    						}
    					});
    					m_label.setFont(GameClient.getFontSmall());
    					m_label.setForeground(Color.white);
    					m_label.pack();
    					m_label.setY((float)10 - m_label.getTextHeight() / 2);
    					m_offerBtn.setHeight(25);
    					m_offerBtn.setX(getWidth() - 92);
    					m_offerBtn.setWidth(45);
    					m_offerBtn.addActionListener(new ActionListener(){
    						public void actionPerformed(ActionEvent e) {
    							acceptOffer(j);
    						}
    					});
    					m_containers.add(new Container());
    					m_containers.get(i).setSize(getWidth(), 25);
    					m_containers.get(i).setLocation(0, y);
    					m_containers.get(i).add(m_label);
    					m_containers.get(i).add(m_offerBtn);
    					m_containers.get(i).add(m_cancel);
    					m_cancel.setX(getWidth() - 47);
    					getContentPane().add(m_containers.get(i));
    					y += 25;
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
    	if(m_offerUser != null && m_offerUser.size() > 0) {
        	GameClient.getInstance().getPacketGenerator().writeTcpMessage("ra" + m_offerUser.get(userIndex));
        	m_offers.remove(m_offerUser.get(userIndex));
        	m_offerUser.remove(userIndex);
        	m_update = true;
    	}
    }
    
    /**
     * And offer was declined
     * @param userIndex
     */
    public void declineOffer(int userIndex) {
    	GameClient.getInstance().getPacketGenerator().writeTcpMessage("rc" + m_offerUser.get(userIndex));
    	m_offers.remove(m_offerUser.get(userIndex));
    	m_offerUser.remove(userIndex);
    	m_update = true;
    }
    
    /**
     * Removes an offer
     * @param username
     */
    public void removeOffer(String username) {
    	if (m_offerUser.contains(username)){
    		m_offers.remove(username);
    		m_offerUser.remove(username);
    		m_update = true;
    	}
    }
    
    /**
     * Clears all offers
     */
    public void clearOffers(){
    	for (String name : m_offerUser) {
    		GameClient.getInstance().getPacketGenerator().writeTcpMessage("rc" + name);
    		m_offers.remove(name);
    		m_offerUser.remove(name);
    		m_update = true;
    	}
    }
}
