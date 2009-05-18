package org.pokenet.client.ui;

import java.util.HashMap;

import mdes.slick.sui.Container;
import mdes.slick.sui.Label;
import mdes.slick.sui.Timer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.ui.base.ProgressBar;

/**
 * Canvas for drawing the battle and it's animations
 * 
 * @author ZombieBear
 * 
 */
public class BattleCanvas extends Container {
	private ProgressBar playerHP = new ProgressBar(0,0);
	private ProgressBar enemyHP = new ProgressBar(0,0);
	private Label bgPic = new Label();
	private Label playerPoke = new Label();
	private Label enemyPoke = new Label();
	private Label playerNameLabel = new Label();
	private Label enemyNameLabel = new Label();
	private Label playerDataBG = new Label();
	private Label enemyDataBG = new Label();
	private Label playerHPBar = new Label();
	private Label enemyHPBar = new Label();
	private Label playerLv = new Label();
	private Label enemyLv = new Label();
	private Label playerStatus = new Label();
	private Label enemyStatus = new Label();
	private HashMap<String, Image> m_statusIcons = new HashMap<String, Image>();

	/**
	 * Default constructor
	 */
	public BattleCanvas(){
		setSize(257, 144);
		setVisible(true);
		loadStatusIcons();
		positionCanvas();
		drawBackground();
		drawOurPoke();
		drawOurInfo();
	}
	
	/**
	 * Draws our Pokemon
	 */
	public void drawOurPoke(){
		//TODO: Animate!
		playerPoke = new Label();
		GameClient.getInstance().getUi().getBattleManager().getCurPoke().setBackSprite();
		playerPoke = new Label(GameClient.getInstance().getUi().getBattleManager().getCurPoke().getBackSprite());
		playerPoke.setSize(80, 80);
		playerPoke.setLocation(20, 76);
		add(playerPoke);
	}
	
	/**
	 * Draws the enemy's Pokemon
	 */
	public void drawEnemyPoke(){
		//TODO: Animate!
		try{
			enemyPoke = new Label (GameClient.getInstance().getUi().getBattleManager()
					.getCurEnemyPoke().getSprite());
			enemyPoke.setSize(80, 80);
			enemyPoke.setLocation(150, 21);
			add(enemyPoke);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Draw our poke's information
	 */
	public void drawOurInfo(){
		// display player's data
		playerNameLabel.setFont(GameClient.getTrueTypeFont());
		playerNameLabel.setText(GameClient.getInstance().getUi().getBattleManager()
				.getCurPoke().getName());
        playerNameLabel.setSize(GameClient.getTrueTypeFont().getWidth(playerNameLabel
        		.getText()), GameClient.getTrueTypeFont().getHeight(playerNameLabel
        				.getText()));
        playerNameLabel.setLocation(playerDataBG.getX() + 30, playerDataBG.getY() + 7);
        
        playerLv = new Label("Lv:" + GameClient.getInstance().getUi().getBattleManager()
        		.getCurPoke().getLevel());
        playerLv.setFont(GameClient.getTrueTypeFont());
        playerLv.setSize(GameClient.getTrueTypeFont().getWidth(playerLv.getText()),
        		GameClient.getTrueTypeFont().getHeight(playerLv.getText()));
        playerLv.setLocation(playerDataBG.getX() + playerDataBG.getWidth() 
        		- playerLv.getWidth() - 5, playerDataBG.getY() + 7);
        
        playerStatus.setSize(30, 12);
        playerStatus.setLocation(playerNameLabel.getX(), 125);
        
        add(playerNameLabel);
        add(playerLv);
        add(playerStatus);
        initPlayerHPBar();
	}
	
	/**
	 * Draw our enemy poke's information
	 */
	public void drawEnemyInfo(){
        //display enemy's data
		enemyNameLabel.setText(GameClient.getInstance().getUi().getBattleManager()
				.getCurEnemyPoke().getName());
		enemyNameLabel.setFont(GameClient.getTrueTypeFont());
		enemyNameLabel.setSize(GameClient.getTrueTypeFont().getWidth(enemyNameLabel.getText()),
        		GameClient.getTrueTypeFont().getHeight(enemyNameLabel.getText()));
        enemyNameLabel.setLocation(enemyDataBG.getX() + 15, enemyDataBG.getY() + 7);

        enemyLv = new Label("Lv: " + GameClient.getInstance().getUi().getBattleManager()
        		.getCurEnemyPoke().getLevel());
        enemyLv.setFont(GameClient.getTrueTypeFont());
        enemyLv.setSize(GameClient.getTrueTypeFont().getWidth(enemyLv.getText()),
        		GameClient.getTrueTypeFont().getHeight(enemyLv.getText()));
        enemyLv.setLocation(enemyDataBG.getX() + enemyDataBG.getWidth() - enemyLv.getWidth()
        		- 25, enemyDataBG.getY() + 7);

        enemyStatus.setSize(30, 12);
        enemyStatus.setLocation(105, 40);
        
        add(enemyNameLabel);
        add(enemyLv);
        add(enemyStatus);
        initEnemyHPBar();
	}
	
	/**
	 * Updates the HP bar for the player's poke
	 * @param newValue
	 */
	public void updatePlayerHP(int newValue) {              
        playerHP.setValue(newValue);
        
        if(GameClient.getInstance().getUi().getBattleManager().getCurPoke().getCurHP() 
        		> GameClient.getInstance().getUi().getBattleManager().getCurPoke().getMaxHP() / 2){
                playerHP.setForeground(Color.green);
        }
        else if(GameClient.getInstance().getUi().getBattleManager().getCurPoke().getCurHP() 
        		< GameClient.getInstance().getUi().getBattleManager().getCurPoke().getMaxHP() / 2 
        		&& GameClient.getInstance().getUi().getBattleManager().getCurPoke().getCurHP() 
        		> GameClient.getInstance().getUi().getBattleManager().getCurPoke().getMaxHP() / 3){
                playerHP.setForeground(Color.orange);
        }
        else if(GameClient.getInstance().getUi().getBattleManager().getCurPoke().getCurHP() 
        		< GameClient.getInstance().getUi().getBattleManager().getCurPoke().getMaxHP() / 3){
                playerHP.setForeground(Color.red);
        }
	}

	/**
	 * Updates the HP bar for the opponent's poke
	 * @param newValue
	 */
	public void updateEnemyHP(int newValue) {
		enemyHP.setValue(newValue);

		if(GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getCurHP() 
				> GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getMaxHP() / 2){
			enemyHP.setForeground(Color.green);
		}
		else if(GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getCurHP() 
				< GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getMaxHP() / 2 
				&& GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getCurHP() 
				> GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getMaxHP() / 3){
			enemyHP.setForeground(Color.orange);
		}
		else if(GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getCurHP() 
				< GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getMaxHP() / 3){
			enemyHP.setForeground(Color.red);
		}
	}

	
	/**
	 * Draws the background
	 */
	public void drawBackground(){
		LoadingList.setDeferredLoading(true);
		try{
			bgPic = new Label(new Image("/res/ui/DP_darkgrass.png"));
			playerDataBG = new Label(new Image("/res/battle/singlePlayerBox2.png"));
			enemyDataBG = new Label(new Image("/res/battle/singleEnemyBox2.png"));
			enemyHPBar = new Label(new Image("/res/battle/HPBar.png"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		add(bgPic);
		add(playerDataBG);
		add(enemyDataBG);
		bgPic.setBounds(0, 0, 256, 144);
		playerDataBG.setBounds(82, 96, 170, 48);
		enemyDataBG.setBounds(-10, 10, 170, 48);
	}
	
	/**
	 * Starts the enemy HP Bar
	 */
	public void initEnemyHPBar(){
		// show enemy hp bar
        enemyHP = new ProgressBar(0, (int)GameClient.getInstance().getUi().getBattleManager()
        		.getCurEnemyPoke().getMaxHP());
        enemyHP.setSize(72, 5);
        
        if(GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getCurHP() 
        		> GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getMaxHP() / 2){
                enemyHP.setForeground(Color.green);
        }
        else if(GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getCurHP() 
        		< GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getMaxHP() / 2 
        		&& GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getCurHP() 
        		> GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getMaxHP() / 3){
                enemyHP.setForeground(Color.orange);
        }
        else if(GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getCurHP() 
        		< GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getMaxHP() / 3){
                enemyHP.setForeground(Color.red);
        }
        updateEnemyHP(GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getCurHP());
        
        LoadingList.setDeferredLoading(true);
		try {
			enemyHPBar = new Label(new Image("/res/battle/HPBar.png"));
		} catch (SlickException e) {}
		LoadingList.setDeferredLoading(false);
		enemyHPBar.setBounds(enemyNameLabel.getX(), 40, 98, 11);
		enemyHP.setLocation(enemyHPBar.getX() + 23, enemyHPBar.getY() + 3);
		
		add(enemyHPBar);
		add(enemyHP);
	}
	
	/**
	 * Starts the player's HP bar
	 */
	public void initPlayerHPBar(){
		// show hp bar
        playerHP = new ProgressBar(0, (int)GameClient.getInstance().getUi().getBattleManager()
        		.getCurPoke().getMaxHP());
        playerHP.setSize(72, 5);

        if(GameClient.getInstance().getUi().getBattleManager().getCurPoke().getCurHP() 
        		> GameClient.getInstance().getUi().getBattleManager().getCurPoke().getMaxHP() / 2){
                playerHP.setForeground(Color.green);
        }
        else if(GameClient.getInstance().getUi().getBattleManager().getCurPoke().getCurHP() 
        		< GameClient.getInstance().getUi().getBattleManager().getCurPoke().getMaxHP() / 2 
        		&& GameClient.getInstance().getUi().getBattleManager().getCurPoke().getCurHP() 
        		> GameClient.getInstance().getUi().getBattleManager().getCurPoke().getMaxHP() / 3){
                playerHP.setForeground(Color.orange);
        }
        else if(GameClient.getInstance().getUi().getBattleManager().getCurPoke().getCurHP() 
        		< GameClient.getInstance().getUi().getBattleManager().getCurPoke().getMaxHP() / 3){
                playerHP.setForeground(Color.red);
        }

        updatePlayerHP(GameClient.getInstance().getUi().getBattleManager().getCurPoke().getCurHP());
        
        LoadingList.setDeferredLoading(true);
		try {
			playerHPBar = new Label(new Image("/res/battle/HPBar.png"));
		} catch (SlickException e) {}
		LoadingList.setDeferredLoading(false);
		playerHPBar.setBounds(playerLv.getX() + playerLv.getWidth() - 98, 125, 98, 11);
		playerHP.setLocation(playerHPBar.getX() + 23, playerHPBar.getY() + 3);
		
		add(playerHPBar);
		add(playerHP);
	}
	
	/**
	 * Sets the status image
	 * @param poke
	 * @param status
	 */
	public void setStatus(int poke, String status){
		if (poke == 0){
			if (status != "normal") {
				playerStatus.setImage(m_statusIcons.get(status));
			} else {
				playerStatus.setImage(null);
			}
		} else {
			if (status != "normal") {
				enemyStatus.setImage(m_statusIcons.get(status));
			} else {
				enemyStatus.setImage(null);
			}
		}
	}
	
	/**
	 * Loads the status icons
	 */
	public void loadStatusIcons(){
		LoadingList.setDeferredLoading(true);
		try{
			m_statusIcons.put("poison", new Image("/res/battle/PSN.png"));
			m_statusIcons.put("sleep", new Image("/res/battle/SLP.png"));
			m_statusIcons.put("frozen", new Image("/res/battle/FRZ.png"));
			m_statusIcons.put("burnt", new Image("/res/battle/BRN.png"));
			m_statusIcons.put("paralyzed", new Image("/res/battle/PAR.png"));
		} catch (SlickException e) {}
		LoadingList.setDeferredLoading(true);
	}
	
	/**
	 * Centers the battle window
	 */
	public void positionCanvas() {
		float y = GameClient.getInstance().getUi().getBattleManager().getBattleWindow().getY() + 20;
		float x = GameClient.getInstance().getUi().getBattleManager().getBattleWindow().getX() + 1;
		setLocation(x, y);
	}
}
