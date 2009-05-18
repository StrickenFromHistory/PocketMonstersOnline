package org.pokenet.client.backend;

import java.util.ArrayList;
import java.util.List;

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
	List<String> m_translator = new ArrayList<String>();
	//Lines for REGEX needed for l10n
	String m_pokeName, m_move, m_trainer;
	int m_newHPValue, m_exp, m_dmg;
	
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
		m_translator = Translator.translate("_BATTLE");
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
		m_pokeName = poke;
		addSpeech(poke + " fainted.");
		for (int i = 0; i < GameClient.getInstance().getOurPlayer().getPokemon().length; i++){
			int counter = 0;
			if (GameClient.getInstance().getOurPlayer().getPokemon()[i].getCurHP() <= 0){
				counter++;
			}
			if (counter < i){
				GameClient.getInstance().getUi().getBattleManager().getBattleWindow().showPokePane(true);
				addSpeech(m_translator.get(0));
				break;
			}
		}
	}
	
	/**
	 * Informs a move was used
	 * @param data
	 */
	public void informMoveUsed(String[] data){
		m_pokeName = data[0];
		m_move = data[1];
		addSpeech(m_translator.get(1));
	}
	
	/**
	 * Informs that a move is requested
	 */
	public void informMoveRequested(){
		GameClient.getInstance().getUi().getBattleManager().requestMoves();
		addSpeech(m_translator.get(2));
	}
	
	/**
	 * Informs that a pokemon gained experience
	 * @param data
	 */
	public void informExperienceGained(String[] data){
		m_exp = Integer.parseInt(data[1]);
		addSpeech(m_translator.get(3));
	}
	
	/**
	 * Informs that a pokemon's status was changed
	 * @param data
	 */
	public void informStatusChanged(String[] data){
		m_pokeName = data[0];
		//TODO: Code this one	
	}

	/**
	 * Informs that a pokemon's status was returned to normal
	 * @param poke
	 */
	public void informStatusHealed(String poke){
		m_pokeName = poke;
		addSpeech(m_translator.get(4));
	}
	
	/**
	 * Informs that a pokemon was switched out.
	 * @param data
	 */
	public void informSwitch(String[] data){
		m_trainer = data[0];
		m_pokeName = data[1];
		m_canvas.drawOurPoke();
		m_canvas.drawOurInfo();
		m_canvas.drawEnemyPoke();
		m_canvas.drawEnemyInfo();
		addSpeech(m_translator.get(5));
	}
	
	/**
	 * Informs that a pokemon switch is required
	 */
	public void informSwitchRequested(){
		GameClient.getInstance().getUi().getBattleManager().getBattleWindow().showPokePane(true);
		addSpeech(m_translator.get(6));
	}
	
	/**
	 * Informs a change in health
	 * @param data
	 * @param i
	 */
	public void informHealthChanged(String[] data,
			int i){
		m_pokeName = data[0];
		m_dmg = Math.abs(Integer.parseInt(data[1]));
		if (i == 0){
			m_pokeName = GameClient.getInstance().getUi().getBattleManager().getCurPoke().getName();
			m_newHPValue = GameClient.getInstance().getUi().getBattleManager().getCurPoke().getCurHP() + 
				Integer.parseInt(data[1]);
			if (m_newHPValue < 0){m_newHPValue = 0;}
			GameClient.getInstance().getUi().getBattleManager().getCurPoke().setCurHP(m_newHPValue);
			m_canvas.updatePlayerHP(GameClient.getInstance().getUi().getBattleManager().getCurPoke()
					.getCurHP());
			data[0] = GameClient.getInstance().getUi().getBattleManager().getCurPoke().getName();
		} else {
			m_pokeName = GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getName();
			m_newHPValue = GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getCurHP() + 
				Integer.parseInt(data[1]);
			if (m_newHPValue < 0){m_newHPValue = 0;}
			GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().setCurHP(m_newHPValue);
			m_canvas.updateEnemyHP(GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke()
					.getCurHP());
			data[0] = GameClient.getInstance().getUi().getBattleManager().getCurEnemyPoke().getName();
		}
		
		if (Integer.parseInt(data[1]) <= 0){
			addSpeech(m_translator.get(7));
			addSpeech(m_translator.get(8));
		} else {
			addSpeech(m_translator.get(9));
		}
	}
	
	/**
	 * Informs a victory on the player's side
	 */
	public void informVictory(){
		m_trainer = GameClient.getInstance().getOurPlayer().getUsername();
		addSpeech(m_translator.get(10));
		GameClient.getInstance().getUi().getBattleManager().endBattle();
	}
	
	/**
	 * Informs a loss on the player's side
	 */
	public void informLoss(){
		m_trainer = GameClient.getInstance().getOurPlayer().getUsername();
		addSpeech(m_translator.get(11));
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
			addSpeech(m_translator.get(12));
			m_narrator.advance();
			GameClient.getInstance().getUi().getBattleManager().endBattle();
		} else {
			addSpeech(m_translator.get(13));
			m_narrator.advance();
			informMoveRequested();
		}
	}
	
	/**
	 * Adds speech to the narrator and waits for it to be read before the next action is taken
	 * @param msg
	 */
	public void addSpeech(String msg){
		String newMsg = parsel10n(msg);
		m_narrator.addSpeech(parsel10n(newMsg));
		while (!m_narrator.getCurrentLine().equalsIgnoreCase(newMsg));
		while (!m_narrator.getAdvancedLine().equalsIgnoreCase(newMsg));
	}
	
	/**
	 * Returns the battle speech
	 * @return
	 */
	public BattleSpeechFrame getBattleSpeech(){
		return m_narrator;
	}

	/**
	 * Returns the battle canvas
	 * @return
	 */
	public BattleCanvas getBattleCanvas(){
		return m_canvas;
	}
	
	/**
	 * Stops the timeline
	 */
	public void stop(){
		GameClient.getInstance().getDisplay().remove(m_canvas);
		while (GameClient.getInstance().getDisplay().containsChild(m_canvas));
		GameClient.getInstance().getDisplay().remove(m_narrator);
		while (GameClient.getInstance().getDisplay().containsChild(m_narrator));
		m_canvas = null;
		m_narrator = null;
	}
	
	/**
	 * Uses regexes to create the appropriate battle messages for battle
	 * @param line
	 */
	public String parsel10n(String line){
		if (line.contains("trainerName")){
			line = line.replaceAll("trainerName", m_trainer);
		}
		if (line.contains("moveName")){
			line = line.replaceAll("moveName", m_move);
		}
		if (line.contains("pokeName")){
			line = line.replace("pokeName", m_pokeName);
		}
		if (line.contains("hpNum")){
			line = line.replaceAll("hpNum", String.valueOf(m_newHPValue));
		}
		if (line.contains("expNum")){
			line = line.replaceAll("expNum", String.valueOf(m_exp));
		}
		if (line.contains("damageNum")){
			line = line.replaceAll("damageNum", String.valueOf(m_dmg));
		}
		return line;
	}
}
