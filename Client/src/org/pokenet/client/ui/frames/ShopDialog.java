package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Image;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Item;
import org.pokenet.client.backend.entity.PlayerItem;

/**
 * The shop dialog
 * @author Nushio
 *
 */
public class ShopDialog extends Frame {
	private Button[] m_categoryButtons;
	private Label[] m_categoryLabels;
	private Button[] m_itemButtons;
	private Label[] m_itemPics;
	private Label[] m_itemLabels;
	private Label[] m_itemStockPics;
	
	List<Item> m_items;
	private Button m_cancel;
	// string being the item name and integer being item quantity
//	private List<Integer> m_merch;

	public ShopDialog(List<Integer> merch) {
//		m_merch = merch;
		setCenter();
		initGUI();
	}
	
	public void categoryClicked(int name) {
//		packetGen.write("x" + name);
		m_items = new ArrayList<Item>();
		switch(name){
		case 0:
			m_items = PlayerItem.generatePokeballs();
			initItems();
			break;
		case 1:
			m_items = PlayerItem.generatePotions();
			initItems();
			break;
		case 2:
			m_items = PlayerItem.generateStatusHeals();
			initItems();
			break;
		case 3:
			m_items = PlayerItem.generateFieldItems();
			initItems();
			break;
		}
	}
	
	public void initGUI() {
		m_categoryButtons = new Button[4];
		m_categoryLabels = new Label[4];
		
		m_categoryButtons[0] = new Button(" ");
		LoadingList.setDeferredLoading(true);
		try{
			m_categoryButtons[0].setImage(new Image("res/ui/shop/pokeball.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		m_categoryButtons[0].setSize(150, 160);
		m_categoryButtons[0].setLocation(0,0);
		m_categoryButtons[0].setFont(GameClient.getFontLarge());
		m_categoryButtons[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				categoryClicked(0);
			}
		});
		getContentPane().add(m_categoryButtons[0]);
		
		m_categoryLabels[0] = new Label("Pokeballs");
		m_categoryLabels[0].setLocation(0,0);
		m_categoryLabels[0].setGlassPane(true);
		m_categoryLabels[0].setZIndex(1000);
		m_categoryLabels[0].setSize(150,10);
		m_categoryLabels[0].setFont(GameClient.getFontLarge());
		getContentPane().add(m_categoryLabels[0]);
		
		m_categoryButtons[1] = new Button(" ");
		LoadingList.setDeferredLoading(true);
		try{
			m_categoryButtons[1].setImage(new Image("res/ui/shop/potion.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		m_categoryButtons[1].setSize(150, 160);
		m_categoryButtons[1].setLocation(151, 0);
		m_categoryButtons[1].setFont(GameClient.getFontLarge());
		m_categoryButtons[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				categoryClicked(1);
			}
		});
		getContentPane().add(m_categoryButtons[1]);
		
		m_categoryLabels[1] = new Label("Potions");
		m_categoryLabels[1].setLocation(151,0);
		m_categoryLabels[1].setGlassPane(true);
		m_categoryLabels[1].setFont(GameClient.getFontLarge());
		m_categoryLabels[1].setZIndex(1000);
		m_categoryLabels[1].setSize(150,10);
		getContentPane().add(m_categoryLabels[1]);
		
		m_categoryButtons[2] = new Button(" ");
		LoadingList.setDeferredLoading(true);
		try{
			m_categoryButtons[2].setImage(new Image("res/ui/shop/status.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		m_categoryButtons[2].setSize(150, 160);
		m_categoryButtons[2].setLocation(0,161);
		m_categoryButtons[2].setFont(GameClient.getFontLarge());
		m_categoryButtons[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				categoryClicked(2);
			}
		});
		getContentPane().add(m_categoryButtons[2]);
		
		m_categoryLabels[2] = new Label("Status Heals");
		m_categoryLabels[2].setLocation(0,161);
		m_categoryLabels[2].setGlassPane(true);
		m_categoryLabels[2].setFont(GameClient.getFontLarge());
		m_categoryLabels[2].setZIndex(1000);
		m_categoryLabels[2].setSize(150,10);
		getContentPane().add(m_categoryLabels[2]);
		
		m_categoryButtons[3] = new Button(" ");
		LoadingList.setDeferredLoading(true);
		try{
			m_categoryButtons[3].setImage(new Image("res/ui/shop/field.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		m_categoryButtons[3].setSize(150, 160);
		m_categoryButtons[3].setLocation(151,161);
		m_categoryButtons[3].setFont(GameClient.getFontLarge());
		m_categoryButtons[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				categoryClicked(3);
			}
		});
		getContentPane().add(m_categoryButtons[3]);
		
		m_categoryLabels[3] = new Label("Field Tools");
		m_categoryLabels[3].setLocation(151,161);
		m_categoryLabels[3].setGlassPane(true);
		m_categoryLabels[3].setFont(GameClient.getFontLarge());
		m_categoryLabels[3].setZIndex(1000);
		m_categoryLabels[3].setSize(150,10);
		getContentPane().add(m_categoryLabels[3]);

		
		m_cancel = new Button("Cancel");
		m_cancel.setSize(300,56);
		m_cancel.setLocation(0,321);
		m_cancel.setFont(GameClient.getFontLarge());
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelled();
			}
		});
		getContentPane().add(m_cancel);
		
		this.getResizer().setVisible(false);
		getCloseButton().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelled();
					}
				});
		setTitle("PokeShop");
		setResizable(false);
		setHeight(400);
		setWidth(300);
		pack();
		setVisible(true);
	}
	
	private void initItems() {
		setCenter();
		for(int i=0;i<m_categoryButtons.length;i++){
			getContentPane().remove(m_categoryButtons[i]);
		}
		for(int i=0;i<m_categoryLabels.length;i++){
			getContentPane().remove(m_categoryLabels[i]);
		}
		getContentPane().remove(m_cancel);
		m_itemButtons = new Button[m_items.size()];
		m_itemPics = new Label[m_items.size()];
		m_itemLabels = new Label[m_items.size()];
		m_itemStockPics = new Label[m_items.size()];
		for(int i = 0;i<m_items.size();i++){
			final int itemChosen = m_items.get(i).getId();
			m_itemButtons[i] = new Button("");
			m_itemButtons[i].setSize(300, 50);
			if(i>0)
				m_itemButtons[i].setLocation(0,(m_itemButtons[i-1].getY()+51));
			else
				m_itemButtons[i].setLocation(0,0);
			m_itemButtons[i].setZIndex(0);
			m_itemButtons[i].setFont(GameClient.getFontLarge());
			m_itemButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					itemClicked(itemChosen);
				}
			});
			getContentPane().add(m_itemButtons[i]);
			
			try{
				LoadingList.setDeferredLoading(true);
				m_itemPics[i] = new Label(new Image("/res/items/"+m_items.get(i).getId()+".png"));
				LoadingList.setDeferredLoading(false);
				m_itemPics[i].setGlassPane(true);
				m_itemPics[i].setSize(32,32);
				if(i>0)
					m_itemPics[i].setLocation(0,(m_itemPics[i-1].getY()+51));
				else
					m_itemPics[i].setLocation(0,12);
				m_itemPics[i].setZIndex(1000);
				getContentPane().add(m_itemPics[i]);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			try{
				LoadingList.setDeferredLoading(true);
				String stock = "empty";
//				if(m_items.get(i).getAvailable()>=100||m_items.size()==-1){
					stock = "full";
//				}else if(m_items.get(i).getAvailable()<100&&m_items.get(i).getAvailable()>=60){
//					stock = "half";
//				}else if(m_items.get(i).getAvailable()<60&&m_items.get(i).getAvailable()>=30){
//					stock = "halfempty";
//				}
				m_itemStockPics[i] = new Label(new Image("/res/ui/shop/"+stock+".png"));
				LoadingList.setDeferredLoading(false);
				m_itemStockPics[i].setGlassPane(true);
				m_itemStockPics[i].setSize(32,32);
				if(i>0)
					m_itemStockPics[i].setLocation(260,(m_itemStockPics[i-1].getY()+51));
				else
					m_itemStockPics[i].setLocation(260,12);
				m_itemStockPics[i].setZIndex(1000);
				getContentPane().add(m_itemStockPics[i]);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			m_itemLabels[i] = new Label(m_items.get(i).getName()+" - $"+m_items.get(i).getPrice());
			m_itemLabels[i].setSize(200,50);
			m_itemLabels[i].setGlassPane(true);
			m_itemLabels[i].setFont(GameClient.getFontLarge());
			m_itemLabels[i].setZIndex(1200);
			m_itemLabels[i].setHorizontalAlignment(0);
			if(i>0)
				m_itemLabels[i].setLocation(30,(m_itemLabels[i-1].getY()+51));
			else
				m_itemLabels[i].setLocation(30,0);
			getContentPane().add(m_itemLabels[i]);
		}
		
		m_cancel = new Button("Cancel");
		m_cancel.setSize(300,40);
		m_cancel.setLocation(0,336);
		m_cancel.setFont(GameClient.getFontLarge());
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<m_itemButtons.length;i++){
					getContentPane().remove(m_itemButtons[i]);
				}
				for(int i=0;i<m_itemPics.length;i++){
					getContentPane().remove(m_itemPics[i]);
				}
				for(int i=0;i<m_itemLabels.length;i++){
					getContentPane().remove(m_itemLabels[i]);
				}
				for(int i=0;i<m_itemStockPics.length;i++){
					getContentPane().remove(m_itemStockPics[i]);
				}
				getContentPane().remove(m_cancel);
				initGUI();
			}
		});
		getContentPane().add(m_cancel);
		
		this.getResizer().setVisible(false);
		getCloseButton().addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelled();
					}
				});
		
		setTitle("Potions");
		setResizable(false);
		setHeight(400);
		setWidth(300);
		pack();
//		for (int i = 0; i < m_itemButtons.length; i++) {
//			if (i > 0)
//				m_itemButtons[i].setLocation(0,m_itemButtons[i-1].getY() + m_itemButtons[i-1].getHeight());
//			m_itemButtons[i].setSize(getWidth(),(getHeight() - 60)/ m_itemButtons.length);
//		}
		setVisible(true);
	}
	
	public void cancelled() {
		GameClient.getInstance().getPacketGenerator().write("Sf");
		setVisible(false);
		GameClient.getInstance().getDisplay().remove(this);
	}
	
	public void itemClicked(int itemid) {
		GameClient.getInstance().getPacketGenerator().write("Sb"+itemid+",1");
	}
	public void pack() {
		
	}
	
	/**
	 * Centers the frame
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - 130;
		int y = (height / 2) - 238;
		this.setBounds(x, y, 259, 475);
	}
}
