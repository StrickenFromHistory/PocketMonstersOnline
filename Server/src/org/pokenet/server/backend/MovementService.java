package org.pokenet.server.backend;

import org.pokenet.server.GameServer;

/**
 * Stores the map matrix and movement managers.
 * @author shadowkanji
 *
 */
public class MovementService {
	private MovementManager [] m_movementManager;
	private ServerMapMatrix m_mapMatrix;
	private ServerMap m_tempMap;
	
	/**
	 * Default constructor
	 */
	public MovementService() {
		m_movementManager = new MovementManager[GameServer.getMovementThreadAmount()];
		m_mapMatrix = new ServerMapMatrix();
	}
	
	/**
	 * Returns the movement manager with the smallest processing load
	 * @return
	 */
	public MovementManager getMovementManager() {
		int smallest = 0;
		if(m_movementManager.length > 1) {
			for(int i = 0; i < m_movementManager.length; i++) {
				if(m_movementManager[i].getProcessingLoad() < m_movementManager[smallest].getProcessingLoad())
					smallest = i;
			}
		}
		return m_movementManager[smallest];
	}
	
	/**
	 * Reloads all maps while the server is running. Puts all players in m_tempMap.
	 * An NPC is there to allow them to return to where they last where when they are ready.
	 */
	public void reloadMap() {
		//TODO: Call map loading script and move players to m_tempMap
	}
	
	/**
	 * Starts the movement service
	 */
	public void start() {
		for(int i = 0; i < m_movementManager.length; i++)
			m_movementManager[i].start();
		//TODO: Load all maps here
	}
	
	/**
	 * Stops the movement service
	 */
	public void stop() {
		
	}
}