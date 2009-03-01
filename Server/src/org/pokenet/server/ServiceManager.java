package org.pokenet.server;

import org.pokenet.server.backend.MovementService;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.BattleService;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.network.NetworkService;

/**
 * Handles all services on the game server
 * @author shadowkanji
 *
 */
public class ServiceManager {
	private NetworkService m_networkService;
	private MovementService m_movementService;
	private BattleService [] m_battleService;
	private DataService m_dataService;
	
	/**
	 * Default constructor
	 */
	public ServiceManager() {
		m_dataService = new DataService();
		m_networkService = new NetworkService();
		m_movementService = new MovementService();
		m_battleService = new BattleService[GameServer.getBattleThreadAmount()];
	}
	
	/**
	 * Returns the battle service with the smallest amount of battles in it
	 * @return
	 */
	public BattleService getBattleService() {
		int smallest = 0;
		if(m_battleService.length > 1) {
			for(int i = 0; i < m_battleService.length; i++) {
				if(m_battleService[i].getProcessingLoad() < m_battleService[smallest].getProcessingLoad())
					smallest = i;
			}
		}
		return m_battleService[smallest];
	}
	
	/**
	 * Locates the battle service that a player is in and returns the battlefield they are on
	 * @param player
	 * @return
	 */
	public BattleField getBattleFieldForPlayer(PlayerChar player) {
		int location = -1;
		for(int i = 0; i < m_battleService.length; i++) {
			location = m_battleService[i].containsPlayer(player);
			if(location > -1)
				return m_battleService[i].getBattleField(location);
		}
		return null;
	}
	
	/**
	 * Returns the data service (contains battle mechanics, polrdb, etc.)
	 * @return
	 */
	public DataService getDataService() {
		return m_dataService;
	}
	
	/**
	 * Returns the movement service
	 * @return
	 */
	public MovementService getMovementService() {
		return m_movementService;
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
		m_movementService.start();
		for(int i = 0; i < m_battleService.length; i++) {
			m_battleService[i] = new BattleService();
			m_battleService[i].start();
		}
		System.out.println("INFO: Battle Service started");
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
		m_movementService.stop();
		for(int i = 0; i < m_battleService.length; i++)
			m_battleService[i].stop();
		m_networkService.stop();
		System.out.println("INFO: Service Manager stopped.");
	}
}
