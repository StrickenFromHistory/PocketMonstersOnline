package org.pokenet.server;

import org.pokenet.server.backend.MovementService;
import org.pokenet.server.battle.BattleService;
import org.pokenet.server.network.NetworkService;

/**
 * Handles all services on the game server
 * @author shadowkanji
 *
 */
public class ServiceManager {
	private NetworkService m_networkService;
	private MovementService [] m_movementService;
	private BattleService [] m_battleService;
	
	/**
	 * Default constructor
	 */
	public ServiceManager() {
		m_networkService = new NetworkService();
		m_movementService = new MovementService[3];
		m_battleService = new BattleService[2];
	}
	
	/**
	 * Returns the battle service with the smallest amount of battles in it
	 * @return
	 */
	public BattleService getBattleService() {
		return null;
	}
	
	/**
	 * Returns the movement service with the smallest amount of players stored in it
	 * @return
	 */
	public MovementService getMovementService() {
		return null;
	}
	
	/**
	 * Returns the network service
	 * @return
	 */
	public NetworkService getNetworkService() {
		return m_networkService;
	}
	
	/**
	 * Starts all services
	 */
	public void start() {
		//Start all threads here
		System.out.println("INFO: Service Manager startup completed.");
	}
	
	/**
	 * Stops all services
	 */
	public void stop() {
		
	}
}
