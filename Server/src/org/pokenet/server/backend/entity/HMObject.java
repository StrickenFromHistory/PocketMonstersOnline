
package org.pokenet.server.backend.entity;


public class HMObject extends NonPlayerChar {
	public enum objectType {
		ROCKSMASH_ROCK, CUT_TREE, STRENGTH_BOULDER, WHIRLPOOL
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
		if (p.getTrainingLevel() >= getNecessaryTrainerLevel(m_HMType)) {
			switch (m_HMType){
			case STRENGTH_BOULDER :
				setNextMovement(p.getFacing());
				break;
			default :
				getMap().removeChar(this);
				// Launch a timer to readd the element?
				break;
			}
		} else {
			// This shouldn't happen unless the client is modified.
			// Log the player's name on the output log
			System.out.println(p.getName() + " has attempted to perform an illegal operation.");
		}
	}
}