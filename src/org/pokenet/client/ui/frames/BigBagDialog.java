package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.HashMap;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.FileLoader;
import org.pokenet.client.backend.ItemDatabase;
import org.pokenet.client.backend.entity.PlayerItem;
import org.pokenet.client.ui.base.ImageButton;

/**
 * The big bag dialog
 * 
 * @author Nushio
 * @author ZombieBear
 * 
 */
public class BigBagDialog extends Frame {
	protected ImageButton[] m_categoryButtons;
	protected ArrayList<Button> m_itemBtns;
	protected ArrayList<Label> m_stockLabels;
	protected Button m_leftButton, m_rightButton, m_cancel;
	protected ItemPopup m_popup;

	private HashMap<Integer, ArrayList<PlayerItem>> m_items;
	private HashMap<Integer, Integer> m_scrollIndex;
	protected int m_curCategory = 0;
	protected boolean m_update = false;

	public BigBagDialog() {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		setCenter();
		initGUI();
		loadItems();
	}
	
	private void loadItems()
	{
		// Load the player's items and sort them by category
		for (PlayerItem item : GameClient.getInstance().getOurPlayer().getItems()) {
			// Field items
			if (item.getItem().getCategory().equalsIgnoreCase("Field") ||
					item.getItem().getCategory().equalsIgnoreCase("Evolution")) {
				m_items.get(0).add(item);
			}
			// Potions and medicine
			else if (item.getItem().getCategory().equalsIgnoreCase("Potions")
					|| item.getItem().getCategory().equalsIgnoreCase(
					"Medicine")) {
				m_items.get(1).add(item);
			}
			// Berries and food
			else if (item.	getItem().getCategory().equalsIgnoreCase("Food")) {
				m_items.get(2).add(item);
			}
			// Pokeballs
			else if (item.getItem().getCategory().equalsIgnoreCase("Pokeball")) {
				m_items.get(3).add(item);
			}
			// TMs
			else if (item.getItem().getCategory().equalsIgnoreCase("TM")) {
				m_items.get(4).add(item);
			}
		}
		m_update = true;
	}
	
	
	/**
	 * Adds an item to the bag
	 * @param id
	 * @param amount
	 */
	public void addItem(int id, boolean newItem) {
		if (newItem) {
			for (PlayerItem item : GameClient.getInstance().getOurPlayer().getItems()){
				if (item.getNumber() == id){
					// Field items
					if (item.getItem().getCategory().equalsIgnoreCase("Field") ||
							item.getItem().getCategory().equalsIgnoreCase("Evolution")) {
						m_items.get(0).add(item);
					}
					// Potions and medicine
					else if (item.getItem().getCategory().equalsIgnoreCase("Potions")
							|| item.getItem().getCategory().equalsIgnoreCase(
							"Medicine")) {
						m_items.get(1).add(item);
					}
					// Berries and food
					else if (item.	getItem().getCategory().equalsIgnoreCase("Food")) {
						m_items.get(2).add(item);
					}
					// Pokeballs
					else if (item.getItem().getCategory().equalsIgnoreCase("Pokeball")) {
						m_items.get(3).add(item);
					}
					// TMs
					else if (item.getItem().getCategory().equalsIgnoreCase("TM")) {
						m_items.get(4).add(item);
					}
				}
			}
		}
		m_update = true;
	}

	/**
	 * Removes an item to the bag
	 * @param id
	 * @param amount
	 */
	public void removeItem(int id, boolean remove) {
		/* The remove variable indicates that this is the last of the item, and it should be
		 * removed from the inventory 
		 */
		if (remove) {
			for (PlayerItem item : GameClient.getInstance().getOurPlayer().getItems()){
				if (item.getNumber() == id){
					// Field items
					if (item.getItem().getCategory().equalsIgnoreCase("Field") ||
							item.getItem().getCategory().equalsIgnoreCase("Evolution")) {
						m_items.get(0).remove(item);
					}
					// Potions and medicine
					else if (item.getItem().getCategory().equalsIgnoreCase("Potions")
							|| item.getItem().getCategory().equalsIgnoreCase(
							"Medicine")) {
						m_items.get(1).remove(item);
					}
					// Berries and food
					else if (item.getItem().getCategory().equalsIgnoreCase("Food")) {
						m_items.get(2).remove(item);

					}
					// Pokeballs
					else if (item.getItem().getCategory().equalsIgnoreCase("Pokeball")) {
						m_items.get(3).remove(item);
					}
					// TMs
					else if (item.getItem().getCategory().equalsIgnoreCase("TM")) {
						m_items.get(4).remove(item);
					}
				}
			}
			/* There is probably a better way to do the code below, but what essentially
			 * occurs is a re-initialization of the bag screen. Then the category is set back
			 * to the previous category. The affect this has for the user is, the item is
			 * instantly removed from the players bag screen when the last of the item is used.
			 */
			int tmpCurCategory = m_curCategory;
			initGUI();
			loadItems();
			m_curCategory = tmpCurCategory;
		}
		m_update = true;
	}
	
	/**
	 * Initializes the interface
	 */
	public void initGUI() {
		/* Does this cause a memory leak in JAVA if called more than once?
		 * If so does java have a delete keyword?  
		 */
		m_items = new HashMap<Integer, ArrayList<PlayerItem>>();
		m_scrollIndex = new HashMap<Integer, Integer>();
		m_itemBtns = new ArrayList<Button>();
		m_stockLabels = new ArrayList<Label>();
		m_categoryButtons = new ImageButton[5];
		//remove any existing Bag gui content
		getContentPane().removeAll();
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		try {
			Image[] bagcat = new Image[] {
					new Image(FileLoader.loadFile(respath+"res/ui/bag/bag_normal.png"), "res/ui/bag/bag_normal.png", false),
					new Image(FileLoader.loadFile(respath+"res/ui/bag/bag_hover.png"), "res/ui/bag/bag_hover.png", false),
					new Image(FileLoader.loadFile(respath+"res/ui/bag/bag_pressed.png"), "res/ui/bag/bag_pressed.png", false)};
			Image[] potioncat = new Image[] {
					new Image(FileLoader.loadFile(respath+"res/ui/bag/potions_normal.png"), "res/ui/bag/potions_normal.png", false),
					new Image(FileLoader.loadFile(respath+"res/ui/bag/potions_hover.png"), "res/ui/bag/potions_hover.png", false),
					new Image(FileLoader.loadFile(respath+"res/ui/bag/potions_pressed.png"), "res/ui/bag/potions_pressed.png", false) };
			Image[] berriescat = new Image[] {
					new Image(FileLoader.loadFile(respath+"res/ui/bag/berries_normal.png"), "res/ui/bag/berries_normal.png", false),
					new Image(FileLoader.loadFile(respath+"res/ui/bag/berries_hover.png"), "res/ui/bag/berries_hover.png", false),
					new Image(FileLoader.loadFile(respath+"res/ui/bag/berries_pressed.png"), "res/ui/bag/berries_pressed.png", false)};
			Image[] pokecat = new Image[] {
					new Image(FileLoader.loadFile(respath+"res/ui/bag/pokeballs_normal.png"), "res/ui/bag/pokeballs_normal.png", false),
					new Image(FileLoader.loadFile(respath+"res/ui/bag/pokeballs_hover.png"), "res/ui/bag/pokeballs_hover.png", false),
					new Image(FileLoader.loadFile(respath+"res/ui/bag/pokeballs_pressed.png"), "res/ui/bag/pokeballs_pressed.png", false) };
			Image[] tmscat = new Image[] {
					new Image(FileLoader.loadFile(respath+"res/ui/bag/tms_normal.png"), "res/ui/bag/tms_normal.png", false),
					new Image(FileLoader.loadFile(respath+"res/ui/bag/tms_hover.png"), "res/ui/bag/tms_hover.png", false),
					new Image(FileLoader.loadFile(respath+"res/ui/bag/tms_pressed.png"), "res/ui/bag/tms_pressed.png", false) };
			for (int i = 0; i < m_categoryButtons.length; i++) {
				final int j = i;

				switch (i) {
				case 0:
					m_categoryButtons[i] = new ImageButton(bagcat[0],
							bagcat[1], bagcat[2]);
					m_categoryButtons[i].setToolTipText("Bag");
					break;
				case 1:
					m_categoryButtons[i] = new ImageButton(potioncat[0],
							potioncat[1], potioncat[2]);
					m_categoryButtons[i].setToolTipText("Potions");
					break;
				case 2:
					m_categoryButtons[i] = new ImageButton(berriescat[0],
							berriescat[1], berriescat[2]);
					m_categoryButtons[i].setToolTipText("Food");
					break;
				case 3:
					m_categoryButtons[i] = new ImageButton(pokecat[0],
							pokecat[1], pokecat[2]);
					m_categoryButtons[i].setToolTipText("Pokeballs");
					break;
				case 4:
					m_categoryButtons[i] = new ImageButton(tmscat[0],
							tmscat[1], tmscat[2]);
					m_categoryButtons[i].setToolTipText("TMs");
					break;
				}

				m_items.put(i, new ArrayList<PlayerItem>());
				m_scrollIndex.put(i, 0);
				m_categoryButtons[i].setSize(40, 40);
				if (i == 0)
					m_categoryButtons[i].setLocation(80, 10);
				else
					m_categoryButtons[i].setLocation(m_categoryButtons[i - 1]
							.getX() + 65, 10);
				m_categoryButtons[i].setFont(GameClient.getFontLarge());
				m_categoryButtons[i].setOpaque(false);
				m_categoryButtons[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						destroyPopup();
						m_curCategory = j;
						m_update = true;
					}
				});
				getContentPane().add(m_categoryButtons[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Bag Image
		Label bagicon = new Label("");
		bagicon.setSize(40, 40);

		LoadingList.setDeferredLoading(true);
		try {
			bagicon.setImage(new Image(respath+"res/ui/bag/front.png", false));
		} catch (SlickException e1) {
		}
		LoadingList.setDeferredLoading(false);

		bagicon.setLocation(18, 0);
		bagicon.setFont(GameClient.getFontLarge());
		getContentPane().add(bagicon);

		// Scrolling Button LEFT
		m_leftButton = new Button("<");
		m_leftButton.setSize(20, 40);
		m_leftButton.setLocation(15, 95);
		m_leftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destroyPopup();
				int i = m_scrollIndex.get(m_curCategory) - 1;
				m_scrollIndex.remove(m_curCategory);
				m_scrollIndex.put(m_curCategory, i);
				m_update = true;
			}
		});
		getContentPane().add(m_leftButton);

		// Item Buttons and Stock Labels
		for (int i = 0; i < 4; i++) {
			final int j = i;
			// Starts the item buttons
			Button item = new Button();
			item.setSize(60, 60);
			item.setLocation(50 + (80 * i), 85);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					destroyPopup();
					useItem(j);
				}
			});
			m_itemBtns.add(item);
			getContentPane().add(item);

			// Starts the item labels
			Label stock = new Label();
			stock.setSize(60, 40);
			stock.setLocation(50 + (80 * i), 135);
			stock.setHorizontalAlignment(Label.CENTER_ALIGNMENT);
			stock.setFont(GameClient.getFontLarge());
			stock.setForeground(Color.white);
			m_stockLabels.add(stock);
			getContentPane().add(stock);
		}

		// Scrolling Button Right
		m_rightButton = new Button(">");
		m_rightButton.setSize(20, 40);
		m_rightButton.setLocation(365, 95);
		m_rightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destroyPopup();
				int i = m_scrollIndex.get(m_curCategory) + 1;
				m_scrollIndex.remove(m_curCategory);
				m_scrollIndex.put(m_curCategory, i);
				m_update = true;
			}
		});
		getContentPane().add(m_rightButton);

		// Close Button
		m_cancel = new Button("Close");
		m_cancel.setSize(390, 32);
		m_cancel.setLocation(5, 180);
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destroyPopup();
				closeBag();
			}
		});
		getContentPane().add(m_cancel);

		// Frame properties
		getResizer().setVisible(false);
		getCloseButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				destroyPopup();
				closeBag();
			}
		});
		getContentPane().addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						super.mouseReleased(e);
						destroyPopup();
					}
				});

		setBackground(new Color(0, 0, 0, 100));
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
	public void update(GUIContext gc, int delta) {
		super.update(gc, delta);
		if (m_update) {
			m_update = false;
			// Enable/disable scrolling
			if (m_scrollIndex.get(m_curCategory) == 0)
				m_leftButton.setEnabled(false);
			else
				m_leftButton.setEnabled(true);
			
			if (m_scrollIndex.get(m_curCategory) + 4 >= m_items.get(m_curCategory).size())
				m_rightButton.setEnabled(false);
			else
				m_rightButton.setEnabled(true);
			
			// Update items and stocks
			System.out.println("Looping through items to display");
			for (int i = 0; i < 5; i++) {
				try {
					m_itemBtns.get(i).setName(
							String.valueOf(m_items.get(m_curCategory).get(
									m_scrollIndex.get(m_curCategory) + i)
									.getNumber()));
					m_itemBtns.get(i).setToolTipText(
							m_items.get(m_curCategory).get(
									m_scrollIndex.get(m_curCategory) + i)
									.getItem().getName()+"\n"+m_items.get(m_curCategory).get(
											m_scrollIndex.get(m_curCategory) + i)
											.getItem().getDescription());
					m_itemBtns.get(i).setImage(
							m_items.get(m_curCategory).get(
									m_scrollIndex.get(m_curCategory) + i)
									.getBagImage());
					m_stockLabels.get(i).setText(
							"x" + m_items.get(m_curCategory).get(
									m_scrollIndex.get(m_curCategory) + i)
									.getQuantity());
					m_itemBtns.get(i).setEnabled(true);
				} catch (Exception e) {
					m_itemBtns.get(i).setImage(null);
					m_itemBtns.get(i).setToolTipText("");
					m_itemBtns.get(i).setText("");
					m_stockLabels.get(i).setText("");
					m_itemBtns.get(i).setEnabled(false);
				}
			}
		}
	}
	
	/**
	 * An item was used!
	 * @param i
	 */
	public void useItem(int i) {
		destroyPopup();
		if (m_curCategory == 0 || m_curCategory == 3){
			if (ItemDatabase.getInstance().getItem(Integer.valueOf(m_itemBtns.get(i).getName()))
					.getCategory().equals("Evolution")){
				m_popup = new ItemPopup(m_itemBtns.get(i).getToolTipText().split("\n")[0], Integer.parseInt(
						m_itemBtns.get(i).getName()), true, false);				
			} else {
				m_popup = new ItemPopup(m_itemBtns.get(i).getToolTipText().split("\n")[0], Integer.parseInt(
						m_itemBtns.get(i).getName()), false, false);
			}
			m_popup.setLocation(m_itemBtns.get(i).getAbsoluteX(), m_itemBtns.get(i).getAbsoluteY() 
					+ m_itemBtns.get(i).getHeight() - getTitleBar().getHeight());
			getDisplay().add(m_popup);
		} else {
			m_popup = new ItemPopup(m_itemBtns.get(i).getToolTipText().split("\n")[0], Integer.parseInt(
					m_itemBtns.get(i).getName()), true, false);
			m_popup.setLocation(m_itemBtns.get(i).getAbsoluteX(), m_itemBtns.get(i).getAbsoluteY() 
					+ m_itemBtns.get(i).getHeight() - getTitleBar().getHeight());
			getDisplay().add(m_popup);
		}
	}
	
	/**
	 * Destroys the item popup
	 */
	public void destroyPopup() {
		if (getDisplay().containsChild(m_popup)){
			m_popup.destroyPopup();
			m_popup = null;
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

/**
 * The use dialog for items
 * @author ZombieBear
 *
 */
class ItemPopup extends Frame{
	private Label m_name;
	private Button m_use;
	private Button m_give;
	private Button m_destroy;
	private Button m_cancel;
	private TeamPopup m_team;
	
	/**
	 * Default Constructor
	 * @param item
	 * @param id
	 * @param useOnPokemon
	 * @param isBattle
	 */
	public ItemPopup(String item, int id, boolean useOnPokemon, boolean isBattle){
		final int m_id = id;
		final boolean m_useOnPoke = useOnPokemon;
		final boolean m_isBattle = isBattle;
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		
		// Item name label
		m_name = new Label(item.split("\n")[0]);
		m_name.setFont(GameClient.getFontSmall());
		m_name.setForeground(Color.white);
		m_name.pack();
		m_name.setLocation(0,0);
		getContentPane().add(m_name);
		
		// Use button
		m_use = new Button("Use");
		m_use.setSize(100,25);
		m_use.setLocation(0, m_name.getY() + m_name.getHeight() + 3);
		m_use.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				useItem(m_id, m_useOnPoke, m_isBattle);
			}
		});
		getContentPane().add(m_use);

		if (!isBattle){
			m_give = new Button("Give");
			m_give.setSize(100,25);
			m_give.setLocation(0, m_use.getY() + 25);
			m_give.setEnabled(false);
			m_give.addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent e){
					giveItem(m_id);
				}
			});
			getContentPane().add(m_give);
		}
		// Destroy the item
		m_destroy = new Button("Drop");
		m_destroy.setSize(100,25);
		if (!isBattle)
			m_destroy.setLocation(0, m_give.getY() + 25);
		else
			m_destroy.setLocation(0, m_use.getY() + 25);
		m_destroy.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){
				GameClient.getInstance().getPacketGenerator().writeTcpMessage("i" + m_id);
				destroyPopup();
			}
		});
		getContentPane().add(m_destroy);
		
		// Close the popup
		m_cancel = new Button("Cancel");
		m_cancel.setSize(100,25);
		m_cancel.setLocation(0, m_destroy.getY() + 25);
		m_cancel.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent e){ 
				destroyPopup();
			}
		});
		getContentPane().add(m_cancel);
		
		// Frame configuration
		addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e) {
				destroyPopup();
			}
		});
		setBackground(new Color(0,0,0,150));
		if (!isBattle)
			setSize(100, 140);
		else
			setSize(100, 115);
		getTitleBar().setVisible(false);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
	}
	
	/**
	 * Destroys the popup
	 */
	public void destroyPopup() {
		getDisplay().remove(m_team);
		m_team = null;
		getDisplay().remove(this);
	}
	
	/**
	 * Use the item. usedOnPoke determine whether the item should be applied to a pokemon
	 * @param id
	 * @param usedOnPoke
	 */
	public void useItem(int id, boolean usedOnPoke, boolean isBattle){
		if (getDisplay().containsChild(m_team))
			getDisplay().remove(m_team);
		m_team = null;
		if (usedOnPoke) {
			setAlwaysOnTop(false);
			m_team = new TeamPopup(this, id, true, isBattle);
			m_team.setLocation(m_use.getAbsoluteX() + getWidth(), m_use.getAbsoluteY() - 15);
			getDisplay().add(m_team);
		} else {
			GameClient.getInstance().getPacketGenerator().writeTcpMessage("I" + id);
			destroyPopup();
		}
	}

	/**
	 * Give the item to a pokemon
	 * @param id
	 */
	public void giveItem(int id){
		setAlwaysOnTop(false);
		if (getDisplay().containsChild(m_team))
			getDisplay().remove(m_team);
		m_team = null;
		m_team = new TeamPopup(this, id, false, false);
		m_team.setLocation(m_give.getAbsoluteX() + getWidth(), m_give.getAbsoluteY() - 15);
		getDisplay().add(m_team);
	}
}

/**
 * PopUp that lists the player's pokemon in order to use/give an item
 * @author ZombieBear
 *
 */
class TeamPopup extends Frame{
	ItemPopup m_parent;
	Label m_details;
	
	/**
	 * Default constructor
	 * @param itemId
	 * @param use
	 * @param useOnPoke
	 */
	public TeamPopup(ItemPopup parent, int itemId, boolean use, boolean isBattle) {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);

		m_parent = parent;
		final int m_item = itemId;
		final boolean m_use = use;
		final boolean m_isBattle = isBattle;

		int y = 0;
		for (int i = 0; i < GameClient.getInstance().getOurPlayer().getPokemon().length; i++) {
			try{
				final Label tempLabel = new Label(GameClient.getInstance().getOurPlayer().getPokemon()[i].getName());
				final int j = i;
				tempLabel.setSize(100, 15);
				tempLabel.setFont(GameClient.getFontSmall());
				tempLabel.setForeground(Color.white);
				tempLabel.setLocation(0, y);
				tempLabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						super.mouseReleased(e);
						processItemUse(m_use, m_item, j, m_isBattle);
					}
					@Override
					public void mouseEntered(MouseEvent e) {
						super.mouseEntered(e);
						tempLabel.setForeground(new Color(255, 215, 0));
					}
					@Override
					public void mouseExited(MouseEvent e) {
						super.mouseExited(e);
						tempLabel.setForeground(new Color(255, 255, 255));
					}
				});
				y += 18;
				getContentPane().add(tempLabel);
			} catch (Exception e) {}
		}
		
		// Frame configuration
		setBackground(new Color(0,0,0,150));
		setSize(100, 115);
		getTitleBar().setVisible(false);
		setVisible(true);
		setResizable(false);
		setAlwaysOnTop(true);
	}
	
	/**
	 * Send the server a packet to inform it an item was used
	 * @param use
	 * @param id
	 * @param pokeIndex
	 * @param isBattle
	 */
	public void processItemUse(boolean use, int id, int pokeIndex, boolean isBattle){
		if (use) {
			GameClient.getInstance().getPacketGenerator().writeTcpMessage("I" + id + "," + pokeIndex);
		} else {
			// TODO: Write "Give" packet
			GameClient.getInstance().getPacketGenerator().writeTcpMessage("");
		}
		m_parent.destroyPopup();
	}
}