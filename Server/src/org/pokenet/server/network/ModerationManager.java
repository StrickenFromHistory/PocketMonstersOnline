package org.pokenet.server.network;

import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.pokenet.server.GameServer;
import org.pokenet.server.backend.entity.Bag;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.backend.entity.PokemonBox;
import org.pokenet.server.backend.entity.Positionable.Direction;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.Pokemon.ExpTypes;
import org.pokenet.server.battle.mechanics.PokemonNature;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;

/**
 * Handles moderator commands
 * @author felty.wos
 *
 */
public class ModerationManager {
	private MySqlManager m_database;
	
	/**
	 * Default Constructor
	 */
	public ModerationManager() {
		m_database = new MySqlManager();
	}
	
	/**
	 * Parses messages for moderator commands
	 * @param msg
	 * @param p
	 * @return
	 */
	public boolean processModeratorCommands(String msg, PlayerChar p) {
		// Level 0 - Nothing special, they don't get to do anything.
		if (p.getAdminLevel() == 0)
			return false;
		
		// Level 1 - Gets to do something, like warn and temp punishments. PMod.
		if (p.getAdminLevel() >= 1) {
			// TODO decide commands to give
		}
		
		// Level 2 - Can do something more. Can kick and punish. Mod level 1.
		if (p.getAdminLevel() >= 2) {
			// TODO decide commands to give
		}
		
		// Level 3 - Now we're going somewhere! Teleport and move people. Mod 2.
		if (p.getAdminLevel() >= 3) {
			// TODO decide commands to give
		}
		
		// Level 4 - Ban hammer. Fun stuff. Mod 3.
		if (p.getAdminLevel() >= 4) {
			// TODO decide commands to give
		}
		
		// Level 5 - Bigger ban hammer. Player manipulation commands. Mod 4.
		if (p.getAdminLevel() >= 5) {
			// TODO decide commands to give
		}
		
		// Level 6 - Delete. Anything else not mentioned elsewhere.
		if (p.getAdminLevel() >= 6) {
			// TODO decide commands to give
		}
		
		return false;
	}
	
	/**
	 * Returns the player object if they exist and not on another server, otherwise null is returned.
	 * @param player
	 * @return
	 */
	private PlayerChar getPlayer(String player) {
		/*
		 * 1. Fetch player object from players currently on this server
		 * 2. Fetch player object from the database
		 */
		if (ConnectionManager.getPlayers().containsKey(player))
			return ConnectionManager.getPlayers().get(player);
		else if (playerExists(player))
			return getPlayerObject(player);
		else
			return null;
	}
	
	/**
	 * Checks to see if the character exists in the database and not logged in
	 * @param player
	 * @return
	 */
	private boolean playerExists(String player) {
		ResultSet data = m_database.query("SELECT * FROM pn_members WHERE username='" + player + "'");
		try {
			data.first();
		} catch (Exception e1) {}
		try {			
			// See if they exist in the database
			if(data != null && data.getString("username") != null && data.getString("username").equalsIgnoreCase(player)) {
				// See if they are not currently logged in
				if(data.getString("lastLoginServer").equalsIgnoreCase("null")) {
					return true;
				}
			}
		} catch (Exception e) {}
		return false;
	}
	
	/**
	 * Returns true if the player is currently on this server
	 * @param player
	 * @return
	 */
	private boolean playerOnline(String player) {
		if (ConnectionManager.getPlayers().containsKey(player))
			return true;
		else
			return false;
	}
	
	/**
	 * Compares GM levels
	 * @param player
	 * @param GM
	 * @return
	 */
	private boolean canDo(PlayerChar player, PlayerChar GM) {
		if (GM.getAdminLevel() > player.getAdminLevel())
			return true;
		else
			return false;
	}
	
	/**
	 * Teleports a player to the specified location
	 * @param player
	 * @param map
	 * @param coords
	 */
	private void teleport(String player, Point map, Point coords) {

	}
	
	/**
	 * Slides a player a certain distance in the given direction
	 * @param player
	 * @param dir
	 * @param distance
	 */
	private void slide(String player, Direction dir, int distance) {
		
	}
	
	/**
	 * Adds a word to the list of censored words
	 * @param word
	 */
	private void addWord(String word) {
		
	}
	
	/**
	 * Removes a word from the list of censored words
	 * @param word
	 */
	private void removeWord(String word) {
		
	}
	
	/**
	 * Sends the player a warning
	 * @param player
	 * @param warning
	 */
	private void warn(String player, String warning) {
		
	}
	
	/**
	 * Mutes a player for the given number of minutes, or until next login
	 * @param player
	 * @param minutes
	 */
	private void mute(String player, int minutes) {
		
	}
	
	/**
	 * Unmutes a player
	 * @param player
	 */
	private void unmute(String player) {
		
	}
	
	/**
	 * Stops a player from moving for the given number of minutes, or until next login
	 * @param player
	 * @param minutes
	 */
	private void freeze(String player, int minutes) {
		
	}
	
	/**
	 * Allows a frozen player to move again
	 * @param player
	 */
	private void thaw(String player) {
		
	}
	
	/**
	 * Kicks the player off of the server
	 * @param player
	 */
	private void kick(String player) {
		
	}
	
	/**
	 * Disconnects the player and all players with the same ip address
	 * @param player
	 */
	private void kickip(String player) {
		
	}
	
	/**
	 * Bans a player for the given number of minutes, or permanently
	 * @param player
	 * @param minutes
	 */
	private void ban(String player, int minutes) {
		
	}
	
	/**
	 * Unbans a player
	 * @param player
	 */
	private void unban(String player) {
		
	}
	
	/**
	 * Bans the ip address of a player for the given number of minutes or permanently
	 * @param player
	 */
	private void ipban(String player, int minutes) {
		
	}
	
	/**
	 * Unbans an ip address or player associated with an ip ban
	 * @param player
	 */
	private void unipban(String player) {
		
	}
	
	/**
	 * Changes the name of a player
	 * @param player
	 * @param newName
	 */
	private void changeName(String player, String newName) {
		
	}
	
	/**
	 * Bans a name, making it unavailable to new accounts
	 * @param name
	 */
	private void banName(String name) {
		
	}
	
	/**
	 * Unbans a name, making it available to new accounts
	 * @param name
	 */
	private void unbanName(String name) {
		
	}
	
	/**
	 * Returns the player ID from the database, account ID and last known IP address of the named player.
	 * @param player
	 */
	private void info(String player) {
		
	}
	
	/**
	 * Allows speaking as if they were another person. The person cannot be online.
	 * @param speaker
	 */
	private void impersonate(String speaker, String msg) {
		
	}
	
	/**
	 * Sends a message to everyone on the server
	 * @param msg
	 */
	private void announce(String msg) {
		
	}
	
	/**
	 * Sets the named skill to the specified level. If none specified, sets it to 100.
	 * @param skillName
	 * @param skillLevel
	 */
	private void setSkill(String name, String skillName, int skillLevel) {
		
	}
	
	/**
	 * Shows the bag and party of the named player to a GM
	 * @param player
	 */
	private void inspect(String player) {
		
	}
	
	/**
	 * Creates an item at your location, or gives to the specified player
	 * @param item
	 * @param player
	 */
	private void item(String item, String player) {
		
	}
	
	/**
	 * Creates a pokemon at your location, or gives to the specified player
	 * @param pokemon
	 * @param player
	 */
	private void pokemon(String pokemon, String player) {
		
	}
	
	/**
	 * Makes it rain on the given map
	 * @param map
	 */
	private void rain(Point map) {
		
	}
	
	/**
	 * Makes it snow on the given map
	 * @param map
	 */
	private void snow(Point map) {
		
	}
	
	/**
	 * Makes thunder on a given map
	 * @param map
	 */
	private void thunder(Point map) {
		
	}
	
	/**
	 * Makes fog appear on a given map
	 * @param map
	 */
	private void fog(Point map) {
		
	}
	
	/**
	 * Returns weather on a given map to normal
	 * @param map
	 */
	private void normalWeather(Point map) {
		
	}
	
	/**
	 * Sets a message of the day to send when a player connects
	 * @param motd
	 */
	private void setMotD(String motd) {
		
	}
	
	/**
	 * Sets a multiplier for all exp gains
	 * @param expMult
	 */
	private void setExpMult(int expMult) {
		
	}
	
	/**
	 * Sets a multiplier for all pokedollar gains
	 * @param PDMult
	 */
	private void setPDMult(int PDMult) {
		
	}
	
	/**
	 * Sets a pokemon to appear on a certain map
	 * @param pokemon
	 * @param map
	 * @param chance
	 */
	private void setPokemonOverride(String pokemon, Point map, int chance) {
		
	}
	
	/**
	 * Deletes a character from the database
	 * @param player
	 */
	private void deleteChar(String player) {
		
	}
	
	/**
	 * Sets the admin level of a player
	 * @param player
	 * @param level
	 */
	private void admin(String player, int level) {
		
	}
	
	// LOADING/SAVING PLAYER NOT ALREADY ONLINE
	/**
	 * Returns a playerchar object for a player
	 * @param data
	 * @return
	 */
	private PlayerChar getPlayerObject(String player) {
		try {
			//First connect to the database
			if(!m_database.connect(GameServer.getDatabaseHost(), GameServer.getDatabaseUsername(), GameServer.getDatabasePassword())) {
				return null;
			}
			m_database.selectDatabase(GameServer.getDatabaseName());
			//Then find the member's information
			ResultSet result = m_database.query("SELECT * FROM pn_members WHERE username='" + player + "'");
			result.first();
			try {
				PlayerChar p = new PlayerChar();
				Pokemon [] party = new Pokemon[6];
			
				p.setName(result.getString("username"));
				p.setVisible(true);
				//Set co-ordinates
				p.setX(result.getInt("x"));
				p.setY(result.getInt("y"));
				p.setMapX(result.getInt("mapX"));
				p.setMapY(result.getInt("mapY"));
				p.setId(result.getInt("id"));
				p.setAdminLevel(result.getInt("adminLevel"));
				p.setLastHeal(result.getInt("healX"), result.getInt("healY"), result.getInt("healMapX"), result.getInt("healMapY"));
				p.setSurfing(Boolean.parseBoolean(result.getString("isSurfing")));
				//Set money and skills
				p.setSprite(result.getInt("sprite"));
				p.setMoney(result.getInt("money"));
				p.setNpcMultiplier(Double.parseDouble(result.getString("npcMul")));
				p.setHerbalismExp(result.getInt("skHerb"));
				p.setCraftingExp(result.getInt("skCraft"));
				p.setFishingExp(result.getInt("skFish"));
				p.setTrainingExp(result.getInt("skTrain"));
				p.setCoordinatingExp(result.getInt("skCoord"));
				p.setBreedingExp(result.getInt("skBreed"));
				//Retrieve refences to all Pokemon
				int pokesId = result.getInt("pokemons");
				ResultSet pokemons = m_database.query("SELECT * FROM pn_mypokes WHERE id='" + pokesId + "'");
				pokemons.first();
				p.setDatabasePokemon(pokemons);
				//Attach party
				ResultSet partyInfo = m_database.query("SELECT * FROM pn_party WHERE id='" + pokemons.getInt("party") + "'");
				partyInfo.first();
				for(int i = 0; i < 6; i++) {
					party[i] = partyInfo.getInt("pokemon" + i) != -1 ? 
							getPokemonObject(m_database.query("SELECT * FROM pn_pokemon WHERE id='" + partyInfo.getInt("pokemon" + i) + "'"))
							: null;
				}
				p.setParty(party);
				//Attach boxes
				PokemonBox[] boxes = new PokemonBox[9];
				ResultSet boxInfo;
				for(int i = 0; i < 9; i++) {
					/*
				 	* -1 is stored in the database if no box exists
				 	*/
					if(pokemons.getInt("box" + i) != -1) {
						boxInfo = m_database.query("SELECT * FROM pn_box WHERE id='" + pokemons.getInt("box" + i) + "'");
						boxInfo.first();
						for(int j = 0; j < 30; j++) {
							/*
						 	* -1 stored in the database if no pokemon exists
						 	*/
							boxes[i] = new PokemonBox();
							boxes[i].setDatabaseId(pokemons.getInt("box" + i));
							if(boxInfo.getInt("pokemon" + j) != -1) {
								boxes[i].setPokemon(j, getPokemonObject(m_database.query("SELECT * FROM pn_pokemon WHERE id='" + boxInfo.getInt("pokemon" + j) + "'")));
							} else {
								boxes[i].setPokemon(j, null);
							}
						}
					}
				}
				p.setBoxes(boxes);
				//Attach bag
				p.setBag(getBagObject(m_database.query("SELECT * FROM pn_bag WHERE id='" + result.getInt("bag") + "'")));
				//Attach badges
				p.generateBadges(result.getString("badges"));
				return p;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns a Pokemon object based on a set of data
	 * @param data
	 * @return
	 */
	private Pokemon getPokemonObject(ResultSet data) {
		if(data != null) {
			try {
				data.first();
				/*
				 * First generate the Pokemons moves
				 */
				MoveListEntry [] moves = new MoveListEntry[4];
				moves[0] = (data.getString("move0") != null && !data.getString("move0").equalsIgnoreCase("null") ?
						GameServer.getServiceManager().getDataService().getMovesList().getMove(data.getString("move0")) :
							null);
				moves[1] = (data.getString("move1") != null && !data.getString("move1").equalsIgnoreCase("null") ?
						GameServer.getServiceManager().getDataService().getMovesList().getMove(data.getString("move1")) :
							null);
				moves[2] = (data.getString("move2") != null && !data.getString("move2").equalsIgnoreCase("null") ?
						GameServer.getServiceManager().getDataService().getMovesList().getMove(data.getString("move2")) :
							null);
				moves[3] = (data.getString("move3") != null && !data.getString("move3").equalsIgnoreCase("null") ?
						GameServer.getServiceManager().getDataService().getMovesList().getMove(data.getString("move3")) :
							null);
				/*
				 * Create the new Pokemon
				 */
				Pokemon p = new Pokemon(
						GameServer.getServiceManager().getDataService().getBattleMechanics(),
						GameServer.getServiceManager().getDataService().getSpeciesDatabase().getSpecies(
								GameServer.getServiceManager().getDataService().getSpeciesDatabase().getPokemonByName(data.getString("speciesName")))
						,
						PokemonNature.getNatureByName(data.getString("nature")),
						data.getString("abilityName"),
						data.getString("itemName"),
						data.getInt("gender"),
						data.getInt("level"),
						new int[] { 
							data.getInt("ivHP"),
							data.getInt("ivATK"),
							data.getInt("ivDEF"),
							data.getInt("ivSPD"),
							data.getInt("ivSPATK"),
							data.getInt("ivSPDEF")},
						new int[] { 
							data.getInt("evHP"),
							data.getInt("evATK"),
							data.getInt("evDEF"),
							data.getInt("evSPD"),
							data.getInt("evSPATK"),
							data.getInt("evSPDEF")},
						moves,
						new int[] {
							data.getInt("ppUp0"),
							data.getInt("ppUp1"),
							data.getInt("ppUp2"),
							data.getInt("ppUp3")
						});
				p.reinitialise();
				/*
				 * Set exp, nickname, isShiny and exp gain type
				 */
				p.setBaseExp(data.getInt("baseExp"));
				p.setExp(Double.parseDouble(data.getString("exp")));
				p.setName(data.getString("name"));
				p.setHappiness(data.getInt("happiness"));
				p.setShiny(Boolean.parseBoolean(data.getString("isShiny")));
				p.setExpType(ExpTypes.valueOf(data.getString("expType")));
				p.setOriginalTrainer(data.getString("originalTrainerName"));
				p.setDatabaseID(data.getInt("id"));
				p.setIsFainted(Boolean.parseBoolean(data.getString("isFainted")));
				/*
				 * Sets the stats
				 */
				p.setRawStat(0, data.getInt("hp"));
				p.setRawStat(1, data.getInt("atk"));
				p.setRawStat(2, data.getInt("def"));
				p.setRawStat(3, data.getInt("speed"));
				p.setRawStat(4, data.getInt("spATK"));
				p.setRawStat(5, data.getInt("spDEF"));
				/*
				 * Sets the pp information
				 */
				p.setPp(0, data.getInt("pp0"));
				p.setPp(1, data.getInt("pp1"));
				p.setPp(2, data.getInt("pp2"));
				p.setPp(3, data.getInt("pp3"));
				p.setMaxPP(0, data.getInt("maxpp0"));
				p.setMaxPP(0, data.getInt("maxpp1"));
				p.setMaxPP(0, data.getInt("maxpp2"));
				p.setMaxPP(0, data.getInt("maxpp3"));
				p.setPpUp(0, data.getInt("ppUp0"));
				p.setPpUp(0, data.getInt("ppUp1"));
				p.setPpUp(0, data.getInt("ppUp2"));
				p.setPpUp(0, data.getInt("ppUp3"));
				return p;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Returns a bag object
	 * @param data
	 * @return
	 */
	private Bag getBagObject(ResultSet data) {
		try {
			data.first();
			Bag b = new Bag();
			b.setDatabaseId(data.getInt("id"));
			for(int i = 0; i < 20; i++) {
				if(data.getInt("item" + i) > 0)
					b.addItem(data.getInt("item" + i), data.getInt("quantity" + i));
			}
			return b;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Saves a player object to the database (Updates an existing player)
	 * TODO Make it save even if first save attempt fails
	 * @param p
	 * @return
	 */
	private void savePlayer(PlayerChar p) {
		try {
			/*
			 * First, check if they have logged in somewhere else.
			 * Don't want to change them if the time it took us to perform a mod
			 * action allowed them time to login elsewhere
			 */
			ResultSet data = m_database.query("SELECT * FROM pn_members WHERE id='" + p.getId() +  "'");
			data.first();
			if(data.getLong("lastLoginTime") == p.getLastLoginTime()) {
				/*
				 * Update the player row
				 */
				String badges = "";
				for(int i = 0; i < 42; i++) {
					if(p.getBadges()[i] == 1)
						badges = badges + "1";
					else
						badges = badges + "0";
				}
				m_database.query("UPDATE pn_members SET " +
						"sprite='" + p.getSprite() + "', " +
						"money='" + p.getMoney() + "', " +
						"npcMul='" + (String.valueOf(p.getNpcMultiplier()).length() > 20 ?
								String.valueOf(p.getNpcMultiplier()).substring(0, 20) :
									String.valueOf(p.getNpcMultiplier())) + "', " +
						"skHerb='" + p.getHerbalismExp() + "', " +
						"skCraft='" + p.getCraftingExp() + "', " +
						"skFish='" + p.getFishingExp() + "', " +
						"skTrain='" + p.getTrainingExp() + "', " +
						"skCoord='" + p.getCoordinatingExp() + "', " +
						"skBreed='" + p.getBreedingExp() + "', " +
						"x='" + p.getX() + "', " +
						"y='" + p.getY() + "', " +
						"mapX='" + p.getMapX() + "', " +
						"mapY='" + p.getMapY() + "', " +
						"healX='" + p.getHealX() + "', " +
						"healY='" + p.getHealY() + "', " +
						"healMapX='" + p.getHealMapX() + "', " +
						"healMapY='" + p.getHealMapY() + "', " +
						"isSurfing='" + String.valueOf(p.isSurfing()) + "', " +
						"badges='" + badges + "' " +
						"WHERE username='" + p.getName() + "' AND id='" + p.getId() + "'");
				/*
				 * Second, update the party
				 */
				//Save all the Pokemon
				for(int i = 0; i < 6; i++) {
					if(p.getParty() != null && p.getParty()[i] != null) {
						if(p.getParty()[i].getDatabaseID() == -1) {
							//This is a new Pokemon, add it to the database
							if(!saveNewPokemon(p.getParty()[i], m_database))
								return;
						} else {
							//Old Pokemon, just update
							if(!savePokemon(p.getParty()[i]))
								return;
						}
					}
				}
				//Save all the Pokemon id's in the player's party
				if(p.getParty() != null) {
					m_database.query("UPDATE pn_party SET " +
							"pokemon0='" + (p.getParty()[0] != null ? p.getParty()[0].getDatabaseID() : -1) + "', " +
							"pokemon1='" + (p.getParty()[1] != null ? p.getParty()[1].getDatabaseID() : -1) + "', " +
							"pokemon2='" + (p.getParty()[2] != null ? p.getParty()[2].getDatabaseID() : -1) + "', " +
							"pokemon3='" + (p.getParty()[3] != null ? p.getParty()[3].getDatabaseID() : -1) + "', " +
							"pokemon4='" + (p.getParty()[4] != null ? p.getParty()[4].getDatabaseID() : -1) + "', " +
							"pokemon5='" + (p.getParty()[5] != null ? p.getParty()[5].getDatabaseID() : -1) + "' " +
							"WHERE member='" + p.getId() + "'");
				} else
					return;
				/*
				 * Save the player's bag
				 */
				if(p.getBag() == null || !saveBag(p.getBag()))
					return;
				/*
				 * Finally, update all the boxes
				 */
				if(p.getBoxes() != null) {
					for(int i = 0; i < 9; i++) {
						if(p.getBoxes()[i] != null) {
							if(p.getBoxes()[i].getDatabaseId() == -1) {
								//New box
								m_database.query("INSERT INTO pn_box(member, pokemon0, pokemon1, pokemon2, " +
										"pokemon3, pokemon4, pokemon5, pokemon 6, pokemon7, pokemon8, pokemon9, " +
										"pokemon10, pokemon11, pokemon12, pokemon13, pokemon14, pokemon15, pokemon16, " +
										"pokemon17, pokemon18, pokemon19, pokemon20, pokemon21, pokemon22, pokemon23, " +
										"pokemon24, pokemon25, pokemon26, pokemon27, pokemon28, pokemon29, pokemon30) " +
										"VALUES ('" + p.getId() + "'," +
										"'-1','-1','-1','-1','-1'," +
										"'-1','-1','-1','-1','-1'," +
										"'-1','-1','-1','-1','-1'," +
										"'-1','-1','-1','-1','-1'," +
										"'-1','-1','-1','-1','-1'," +
										"'-1','-1','-1','-1','-1')");
								ResultSet result = m_database.query("SELECT * FROM pn_box WHERE member='" + p.getId() + "'");
								result.last();
								p.getBoxes()[i].setDatabaseId(result.getInt("id"));
							}
							//Save all pokemon first
							for(int j = 0; j < p.getBoxes()[i].getPokemon().length; j++) {
								if(p.getBoxes()[i].getPokemon(j).getId() == -1) {
									if(!saveNewPokemon(p.getBoxes()[i].getPokemon(j), m_database))
										return;
								} else {
									if(!savePokemon(p.getBoxes()[i].getPokemon(j)))
										return;
								}
							}
							//Now save all references to the box
							for(int j = 0; j < p.getBoxes()[i].getPokemon().length; j++) {
								m_database.query("UPDATE pn_box SET pokemon" + j + "='" +  p.getBoxes()[i].getPokemon(j).getDatabaseID() + "' " +
										"WHERE id='" + p.getBoxes()[i].getDatabaseId() + "'");
							}
						}
					}
				}
				//Dispose of the player object
				p.dispose();
				return;
			} else
				return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Saves a pokemon to the database that didn't exist in it before
	 * @param p
	 */
	private boolean saveNewPokemon(Pokemon p, MySqlManager db) {
		try {
			/*
			 * Insert the Pokemon into the database
			 */
			db.query("INSERT INTO pn_pokemon" +
					"(name, speciesName, exp, baseExp, expType, isFainted, level, happiness, " +
					"gender, nature, abilityName, itemName, isShiny, originalTrainerName, date)" +
					"VALUES (" +
					"'" + p.getName() +"', " +
					"'" + p.getSpeciesName() +"', " +
					"'" + String.valueOf(p.getExp()) +"', " +
					"'" + p.getBaseExp() +"', " +
					"'" + p.getExpType().name() +"', " +
					"'" + String.valueOf(p.isFainted()) +"', " +
					"'" + p.getLevel() +"', " +
					"'" + p.getHappiness() +"', " +
					"'" + p.getGender() +"', " +
					"'" + p.getNature().getName() +"', " +
					"'" + p.getAbilityName() +"', " +
					"'" + p.getItemName() +"', " +
					"'" + String.valueOf(p.isShiny()) +"', " +
					"'" + p.getOriginalTrainer() + "', " +
					"'" + p.getDateCaught() + "')");
			/*
			 * Get the pokemon's database id and attach it to the pokemon.
			 * This needs to be done so it can be attached to the player in the database later.
			 */
			ResultSet result = db.query("SELECT * FROM pn_pokemon WHERE originalTrainerName='"  + p.getOriginalTrainer() + 
					"' AND date='" + p.getDateCaught() + "'");
			result.first();
			p.setDatabaseID(result.getInt("id"));
			db.query("UPDATE pn_pokemon SET move0='" + p.getMove(0).getName() +
					"', move1='" + (p.getMove(1) == null ? "null" : p.getMove(1).getName()) +
					"', move2='" + (p.getMove(2) == null ? "null" : p.getMove(2).getName()) +
					"', move3='" + (p.getMove(3) == null ? "null" : p.getMove(3).getName()) +
					"', hp='" + p.getHealth() +
					"', atk='" + p.getStat(1) +
					"', def='" + p.getStat(2) +
					"', speed='" + p.getStat(3) +
					"', spATK='" + p.getStat(4) +
					"', spDEF='" + p.getStat(5) +
					"', evHP='" + p.getEv(0) +
					"', evATK='" + p.getEv(1) +
					"', evDEF='" + p.getEv(2) +
					"', evSPD='" + p.getEv(3) +
					"', evSPATK='" + p.getEv(4) +
					"', evSPDEF='" + p.getEv(5) +
					"' WHERE id='" + p.getDatabaseID() + "'");
			db.query("UPDATE pn_pokemon SET ivHP='" + p.getIv(0) +
					"', ivATK='" + p.getIv(1) +
					"', ivDEF='" + p.getIv(2) +
					"', ivSPD='" + p.getIv(3) +
					"', ivSPATK='" + p.getIv(4) +
					"', ivSPDEF='" + p.getIv(5) +
					"', pp0='" + p.getPp(0) +
					"', pp1='" + p.getPp(1) +
					"', pp2='" + p.getPp(2) +
					"', pp3='" + p.getPp(3) +
					"', maxpp0='" + p.getMaxPp(0) +
					"', maxpp1='" + p.getMaxPp(1) +
					"', maxpp2='" + p.getMaxPp(2) +
					"', maxpp3='" + p.getMaxPp(3) +
					"', ppUp0='" + p.getPpUpCount(0) +
					"', ppUp1='" + p.getPpUpCount(1) +
					"', ppUp2='" + p.getPpUpCount(2) +
					"', ppUp3='" + p.getPpUpCount(3) +
					"' WHERE id='" + p.getDatabaseID() + "'");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Updates a pokemon in the database
	 * @param p
	 */
	private boolean savePokemon(Pokemon p) {
		try {
			/*
			 * Update the pokemon in the database
			 */
			m_database.query("UPDATE pn_pokemon SET " +
					"name='" + p.getName() +"', " +
					"speciesName='" + p.getSpeciesName() +"', " +
					"exp='" + String.valueOf(p.getExp()) +"', " +
					"baseExp='" + p.getBaseExp() +"', " +
					"expType='" + p.getExpType().name() +"', " +
					"isFainted='" + String.valueOf(p.isFainted()) +"', " +
					"level='" + p.getLevel() +"', " +
					"happiness='" + p.getHappiness() +"', " +
					"gender='" + p.getGender() +"', " +
					"nature='" + p.getNature().getName() +"', " +
					"abilityName='" + p.getAbilityName() +"', " +
					"itemName='" + p.getItemName() +"', " +
					"isShiny='" + String.valueOf(p.isShiny()) +"' " +
					"WHERE id='" + p.getDatabaseID() + "'");
			m_database.query("UPDATE pn_pokemon SET move0='" + p.getMove(0).getName() +
					"', move1='" + (p.getMove(1) == null ? "null" : p.getMove(1).getName()) +
					"', move2='" + (p.getMove(2) == null ? "null" : p.getMove(2).getName()) +
					"', move3='" + (p.getMove(3) == null ? "null" : p.getMove(3).getName()) +
					"', hp='" + p.getHealth() +
					"', atk='" + p.getStat(1) +
					"', def='" + p.getStat(2) +
					"', speed='" + p.getStat(3) +
					"', spATK='" + p.getStat(4) +
					"', spDEF='" + p.getStat(5) +
					"', evHP='" + p.getEv(0) +
					"', evATK='" + p.getEv(1) +
					"', evDEF='" + p.getEv(2) +
					"', evSPD='" + p.getEv(3) +
					"', evSPATK='" + p.getEv(4) +
					"', evSPDEF='" + p.getEv(5) +
					"' WHERE id='" + p.getDatabaseID() + "'");
			m_database.query("UPDATE pn_pokemon SET ivHP='" + p.getIv(0) +
					"', ivATK='" + p.getIv(1) +
					"', ivDEF='" + p.getIv(2) +
					"', ivSPD='" + p.getIv(3) +
					"', ivSPATK='" + p.getIv(4) +
					"', ivSPDEF='" + p.getIv(5) +
					"', pp0='" + p.getPp(0) +
					"', pp1='" + p.getPp(1) +
					"', pp2='" + p.getPp(2) +
					"', pp3='" + p.getPp(3) +
					"', maxpp0='" + p.getMaxPp(0) +
					"', maxpp1='" + p.getMaxPp(1) +
					"', maxpp2='" + p.getMaxPp(2) +
					"', maxpp3='" + p.getMaxPp(3) +
					"', ppUp0='" + p.getPpUpCount(0) +
					"', ppUp1='" + p.getPpUpCount(1) +
					"', ppUp2='" + p.getPpUpCount(2) +
					"', ppUp3='" + p.getPpUpCount(3) +
					"' WHERE id='" + p.getDatabaseID() + "'");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Saves a bag to the database.
	 * @param b
	 * @return
	 */
	private boolean saveBag(Bag b) {
		try {
			for(int i = 0; i < b.getItems().length; i++) {
				if(b.getItems()[i] != null) {
					/*
					 * NOTE: Items are stored as values 1 - 999
					 */
					m_database.query("UPDATE pn_bag SET item" + i + "='" 
							+ (b.getItems()[i].getItemNumber() > 0 ? b.getItems()[i].getItemNumber() : 0) +
							", quantity" + i + "='" +
							(b.getItems()[i].getQuantity() > 0 ? b.getItems()[i].getQuantity() : 0) +
							"' WHERE id='" + b.getDatabaseId() + "'");
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}