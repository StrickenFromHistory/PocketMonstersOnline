package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.HashMap;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.PlayerItem;
import org.pokenet.client.ui.base.ImageButton;

/**
 * The big bag dialog
 * @author Nushio
 *
 */
public class BigBagDialog extends Frame {
	private ImageButton[] m_categoryButtons;
	private ArrayList<Button> m_itemBtns = new ArrayList<Button>();
	private ArrayList<Label> m_stockLabels = new ArrayList<Label>();
	private Button m_cancel;
	
	private HashMap<Integer, ArrayList<PlayerItem>> m_items = new HashMap<Integer, ArrayList<PlayerItem>>();
	private HashMap<Integer, Integer> m_scrollIndex = new HashMap<Integer,Integer>();
	private int m_curCategory = 0;
	boolean m_update = false;
	
	public BigBagDialog() {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		setCenter();
		initGUI();

		// Load the player's items and sort them by category
		for (PlayerItem i : GameClient.getInstance().getOurPlayer().getItems()){
			System.out.println("Item: " + i.getItem().getName());
			System.out.println(i.getItem().getCategory());
			//Field items
			if (i.getItem().getCategory().equalsIgnoreCase("Field")){
				m_items.get(0).add(i);
				System.out.println("Added to field! " + m_items.get(0).size());
			}
			//Potions and medicine
			else if (i.getItem().getCategory().equalsIgnoreCase("Potions") ||
					i.getItem().getCategory().equalsIgnoreCase("Medicine")){
				m_items.get(1).add(i);
				System.out.println("Added to potions! " + m_items.get(1).size());
			}
			// Berries and food
			else if (i.getItem().getCategory().equalsIgnoreCase("Food")){
				m_items.get(2).add(i);	
				System.out.println("Added to Berries! " + m_items.get(2).size());
			}
			// Pokeballs
			else if (i.getItem().getCategory().equalsIgnoreCase("Pokeball")){
				m_items.get(3).add(i);
				System.out.println("Added to pokeballs! " + m_items.get(3).size());
			}
			//TMs
			else if (i.getItem().getCategory().equalsIgnoreCase("TM") ){
				m_items.get(4).add(i);
				System.out.println("Added to TMs! " + m_items.get(4).size());
			}
		}
		m_update = true;
	}
	
	/**
	 * Initializes the interface
	 */
	public void initGUI() {
		m_categoryButtons = new ImageButton[5];
		
		for(int i = 0; i < m_categoryButtons.length; i++){
			final int j = i;
			try{
				Image[] bagcat = new Image[] {new Image("res/ui/bag/bag_normal.png"),
						new Image("res/ui/bag/bag_hover.png"), new Image("res/ui/bag/bag_pressed.png")};
				Image[] potioncat = new Image[] {new Image("res/ui/bag/potions_normal.png"),
						new Image("res/ui/bag/potions_hover.png"), new Image("res/ui/bag/potions_pressed.png")};
				Image[] berriescat = new Image[] {new Image("res/ui/bag/berries_normal.png"),
						new Image("res/ui/bag/berries_hover.png"), new Image("res/ui/bag/berries_pressed.png")};
				Image[] pokecat = new Image[] {new Image("res/ui/bag/pokeballs_normal.png"),
						new Image("res/ui/bag/pokeballs_hover.png"), new Image("res/ui/bag/pokeballs_pressed.png")};
				Image[] tmscat = new Image[] {new Image("res/ui/bag/tms_normal.png"),
						new Image("res/ui/bag/tms_hover.png"), new Image("res/ui/bag/tms_pressed.png")};
				
				switch (i){
				case 0:
					m_categoryButtons[i] = new ImageButton(bagcat[0], bagcat[1], bagcat[2]);
					break;
				case 1:
					m_categoryButtons[i] = new ImageButton(potioncat[0], potioncat[1], potioncat[2]);
					break;
				case 2:
					m_categoryButtons[i] = new ImageButton(berriescat[0], berriescat[1], berriescat[2]);
					break;
				case 3:
					m_categoryButtons[i] = new ImageButton(pokecat[0], pokecat[1], pokecat[2]);
					break;
				case 4:
					m_categoryButtons[i] = new ImageButton(tmscat[0], tmscat[1], tmscat[2]);
					break;
				}
				
				m_items.put(i, new ArrayList<PlayerItem>());
				m_scrollIndex.put(i, 0);
			}catch(Exception e){
				e.printStackTrace();
			}
			m_categoryButtons[i].setSize(40,40);
			if(i==0)
				m_categoryButtons[i].setLocation(80,10);
			else
				m_categoryButtons[i].setLocation(m_categoryButtons[i-1].getX() + 65, 10);
			m_categoryButtons[i].setFont(GameClient.getFontLarge());
			m_categoryButtons[i].setOpaque(false);
			m_categoryButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					m_curCategory = j;
					m_update = true;
				}
			});
			getContentPane().add(m_categoryButtons[i]);
		}
		
		// Bag Image
		Label bagicon = new Label("");
		bagicon.setSize(40,40);
		
		LoadingList.setDeferredLoading(true);
		try {
			bagicon.setImage(new Image("res/ui/bag/front.png"));
		} catch (SlickException e1) {}
		LoadingList.setDeferredLoading(false);
		
		bagicon.setLocation(18,0);
		bagicon.setFont(GameClient.getFontLarge());
		getContentPane().add(bagicon);
		
		// Scrolling Button LEFT
		Button leftButton = new Button("<");
		leftButton.setSize(20,40);
		leftButton.setLocation(15,95);
		leftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = m_scrollIndex.get(m_curCategory) - 1;
				m_scrollIndex.remove(m_curCategory);
				m_scrollIndex.put(m_curCategory, i);
				m_update = true;
			}
		});
		getContentPane().add(leftButton);
		
		// Item Buttons and Stock Labels
		for (int i = 0; i < 4; i++){
			// Starts the item buttons
			Button item = new Button("id" + i);
			item.setSize(60,60);
			item.setLocation(50 + (80 * i), 85);
			item.setFont(GameClient.getFontLarge());
			m_itemBtns.add(item);
			getContentPane().add(item);

			// Starts the item labels
			Label stock = new Label("x0");
			stock.setSize(60,40);
			stock.setLocation(50 + (80 * i),135);
			stock.setHorizontalAlignment(Label.CENTER_ALIGNMENT);
			stock.setFont(GameClient.getFontLarge());
			stock.setForeground(Color.white);
			m_stockLabels.add(stock);
			getContentPane().add(stock);
		}
			
		// Scrolling Button Right
		Button rightButton = new Button(">");
		rightButton.setSize(20,40);
		rightButton.setLocation(365,95);
		rightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i = m_scrollIndex.get(m_curCategory) + 1;
				m_scrollIndex.remove(m_curCategory);
				m_scrollIndex.put(m_curCategory, i);
				m_update = true;
			}
		});
		getContentPane().add(rightButton);
		
		// Close Button
		m_cancel = new Button("Close");
		m_cancel.setSize(400,32);
		m_cancel.setLocation(0,195);
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeBag();
			}
		});
		getContentPane().add(m_cancel);
		
		// Frame properties
		getResizer().setVisible(false);
		getCloseButton().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						closeBag();
					}
				});
		setBackground(new Color(0, 0, 0, 75));
		setTitle("Bag");
		setResizable(false);
		setHeight(250);
		setWidth(m_categoryButtons.length * 80);
		setVisible(true);
		setCenter();
	}

	/**
	 * Closes the bag
	 */
	public void closeBag() {
		setVisible(false);
		GameClient.getInstance().getDisplay().remove(this);
	}
	
	@Override
	public void update(GUIContext gc, int delta){
		super.update(gc, delta);
		if (m_update){
			m_update = false;
			for (int i = 0; i < 5; i++){
				if (m_items.get(m_curCategory).size() > (m_scrollIndex.get(m_curCategory) + i)){
					try{
						System.out.println(m_items.get(m_curCategory).get(m_scrollIndex.get(
								m_curCategory + i)).getItem().getName());
						m_itemBtns.get(i).setImage(m_items.get(m_curCategory).get(m_scrollIndex.get(
								m_curCategory + i)).getBagImage());
					} catch (Exception e) {e.printStackTrace();}
				} else {
					System.out.println("Item list length is shorter than " + 
							(m_scrollIndex.get(m_curCategory) + i));
					System.out.println(m_curCategory + " " + m_items.get(m_curCategory).size());
					m_itemBtns.get(i).setImage(null);
				}
			}
		}
	}
	
	/**
	 * Centers the frame
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - 200;
		int y = (height / 2) - 200;
		this.setBounds(x, y, this.getWidth(), this.getHeight());
	}
}
