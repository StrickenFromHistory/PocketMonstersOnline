package org.pokenet.client.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.pokenet.client.ui.frames.LoginDialog;
import org.pokenet.client.ui.frames.RegisterDialog;
import org.pokenet.client.ui.frames.ServerDialog;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Label;
import mdes.slick.sui.Window;

/**
 * The login screen (contains server selector, login and registration)
 * @author shadowkanji
 *
 */
public class LoginScreen extends Window {
	private Label m_bg;
	private ServerDialog m_select;
	private LoginDialog m_login;
	private RegisterDialog m_register;
	private Color m_bgColor;

	/**
	 * Default constructor
	 */
	public LoginScreen() {
		try {
			m_bgColor = new Color(255, 255, 255, 70);
			
			/*
			 * Load the background image
			 */
			m_bg = new Label(new Image("res/pokenet.png"));
			m_bg.pack();
			m_bg.setLocation(0, 0);
			m_bg.setVisible(true);
			this.add(m_bg);
			
			/*
			 * Create the server selector container
			 */
			m_select = new ServerDialog();
			this.add(m_select);
			
			/*
			 * Create the login container
			 */
			m_login = new LoginDialog();
			this.add(m_login);
			
			m_register = new RegisterDialog();
			this.add(m_register);
			
			this.setLocation(0, 0);
			this.setSize(800, 600);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Shows the login dialog
	 */
	public void showLogin() {
		m_select.setVisible(false);
		m_register.setVisible(false);
		m_login.setVisible(true);
	}
	
	/**
	 * Shows the registration dialog
	 */
	public void showRegistration() {
		m_select.setVisible(false);
		m_login.setVisible(false);
		m_register.setVisible(true);
	}
	
	/**
	 * Shows the server selection dialog
	 */
	public void showServerSelect() {
		m_register.setVisible(false);
		m_login.setVisible(false);
		m_select.setVisible(true);
	}
}
