package org.pokenet.client.ui.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mdes.slick.sui.Container;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;

import org.newdawn.slick.Color;

public class ListBox extends Container {
    private List<Label> m_items;
    
    private int m_bottomY;
    
    private int m_selectedIndex = -1;
    private String m_selectedName;
    
    private int maxWidth = 30;
    
    private boolean m_allowDisable;
    
    public int getSelectedIndex() {
            return m_selectedIndex;
    }
    public String getSelectedName() {
            return m_selectedName;
    }
    protected void itemClicked(String itemName, int idx) {
            if (idx == m_selectedIndex && itemName.equals(m_selectedName) && m_allowDisable) {
                    m_selectedIndex = -1;
                    m_selectedName = null;
            } else {
                    m_selectedIndex = idx;
                    m_selectedName = itemName;
            }
            
            for (int i = 0; i < m_items.size(); i++) {
                    if (m_items.get(i).getText().equals(m_selectedName)
                                    && i == m_selectedIndex) {
                            m_items.get(i).setBackground(new Color(0,191,255));
                    } else {
                            m_items.get(i).setBackground(null);
                    }
            }
            
    }
    public ListBox(String[] items) {
            this(Arrays.asList(items));
    }
    public ListBox(String[] items, boolean allowDisable) {
            this(Arrays.asList(items), allowDisable);
    }
    public ListBox(List<String> items) {
            m_items = new ArrayList<Label>();
            m_allowDisable = true;
            for (final String name : items) {
                    final Label itemLabel = new Label(name);
                    final int idx = m_items.size();
                    itemLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseReleased(MouseEvent e) {
                                    itemClicked(name, idx);
                            }
                    });
                    itemLabel.setOpaque(true);
                    itemLabel.setHorizontalAlignment(Label.LEFT_ALIGNMENT);
                    itemLabel.pack();
                    if (itemLabel.getWidth() > maxWidth)
                            maxWidth = (int)itemLabel.getWidth();
                    else
                            itemLabel.setWidth(maxWidth);
                    itemLabel.setHeight(17);
                    itemLabel.setLocation(0, m_bottomY);
                    m_bottomY += itemLabel.getHeight();
                    
                    add(itemLabel);
                    m_items.add(itemLabel);
            }
            setWidth(maxWidth);
            setHeight(m_bottomY);
            setVisible(true);
    }
    public ListBox(List<String> items, boolean allowDisable) {
            m_items = new ArrayList<Label>();
            m_allowDisable = allowDisable;
            for (final String name : items) {
                    final Label itemLabel = new Label(name);
                    final int idx = m_items.size();
                    itemLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseReleased(MouseEvent e) {
                                    itemClicked(name, idx);
                            }
                    });
                    itemLabel.setOpaque(true);
                    itemLabel.setHorizontalAlignment(Label.LEFT_ALIGNMENT);
                    itemLabel.pack();
                    if (itemLabel.getWidth() > maxWidth)
                            maxWidth = (int)itemLabel.getWidth();
                    else
                            itemLabel.setWidth(maxWidth);
                    itemLabel.setHeight(17);
                    itemLabel.setLocation(2, m_bottomY);
                    m_bottomY += itemLabel.getHeight();
                    
                    add(itemLabel);
                    m_items.add(itemLabel);
            }
            if (!allowDisable) {
                    itemClicked(m_items.get(0).getText(), 0);
            }
            setWidth(maxWidth);
            setHeight(m_bottomY);
            setVisible(true);
    }
    
    public void pack() {
            maxWidth = 30;
            for (Label l : m_items) {
                    l.pack();
                    if (l.getWidth() > maxWidth)
                            maxWidth = (int)l.getWidth();
                    else
                            l.setWidth(maxWidth);
                    l.setHeight(17);
            }
            setWidth(maxWidth);
            setHeight(m_bottomY);
            ensureZOrder();
    }
}
