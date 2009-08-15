package org.pokenet.server.battle;

import java.io.File;
import java.util.ArrayList;

import org.pokenet.server.backend.item.DropDatabase;
import org.pokenet.server.battle.mechanics.JewelMechanics;
import org.pokenet.server.battle.mechanics.moves.MoveList;
import org.pokenet.server.battle.mechanics.moves.MoveSetData;
import org.pokenet.server.battle.mechanics.polr.POLRDatabase;
import org.pokenet.server.feature.FishDB;
import org.simpleframework.xml.core.Persister;

/**
 * Provides a data service for accessing Pokemon species data.
 * @author shadowkanji
 *
 */
public class DataService {
	private static PokemonSpeciesData m_speciesData;
	private static POLRDatabase m_polrData;
	private static JewelMechanics m_mechanics;
	private static MoveList m_moveList;
	private static MoveSetData m_moveSetData;
	private static DropDatabase m_dropData;
	private static FishDB m_fishingData;
	private static ArrayList<String> m_nonTrades;
	
	/**
	 * Default constructor. Loads data immediately.
	 */
	public DataService() {
		try {
			Persister stream = new Persister();
			/*
			 * Load all of shoddy's databases
			 */
			m_moveList = new MoveList(true);
			m_moveSetData = new MoveSetData();
			m_speciesData = new PokemonSpeciesData();
			m_mechanics = new JewelMechanics(5);
			m_dropData = new DropDatabase();
			m_dropData.reinitialise();
			m_fishingData = new FishDB();
			m_fishingData.reinitialise();
			JewelMechanics.loadMoveTypes("res/movetypes.txt");
			File f = new File(".");
			m_moveSetData = stream.read(MoveSetData.class, new File(f.getCanonicalPath() + "/res/movesets.xml"));
			m_speciesData = stream.read(PokemonSpeciesData.class, new File(f.getCanonicalPath() + "/res/species.xml"));
			PokemonSpecies.setDefaultData(m_speciesData);
			m_polrData = stream.read(POLRDatabase.class, new File(f.getCanonicalPath() + "/res/polrdb.xml"));
			System.out.println("INFO: Pokemon Databases loaded.");
			/*
			 * List of non-tradeable Pokemon
			 */
			m_nonTrades = new ArrayList<String>();
			m_nonTrades.add("Bulbasaur");
			m_nonTrades.add("Ivysaur");
			m_nonTrades.add("Venusaur");
			m_nonTrades.add("Squirtle");
			m_nonTrades.add("Wartortle");
			m_nonTrades.add("Blastoise");
			m_nonTrades.add("Charmander");
			m_nonTrades.add("Charmeleon");
			m_nonTrades.add("Charizard");
			m_nonTrades.add("Chikorita");
			m_nonTrades.add("Bayleef");
			m_nonTrades.add("Meganium");
			m_nonTrades.add("Cyndaquil");
			m_nonTrades.add("Quilava");
			m_nonTrades.add("Typhlosion");
			m_nonTrades.add("Totodile");
			m_nonTrades.add("Croconaw");
			m_nonTrades.add("Feraligatr");
			m_nonTrades.add("Treecko");
			m_nonTrades.add("Grovyle");
			m_nonTrades.add("Sceptile");
			m_nonTrades.add("Torchic");
			m_nonTrades.add("Combusken");
			m_nonTrades.add("Blaziken");
			m_nonTrades.add("Mudkip");
			m_nonTrades.add("Marshtomp");
			m_nonTrades.add("Swampert");
			m_nonTrades.add("Turtwig");
			m_nonTrades.add("Grotle");
			m_nonTrades.add("Torterra");
			m_nonTrades.add("Chimchar");
			m_nonTrades.add("Monferno");
			m_nonTrades.add("Infernape");
			m_nonTrades.add("Piplup");
			m_nonTrades.add("Prinplup");
			m_nonTrades.add("Empoleon");
			System.out.println("INFO: Trade Block List established.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns true if the pokemon is tradeable
	 * @param pokemon
	 * @return
	 */
	public static boolean canTrade(String pokemon) {
		for(int i = 0; i < m_nonTrades.size(); i++) {
			if(m_nonTrades.get(i).equalsIgnoreCase(pokemon))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns the shoddybattle species database
	 * @return
	 */
	public static PokemonSpeciesData getSpeciesDatabase() {
		return m_speciesData;
	}
	
	/**
	 * Returns the drop database
	 * @return
	 */
	public static DropDatabase getDropDatabase() {
		return m_dropData;
	}
	/**
	 * Returns the fish database
	 * @return
	 */
	public static FishDB getFishDatabase() {
		return m_fishingData;
	}
	
	/**
	 * Returns the polr database (contains evolution, move learning, etc.)
	 * @return
	 */
	public static POLRDatabase getPOLRDatabase() {
		return m_polrData;
	}
	
	/**
	 * Returns shoddybattle battle mechanics
	 * @return
	 */
	public static JewelMechanics getBattleMechanics() {
		return m_mechanics;
	}
	
	/**
	 * Returns the move list
	 * @return
	 */
	public static MoveList getMovesList() {
		return m_moveList;
	}
	
	/**
	 * Returns move set data
	 * @return
	 */
	public static MoveSetData getMoveSetData() {
		return m_moveSetData;
	}
}
