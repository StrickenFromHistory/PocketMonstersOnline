package org.pokenet.client.backend.entity;

import java.io.InputStream;
import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.pokenet.client.GameClient;

/**
 * HM Objects
 * @author ZombieBear
 *
 */
public class HMObject {
	private static final HashMap<String, Image> OBJECT_IMAGES;
	private int m_neededTrainerLvl;
	private String m_objectName;
	private Image m_image;
	
	static {
		String path = "";
		OBJECT_IMAGES = new HashMap<String, Image>();
		for (HMObjectType HMObj : HMObjectType.values()) {
			InputStream f = HMObject.class.getClassLoader().getResourceAsStream(
					path + getObjectName(HMObj) + ".png");
			try {
				Image tempImage = new Image(f, getObjectName(HMObj), false);
				OBJECT_IMAGES.put(getObjectName(HMObj), tempImage);
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static enum HMObjectType {
		CUT_TREE,
		ROCKSMASH_ROCK,
		STRENGHT_BOULDER,
		WHIRLPOOL,
		HEADBUTT_TREE
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
			return 0;
		case ROCKSMASH_ROCK:
			return 0;
		case STRENGHT_BOULDER:
			return 0;
		case WHIRLPOOL:
			return 0; 
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
		m_objectName = getObjectName(e);
		m_neededTrainerLvl = getNeededTrainerLvl(e);
		m_image = OBJECT_IMAGES.get(getObjectName(e));
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
	 * Returns the object's image
	 * @return
	 */
	public Image getImage() {
		return m_image;
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
