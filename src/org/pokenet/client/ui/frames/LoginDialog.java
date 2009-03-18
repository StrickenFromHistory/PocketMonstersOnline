package org.pokenet.client.ui.frames;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.TextField;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

/**
 * Handles the login box
 * @author shadowkanji
 *
 */
public class LoginDialog extends Frame {
	private TextField m_username, m_password;
	private Label m_userLabel, m_passLabel;
	private Button m_login, m_register;
	private Color m_white;
	
	/**
	 * Default constructor
	 */
	public LoginDialog() {
		this.setBorderRendered(false);
		this.getTitleBar().setVisible(false);
		this.setSize(320, 160);
		this.setLocation(480, 424);
		this.setBackground(new Color(0, 0, 0, 70));
		this.setDraggable(false);
		this.setResizable(false);
		
		/*
		 * Set up the components
		 */
		m_white = new Color(255, 255, 255);
		
		m_username = new TextField();
		m_username.setSize(132, 24);
		m_username.setLocation(128, 8);
		m_username.setVisible(true);
		this.add(m_username);
		
		m_password = new TextField();
		m_password.setSize(132, 24);
		m_password.setLocation(128, 40);
		m_password.setVisible(true);
		m_password.setMaskCharacter('*');
		m_password.setMaskEnabled(true);
		this.add(m_password);
		
		m_userLabel = new Label("Username:");
		m_userLabel.pack();
		m_userLabel.setLocation(m_username.getX() - m_userLabel.getWidth() - 24, 12);
		m_userLabel.setVisible(true);
		m_userLabel.setFont(GameClient.getFontSmall());
		m_userLabel.setForeground(m_white);
		this.add(m_userLabel);
		
		m_passLabel = new Label("Password:");
		m_passLabel.pack();
		m_passLabel.setLocation(m_userLabel.getX(), 40);
		m_passLabel.setVisible(true);
		m_passLabel.setFont(GameClient.getFontSmall());
		m_passLabel.setForeground(m_white);
		this.add(m_passLabel);
		
		m_login = new Button("Login");
		m_login.setSize(64, 32);
		m_login.setLocation(m_password.getX(), m_password.getY() + m_password.getHeight() + 8);
		m_login.setVisible(true);
		m_login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(m_username.getText() != null & !m_username.getText().equalsIgnoreCase("") && 
						m_password.getText() != null && !m_password.getText().equalsIgnoreCase("")) {
					login();
				}
			}
		});
		this.add(m_login);
		
		m_register = new Button("Register");
		m_register.setSize(64, 32);
		m_register.setLocation(m_login.getX() + m_login.getWidth() + 8, m_login.getY());
		m_register.setVisible(true);
		m_register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				register();
			}
		});
		this.add(m_register);
		
		this.setVisible(false);
	}
	
	/**
	 * Sends login information to packet generator to be sent to server
	 */
	private void login() {
		m_login.setEnabled(false);
		GameClient.getInstance().getLoadingScreen().setVisible(true);
		GameClient.getInstance().getPacketGenerator().login(m_username.getText(), m_password.getText());
	}
	
	/**
	 * Opens registration window
	 */
	private void register() {
		GameClient.getInstance().getLoginScreen().showRegistration();
	}
	
	/**
	 * Returns the login button
	 * @return
	 */
	public Button getLoginButton() {
		return m_login;
	}
}
