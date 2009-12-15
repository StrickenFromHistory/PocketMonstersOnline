package org.pokenet.client.ui.frames;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Image;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.FileLoader;
import org.pokenet.client.backend.entity.PlayerItem;

/**
 * Bag dialog
 * @author ZombieBear
 * @author Nushio
 *
 */
public abstract class BagDialog extends Container {
        private Button[] m_itemButtons;
        private Button m_bag;
        private Button m_cancel;
        Container[] m_container;
        
        private List<PlayerItem> m_items;

        /**
         * Default Constructor
         * @param bag
         */
        public BagDialog(ArrayList<PlayerItem> bag) {
        	m_items = new ArrayList<PlayerItem>();
        	//Assign Potion Fave
        	if(GameClient.getInstance().getOurPlayer().getItemQuantity(4)>0){
        		m_items.add(new PlayerItem(4,GameClient.getInstance().getOurPlayer().getItemQuantity(4)));
        	}else if(GameClient.getInstance().getOurPlayer().getItemQuantity(3)>0){
        		m_items.add(new PlayerItem(3,GameClient.getInstance().getOurPlayer().getItemQuantity(3)));
        	}else if(GameClient.getInstance().getOurPlayer().getItemQuantity(2)>0){
        		m_items.add(new PlayerItem(2,GameClient.getInstance().getOurPlayer().getItemQuantity(2)));
        	}else {
        		m_items.add(new PlayerItem(1,GameClient.getInstance().getOurPlayer().getItemQuantity(1)));
        	}
        	
        	//Assign Antidote Fave
        	if(GameClient.getInstance().getOurPlayer().getItemQuantity(5)>0){
        		m_items.add(new PlayerItem(5,GameClient.getInstance().getOurPlayer().getItemQuantity(5)));
        	}else if(GameClient.getInstance().getOurPlayer().getItemQuantity(21)>0){
        		m_items.add(new PlayerItem(21,GameClient.getInstance().getOurPlayer().getItemQuantity(21)));
        	}else {
        		m_items.add(new PlayerItem(16,GameClient.getInstance().getOurPlayer().getItemQuantity(16)));
        	}
        	
        	//Assign Repel Fave
        	if(GameClient.getInstance().getOurPlayer().getItemQuantity(87)>0){
        		m_items.add(new PlayerItem(87,GameClient.getInstance().getOurPlayer().getItemQuantity(87)));
        	}else if(GameClient.getInstance().getOurPlayer().getItemQuantity(86)>0){
        		m_items.add(new PlayerItem(86,GameClient.getInstance().getOurPlayer().getItemQuantity(86)));
        	}else {
        		m_items.add(new PlayerItem(85,GameClient.getInstance().getOurPlayer().getItemQuantity(85)));
        	}
        	
        	//Assign EscapeRope Fave
        	m_items.add(new PlayerItem(91,GameClient.getInstance().getOurPlayer().getItemQuantity(91)));
        	
        	//Assign PokeBall Fave
        	if(GameClient.getInstance().getOurPlayer().getItemQuantity(38)>0){
        		m_items.add(new PlayerItem(38,GameClient.getInstance().getOurPlayer().getItemQuantity(38)));
        	}else if(GameClient.getInstance().getOurPlayer().getItemQuantity(37)>0){
        		m_items.add(new PlayerItem(37,GameClient.getInstance().getOurPlayer().getItemQuantity(37)));
        	}else if(GameClient.getInstance().getOurPlayer().getItemQuantity(36)>0){
        		m_items.add(new PlayerItem(36,GameClient.getInstance().getOurPlayer().getItemQuantity(36)));
        	}else {
        		m_items.add(new PlayerItem(35,GameClient.getInstance().getOurPlayer().getItemQuantity(35)));
        	}
        
        	initGUI();
        }
        
        /**
         * Handles events on click
         * @param item
         */
        public abstract void itemClicked(PlayerItem item);
        
        /**
         * Initializes the interface
         */
        public void initGUI() {
        	InputStream f;
        	Label[] m_itemIcon = new Label[m_items.size()];
        	m_itemButtons = new Button[m_items.size()];
        	String respath = System.getProperty("res.path");
    		if(respath==null)
    			respath="";
        	for (int i = 0; i < m_items.size(); i++) {
        		final int j = i;
        		m_itemButtons[i] = new Button("       x" + m_items.get(i).getQuantity());
        		m_itemButtons[i].setToolTipText(m_items.get(i).getItem().getName()+"\n"+m_items.get(i).getItem().getDescription());
        		LoadingList.setDeferredLoading(true);
        		try {
        			m_itemIcon[i] = new Label();
            		m_itemIcon[i].setSize(32, 32);
            		f = FileLoader.loadFile(respath+"res/items/24/" + m_items.get(i).getNumber() + ".png");
        			m_itemIcon[i].setImage(new Image(f, respath+"res/items/24/" + m_items.get(i).getNumber() + ".png", false));
        			m_itemIcon[i].setGlassPane(true);
        			m_itemIcon[i].setToolTipText(m_items.get(i).getItem().getName()+"\n"+m_items.get(i).getItem().getDescription());
					m_itemButtons[i].add(m_itemIcon[i]);
				} catch (Exception e1) {
//					e1.printStackTrace();
				}
				LoadingList.setDeferredLoading(false);
        		m_itemButtons[i].addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent e) {
        				itemClicked(m_items.get(j));
        			}
        		});
        		add(m_itemButtons[i]);
        	}
        	m_bag = new Button("Bag");
        	m_bag.setToolTipText("Opens the Bag to see your items");
        	m_bag.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			loadBag();
        		}
        	});
        	add(m_bag);
        	
        	m_cancel = new Button("Cancel");
        	m_cancel.setToolTipText("Closes this dialog");
        	m_cancel.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			cancelled();
        		}
        	});
        	add(m_cancel);
        	pack();
        	setVisible(true);
        }

        /**
         * Handles cancelation
         */
        public abstract void cancelled();
        
        /**
         * Handles loading Big Bag
         */
        public abstract void loadBag();
        
        /**
         * Resizes items for optimal size
         */
        public void pack() {
        	
                m_cancel.setWidth(getWidth());
                m_cancel.setHeight(20);
                m_cancel.setY(getHeight() - 20);
                m_cancel.setX(0);
                m_bag.setWidth(getWidth());
            	m_bag.setHeight(40);
            	m_bag.setX(0);
            	m_bag.setY(m_cancel.getY()-40);
                for (int i = 0; i < m_itemButtons.length; i++) {
                        if (i > 0)
                                m_itemButtons[i].setY(m_itemButtons[i-1].getY()
                                                + m_itemButtons[i-1].getHeight());
                        m_itemButtons[i].setHeight((getHeight() - 60)/ m_items.size());
                        m_itemButtons[i].setWidth(getWidth());
                }
        }
}
