package org.pokenet.client.ui;

import mdes.slick.sui.Container;
import mdes.slick.sui.Label;

import org.newdawn.slick.Image;
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
		setBounds(0, 1, 257, 105);
		setSize(257, 144);
		setVisible(true);	
	}
	
	/**
	 * Draws our Pokemon
	 */
	public void drawOurPoke(){
		
	}
	
	/**
	 * Draws the enemie's Pokemon
	 */
	public void drawEnemyPoke(){
		
	}
}
