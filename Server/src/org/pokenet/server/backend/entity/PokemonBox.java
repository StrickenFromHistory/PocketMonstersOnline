package org.pokenet.server.backend.entity;

import org.pokenet.server.battle.Pokemon;

/**
 * Represents a Pokemon box.
 * @author shadowkanji
 *
 */
public class PokemonBox {
	private Pokemon [] m_pokemon;
	
	/**
	 * Sets the pokemon in this box
	 * @param pokes
	 */
	public void setPokemon(Pokemon [] pokes) {
		m_pokemon = pokes;
	}
	
	/**
	 * Returns all pokemon
	 * @return
	 */
	public Pokemon [] getPokemon() {
		return m_pokemon;
	}
	
	/**
	 * Returns a specific pokemon
	 * @param i
	 * @return
	 */
	public Pokemon getPokemon(int i) {
		return m_pokemon[i];
	}
	
	/**
	 * Sets a specific pokemon
	 * @param index
	 * @param p
	 */
	public void setPokemon(int index, Pokemon p) {
		m_pokemon[index] = p;
	}
}
