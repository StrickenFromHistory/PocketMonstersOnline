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
	private boolean m_isBattling;
	private IoSession m_session;
	
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
	 * Sends an error code to the client. -128 to 127
	 * @param error
	 */
	public void sendErrorCode(String error) {
		
	}
}
