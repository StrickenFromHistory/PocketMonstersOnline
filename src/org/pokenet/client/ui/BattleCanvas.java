package org.pokenet.client.ui;

import mdes.slick.sui.Container;
import mdes.slick.sui.Label;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Pokemon;
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
/*	private String ourSpritePath;
	private String enemySpritePath;
	private String checkOurShiny;
	private String checkEnemyShiny;*/
	private Label bgPic = new Label();
	private Label playerPoke = new Label();
	private Label enemyPoke = new Label();
	private Label playerInfo = new Label();
	private Label enemyInfo = new Label();
//	private Image pic;
//	private Image playerPokeImage;
//	private Image enemyPokeImage;

	/**
	 * Default constructor
	 */
	public BattleCanvas(){
		setSize(257, 144);
		setVisible(true);
		positionCanvas();
		drawBackground();
		drawOurPoke();
		drawEnemyPoke();
		drawOurInfo();
		drawEnemyInfo();
	}
	
	/**
	 * Draws our Pokemon
	 */
	public void drawOurPoke(){
		//TODO: Animate!
		playerPoke = new Label(GameClient.getInstance().getOurPlayer().getPokemon()
				[GameClient.getInstance().getUi().getBattleManager().getCurPokeIndex()].getBackSprite());
		playerPoke.setSize(80, 80);
		playerPoke.setLocation(0, 76);
		add(playerPoke);
	}
	
	/**
	 * Draws the enemy's Pokemon
	 */
	public void drawEnemyPoke(){
		//TODO: Animate!
		enemyPoke = new Label("POKE");
		LoadingList.setDeferredLoading(true);
		enemyPoke.setSize(80, 80);
		enemyPoke.setLocation(150, 21);
		LoadingList.setDeferredLoading(false);
		add(enemyPoke);
	}
	
	/**
	 * Draw our poke's information
	 */
	public void drawOurInfo(){
		// display player's data
		String info = GameClient.getInstance().getUi().getBattleManager().getCurPoke().getName() 
			+ "  Lv:" + GameClient.getInstance().getUi().getBattleManager().getCurPoke().getLevel();
		playerInfo.setText(info);
        
		playerInfo.setForeground(Color.white);
        playerInfo.setBounds(152, 109, 94, 12);
		
		// show hp bar
        playerHP = new ProgressBar(0, (int)GameClient.getInstance().getUi().getBattleManager()
        		.getCurPoke().getMaxHP());
        playerHP.setBounds(150, 126, 95, 10);

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
        add(playerInfo);
        add(playerHP);
	}
	
	/**
	 * Draw our enemy poke's information
	 */
	public void drawEnemyInfo(){
        //display enemy's data
		enemyInfo.setText(GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getName()
        		+ "  Lv:" + GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getLevel());
        enemyInfo.setForeground(Color.white);
        enemyInfo.setBounds(12, 12, 96, 12);
        
        // show enemy hp bar
        enemyHP = new ProgressBar(0, (int)GameClient.getInstance().getUi().getBattleManager()
        		.getCurEnemyPoke().getMaxHP());
        enemyHP.setBounds(11, 27, 95, 10);
        
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

        add(enemyInfo);
        add(enemyHP);
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
		bgPic = new Label();
		LoadingList.setDeferredLoading(true);
		try{
			bgPic = new Label(new Image("/res/ui/DP_darkgrass.png"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);
		add(bgPic);
		bgPic.setBounds(0, 0, 256, 144);
		bgPic.setVisible(true);
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
