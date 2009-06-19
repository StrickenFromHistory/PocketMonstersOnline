package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Item;
import org.pokenet.client.backend.entity.PlayerItem;
import org.pokenet.client.ui.base.ImageButton;

/**
 * The big bag dialog
 * @author Nushio
 *
 */
public class BigBagDialog extends Frame {
	private ImageButton[] m_categoryButtons;
	
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
	
	public void initGUI() {
		m_categoryButtons = new ImageButton[5];
		
		
		
		for(int i = 0; i < m_categoryButtons.length;i++){
			try{
				Image bagcat = new Image("res/ui/bag/bag_normal.png");
				Image berriescat = new Image("res/ui/bag/berries_normal.png");
				Image pokecat = new Image("res/ui/bag/pokeballs_normal.png");
				Image potioncat = new Image("res/ui/bag/potions_normal.png");
				Image tmscat = new Image("res/ui/bag/tms_normal.png");
				
				if(i==0)
					m_categoryButtons[i] = new ImageButton(bagcat, bagcat, bagcat);
				else if(i==1)
					m_categoryButtons[i] = new ImageButton(potioncat, potioncat, potioncat);
				else if(i==2)
					m_categoryButtons[i] = new ImageButton(berriescat, berriescat, berriescat);
				else if(i==3)
					m_categoryButtons[i] = new ImageButton(pokecat, pokecat, pokecat);
				else if(i==4)
					m_categoryButtons[i] = new ImageButton(tmscat, tmscat, tmscat);
			}catch(Exception e){
				e.printStackTrace();
			}
			m_categoryButtons[i].setSize(40,40);
			if(i==0)
				m_categoryButtons[i].setLocation(80,10);
			else
				m_categoryButtons[i].setLocation(m_categoryButtons[i-1].getLocation().x+65, 10);
			m_categoryButtons[i].setFont(GameClient.getFontLarge());
			m_categoryButtons[i].setOpaque(false);
			m_categoryButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					categoryClicked(0);
				}
			});
			getContentPane().add(m_categoryButtons[i]);
		}
		
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
		
		Button leftbutton = new Button("<");
		leftbutton.setSize(20,40);
		leftbutton.setLocation(15,95);
		leftbutton.setFont(GameClient.getFontLarge());
		getContentPane().add(leftbutton);
		
		Button item0 = new Button("id0");
		item0.setSize(60,60);
		item0.setLocation(50,85);
		item0.setFont(GameClient.getFontLarge());
		getContentPane().add(item0);
		
		Label potionicon = new Label("  x20");
		potionicon.setSize(40,40);
		potionicon.setLocation(55,135);
		potionicon.setFont(GameClient.getFontLarge());
		getContentPane().add(potionicon);
		
		
		
		Button item1 = new Button("id1");
		item1.setSize(60,60);
		item1.setLocation(130,85);
		item1.setFont(GameClient.getFontLarge());
		getContentPane().add(item1);
		
		Label icon2 = new Label("  x15");
		icon2.setSize(40,40);
		icon2.setLocation(135,135);
		icon2.setFont(GameClient.getFontLarge());
		getContentPane().add(icon2);
		
		Button item2 = new Button("id2");
		item2.setSize(60,60);
		item2.setLocation(210,85);
		item2.setFont(GameClient.getFontLarge());
		getContentPane().add(item2);
		
		Label icon3 = new Label("  x10");
		icon3.setSize(40,40);
		icon3.setLocation(215,135);
		icon3.setFont(GameClient.getFontLarge());
		getContentPane().add(icon3);
		
		Button item3 = new Button("id3");
		item3.setSize(60,60);
		item3.setLocation(290,85);
		item3.setFont(GameClient.getFontLarge());
		getContentPane().add(item3);
		
		Label icon4 = new Label("  x0");
		icon4.setSize(40,40);
		icon4.setLocation(290,135);
		icon4.setFont(GameClient.getFontLarge());
		getContentPane().add(icon4);
			
		Button rightbutton = new Button(">");
		rightbutton.setSize(20,40);
		rightbutton.setLocation(365,95);
		rightbutton.setFont(GameClient.getFontLarge());
		getContentPane().add(rightbutton);
		
		m_cancel = new Button("Close");
		m_cancel.setSize(400,32);
		m_cancel.setLocation(0,195);
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
		this.setBackground(new Color(0, 0, 0, 75));
		setTitle("Bag");
		setResizable(false);
		setHeight(250);
		setWidth(m_categoryButtons.length*80);
		pack();
		setVisible(true);
		setCenter();
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
		int x = (width / 2) - 200;
		int y = (height / 2) - 200;
		this.setBounds(x, y, this.getWidth(), this.getHeight());
	}
}
