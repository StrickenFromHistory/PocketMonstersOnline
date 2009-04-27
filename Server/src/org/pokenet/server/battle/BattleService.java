package org.pokenet.server.battle;

import java.util.ArrayList;

import org.pokenet.server.GameServer;
import org.pokenet.server.backend.entity.NonPlayerChar;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.battle.impl.WildBattleField;

/**
 * Handles battles
 * @author shadowkanji
 */
public class BattleService implements Runnable {	
	private ArrayList<BattleField> m_battleFields;
	private Thread m_thread;
	private boolean m_isRunning;
	
	/**
	 * Default constructor
	 */
	public BattleService() {
		m_battleFields = new ArrayList<BattleField>();
		m_thread = new Thread(this);
	}
	
	/**
	 * Returns the index of the battlefield that the player is on.
	 * Returns -1 if they are not on any battlefield
	 * @param p
	 * @return
	 */
	public int containsPlayer(PlayerChar p) {
		for(int i = 0; i < m_battleFields.size(); i++) {
			if(m_battleFields.get(i).isParticipating(p))
				return i;
		}
		return -1;
	}
	
	/**
	 * Returns a battlefield based on the index of it in the arraylist of battlefields
	 * @param index
	 * @return
	 */
	public BattleField getBattleField(int index) {
		return m_battleFields.get(index);
	}
	
	/**
	 * Returns the processing load of this thread (how many battlefields exist in it)
	 * @return
	 */
	public int getProcessingLoad() {
		return m_battleFields.size();
	}
	
	/**
	 * Starts an npc battle between player and npc
	 * @param player
	 * @param npc
	 */
	public void startNpcBattle(PlayerChar player, NonPlayerChar npc) {
		
	}
	
	/**
	 * Starts a pvp battle between player1 and player2
	 * @param player1
	 * @param player2
	 */
	public void startPvpBattle(PlayerChar player1, PlayerChar player2) {
		
	}
	
	/**
	 * Starts a wild battle between player and pokemon
	 * @param player
	 * @param pokemon
	 */
	public void startWildBattle(PlayerChar player, Pokemon pokemon) {
		player.setBattling(true);
		m_battleFields.add(new WildBattleField(
				DataService.getBattleMechanics(),
				player, pokemon));
	}
	
	/**
	 * Called by m_thread.start(). Loops through all battles and calls BattleField.executeTurn()
	 * if both participants have selected their moves.
	 */
	public void run() {
		while(m_isRunning) {
			synchronized(m_battleFields) {
				for(int i = 0; i < m_battleFields.size(); i++) {
					/*
					 * If the battle is threaded, ignore it, let the thread finish
					 */
					if(!m_battleFields.get(i).isThreaded()) {
						/*
						 * If the both players have selected their moves, execute the turn
						 */
						if(m_battleFields.get(i).isExecuteForced() || m_battleFields.get(i).isReady()) {
							m_battleFields.get(i).executeTurn();
						}
						/*
						 * If the battle if over, remove it from the list
						 */
						if(m_battleFields.get(i).isFinished()) {
							m_battleFields.get(i).dispose();
							m_battleFields.remove(i);
							m_battleFields.trimToSize();
						}
					}
				}
			}
			try {
				Thread.sleep(350);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Starts this battle service
	 */
	public void start() {
		m_isRunning = true;
		m_thread.start();
	}
	
	/**
	 * Stops this battle service
	 */
	public void stop() {
		m_isRunning = false;
	}

}
