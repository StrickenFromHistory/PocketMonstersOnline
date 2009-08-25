package org.pokenet.client.backend.entity;

import org.pokenet.client.GameClient;

/**
 * HM Objects
 * @author ZombieBear
 *
 */
public class HMObject extends Player {
	private int m_neededTrainerLvl;
	private String m_objectName;

	public static enum HMObjectType {
		CUT_TREE,
		ROCKSMASH_ROCK,
		STRENGHT_BOULDER,
		WHIRLPOOL,
		HEADBUTT_TREE
	}
	
	public static HMObjectType parseHMObject(String s) throws Exception {
		for (HMObjectType HMObj : HMObjectType.values()) {
			if (s.equalsIgnoreCase(HMObj.name()))
				return HMObj;
		}
		throw new Exception("This is not an HM Object");
	}
	
	/**
	 * Returns the objeect's name (for internal use only)
	 * @param e
	 * @return
	 */
	private static String getObjectName(HMObjectType e){
		switch (e) {
		case CUT_TREE:
			return "Headbutt Tree";
		case ROCKSMASH_ROCK:
			return "Rocksmash Rock";
		case STRENGHT_BOULDER:
			return "Strength Boulder";
		case WHIRLPOOL:
			return "Whirlpool"; 
		case HEADBUTT_TREE:
			return "Headbutt Tree";
		}
		return "";
	}
	
	/**
	 * Returns the necessary trainer level to work the object (for internal use only)
	 * @param e
	 * @return
	 */
	private int getNeededTrainerLvl(HMObjectType e){
		switch (e) {
		case CUT_TREE:
			return 15;
		case ROCKSMASH_ROCK:
			return 30;
		case STRENGHT_BOULDER:
			return 35;
		case WHIRLPOOL:
			return 40; 
		case HEADBUTT_TREE:
			return 0;
		}
		return 0;
	}
	
	/**
	 * Default constructor
	 * @param e HMObjectType enum
	 */
	public HMObject(HMObjectType e){
		super();
		m_objectName = getObjectName(e);
		m_neededTrainerLvl = getNeededTrainerLvl(e);
		setUsername(m_objectName); //Set to "" when done testing
	}
	
	
	
	/**
	 * Returns the necessary trainer level to use the object
	 * @return
	 */
	public int getRequiredTrainerLevel() {
		return m_neededTrainerLvl;
	}
	
	/**
	 * Returns the object's name
	 * @return
	 */
	public String getName(){
		return m_objectName;
	}
	
	/**
	 * Performs the action if a player's trainer level is enough.
	 * Otherwise lets the player know he can't do so.
	 */
	public void performAction() {
		if (GameClient.getInstance().getOurPlayer().getTrainerLevel() >= m_neededTrainerLvl){
			//TODO: Tell server to remove the object
			//GameClient.getInstance().getPacketGenerator().getTcpSession().write("");
			//TODO: Perform the action
		} else {
			GameClient.messageDialog("You are not strong enough to do this.\n" +
					"Your trainer level must be " + getRequiredTrainerLevel() + " to do this.",
					GameClient.getInstance().getDisplay());
		}
	}
}
