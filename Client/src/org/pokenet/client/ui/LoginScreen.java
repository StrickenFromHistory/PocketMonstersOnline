package org.pokenet.client.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import mdes.slick.sui.Button;
import mdes.slick.sui.Label;
import mdes.slick.sui.Window;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.Translator;
import org.pokenet.client.ui.frames.AboutDialog;
import org.pokenet.client.ui.frames.LanguageDialog;
import org.pokenet.client.ui.frames.LoginDialog;
import org.pokenet.client.ui.frames.RegisterDialog;
import org.pokenet.client.ui.frames.ServerDialog;
import org.pokenet.client.ui.frames.ToSDialog;

/**
 * The login screen (contains server selector, login and registration)
 * @author shadowkanji
 *
 */
public class LoginScreen extends Window {
	private Label m_bg, m_serverRev, m_clientRev;
	private ServerDialog m_select;
	private LoginDialog m_login;
	private LanguageDialog m_lang;
	private RegisterDialog m_register;
	private AboutDialog m_about;
	private ToSDialog m_terms;
	private Button m_openAbout, m_openToS;

	/**
	 * Default constructor
	 */
	public LoginScreen() {
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		try {
			InputStream f;
//			m_bgColor = new Color(255, 255, 255, 70);
			List<String> translated = new ArrayList<String>();
			translated = Translator.translate("_LOGIN");
			/*
			 * Load the background image
			 * NOTE: Months start at 0, not 1
			 */
			Calendar cal = Calendar.getInstance();
			if(cal.get(Calendar.MONTH) == 1) {
					if(cal.get(Calendar.DAY_OF_MONTH) >= 7
							&& cal.get(Calendar.DAY_OF_MONTH) <= 14) {
						/* Valentines day! */
						f = new FileInputStream(respath+"res/pokenet_valentines.png");
					} else {
						f = new FileInputStream(respath+"res/pokenet_venonat.png");
					}
			} else if(cal.get(Calendar.MONTH) == 2 
					&& cal.get(Calendar.DAY_OF_MONTH) > 14) {
				/* If second half of March, show Easter login */
				f = new FileInputStream(respath+"res/pokenet_easter.png");
			} else if(cal.get(Calendar.MONTH) == 3 
					&& cal.get(Calendar.DAY_OF_MONTH) < 26) {
				/* If before April 26, show Easter login */
				f = new FileInputStream(respath+"res/pokenet_easter.png");
			} else if(cal.get(Calendar.MONTH) == 9) {
				/* Halloween */
				f = new FileInputStream(respath+"res/pokenet_halloween.png");
			} else if(cal.get(Calendar.MONTH) == 11) {
				/* Christmas! */
				f = new FileInputStream(respath+"res/pokenet_xmas.png");
			} else if(cal.get(Calendar.MONTH) == 0) {
				/* January - Venonat Time! */
				f = new FileInputStream(respath+"res/pokenet_venonat.png");
			} else if(cal.get(Calendar.MONTH) >= 5 
					&& cal.get(Calendar.MONTH) <= 7) {
				/* Summer login */
				f = new FileInputStream(respath+"res/pokenet_summer.png");
			} else {
				/* Show normal login screen */
				f = new FileInputStream(respath+"res/pokenet_normal.png");
			}
			m_bg = new Label(new Image(f, "bg", false));
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
			 * Create the language selector container
			 */
			m_lang = new LanguageDialog();
			this.add(m_lang);
			
			
			/*
			 * Create the login container
			 */
			m_login = new LoginDialog();
			this.add(m_login);
			
			m_register = new RegisterDialog();
			this.add(m_register);
			
			m_about = new AboutDialog();
			this.add(m_about);
			
			m_terms = new ToSDialog();
			this.add(m_terms);
			
			m_openAbout = new Button(translated.get(3));
			m_openAbout.setSize(64, 32);
			m_openAbout.setLocation(800 - 64 - 8, 8);
			m_openAbout.setVisible(false);
			m_openAbout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					showAbout();
				}
			});
			this.add(m_openAbout);
			
			m_openToS = new Button(translated.get(4));
			m_openToS.setSize(64, 32);
			m_openToS.setLocation(800 - 64 - 8, 40);
			m_openToS.setVisible(false);
			m_openToS.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					showToS();
				}
			});
			this.add(m_openToS);
			
			setClientRevision();
			
			m_serverRev = new Label("Server Version: ?");
			m_serverRev.setFont(GameClient.getFontSmall());
			m_serverRev.setForeground(new Color(255, 255, 255));
			m_serverRev.pack();
			m_serverRev.setLocation(m_clientRev.getX() + m_clientRev.getWidth() + 16, 
					m_clientRev.getY());
			m_serverRev.setVisible(true);
			this.add(m_serverRev);

			this.setLocation(0, 0);
			this.setSize(800, 600);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the server version to be displayed
	 * @param rev
	 */
	public void setServerRevision(String rev) {
		m_serverRev.setText("Server Version: r" + rev);
		m_serverRev.pack();
		m_serverRev.setLocation(m_clientRev.getX() + m_clientRev.getWidth() + 16, 
				m_clientRev.getY());
	}
	
	/**
	 * Displays client version (ThinClient Version) based on rev.txt
	 * If rev.txt is not found, ? is displayed
	 */
	private void setClientRevision() {
		String path = System.getProperty("res.path");
		if (path == null || path.equalsIgnoreCase("NULL")) {
			path = "./";
		}
		File f = new File(path + "/.svn/entries");

		try {
			if (f.exists()) {
				Scanner s = new Scanner(f);
				s.nextLine();
				s.nextLine();
				s.nextLine();

				m_clientRev = new Label("Client Version: svn:" + s.nextLine());
				f = new File(path + "rev.txt");
				s = new Scanner(f);
				m_clientRev.setText(m_clientRev.getText() + " // rev:" + s.nextLine());
				
			} else {
				f = new File(path + "rev.txt");
				Scanner s;
				s = new Scanner(f);

				m_clientRev = new Label("Client Version: r" + s.nextLine());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			m_clientRev = new Label("Client Version: ?");
		}
		
		m_clientRev.setFont(GameClient.getFontSmall());
		m_clientRev.setForeground(new Color(255, 255, 255));
		m_clientRev.pack();
		m_clientRev.setLocation(4, 600 - m_clientRev.getHeight() - 8);
		this.add(m_clientRev);

	}

	/**
	 * Shows the login dialog
	 */
	public void showLogin() {
		m_login.reloadStrings();
		m_select.setVisible(false);
		m_register.setVisible(false);
		m_login.setVisible(true);
		m_openAbout.setVisible(true);
		m_openToS.setVisible(true);
		m_login.getLoginButton().setEnabled(true);
		m_lang.setVisible(false);
	}
	
	/**
	 * Shows the registration dialog
	 */
	public void showRegistration() {
		m_select.setVisible(false);
		m_login.setVisible(false);
		m_openAbout.setVisible(true);
		m_openToS.setVisible(true);
		m_lang.setVisible(false);
		m_register.reloadStrings();
		m_register.setVisible(true);
		m_register.grabFocus();
	}
	
	/**
	 * Shows the server selection dialog
	 */
	public void showServerSelect() {
		m_register.setVisible(false);
		m_login.setVisible(false);
		m_select.reloadStrings();
		m_select.setVisible(true);
		m_openAbout.setVisible(false);
		m_openToS.setVisible(false);
		m_lang.setVisible(false);
	}
	
	/**
	 * Shows the server selection dialog
	 */
	public void showLanguageSelect() {
		m_register.setVisible(false);
		m_login.setVisible(false);
		m_select.setVisible(false);
		m_lang.setVisible(true);
		m_openAbout.setVisible(false);
		m_openToS.setVisible(false);
	}
	
	/**
	 * Shows about dialog
	 */
	public void showAbout() {
		m_about.reloadStrings();
		m_about.setVisible(true);
	}
	
	/**
	 * Shows the terms of service dialog
	 */
	public void showToS() {
		m_terms.reloadStrings();
		m_terms.setVisible(true);
	}
	
	/**
	 * Enables the login button
	 */
	public void enableLogin() {
		m_login.getLoginButton().setEnabled(true);
	}
	
	 /**
     * Returns the register screen
     * @return
     */
	public RegisterDialog getRegistration() {
		return m_register;
	}
	/**
	 * Logs the user with current user and pass, this way they don't have to click "Login". 
	 * @return
	 */
	public void enterKeyDefault() {
		if (!m_lang.isVisible()){
			if(m_select.isVisible()){
				m_select.goServer();
			}else{
				m_login.goLogin();
			}
		}
	}
	
	/**
     * Tabs on Login for easy login. Redundant?
     * @return
     */
	public void tabKeyDefault() {
		if(m_register.isActive()){
			m_register.goToNext();
		}else{
			m_login.goToPass();
		}
	}
}
