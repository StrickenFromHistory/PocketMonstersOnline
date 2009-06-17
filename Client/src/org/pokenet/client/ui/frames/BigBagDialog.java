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
	
	public void initGUI() {
		m_categoryButtons = new Button[5];
		m_categoryLabels = new Label[5];
		
		for(int i = 0; i < m_categoryButtons.length;i++){
			m_categoryLabels[i] = new Label(i+"");
			if(i==0)
				m_categoryLabels[i].setLocation(0,0);
			else
				m_categoryLabels[i].setLocation(m_categoryLabels[i-1].getLocation().x+50, 80);
			m_categoryLabels[i].setGlassPane(true);
			m_categoryLabels[i].setZIndex(1000);
			m_categoryLabels[i].setSize(40,40);
			m_categoryLabels[i].setFont(GameClient.getFontLarge());
			
			m_categoryButtons[i] = new Button(" ");
			LoadingList.setDeferredLoading(true);
//			try{
//				m_categoryButtons[0].setImage(new Image("res/ui/shop/pokeball.png"));
//			}catch(Exception e){
//				e.printStackTrace();
//			}
			LoadingList.setDeferredLoading(false);
			m_categoryButtons[i].setSize(40, 40);
			if(i==0)
				m_categoryButtons[i].setLocation(80,10);
			else
				m_categoryButtons[i].setLocation(m_categoryButtons[i-1].getLocation().x+65, 10);
			m_categoryButtons[i].setFont(GameClient.getFontLarge());
			m_categoryButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					categoryClicked(0);
				}
			});
			m_categoryButtons[i].add(m_categoryLabels[i]);
			getContentPane().add(m_categoryButtons[i]);
		}
		
		Label bagicon = new Label("Bag");
		bagicon.setSize(40,40);
		bagicon.setLocation(20,10);
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
