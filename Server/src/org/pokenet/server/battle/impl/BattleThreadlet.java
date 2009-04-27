package org.pokenet.server.battle.impl;

import org.pokenet.server.battle.BattleField;

/**
 * A battle thread which gives support for moves
 * where players are forced to switch Pokemon
 * @author shadowkanji
 *
 */
public class BattleThreadlet implements Runnable {
	private BattleField m_field;
	
	/**
	 * Constructor
	 * @param b
	 */
	public BattleThreadlet(BattleField b) {
		m_field = b;
	}

	/**
	 * Run the thread
	 */
	public void run() {
		/*
		 * First, wait for both players to select moves
		 */
		while(!m_field.isReady());
		/*
		 * Now that players are ready, 
		 * execute the moves
		 * (this will be stalled when requestAndWaitForSwitch is called)
		 */
		m_field.executeTurn();
		/*
		 * Tell the field it is not longer threaded
		 */
		m_field.setThreaded(false);
	}

}
