package org.pokenet.client.ui.frames;


import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Item;
import org.pokenet.client.backend.entity.OurPlayer;
import org.pokenet.client.network.PacketGenerator;
import org.pokenet.client.ui.base.HUDButtonFactory;
import org.pokenet.client.ui.base.ListBox;

/**
 * HUD
 * @author ZombieBear
 *
 */
public class MainHUD extends Frame {
		private Label m_moneyLabel = new Label();
        private Button m_bag;
        private Button m_help;
        private Button m_options;
        private Button m_requests;
        private Button m_party;
        private OptionsDialog m_optionsForm;
        private RequestWindow m_requestsForm;
        private HelpWindow m_helpForm;
        private Frame m_bagForm;
        private PartyInfo m_teamInfo;
        private boolean m_isOption;
        private OurPlayer m_player;
        private static final int UI_WIDTH = 32*7;
        private PacketGenerator m_packetGen;
        
        /**
         * Default constructor
         * @param ourPlayer
         * @param out
         */
        public MainHUD(){
                m_player = GameClient.getInstance().getOurPlayer();
                m_packetGen = GameClient.getInstance().getPacketGenerator();
                initGUI();
        }
        
        /**
         * Initializes the interface
         */
        public void initGUI(){
                m_moneyLabel.setFont(GameClient.getFontSmall());
                m_moneyLabel.setText("$" + m_player.getMoney());
                m_moneyLabel.setLocation(170, 8);
                m_moneyLabel.pack();
                m_moneyLabel.setVisible(true);
                m_moneyLabel.setForeground(Color.white);
                
                m_requests = HUDButtonFactory.getButton("requests");
                m_requests.pack();
                m_requests.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                if (getDisplay().containsChild(m_requestsForm)) {
                                        getDisplay().remove(m_requestsForm);      
                                        hideHUD();
                                } else {
                                        hideHUD();
                                        m_requestsForm = new RequestWindow();
                                        m_requestsForm.setWidth(UI_WIDTH);
                                        m_requestsForm.setLocation(0, 32);
                                        m_requestsForm.setPokeData(m_player.getPokemon());
                                        getDisplay().add(m_requestsForm);
                                }
                        }
                });
                m_requests.setLocation(0, 0);
                getContentPane().add(m_requests);
                
                m_bag = HUDButtonFactory.getButton("pokegear");
                m_bag.pack();
                m_bag.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                
                                if (m_bagForm != null) {
                                        getDisplay().remove(m_bagForm);
                                        
                                        hideHUD();
                                } else {
                                        hideHUD();
                                        m_bagForm = new Frame();
                                        BagDialog pane = new BagDialog(
                                                        m_player.getItems()) {
                                                public void itemClicked(Item item) {
                                                        m_packetGen.write("u" + item.getName());
                                                }
                                                public void cancelled() {
                                                        m_bagForm.setVisible(false);
                                                }
                                        };
                                        pane.setSize(UI_WIDTH, 300);
                                        pane.pack();
                                
                                        ListBox badges = new ListBox(
                                                        m_player.getBadges());
                                        badges.setSize(UI_WIDTH, 200);
                                        badges.pack();
                                        m_bagForm.getTitleBar().getCloseButton().setVisible(false);
                                        m_bagForm.getContentPane().add(badges);
                                        m_bagForm.getContentPane().add(pane);
                                        badges.setLocation(0, 300);
                                        m_bagForm.setSize(pane.getWidth(), 
                                                        pane.getHeight() + badges.getHeight() + m_bagForm.getTitleBar().getHeight());
                                        getDisplay().add(m_bagForm);
                                        m_bagForm.setLocation(0, 32);
                                }
                                
                        }
                });
                m_bag.setLocation(32, 0);
                getContentPane().add(m_bag);
                
                m_party = HUDButtonFactory.getButton("pokemon");
                m_party.pack();
                m_party.setLocation(64, 0);
                getContentPane().add(m_party);
                m_party.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                
                                if (m_teamInfo != null) {
                                        getDisplay().remove(m_teamInfo);
                                        
                                        hideHUD();
                                } else {
                                        hideHUD();
                                
                                        m_teamInfo = new PartyInfo(m_player.getPokes());
                                        m_teamInfo.setWidth(UI_WIDTH);
                                        m_teamInfo.setLocation(0, 32);
                                        getDisplay().add(m_teamInfo);
                                }
                        }
                });
                
                m_options = HUDButtonFactory.getButton("options");
                m_options.pack();
                m_options.setLocation(96, 0);
                getContentPane().add(m_options);
                m_options.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                if (m_optionsForm != null) {
                                        m_isOption = false;
                                        getDisplay().remove(m_optionsForm);
                                        hideHUD();
                                } else {
                                        hideHUD();
                                        m_isOption = true;
                                        m_optionsForm = new OptionsDialog();
                                        m_optionsForm.setWidth(UI_WIDTH);
                                        m_optionsForm.setLocation(0, 32);
                                        getDisplay().add(m_optionsForm);
                                }
                        }
                });
                
                m_help = HUDButtonFactory.getButton("help");
                m_help.pack();
                m_help.setLocation(128, 0);
                getContentPane().add(m_help);
                m_help.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                                if (m_helpForm != null) { // if we're on help, then just hide all
                                        getDisplay().remove(m_helpForm);
                                        
                                        hideHUD();
                                } else {
                                        hideHUD();
                                
                                        m_helpForm = new HelpWindow();
                                        m_helpForm.setWidth(UI_WIDTH);
                                        m_helpForm.setLocation(0, 32);
                                        getDisplay().add(m_helpForm);
                                }
                        }
                });
                
                getContentPane().add(m_moneyLabel);
                this.getTitleBar().setVisible(false);
                this.setResizable(false);
                this.setWidth(UI_WIDTH);
                this.setHeight(32 + getTitleBar().getHeight());
        }
        
        /**
         * Updates the data
         * @param p
         */
        public void update(OurPlayer p){
                m_player = p;
                m_moneyLabel.setText("$" + p.getMoney());
                if (m_teamInfo != null) m_teamInfo.update(p.getPokes());
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
}
