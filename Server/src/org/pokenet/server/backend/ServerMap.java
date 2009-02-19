package org.pokenet.server.backend;

import java.util.ArrayList;
import java.util.HashMap;

import org.pokenet.server.backend.entity.Char;
import org.pokenet.server.backend.entity.NonPlayerChar;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.backend.entity.Positionable.Direction;

import tiled.core.TileLayer;

/**
 * Represents a map in the game world
 * @author shadowkanji
 *
 */
public class ServerMap {
	public enum PvPType { DISABLE, ENABLED, ENFORCED }
	
	private int m_x;
	private int m_y;
	private PvPType m_pvpType = PvPType.ENABLED;
	//Players and NPCs
	private ArrayList<PlayerChar> m_players;
	private ArrayList<NonPlayerChar> m_npcs;
	//The following stores information for day, night and water wild pokemon
	private HashMap<String, int[]> m_dayPokemonLevels;
	private HashMap<String, Integer> m_dayPokemonChances;
	private HashMap<String, int[]> m_nightPokemonLevels;
	private HashMap<String, Integer> m_nightPokemonChances;
	private HashMap<String, int[]> m_waterPokemonLevels;
	private HashMap<String, Integer> m_waterPokemonChances;
	//The following stores collision information
	private TileLayer blocked = null;
	private TileLayer surf = null;
	private TileLayer grass = null;
	private TileLayer ledgesDown = null;
	private TileLayer ledgesLeft = null;
	private TileLayer ledgesRight = null;
	
	/**
	 * Adds a player to this map and notifies all other clients on the map.
	 * @param player
	 */
	public void addChar(Char c) {
		if(c instanceof PlayerChar) {
			m_players.add((PlayerChar) c);
		} else if(c instanceof NonPlayerChar) {
			//Set the id of the npc
			c.setId(-1 - m_npcs.size());
			m_npcs.add((NonPlayerChar) c);
		}
		for(int i = 0; i < m_players.size(); i++) {
			//TODO: Send information of new player to clients on map
		}
	}
	
	/**
	 * Returns the x co-ordinate of this servermap in the map matrix
	 * @return
	 */
	public int getX() {
		return m_x;
	}
	
	/**
	 * Returns the y co-ordinate of this servermap in the map matrix
	 * @return
	 */
	public int getY() {
		return m_y;
	}
	
	/**
	 * Removes a char from this map
	 * @param c
	 */
	public void removeChar(Char c) {
		if(c instanceof PlayerChar) {
			m_players.remove((PlayerChar) c);
			m_players.trimToSize();
		} else if(c instanceof NonPlayerChar) {
			m_npcs.remove((NonPlayerChar) c);
			m_npcs.trimToSize();
		}
		for(int i = 0; i < m_players.size(); i++) {
			//TODO: Send information to all clients on map
		}
	}
	
	/**
	 * Attempts to move a char and sends the movement to everyone, returns true on success
	 * @param c
	 * @param d
	 */
	public boolean moveChar(Char c, Direction d) {
		//TODO: Check if the character can move, if so send the movement info to everyone and return true
		return false;
	}
}
