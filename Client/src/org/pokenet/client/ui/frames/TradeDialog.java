package org.pokenet.client.ui.frames;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.TextField;
import mdes.slick.sui.ToggleButton;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Pokemon;

/**
 * The trade interface
 * @author ZombieBear
 *
 */
public class TradeDialog extends Frame {
	private ToggleButton[] m_ourPokes;
	private ToggleButton[] m_theirPokes;
	private int[] m_pokes;
	private Button m_makeOfferBtn;
	private Button m_tradeBtn;
	private Button m_cancelBtn;
	private Label m_ourCashLabel;
	private Label m_theirMoneyOffer;
	private TextField m_ourMoneyOffer;
	private ActionListener m_offerListener;
	private ConfirmationDialog m_confirm;
	
	/**
	 * Default constructor
	 */
	public TradeDialog(int[] pokes, String trainerName){
		m_pokes = pokes;
		initGUI();
		setVisible(true);
		setTitle("Trade with " + trainerName);
		setCenter();
		GameClient.getInstance().getDisplay().add(this);
	}

	/**
	 * Sends the offer to the server
	 */
	private void makeOffer(){
		//TODO: Make the proper packet
		//GameClient.getInstance().getPacketGenerator().write("PACKET GOES HERE");
		m_makeOfferBtn.setText("Cancel Offer");
		for (int i = 0; i < 6; i++){
			m_ourPokes[i].setGlassPane(true);
		}
	}
	
	/**
	 * Cancels a sent offer
	 */
	private void cancelOffer(){
		//TODO: Make the proper packet
		//GameClient.getInstance().getPacketGenerator().write("PACKET GOES HERE");
		m_makeOfferBtn.setText("Make Offer");
		for (int i = 0; i < 6; i++){
			m_ourPokes[i].setGlassPane(false);
		}
	}
	
	/**
	 * Allows only one pokemon to be toggled
	 * @param btnIndex
	 */
	private void untoggleOthers(int btnIndex){
		for (int i = 0; i < 6; i++){
			if (i != btnIndex)
				m_ourPokes[i].setSelected(false);
		}
	}
	
	/**
	 * Performs the trade
	 */
	private void performTrade(){
		//TODO: Make the proper packet
		//GameClient.getInstance().getPacketGenerator().write("PACKET GOES HERE");
		System.out.println("Trade complete");
		this.setVisible(false);
	}
	
	/**
	 * Cancels the trade
	 */
	private void cancelTrade(){
		ActionListener yes = new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				//TODO: Send cancel packet
				m_confirm.setVisible(false);
				getDisplay().remove(m_confirm);
				m_confirm = null;
				setVisible(false);
				System.out.println("Trade Cancelled");
			}
		
		};
		ActionListener no = new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				getDisplay().remove(m_confirm);
				m_confirm = null;
			}
		
		};
		m_confirm = new ConfirmationDialog("Are you sure you want to cancel the trade?", yes, no);
	}
	
	/**
	 * Receives an offer
	 * @param index
	 * @param cash
	 */
	public void getOffer(int index, int cash){
		for (int i = 0; i < 6; i++){
			m_theirPokes[i].setSelected(false);
		}
		m_theirPokes[index].setSelected(true);
		m_theirMoneyOffer.setText("$" + cash);
	}
	
	/**
	 * Updates the UI when the other player cancels his/her offer
	 */
	public void cancelTheirOffer(){
		for (int i = 0; i < 6; i++){
			m_theirPokes[i].setSelected(false);
		}
		m_theirMoneyOffer.setText("$0");
	}
	
	/**
	 * Initializes the interface
	 */
	private void initGUI(){
		m_ourPokes = new ToggleButton[6];
		m_theirPokes = new ToggleButton[6];
		m_ourMoneyOffer = new TextField();
		m_makeOfferBtn = new Button();
		m_tradeBtn = new Button();
		m_cancelBtn = new Button();
		
		//Action Listener for the offer button
		m_offerListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_makeOfferBtn.getText().equalsIgnoreCase("Make Offer"))
					makeOffer();
				else 
					cancelOffer();
			}
		};
		
		int x = 10, y = 10;
		for (int i = 0; i < 6; i++){
			final int j = i;
			//Show Our Pokemon for Trade
			m_ourPokes[i] = new ToggleButton();
			m_ourPokes[i].setSize(32, 32);
			m_ourPokes[i].setVisible(true);
			try {
				GameClient.getInstance().getOurPlayer().getPokemon()[i].setIcon();
				m_ourPokes[i].setImage(GameClient.getInstance().getOurPlayer().getPokemon()[i].getIcon());
			} catch (NullPointerException e){
				m_ourPokes[i].setGlassPane(true);
				System.out.println("NO POKE: " + i);
			}

			m_ourPokes[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					m_ourPokes[j].setSelected(true);
					untoggleOthers(j);
				};
			});
			
			getContentPane().add(m_ourPokes[i]);
			if (i < 3)
				m_ourPokes[i].setLocation(x, y);
			else
				m_ourPokes[i].setLocation(x + 40, y);
			
			//Show the Other Character's Pokemon for Trade
			m_theirPokes[i] = new ToggleButton();
			m_theirPokes[i].setSize(32, 32);
			m_theirPokes[i].setVisible(true);
            LoadingList.setDeferredLoading(true);
            if (i < m_pokes.length){
            	try {
            		m_theirPokes[i].setImage(new Image(Pokemon.getIconPathByIndex(m_pokes[i])));
            	} catch (SlickException e){
            		System.out.println("CAN'T LOAD OTHER POKE IMAGE: " + i);
            	}
			}
            LoadingList.setDeferredLoading(false);
			m_theirPokes[i].setGlassPane(true);
			getContentPane().add(m_theirPokes[i]);

			//Item Location Algorithms
			if (i < 3)
				m_theirPokes[i].setLocation(x + 178, y);
			else
				m_theirPokes[i].setLocation(x + 218, y);
		
			if (i == 2)
				y = 10;
			else
				y += 40;
		}
		
		//UI Buttons
		m_makeOfferBtn.setText("Make Offer");
		m_makeOfferBtn.setSize(90, 30);
		m_makeOfferBtn.setLocation(90, 10);
		m_makeOfferBtn.addActionListener(m_offerListener);
		getContentPane().add(m_makeOfferBtn);
		
		m_tradeBtn.setText("Trade");
		m_tradeBtn.setEnabled(false);
		m_tradeBtn.setSize(90, 30);
		m_tradeBtn.setLocation(90, 50);
		m_tradeBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				ActionListener yes = new ActionListener(){
					public void actionPerformed(ActionEvent evt) {
						performTrade();
						setVisible(true);
					}
				
				};
				ActionListener no = new ActionListener(){
					public void actionPerformed(ActionEvent evt) {
						m_confirm.setVisible(false);
						getDisplay().remove(m_confirm);
						m_confirm = null;
						setVisible(true);
					}
				
				};
				m_confirm = new ConfirmationDialog("Are you sure you want to trade?", yes, no);
				setVisible(false);
			}
		});
		getContentPane().add(m_tradeBtn);
		
		m_cancelBtn.setText("Cancel Trade");
		m_cancelBtn.setSize(90, 30);
		m_cancelBtn.setLocation(90, 90);
		m_cancelBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				cancelTrade();
			};
		});
		getContentPane().add(m_cancelBtn);
		
		//Our money trade info
		m_ourCashLabel = new Label("$");
		m_ourCashLabel.pack();
		m_ourCashLabel.setLocation(10, 130);
		getContentPane().add(m_ourCashLabel);
		m_ourMoneyOffer = new TextField();
		m_ourMoneyOffer.setSize(60, 20);
		m_ourMoneyOffer.setLocation(20, 128);
		getContentPane().add(m_ourMoneyOffer);
		//Their money trade info
		m_theirMoneyOffer = new Label("$0");
		m_theirMoneyOffer.pack();
		m_theirMoneyOffer.setLocation(188, 130);
		getContentPane().add(m_theirMoneyOffer);
		
		//Window Settings
		getTitleBar().remove(getCloseButton());
		setSize(270,178);
		setResizable(false);
	}
	
	/**
	 * Centers the frame
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - ((int)getWidth()/2);
		int y = (height / 2) - ((int)getHeight()/2);
		this.setLocation(x, y);
	}
}
