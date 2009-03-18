package org.pokenet.client.ui.frames;

import java.util.HashMap;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.pokenet.client.backend.entity.Item;

/**
 * Bag dialog
 * @author ZombieBear
 *
 */
public abstract class BagDialog extends Container {
        private Button[] m_itemButtons;
        private Button m_cancel;

        private HashMap<Item, Integer> m_items;

        /**
         * Daefault Constructor
         * @param bag
         */
        public BagDialog(Item[] bag) {
                m_items = new HashMap<Item, Integer>();
                for (Item s : bag) {
                        if (m_items.containsKey(s)) {
                                int quantity = m_items.get(s) + 1;
                                m_items.remove(s);
                                m_items.put(s, quantity);
                        } else {
                                if (s != null)
                                        m_items.put(s, 1);
                        }
                }
                initGUI();
        }
        
        /**
         * Handles events on click
         * @param item
         */
        public abstract void itemClicked(Item item);
        
        /**
         * Initializes the interface
         */
        public void initGUI() {
                m_itemButtons = new Button[m_items.size()];
                for (int i = 0; i < m_items.size(); i++) {
                	m_itemButtons[i] = new Button(m_items.keySet().iterator().next().getName()
                			+ " x" + m_items.values().iterator().next());
                	m_itemButtons[i].addActionListener(new ActionListener() {

                		public void actionPerformed(ActionEvent e) {
                			itemClicked(m_items.keySet().iterator().next());
                		}
                	});
                	add(m_itemButtons[i]);
                }

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
         * Resizes items for optimal size
         */
        public void pack() {
                m_cancel.setWidth(getWidth());
                m_cancel.setHeight(20);
                m_cancel.setY(getHeight() - 20);
                m_cancel.setX(0);
                for (int i = 0; i < m_itemButtons.length; i++) {
                        if (i > 0)
                                m_itemButtons[i].setY(m_itemButtons[i-1].getY()
                                                + m_itemButtons[i-1].getHeight());
                        m_itemButtons[i].setHeight((getHeight() - 20)/ m_items.size());
                        m_itemButtons[i].setWidth(getWidth());
                }
        }
}
