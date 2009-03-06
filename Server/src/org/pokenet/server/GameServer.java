package org.pokenet.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.pokenet.server.network.ConnectionManager;

/**
 * Represents a game server.
 * 
 * Starting a server requires a parameter to be passed in, i.e. java GameServer -low
 * Here are the different settings:
 * -low
 * 		< 1.86ghz
 * 		< 512MB Ram
 * 		< 1mbps Up/Down Connection
 * 		75 Playeys
 * -medium
 * 		< 2ghz
 * 		1GB Ram
 * 		1mbps Up/Down Connection
 * 		200 Players
 * -high
 * 		> 1.86ghz
 * 		> 1GB Ram
 * 		> 1mbps Up/Down Connection
 * 		> 500 Players
 * @author shadowkanji
 *
 */
public class GameServer extends JFrame {
	private static GameServer m_instance;
	private static final long serialVersionUID = 1L;
	private static ServiceManager m_serviceManager;
	private static int m_maxPlayers, m_movementThreads, m_battleThreads;
	private static String m_dbServer, m_dbName, m_dbUsername, m_dbPassword, m_serverName;
	private JTextField m_dbS, m_dbN, m_dbU, m_name;
	private JPasswordField m_dbP;
	private JButton m_start, m_stop, m_set, m_exit;
	private int m_highest;
	private JLabel m_pAmount, m_pHighest;
	
	/**
	 * Default constructor
	 */
	public GameServer() {
		super("Pokenet Server");
		this.setSize(148, 340);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(null);
		this.setResizable(false);
		this.setLocation(32, 32);
		
		/*
		 * Set up the buttons
		 */
		m_pAmount = new JLabel("0 players online");
		m_pAmount.setSize(160, 16);
		m_pAmount.setLocation(4, 4);
		m_pAmount.setVisible(true);
		this.getContentPane().add(m_pAmount);
		
		m_pHighest = new JLabel("[No record]");
		m_pHighest.setSize(160, 16);
		m_pHighest.setLocation(4, 24);
		m_pHighest.setVisible(true);
		this.getContentPane().add(m_pHighest);
		
		m_start = new JButton("Start Server");
		m_start.setSize(128, 24);
		m_start.setLocation(4, 48);
		m_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				start();
			}
		});
		this.getContentPane().add(m_start);
		
		m_stop = new JButton("Stop Server");
		m_stop.setSize(128, 24);
		m_stop.setLocation(4, 74);
		m_stop.setEnabled(false);
		m_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stop();
			}
		});
		this.getContentPane().add(m_stop);
		
		m_set = new JButton("Save Settings");
		m_set.setSize(128, 24);
		m_set.setLocation(4, 100);
		m_set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				m_dbServer = m_dbS.getText();
				m_dbName = m_dbN.getText();
				m_dbUsername = m_dbU.getText();
				m_dbPassword = new String(m_dbP.getPassword());
				m_serverName = m_name.getText();
			}
		});
		this.getContentPane().add(m_set);
		
		m_exit = new JButton("Quit");
		m_exit.setSize(128, 24);
		m_exit.setLocation(4, 290);
		m_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}
		});
		this.getContentPane().add(m_exit);
		
		/*
		 * Settings text boxes
		 */
		m_dbS = new JTextField();
		m_dbS.setSize(128, 24);
		m_dbS.setText("MySQL Host");
		m_dbS.setLocation(4, 128);
		this.getContentPane().add(m_dbS);
		
		m_dbN = new JTextField();
		m_dbN.setSize(128, 24);
		m_dbN.setText("MySQL Database Name");
		m_dbN.setLocation(4, 160);
		this.getContentPane().add(m_dbN);
		
		m_dbU = new JTextField();
		m_dbU.setSize(128, 24);
		m_dbU.setText("MySQL Username");
		m_dbU.setLocation(4, 192);
		this.getContentPane().add(m_dbU);
		
		m_dbP = new JPasswordField();
		m_dbP.setSize(128, 24);
		m_dbP.setText("Pass");
		m_dbP.setLocation(4, 224);
		this.getContentPane().add(m_dbP);
		
		m_name = new JTextField();
		m_name.setSize(128, 24);
		m_name.setText("Your Server Name");
		m_name.setLocation(4, 260);
		this.getContentPane().add(m_name);
		
		m_instance = this;
		this.setVisible(true);
	}
	
	/**
	 * Starts the game server
	 */
	public void start() {
		m_serviceManager = new ServiceManager();
		m_serviceManager.start();
		m_start.setEnabled(false);
		m_stop.setEnabled(true);
	}
	
	/**
	 * Stops the game server
	 */
	public void stop() {
		m_serviceManager.stop();
		m_start.setEnabled(true);
		m_stop.setEnabled(false);
	}
	
	/**
	 * Exits the game server application
	 */
	private void exit() {
		if(m_stop.isEnabled()) {
			JOptionPane.showMessageDialog(null, "You must stop the server before exiting.");
		} else {
			System.exit(0);
		}
	}
	
	/**
	 * Updates the player count information
	 * @param amount
	 */
	public void updatePlayerCount() {
		int amount = ConnectionManager.getPlayerCount();
		m_pAmount.setText(amount + " players online");
		if(amount > m_highest) {
			m_pHighest.setText("Highest: " + amount);
			m_highest = amount;
		}
	}
	
	/**
	 * Returns the instance of game server
	 * @return
	 */
	public static GameServer getInstance() {
		return m_instance;
	}
	
	/**
	 * If you don't know what this method does, you clearly don't know enough Java to be working on this.
	 * @param args
	 */
	public static void main(String [] args) {
		if(args.length > 0) {
			/*
			 * The following sets the server's settings based on the
			 * computing ability of the server specified by the server owner.
			 */
			if(args[0].equalsIgnoreCase("-low")) {
				m_maxPlayers = 75;
				m_movementThreads = 2;
				m_battleThreads = 2;
			} else if(args[0].equalsIgnoreCase("-medium")) {
				m_maxPlayers = 200;
				m_movementThreads = 4;
				m_battleThreads = 4;
			} else if(args[0].equalsIgnoreCase("-high")) {
				m_maxPlayers = 500;
				m_movementThreads = 8;
				m_battleThreads = 8;
			} else {
				System.err.println("Server requires a settings parameter, e.g. java GameServer -medium");
				System.exit(0);
			}
			GameServer gs = new GameServer();
		} else {
			System.err.println("Server requires a settings parameter, e.g. java GameServer -medium");
		}
	}
	
	/**
	 * Returns the service manager of the server
	 * @return
	 */
	public static ServiceManager getServiceManager() {
		return m_serviceManager;
	}
	
	/**
	 * Returns the amount of players this server will allow
	 * @return
	 */
	public static int getMaxPlayers() {
		return m_maxPlayers;
	}
	
	/**
	 * Returns the amount of battle threads running in this server
	 * @return
	 */
	public static int getBattleThreadAmount() {
		return m_battleThreads;
	}
	
	/**
	 * Returns the amount of movement threads running in this server
	 * @return
	 */
	public static int getMovementThreadAmount() {
		return m_movementThreads;
	}
	
	/**
	 * Returns the database host
	 * @return
	 */
	public static String getDatabaseHost() {
		return m_dbServer;
	}
	
	/**
	 * Returns the database username
	 * @return
	 */
	public static String getDatabaseUsername() {
		return m_dbUsername;
	}
	
	/**
	 * Returns the database password
	 * @return
	 */
	public static String getDatabasePassword() {
		return m_dbPassword;
	}
	
	/**
	 * Returns the name of this server
	 * @return
	 */
	public static String getServerName() {
		return m_serverName;
	}
	
	/**
	 * Returns the database selected
	 * @return
	 */
	public static String getDatabaseName() {
		return m_dbName;
	}
}
