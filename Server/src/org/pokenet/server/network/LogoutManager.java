package org.pokenet.server.network;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.pokenet.server.GameServer;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.battle.Pokemon;

/**
 * Handles logging players out
 * @author shadowkanji
 *
 */
public class LogoutManager implements Runnable {
	private Queue<PlayerChar> m_logoutQueue;
	private Thread m_thread;
	private boolean m_isRunning;
	private MySqlManager m_database;
	
	/**
	 * Default constructor
	 */
	public LogoutManager() {
		m_database = new MySqlManager();
		m_logoutQueue = new ConcurrentLinkedQueue<PlayerChar>();
		m_thread = new Thread(this);
	}
	
	/**
	 * Attempts to logout a player by saving their data. Returns true on success
	 * @param player
	 */
	private boolean attemptLogout(PlayerChar player) {
		m_database.connect(GameServer.getDatabaseHost(), GameServer.getDatabaseUsername(), GameServer.getDatabasePassword());
		//TODO: Store all player information
		if(!savePlayer(player))
			return false;
		//Finally, store that the player is logged out and close connection
		m_database.query("UPDATE pn_members SET lastLoginServer='null' WHERE id='" + player.getId() + "'");
		m_database.close();
		//Close the session fully if its not closed already
		if(player.getSession() != null && player.getSession().isConnected())
			player.getSession().close();
		return true;
	}
	
	/**
	 * Returns true if a user is being logged out
	 * This is used during login. If a player is in the logout queue,
	 * the player must wait to be logged out before being logged back in again.
	 * @param username
	 * @return
	 */
	public boolean containsPlayer(String username) {
		Iterator<PlayerChar> it = m_logoutQueue.iterator();
		PlayerChar p;
		while(it.hasNext()) {
			p = it.next();
			if(p.getName().equalsIgnoreCase(username))
				return true;
		}
		return false;
	}
	
	/**
	 * Queues a player to be logged out
	 * @param player
	 */
	public void queuePlayer(PlayerChar player) {
		if(!m_logoutQueue.contains(player))
			m_logoutQueue.add(player);
	}

	/**
	 * Called by m_thread.start()
	 */
	public void run() {
		PlayerChar p;
		while(m_isRunning) {
			synchronized(m_logoutQueue) {
				if(m_logoutQueue.peek() != null) {
					p = m_logoutQueue.poll();
					if(!attemptLogout(p)) {
						m_logoutQueue.add(p);
					}
				}
			}
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Start this logout manager
	 */
	public void start() {
		m_isRunning = true;
		m_thread.start();
	}
	
	/**
	 * Stop this logout manager
	 */
	public void stop() {
		//Stop the thread
		m_isRunning = false;
		//Save all players
		PlayerChar p;
		while(m_logoutQueue.peek() != null) {
			p = m_logoutQueue.poll();
			if(!attemptLogout(p)) {
				m_logoutQueue.add(p);
			}
		}
		System.out.println("INFO: All player data saved successfully.");
	}
	
	/**
	 * Saves a player object to the database
	 * @param p
	 * @return
	 */
	private boolean savePlayer(PlayerChar p) {
		try {
			/*
			 * First, check if they have logged in somewhere else.
			 * This is useful for when as server loses its internet connection
			 */
			ResultSet data = m_database.query("SELECT * FROM pn_members WHERE id='" + p.getId() +  "'");
			data.first();
			if(data.getLong("lastLoginTime") == p.getLastLoginTime()) {
				/*
				 * Update the player row
				 */
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
						"mapY='" + p.getMapY() + "' " +
						"WHERE username='" + p.getName() + "' AND id='" + p.getId() + "'");
				/*
				 * Second, update the party
				 */
				//Save all the Pokemon
				for(int i = 0; i < 6; i++) {
					if(p.getParty()[i] != null) {
						if(p.getParty()[i].getDatabaseID() == -1) {
							//This is a new Pokemon, add it to the database
							if(!saveNewPokemon(p.getParty()[i]))
								return false;
						} else {
							//Old Pokemon, just update
							if(!savePokemon(p.getParty()[i]))
								return false;
						}
					}
				}
				//Save all the Pokemon id's in the player's party
				m_database.query("UPDATE pn_party SET " +
						"pokemon0='" + (p.getParty()[0] != null ? p.getParty()[0].getDatabaseID() : -1) + "', " +
						"pokemon1='" + (p.getParty()[1] != null ? p.getParty()[1].getDatabaseID() : -1) + "', " +
						"pokemon2='" + (p.getParty()[2] != null ? p.getParty()[2].getDatabaseID() : -1) + "', " +
						"pokemon3='" + (p.getParty()[3] != null ? p.getParty()[3].getDatabaseID() : -1) + "', " +
						"pokemon4='" + (p.getParty()[4] != null ? p.getParty()[4].getDatabaseID() : -1) + "', " +
						"pokemon5='" + (p.getParty()[5] != null ? p.getParty()[5].getDatabaseID() : -1) + "', " +
						"WHERE id='" + p.getDatabasePokemon().getInt("party") + "' AND member='" + p.getId() + "'");
				/*
				 * Finally, update all the boxes
				 */
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
								if(!saveNewPokemon(p.getBoxes()[i].getPokemon(j)))
									return false;
							} else {
								if(!savePokemon(p.getBoxes()[i].getPokemon(j)))
									return false;
							}
						}
						//Now save all references to the box
						for(int j = 0; j < p.getBoxes()[i].getPokemon().length; j++) {
							m_database.query("UPDATE pn_box SET pokemon" + j + "='" +  p.getBoxes()[i].getPokemon(j).getDatabaseID() + "'");
						}
					}
				}
				return true;
			} else
				return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Saves a pokemon to the database that didn't exist in it before
	 * @param p
	 */
	private boolean saveNewPokemon(Pokemon p) {
		try {
			/*
			 * Insert the Pokemon into the database
			 */
			m_database.query("INSERT INTO pn_pokemon" +
					"(name, speciesName, exp, baseExp, expType, isFainted, level, happiness, " +
					"gender, nature, abilityName, itemName, isShiny, originalTrainerName, " +
					"move0, move1, move2, move3, hp, atk, def, speed, spATK, spDEF, " +
					"evHP, evATK, evDEF, evSPD, evSPATK, evSPDEF, ivHP, ivATK, ivDEF, ivSPD, ivSPATK, ivSPDEF, " +
					"pp0, pp1, pp2, pp3, maxpp0, maxpp1, maxpp2, maxpp3, maxpp4, ppUp0, ppUp1, ppUp2, ppUp3, date) " +
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
					"'" + p.getTrainerName() + "', " +
					"'" + p.getMoveName(0) +"', " +
					"'" + p.getMoveName(1) +"', " +
					"'" + p.getMoveName(2) +"', " +
					"'" + p.getMoveName(3) +"', " +
					"'" + p.getHealth() +"', " +
					"'" + p.getStat(1) +"', " +
					"'" + p.getStat(2) +"', " +
					"'" + p.getStat(3) +"', " +
					"'" + p.getStat(4) +"', " +
					"'" + p.getStat(5) +"', " +
					"'" + p.getEv(0) +"', " +
					"'" + p.getEv(1) +"', " +
					"'" + p.getEv(2) +"', " +
					"'" + p.getEv(3) +"', " +
					"'" + p.getEv(4) +"', " +
					"'" + p.getEv(5) +"', " +
					"'" + p.getIv(0) +"', " +
					"'" + p.getIv(1) +"', " +
					"'" + p.getIv(2) +"', " +
					"'" + p.getIv(3) +"', " +
					"'" + p.getIv(4) +"', " +
					"'" + p.getIv(5) +"', " +
					"'" + p.getPp(0) +"', " +
					"'" + p.getPp(1) +"', " +
					"'" + p.getPp(2) +"', " +
					"'" + p.getPp(3) +"', " +
					"'" + p.getMaxPp(0) +"', " +
					"'" + p.getMaxPp(1) +"', " +
					"'" + p.getMaxPp(2) +"', " +
					"'" + p.getMaxPp(3) +"', " +
					"'" + p.getPpUpCount(0) +"', " +
					"'" + p.getPpUpCount(1) +"', " +
					"'" + p.getPpUpCount(2) +"', " +
					"'" + p.getPpUpCount(3) +"', " +
					"'" + p.getDateCaught() + "')");
			/*
			 * Get the pokemon's database id and attach it to the pokemon.
			 * This needs to be done so it can be attached to the player in the database later.
			 */
			ResultSet result = m_database.query("SELECT * FROM pn_pokemon WHERE originalTrainerName='"  + p.getOriginalTrainer() + 
					"' AND date='" + p.getDateCaught() + "'");
			result.first();
			p.setDatabaseID(result.getInt("id"));
			return true;
		} catch (Exception e) {
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
					"isShiny='" + String.valueOf(p.isShiny()) +"', " +
					"hp='" + p.getHealth() +"', " +
					"atk='" + p.getStat(1) +"', " +
					"def='" + p.getStat(2) +"', " +
					"speed='" + p.getStat(3) +"', " +
					"spATK='" + p.getStat(4) +"', " +
					"spDEF='" + p.getStat(5) +"', " +
					"evHP='" + p.getEv(0) +"', " +
					"evATK='" + p.getEv(1) +"', " +
					"evDEF='" + p.getEv(2) +"', " +
					"evSPD='" + p.getEv(3) +"', " +
					"evSPATK='" + p.getEv(4) +"', " +
					"evSPDEF='" + p.getEv(5) +"', " +
					"ivHP='" + p.getIv(0) +"', " +
					"ivATK='" + p.getIv(1) +"', " +
					"ivDEF='" + p.getIv(2) +"', " +
					"ivSPD='" + p.getIv(3) +"', " +
					"ivSPATK='" + p.getIv(4) +"', " +
					"ivSPDEF='" + p.getIv(5) +"', " +
					"pp0='" + p.getPp(0) +"', " +
					"pp1='" + p.getPp(1) +"', " +
					"pp2='" + p.getPp(2) +"', " +
					"pp3='" + p.getPp(3) +"', " +
					"maxpp0='" + p.getMaxPp(0) +"', " +
					"maxpp1='" + p.getMaxPp(1) +"', " +
					"maxpp2='" + p.getMaxPp(2) +"', " +
					"maxpp3='" + p.getMaxPp(3) +"', " +
					"ppUp0='" + p.getPpUpCount(0) +"', " +
					"ppUp1='" + p.getPpUpCount(1) +"', " +
					"ppUp2='" + p.getPpUpCount(2) +"', " +
					"ppUp3='" + p.getPpUpCount(3) +"', " +
					"move0='" + p.getMoveName(0) +"', " +
					"move1='" + p.getMoveName(1) +"', " +
					"move2='" + p.getMoveName(2) +"', " +
					"move3='" + p.getMoveName(3) +"', " +
					"WHERE id='" + p.getDatabaseID() + "'");
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
