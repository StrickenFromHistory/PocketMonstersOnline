package org.pokenet.server.backend.map;

import java.util.ArrayList;

import tiled.core.TileLayer;

/**
 * Stores a tile layer
 * @author shadowkanji
 *
 */
public class ServerTileLayer {
	private ArrayList<ServerTile> m_tiles;
	/**
	 * Constructor
	 * @param m
	 */
	public ServerTileLayer(TileLayer m) {
		m_tiles = new ArrayList<ServerTile>();
		for(int x = 0; x < m.getWidth(); x++) {
			for(int y = 0; y < m.getHeight(); y++) {
				if(m.getTileAt(x, y) != null) {
					m_tiles.add(new ServerTile(x, y));
				}
			}
		}
	}
	
	/**
	 * Returns the tile at x, y
	 * @param x
	 * @param y
	 * @return
	 */
	public ServerTile getTileAt(int x, int y) {
		for(int i = 0; i < m_tiles.size(); i++) {
			if(m_tiles.get(i).getX() == x && m_tiles.get(i).getY() == y)
				return m_tiles.get(i);
		}
		return null;
	}
}
