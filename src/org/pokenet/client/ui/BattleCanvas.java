package org.pokenet.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mdes.slick.sui.Container;
import mdes.slick.sui.Label;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.BattleManager;
import org.pokenet.client.ui.base.ProgressBar;

/**
 * Canvas for drawing the battle and it's animations
 * 
 * @author ZombieBear
 * 
 */
public class BattleCanvas extends Container {
	private ProgressBar playerHP;
	private ProgressBar enemyHP;
	private Label bgPic;
	private Label playerPoke;
	private Label enemyPoke;
	private Label playerNameLabel;
	private Label enemyNameLabel;
	private Label playerDataBG;
	private Label enemyDataBG;
	private Label playerHPBar;
	private Label enemyHPBar;
	private Label playerLv;
	private Label enemyLv;
	private Label playerStatus;
	private Label enemyStatus;
	private List<Label> m_enemyPokeballs = new ArrayList<Label>();
	private HashMap<String, Image> m_statusIcons = new HashMap<String, Image>();
	private HashMap<String, Image> m_pokeballIcons = new HashMap<String, Image>();
	// Image Loading stuff
	private String m_path = "res/battle/";
	
	/**
	 * Default constructor
	 */
	public BattleCanvas(){
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		m_path = respath+m_path;
		setSize(257, 144);
		setVisible(true);
		loadImages();
		startPokeballs();
		loadStatusIcons();
	}
	
	/**
	 * Draws our Pokemon
	 */
	public void drawOurPoke(){
		//TODO: Animate!
		try {
			remove(playerPoke);
		} catch (Exception e) {}
		playerPoke = new Label();
		playerPoke = new Label(BattleManager.getInstance().getCurPoke().getBackSprite());
		playerPoke.setSize(80, 80);
		playerPoke.setLocation(20, 76);
		add(playerPoke);
	}

	/**
	 * Starts a battle
	 */
	public void startBattle() {
		initComponents();
		positionCanvas();
		drawBackground();
		drawOurPoke();
		drawOurInfo();
	}
	
	/**
	 * Initializes the pokeballs for trainer battles
	 */
	public void startPokeballs(){
		m_enemyPokeballs.clear();
		int x = 1;
		for (int i = 0; i < 6; i++){
			m_enemyPokeballs.add(new Label());
			m_enemyPokeballs.get(i).setSize(14, 14);
			m_enemyPokeballs.get(i).setImage(m_pokeballIcons.get("empty"));
			m_enemyPokeballs.get(i).setLocation(125 + 14 * x + x * 5, 3);
			x++;
		}
	}
	
	/**
	 * Loads images that can't be loading on startBattle()
	 */
	public void loadImages(){
        LoadingList.setDeferredLoading(true);
		try {
			enemyHPBar = new Label(new Image( m_path + "HPBar.png", false));
			playerHPBar = new Label(new Image( m_path + "HPBar.png", false));
		} catch (SlickException e) {}
		try{
			m_pokeballIcons.put("empty", new Image(m_path + "ballempty" + ".png", false));
			m_pokeballIcons.put("normal", new Image(m_path + "ballnormal" + ".png", false));
			m_pokeballIcons.put("status", new Image(m_path + "ballstatus" + ".png", false));
			m_pokeballIcons.put("fainted", new Image(m_path + "ballfainted" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();}
		LoadingList.setDeferredLoading(false);
		enemyHPBar.setSize(98, 11);
		playerHPBar.setSize(98, 11);
	}
	
	/**
	 * Draws the enemy's Pokemon
	 */
	public void drawEnemyPoke(){
		//TODO: Animate!
		try{
			try {
				remove(enemyPoke);
			} catch (Exception e) {}
			enemyPoke = new Label (BattleManager.getInstance()
					.getCurEnemyPoke().getSprite());
			enemyPoke.setSize(80, 80);
			enemyPoke.setLocation(150, 21);
			add(enemyPoke);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initComponents() {
		playerHP = new ProgressBar(0,0);
		enemyHP = new ProgressBar(0,0);
		bgPic = new Label();
		playerPoke = new Label();
		enemyPoke = new Label();
		playerNameLabel = new Label();
		enemyNameLabel = new Label();
		playerDataBG = new Label();
		enemyDataBG = new Label();
		playerLv = new Label();
		enemyLv = new Label();
		playerStatus = new Label();
		enemyStatus = new Label();
	}
	
	/**
	 * Draw our poke's information
	 */
	public void drawOurInfo(){
		// display player's data
		playerNameLabel.setFont(GameClient.getFontSmall());
		playerNameLabel.setForeground(Color.white);
		playerNameLabel.setText(BattleManager.getInstance()
				.getCurPoke().getName());
        playerNameLabel.setSize(GameClient.getFontSmall().getWidth(playerNameLabel
        		.getText()), GameClient.getFontSmall().getHeight(playerNameLabel
        				.getText()));
        playerNameLabel.setLocation(playerDataBG.getX() + 30, playerDataBG.getY() + 7);
        
        playerLv.setText("Lv:" + BattleManager.getInstance()
        		.getCurPoke().getLevel());
        playerLv.setFont(GameClient.getFontSmall());
        playerLv.setForeground(Color.white);
        playerLv.setSize(GameClient.getFontSmall().getWidth(playerLv.getText()),
        		GameClient.getFontSmall().getHeight(playerLv.getText()));
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
		enemyNameLabel.setText(BattleManager.getInstance()
				.getCurEnemyPoke().getName());
		enemyNameLabel.setFont(GameClient.getFontSmall());
		enemyNameLabel.setForeground(Color.white);
		enemyNameLabel.setSize(GameClient.getFontSmall().getWidth(enemyNameLabel.getText()),
				GameClient.getFontSmall().getHeight(enemyNameLabel.getText()));
        enemyNameLabel.setLocation(enemyDataBG.getX() + 15, enemyDataBG.getY() + 7);

        enemyLv.setText("Lv: " + BattleManager.getInstance()
        		.getCurEnemyPoke().getLevel());
        enemyLv.setFont(GameClient.getFontSmall());
        enemyLv.setForeground(Color.white);
        enemyLv.setSize(GameClient.getFontSmall().getWidth(enemyLv.getText()),
        		GameClient.getFontSmall().getHeight(enemyLv.getText()));
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
        
        if(BattleManager.getInstance().getCurPoke().getCurHP() 
        		> BattleManager.getInstance().getCurPoke().getMaxHP() / 2){
                playerHP.setForeground(Color.green);
        }
        else if(BattleManager.getInstance().getCurPoke().getCurHP() 
        		< BattleManager.getInstance().getCurPoke().getMaxHP() / 2 
        		&& BattleManager.getInstance().getCurPoke().getCurHP() 
        		> BattleManager.getInstance().getCurPoke().getMaxHP() / 3){
                playerHP.setForeground(Color.orange);
        }
        else if(BattleManager.getInstance().getCurPoke().getCurHP() 
        		< BattleManager.getInstance().getCurPoke().getMaxHP() / 3){
                playerHP.setForeground(Color.red);
        }
	}

	/**
	 * Updates the HP bar for the opponent's poke
	 * @param newValue
	 */
	public void updateEnemyHP(int newValue) {
		enemyHP.setValue(newValue);

		if(BattleManager.getInstance().getCurEnemyPoke().getCurHP() 
				> BattleManager.getInstance().getCurEnemyPoke().getMaxHP() / 2){
			enemyHP.setForeground(Color.green);
		}
		else if(BattleManager.getInstance().getCurEnemyPoke().getCurHP() 
				< BattleManager.getInstance().getCurEnemyPoke().getMaxHP() / 2 
				&& BattleManager.getInstance().getCurEnemyPoke().getCurHP() 
				> BattleManager.getInstance().getCurEnemyPoke().getMaxHP() / 3){
			enemyHP.setForeground(Color.orange);
		}
		else if(BattleManager.getInstance().getCurEnemyPoke().getCurHP() 
				< BattleManager.getInstance().getCurEnemyPoke().getMaxHP() / 3){
			enemyHP.setForeground(Color.red);
		}
	}

	
	/**
	 * Draws the background
	 */
	public void drawBackground(){
		LoadingList.setDeferredLoading(true);
		String respath = System.getProperty("res.path");
		if(respath == null || respath.equals("null"))
			respath="";
		try {
			bgPic = new Label(new Image(respath+"res/ui/DP_darkgrass.png", false));
		} catch (SlickException e) {
			e.printStackTrace();
		} try {
			playerDataBG = new Label(new Image(respath+"res/battle/singlePlayerBox3.png", false));
		} catch (SlickException e) {
			e.printStackTrace();
		} try {
			enemyDataBG = new Label(new Image(respath+"res/battle/singleEnemyBox3.png", false));
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
        enemyHP = new ProgressBar(0, (int)BattleManager.getInstance()
        		.getCurEnemyPoke().getMaxHP());
        enemyHP.setSize(72, 5);
        
        if(BattleManager.getInstance().getCurEnemyPoke().getCurHP() 
        		> BattleManager.getInstance().getCurEnemyPoke().getMaxHP() / 2){
                enemyHP.setForeground(Color.green);
        }
        else if(BattleManager.getInstance().getCurEnemyPoke().getCurHP() 
        		< BattleManager.getInstance().getCurEnemyPoke().getMaxHP() / 2 
        		&& BattleManager.getInstance().getCurEnemyPoke().getCurHP() 
        		> BattleManager.getInstance().getCurEnemyPoke().getMaxHP() / 3){
                enemyHP.setForeground(Color.orange);
        }
        else if(BattleManager.getInstance().getCurEnemyPoke().getCurHP() 
        		< BattleManager.getInstance().getCurEnemyPoke().getMaxHP() / 3){
                enemyHP.setForeground(Color.red);
        }
        updateEnemyHP(BattleManager.getInstance().getCurEnemyPoke().getCurHP());
        
        enemyHPBar.setLocation(enemyNameLabel.getX(), 40); 
		enemyHP.setLocation(enemyHPBar.getX() + 23, enemyHPBar.getY() + 3);
		
		add(enemyHPBar);
		add(enemyHP);
	}
	
	/**
	 * Starts the player's HP bar
	 */
	public void initPlayerHPBar(){
		// show hp bar
        playerHP = new ProgressBar(0, (int)BattleManager.getInstance()
        		.getCurPoke().getMaxHP());
        playerHP.setSize(72, 5);

        if(BattleManager.getInstance().getCurPoke().getCurHP() 
        		> BattleManager.getInstance().getCurPoke().getMaxHP() / 2){
                playerHP.setForeground(Color.green);
        }
        else if(BattleManager.getInstance().getCurPoke().getCurHP() 
        		< BattleManager.getInstance().getCurPoke().getMaxHP() / 2 
        		&& BattleManager.getInstance().getCurPoke().getCurHP() 
        		> BattleManager.getInstance().getCurPoke().getMaxHP() / 3){
                playerHP.setForeground(Color.orange);
        }
        else if(BattleManager.getInstance().getCurPoke().getCurHP() 
        		< BattleManager.getInstance().getCurPoke().getMaxHP() / 3){
                playerHP.setForeground(Color.red);
        }

        updatePlayerHP(BattleManager.getInstance().getCurPoke().getCurHP());
        
        playerHPBar.setLocation(playerLv.getX() + playerLv.getWidth() - 98, 125); 
		playerHP.setLocation(playerHPBar.getX() + 23, playerHPBar.getY() + 3);
		
		add(playerHPBar);
		add(playerHP);
	}
	
	/**
	 * Sets the status image
	 * @param trainer
	 * @param status
	 */
	public void setStatus(int trainer, String status){
		if (trainer == 0){
			// The player's pokemon
			if (status != "normal") {
				BattleManager.getInstance().getOurStatuses().put(
						BattleManager.getInstance().getCurPokeIndex(), status);
				playerStatus.setImage(m_statusIcons.get(status));
			} else {
				BattleManager.getInstance().getOurStatuses().remove(
						BattleManager.getInstance().getCurPokeIndex());
				playerStatus.setImage(null);
			}
		} else {
			// The enemy's pokemon
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
			m_statusIcons.put("Poison", new Image( m_path + "PSN" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Sleep", new Image( m_path + "SLP" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Freze", new Image( m_path + "FRZ" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Burn", new Image( m_path + "BRN" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Paralysis", new Image(m_path + "PAR" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();}
		LoadingList.setDeferredLoading(false);
	}
	
	/**
	 * Shows pokeballs
	 */
	public void showPokeballs(){
		for (Label l : m_enemyPokeballs){
			if (!containsChild(l))
				add(l);
		}
	}
	
	/**
	 * Hides pokeballs
	 */
	public void hidePokeballs(){
		for (Label l : m_enemyPokeballs){
			l.setImage(m_pokeballIcons.get("empty"));
			try{
				remove(l);
			} catch (Exception e){}
		}
	}
	
	/**
	 * Sets the image for the pokeballs
	 * @param i
	 * @param key
	 */
	public void setPokeballImage(int i, String key){
		m_enemyPokeballs.get(i).setImage(m_pokeballIcons.get(key));
	}
	
	/**
	 * Centers the battle window
	 */
	public void positionCanvas() {
		float y = BattleManager.getInstance().getBattleWindow().getY() 
		 	+ BattleManager.getInstance().getBattleWindow().getTitleBar().getHeight();
		float x = BattleManager.getInstance().getBattleWindow().getX() + 1;
		setLocation(x, y);
	}
	
	/**
	 * Stops the canvas
	 */
	public void stop() {
		this.removeAll();
		playerHP = null;
		enemyHP = null;
		bgPic = null;
		playerPoke = null;
		enemyPoke = null;
		playerNameLabel = null;
		enemyNameLabel = null;
		playerDataBG = null;
		enemyDataBG = null;
		playerLv = null;
		enemyLv = null;
		playerStatus = null;
		enemyStatus = null;
		hidePokeballs();
	}
}
