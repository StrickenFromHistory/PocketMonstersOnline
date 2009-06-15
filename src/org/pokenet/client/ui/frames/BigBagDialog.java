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
 * The big bag dialog
 * @author Nushio
 *
 */
public class BigBagDialog extends Frame {
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

	public BigBagDialog() {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
//		m_merch = merch;
//		packetGen = out;
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
		m_categoryButtons = new Button[5];
		m_categoryLabels = new Label[5];
		
		for(int i = 0; i < m_categoryButtons.length;i++){
			m_categoryLabels[i] = new Label(i+"");
			if(i==0)
				m_categoryLabels[i].setLocation(0,0);
			else if(i>0 && i<6)
				m_categoryLabels[i].setLocation(m_categoryLabels[i-1].getLocation().x+80, 0);
			else if(i==6)
				m_categoryLabels[i].setLocation(0,80);
			else
				m_categoryLabels[i].setLocation(m_categoryLabels[i-1].getLocation().x+80, 80);
			m_categoryLabels[i].setGlassPane(true);
			m_categoryLabels[i].setZIndex(1000);
			m_categoryLabels[i].setSize(80,80);
			m_categoryLabels[i].setFont(GameClient.getFontLarge());
			
			m_categoryButtons[i] = new Button(" ");
			LoadingList.setDeferredLoading(true);
//			try{
//				m_categoryButtons[0].setImage(new Image("res/ui/shop/pokeball.png"));
//			}catch(Exception e){
//				e.printStackTrace();
//			}
			LoadingList.setDeferredLoading(false);
			m_categoryButtons[i].setSize(80, 80);
			if(i==0)
				m_categoryButtons[i].setLocation(0,0);
			else if(i>0 && i<6)
				m_categoryButtons[i].setLocation(m_categoryButtons[i-1].getLocation().x+80,0);
			else if(i==6)
				m_categoryButtons[i].setLocation(0,80);
			else
				m_categoryButtons[i].setLocation(m_categoryButtons[i-1].getLocation().x+80,80);
			m_categoryButtons[i].setFont(GameClient.getFontLarge());
			m_categoryButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					categoryClicked(0);
				}
			});
			m_categoryButtons[i].add(m_categoryLabels[i]);
			getContentPane().add(m_categoryButtons[i]);
		}
				
		m_cancel = new Button("Close");
		m_cancel.setSize(400,32);
		m_cancel.setLocation(0,144);
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
		setTitle("Bag");
		setResizable(false);
		setHeight(200);
		setWidth(m_categoryButtons.length*80);
		pack();
		setVisible(true);
		setCenter();
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
//					itemClicked("Pokeballs");
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
//		packetGen.write("Sf");
		setVisible(false);
		GameClient.getInstance().getDisplay().remove(this);
	}
	public void pack() {
		
	}
	
	/**
	 * Centers the frame
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - 400;
		int y = (height / 2) - 200;
		this.setBounds(x, y, this.getWidth(), this.getHeight());
	}
}
