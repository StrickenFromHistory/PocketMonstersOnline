package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.CheckBox;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.TextField;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.PreferencesManager;
import org.pokenet.client.backend.Translator;

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
	private CheckBox remember;
	boolean isRemembered = false;
	private static final String DUMMY_PASS_TEXT = "********";
	
	private PreferencesManager prefs = PreferencesManager.getPreferencesManager();


	
	/**
	 * Default constructor
	 */
	public LoginDialog() {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		List<String> translated = Translator.translate("_LOGIN");
		this.setBorderRendered(false);
		this.getTitleBar().setVisible(false);
		this.setSize(320, 200);
		this.setLocation(480, 424);
		this.setBackground(new Color(0, 0, 0, 140));
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
		
		m_userLabel = new Label(translated.get(5));
		m_userLabel.pack();
		m_userLabel.setLocation(m_username.getX() - m_userLabel.getWidth() - 24, 12);
		m_userLabel.setVisible(true);
		m_userLabel.setFont(GameClient.getFontSmall());
		m_userLabel.setForeground(m_white);
		this.add(m_userLabel);
		
		m_passLabel = new Label(translated.get(6));
		m_passLabel.pack();
		m_passLabel.setLocation(m_userLabel.getX(), 40);
		m_passLabel.setVisible(true);
		m_passLabel.setFont(GameClient.getFontSmall());
		m_passLabel.setForeground(m_white);
		this.add(m_passLabel);
		
	
		// insert text into fields
		try{ isRemembered = prefs.getBoolForKey(prefs.REMEMBER_ME_KEY_NAME);
		} catch (NullPointerException e){ isRemembered = false;}
		if(isRemembered)
		{
			m_username.setText(prefs.getStringForKey(prefs.USER_KEY_NAME));
			m_password.setText(DUMMY_PASS_TEXT);
		}
		
		
		m_login = new Button(translated.get(7));
		m_login.setSize(64, 32);
		m_login.setLocation(m_password.getX(), m_password.getY() + m_password.getHeight() + 8);
		m_login.setVisible(true);
		m_login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(m_username.getText() != null & !m_username.getText().equalsIgnoreCase("") && 
						m_password.getText() != null && !m_password.getText().equalsIgnoreCase("")) {
					login();
				}
				
				if(remember.isSelected()) {
					prefs.setObjectForKey(m_username.getText(), prefs.USER_KEY_NAME);
					prefs.setObjectForKey(encryptPassword(), prefs.PASS_KEY_NAME);
				}
				
				prefs.setObjectForKey(remember.isSelected(), prefs.REMEMBER_ME_KEY_NAME);
			}

		});
		this.add(m_login);
		
		m_register = new Button(translated.get(8));
		m_register.setSize(64, 32);
		m_register.setLocation(m_login.getX() + m_login.getWidth() + 8, m_login.getY());
		m_register.setVisible(true);
		m_register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				register();
			}
		});
		this.add(m_register);
		
		// for remembering the username and password
		remember = new CheckBox();
		remember.setSize(120, 15); 
		remember.pack();
		remember.setLocation(m_login.getX(), m_login.getY() + m_login.getHeight() + 8);
		remember.setFont(GameClient.getFontSmall());
		remember.setForeground(m_white);
		remember.setVisible(true);
		remember.setSelected(isRemembered);
		this.add(remember);
		
		// add the text for the above check box
		Label rememberLabel = new Label("Remember Me?");
		rememberLabel.pack();
		rememberLabel.setLocation(remember.getX() + remember.getWidth() + 8, remember.getY() + 4);
		rememberLabel.setVisible(true);
		rememberLabel.setFont(GameClient.getFontSmall());
		rememberLabel.setForeground(m_white);
		this.add(rememberLabel);
		
		
		this.setVisible(false);
	}
	
	
	/**
	 * Sends login information to packet generator to be sent to server
	 */
	private void login() {
		m_login.setEnabled(false);
		GameClient.getInstance().getLoadingScreen().setVisible(true);
		GameClient.getInstance().getPacketGenerator().login(m_username.getText(), 
				(DUMMY_PASS_TEXT == m_password.getText()) ? decryptPassword() : m_password.getText());
	}
	
	/**
	 * using for locally storing password only
	 * @return
	 */
	private ArrayList<Integer> encryptPassword() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for(int i = 0; i < m_password.getText().length(); i++) {
			result.add( ((byte)m_password.getText().charAt(i) * 12 * (i + 1)));
		}
		return result;
	}
	
	/**
	 * using for locally storing password only
	 * @return
	 */
	private String decryptPassword() {
		String result = "";
		ArrayList<Integer> pass = prefs.getIntegerArrayListForKey(prefs.PASS_KEY_NAME);
		
		for (int i = 0; i < pass.size(); i++) {
			System.out.println(pass.get(i));
			result += (char)((pass.get(i) / 12 / (i+1)));
		}

		System.out.println(result);
		return result;
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
	
	/**
	 * Tab to pass
	 * @return
	 */
	public void goToPass() {
		if(m_username.hasFocus()){
			m_username.releaseFocus();
			m_password.grabFocus();	
		}else{
			m_password.releaseFocus();
			m_username.grabFocus();
		}
		
	}
	
	/**
	 * Enter to login
	 * @return
	 */
	public void goLogin() {
		if(m_username.getText() != null & !m_username.getText().equalsIgnoreCase("") && 
				m_password.getText() != null && !m_password.getText().equalsIgnoreCase(""))
		{
			login();		
		}
	}
	
	/**
	 * Reloads strings with language selected. 
	 */
	public void reloadStrings(){
		List<String> translated = Translator.translate("_LOGIN");
		m_userLabel.setText(translated.get(5));
		m_passLabel.setText(translated.get(6));
		m_login.setText(translated.get(7));
		m_register.setText(translated.get(8));
	}
}
