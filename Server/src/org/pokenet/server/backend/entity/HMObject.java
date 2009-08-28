
package org.pokenet.server.backend.entity;

import java.util.Timer;
import java.util.TimerTask;

import org.pokenet.server.GameServer;

public class HMObject extends NonPlayerChar {
	public enum objectType {
		ROCKSMASH_ROCK, CUT_TREE, STRENGTH_BOULDER, WHIRLPOOL
	}
	
	private static int HMObjectID = 0;
	private boolean addToMovementManager = true;
	private int originalX, originalY;
	private Timer timer = new Timer();
	
	public static objectType parseHMObject(String name) throws Exception{
		for (objectType oT : objectType.values()){
			if (name.equalsIgnoreCase(oT.name()))
				return oT;
		}
		throw new Exception("The HMObject requested is invalid.");
	}
	
	private objectType m_HMType;
	private int m_objId;
	final HMObject hmObj = this;
	
	public objectType getType() {
		return m_HMType;
	}
	
	public void setOriginalX(int x){
		originalX = x;
	}

	public void setOriginalY(int y){
		originalY = y;
	}
	
	public int getObjId(){
		return m_objId;
	}

	public void setType(objectType oT) {
		m_HMType = oT;
		if (oT == objectType.STRENGTH_BOULDER){
			HMObjectID++;
			m_objId = HMObjectID; 
		}
		switch (oT){
		case ROCKSMASH_ROCK:
			setSprite(-4);
			break;
		case CUT_TREE:
			setSprite(-2);
			break;
		case STRENGTH_BOULDER:
			setSprite(-3);
			break;
		case WHIRLPOOL:
			setSprite(-5);
			break;
		}
	}
	
	public int getNecessaryTrainerLevel(objectType oT) {
		switch (oT) {
		case ROCKSMASH_ROCK:
			return 30;
		case CUT_TREE:
			return 15;
		case STRENGTH_BOULDER:
			return 35;
		case WHIRLPOOL:
			return 40;
		}
		return 0;
	}
	
	@Override
	public void talkToPlayer(PlayerChar p) {
		// Handle event
		if (p.getTrainingLevel() >= getNecessaryTrainerLevel(getType())) {
			switch (m_HMType){
			case STRENGTH_BOULDER :
				setNextMovement(p.getFacing());
				if (addToMovementManager) {
					GameServer.getServiceManager().getMovementService().getMovementManager().addHMObject(this);
					addToMovementManager = false;
				}
				// Return to original position 30 seconds after last movement
				timer.schedule(
						new TimerTask(){
							public void run(){
								hmObj.setX(originalX);
								hmObj.setY(originalY);
							}
						}, 30000);
				break;
			case CUT_TREE:
			case ROCKSMASH_ROCK:
			case WHIRLPOOL:
				getMap().removeChar(this);
				// Regrow tree after 30 seconds
				timer.schedule(
						new TimerTask(){
							public void run(){
								m_map.addChar(hmObj);
							}
						}, 30000);
				break;
			}
		} else {
			// The player isn't strong enough to do this. Alert client
			p.getTcpSession().write("ch" + getNecessaryTrainerLevel(m_HMType));
		}
	}
}