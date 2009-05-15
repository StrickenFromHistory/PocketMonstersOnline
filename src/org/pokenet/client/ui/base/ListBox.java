package org.pokenet.client.ui.base;

import java.util.Arrays;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;
import mdes.slick.sui.skin.simple.SimpleArrowButton;

import org.newdawn.slick.Color;
/**
 * List Box
 * @author ZombieBear
 *
 */
@SuppressWarnings({ "deprecation"})
public class ListBox extends Container {
    private List<String> m_items;
    private Label[] m_shownLabels;
    private int m_shownItems;
    private int m_scrollIndex = 0, m_selectedIndex = -1, m_bottomY;
    private String m_selectedName;
    private int maxWidth = 30;
    private boolean m_allowDisable;
    private Button m_up, m_down;
    private Color m_selectedColor = new Color(0,191,255);
	
    /**
     * Constructor
     * @param items
     */
	public ListBox(String[] items){
		this(Arrays.asList(items), true);
	}
	
	/**
	 * Constructor
	 * @param items
	 * @param allowDisable
	 */
	public ListBox(String[] items,
			boolean allowDisable){
		this(Arrays.asList(items), allowDisable);
	}
	
	/**
	 * Constructor
	 * @param items
	 */
	public ListBox(List<String> items){
		this(items, true);
	}
	
	/**
	 * Constructor
	 * @param items
	 * @param allowDisable
	 */
	public ListBox(List<String> items,
			boolean allowDisable){
		m_items = items;
		m_allowDisable = allowDisable;

		if (!m_allowDisable) {
			itemClicked(m_items.get(0), 0);
		}
		
		layoutScrollButtons();
        setVisible(true);
	}
	
	/**
	 * Lays out the scroll buttons
	 */
	public void layoutScrollButtons(){
		int buttonWidth = 16;
		m_up = new SimpleArrowButton(SimpleArrowButton.FACE_UP);
		m_up.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(-1);
			}
		});
		m_up.setEnabled(false);
		m_up.setSize(buttonWidth, buttonWidth);
		m_up.setLocation(getWidth() - buttonWidth, 0);
		
		m_down = new SimpleArrowButton(SimpleArrowButton.FACE_DOWN);
		m_down.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(1);
			}
		});
		m_down.setSize(buttonWidth, buttonWidth);
		m_down.setLocation((float)getWidth() - buttonWidth, (float)(getHeight() - 2.5 * buttonWidth));
		m_up.setZIndex(99);
		m_down.setZIndex(99);
		scroll(0);
		ensureZOrder();
		add(m_up);
		add(m_down);
	}
	
	/**
	 * Handles scrolling
	 * @param indexMod
	 */
	public void scroll(int indexMod){
		m_shownItems = (int)(getHeight() / 17);
		m_shownLabels = new Label[m_shownItems];
		
		if (m_shownItems >= m_items.size()){
			m_up.setVisible(false);
			m_down.setVisible(false);
		}
		
		int y = -17;

		m_scrollIndex = m_scrollIndex + indexMod;
		//Handles the buttons' availability
		if (m_scrollIndex == 0)
			m_up.setEnabled(false);
		else
			m_up.setEnabled(true);
		
		if (m_scrollIndex + m_shownItems >= m_items.size())
			m_down.setEnabled(false);
		else
			m_down.setEnabled(true);
		
		//Shows the items
		if (m_shownItems != 0){
			for (int i = 0; i < m_shownItems; i++){
				final int j = i;
				try{
					m_shownLabels[i].setBackground(getBackground());
					remove(m_shownLabels[i]);
					m_shownLabels[i] = null;
				} catch (NullPointerException e){}

				try{
					m_shownLabels[i] = new Label(m_items.get(i + m_scrollIndex));
					//Creates the Labels for each item and creates mouse listeners.
					final int idx = m_items.size();
					m_shownLabels[i].addMouseListener(new MouseAdapter() {
						@Override
						public void mouseReleased(MouseEvent e) {
							itemClicked(m_shownLabels[j].getText(), idx);
						}
					});
					m_shownLabels[i].setOpaque(true);
					m_shownLabels[i].setHorizontalAlignment(Label.LEFT_ALIGNMENT);
					m_shownLabels[i].pack();
					if (m_shownLabels[i].getWidth() > maxWidth)
						maxWidth = (int)m_shownLabels[i].getWidth();
					else
						m_shownLabels[i].setWidth(maxWidth);
					m_shownLabels[i].setHeight(17);
					m_shownLabels[i].setLocation(2, m_bottomY);
					m_bottomY += m_shownLabels[i].getHeight();
				
					if (m_shownLabels[i].getText().equals(m_selectedName))
						m_shownLabels[i].setBackground(m_selectedColor);
				} catch (Exception e) {
					m_shownLabels[i] = null;
					m_shownLabels[i] = new Label();
				}
				add(m_shownLabels[i]);
				y += 17;
				m_shownLabels[i].setLocation(2, y);
			}
		}
	}
	
	/**
	 * Returns the selected item's index
	 * @return the selected item's index
	 */
	public int getSelectedIndex() {
		return m_selectedIndex;
	}
	
	/**
	 * Returns the selected item's name
	 * @return the selected item's name
	 */
	public String getSelectedName() {
		return m_selectedName;
	}

	/**
	 * An item was clicked
	 * @param itemName
	 * @param idx
	 */
	protected void itemClicked(String itemName,
			int idx) {
		if (idx == m_selectedIndex && itemName.equals(m_selectedName) && m_allowDisable) {
			m_selectedIndex = -1;
			m_selectedName = "";
		} else {
			m_selectedIndex = idx;
			m_selectedName = itemName;
		}

		try{
			for (int i = 0; i < m_shownLabels.length; i++) {
				if (m_shownLabels[i].getText().equals(m_selectedName)) {
					m_shownLabels[i].setBackground(m_selectedColor);
				} else {
					m_shownLabels[i].setBackground(getBackground());
				}
			}
		} catch (NullPointerException e){}
	}
	
	/**
	 * Packs the ListBox
	 */
    public void pack() {
    	maxWidth = 30;
    	for (String x : m_items) {
    		Label l = new Label(x);
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
    
    /**
     * Sets the color to show on the selected item
     * @param color
     */
    public void setSelectedColor(Color color){
    	m_selectedColor = color;
    }
    
    @Override
    public void setSize(float width, float height){
    	super.setSize(width, height);
    	layoutScrollButtons();
    }
}