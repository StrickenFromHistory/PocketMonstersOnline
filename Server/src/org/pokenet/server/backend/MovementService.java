package org.pokenet.server.backend;

import java.io.File;
import java.util.HashMap;

import org.pokenet.server.GameServer;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.backend.map.ServerMap;
import org.pokenet.server.backend.map.ServerMapMatrix;
import org.pokenet.server.battle.impl.NpcSleepTimer;

import tiled.io.xml.XMLMapTransformer;

/**
 * Stores the map matrix and movement managers.
 * @author shadowkanji
 *
 */
public class MovementService {
	private MovementManager [] m_movementManager;
	private ServerMapMatrix m_mapMatrix;
	private ServerMap m_tempMap;
	private NpcSleepTimer m_sleepTimer;
	
	/**
	 * Default constructor
	 */
	public MovementService() {
		m_movementManager = new MovementManager[GameServer.getMovementThreadAmount()];
		m_mapMatrix = new ServerMapMatrix();
		m_sleepTimer = new NpcSleepTimer();
	}
	
	/**
	 * Removes a player from the movement service
	 * @param username
	 */
	public void removePlayer(String username) {
		for(int i = 0; i < m_movementManager.length; i++) {
			if(m_movementManager[i].removePlayer(username)) {
				return;
			}
		}
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
		if(m_movementManager[smallest] == null) {
			m_movementManager[smallest] = new MovementManager();
		}
		if(!m_movementManager[smallest].isRunning()) {
			m_movementManager[smallest].start();
		}
		return m_movementManager[smallest];
	}
	
	/**
	 * Returns the map matrix
	 * @return
	 */
	public ServerMapMatrix getMapMatrix() {
		return m_mapMatrix;
	}
	
	/**
	 * Reloads all maps while the server is running. Puts all players in m_tempMap.
	 * An NPC is there to allow them to return to where they last where when they are ready.
	 * Optionally, we can skip saving players in a temporary map.
	 * @param forceSkip
	 */
	public void reloadMaps(boolean forceSkip) {
		/*
		 * First move all players out of their maps
		 */
		if(!forceSkip) {
			HashMap<String, PlayerChar> players;
			for(int x = 0; x < 100; x++) {
				for(int y = 0; y < 100; y++) {
					if(m_mapMatrix.getMapByRealPosition(x, y) != null) {
						players = m_mapMatrix.getMapByRealPosition(x, y).getPlayers();
						for(PlayerChar p : players.values()) {
							p.setLastHeal(p.getX(), p.getY(), p.getMapX(), p.getMapY());
							p.setMap(m_tempMap, null);
						}
					}
				}
			}
		}
		/*
		 * Reload all the maps
		 */
		XMLMapTransformer loader = new XMLMapTransformer();
		File nextMap;
		ServerMap s;
		for(int x = -50; x < 50; x++) {
			for(int y = -50; y < 50; y++) {
				nextMap = new File("res/maps/" + String.valueOf(x) + "." + String.valueOf(y) + ".tmx");
				if(nextMap.exists()) {
					try {
						s = new ServerMap(loader.readMap(nextMap.getCanonicalPath()), x, y);
						s.setMapMatrix(m_mapMatrix);
						s.loadData();
						m_mapMatrix.setMap(s , x + 50, y + 50);
						Thread.sleep(100);
					} catch (Exception e) {
						System.err.println("Error loading " + x + "." + y + ".tmx - Bad map file");
						m_mapMatrix.setMap(null, x + 50, y + 50);
					}
				}
			}
		}
		System.out.println("INFO: Maps loaded");
	}
	
	/**
	 * Reloads all maps while the server is still running.
	 */
	public void reloadMaps() {
		this.reloadMaps(false);
	}
	
	/**
	 * Starts the movement service
	 */
	public void start() {
		this.reloadMaps(true);
		m_sleepTimer.start();
		for(int i = 0; i < m_movementManager.length; i++) {
			m_movementManager[i] = new MovementManager();
			m_movementManager[i].start();
		}
		System.out.println("INFO: Movement Service started");
	}
	
	/**
	 * Stops the movement service
	 */
	public void stop() {
		m_sleepTimer.stop();
		for(int i = 0; i < m_movementManager.length; i++) {
			m_movementManager[i].stop();
		}
		System.out.println("INFO: Movement Service stopped");
	}
}