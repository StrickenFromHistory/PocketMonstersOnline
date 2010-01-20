package org.pokenet.client.backend.entity;


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
		STRENGTH_BOULDER,
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
		case STRENGTH_BOULDER:
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
		case STRENGTH_BOULDER:
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
		setUsername("");
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
	
	@Override
	public int getType(){
		return 2;
	}
}
