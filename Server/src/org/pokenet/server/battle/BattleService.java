package org.pokenet.server.battle;

import org.pokenet.server.backend.entity.NonPlayerChar;
import org.pokenet.server.backend.entity.PlayerChar;

/**
 * Handles battles
 * @author shadowkanji
 */
public class BattleService implements Runnable {	
	//Store battlefields in some way
	private Thread m_thread;
	
	/**
	 * Default constructor
	 */
	public BattleService() {
		m_thread = new Thread(this);
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
