package org.pokenet.client.ui.frames;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.TextField;

/**
 * Handles the login box
 * @author shadowkanji
 *
 */
public class LoginDialog extends Frame {
	private TextField m_username, m_password;
	private Label m_userLabel, m_passLabel;
	private Button m_login, m_register;
	private Color m_bg;
	
	/**
	 * Default constructor
	 */
	public LoginDialog() {
		this.setBorderRendered(false);
		this.getTitleBar().setVisible(false);
		this.setSize(240, 160);
		this.setLocation(480, 424);
		this.setBackground(new Color(0, 0, 0, 70));
		this.setDraggable(false);
		this.setResizable(false);
		
		/*
		 * Set up the components
		 */
		m_username = new TextField();
		m_username.setSize(196, 24);
		m_username.setLocation(128, 8);
		m_username.setVisible(true);
		this.add(m_username);
		
		this.setVisible(false);
	}
	
	/**
	 * Sends login information to packet generator to be sent to server
	 */
	private void login() {
		GameClient.getInstance().getPacketGenerator().login(m_username.getText(), m_password.getText());
	}
	
	/**
	 * Opens registration window
	 */
	private void register() {
		GameClient.getInstance().getLoginScreen().showRegistration();
	}
}
