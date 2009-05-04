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
	
	public void itemClicked(String name) {
//		packetGen.write("x" + name);
		
	}
	
	public void initGUI() {
		m_categoryButtons = new Button[4];
//		HashMap<String, Integer> m_itemslist = new HashMap<String, Integer>();
//		m_itemslist.put("Pokeball", 200);
//		m_itemslist.put("Greatball", 600);
		
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
				itemClicked("Pokeballs");
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
				itemClicked("Potions");
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
				itemClicked("Status");
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
				itemClicked("Field");
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

	public void cancelled() {
//		packetGen.write("F");
		setVisible(false);
		getDisplay().remove(this);
	}
	public void pack() {
//		m_cancel.setWidth(getWidth());
//		m_cancel.setHeight(20);
//		m_cancel.setY(getHeight() - 20);
//		m_cancel.setX(0);
//		for (int i = 0; i < m_categoryButtons.length; i++) {
//			if (i > 0)
//				m_categoryButtons[i].setY(m_categoryButtons[i-1].getY()
//						+ m_categoryButtons[i-1].getHeight());
//			m_categoryButtons[i].setHeight((getHeight() - 20)/ 4);
//			m_categoryButtons[i].setWidth(getWidth());
//		}
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
