package org.pokenet.client.ui;

import mdes.slick.sui.Container;
import mdes.slick.sui.Label;

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
	private ProgressBar playerHP;
	private ProgressBar enemyHP;
	private String ourSpritePath;
	private String enemySpritePath;
	private String checkOurShiny;
	private String checkEnemyShiny;
	private Label bgPic = new Label();
	private Label playerPoke = new Label();
	private Label enemyPoke = new Label();
	private Label playerInfo = new Label();
	private Label enemyInfo = new Label();
	private Image pic;
	private Image playerPokeImage;
	private Image enemyPokeImage;

	/**
	 * Default constructor
	 */
	public BattleCanvas(){
		setSize(257, 144);
		setVisible(true);
		drawBackground();
		drawOurPoke();
		drawEnemyPoke();
		positionCanvas();
	}
	
	/**
	 * Draws our Pokemon
	 */
	public void drawOurPoke(){
		//TODO: Animate!
		LoadingList.setDeferredLoading(true);
		playerPoke = new Label(GameClient.getInstance().getUi().getBattleManager().getCurPoke().getBackSprite());
		playerPoke.setSize(80, 80);
		LoadingList.setDeferredLoading(false);
		playerPoke.setLocation(10, 0);
		add(playerPoke);
	}
	
	/**
	 * Draws the enemy's Pokemon
	 */
	public void drawEnemyPoke(){
		//TODO: Animate!
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
