package org.pokenet.server.battle;

import java.util.ArrayList;

import org.pokenet.server.backend.entity.NonPlayerChar;
import org.pokenet.server.backend.entity.PlayerChar;

/**
 * Handles battles
 * @author shadowkanji
 */
public class BattleService implements Runnable {	
	private ArrayList<BattleField> m_battleFields;
	private Thread m_thread;
	
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
		
	}
	
	/**
	 * Called by m_thread.start(). Loops through all battles and calls BattleField.executeTurns()
	 * if both participants have selected their moves.
	 */
	public void run() {
		
	}
	
	/**
	 * Starts this battle service
	 */
	public void start() {
		m_thread.start();
		System.out.println("INFO: Battle Service started");
	}
	
	/**
	 * Stops this battle service
	 */
	public void stop() {
		
	}

}
