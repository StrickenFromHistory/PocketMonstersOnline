package org.pokenet.server;

/**
 * Represents a game server.
 * 
 * Starting a server requires a parameter to be passed in, i.e. java GameServer -low
 * Here are the different settings:
 * -low
 * 		< 1.86ghz
 * 		< 512MB Ram
 * 		< 1mbps Up/Down Connection
 * 		75 Playeys
 * -medium
 * 		< 2ghz
 * 		1GB Ram
 * 		1mbps Up/Down Connection
 * 		200 Players
 * -high
 * 		> 1.86ghz
 * 		> 1GB Ram
 * 		> 1mbps Up/Down Connection
 * 		> 500 Players
 * @author shadowkanji
 *
 */
public class GameServer {
	private static ServiceManager m_serviceManager;
	private static int m_maxPlayers, m_movementThreads, m_battleThreads;
	
	/**
	 * If you don't know what this method does, you clearly don't know enough Java to be working on this.
	 * @param args
	 */
	public static void main(String [] args) {
		if(args.length > 0) {
			/*
			 * The following sets the server's settings based on the
			 * computing ability of the server specified by the server owner.
			 */
			if(args[0].equalsIgnoreCase("-low")) {
				m_maxPlayers = 75;
				m_movementThreads = 2;
				m_battleThreads = 1;
			} else if(args[0].equalsIgnoreCase("-medium")) {
				m_maxPlayers = 200;
				m_movementThreads = 4;
				m_battleThreads = 2;
			} else if(args[0].equalsIgnoreCase("-high")) {
				m_maxPlayers = 500;
				m_movementThreads = 8;
				m_battleThreads = 4;
			} else {
				System.err.println("Server requires a settings parameter, e.g. java GameServer -medium");
				System.exit(0);
			}
			/*
			 * For the moment we'll just start the service manager but
			 * we'll make it open a gui which can start/shutdown the server.
			 * Just stopping the server isn't safe so a shutdown method will
			 * be needed.
			 */
			m_serviceManager = new ServiceManager();
			m_serviceManager.start();
		} else {
			System.err.println("Server requires a settings parameter, e.g. java GameServer -medium");
		}
	}
	
	/**
	 * Returns the service manager of the server
	 * @return
	 */
	public static ServiceManager getServiceManager() {
		return m_serviceManager;
	}
	
	/**
	 * Returns the amount of players this server will allow
	 * @return
	 */
	public static int getMaxPlayers() {
		return m_maxPlayers;
	}
	
	/**
	 * Returns the amount of battle threads running in this server
	 * @return
	 */
	public static int getBattleThreadAmount() {
		return m_battleThreads;
	}
	
	/**
	 * Returns the amount of movement threads running in this server
	 * @return
	 */
	public static int getMovementThreadAmount() {
		return m_movementThreads;
	}
}
