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
		m_movementService = new MovementService[GameServer.getMovementThreadAmount()];
		m_battleService = new BattleService[GameServer.getBattleThreadAmount()];
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
		int smallest = 0;
		for(int i = 0; i < m_movementService.length; i++) {
			synchronized(m_movementService[i]) {
				if(m_movementService[i].getPlayerAmount() < m_movementService[smallest].getPlayerAmount())
					smallest = i;
			}
		}
		return m_movementService[smallest];
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
		/*
		 * Start the network service first as it needs to bind the address/port to the game server.
		 * Then start all other services with TimeService last.
		 */
		m_networkService.start();
		for(int i = 0; i < m_movementService.length; i++)
			m_movementService[i].start();
		for(int i = 0; i < m_battleService.length; i++)
			m_battleService[i].start();
		System.out.println("INFO: Service Manager startup completed.");
	}
	
	/**
	 * Stops all services
	 */
	public void stop() {
		/*
		 * Stopping services is very delicate and must be done in the following order to avoid
		 * leaving player objects in a non-concurrent state.
		 */
		for(int i = 0; i < m_movementService.length; i++)
			m_movementService[i].stop();
		for(int i = 0; i < m_battleService.length; i++)
			m_battleService[i].stop();
		m_networkService.stop();
		System.out.println("INFO: Service Manager stopped.");
	}
}
