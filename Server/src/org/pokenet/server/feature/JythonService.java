package org.pokenet.server.feature;

import org.pokenet.server.backend.entity.PlayerChar;

/**
 * Handles all scripting. This is one of the few services which isn't (and shouldn't) run as a thread.
 * @author shadowkanji
 *
 */
public class JythonService {
	private JythonService m_defaultInstance = null;
	
	/**
	 * Returns the default instance of JythonService
	 * @return
	 */
	public JythonService getInstance() {
		if(m_defaultInstance == null)
			m_defaultInstance = new JythonService();
		return m_defaultInstance;
	}
	
	/**
	 * Returns an object based on a jython module
	 * @param interfaceName
	 * @param pathToJythonModule
	 * @return
	 */
	public Object getJythonObject(
			String interfaceName,
            String pathToJythonModule) {
		return null;
	}
	
	/**
	 * Runs a quest script on a specific player
	 * @param player
	 */
	public void runQuestScript(PlayerChar player) {
		
	}
}
