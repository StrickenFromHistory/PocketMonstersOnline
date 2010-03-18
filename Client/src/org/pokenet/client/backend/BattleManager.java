package org.pokenet.client.backend;

import java.util.HashMap;
import java.util.Map;

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
	private int m_curEnemyIndex;
	private String m_enemy;
	private boolean m_isWild;
	private Map<Integer, String> m_ourStatuses = new HashMap<Integer, String>();
	private static BattleManager m_instance;
	private static boolean m_isBattling = false;
	private String m_curTrack;
	
	/**
	 * Default Constructor
	 */
	public BattleManager() {
		m_instance = this;
		m_battle = new BattleWindow("Battle!");
		m_timeLine = new BattleTimeLine();
		m_battle.setVisible(false);
		m_battle.setAlwaysOnTop(true);
	}

	/**
	 * Returns the instance
	 * @return
	 */
	public static BattleManager getInstance() {
		return m_instance;
	}
	
	/**
	 * Retrieves player data
	 */
	private void getPlayerData() {
		m_player = GameClient.getInstance().getOurPlayer();
		m_ourPokes = m_player.getPokemon();
		for (int i = 0; i < 6; i++){
			if(m_ourPokes[i].getCurHP() > 0){
				m_curPokeIndex = i;
				m_curPoke = m_ourPokes[i];
				break;
			}
		}
	}

	/**
	 * Sets the enemy's data 
	 */
	private void setEnemyData() {
		m_curEnemyPoke = m_enemyPokes[0];
		m_curEnemyIndex = 0;
		try{
			m_timeLine.getBattleCanvas().drawEnemyPoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			m_timeLine.getBattleCanvas().drawEnemyInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			if(!m_isWild){
				m_timeLine.getBattleCanvas().showPokeballs();
				m_timeLine.addSpeech(m_enemy + " sent out " + m_curEnemyPoke.getName());
			} else{
				m_timeLine.getBattleCanvas().hidePokeballs();
				m_timeLine.addSpeech("A wild " + m_curEnemyPoke.getName() + " attacked!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the enemy's name
	 * @param name
	 */
	public void setEnemyName(String name) {
		m_enemy = name;
	}

	/**
	 * Starts a new BattleWindow and BattleCanvas
	 * @param isWild
	 * @param pokeAmount
	 */
	public void startBattle(char isWild,
			int pokeAmount) {
		m_isBattling = true;

		GameClient.getInstance().getUi().hideHUD(true);
		if (isWild == '0'){
			setWild(false);
		} else {
			setWild(true);
		}
		m_battle.showAttack();
		m_battle.setVisible(true);
		m_enemyPokes = new Pokemon[pokeAmount];
		getPlayerData();
		m_battle.disableMoves();
		updateMoves();
		updatePokePane();
		m_timeLine.startBattle();
        m_curTrack = GameClient.getSoundPlayer().m_trackName;
        System.out.println("Before Battle Music Name:" + m_curTrack);
		GameClient.getInstance().getDisplay().add(m_battle);
		GameClient.changeTrack("pvnpc");
	}
	
	/**
	 * Ends the battle
	 */
	public void endBattle() {
		GameClient.getInstance().getUi().hideHUD(false);
		m_timeLine.endBattle();
		m_battle.setVisible(false);
		m_isBattling = false;
		if (GameClient.getInstance().getDisplay().containsChild(m_battle.m_bag))
			GameClient.getInstance().getDisplay().remove(m_battle.m_bag);
		GameClient.getInstance().getDisplay().remove(m_battle);
		while (GameClient.getInstance().getDisplay().containsChild(m_battle));
		GameClient.getSoundPlayer().setTrackByLocation(GameClient.getInstance().getMapMatrix().getCurrentMap().getName());
		if (GameClient.getSoundPlayer().m_trackName == "pvnpc") {
			GameClient.getSoundPlayer().setTrack(m_curTrack);
		}
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
		for (int i = 0; i < 4; i++){
			if (m_ourPokes[pokeIndex].getMoves()[i] != null) {
				m_battle.m_moveButtons.get(i).setText(m_ourPokes[pokeIndex].getMoves()[i]);
				m_battle.m_ppLabels.get(i).setText(m_ourPokes[pokeIndex].getMoveCurPP()[i] + "/"
						+ m_ourPokes[pokeIndex].getMoveMaxPP()[i]);
			} else {
				m_battle.m_moveButtons.get(i).setText("");
				m_battle.m_ppLabels.get(i).setText("");
			}
		}
	}
	
	/**
	 * Updates moves with the current poke
	 */
	public void updateMoves() {
		for (int i = 0; i < 4; i++){
			if (m_curPoke != null && m_curPoke.getMoves()[i] != null) {
				m_battle.m_moveButtons.get(i).setText(m_curPoke.getMoves()[i]);
				m_battle.m_ppLabels.get(i).setText(m_curPoke.getMoveCurPP()[i] + "/"
						+ m_curPoke.getMoveMaxPP()[i]);
			} else {
				m_battle.m_moveButtons.get(i).setText("");
				m_battle.m_ppLabels.get(i).setText("");
			}
		}
	}

	
	/**
	 * Switch a pokemon
	 * @param trainer
	 * @param pokeIndex
	 */
	public void switchPoke(int trainer, int pokeIndex){
		if (trainer == 0) {
			m_curPoke = GameClient.getInstance().getOurPlayer().getPokemon()[pokeIndex];
			m_curPokeIndex = pokeIndex;
			updateMoves();
			updatePokePane();
			m_timeLine.getBattleCanvas().drawOurPoke();
			m_timeLine.getBattleCanvas().drawOurInfo();
		} else {
			m_curEnemyPoke = m_enemyPokes[pokeIndex];
			m_curEnemyIndex = pokeIndex;
		}
	}
	
	/**
	 * Updates the pokemon pane
	 */
	public void updatePokePane() {
		for (int i = 0; i < 6; i++) {
			try{
				m_battle.m_pokeButtons.get(i).setText(m_ourPokes[i].getName());
				m_battle.m_pokeInfo.get(i).setText("Lv: " + m_ourPokes[i].getLevel() + " HP:"
						+ m_ourPokes[i].getCurHP() + "/" + m_ourPokes[i].getMaxHP());
				try{
					if (m_ourStatuses.containsKey(i) && m_battle.m_statusIcons.containsKey(m_ourStatuses.get(i))){
						m_battle.m_pokeStatus.get(i).setImage(m_battle.m_statusIcons.get(m_ourStatuses.get(i)));
					} else {
						m_battle.m_pokeStatus.get(i).setImage(null);
					}
				} catch (Exception e2){}
				if (m_ourPokes[i].getCurHP() <= 0 || m_curPokeIndex == i)
					m_battle.m_pokeButtons.get(i).setEnabled(false);
				else
					m_battle.m_pokeButtons.get(i).setEnabled(true);
			} catch (Exception e) {}
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
	 * Returns the active enemy pokemon's index in party
	 * @return
	 */
	public int getCurEnemyIndex(){
		return m_curEnemyIndex;
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

		if (curHP != 0)
			m_timeLine.getBattleCanvas().setPokeballImage(index, "normal");
		else
			m_timeLine.getBattleCanvas().setPokeballImage(index, "fainted");
		
		m_enemyPokes[index] = new Pokemon();
		m_enemyPokes[index].setName(name);
		m_enemyPokes[index].setLevel(level);
		m_enemyPokes[index].setGender(gender);
		m_enemyPokes[index].setMaxHP(maxHP);
		m_enemyPokes[index].setCurHP(curHP);
		m_enemyPokes[index].setSpriteNumber(spriteNum + 1);
		m_enemyPokes[index].setShiny(isShiny);
		
		if ((index + 1) == m_enemyPokes.length)
			setEnemyData();
	}

	/**
	 * Sets wild battle
	 * @param m_isWild
	 */
	public void setWild(boolean m_isWild) {
		this.m_isWild = m_isWild;
		m_battle.setWild(m_isWild);
	}
	
	/**
	 * Returns a boolean determining whether the pokemon is wild
	 * @return m_isWild
	 */
	public boolean isWild() {
		return m_isWild;
	}
	
	/**
	 * Returns a list of our pokes who are affected by statuses
	 * @return a list of our pokes who are affected by statuses
	 */
	public Map<Integer, String> getOurStatuses(){
		return m_ourStatuses;
	}
	
	/**
	 * Returns true if a battle is in progress
	 * @return true if a battle is in progress
	 */
	public static boolean isBattling(){
		return m_isBattling;
	}
}
