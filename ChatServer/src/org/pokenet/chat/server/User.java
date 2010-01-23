package org.pokenet.chat.server;

import java.util.ArrayList;

import org.apache.mina.core.session.IoSession;

/**
 * Represents a user/player
 * @author shadowkanji
 *
 */
public class User {
	public enum Language { NONE, ENGLISH, PORTUGESE, ITALIAN, FRENCH, FINNISH, SPANISH, GERMAN, DUTCH }
	private Language m_language;
	private String m_username;
	private int m_level;
	private String m_team;
	private IoSession m_session;
	private ArrayList<String> m_friends;
	
	/**
	 * Constructor
	 */
	public User(Language l) {
		m_friends = new ArrayList<String>();
		m_language = l;
	}
	
	/**
	 * Returns the language of the user
	 * @return
	 */
	public Language getLanguage() {
		return m_language;
	}
	
	/**
	 * Returns friends list
	 * @return
	 */
	public ArrayList<String> getFriends() {
		return m_friends;
	}
	
	/**
	 * Adds a friend to friends list
	 * @param s
	 */
	public void addFriend(String s) {
		m_friends.add(s);
		m_session.write("a" + s);
	}
	
	/**
	 * Removes a friend
	 * @param s
	 */
	public void removeFriend(String s) {
		if(m_friends.remove(s)) {
			m_session.write("r" + s);
		}
	}
	
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
