package org.pokenet.server;

import org.pokenet.server.backend.MovementService;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.feature.JythonService;
import org.pokenet.server.feature.TimeService;
import org.pokenet.server.network.NetworkService;

/**
 * Handles all services on the game server
 * @author shadowkanji
 *
 */
public class ServiceManager {
	private NetworkService m_networkService;
	private MovementService m_movementService;
	private DataService m_dataService;
	private TimeService m_timeService;
	private JythonService m_jythonService;
	
	/**
	 * Default constructor
	 */
	public ServiceManager() {
		/*
		 * Initialize all the services
		 */
		m_jythonService = new JythonService();
		m_timeService = new TimeService();
		m_dataService = new DataService();
		m_networkService = new NetworkService();
		m_movementService = new MovementService();
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
	 * Returns the time service
	 * @return
	 */
	public TimeService getTimeService() {
		return m_timeService;
	}
	
	/**
	 * Returns the jython service
	 * @return
	 */
	public JythonService getJythonService() {
		return m_jythonService;
	}
	
	/**
	 * Starts all services
	 */
	public void start() {
		/*
		 * Start the network service first as it needs to bind the address/port to the game server.
		 * Then start all other services with TimeService last.
		 */
		m_movementService.start();
		m_networkService.start();
		m_timeService.start();
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
		m_timeService.stop();
		m_movementService.stop();
		m_networkService.stop();
		System.out.println("INFO: Service Manager stopped.");
	}
}
