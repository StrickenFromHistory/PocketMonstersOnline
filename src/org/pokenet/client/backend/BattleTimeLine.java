package org.pokenet.client.backend;

import org.pokenet.client.GameClient;
import org.pokenet.client.ui.BattleCanvas;
import org.pokenet.client.ui.frames.BattleSpeechFrame;

/**
 * Handles Battle Events and arranges them for visual purposes.
 * @author ZombieBear
 *
 */
public class BattleTimeLine {
	private BattleSpeechFrame m_narrator;
	private BattleCanvas m_canvas;
	
	/**
	 * Default constructor
	 */
	public BattleTimeLine(){
		init();
	}
	
	/**
	 * Starts the TimeLine's components
	 */
	public void init(){
		m_canvas = new BattleCanvas();
		m_narrator = new BattleSpeechFrame();
		GameClient.getInstance().getDisplay().add(m_canvas);
		GameClient.getInstance().getDisplay().add(m_narrator);
	}
	
	/**
	 * Informs a pokemon fainted
	 * @param poke
	 */
	public void informFaintedPoke(String poke){
		addSpeech(poke + " fainted.");
		for (int i = 0; i < GameClient.getInstance().getOurPlayer().getPokemon().length; i++){
			int counter = 0;
			if (GameClient.getInstance().getOurPlayer().getPokemon()[i].getCurHP() <= 0){
				counter++;
			}
			if (counter < i){
				GameClient.getInstance().getUi().getBattleManager().getBattleWindow().showPokePane(true);
				addSpeech("Please select a new Pokemon");
				break;
			}
		}
	}
	
	/**
	 * Informs a move was used
	 * @param data
	 */
	public void informMoveUsed(String[] data){
		addSpeech(data[0] + " used " + data[1]);
	}
	
	/**
	 * Informs that a move is requested
	 */
	public void informMoveRequested(){
		GameClient.getInstance().getUi().getBattleManager().requestMoves();
		m_narrator.addSpeech("Waiting on your move.");
	}
	
	/**
	 * Informs that a pokemon gained experience
	 * @param data
	 */
	public void informExperienceGained(String[] data){
		m_narrator.addSpeech(data[0] + " gained " + data[1] + "EXP.");
	}
	
	/**
	 * Informs that a pokemon's status was changed
	 * @param data
	 */
	public void informStatusChanged(String[] data){
		//TODO: Code this one	
	}

	/**
	 * Informs that a pokemon's status was returned to normal
	 * @param poke
	 */
	public void informStatusHealed(String poke){
		m_narrator.addSpeech(poke + " returned to normal.");
	}
	
	/**
	 * Informs that a pokemon was switched out.
	 * @param data
	 */
	public void informSwitch(String[] data){
		m_canvas.drawOurPoke();
		m_canvas.drawOurInfo();
		m_canvas.drawEnemyPoke();
		m_canvas.drawEnemyInfo();
		m_narrator.addSpeech(data[0] + " sent out " + data[1]);
	}
	
	/**
	 * Informs a change in health
	 * @param data
	 * @param i
	 */
	public void informHealthChanged(String[] data, int i){
		if (i == 0)
			m_canvas.updatePlayerHP(Integer.parseInt(data[1]));
		else
			m_canvas.updateEnemyHP(Integer.parseInt(data[1]));
		
		if (Integer.parseInt(data[1]) >= 0){
			m_narrator.addSpeech(data[0] + " took " + data[1] + " damage.");
		} else {
			
			m_narrator.addSpeech(data[0] + " recovered " + data[1] + " HP.");
		}
	}
	
	/**
	 * Informs a victory on the player's side
	 */
	public void informVictory(){
		m_narrator.addSpeech(GameClient.getInstance().getOurPlayer().getUsername() + " won the battle!");
	}
	
	/**
	 * Informs a loss on the player's side
	 */
	public void informLoss(){
		m_narrator.addSpeech(GameClient.getInstance().getOurPlayer().getUsername() + " was defeated!");
		GameClient.getInstance().getUi().getBattleManager().endBattle();
	}
	
	/**
	 * Shows a custom message sent by the server
	 * @param msg
	 */
	public void showMessage(String msg){
		addSpeech(msg);
	}
	
	/**
	 * Informs if a run was successful
	 * @param canRun
	 */
	public void informRun(boolean canRun){
		if (canRun){
			addSpeech("You got away succesfully.");
			GameClient.getInstance().getUi().getBattleManager().endBattle();
		} else {
			addSpeech("You failed to run away.");
			informMoveRequested();
		}
	}
	
	/**
	 * Adds speech to the narrator and waits for it to be read before the next action is taken
	 * @param msg
	 */
	public void addSpeech(String msg){
		m_narrator.addSpeech(msg);
		while (!m_narrator.getCurrentLine().equalsIgnoreCase(msg));
		while (!m_narrator.getAdvancedLine().equalsIgnoreCase(msg));
	}
	
	/**
	 * Returns the battle speech
	 * @return
	 */
	public BattleSpeechFrame getBattleSpeech(){
		return m_narrator;
	}
	
	/**
	 * Stops the timeline
	 */
	public void stop(){
		GameClient.getInstance().getDisplay().remove(m_canvas);
		GameClient.getInstance().getDisplay().remove(m_narrator);
		m_canvas = null;
		m_narrator = null;
	}
}
