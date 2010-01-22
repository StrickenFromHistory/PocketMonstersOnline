package org.pokenet.chat.server;

import org.apache.mina.core.session.IoSession;

/**
 * Represents a user/player
 * @author shadowkanji
 *
 */
public class User {
	private String m_username;
	private int m_level;
	private String m_team;
	private IoSession m_session;
	
	/**
	 * Sets username
	 * @param username
	 */
	public void setUsername(String username) {
		m_username = username;
	}
	
	/**
	 * Sets permission level
	 * @param l
	 */
	public void setLevel(int l) {
		m_level = l;
	}
	
	/**
	 * Sets team (e.g. Team Rocket)
	 * @param team
	 */
	public void setTeam(String team) {
		m_team = team;
	}
	
	/**
	 * Sets session of user
	 * @param s
	 */
	public void setSession(IoSession s) {
		m_session = s;
	}
	
	/**
	 * Returns username
	 * @return
	 */
	public String getUsername() {
		return m_username;
	}
	
	/**
	 * Returns the session
	 * @return
	 */
	public IoSession getSession() {
		return m_session;
	}
	
	/**
	 * Returns permission level
	 * @return
	 */
	public int getLevel() {
		return m_level;
	}
	
	/**
	 * Returns player's team
	 * @return
	 */
	public String getTeam() {
		return m_team;
	}
}
