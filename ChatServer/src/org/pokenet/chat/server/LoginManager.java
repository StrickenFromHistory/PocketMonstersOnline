package org.pokenet.chat.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import org.apache.mina.core.session.IoSession;
import org.pokenet.chat.server.User.Language;

/**
 * Handles log ins
 * @author shadowkanji
 *
 */
public class LoginManager implements Runnable {
	private Queue<Object []> m_queue = new LinkedList<Object []>();
	private MySqlManager m_mysql = new MySqlManager();
	private String m_dbServer, m_dbUser, m_dbPass, m_dbDatabase;
	
	/**
	 * Constructor
	 */
	public LoginManager() {
		/* Load mysql settings from settings.txt */
		try {
			Scanner s = new Scanner(new File("./settings.txt"));
			m_dbServer = s.nextLine();
			m_dbDatabase = s.nextLine();
			m_dbUser = s.nextLine();
			m_dbPass = s.nextLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Queues a login
	 * @param s
	 * @param username
	 * @param password
	 */
	public void queueLogin(IoSession s, String username, String password, char l) {
		synchronized(m_queue) {
			m_queue.offer(new Object [] { s, username, password, l });
		}
	}

	@Override
	public void run() {
		Object [] tmp = null;
		IoSession s = null;
		String username = null;
		String password = null;
		char language;
		while(true) {
			synchronized(m_queue) {
				tmp = m_queue.poll();
			}
			if(tmp != null) {
				s = (IoSession) tmp[0];
				username = (String) tmp[1];
				password = (String) tmp[2];
				language = (Character) tmp[3];
				User u = login(s, username, password, language);
				if(u != null) {
					ChatProtocolHandler.addUser(u);
				}
				tmp = null;
			}
			try {
				Thread.sleep(500);
			} catch (Exception e) {}
		}
	}

	/**
	 * Logs in a user and returns their user object
	 * @param s
	 * @param username
	 * @param password
	 * @param language 
	 * @return
	 */
	private User login(IoSession s, String username, String password, char language) {
		username = MySqlManager.parseSQL(username);
		password = MySqlManager.parseSQL(password);
		if(m_mysql.connect(m_dbServer, m_dbUser, m_dbPass)) {
			m_mysql.selectDatabase(m_dbDatabase);
			ResultSet result = m_mysql.query("SELECT team, adminLevel" +
					" FROM pn_members WHERE username='" + username + "' AND password='" +
					password + "'");
			if(result != null) {
				//Valid user, yay!
				User user = new User(getLanguageByChar(language));
				/* Set user information */
				user.setUsername(username);
				try {
					user.setLevel(result.getInt("adminLevel"));
				} catch (Exception e) { user.setLevel(0); }
				try {
					user.setTeam(result.getString("team"));
				} catch (Exception e) { user.setTeam(""); }
				user.setSession(s);
				/* TODO: Get friends list */
				s.write("ls");
				m_mysql.close();
				return user;
			} else {
				//Invalid username or password
				s.write("le");
			}
			m_mysql.close();
		} else {
			s.write("lc");
		}
		return null;
	}
	
	/**
	 * Returns a language based on a character
	 * @param c
	 * @return
	 */
	private Language getLanguageByChar(char c) {
		switch(c) {
		case '0':
			return Language.ENGLISH;
		case '1':
			return Language.PORTUGESE;
		case '2':
			return Language.ITALIAN;
		case '3':
			return Language.FRENCH;
		case '4':
			return Language.FINNISH;
		case '5':
			return Language.SPANISH;
		case '6':
			return Language.DUTCH;
		case '7':
			return Language.GERMAN;
		}
		return Language.ENGLISH;
	}
}
