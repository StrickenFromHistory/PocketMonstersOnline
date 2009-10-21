package org.pokenet.client.ui.base;

import java.util.ArrayList;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;
import mdes.slick.sui.skin.simple.SimpleArrowButton;

import org.newdawn.slick.Color;
import org.newdawn.slick.gui.GUIContext;
import org.pokenet.client.GameClient;

/**
 * Combo box
 * @author ZombieBear
 *
 */
@SuppressWarnings("deprecation")
public class ComboBox extends Container{
	private List<String> m_elements;
	private Label m_item;
	private Button m_arrow;
	private String m_selected;
	private ItemSelector m_itemSelector;
	private ComboBox m_this;
	
	/**
	 * Default Constructor
	 */
	public ComboBox(){
		m_this = this;
		m_elements = new ArrayList<String>();
		m_arrow = new SimpleArrowButton(SimpleArrowButton.DOWN);
		m_arrow.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if (m_itemSelector == null) {
					m_itemSelector = new ItemSelector(m_this, 6, m_elements.toArray(new String
							[m_elements.size()]), (int)getWidth(), (int)m_arrow.getWidth());
					m_itemSelector.setLocation(getAbsoluteX(), getAbsoluteY() - 5);
					getDisplay().add(m_itemSelector);
				} else {
					m_itemSelector.destroy();
					m_itemSelector = null;
				}
			}
		});
		m_item = new Label("");
		add(m_arrow);
		add(m_item);
	}
	
	@Override
	public void setSize(float width, float height){
		super.setSize(width, height);
		m_arrow.setSize(getHeight(), getHeight());
		m_arrow.setLocation(getWidth() - m_arrow.getWidth(), 0);
	}
	
	/**
	 * Sets the selected item
	 * @param item
	 */
	public void setSelected(String item){
		m_item.setText(item);
		m_item.pack();
		m_item.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				m_item.setForeground(Color.blue);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				m_item.setForeground(getForeground());
			}
			
			@Override
			public void mousePressed(MouseEvent e){
				if (m_itemSelector == null){
					m_itemSelector = new ItemSelector(m_this, 5, m_elements.toArray(new String[m_elements.size()]),
						(int)getWidth(), (int)m_arrow.getWidth());
					m_itemSelector.setLocation(getAbsoluteX(), getAbsoluteY() - 5);
					getDisplay().add(m_itemSelector);
				} else {
					m_itemSelector.destroy();
					m_itemSelector = null;
				}
			}
		});
		
		m_selected = item;
	}
	
	/**
	 * Enables or disables the ComboBox
	 * @param enabled
	 */
	public void setEnabled(boolean enabled){
		m_arrow.setEnabled(enabled);
	}
	
	/**
	 * Gets the selected item
	 * @return the selected item
	 */
	public String getSelected(){
		return m_selected;
	}
	
	/**
	 * Returns the selected item's index
	 * @return the selected item's index
	 */
	public int getSelectedIndex() {
		return m_elements.indexOf(m_selected);
	}
	
	/**
	 * Returns true if an item is being selected
	 * @return true if an item is being selected
	 */
	public boolean isSelecting() {
		return m_itemSelector != null;
	}
	
	/**
	 * Adds an element
	 * @param element
	 */
	public void addElement(String element){
		m_elements.add(element);
		if (m_item.getText() == ""){
			setSelected(element);
		}
	}

	@Override
	public void update(GUIContext container, int delta){
		super.update(container, delta);
		if (m_itemSelector != null && m_itemSelector.isChoiceMade()){
			setSelected(m_itemSelector.getSelected());
			m_itemSelector.destroy();
			m_itemSelector = null;
		}
	}
	
	@Override
	public void setForeground(Color c){
		super.setForeground(c);
		try{
			m_item.setForeground(c);
		} catch (NullPointerException e) {}
	}
}

/**
 * Item selector for combo boxes
 * @author ZombieBear
 *
 */
class ItemSelector extends Frame{
	Label[] m_shownItems;
	String[] m_items;
	Button m_up, m_down;
	int m_amountShown, m_index, m_selected, m_width;
	private boolean m_choiceMade = false;
	ComboBox m_parent;
	
	/**
	 * Default Constructor
	 * @param parent
	 * @param shown
	 * @param items
	 * @param width
	 * @param buttonWidth
	 */
	@SuppressWarnings("deprecation")
	public ItemSelector(ComboBox parent,
			int shown,
			String[] items,
			int width,
			int buttonWidth){
		m_parent = parent;
		m_amountShown = shown;
		m_items = items;
		m_width = width;
		m_index = 0;

		setSize(width, m_amountShown * 20);
		
		m_shownItems = new Label[m_amountShown];
		for (int i = 0; i < m_amountShown; i++){
			m_shownItems[i] = new Label();
		}

		m_up = new SimpleArrowButton(SimpleArrowButton.FACE_UP);
		m_up.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(-1);
			}
		});
		m_up.setEnabled(false);
		m_up.setSize(buttonWidth, buttonWidth);
		m_up.setLocation(width - buttonWidth, 0);
		
		m_down = new SimpleArrowButton(SimpleArrowButton.FACE_DOWN);
		if (m_items.length <= m_amountShown)
			m_down.setEnabled(false);
		m_down.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				scroll(1);
			}
		});
		m_down.setSize(buttonWidth, buttonWidth);
		m_down.setLocation((float)width - buttonWidth, (float)(m_amountShown * 20 - 2.5 * buttonWidth));
		scroll(0);
		m_up.setZIndex(10);
		m_down.setZIndex(9);
		ensureZOrder();
		getContentPane().add(m_up);
		getContentPane().add(m_down);
		getTitleBar().setVisible(false);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
	}
	
	/**
	 * Returns the selected item
	 * @return the selected item
	 */
	public String getSelected(){
		return m_items[m_selected];
	}
	
	/**
	 * Scrolls according to the index modifier
	 * @param indexMod
	 */
	public void scroll(int indexMod){
		int y = -15;
		m_index += indexMod;

		if (m_index == 0)
			m_up.setEnabled(false);
		else
			m_up.setEnabled(true);
		
		if (m_index + m_amountShown >= m_items.length)
			m_down.setEnabled(false);
		else
			m_down.setEnabled(true);
		
		for (int i = 0; i < m_amountShown; i++){
			final int j = i;
			if (m_shownItems[i] != null){
				getContentPane().remove(m_shownItems[i]);
				m_shownItems[i] = null;
			}
			try{
				m_shownItems[i] = new Label(m_items[i + m_index]);
			} catch (Exception e) {
				m_shownItems[i] = new Label();
			}
			m_shownItems[i].pack();
			m_shownItems[i].addMouseListener(new MouseAdapter(){
				@Override
				public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
					m_shownItems[j].setForeground(Color.blue);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					super.mouseExited(e);
					m_shownItems[j].setForeground(getForeground());
				}
				
				@Override
				public void mouseReleased(MouseEvent e){
					m_selected = j + m_index;
					m_choiceMade = true;
				}
			});
			getContentPane().add(m_shownItems[i]);
			y += 15;
			m_shownItems[i].setLocation(2, y);
		}
	}
	
	/**
	 * Returns true if a choice was made
	 * @return true if a choice was made
	 */
	public boolean isChoiceMade(){
		return m_choiceMade;
	}
	
	@Override
	public void update(GUIContext container, int delta){
		super.update(container, delta);
		if (!m_parent.getParent().getParent().isVisible()){
			destroy();
		}
	}
	
	/**
	 * Destroys the item selector
	 */
	public void destroy(){
		GameClient.getInstance().getDisplay().remove(this);
	}
}