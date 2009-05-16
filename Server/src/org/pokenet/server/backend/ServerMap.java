package org.pokenet.server.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.pokenet.server.backend.entity.Char;
import org.pokenet.server.backend.entity.NonPlayerChar;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.backend.entity.Positionable.Direction;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.feature.TimeService;
import org.pokenet.server.feature.TimeService.Weather;

import tiled.core.Map;
import tiled.core.TileLayer;

/**
 * Represents a map in the game world
 * @author shadowkanji
 *
 */
public class ServerMap {
	public enum PvPType { DISABLE, ENABLED, ENFORCED }
	
	//Stores the width, heigth, x, y and offsets of this map
	private int m_width;
	private int m_heigth;
	private int m_x;
	private int m_y;
	private int m_xOffsetModifier;
	private int m_yOffsetModifier;
	@SuppressWarnings("unused")
	private PvPType m_pvpType = PvPType.ENABLED;
	private ServerMapMatrix m_mapMatrix;
	private Weather m_forcedWeather = null;
	//Players and NPCs
	private ArrayList<PlayerChar> m_players;
	private ArrayList<NonPlayerChar> m_npcs;
	private ArrayList<WarpTile> m_warps;
	private ArrayList<MapItem> m_items;
	//The following stores information for day, night and water wild pokemon
	private HashMap<String, int[]> m_dayPokemonLevels;
	private HashMap<String, Integer> m_dayPokemonChances;
	private HashMap<String, int[]> m_nightPokemonLevels;
	private HashMap<String, Integer> m_nightPokemonChances;
	private HashMap<String, int[]> m_waterPokemonLevels;
	private HashMap<String, Integer> m_waterPokemonChances;
	private int m_wildProbability;
	//The following stores collision information
	private TileLayer m_blocked = null;
	private TileLayer m_surf = null;
	private TileLayer m_grass = null;
	private TileLayer m_ledgesDown = null;
	private TileLayer m_ledgesLeft = null;
	private TileLayer m_ledgesRight = null;
	//Misc
	private Random m_random = DataService.getBattleMechanics().getRandom();
	
	/**
	 * Default constructor
	 * @param map
	 * @param x
	 * @param y
	 */
	public ServerMap(Map map, int x, int y) {
		m_x = x;
		m_y = y;
		m_heigth = map.getHeight();
		m_width = map.getWidth();
		/*
		 * Store all the map layers
		 */
		for(int i = 0; i < map.getTotalLayers(); i++) {
			if(map.getLayer(i).getName().equalsIgnoreCase("Grass")) {
				m_grass = (TileLayer) map.getLayer(i);
			} else if(map.getLayer(i).getName().equalsIgnoreCase("Collisions")) {
				m_blocked = (TileLayer) map.getLayer(i);
			} else if(map.getLayer(i).getName().equalsIgnoreCase("LedgesLeft")) {
				m_ledgesLeft = (TileLayer) map.getLayer(i);
			} else if(map.getLayer(i).getName().equalsIgnoreCase("LedgesRight")) {
				m_ledgesRight = (TileLayer) map.getLayer(i);
			} else if(map.getLayer(i).getName().equalsIgnoreCase("LedgesDown")) {
				m_ledgesDown = (TileLayer) map.getLayer(i);
			} else if(map.getLayer(i).getName().equalsIgnoreCase("Water")) {
				m_surf = (TileLayer) map.getLayer(i);
			}
		}
		
		m_players = new ArrayList<PlayerChar>();
		m_npcs = new ArrayList<NonPlayerChar>();
		
		/*
		 * Add enforced weather if any
		 */
		try {
			if(x < -30) {
				if(x != -49 || y != -3) {
					m_forcedWeather = Weather.NORMAL;
				}
			} else if(map.getProperties().getProperty("forcedWeather") != null && 
					!map.getProperties().getProperty("forcedWeather").equalsIgnoreCase("")) {
				m_forcedWeather = Weather.valueOf(map.getProperties().getProperty("forcedWeather"));
			}
		} catch (Exception e) {
			m_forcedWeather = null;
		}
		
		/*
		 * Load offsets
		 */
		try {
			m_xOffsetModifier = Integer.parseInt(map.getProperties().getProperty("xOffsetModifier"));
		} catch (Exception e) {
			m_xOffsetModifier = 0;
		}
		try {
			m_yOffsetModifier = Integer.parseInt(map.getProperties().getProperty("yOffsetModifier"));
		} catch (Exception e) {
			m_yOffsetModifier = 0;
		}
		
		/*
		 * Load wild pokemon
		 */
		try {
			if(!map.getProperties().getProperty("wildProbabilty").equalsIgnoreCase("")) {
				m_wildProbability = Integer.parseInt(map.getProperties().getProperty("wildProbabilty"));
			} else {
				m_wildProbability = 28;
			}
		} catch (Exception e) {
			m_wildProbability = 28;
		}
		
		String[] species;
		String[] levels;
		//Daytime Pokemon
		try {
			if(!map.getProperties().getProperty("dayPokemonChances").equalsIgnoreCase("")) {
				species = map.getProperties().getProperty("dayPokemonChances").split(";");
				levels = map.getProperties().getProperty("dayPokemonLevels").split(";");
				if (!species[0].equals("") && !levels[0].equals("") && species.length == levels.length) {
					m_dayPokemonChances = new HashMap<String, Integer>();
					m_dayPokemonLevels = new HashMap<String, int[]> ();
						for (int i = 0; i < species.length; i++) {
							String[] speciesInfo = species[i].split(",");
							m_dayPokemonChances.put(speciesInfo[0], Integer.parseInt(speciesInfo[1]));
							String[] levelInfo = levels[i].split("-");
							m_dayPokemonLevels.put(speciesInfo[0], new int[] {
									Integer.parseInt(levelInfo[0]),
									Integer.parseInt(levelInfo[1]) });
						}
				}
			}
		} catch (Exception e) {
			m_dayPokemonChances = null;
			m_dayPokemonLevels = null;
			species = new String[] { "" };
			levels = new String[] { "" };
		}
		//Nocturnal Pokemon
		try {
			if(!map.getProperties().getProperty("nightPokemonChances").equalsIgnoreCase("")) {
				species = map.getProperties().getProperty("nightPokemonChances").split(";");
				levels = map.getProperties().getProperty("nightPokemonLevels").split(";");
				if (!species[0].equals("") && !levels[0].equals("") && species.length == levels.length) {
					m_nightPokemonChances = new HashMap<String, Integer>();
					m_nightPokemonLevels = new HashMap<String, int[]> ();
						for (int i = 0; i < species.length; i++) {
							String[] speciesInfo = species[i].split(",");
							m_nightPokemonChances.put(speciesInfo[0], Integer.parseInt(speciesInfo[1]));
							String[] levelInfo = levels[i].split("-");
							m_nightPokemonLevels.put(speciesInfo[0], new int[] {
									Integer.parseInt(levelInfo[0]),
									Integer.parseInt(levelInfo[1]) });
						}
				}
			}
		} catch (Exception e) {
			m_nightPokemonChances = null;
			m_nightPokemonLevels = null;
			species = new String[] { "" };
			levels = new String[] { "" };
		}
		//Surf Pokemon
		try {
			if(!map.getProperties().getProperty("waterPokemonChances").equalsIgnoreCase("")) {
				species = map.getProperties().getProperty("waterPokemonChances").split(";");
				levels = map.getProperties().getProperty("waterPokemonLevels").split(";");
				if (!species[0].equals("") && !levels[0].equals("") && species.length == levels.length) {
					m_waterPokemonChances = new HashMap<String, Integer>();
					m_waterPokemonLevels = new HashMap<String, int[]> ();
						for (int i = 0; i < species.length; i++) {
							String[] speciesInfo = species[i].split(",");
							m_waterPokemonChances.put(speciesInfo[0], Integer.parseInt(speciesInfo[1]));
							String[] levelInfo = levels[i].split("-");
							m_waterPokemonLevels.put(speciesInfo[0], new int[] {
									Integer.parseInt(levelInfo[0]),
									Integer.parseInt(levelInfo[1]) });
						}
				}
			}
		} catch (Exception e) {
			m_waterPokemonChances = null;
			m_waterPokemonLevels = null;
			species = new String[] { "" };
			levels = new String[] { "" };
		}
	}
	
	/**
	 * Loads all npc and warp tile data
	 */
	public void loadData() {
		/*
		 * Load all npcs and warptiles
		 */
		File f = new File("res/npc/" + m_x + "." + m_y + ".txt");
		if(f.exists()) {
			try {
				@SuppressWarnings("unused")
				DataLoader d = new DataLoader(f, this);
			} catch (Exception e) {
				
			}
		}
	}
	
	/**
	 * Adds a warp tile to the map
	 * @param w
	 */
	public void addWarp(WarpTile w) {
		if(m_warps == null)
			m_warps = new ArrayList<WarpTile>();
		m_warps.add(w);
	}
	
	/**
	 * Adds an item to the map
	 * @param x
	 * @param y
	 * @param id
	 */
	public void addItem(int x, int y, int id) {
		m_items.add(new MapItem(x, y, id));
	}
	
	/**
	 * Allows a player to pick up an item
	 * @param p
	 */
	public void pickupItem(PlayerChar p) {
		
	}
	
	/**
	 * Returns true if this map has a forced weather
	 * @return
	 */
	public boolean isWeatherForced() {
		return m_forcedWeather != null;
	}
	
	/**
	 * Returns the enforced weather on this map
	 * @return
	 */
	public Weather getWeather() {
		return m_forcedWeather;
	}
	
	/**
	 * Sets forced weather
	 * @param w
	 */
	public void setWeather(Weather w) {
		m_forcedWeather = w;
	}
	
	/**
	 * Removes forced weather
	 */
	public void removeWeather() {
		m_forcedWeather = null;
	}
	
	/**
	 * Returns the weather id for the enforced weather on this map
	 * @return
	 */
	public int getWeatherId() {
		if(m_forcedWeather != null) {
			switch(m_forcedWeather) {
			case NORMAL:
				return 0;
			case RAIN:
				return 1;
			case HAIL:
				return 2;
			case SANDSTORM:
				return 3;
			case FOG:
				return 4;
			default:
				return 0;
			}
		} else
			return 0;
	}
	
	/**
	 * Sets the map matrix
	 * @param matrix
	 */
	public void setMapMatrix(ServerMapMatrix matrix) {
		m_mapMatrix = matrix;
	}
	
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
			if(c.getId() != m_players.get(i).getId())
				m_players.get(i).getSession().write("ma" + c.getName() + "," + 
					c.getId() + "," + c.getSprite() + "," + c.getX() + "," + c.getY() + "," + 
					(c.getFacing() == Direction.Down ? "D" : 
						c.getFacing() == Direction.Up ? "U" :
							c.getFacing() == Direction.Left ? "L" :
								"R"));
		}
	}
	
	/**
	 * Adds a char and sets their x y based on a 32 by 32 pixel grid.
	 * Allows easier adding of NPCs as the x,y can easily be counted via Tiled
	 * @param c
	 * @param tileX
	 * @param tileY
	 */
	public void addChar(Char c, int tileX, int tileY) {
		this.addChar(c);
		c.setX(tileX * 32);
		c.setY((tileY * 32) - 8);
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
	 * Returns the width of this map
	 * @return
	 */
	public int getWidth() {
		return m_width;
	}
	
	/**
	 * Returns the height of this map
	 * @return
	 */
	public int getHeight() {
		return m_heigth;
	}
	
	/**
	 * Returns the x offset of this map
	 * @return
	 */
	public int getXOffsetModifier() {
		return m_xOffsetModifier;
	}
	
	/**
	 * Returns the y offset of this map
	 * @return
	 */
	public int getYOffsetModifier() {
		return m_yOffsetModifier;
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
			m_players.get(i).getSession().write("mr" + c.getId());
		}
	}
	
	/**
	 * Allows a player to talk to the npc in front of them, if any
	 * @param p
	 */
	public void talkToNpc(PlayerChar p) {
		int x = 0, y = 0;
		switch(p.getFacing()) {
		case Up:
			x = p.getX();
			y = p.getY() - 32;
			break;
		case Down:
			x = p.getX();
			y = p.getY() + 32;
			break;
		case Left:
			x = p.getX() - 32;
			y = p.getY();
			break;
		case Right:
			x = p.getX() + 32;
			y = p.getY();
			break;
		default:
			break;
		}
		for(int i = 0; i < m_npcs.size(); i++) {
			if(m_npcs.get(i).getX() == x && m_npcs.get(i).getY() == y) {
				p.setTalking(true);
				m_npcs.get(i).talkToPlayer(p);
				break;
			}
		}
	}
	
	/**
	 * Returns true if there is an obstacle
	 * @param x
	 * @param y
	 * @param d
	 * @return
	 */
	private boolean isBlocked(int x, int y, Direction d) {
		if (m_blocked.getTileAt(x, y) != null)
			return true;
		for(int i = 0; i < m_npcs.size(); i++) {
			if(m_npcs.get(i).getX() == (x * 32) && m_npcs.get(i).getY() == ((y * 32) - 8))
				return true;
		}
		if(m_ledgesRight != null && m_ledgesRight.getTileAt(x, y) != null) {
			if(d == Direction.Left || d == Direction.Up || d == Direction.Down)
				return true;
		}
		if(m_ledgesLeft != null && m_ledgesLeft.getTileAt(x, y) != null) {
			if(d == Direction.Right || d == Direction.Up || d == Direction.Down)
				return true;
		}
		if(m_ledgesDown != null && m_ledgesDown.getTileAt(x, y) != null) {
			if(d == Direction.Left || d == Direction.Up || d == Direction.Right)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the char was warped
	 * @param x
	 * @param y
	 * @param c
	 * @return
	 */
	private boolean isWarped(int x, int y, Char c) {
		if(m_warps != null) {
			for(int i = 0; i < m_warps.size(); i++) {
				if(m_warps.get(i).getX() == x && m_warps.get(i).getY() == y) {
					m_warps.get(i).warp(c);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Attempts to move a char and sends the movement to everyone, returns true on success
	 * @param c
	 * @param d
	 */
	public boolean moveChar(Char c, Direction d) {
		int playerX = c.getX();
		int playerY = c.getY();
		int newX;
		int newY;

		switch(d) {
		case Up:
			newX = playerX / 32;
			newY = ((playerY + 8) - 32) / 32;
			if (playerY >= 1) {
				if (!isBlocked(newX, newY, Direction.Up)) {
					if(m_surf != null && m_surf.getTileAt(newX, newY) != null) {
						if(c.isSurfing()) {
							return true;
						} else {
							if(c instanceof PlayerChar) {
								PlayerChar p = (PlayerChar) c;
								if(p.canSurf()) {
									p.setSurfing(true);
									return true;
								} else {
									return false;
								}
							}
						}
					} else {
						if(c.isSurfing())
							c.setSurfing(false);
						if(!isWarped(newX, newY, c))
							return true;
					}
				}
			} else {
				ServerMap newMap = m_mapMatrix.getMapByGamePosition(m_x, m_y - 1);
				if (newMap != null) {
					m_mapMatrix.moveBetweenMaps(c, this, newMap);
				}
			}
			break;
		case Down:
			newX = playerX / 32;
			newY = ((playerY + 8) + 32) / 32;
			if (playerY + 40 < m_heigth * 32) {
				if (!isBlocked(newX, newY, Direction.Down)) {
					if(m_surf != null && m_surf.getTileAt(newX, newY) != null) {
						if(c.isSurfing()) {
							return true;
						} else {
							if(c instanceof PlayerChar) {
								PlayerChar p = (PlayerChar) c;
								if(p.canSurf()) {
									p.setSurfing(true);
									return true;
								} else {
									return false;
								}
							}
						}
					} else {
						if(c.isSurfing())
							c.setSurfing(false);
						if(!isWarped(newX, newY, c))
							return true;
					}
				}
			} else {
				ServerMap newMap = m_mapMatrix.getMapByGamePosition(m_x, m_y + 1);
				if (newMap != null) {
					m_mapMatrix.moveBetweenMaps(c, this, newMap);
				}
			}
			break;
		case Left:
			newX = (playerX - 32) / 32;
			newY = (playerY + 8) / 32;
			if (playerX >= 32) {
				if (!isBlocked(newX, newY, Direction.Left)) {
					if(m_surf != null && m_surf.getTileAt(newX, newY) != null) {
						if(c.isSurfing()) {
							return true;
						} else {
							if(c instanceof PlayerChar) {
								PlayerChar p = (PlayerChar) c;
								if(p.canSurf()) {
									p.setSurfing(true);
									return true;
								} else {
									return false;
								}
							}
						}
					} else {
						if(c.isSurfing())
							c.setSurfing(false);
						if(!isWarped(newX, newY, c))
							return true;
					}
				}
			} else {
				ServerMap newMap = m_mapMatrix.getMapByGamePosition(m_x - 1, m_y);
				if (newMap != null) {
					m_mapMatrix.moveBetweenMaps(c, this, newMap);
				}
			}
			break;
		case Right:
			newX = (playerX + 32) / 32;
			newY = (playerY + 8) / 32;
			if (playerX + 32 < m_width * 32) {
				if (!isBlocked(newX, newY, Direction.Right)) {
					if(m_surf != null && m_surf.getTileAt(newX, newY) != null) {
						if(c.isSurfing()) {
							return true;
						} else {
							if(c instanceof PlayerChar) {
								PlayerChar p = (PlayerChar) c;
								if(p.canSurf()) {
									p.setSurfing(true);
									return true;
								} else {
									return false;
								}
							}
						}
					} else {
						if(c.isSurfing())
							c.setSurfing(false);
						if(!isWarped(newX, newY, c))
							return true;
					}
				}
			} else {
				ServerMap newMap = m_mapMatrix.getMapByGamePosition(m_x + 1, m_y);
				if (newMap != null) {
					m_mapMatrix.moveBetweenMaps(c, this, newMap);
				}
			}
			break;
		}
		return false;
	}
	
	/**
	 * Returns true if a wild pokemon was encountered.
	 * @return
	 */
	public boolean isWildBattle(int x, int y, PlayerChar p) {
		if (m_random.nextInt(2874) < m_wildProbability * 16) {
			if(p.isSurfing()) {
				if(m_waterPokemonChances != null && m_waterPokemonLevels != null)
					return true;
			} else {
				if (m_grass != null && m_grass.getTileAt(x / 32, (y + 8) / 32) != null)
					if((m_dayPokemonChances != null && m_dayPokemonLevels != null) ||
							(m_nightPokemonChances != null && m_nightPokemonLevels != null))
						return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a wild pokemon.
	 * Different players have different chances of encountering rarer Pokemon.
	 * @return
	 */
	public Pokemon getWildPokemon(PlayerChar player) {
		int [] range;
		String species;
		if(player.isSurfing()) {
			//Generate a Pokemon from the water
			species = getWildSpeciesWater();
			range = m_waterPokemonLevels.get(species);
			return Pokemon.getRandomPokemon(species, (m_random.nextInt((range[1] - range[0]) + 1)) + range[0]);
		} else {
			if(TimeService.isNight()) {
				//Generate a nocturnal Pokemon
				species = getWildSpeciesNight();
				range = m_nightPokemonLevels.get(species);
				return Pokemon.getRandomPokemon(species, (m_random.nextInt((range[1] - range[0]) + 1)) + range[0]);
			} else {
				//Generate a day Pokemon
				species = getWildSpeciesDay();
				range = m_dayPokemonLevels.get(species);
				return Pokemon.getRandomPokemon(species, (m_random.nextInt((range[1] - range[0]) + 1)) + range[0]);
			}
		}
	}
	
	/**
	 * Returns a wild species for day
	 * @return
	 */
	private String getWildSpeciesDay() {
		ArrayList<String> potentialSpecies = new ArrayList<String>();
		do {
			for (String species : m_dayPokemonChances.keySet()) {
				if (m_random.nextInt(101) < m_dayPokemonChances.get(species))
					potentialSpecies.add(species);
			}
		} while (potentialSpecies.size() <= 0);
		return potentialSpecies.get(m_random.nextInt(potentialSpecies.size()));
	}
	
	/**
	 * Returns a wild species for night
	 * @return
	 */
	private String getWildSpeciesNight() {
		ArrayList<String> potentialSpecies = new ArrayList<String>();
		do {
			for (String species : m_nightPokemonChances.keySet()) {
				if (m_random.nextInt(101) < m_nightPokemonChances.get(species))
					potentialSpecies.add(species);
			}
		} while (potentialSpecies.size() <= 0);
		return potentialSpecies.get(m_random.nextInt(potentialSpecies.size()));
	}
	
	/**
	 * Returns a wild species for water
	 * @return
	 */
	private String getWildSpeciesWater() {
		ArrayList<String> potentialSpecies = new ArrayList<String>();
		do {
			for (String species : m_waterPokemonChances.keySet()) {
				if (m_random.nextInt(101) < m_waterPokemonChances.get(species))
					potentialSpecies.add(species);
			}
		} while (potentialSpecies.size() <= 0);
		return potentialSpecies.get(m_random.nextInt(potentialSpecies.size()));
	}
	
	/**
	 * Sends a packet to all players on the map
	 * @param message
	 */
	public void sendToAll(String message) {
		for(int i = 0; i < m_players.size(); i++) {
			m_players.get(i).getSession().write(message);
		}
	}
	
	/**
	 * Returns the arraylist of players
	 * @return
	 */
	public ArrayList<PlayerChar> getPlayers() {
		return m_players;
	}
	
	/**
	 * Returns the arraylist of npcs
	 * @return
	 */
	public ArrayList<NonPlayerChar> getNpcs() {
		return m_npcs;
	}
}
