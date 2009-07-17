package org.pokenet.server.network.message;

import org.pokenet.server.backend.entity.Positionable.Direction;

/**
 * A character movement message
 * @author shadowkanji
 *
 */
public class MoveMessage extends PokenetMessage {
	/**
	 * Constructor
	 * @param d
	 * @param id
	 */
	public MoveMessage(Direction d, int id, boolean directionChange) {
		if(directionChange) {
			switch(d) {
			case Up:
				m_message = "cU" + id;
				break;
			case Down:
				m_message = "cD" + id;
				break;
			case Left:
				m_message = "cL" + id;
				break;
			case Right:
				m_message = "cR" + id;
				break;
			}
		} else {
			switch(d) {
			case Up:
				m_message = "U" + id;
				break;
			case Down:
				m_message = "D" + id;
				break;
			case Left:
				m_message = "L" + id;
				break;
			case Right:
				m_message = "R" + id;
				break;
			}
		}
	}

}
