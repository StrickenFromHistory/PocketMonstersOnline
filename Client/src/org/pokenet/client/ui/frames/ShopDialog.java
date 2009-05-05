package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Item;
import org.pokenet.client.network.PacketGenerator;

import mdes.slick.sui.Frame;
import mdes.slick.sui.Button;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

/**
 * The shop dialog
 * @author Nushio
 *
 */
public class ShopDialog extends Frame {
	private Button[] m_categoryButtons;
	private Button[] m_itemButtons;
	private Label[] m_itemPics;
	List<Item> m_items;
	private Button m_cancel;
	// string being the item name and integer being item quantity
//	private List<Integer> m_merch;

	private PacketGenerator packetGen;
	
	public ShopDialog(List<Integer> merch, PacketGenerator out) {
//		m_merch = merch;
		packetGen = out;
		setCenter();
		initGUI();
	}
	
	public void categoryClicked(int name) {
//		packetGen.write("x" + name);
		m_items = new ArrayList<Item>();
		switch(name){
		case 0:
			m_items = Item.generatePokeballs();
			initItems();
			break;
		case 1:
			m_items = Item.generatePotions();
			initItems();
			break;
		case 2:
			m_items = Item.generateStatusHeals();
			initItems();
			break;
		case 3:
			m_items = Item.generateFieldItems();
			initItems();
			break;
		}
	}
	
	public void initGUI() {
		m_categoryButtons = new Button[4];
		
		m_categoryButtons[0] = new Button("Pokeballs\n\n\n\n\n");
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
		
		m_categoryButtons[1] = new Button("Potions\n\n\n\n\n");
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
		
		m_categoryButtons[2] = new Button("Status Healers\n\n\n\n\n");
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
		
		m_categoryButtons[3] = new Button("Field Tools\n\n\n\n\n");
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
		getContentPane().remove(m_cancel);
		m_itemButtons = new Button[m_items.size()];
		m_itemPics = new Label[m_items.size()];
		for(int i = 0;i<m_items.size();i++){
			m_itemButtons[i] = new Button("    "+m_items.get(i).getName()+" - $"+m_items.get(i).getCost()+"     "+m_items.get(i).getAvailable()+" left");
			m_itemButtons[i].setSize(300, 50);
			if(i>0)
				m_itemButtons[i].setLocation(0,(m_itemButtons[i-1].getY()+51));
			else
				m_itemButtons[i].setLocation(0,0);
			m_itemButtons[i].setZIndex(0);
			m_itemButtons[i].setFont(GameClient.getFontLarge());
			m_itemButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					itemClicked("Pokeballs");
				}
			});
			getContentPane().add(m_itemButtons[i]);
			LoadingList.setDeferredLoading(true);
			try{
				m_itemPics[i] = new Label(new Image("/res/items/"+m_items.get(i).getPicname()+".png"));
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
//		packetGen.write("F");
		setVisible(false);
		getDisplay().remove(this);
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
