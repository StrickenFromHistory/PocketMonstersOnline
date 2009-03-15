package org.pokenet.client.ui.base;

import org.newdawn.slick.Color;

import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;

/**
 * A notification
 * @author shadowkanji
 *
 */
public class Notification extends Frame {
	private Label m_text;
	
	public Notification(String message) {
		this.setSize(96, 64);
		this.setBackground(new Color(0, 0, 0, 75));
		
		m_text = new Label(message);
		m_text.pack();
		
	}

}
