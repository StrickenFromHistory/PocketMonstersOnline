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
	}
	
	/**
	 * Informs a pokemon fainted
	 * @param poke
	 */
	public void informFaintedPoke(String poke){
		m_narrator.addSpeech(poke + " fainted.");
	}
	
	/**
	 * Informs a move was used
	 * @param data
	 */
	public void informMoveUsed(String[] data){
		m_narrator.addSpeech(data[0] + " used " + data[1]);
	}
	
	/**
	 * Informs that a move is requested
	 */
	public void informMoveRequested(){
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
		m_narrator.addSpeech(data[0] + " sent out " + data[1]);
	}
	
	/**
	 * Informs a change in health
	 * @param data
	 * @param i
	 */
	public void informHealthChanged(String[] data, int i){
		if (Integer.parseInt(data[1]) >= 0)
			m_narrator.addSpeech(data[0] + " took " + data[1] + " damage.");
		else
			m_narrator.addSpeech(data[0] + " recovered " + data[1] + " HP.");
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
	}
	
	/**
	 * Shows a custom message sent by the server
	 * @param msg
	 */
	public void showMessage(String msg){
		m_narrator.addSpeech(msg);
	}
}
