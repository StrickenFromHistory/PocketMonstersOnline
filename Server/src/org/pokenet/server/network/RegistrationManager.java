package org.pokenet.server.network;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.common.IoSession;
import org.pokenet.server.GameServer;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.mechanics.PokemonNature;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;

/**
 * Handles registrations
 * @author shadowkanji
 *
 */
public class RegistrationManager implements Runnable {
	private Queue<IoSession> m_queue;
	private Thread m_thread;
	private boolean m_isRunning;
	private MySqlManager m_database;
	
	/**
	 * Constructor
	 */
	public RegistrationManager() {
		m_database = new MySqlManager();
		m_thread = new Thread(this);
		m_queue = new ConcurrentLinkedQueue<IoSession>();
	}
	
	/**
	 * Queues a registration
	 * @param session
	 * @param packet
	 */
	public void queueRegistration(IoSession session, String packet) {
		if(!m_queue.contains(session)) {
			session.setAttribute("reg", packet);
			m_queue.add(session);
		}
		session.suspendRead();
		session.suspendWrite();
	}
	
	/**
	 * Registers a new player
	 * @param session
	 */
	public void register(IoSession session) throws Exception {
		String [] info = ((String) session.getAttribute("reg")).split(",");
		if(m_database.connect(GameServer.getDatabaseHost(), GameServer.getDatabaseUsername(), GameServer.getDatabasePassword())) {
			m_database.selectDatabase(GameServer.getDatabaseName());
			int s = Integer.parseInt(info[4]);
			/*
			 * Check if the user exists
			 */
			ResultSet data = m_database.query("SELECT * FROM pn_members WHERE username='" + info[0] + "'");
			data.first();
			try {				
				if(data != null && data.getString("username") != null && data.getString("username").equalsIgnoreCase(info[0])) {
					session.resumeRead();
					session.resumeWrite();
					session.write("r2");
					return;
				}
			} catch (Exception e) {}
			/*
			 * Check if user is not trying to register their starter as a non-starter Pokemon
			 */
			if(!(s == 1 || s == 4 || s == 7 || s == 152 || s == 155 || s == 158 || s == 252 || s == 255 || s == 258
					|| s == 387 || s == 390 || s == 393)) {
				session.write("r4");
				return;
			}
			/*
			 * Create the player in the database
			 */
			String badges = "";
			for(int i = 0; i < 50; i++)
				badges = badges + "0";
			m_database.query("INSERT INTO pn_members (username, password, dob, email, lastLoginTime, lastLoginServer, " +
					"sprite, money, npcMul, skHerb, skCraft, skFish, skTrain, skCoord, skBreed, " +
					"x, y, mapX, mapY, badges, healX, healY, healMapX, healMapY, isSurfing, adminLevel) VALUE " +
					"('" + info[0] + "', '" + info[1] + "', '" + info[3] + "', '" + info[2] + "', " +
							"'0', 'null', '" + info[5] + "', '0', '1.5', '0', '0', '0', '0', '0', '0', '256', '248', " +
									"'0', '0', '" + badges + "', '256', '248', '-50', '-50', 'false', '0')");
			/*
			 * Retrieve the player's unique id
			 */
			data = m_database.query("SELECT * FROM pn_members WHERE username='" + info[0] + "'");
			data.first();
			int playerId = data.getInt("id");
			/*
			 * Create the player's bag
			 */
			String bagBuilder = "";
			for(int i = 0; i < 39; i++)
				bagBuilder = bagBuilder + "'-1', ";
			m_database.query("INSERT INTO pn_bag (member) VALUE ('" + playerId + "')");
			data = m_database.query("SELECT * FROM pn_bag WHERE member='" + playerId + "'");
			data.first();
			m_database.query("UPDATE pn_members SET bag='" + data.getInt("id") + "' WHERE id='" + playerId + "'");
			/*
			 * Create the players party
			 */
			Pokemon p = this.createStarter(s);
			p.setOriginalTrainer(info[0]);
			p.setDateCaught(new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(new Date()));
			this.saveNewPokemon(p, m_database);
			
			m_database.query("INSERT INTO pn_party (member, pokemon0, pokemon1, pokemon2, pokemon3, pokemon4, pokemon5) VALUES ('" +
					+ playerId + "','" + p.getDatabaseID() + "','-1','-1','-1','-1','-1')");
			data = m_database.query("SELECT * FROM pn_party WHERE member='" + playerId + "'");
			data.first();
			/*
			 * Create the players pokemon storage
			 */
			m_database.query("INSERT INTO pn_mypokes (member, party, box0, box1, box2, box3, box4, box5, box6, box7, box8) VALUES ('" +
					+ playerId + "','" + data.getInt("id") + "','-1','-1','-1','-1','-1','-1','-1','-1','-1')");
			data = m_database.query("SELECT * FROM pn_mypokes WHERE member='" + playerId + "'");
			data.first();
			/*
			 * Attach pokemon to the player
			 */
			m_database.query("UPDATE pn_members SET pokemons='" + data.getInt("id") + "' WHERE id='" + playerId + "'");
			/*
			 * Finish
			 */
			m_database.close();
			
			session.resumeRead();
			session.resumeWrite();
			session.write("rs");
		} else {
			session.resumeRead();
			session.resumeWrite();
			session.write("r1");
		}
	}

	/**
	 * Called by m_thread.start()
	 */
	public void run() {
		IoSession session;
		while(m_isRunning) {
			synchronized(m_queue) {
				if(m_queue.peek() != null) {
					session = m_queue.poll();
					try {
						this.register(session);
					} catch (Exception e) {
						e.printStackTrace();
						session.resumeRead();
						session.resumeWrite();
						session.write("r3");
					}
				}
				try {
					Thread.sleep(250);
				} catch (Exception e) {}
			}
		}
	}
	
	/**
	 * Start the registration manager
	 */
	public void start() {
		m_isRunning = true;
		m_thread.start();
	}

	/**
	 * Stop the registration manager
	 */
	public void stop() {
		m_isRunning = false;
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
	 * Creates a starter Pokemon
	 * @param speciesIndex
	 * @return
	 * @throws Exception
	 */
	private Pokemon createStarter(int speciesIndex) throws Exception {
		/*
		 * Get the Pokemon species. Use getPokemonByName as once the 
		 * species array gets to gen 3 it loses the pokedex numbering
		 */
		PokemonSpecies species = null;
		switch(speciesIndex) {
		case 1:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Bulbasaur"));
			break;
		case 4:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Charmander"));
			break;
		case 7:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Squirtle"));
			break;
		case 152:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Chikorita"));
			break;
		case 155:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Cyndaquil"));
			break;
		case 158:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Totodile"));
			break;
		case 252:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Treecko"));
			break;
		case 255:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Torchic"));
			break;
		case 258:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Mudkip"));
			break;
		case 387:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Turtwig"));
			break;
		case 390:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Chimchar"));
			break;
		case 393:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Piplup"));
			break;
		default:
			species = PokemonSpecies.getDefaultData().getSpecies
			(PokemonSpecies.getDefaultData().getPokemonByName("Mudkip"));
		}
        
        ArrayList<MoveListEntry> possibleMoves = new ArrayList<MoveListEntry>();
        MoveListEntry[] moves = new MoveListEntry[4];
        Random random = DataService.getBattleMechanics().getRandom();

        for (int i = 0; i < DataService.getPOLRDatabase().getPokemonData(speciesIndex)
                        .getStarterMoves().size(); i++) {
                possibleMoves.add(DataService.getMovesList().getMove(
                		DataService.getPOLRDatabase().getPokemonData(
                                speciesIndex).getStarterMoves().get(i)));
        }
        for (int i = 1; i <= 5; i++) {
                if (DataService.getPOLRDatabase().getPokemonData(speciesIndex).getMoves().containsKey(i)) {
                        possibleMoves.add(DataService.getMovesList().getMove(
                        		DataService.getPOLRDatabase().getPokemonData(
                                        speciesIndex).getMoves().get(i)));
                }
        }
        if (possibleMoves.size() <= 4) {
                for (int i = 0; i < possibleMoves.size(); i++) {
                        moves[i] = possibleMoves.get(i);
                }
        } else {
                for (int i = 0; i < moves.length; i++) {
                        if (possibleMoves.size() == 0)
                                moves[i] = null;
                        moves[i] = possibleMoves.get(random.nextInt(possibleMoves
                                        .size()));
                        possibleMoves.remove(moves[i]);
                }
        }
        String [] abilities = PokemonSpecies.getDefaultData().getPossibleAbilities(species.getName());
        Pokemon starter = new Pokemon(
        		DataService.getBattleMechanics(),
                        species,
                        PokemonNature.getNature(random.nextInt(PokemonNature.getNatureNames().length)),
                                        abilities[random.nextInt(abilities.length)],
                        null, (random.nextInt(100) > 87 ? Pokemon.GENDER_FEMALE
                                        : Pokemon.GENDER_MALE), 5, new int[] {
                                        random.nextInt(32), // IVs
                                        random.nextInt(32), random.nextInt(32),
                                        random.nextInt(32), random.nextInt(32),
                                        random.nextInt(32) }, new int[] { 0, 0, 0, 0, 0, 0 }, //EVs
                        moves, new int[] { 0, 0, 0, 0 });
        starter.setExpType(DataService.getPOLRDatabase().getPokemonData(speciesIndex)
                        .getGrowthRate());
        starter.setBaseExp(DataService.getPOLRDatabase().getPokemonData(speciesIndex).getBaseEXP());
        starter.setExp(DataService.getBattleMechanics().getExpForLevel(starter, 5));
        starter.setHappiness(DataService.getPOLRDatabase().getPokemonData(speciesIndex).getHappiness());
        starter.setName(starter.getSpeciesName());
        return starter;
	}
}
