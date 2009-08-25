
package org.pokenet.server.backend.entity;

import java.util.Timer;
import java.util.TimerTask;

public class HMObject extends NonPlayerChar {
	public enum objectType {
		ROCKSMASH_ROCK, CUT_TREE, STRENGTH_BOULDER, WHIRLPOOL
	}
	
	public static objectType parseHMObject(String name) throws Exception{
		for (objectType oT : objectType.values()){
			if (name.equalsIgnoreCase(oT.name()))
				return oT;
		}
		throw new Exception("The HMObject requested is invalid.");
	}

	private objectType m_HMType;

	public objectType getType() {
		return m_HMType;
	}

	public void setType(objectType oT) {
		m_HMType = oT;
	}
	
	public int getNecessaryTrainerLevel(objectType oT) {
		switch (oT) {
		case ROCKSMASH_ROCK:
			return 30;
		case CUT_TREE:
			return 0;
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
		System.out.println("Talking to an HM object? It won't answer to you...");
		System.out.println(getMap().getX() + " " + getMap().getY());
		if (p.getTrainingLevel() >= getNecessaryTrainerLevel(getType())) {
			System.err.println("WOO YOU CAN DO THIS!");
			switch (m_HMType){
			case STRENGTH_BOULDER :
				setNextMovement(p.getFacing());
				break;
			case CUT_TREE:
			case ROCKSMASH_ROCK:
			case WHIRLPOOL:
				getMap().removeChar(this);
				final HMObject hmObj = this;
				Timer timer = new Timer();
				timer.schedule(
						new TimerTask(){
							public void run(){
								m_map.addChar(hmObj);
							}
						}, 10000);
				break;
			}
		} else {
			// The player isn't strong enough to do this. Alert client
		}
	}
}