package org.pokenet.server.battle;

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
	private PokemonSpeciesData m_speciesData;
	private POLRDatabase m_polrData;
	private JewelMechanics m_mechanics;
	private MoveList m_moveList;
	private MoveSetData m_moveSetData;
	
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
			JewelMechanics.loadMoveTypes("res/movetypes.txt");
			m_moveSetData = stream.read(MoveSetData.class, "res/movesets.xml");
			m_speciesData = stream.read(PokemonSpeciesData.class, "res/species.xml");
			m_polrData = stream.read(POLRDatabase.class, "res/polrdb.xml");
			System.out.println("INFO: Databases loaded.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the shoddybattle species database
	 * @return
	 */
	public PokemonSpeciesData getSpeciesDatabase() {
		return m_speciesData;
	}
	
	/**
	 * Returns the polr database (contains evolution, move learning, etc.)
	 * @return
	 */
	public POLRDatabase getPOLRDatabase() {
		return m_polrData;
	}
	
	/**
	 * Returns shoddybattle battle mechanics
	 * @return
	 */
	public JewelMechanics getBattleMechanics() {
		return m_mechanics;
	}
	
	/**
	 * Returns the move list
	 * @return
	 */
	public MoveList getMovesList() {
		return m_moveList;
	}
	
	/**
	 * Returns move set data
	 * @return
	 */
	public MoveSetData getMoveSetData() {
		return m_moveSetData;
	}
}
