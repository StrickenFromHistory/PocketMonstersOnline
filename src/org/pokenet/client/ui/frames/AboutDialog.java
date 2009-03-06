package org.pokenet.client.ui.frames;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;

import mdes.slick.sui.Frame;
import mdes.slick.sui.TextArea;

/**
 * A window with information about the game
 * @author shadowkanji
 *
 */
public class AboutDialog extends Frame {
	private TextArea m_info;
	private Color m_bg, m_white;

	/**
	 * Default constructor
	 */
	public AboutDialog() {
		m_bg = new Color(0, 0, 0, 70);
		m_white = new Color(255, 255, 255);
		
		this.setTitle("About Pokenet");
		this.setLocation(128, 256);
		this.setBackground(m_bg);
		this.setResizable(false);
		
		m_info = new TextArea();
		m_info.setSize(280, 320);
		m_info.setLocation(4, 4);
		m_info.setWrapEnabled(true);
		m_info.setText("Pokenet is a free, non-profit, open-source Pokemon MMORPG. " +
				"Pokenet is in no way affiliated with or officially supported by Nintendo. " +
				"Pokèmon is a registered trademark of Nintendo (1995 - Present).\n \n" +
				"Contributors:\n" +
				"Fshy, ZombieBear, Ryan, Pivot/Ilentcape, Sienide, TMKCodes, Drawing, LordAdmiral, Psycho, Dragina, " +
				"Oblivion, Lastplacer, Firefly17, Gama, Jacobe, Latias, Callagan, xkendrickx, Mewtwo, PheonixClaw, " +
				"JayJay, May");
		m_info.setFont(GameClient.getFontSmall());
		m_info.setBackground(m_bg);
		m_info.setForeground(m_white);
		this.add(m_info);
		
		this.setSize(288, 320);
		
		this.setVisible(false);
	}
}
