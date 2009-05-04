package org.pokenet.client.ui.frames;

import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.network.PacketGenerator;

import mdes.slick.sui.Frame;
import mdes.slick.sui.Button;
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
	
	public void itemClicked(int name) {
//		packetGen.write("x" + name);
		switch(name){
		case 0:
			initPokeballs();
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
				itemClicked(0);
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
				itemClicked(1);
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
				itemClicked(2);
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
				itemClicked(3);
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
	
	private void initPokeballs() {
//		cancelled();
		setCenter();
		for(int i=0;i<m_categoryButtons.length;i++){
			getContentPane().remove(m_categoryButtons[i]);
		}
		m_itemButtons = new Button[3];
		
		m_itemButtons[0] = new Button("Pokeball - $200\n\n\n     200 left");
		LoadingList.setDeferredLoading(true);
		try{
			m_itemButtons[0].setImage(new Image("res/items/pokeball.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
//		m_itemButtons[0].setSize(300, 50);
//		m_itemButtons[0].setLocation(0,0);
		m_itemButtons[0].setFont(GameClient.getFontLarge());
		m_itemButtons[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				itemClicked("Pokeballs");
			}
		});
		getContentPane().add(m_itemButtons[0]);
		
		m_itemButtons[1] = new Button("Greatball - $600\n\n\n     150 left");
		LoadingList.setDeferredLoading(true);
		try{
			m_itemButtons[1].setImage(new Image("res/items/pokeball.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
//		m_itemButtons[1].setSize(300, 50);
//		m_itemButtons[1].setLocation(0,51);
		m_itemButtons[1].setFont(GameClient.getFontLarge());
		m_itemButtons[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				itemClicked("Pokeballs");
			}
		});
		getContentPane().add(m_itemButtons[1]);
		
		m_itemButtons[2] = new Button("Ultraball - $1000\n\n\n      20 left");
		LoadingList.setDeferredLoading(true);
		try{
			m_itemButtons[2].setImage(new Image("res/items/pokeball.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
//		m_itemButtons[2].setSize(300, 50);
//		m_itemButtons[2].setLocation(0,51);
		m_itemButtons[2].setFont(GameClient.getFontLarge());
		m_itemButtons[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				itemClicked("Pokeballs");
			}
		});
		getContentPane().add(m_itemButtons[2]);
		
		m_cancel = new Button("Cancel");
		m_cancel.setSize(300,40);
		m_cancel.setLocation(0,341);
		m_cancel.setFont(GameClient.getFontLarge());
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i=0;i<m_itemButtons.length;i++){
					getContentPane().remove(m_itemButtons[i]);
				}
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
		
		setTitle("Pokeballs");
		setResizable(false);
		setHeight(400);
		setWidth(300);
		pack();
		for (int i = 0; i < m_itemButtons.length; i++) {
			if (i > 0)
				m_itemButtons[i].setLocation(0,m_itemButtons[i-1].getY() + m_itemButtons[i-1].getHeight());
			m_itemButtons[i].setSize(getWidth(),(getHeight() - 60)/ m_itemButtons.length);
		}
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
