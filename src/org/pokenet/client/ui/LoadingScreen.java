package org.pokenet.client.ui;

import java.io.InputStream;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;

/**
 * The loading screen
 * @author shadowkanji
 *
 */
public class LoadingScreen extends Frame {
	private Label m_bg;
	
	/**
	 * Default constructor
	 */
	public LoadingScreen() {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		try {
			this.setSize(800, 632);
			this.setBackground(new Color(255, 255, 255, 70));
			this.setLocation(0, -32);
			this.setResizable(false);
			this.getTitleBar().setVisible(false);
			
			InputStream f = getClass().getResourceAsStream("/res/ui/loading.png");
			m_bg = new Label(new Image(f, "/res/ui/loading.png", false));
			m_bg.pack();
			m_bg.setLocation(400 - (m_bg.getWidth() / 2), 300 - (m_bg.getHeight() /2));
			m_bg.setVisible(true);
			this.add(m_bg);
			
			this.setVisible(false);
			this.setAlwaysOnTop(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
