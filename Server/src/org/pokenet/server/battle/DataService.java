package org.pokenet.server.battle;

import java.io.File;

import org.pokenet.server.backend.item.DropDatabase;
import org.pokenet.server.battle.mechanics.JewelMechanics;
import org.pokenet.server.battle.mechanics.moves.MoveList;
import org.pokenet.server.battle.mechanics.moves.MoveSetData;
import org.pokenet.server.battle.mechanics.polr.POLRDatabase;
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
	
	/**
	 * Default constructor. Loads data immediately.
	 */
	public DataService() {
		try {
			Persister stream = new Persister();
			m_moveList = new MoveList(true);
			m_moveSetData = new MoveSetData();
			m_speciesData = new PokemonSpeciesData();
			m_mechanics = new JewelMechanics(5);
			m_dropData = new DropDatabase();
			m_dropData.reinitialise();
			JewelMechanics.loadMoveTypes("res/movetypes.txt");
			File f = new File(".");
			m_moveSetData = stream.read(MoveSetData.class, new File(f.getCanonicalPath() + "/res/movesets.xml"));
			m_speciesData = stream.read(PokemonSpeciesData.class, new File(f.getCanonicalPath() + "/res/species.xml"));
			PokemonSpecies.setDefaultData(m_speciesData);
			m_polrData = stream.read(POLRDatabase.class, new File(f.getCanonicalPath() + "/res/polrdb.xml"));
			System.out.println("INFO: Databases loaded.");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
