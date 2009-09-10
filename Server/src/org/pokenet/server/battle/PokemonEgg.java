package org.pokenet.server.battle;

/**
 * 
 * @author ZombieBear
 * 
 */
public class PokemonEgg extends Pokemon {
	private static final long serialVersionUID = -3895787504332248433L;
	private int m_timeRemaining;

	/**
	 * Constructor
	 * @param poke The baby pokemon to hatch from the egg
	 * @param time The time in milliseconds for the egg to hatch
	 */
	public PokemonEgg(Pokemon poke, int time) {
		super(poke);
		m_timeRemaining = time;
	}

	/**
	 * Returns the amount of time remaining
	 * @return
	 */
	public int getTimeRemaining(){
		return m_timeRemaining;
	}
	
	/**
	 * Returns the pokemon held inside the egg
	 * @return
	 */
	public Pokemon hatch() {
		return (Pokemon)this;
	}
}