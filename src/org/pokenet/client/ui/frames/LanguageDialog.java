package org.pokenet.client.ui.frames;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

/**
 * Handles language selection
 * @author Nushio
 *
 */
public class LanguageDialog extends Frame {
	private Button[] m_languages;
	private Label m_info;
	private Color m_black;
	/**
	 * Default constructor
	 */
	public LanguageDialog() {
		m_black = new Color(0, 0, 0);
		
		this.setSize(316, 280);
		this.setLocation(400 - 160, 280);
		this.setTitle("Pokenet Language Selection");
		this.setBackground(new Color(0, 0, 0, 70));
		this.getTitleBar().setForeground(m_black);
		this.setDraggable(false);
		this.setResizable(false);
		this.getTitleBar().getCloseButton().setVisible(false);
		
		/*
		 * Create the info label
		 */
		m_info = new Label("Welcome | Bienvenido | Bienvenue | Bem-vindo");
		m_info.pack();
		m_info.setLocation(24, 8);
		m_info.setForeground(new Color(255, 255, 255));
		this.add(m_info);
		
		/*
		 * Create all the server buttons
		 */
		try {
			m_languages = new Button[4];
			
			
			m_languages[0] = new Button("English");
			m_languages[0].setSize(280, 24);
			m_languages[0].setLocation(16, 32);
			m_languages[0].setVisible(true);
			m_languages[0].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					GameClient.setLanguage("english");
				}
			});
			this.add(m_languages[0]);
			
			m_languages[1] = new Button("Espanol");
			m_languages[1].setSize(280, 24);
			m_languages[1].setLocation(16, 32);
			m_languages[1].setVisible(true);
			m_languages[1].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					GameClient.setLanguage("spanish");
				}
			});
			this.add(m_languages[1]);
			
			m_languages[2] = new Button("Francais");
			m_languages[2].setSize(280, 24);
			m_languages[2].setLocation(16, 32);
			m_languages[2].setVisible(true);
			m_languages[2].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					GameClient.setLanguage("french");
				}
			});
			this.add(m_languages[2]);
			
			m_languages[3] = new Button("Portugues");
			m_languages[3].setSize(280, 24);
			m_languages[3].setLocation(16, 32);
			m_languages[3].setVisible(true);
			m_languages[3].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					GameClient.setLanguage("portugues");
				}
			});
			this.add(m_languages[3]);
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		this.setVisible(true);
	}
	
}
