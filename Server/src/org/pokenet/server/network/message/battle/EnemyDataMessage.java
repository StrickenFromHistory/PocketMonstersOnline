package org.pokenet.server.network.message.battle;

import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.network.message.PokenetMessage;

/**
 * Battle: Enemy data
 * @author shadowkanji
 *
 */
public class EnemyDataMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param index
	 * @param p
	 */
	public EnemyDataMessage(int index, Pokemon p) {
		m_message = "bP" + index + "," + p.getName() + "," + p.getLevel() + ","
			+ p.getGender() + "," + p.getHealth() + ","
			+ p.getHealth() + "," + p.getSpeciesNumber()
			+ "," + p.isShiny();
	}
}
