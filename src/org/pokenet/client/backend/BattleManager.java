package org.pokenet.client.backend;

import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.OurPlayer;
import org.pokenet.client.backend.entity.OurPokemon;
import org.pokenet.client.backend.entity.Pokemon;
import org.pokenet.client.ui.BattleWindow;

/**
 * Handles battle events and controls the battle window
 * 
 * @author ZombieBear
 * 
 */
public class BattleManager {
	private OurPlayer m_player;
	private BattleWindow m_battle;
	private OurPokemon[] m_ourPokes;
	private Pokemon[] m_enemyPokes;
	private BattleTimeLine m_timeLine;
	private OurPokemon m_curPoke;
	private int m_curPokeIndex;
	private Pokemon m_curEnemyPoke;
	private String m_enemy;
	private boolean m_isWild;
	
	/**
	 * Default Constructor
	 */
	public BattleManager() {
	}

	/**
	 * Retrieves player data
	 */
	private void getPlayerData() {
		m_player = GameClient.getInstance().getOurPlayer();
		m_ourPokes = m_player.getPokemon();
		m_curPoke = m_ourPokes[0];
		m_curPokeIndex = 0;
	}

	/**
	 * Sets the enemy's data 
	 */
	private void setEnemyData() {
		m_curEnemyPoke = m_enemyPokes[0];
		m_timeLine.getBattleCanvas().drawEnemyPoke();
		m_timeLine.getBattleCanvas().drawEnemyInfo();
		if(!m_isWild){
			// TODO: Draw pokeballs equal to the enemy's pokemon count
			m_timeLine.addSpeech(m_enemy + " sent out " + m_curEnemyPoke.getName());
		} else{
			m_timeLine.addSpeech("A wild " + m_curEnemyPoke.getName() + " attacked!");
		}
	}

	/**
	 * Starts a new BattleWindow and BattleCanvas
	 * @param isWild
	 * @param pokeAmount
	 */
	public void startBattle(char isWild,
			int pokeAmount) {
		if (isWild == '0'){
			setWild(false);
		} else {
			setWild(true);
		}
		getPlayerData();
		m_battle = new BattleWindow("Battle!", m_isWild);
		m_battle.disableMoves();
		updateMoves(0);
		updatePokePane();
		GameClient.getInstance().getDisplay().add(m_battle);
		m_timeLine = new BattleTimeLine();
		m_enemyPokes = new Pokemon[pokeAmount];
		GameClient.changeTrack("pvnpc");
	}
	
	/**
	 * Ends the battle
	 */
	public void endBattle() {
		m_timeLine.stop();
		m_timeLine = null;
		GameClient.getInstance().getDisplay().remove(m_battle);
		m_battle = null;
		GameClient.changeTrack("newbark");
	}

	/**
	 * Returns the TimeLine
	 * @return m_timeLine
	 */
	public BattleTimeLine getTimeLine(){
		return m_timeLine;
	}
	
	/**
	 * Retrieves a pokemon's moves and updates the BattleWindow
	 * @param int pokeIndex
	 */
	public void updateMoves(int pokeIndex) {
		for (int i = 0; i < 3; i++){
			if (m_ourPokes[pokeIndex].getMoves()[i] != null) {
				m_battle.m_moveButtons.get(i).setText(m_ourPokes[pokeIndex].getMoves()[i]);
				m_battle.m_ppLabels.get(i).setText(m_ourPokes[pokeIndex].getMovecurPP()[i] + "/"
						+ m_ourPokes[pokeIndex].getMovemaxPP()[i]);
			} else {
				m_battle.m_moveButtons.get(i).setText("");
				m_battle.m_ppLabels.get(i).setText("");
			}
		}
	}

	/**
	 * Updates the pokemon pane
	 */
	public void updatePokePane() {
		m_battle.pokeBtn1.setText(m_ourPokes[0].getName());
		m_battle.info1.setText("Lv: " + m_ourPokes[0].getLevel() + " HP:"
				+ m_ourPokes[0].getCurHP() + "/" + m_ourPokes[0].getMaxHP());
		if (m_ourPokes[0].getCurHP() <= 0)
			m_battle.pokeBtn1.setEnabled(false);
		else
			m_battle.pokeBtn1.setEnabled(true);

		try {
			m_battle.pokeBtn2.setText(m_ourPokes[1].getName());
			m_battle.info2
					.setText("Lv: " + m_ourPokes[1].getLevel() + " HP:"
							+ m_ourPokes[1].getCurHP() + "/"
							+ m_ourPokes[1].getMaxHP());
			if (m_ourPokes[1].getCurHP() <= 0)
				m_battle.pokeBtn2.setEnabled(false);
			else
				m_battle.pokeBtn2.setEnabled(true);
		} catch (NullPointerException e) {
			m_battle.pokeBtn2.setEnabled(false);
		}

		try {
			m_battle.pokeBtn3.setText(m_ourPokes[2].getName());
			m_battle.info3
					.setText("Lv: " + m_ourPokes[2].getLevel() + " HP:"
							+ m_ourPokes[2].getCurHP() + "/"
							+ m_ourPokes[2].getMaxHP());
			if (m_ourPokes[2].getCurHP() <= 0)
				m_battle.pokeBtn3.setEnabled(false);
			else
				m_battle.pokeBtn3.setEnabled(true);
		} catch (NullPointerException e) {
			m_battle.pokeBtn3.setEnabled(false);
		}

		try {
			m_battle.pokeBtn4.setText(m_ourPokes[3].getName());
			m_battle.info4
					.setText("Lv: " + m_ourPokes[3].getLevel() + " HP:"
							+ m_ourPokes[3].getCurHP() + "/"
							+ m_ourPokes[3].getMaxHP());
			if (m_ourPokes[3].getCurHP() <= 0)
				m_battle.pokeBtn4.setEnabled(false);
			else
				m_battle.pokeBtn4.setEnabled(true);
		} catch (NullPointerException e) {
			m_battle.pokeBtn4.setEnabled(false);
		}
		try {
			m_battle.pokeBtn5.setText(m_ourPokes[4].getName());
			m_battle.info5
					.setText("Lv: " + m_ourPokes[4].getLevel() + " HP:"
							+ m_ourPokes[4].getCurHP() + "/"
							+ m_ourPokes[4].getMaxHP());
			if (m_ourPokes[4].getCurHP() <= 0)
				m_battle.pokeBtn5.setEnabled(false);
			else
				m_battle.pokeBtn5.setEnabled(true);
		} catch (NullPointerException e) {
			m_battle.pokeBtn5.setEnabled(false);
		}
		try {
			m_battle.pokeBtn6.setText(m_ourPokes[5].getName());
			m_battle.info6
					.setText("Lv: " + m_ourPokes[5].getLevel() + " HP:"
							+ m_ourPokes[5].getCurHP() + "/"
							+ m_ourPokes[5].getMaxHP());
			if (m_ourPokes[5].getCurHP() <= 0)
				m_battle.pokeBtn6.setEnabled(false);
			else
				m_battle.pokeBtn6.setEnabled(true);
		} catch (NullPointerException e) {
			m_battle.pokeBtn6.setEnabled(false);
		}

	}

	/**
	 * Switches a pokemon on the field
	 * 
	 * @param trainerIndex
	 * @param pokeIndex
	 */
	public void switchPoke(int trainerIndex,
			int pokeIndex,
			boolean isForced) {
		if (trainerIndex == 0) {
			// TODO: Draw our poke
			updateMoves(pokeIndex);
			m_battle.showPokePane(isForced);
		} else {
			// TODO: Draw enemy poke
		}
	}

	/**
	 * Requests a move from the player
	 */
	public void requestMoves() {
		m_battle.enableMoves();
		m_battle.showAttack();
	}
	
	/**
	 * Gets the BattleWindow
	 * @return
	 */
	public BattleWindow getBattleWindow(){
		return m_battle;
	}
	
	/**
	 * Returns the player's active pokemon
	 */
	public OurPokemon getCurPoke(){
		return m_curPoke;
	}
	
	/**
	 * Returns the active pokemon's index in party
	 * @return
	 */
	public int getCurPokeIndex(){
		return m_curPokeIndex;
	}
	
	/**
	 * Returns the enemy's active pokemon or the wild pokemon
	 */
	public Pokemon getCurEnemyPoke(){
		return m_curEnemyPoke;
	}
	
	/**
	 * Adds an enemy poke
	 * @param index
	 * @param name
	 * @param level
	 * @param gender
	 * @param maxHP
	 * @param curHP
	 * @param spriteNum
	 * @param isShiny
	 */
	public void setEnemyPoke(int index,
			String name,
			int level,
			int gender,
			int maxHP,
			int curHP,
			int spriteNum,
			boolean isShiny){
		m_enemyPokes[index] = new Pokemon();
		m_enemyPokes[index].setName(name);
		m_enemyPokes[index].setLevel(level);
		m_enemyPokes[index].setGender(gender);
		m_enemyPokes[index].setMaxHP(maxHP);
		m_enemyPokes[index].setCurHP(curHP);
		m_enemyPokes[index].setSpriteNumber(spriteNum);
		m_enemyPokes[index].setShiny(isShiny);
		m_enemyPokes[index].setSprite();

		if ((index + 1) == m_enemyPokes.length)
			setEnemyData();
	}

	/**
	 * Sets wild battle
	 * @param m_isWild
	 */
	public void setWild(boolean m_isWild) {
		this.m_isWild = m_isWild;
	}
	
	/**
	 * Returns a boolean determining whether the pokemon is wild
	 * @return m_isWild
	 */
	public boolean isWild() {
		return m_isWild;
	}
}
