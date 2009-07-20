package org.pokenet.client.ui.base;

import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;

import org.newdawn.slick.Color;

/**
 * A notification
 * @author shadowkanji
 *
 */
public class Notification extends Frame {
	private Label m_text;
	
	public Notification(String message) {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		this.setSize(96, 64);
		this.setBackground(new Color(0, 0, 0, 75));
		
		m_text = new Label(message);
		m_text.pack();
		
	}

}
