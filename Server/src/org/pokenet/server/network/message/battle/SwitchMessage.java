package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * Switch in Pokemon
 * @author shadowkanji
 *
 */
public class SwitchMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param playerName
	 * @param pokemonSpecies
	 * @param trainer
	 * @param partyIndex
	 */
	public SwitchMessage(String playerName, String pokemonSpecies, int trainer, int partyIndex) {
		m_message = "bS" + playerName + "," + pokemonSpecies
			+ "," + trainer + "," + partyIndex;
	}
}
