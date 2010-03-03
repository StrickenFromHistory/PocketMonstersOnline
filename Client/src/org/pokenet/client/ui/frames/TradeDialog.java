package org.pokenet.client.ui.frames;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.TextField;
import mdes.slick.sui.ToggleButton;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.OurPokemon;
import org.pokenet.client.backend.entity.Pokemon;
import org.pokenet.client.ui.base.ConfirmationDialog;

/**
 * The trade interface
 * @author ZombieBear
 *
 */
public class TradeDialog extends Frame {
	private ToggleButton[] m_ourPokes;
	private ToggleButton[] m_theirPokes;
	private PokemonInfoDialog[] m_theirPokeInfo;
	private Button m_makeOfferBtn;
	private Button m_tradeBtn;
	private Button m_cancelBtn;
	private Label m_ourCashLabel;
	private Label m_theirMoneyOffer;
	private TextField m_ourMoneyOffer;
	private ActionListener m_offerListener;
	private ConfirmationDialog m_confirm;
	private int m_offerNum = 6;
	private boolean	m_madeOffer = false;
	private boolean	m_receivedOffer = false;

	
	/**
	 * Default constructor
	 */
	public TradeDialog(String trainerName){
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
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
		if(m_ourMoneyOffer.getText().equals("")) m_ourMoneyOffer.setText("0");
		
		if (!m_ourMoneyOffer.getText().equals("")){
			GameClient.getInstance().getPacketGenerator().writeTcpMessage("To" + m_offerNum + "," + 
					m_ourMoneyOffer.getText());
		} else {
			GameClient.getInstance().getPacketGenerator().writeTcpMessage("To" + m_offerNum + ",0");
		}
			
		m_makeOfferBtn.setText("Cancel Offer");
		for (int i = 0; i < 6; i++){
			m_ourPokes[i].setGlassPane(true);
		}
		
		m_madeOffer = true;
		if(m_receivedOffer) m_tradeBtn.setEnabled(true);
	}
	
	/**
	 * Cancels a sent offer
	 */
	private void cancelOffer(){
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("Tc");
		m_makeOfferBtn.setText("Make Offer");
		for (int i = 0; i < 6; i++){
			m_ourPokes[i].setGlassPane(false);
		}
		m_tradeBtn.setEnabled(false);
	}
	
	/**
	 * Allows only one pokemon to be toggled
	 * @param btnIndex
	 */
	private void untoggleOthers(int btnIndex){		
		for (int i = 0; i < 6; i++){
			if (i != btnIndex){
				m_ourPokes[i].setSelected(false);
				m_ourPokes[i].setBorderRendered(false);
			} else {
				m_ourPokes[btnIndex].setBorderRendered(true);
				m_ourPokes[btnIndex].setSelected(true);
			}
		}
	}
	
	/**
	 * Performs the trade
	 */
	private void performTrade(){
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("Tt");
		System.out.println("Trade complete");
		this.setVisible(false);
	}
	
	/**
	 * Cancels the trade
	 */
	private void cancelTrade(){
		ActionListener yes = new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				GameClient.getInstance().getPacketGenerator().writeTcpMessage("TC");
				m_confirm.setVisible(false);
				getDisplay().remove(m_confirm);
				m_confirm = null;
				setVisible(false);
				GameClient.getInstance().getUi().stopTrade();
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
			m_theirPokes[i].setBorderRendered(false);
			m_theirPokes[i].setSelected(false);
		}
		if (index < 6)
		{
			m_theirPokes[index].setSelected(true);
			m_theirPokes[index].setBorderRendered(true);
		}
		m_theirMoneyOffer.setText("$" + cash);
		m_receivedOffer  = true;
		if(m_madeOffer) m_tradeBtn.setEnabled(true);
	}
	
	/**
	 * Updates the UI when the other player cancels his/her offer
	 */
	public void cancelTheirOffer(){
		for (int i = 0; i < 6; i++){
			m_theirPokes[i].setSelected(false);
		}
		m_theirMoneyOffer.setText("$0");
		m_tradeBtn.setEnabled(false);
	}
	
	/**
	 * Initializes the interface
	 */
	private void initGUI(){
		m_ourPokes = new ToggleButton[6];
		m_theirPokes = new ToggleButton[6];
		m_theirPokeInfo = new PokemonInfoDialog[6];
		m_ourMoneyOffer = new TextField();
		m_makeOfferBtn = new Button();
		m_tradeBtn = new Button();
		m_cancelBtn = new Button();
		
		//Action Listener for the offer button
		m_offerListener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_makeOfferBtn.getText().equalsIgnoreCase("Make Offer")){
					if(m_ourMoneyOffer.getText().equals("") || m_ourMoneyOffer.getText() == null){
						m_ourMoneyOffer.setText("0");
					}
					makeOffer();
				}
				else {
					cancelOffer();
				}
			}
		};
		
		int x = 10, y = 10;
		for (int i = 0; i < 6; i++){
			//Show Our Pokemon for Trade
			m_ourPokes[i] = new ToggleButton();
			m_ourPokes[i].setSize(32, 32);
			m_ourPokes[i].setVisible(true);
			try {
				m_ourPokes[i].setImage(GameClient.getInstance().getOurPlayer()
						.getPokemon()[i].getIcon());
			} catch (NullPointerException e){
				m_ourPokes[i].setGlassPane(true);
			}
			
			getContentPane().add(m_ourPokes[i]);
			if (i < 3)
				m_ourPokes[i].setLocation(x, y);
			else
				m_ourPokes[i].setLocation(x + 40, y);
			
			//Show the Other Character's Pokemon for Trade
			m_theirPokes[i] = new ToggleButton();
			m_theirPokes[i].setSize(32, 32);
			m_theirPokes[i].setVisible(true);
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
		m_ourPokes[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 0){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 0;
					untoggleOthers(0);
				}
				m_makeOfferBtn.setEnabled(true);

			};
		});
		m_ourPokes[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 1){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 1;
					untoggleOthers(1);
				}
				m_makeOfferBtn.setEnabled(true);

			};
		});
		m_ourPokes[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 2){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 2;
					untoggleOthers(2);
				}
				m_makeOfferBtn.setEnabled(true);

			};
		});
		m_ourPokes[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 3){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 3;
					untoggleOthers(3);
				}
				m_makeOfferBtn.setEnabled(true);

			};
		});
		m_ourPokes[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 4){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 4;
					untoggleOthers(4);
				}
				
				m_makeOfferBtn.setEnabled(true);
			};
		});
		m_ourPokes[5].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (m_offerNum == 5){
					m_offerNum = 6;
					untoggleOthers(6);
				} else {
					m_offerNum = 5;
					untoggleOthers(5);
				}
				
				m_makeOfferBtn.setEnabled(true);
			};
		});
		
		//UI Buttons
		m_makeOfferBtn.setText("Make Offer");
		m_makeOfferBtn.setSize(90, 30);
		m_makeOfferBtn.setLocation(90, 10);
		m_makeOfferBtn.setEnabled(false);
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
					 	getDisplay().remove(m_confirm);
						m_confirm = null;
						setVisible(false);
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
	
	/**
	 * Adds a pokemon to the other player's side
	 * @param data
	 */
	public void addPoke(int index, String[] data) {
        final int j = index;
		LoadingList.setDeferredLoading(true);
		int ic = Integer.parseInt(data[0]);
		if(ic > 389) {
			ic -= 2;
		} else {
			ic ++;
		}
        try {
        	m_theirPokes[index].setImage(new Image(Pokemon.getIconPathByIndex(ic)));
        } catch (SlickException e){}
        LoadingList.setDeferredLoading(false);
        
        // Load pokemon data
        OurPokemon tempPoke = new OurPokemon().initTradePokemon(data);
        
        // Create a pokemon information panel with stats
        // for informed decisions during trade
        m_theirPokeInfo[index] = new PokemonInfoDialog(tempPoke);
        m_theirPokeInfo[index].setVisible(false);
        m_theirPokeInfo[index].setAlwaysOnTop(true);
        m_theirPokeInfo[index].setLocation(m_theirPokes[index].getX(),
        		m_theirPokes[index].getY() + 32);
        GameClient.getInstance().getDisplay().add(m_theirPokeInfo[index]);
        m_theirPokes[index].addMouseListener(new MouseAdapter() {
        	@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				m_theirPokeInfo[j].setVisible(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				m_theirPokeInfo[j].setVisible(false);
			}
        });
	}
}
