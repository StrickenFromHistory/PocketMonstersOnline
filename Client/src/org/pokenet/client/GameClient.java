package org.pokenet.client;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import mdes.slick.sui.Display;
import mdes.slick.sui.Sui;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.pokenet.client.backend.Animator;
import org.pokenet.client.backend.ClientMap;
import org.pokenet.client.backend.ClientMapMatrix;
import org.pokenet.client.backend.entity.OurPlayer;
import org.pokenet.client.backend.entity.Player;
import org.pokenet.client.backend.entity.Player.Direction;
import org.pokenet.client.network.ConnectionManager;
import org.pokenet.client.network.PacketGenerator;
import org.pokenet.client.ui.LoadingScreen;
import org.pokenet.client.ui.LoginScreen;

/**
 * The game client
 * @author shadowkanji
 *
 */
public class GameClient extends BasicGame {
	//Some variables needed
	private static GameClient m_instance;
	private ClientMapMatrix m_mapMatrix;
	private OurPlayer m_ourPlayer = null;
	private boolean m_isNewMap = false;
	private int m_mapX, m_mapY, m_playerId;
	private PacketGenerator m_packetGen;
	private Animator m_animator;
	//Static variables
	private static Font m_fontLarge, m_fontSmall;
	private static String m_host;
	//UI
	private LoadingScreen m_loading;
	private LoginScreen m_login;
	//The gui display layer
	private Display m_display;
	private Font m_dpFontLarge, m_dpFontSmall;
	
	/**
	 * Default constructor
	 * @param title
	 */
	public GameClient(String title) {
		super(title);
	}

	/**
	 * Called before the window is created
	 */
	@Override
	public void init(GameContainer gc) throws SlickException {
		gc.setShowFPS(false);
		m_display = new Display(gc);
		
		/*
		 * Setup variables
		 */
		m_fontLarge = new AngelCodeFont("res/fonts/dp.fnt",
		"res/fonts/dp.png");	
		m_fontSmall = new AngelCodeFont("res/fonts/dp-small.fnt",
		"res/fonts/dp-small.png");
		Player.loadSpriteFactory();
		
		/*
		 * Add the ui components
		 */
		m_loading = new LoadingScreen();
		m_display.add(m_loading);
		
		m_login = new LoginScreen();
		m_display.add(m_login);
		
		/*
		 * The animator and map matrix
		 */
		m_mapMatrix = new ClientMapMatrix();
		m_animator = new Animator(m_mapMatrix);
		
		m_instance = this;
		gc.getInput().enableKeyRepeat(50, 300);
	}

	/**
	 * Updates the game window
	 */
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		/*
		 * Update the gui layer
		 */
		try {
			synchronized (m_display) {
				m_display.update(gc, delta);
			}
		} catch (Exception e) { e.printStackTrace(); }
		/*
		 * Check if we need to connect to a selected server
		 */
		if(m_host != null && !m_host.equalsIgnoreCase("") && m_packetGen == null) {
			this.connect();
		}
		/*
		 * Check if we need to loads maps
		 */
		if(m_isNewMap && m_loading.isVisible()) {
			m_mapMatrix.loadMaps(m_mapX, m_mapY, gc.getGraphics());
			while(m_ourPlayer == null);
			m_mapMatrix.getCurrentMap().setXOffset(400 - m_ourPlayer.getX(), false);
			m_mapMatrix.getCurrentMap().setYOffset(300 - m_ourPlayer.getY(), false);
			m_mapMatrix.recalibrate();
			m_isNewMap = false;
			m_loading.setVisible(false);
		}
		/*
		 * Animate the player
		 */
		if(m_ourPlayer != null) {
			m_animator.animate();
		}
		/*
		 * Check if we need to update daylight
		 */
	}

	/**
	 * Renders to the game window
	 */
	public void render(GameContainer gc, Graphics g) throws SlickException {
		/*
		 * If the player is playing, run this rendering algorithm for maps.
		 * The uniqueness here is:
		 *  For the current map it only renders line by line for the layer that the player's are on, 
		 *  other layers are rendered directly to the screen.
		 *  All other maps are simply rendered directly to the screen.
		 */
		if(!m_isNewMap && m_ourPlayer != null) {
			ClientMap thisMap;
			g.setFont(m_fontLarge);
			g.scale(2, 2);
            for (int x = 0; x <= 2; x++) {
                     for (int y = 0; y <= 2; y++) {
                    		 thisMap = m_mapMatrix.getMap(x, y);
                             if (thisMap != null && thisMap.isRendering()) {
                            	 if(!thisMap.isCurrent())
                        			 thisMap.render(thisMap.getXOffset() / 2,
                                             thisMap.getYOffset() / 2, 0, 0,
                                             (gc.getScreenWidth() - thisMap.getXOffset()) / 32,
                                             (gc.getScreenHeight() - thisMap.getYOffset()) / 32,
                                             false);
                        		 else {
                                	 for(int l = 0; l < thisMap.getLayerCount(); l++) {
                                		 thisMap.render(thisMap.getXOffset() / 2,
                                                 thisMap.getYOffset() / 2, 0, 0,
                                                 (gc.getScreenWidth() - thisMap.getXOffset()) / 32,
                                                 (gc.getScreenHeight() - thisMap.getYOffset()) / 32,
                                                 l, thisMap.getLastLayerRendered() + 1 == thisMap.getWalkableLayer());
                            			 thisMap.setLastLayerRendered(l); 
                                	 }
                        		 }
                             }
                            	 
                     }
            }
            g.resetTransform();
		}
		/*
		 * Update the UI layer
		 */
		try {
			synchronized(m_display) {
				m_display.render(gc, g);
			}
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * Accepts the user input.
	 * @param key The integer representing the key pressed.
	 * @param c ???
	 */
	@Override
	public void keyPressed(int key, char c) {
		if (key == (Input.KEY_ESCAPE)) {
			try {
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(m_ourPlayer != null && !m_isNewMap
				/*&& m_loading != null && !m_loading.isVisible()*/
				&& m_ourPlayer.getX() == m_ourPlayer.getServerX()
				&& m_ourPlayer.getY() == m_ourPlayer.getServerY()) {
			if (key == (Input.KEY_DOWN)) {
				if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Down)) {
					m_packetGen.move(Direction.Down);
				} else if(m_ourPlayer.getDirection() != Direction.Down) {
					m_packetGen.move(Direction.Down);
				}
			} else if (key == (Input.KEY_UP)) {
				if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Up)) {
					m_packetGen.move(Direction.Up);
				} else if(m_ourPlayer.getDirection() != Direction.Up) {
					m_packetGen.move(Direction.Up);
				}
			} else if (key == (Input.KEY_LEFT)) {
				if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Left)) {
					m_packetGen.move(Direction.Left);
				} else if(m_ourPlayer.getDirection() != Direction.Left) {
					m_packetGen.move(Direction.Left);
				}
			} else if (key == (Input.KEY_RIGHT)) {
				if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Right)) {
					m_packetGen.move(Direction.Right);
				} else if(m_ourPlayer.getDirection() != Direction.Right) {
					m_packetGen.move(Direction.Right);
				}
			}
		}
	}
	
	/**
	 * Connects to a selected server
	 */
	public void connect() {
		SocketConnector connector = new SocketConnector();
        SocketConnectorConfig cfg = new SocketConnectorConfig();
        ((SocketSessionConfig) cfg.getSessionConfig()).setTcpNoDelay(true);
        cfg.getFilterChain().addLast(
              "codec",
              new ProtocolCodecFilter(
                      new TextLineCodecFactory(Charset.forName("US-ASCII"))));
        cfg.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors
				.newCachedThreadPool()));
        // Start communication.
       ConnectFuture cf = connector.connect(new InetSocketAddress(
                m_host, 3128), new ConnectionManager(this), cfg);
        // Wait for the connection attempt to be finished
        cf.join();
        int i = 0;
        while(!cf.isConnected()) {
        	i++;
        	//Connection attempt times out and a dialog appears
        	if(i >= 10000) {
        		JOptionPane.showMessageDialog(null,
						"Connection timed out.\n"
						+ "The server may be offline.\n"
						+ "Contact an administrator for assistance.");
				m_host = "";
				return;
        	}
        }
        m_packetGen = new PacketGenerator(cf.getSession());
        m_login.showLogin();
	}
	
	/**
	 * Returns the map matrix
	 * @return
	 */
	public ClientMapMatrix getMapMatrix() {
		return m_mapMatrix;
	}
	
	/**
	 * If you don't know what this does, you shouldn't be programming!
	 * @param args
	 */
	public static void main(String [] args) {
		try {
			AppGameContainer gc = new AppGameContainer(new GameClient("Pokenet: Fearless Feebas"), 800, 600, false);
			gc.setTargetFrameRate(50);
			gc.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the font in large
	 * @return
	 */
	public static Font getFontLarge() {
		return m_fontLarge;
	}
	
	/**
	 * Returns the font in small
	 * @return
	 */
	public static Font getFontSmall() {
		return m_fontSmall;
	}
	
	/**
	 * Sets the server host. The server will connect once m_host is not equal to ""
	 * @param s
	 */
	public static void setHost(String s) {
		m_host = s;
	}
	
	/**
	 * Returns this instance of game client
	 * @return
	 */
	public static GameClient getInstance() {
		return m_instance;
	}
	
	/**
	 * Returns the packet generator
	 * @return
	 */
	public PacketGenerator getPacketGenerator() {
		return m_packetGen;
	}
	
	/**
	 * Returns the login screen
	 * @return
	 */
	public LoginScreen getLoginScreen() {
		return m_login;
	}
	
	/**
	 * Returns the loading screen
	 * @return
	 */
	public LoadingScreen getLoadingScreen() {
		return m_loading;
	}
	
	/**
	 * Stores the player's id
	 * @param id
	 */
	public void setPlayerId(int id) {
		m_playerId = id;
	}
	
	/**
	 * Returns this player's id
	 * @return
	 */
	public int getPlayerId() {
		return m_playerId;
	}
	
	/**
	 * Resets the client back to the start
	 */
	public void reset() {
		m_packetGen = null;
		m_host = "";
		m_login.setVisible(true);
		m_login.showServerSelect();
	}
	
	/**
	 * Sets the map and loads them on next update() call
	 * @param x
	 * @param y
	 */
	public void setMap(int x, int y) {
		m_mapX = x;
		m_mapY = y;
		m_isNewMap = true;
		m_loading.setVisible(true);
	}
	
	/**
	 * Returns our player
	 * @return
	 */
	public OurPlayer getOurPlayer() {
		return m_ourPlayer;
	}
	
	/**
	 * Sets our player
	 * @param pl
	 */
	public void setOurPlayer(OurPlayer pl) {
		m_ourPlayer = pl;
	}
}
