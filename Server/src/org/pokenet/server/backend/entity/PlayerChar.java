package org.pokenet.server.backend.entity;

import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.Pokemon;

/**
 * Represents a player
 * @author shadowkanji
 *
 */
public class PlayerChar extends Char implements Battleable {
	/**
	 * Returns the battlefield this player is on.
	 */
	public BattleField getField() {
		/*
		 * Maybe it'll be better to return the battlefield via BattleService as it'll be a direct access to the object.
		 * On the other hand, this requires a little bit of processing (unless we store the battle service and place in the
		 * battle service arraylist) but it'll reduce problems with garbage collection.
		 */
		return null;
	}

	/**
	 * Returns the battle id of this player on the battlefield
	 */
	public int getBattleID() {
		/*
		 * See getField about using direct reference. 
		 */
		return 0;
	}

	/**
	 * Returns this player's opponent
	 */
	public Battleable getOpponent() {
		return null;
	}

	/**
	 * Returns the Pokemon party of this player
	 */
	public Pokemon[] getParty() {
		return null;
	}

	/**
	 * Returns true if this player is battling
	 */
	public boolean isBattling() {
		return false;
	}

	/**
	 * Sets this player's battle id on a battlefield
	 */
	public void setBattleID(int battleID) {
	
	}

	/**
	 * Sets the name (username) of this player
	 */
	public void setName(String name) {

	}

	/**
	 * Set the pokemon party of this player
	 */
	public void setParty(Pokemon[] team) {
		
	}
}
