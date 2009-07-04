package org.pokenet.server.battle.impl;

import java.util.Random;

import org.pokenet.server.GameServer;
import org.pokenet.server.backend.entity.NonPlayerChar;
import org.pokenet.server.backend.map.ServerMap;

/**
 * A thread which wakes sleeping NPCs (NPCs sleep for ~15 minutes after battle)
 * @author shadowkanji
 *
 */
public class NpcSleepTimer implements Runnable {
	private boolean m_running;

	public void run() {
		System.out.println("INFO: Npc sleep timer started");
		Random r = new Random();
		NonPlayerChar n = null;
		ServerMap m = null;
		while(m_running) {
			/*
			 * Loop through every map
			 */
			for(int x = 0; x < 100; x++) {
				for(int y = 0; y < 100; y++) {
					m = GameServer.getServiceManager().
						getMovementService().getMapMatrix().getMapByRealPosition(x, y);
					if(m != null) {
						/*
						 * Loop through every npc on the map
						 * If they're sleeping, check if its time to wake them
						 */
						for(int i = 0; i < m.getNpcs().size(); i++) {
							n = m.getNpcs().get(i);
							if(n != null && !n.canBattle() && 
									System.currentTimeMillis() - n.getLastBattleTime()
									>= 300000 + r.nextInt(300000)) {
								n.setLastBattleTime(0);
							}
						}
						try {
							Thread.sleep(500);
						} catch (Exception e) {}
					}
					n = null;
				}
			}
			try {
				Thread.sleep(300000);
			} catch (Exception e) {}
		}
		System.out.println("INFO: Npc sleep timer stopped");
	}

	/**
	 * Starts the timer
	 */
	public void start() {
		m_running = true;
		new Thread(this).start();
	}
	
	/**
	 * Stops the timer
	 */
	public void stop() {
		m_running = false;
	}
}
