package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
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
        public BagDialog(PlayerItem[] bag) {
        	//Lets Fake the Bag for now.
        	m_items = new ArrayList<PlayerItem>();
        	m_items.add(new PlayerItem(1,20));
        	m_items.add(new PlayerItem(2,20));
        	m_items.add(new PlayerItem(3,20));
        	m_items.add(new PlayerItem(4,20));
        	m_items.add(new PlayerItem(5,20));
//                 
//                Item potion1 = new Item(1,20);
//                Item potion2 = new Item(2,20);
//                Item potion3 = new Item(3,20);
//                Item potion4 = new Item(4,20);
//                Item potion5 = new Item(5,20);
//                Item[] bags = new Item[5];
//                bags[0] = potion1;
//                bags[1] = potion2;
//                bags[2] = potion3;
//                bags[3] = potion4;
//                bags[4] = potion5;
//                
////                for (Item s : bags) {
//                for(int i = 0;i < bags.length; i++){
//                	Item s = bags[i];
//                        if (m_items.containsValue(s.getNumber())) {
//                                int quantity = m_items.get(s.getNumber());
//                                m_items.remove(s);
//                                m_items.put(s, quantity);
//                        } else {
//                                if (s != null)
//                                        m_items.put(s, 1);
//                        }
//                }
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
        	Label[] m_itemIcon = new Label[m_items.size()];
        	m_itemButtons = new Button[m_items.size()];
        	for (int i = 0; i < m_items.size(); i++) {
        		final int j = i;
        		m_itemButtons[i] = new Button("       x" + m_items.get(i).getQuantity());
        		m_itemIcon[i] = new Label();
        		m_itemIcon[i].setSize(32, 32);
        		try {
        			LoadingList.setDeferredLoading(true);
        			Image itemImage = new Image("/res/items/" + m_items.get(i).getNumber() + ".png");
        			m_itemIcon[i].setImage(itemImage);
        			m_itemIcon[i].setGlassPane(true);
					LoadingList.setDeferredLoading(false);
					m_itemButtons[i].add(m_itemIcon[i]);
				} catch (SlickException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
				}
        		m_itemButtons[i].addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent e) {
        				itemClicked(m_items.get(j));
        			}
        		});
        		add(m_itemButtons[i]);
        	}
        	m_bag = new Button("Bag");
        	m_bag.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			loadBag();
        		}
        	});
        	add(m_bag);
        	
        	m_cancel = new Button("Cancel");
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
