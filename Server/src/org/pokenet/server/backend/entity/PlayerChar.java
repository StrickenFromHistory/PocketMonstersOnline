package org.pokenet.server.backend.entity;

import org.apache.mina.common.IoSession;
import org.pokenet.server.GameServer;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.Pokemon;

/**
 * Represents a player
 * @author shadowkanji
 *
 */
public class PlayerChar extends Char implements Battleable {
	private Pokemon[] m_pokemon;
	private boolean m_isBattling = false;
	private IoSession m_session;
	/*
	 * Badges are stored as bytes. 0 = not obtained, 1 = obtained
	 * Stored as following:
	 * 0 - 7   Kanto Badges
	 * 8 - 15  Johto Badges
	 * 16 - 23 Hoenn Badges
	 * 24 - 31 Sinnoh Badges
	 * 32 - 35 Orange Islands
	 * 36 - 41
	 */
	private byte [] m_badges;
	
	/**
	 * Creates a new PlayerChar
	 */
	public void createNewPlayer() {
		//Set up all badges.
		m_badges = new byte[42];
		for(int i = 0; i < m_badges.length; i++) {
			m_badges[i] = 0;
		}
	}
	
	/**
	 * Sets the badges this player has
	 * @param badges
	 */
	public void setBadges(byte [] badges) {
		m_badges = badges;
	}
	
	/**
	 * Returns the battlefield this player is on.
	 */
	public BattleField getBattleField() {
		return GameServer.getServiceManager().getBattleFieldForPlayer(this);
	}

	/**
	 * Returns the battle id of this player on the battlefield
	 */
	public int getBattleID() {
		//NOTE: I HAVE NO IDEA WHAT THIS DOES? IS IT THE TRAINER INDEX ON THE BATTLEFIELD?
		return 0;
	}

	/**
	 * Returns this player's opponent
	 */
	public Battleable getOpponent() {
		//DO WE REALLY NEED THIS?
		return null;
	}

	/**
	 * Returns the Pokemon party of this player
	 */
	public Pokemon[] getParty() {
		return m_pokemon;
	}

	/**
	 * Returns true if this player is battling
	 */
	public boolean isBattling() {
		return m_isBattling;
	}

	/**
	 * Sets this player's battle id on a battlefield
	 */
	public void setBattleID(int battleID) {
	
	}

	/**
	 * Set the pokemon party of this player
	 */
	public void setParty(Pokemon[] team) {
		m_pokemon = team;
	}
	
	/**
	 * Sets the session for this player (their connection to the server)
	 * @param session
	 */
	public void setSession(IoSession session) {
		m_session = session;
	}
	
	/**
	 * Returns the session (connection to server) for this player
	 * @return
	 */
	public IoSession getSession() {
		return m_session;
	}
	
	/**
	 * Sends an error code to the client. Must be between -128 to 127.
	 * See the wiki page of error codes @ http://pokenetonline.googlecode.com
	 * @param error
	 */
	public void sendErrorCode(String error) {
		
	}
	
	/**
	 * Overrides char's move method.
	 * Adds a check for wild battles and clears battle/trade request lists
	 */
	public void move() {
		super.move();
		if(this.getMap().isWildBattle())
			GameServer.getServiceManager().getBattleService().startWildBattle(this, this.getMap().getWildPokemon(this));
		//TODO: Clear requests list
	}
}
