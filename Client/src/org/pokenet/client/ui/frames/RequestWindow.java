package org.pokenet.client.ui.frames;

import java.util.ArrayList;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.OurPokemon;

/**
 * Request window
 * @author ZombieBear
 *
 */
public class RequestWindow extends Frame {
    ArrayList<Label> m_playerInfo = new ArrayList<Label>();
    ArrayList<Button> m_tradeButtons = new ArrayList<Button>();
    ArrayList<Button> m_battleButtons = new ArrayList<Button>();
   
    ArrayList<Offer> m_offers = new ArrayList<Offer>();
    public String m_tradeOfferInfo, m_trader;
    OurPokemon [] m_party = new OurPokemon[6];
    Container m_current, m_otherOffers;
    Label m_amount, m_status, m_c1, m_c2, m_you, m_them, m_selectedPoke;
    public Button m_tradeCancel, m_tradeAccept, m_tradeDecline, m_tradeSend, m_incPokeIndex, m_decPokeIndex, m_incAmount, m_decAmount;
    int m_pokeIndex;
    int m_pokedollars;
   
    /**
     * Default constructor
     */
    public RequestWindow() {
            m_pokeIndex = 0;
            m_pokedollars = 0;
            initGUI();
    }
   
    /**
     * Sets personal pokemon data
     * @param pokes
     */
    public void setPokeData(OurPokemon[] pokes) {
            m_party = pokes;
    }
   
    /**
     * Adds a trade request
     * @param username
     */
    public void addTradeRequest(String username) {
            String s = "tk" + username.toString();
            Offer r = new Offer(username);
            if(m_offers.size() > 4) {
                    m_offers.remove(0);
                    m_offers.trimToSize();
            }
            m_offers.add(r);
            reloadRequests();
    }

    /**
     * Initializes the interface
     */
    public void initGUI() {
            this.getTitleBar().getCloseButton().setVisible(false);
            this.setTitle("PvP Battles/Trades");
            this.setBackground(new Color(0, 0, 0, 85));
            this.setForeground(new Color(255, 255, 255));
            this.setSize(210, 360);
            this.setLocation(0, 0);
            this.setResizable(false);
           
            m_current = new Container();
            m_current.setForeground(new Color(255, 255, 255));
            m_current.setBackground(new Color(0, 0, 0, 85));
            GameClient.getInstance();
			m_current.setFont(GameClient.getFontSmall());
            m_current.setBounds(2, 16, 240, 174);
            this.add(m_current);
           
            m_otherOffers = new Container();
            m_otherOffers.setForeground(new Color(255, 255, 255));
            m_otherOffers.setBackground(new Color(0, 0, 0, 85));
            GameClient.getInstance();
			m_otherOffers.setFont(GameClient.getFontSmall());
            m_otherOffers.setBounds(2, 182, 240, 174);
            this.add(m_otherOffers);
           
            m_tradeCancel = new Button();
            m_tradeCancel.setText("Cancel");
            m_tradeCancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            GameClient.getInstance().getPacketGenerator().write("tc");
                            loadRequestInterface();
                    }
            });
            m_tradeCancel.setSize(64, 32);
            m_tradeCancel.setLocation(120, 114);
            m_tradeCancel.pack();
           
            m_tradeAccept = new Button();
            m_tradeAccept.setText("Accept");
            m_tradeAccept.setSize(64, 32);
            m_tradeAccept.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            GameClient.getInstance().getPacketGenerator().write("tk");
                                    m_tradeAccept.setEnabled(false);
                                    m_tradeDecline.setEnabled(false);
                    }
            });
            m_tradeAccept.setLocation(4, 114);
            m_tradeAccept.setEnabled(false);
            m_tradeAccept.pack();
           
            m_tradeDecline = new Button();
            m_tradeDecline.setText("Decline");
            m_tradeDecline.setSize(64, 32);
            m_tradeDecline.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            GameClient.getInstance().getPacketGenerator().write("tc");
                            loadRequestInterface();
                    }
            });
            m_tradeDecline.setEnabled(false);
            m_tradeDecline.setLocation(60, 114);
            m_tradeDecline.pack();
           
            m_tradeSend = new Button();
            m_tradeSend.setText("Send Offer");
            m_tradeSend.setSize(64, 32);
            m_tradeSend.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            GameClient.getInstance().getPacketGenerator().write("to" + "," + m_pokedollars + "," + (m_pokeIndex + 1));
                            m_status.setText("Waiting...");
                            m_tradeSend.setEnabled(false);
                    }
            });
            m_tradeSend.setLocation(4, m_tradeAccept.getY() - 25);
            m_tradeSend.pack();
           
            m_you = new Label();
            m_you.setText("Your Offer:");
            m_you.setLocation(4, 4);
            m_you.setFont(GameClient.getFontSmall());
            m_you.setForeground(new Color(255, 255, 255));
            m_you.pack();
           
            m_them = new Label();
            m_them.setText("Their Offer:");
            m_them.setLocation(88, 4);
            m_them.setFont(GameClient.getFontSmall());
            m_them.setForeground(new Color(255, 255, 255));
            m_them.pack();
           
            m_amount = new Label();
            m_amount.setText("PD: " + m_pokedollars);
            m_amount.setLocation(48, 114);
            m_amount.setFont(GameClient.getFontSmall());
            m_amount.setForeground(new Color(255, 255, 255));
            m_amount.pack();
            m_current.add(m_amount);
           
            m_incAmount = new Button();
            m_incAmount.setSize(8, 8);
            m_incAmount.setLocation(16, 114);
            m_incAmount.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            m_pokedollars = m_pokedollars + 5;
                            updateAmountLabel();
                    }
            });
            m_incAmount.pack();
            m_current.add(m_incAmount);
           
            m_decAmount = new Button();
            m_decAmount.setSize(8, 8);
            m_decAmount.setLocation(16, 124);
            m_decAmount.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            if(m_pokedollars > 0) {
                                    m_pokedollars = m_pokedollars - 5;
                                    updateAmountLabel();    
                            }
                    }
            });
            m_decAmount.pack();
            m_current.add(m_decAmount);
           
            m_incPokeIndex = new Button();
            m_incPokeIndex.setSize(8, 8);
            m_incPokeIndex.setLocation(4, 24);
            m_incPokeIndex.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            if(m_pokeIndex < 5 && m_party[m_pokeIndex + 1] != null) {
                                    m_pokeIndex++;
                                    updatePokeLabel();
                            }
                    }
            });
            m_incPokeIndex.pack();
           
            m_decPokeIndex = new Button();
            m_decPokeIndex.setSize(8, 8);
            m_decPokeIndex.setLocation(4, 32);
            m_decPokeIndex.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            if(m_pokeIndex > -1) {
                                    m_pokeIndex--;    
                                    if(m_pokeIndex == -1) {
                                            m_selectedPoke.setText("Money Only");
                                    }
                                    else {
                                            updatePokeLabel();
                                    }
                            }
                    }
            });
            m_decPokeIndex.pack();
           
            m_selectedPoke = new Label();
            m_selectedPoke.setText("test");
            m_selectedPoke.setForeground(new Color(255, 255, 255));
            m_selectedPoke.setFont(GameClient.getFontSmall());
            m_selectedPoke.setLocation(20, 48);
            m_selectedPoke.pack();
           
            m_status = new Label();
            m_status.setText("Waiting...");
            m_status.setForeground(new Color(255, 255, 255));
            m_status.setFont(GameClient.getFontSmall());
            m_status.setLocation(94, 24);
            m_status.pack();
           
            m_c1 = new Label();
            m_c1.setText("Send a Request");
            m_c1.setFont(GameClient.getFontSmall());
            m_c1.setForeground(new Color(255, 255, 255));
            m_c1.setLocation(2,2);
            m_c1.pack();
            this.add(m_c1);
           
            m_c2 = new Label();
            m_c2.setText("Requests Received");
            m_c2.setFont(GameClient.getFontSmall());
            m_c2.setForeground(new Color(255, 255, 255));
            m_c2.setLocation(2, 162);
            m_c2.pack();
            this.add(m_c2);
    }
   
    /**
     * Displays a trade offer
     * @param pokedollars2
     * @param requestedPokemon
     */
    public void showOffer(int pokedollars2, OurPokemon requestedPokemon) {
            getDisplay().add(new PokeInfoPane(requestedPokemon));
            m_status.setText("PD: " + pokedollars2);
            m_tradeAccept.setEnabled(true);
            m_tradeDecline.setEnabled(true);
    }
    
    /**
     * Loads the request pane
     */
    public void loadRequestInterface() {
            m_current.removeAll();
            m_incAmount.setLocation(16, 114);
            m_incAmount.pack();
            m_current.add(m_incAmount);
           
            m_decAmount.setLocation(16, 124);
            m_decAmount.pack();
            m_current.add(m_decAmount);
           
            m_amount.setLocation(48, 114);
            m_amount.pack();
            m_current.add(m_amount);
            for(int i = 0; i < m_playerInfo.size(); i++) {
                    m_playerInfo.get(i).setLocation(2, (32 * i) + 4);
                    m_playerInfo.get(i).setVisible(true);
                    m_playerInfo.get(i).pack();
                    m_current.add(m_playerInfo.get(i));
                   
                    m_tradeButtons.get(i).setLocation(110, (32 * i));
                    m_tradeButtons.get(i).setVisible(true);
                    m_tradeButtons.get(i).pack();
                    m_current.add(m_tradeButtons.get(i));
                   
                    m_battleButtons.get(i).setLocation(156, (32 * i));
                    m_battleButtons.get(i).setVisible(true);
                    m_battleButtons.get(i).pack();
                    m_current.add(m_battleButtons.get(i));
            }
    }
   
    /**
     * Updates the money offer
     */
    public void updateAmountLabel() {
            m_amount.setText("PD: " + m_pokedollars);
    }
   
    /***
     * Updates the pokemon offer
     */
    public void updatePokeLabel() {
            m_selectedPoke.setText(m_party[m_pokeIndex].getName());
    }
   
    /**
     * Enables trade
     */
    public void enableTrade() {
            m_tradeAccept.setEnabled(true);
            m_tradeDecline.setEnabled(true);
            m_tradeSend.setEnabled(true);
    }
   
    
    /**
     * Loads the trade interface
     */
    public void loadTradeInterface() {
            m_c1.setText("Trade : " + m_trader);
            m_current.removeAll();
           
            m_current.add(m_you);
            m_current.add(m_them);
            m_current.add(m_tradeCancel);
            m_current.add(m_tradeAccept);
            m_current.add(m_tradeDecline);
            m_current.add(m_tradeSend);
            m_incAmount.setLocation(86, m_tradeAccept.getY() - 25);
            m_current.add(m_incAmount);
            m_decAmount.setLocation(86, m_tradeAccept.getY() - 16);
            m_current.add(m_decAmount);
            m_amount.setLocation(112, m_tradeAccept.getY() - 25);
            m_current.add(m_amount);
            m_current.add(m_status);
            m_current.add(m_incPokeIndex);
            m_current.add(m_decPokeIndex);
            if(m_pokeIndex != -1 && m_party[m_pokeIndex] != null) {
                    m_selectedPoke.setText(m_party[m_pokeIndex].getName());
            }
            else {
                    m_selectedPoke.setText("Money only");
            }
            m_current.add(m_selectedPoke);
    }
   
    /**
     * Reloads the requests
     */
    public void reloadRequests() {
            m_c1.setText("Send a Request");
            m_otherOffers.removeAll();
            for(int i = 0; i < m_offers.size(); i++) {
                    m_offers.get(i).getUserLabel().setLocation(0, (48 * i) + 4);
                    m_offers.get(i).getUserLabel().pack();
                    m_otherOffers.add(m_offers.get(i).getUserLabel());
                   
                    m_offers.get(i).getButton().setLocation(124, (48 * i));
                    m_offers.get(i).getButton().pack();
                    m_otherOffers.add(m_offers.get(i).getButton());
                   
                    m_offers.get(i).getDetails().setLocation(0, (48 * i) + 24);
                    m_offers.get(i).getDetails().pack();
                    m_otherOffers.add(m_offers.get(i).getDetails());
            }
    }
   
    /**
     * Checks for trade/battle availability from a given player
     * @param username
     * @return
     */
    public boolean contains(String username) {
            boolean result = false;
            for(int i = 0; i < m_offers.size(); i++) {
                    if(m_offers.get(i).getUsername().equalsIgnoreCase(username)) {
                            result = true;
                            break;
                    }
            }
            for(int i = 0; i < m_playerInfo.size(); i++) {
                    if(m_playerInfo.get(i).getText().substring(0, m_playerInfo.get(i).getText().indexOf(" L:")) == username) {
                            result = true;
                            break;
                    }
            }
            return result;
    }
   
    /**
     * Removes offers from a given player
     * @param username
     */
    public void remove(String username) {
            for(int i = 0; i < m_offers.size(); i++) {
                    if(m_offers.get(i).getUsername().equalsIgnoreCase(username)) {
                            m_offers.remove(i);
                            m_offers.trimToSize();
                            reloadRequests();
                    }
            }
            for(int i = 0; i < m_playerInfo.size(); i++) {
                    if(m_playerInfo.get(i).getText().substring(0, m_playerInfo.get(i).getText().indexOf(" L:")) == username) {
                            m_playerInfo.remove(i);
                            m_playerInfo.trimToSize();
                            m_tradeButtons.remove(i);
                            m_tradeButtons.trimToSize();
                            m_battleButtons.remove(i);
                            m_battleButtons.trimToSize();
                            loadRequestInterface();
                    }
            }
    }
   
    /**
     * Clears the trade offer
     */
    public void clear() {
            m_playerInfo.clear();
            m_tradeButtons.clear();
            m_battleButtons.clear();
            m_offers.clear();
            m_current.removeAll();
            m_incAmount.setLocation(16, 114);
            m_incAmount.pack();
            m_current.add(m_incAmount);

           
            m_decAmount.setLocation(16, 124);
            m_decAmount.pack();
            m_current.add(m_decAmount);
           
            m_amount.setLocation(48, 114);
            m_amount.pack();
            m_current.add(m_amount);
            m_otherOffers.removeAll();
    }
   
    
    /**
     * Adds a request
     * @param username
     * @param request
     */
    public void addRequest(final String username, String request) {
            if(request.charAt(0) == 'f') {
                    System.out.println(username + " " + request);
                    String s = "c" + username.toString() + "," + request.substring(request.indexOf(',') + 1);
                    Offer r = new Offer(username, s, request.substring(1, request.indexOf(',')));
                    if(m_offers.size() > 4) {
                            m_offers.remove(0);
                            m_offers.trimToSize();
                    }
                    m_offers.add(r);
                    reloadRequests();
            }
            else if(request.charAt(0) == 'a') {
                            Label l = new Label();
                            if(username.length() > 7)
                                    l.setText(username.substring(0, 8) + " L: " + request.substring(1));
                            else
                                    l.setText(username + " L: " + request.substring(1));
                            l.setFont(GameClient.getFontSmall());
                            l.setForeground(new Color(255, 255, 255));
                            l.pack();
                            if(m_playerInfo.size() > 4) {
                                    m_playerInfo.remove(0);
                                    m_playerInfo.trimToSize();
                            }
                            m_playerInfo.add(l);
                           
                            Button b = new Button();
                            b.setText("Battle");
                            b.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent evt) {
                                            GameClient.getInstance().getPacketGenerator().write("c" + username.toString() + "," + m_pokedollars);
                                            setEnabled(false);
                                    }
                            });
                            b.pack();
                            if(m_battleButtons.size() > 4) {
                                    m_battleButtons.remove(0);
                                    m_battleButtons.trimToSize();
                            }
                            m_battleButtons.add(b);
                           
                            Button t = new Button();
                            t.setText("Trade");
                            t.setToolTipText(new String(username));
                            t.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent evt) {
                                            m_trader = username;
                                            GameClient.getInstance().getPacketGenerator().write("tb" + username);
                                            setEnabled(false);
                                    }
                            });
                            t.pack();
                            if(m_tradeButtons.size() > 4) {
                                    m_tradeButtons.remove(0);
                                    m_tradeButtons.trimToSize();
                            }
                            m_tradeButtons.add(t);
                           
                            loadRequestInterface();
            }
    }

    /**
     * Shows a trade offer
     * @param pokedollars2
     */
    public void showOffer(int pokedollars2) {
            m_status.setText("PD: " + pokedollars2);
            m_tradeAccept.setEnabled(true);
            m_tradeDecline.setEnabled(true);
    }
}


/**
 * Trade/Battle offer object
 * @author ZombieBear
 *
 */
class Offer {
    private String m_reqUsrname;
    private String m_offer;
    private Label m_userLabel;
    private Label m_details;
    private Button m_accept;
   
    /**
     * Default constructor
     * @param user
     */
    public Offer(String user) {
            m_reqUsrname = user;
           
            m_userLabel = new Label();
            m_userLabel.setText(m_reqUsrname);
            m_userLabel.setForeground(new Color(255, 255, 255));
            m_userLabel.setFont(GameClient.getFontSmall());
            m_userLabel.pack();
           
            m_details = new Label();
            m_details.setText("Trade");
            m_details.setForeground(new Color(255, 255, 255));
            m_details.setFont(GameClient.getFontSmall());
            m_details.pack();
           
            m_accept = new Button();
            m_accept.setText("Accept");
            m_accept.setSize(64, 32);
            m_accept.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            GameClient.getInstance().getPacketGenerator().write("tb" + m_reqUsrname);
                    }
            });
            m_accept.pack();
    }
   
    /**
     * Constructor
     * @param user
     * @param request
     * @param level
     */
    public Offer(String user, String request, String level) {
            m_reqUsrname = user;
            m_offer = request;
            System.out.println(m_reqUsrname + " " + m_offer);
           
            m_userLabel = new Label();
            m_userLabel.setText(m_reqUsrname + " L: " + level);
            m_userLabel.setForeground(new Color(255, 255, 255));
            m_userLabel.setFont(GameClient.getFontSmall());
            m_userLabel.pack();
           
            m_details = new Label();
            m_details.setText("PvP Battle: " + m_offer.substring(m_offer.indexOf(',') + 1));
            m_details.setForeground(new Color(255, 255, 255));
            m_details.setFont(GameClient.getFontSmall());
            m_details.pack();
           
            m_accept = new Button();
            m_accept.setText("Accept");
            m_accept.setSize(64, 32);
            m_accept.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                            GameClient.getInstance().getPacketGenerator().write(m_offer);
                    }
            });
            m_accept.pack();
    }
   
    /**
     * Returns the offer
     * @return
     */
    public String getOffer() {
            return m_offer;
    }
   
    /**
     * Returns the username of the player who made the offer
     * @return
     */
    public String getUsername() {
            return m_reqUsrname;
    }
   
    /**
     * Returns the username label for the interface
     * @return
     */
    public Label getUserLabel() {
            return m_userLabel;
    }
   
    /**
     * Returns the details label for the interface
     * @return
     */
    public Label getDetails() {
            return m_details;
    }
   
    /***
     * Returns the offer's button for the interface
     * @return
     */
    public Button getButton() {
            return m_accept;
    }
}
