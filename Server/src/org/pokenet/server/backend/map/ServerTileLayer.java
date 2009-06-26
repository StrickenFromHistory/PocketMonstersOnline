package org.pokenet.server.backend.map;

import java.util.ArrayList;
import java.util.HashMap;

import tiled.core.TileLayer;

/**
 * Stores a tile layer
 * @author shadowkanji
 *
 */
public class ServerTileLayer {
	private HashMap<ServerTile, Byte> m_tiles;
	/**
	 * Constructor
	 * @param m
	 */
	public ServerTileLayer(TileLayer m) {
		m_tiles = new HashMap<ServerTile, Byte>();
		for(int x = 0; x < m.getWidth(); x++) {
			for(int y = 0; y < m.getHeight(); y++) {
				if(m.getTileAt(x, y) != null) {
					m_tiles.put(new ServerTile(x, y), new Byte("1"));
				}
			}
		}
	}
	
	/**
	 * Returns true ithe tile at x, y
	 * @param x
	 * @param y
	 * @return
	 */
	public Byte getTileAt(int x, int y) {
		return m_tiles.get(new ServerTile(x, y));
	}
}
