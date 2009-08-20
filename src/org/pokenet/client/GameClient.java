package org.pokenet.client;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import mdes.slick.sui.Container;
import mdes.slick.sui.Display;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.muffin.FileMuffin;
import org.pokenet.client.backend.Animator;
import org.pokenet.client.backend.BattleManager;
import org.pokenet.client.backend.ClientMap;
import org.pokenet.client.backend.ClientMapMatrix;
import org.pokenet.client.backend.ItemDatabase;
import org.pokenet.client.backend.MoveLearningManager;
import org.pokenet.client.backend.SoundManager;
import org.pokenet.client.backend.entity.OurPlayer;
import org.pokenet.client.backend.entity.Player;
import org.pokenet.client.backend.entity.Player.Direction;
import org.pokenet.client.backend.time.TimeService;
import org.pokenet.client.backend.time.WeatherService;
import org.pokenet.client.backend.time.WeatherService.Weather;
import org.pokenet.client.network.PacketGenerator;
import org.pokenet.client.network.TcpProtocolHandler;
import org.pokenet.client.network.UdpProtocolHandler;
import org.pokenet.client.ui.LoadingScreen;
import org.pokenet.client.ui.LoginScreen;
import org.pokenet.client.ui.Ui;
import org.pokenet.client.ui.base.ConfirmationDialog;
import org.pokenet.client.ui.base.MessageDialog;
import org.pokenet.client.ui.frames.PlayerPopupDialog;

/**
 * The game client
 * @author shadowkanji
 * @author ZombieBear
 * @author Nushio
 *
 */
@SuppressWarnings("unchecked")
public class GameClient extends BasicGame {
	//Some variables needed
	private static GameClient m_instance;
	private static AppGameContainer gc;
	private ClientMapMatrix m_mapMatrix;
	private OurPlayer m_ourPlayer = null;
	private boolean m_isNewMap = false;
	private int m_mapX, m_mapY, m_playerId;
	private PacketGenerator m_packetGen;
	private Animator m_animator;
	private static HashMap<String, String> options;
	//Static variables
	private static Font m_fontLarge, m_fontSmall, m_trueTypeFont;
	private static String m_host;
	//UI
	private LoadingScreen m_loading;
	private LoginScreen m_login;
	//The gui display layer
	private Display m_display;

	private WeatherService m_weather;
	private TimeService m_time;
	private Ui m_ui;
	private Color m_daylight;
	private static String m_language = "";
	private ConfirmationDialog m_confirm;
	private PlayerPopupDialog m_playerDialog;
	private MoveLearningManager m_moveLearningManager;
    private static SoundManager m_soundPlayer;
    private static boolean m_disableMaps = false;
    public static String UDPCODE = "";
    
	private boolean m_close = false; //Used to tell the game to close or not. 
	/**
	 * Load options
	 */
	static {
		try {
			options = new FileMuffin().loadFile("options.dat");
			if (options == null) {
				options = new HashMap<String,String>();
				options.put("soundMuted", String.valueOf(true));
				options.put("disableMaps", String.valueOf(false));
				options.put("disableWeather", String.valueOf(false));
			}
			m_instance = new GameClient("Pokenet: Valiant Venonat");
			m_soundPlayer = new SoundManager();
			m_soundPlayer.mute(Boolean.parseBoolean(options.get("soundMuted")));
			m_soundPlayer.start();
			m_soundPlayer.setTrack("introandgym");
			m_disableMaps = Boolean.parseBoolean(options.get("disableMaps"));
		} catch (Exception e) { 
			e.printStackTrace();
			m_instance = new GameClient("Pokenet: Valiant Venonat");
			m_disableMaps = false;
			m_soundPlayer = new SoundManager();
			m_soundPlayer.mute(false);
			m_soundPlayer.start();
			m_soundPlayer.setTrack("introandgym");
		}
	}

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
	@SuppressWarnings("deprecation")
	@Override
	public void init(GameContainer gc) throws SlickException {
		m_instance = this;
		gc.getGraphics().setWorldClip(-32, -32, 832, 832);
		gc.setShowFPS(false);
		m_display = new Display(gc);
		InputStream f;
		InputStream fi;
		
		/*
		 * Setup variables
		 */
		f = getClass().getResourceAsStream("/res/fonts/dp.fnt");
		fi = getClass().getResourceAsStream("/res/fonts/dp.png");
		m_fontLarge = new AngelCodeFont("/res/fonts/dp.fnt", f, fi);
		f = getClass().getResourceAsStream("/res/fonts/dp-small.fnt");
		fi = getClass().getResourceAsStream("/res/fonts/dp-small.png");
		m_fontSmall = new AngelCodeFont("/res/fonts/dp-small.fnt", f, fi);
		Player.loadSpriteFactory();
		f = getClass().getResourceAsStream("/res/fonts/PokeFont.ttf");
		try {
			m_trueTypeFont = new TrueTypeFont(java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, f)
					.deriveFont(java.awt.Font.PLAIN, 24), false);
		} catch (Exception e) { e.printStackTrace(); m_trueTypeFont = m_fontSmall;}
		
		/*
		 * Time/Weather Services
		 */
		m_time = new TimeService();
		m_weather = new WeatherService();
		if(options != null)
			m_weather.setEnabled(!Boolean.parseBoolean(options.get("disableWeather")));
		
		/*
		 * Add the ui components
		 */
		m_loading = new LoadingScreen();
		m_display.add(m_loading);
		
		m_login = new LoginScreen();
		m_login.showLanguageSelect();
		m_display.add(m_login);
		
		m_ui = new Ui(m_display);
		m_ui.setAllVisible(false);

		/*
		 * Item DB
		 */
		ItemDatabase m_itemdb = new ItemDatabase();
		m_itemdb.reinitialise();
		
		/*
		 * Move Leraning Manager
		 */
		m_moveLearningManager = new MoveLearningManager();
		m_moveLearningManager.start();
		
		/*
		 * The animator and map matrix
		 */
		m_mapMatrix = new ClientMapMatrix();
		m_animator = new Animator(m_mapMatrix);
		
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
				try{
					m_display.update(gc, delta);
				} catch (Exception e) {}
			}
		} catch (Exception e) { e.printStackTrace(); }
		/*
		 * Check if language was chosen.
		 */
		if(m_language != null && !m_language.equalsIgnoreCase("") && ((m_host != null && m_host.equalsIgnoreCase("")) || m_packetGen == null)){
			m_login.showServerSelect();
		} else if(m_language == null || m_language.equalsIgnoreCase("")){
			m_login.showLanguageSelect();
		}
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
			m_mapMatrix.getCurrentMap().setName(m_mapMatrix.getMapName(m_mapX, m_mapY));
			m_mapMatrix.getCurrentMap().setXOffset(400 - m_ourPlayer.getX(), false);
			m_mapMatrix.getCurrentMap().setYOffset(300 - m_ourPlayer.getY(), false);
			m_mapMatrix.recalibrate();
			m_ui.getMap().setPlayerLocation();
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
		 * Update weather and daylight
		 */
		if(!m_isNewMap) {
			int a = 0;
			//Daylight
			m_time.updateDaylight();
			a = m_time.getDaylight();
			//Weather
			if(m_weather.isEnabled() && m_weather.getWeather() != Weather.NORMAL) {
				try {
					m_weather.getParticleSystem().update(delta);
				} catch (Exception e) {
					m_weather.setEnabled(false);
				}
				a = a < 100 ? a + 60 : a;
			}
			m_daylight = new Color(0, 0, 0, a);
		}
	}

	/**
	 * Renders to the game window
	 */
	public void render(GameContainer gc, Graphics g) throws SlickException {
		/* Clip the screen, no need to render what we're not seeing */
		g.setWorldClip(-32, -32, 864, 664);
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
            			thisMap.render(thisMap.getXOffset() / 2,
            					thisMap.getYOffset() / 2, 0, 0,
            					(gc.getScreenWidth() - thisMap.getXOffset()) / 32,
            					(gc.getScreenHeight() - thisMap.getYOffset()) / 32,
            					false);
            		}
            	}
            }
            g.resetTransform();
            try {
            	m_mapMatrix.getCurrentMap().renderTop(g);
            }catch (ConcurrentModificationException e){
            	m_mapMatrix.getCurrentMap().renderTop(g);
            }

           	if(m_mapX > -30) {
                //Render the current weather
                if(m_weather.isEnabled() && m_weather.getParticleSystem() != null) {
                	try {
                      	m_weather.getParticleSystem().render();
                	} catch(Exception e) {
                		m_weather.setEnabled(false);
                	}
                }
                //Render the current daylight
                if(m_time.getDaylight() > 0 || 
                		(m_weather.getWeather() != Weather.NORMAL && 
                				m_weather.getWeather() != Weather.SANDSTORM)) {
                	g.setColor(m_daylight);
                	g.fillRect(0, 0, 800, 600);
                }
            }
		}
		/*
		 * Render the UI layer
		 */
		try {
			synchronized(m_display) {
				try{
					m_display.render(gc, g);
				} catch (ConcurrentModificationException e){m_display.render(gc, g);}
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
		if (m_login.isVisible()){
			if (key == (Input.KEY_ENTER) || key == (Input.KEY_NUMPADENTER))
				m_login.enterKeyDefault();
			if (key == (Input.KEY_TAB))
				m_login.tabKeyDefault();
		}
		
		if (key == (Input.KEY_ESCAPE)) {
			if(m_confirm==null){
				ActionListener yes = new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							System.exit(0);
						} catch (Exception e) {
							e.printStackTrace();
						}
				
					}
				};
				ActionListener no = new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						m_confirm.setVisible(false);
						getDisplay().remove(m_confirm);
						m_confirm = null;
					}
				};
				m_confirm = new ConfirmationDialog("Are you sure you want to exit?",yes,no);
				getUi().getDisplay().add(m_confirm);
			}else{
				System.exit(0);
			}
		}
		if(m_ui.getNPCSpeech() == null && !m_ui.getChat().isActive() && !m_login.isVisible()
				&& !getDisplay().containsChild(m_playerDialog)){
			if(m_ourPlayer != null && !m_isNewMap
					/*&& m_loading != null && !m_loading.isVisible()*/
					&& m_ourPlayer.canMove()) {
				if (key == (Input.KEY_DOWN) || key == (Input.KEY_S)) {
					if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Down)) {
						m_packetGen.move(Direction.Down);
					} else if(m_ourPlayer.getDirection() != Direction.Down) {
						m_packetGen.move(Direction.Down);
					}
				} else if (key == (Input.KEY_UP) || key == (Input.KEY_W)) {
					if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Up)) {
						m_packetGen.move(Direction.Up);
					} else if(m_ourPlayer.getDirection() != Direction.Up) {
						m_packetGen.move(Direction.Up);
					}
				} else if (key == (Input.KEY_LEFT) || key == (Input.KEY_A)) {
					if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Left)) {
						m_packetGen.move(Direction.Left);
					} else if(m_ourPlayer.getDirection() != Direction.Left) {
						m_packetGen.move(Direction.Left);
					}
				} else if (key == (Input.KEY_RIGHT) || key == (Input.KEY_D)) {
					if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Right)) {
						m_packetGen.move(Direction.Right);
					} else if(m_ourPlayer.getDirection() != Direction.Right) {
						m_packetGen.move(Direction.Right);
					}
				} else if (key == (Input.KEY_1)) {
					m_ui.togglePokemon();
				} else if (key == (Input.KEY_2)) {
					m_ui.toggleBag();
				} else if (key == (Input.KEY_3)) {
					m_ui.toggleMap();
				} else if (key == (Input.KEY_4)) {
					m_ui.toggleFriends();
				} else if (key == (Input.KEY_5)) {
					m_ui.toggleRequests();
				} else if (key == (Input.KEY_6)) {
					m_ui.toggleOptions();
				} else if (key == (Input.KEY_7)) {
					m_ui.toggleHelp();
				}
			}
		}
		if ((key == (Input.KEY_SPACE) || key == (Input.KEY_E)) && !m_login.isVisible() &&
				!m_ui.getChat().isActive() && !getDisplay().containsChild(MoveLearningManager.getInstance()
						.getMoveLearning()) && !getDisplay().containsChild(getUi().getShop())) {
			if(m_ui.getNPCSpeech() == null && !getDisplay().containsChild(BattleManager.getInstance()
					.getBattleWindow()) ){
				m_packetGen.writeTcpMessage("Ct");
			}
			if (getDisplay().containsChild(BattleManager.getInstance().getBattleWindow()) && 
					 getDisplay().containsChild(BattleManager.getInstance().getTimeLine().getBattleSpeech())
					 && !getDisplay().containsChild(MoveLearningManager.getInstance().getMoveLearning())) {
				BattleManager.getInstance().getTimeLine().getBattleSpeech().advance();
			} else{
				try {
					m_ui.getNPCSpeech().advance();
				} catch (Exception e) { 
					m_ui.nullSpeechFrame();
//					m_packetGen.write("F"); 
				}
			}
		}
	}
	
	@Override
	public void controllerDownPressed(int controller){
		if(m_ui.getNPCSpeech() == null && m_ui.getChat().isActive()==false && !m_login.isVisible()
				&& !m_ui.getChat().isActive() && !getDisplay().containsChild(m_playerDialog)){
			if(m_ourPlayer != null && !m_isNewMap
					/*&& m_loading != null && !m_loading.isVisible()*/
					&& m_ourPlayer.canMove()) {
				if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Down)) {
					m_packetGen.move(Direction.Down);
				} else if(m_ourPlayer.getDirection() != Direction.Down) {
					m_packetGen.move(Direction.Down);
				}
			}
		}
	}

	@Override
	public void controllerUpPressed(int controller){
		if(m_ui.getNPCSpeech() == null && m_ui.getChat().isActive()==false && !m_login.isVisible()
				&& !m_ui.getChat().isActive() && !getDisplay().containsChild(m_playerDialog)){
			if(m_ourPlayer != null && !m_isNewMap
					/*&& m_loading != null && !m_loading.isVisible()*/
					&& m_ourPlayer.canMove()) {
				if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Up)) {
					m_packetGen.move(Direction.Up);
				} else if(m_ourPlayer.getDirection() != Direction.Up) {
					m_packetGen.move(Direction.Up);
				}
			}
		}
	}

	@Override
	public void controllerLeftPressed(int controller){
		if(m_ui.getNPCSpeech() == null && m_ui.getChat().isActive()==false && !m_login.isVisible()
				&& !m_ui.getChat().isActive() && !getDisplay().containsChild(m_playerDialog)){
			if(m_ourPlayer != null && !m_isNewMap
					/*&& m_loading != null && !m_loading.isVisible()*/
					&& m_ourPlayer.canMove()) {
				if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Left)) {
					m_packetGen.move(Direction.Left);
				} else if(m_ourPlayer.getDirection() != Direction.Left) {
					m_packetGen.move(Direction.Left);
				}
			}
		}
	}

	@Override
	public void controllerRightPressed(int controller){
		if(m_ui.getNPCSpeech() == null && m_ui.getChat().isActive()==false && !m_login.isVisible()
				&& !m_ui.getChat().isActive() && !getDisplay().containsChild(m_playerDialog)){
			if(m_ourPlayer != null && !m_isNewMap
					/*&& m_loading != null && !m_loading.isVisible()*/
					&& m_ourPlayer.canMove()) {
				if(!m_mapMatrix.getCurrentMap().isColliding(m_ourPlayer, Direction.Right)) {
					m_packetGen.move(Direction.Right);
				} else if(m_ourPlayer.getDirection() != Direction.Right) {
					m_packetGen.move(Direction.Right);
				}
			}
		}
	}

	/**
	 * Accepts the mouse input
	 */
	@Override
	public void mousePressed(int button, int x, int y) {
		// Right Click
        if (button == 1) {
        	// loop through the players and look for one that's in the
        	// place where the user just right-clicked
        	for (Player p : m_mapMatrix.getPlayers()) {
        		if ((x >= p.getX() + m_mapMatrix.getCurrentMap().getXOffset() && x <= p.getX() + 32 + m_mapMatrix.getCurrentMap().getXOffset()) 
        				&& (y >= p.getY() + m_mapMatrix.getCurrentMap().getYOffset() && y <= p.getY() + 40 + m_mapMatrix.getCurrentMap().getYOffset())) {
        			// Brings up a popup menu with player options
        			if (!p.isOurPlayer()){
        				if (getDisplay().containsChild(m_playerDialog))
        					getDisplay().remove(m_playerDialog);
        				m_playerDialog = new PlayerPopupDialog(p.getUsername());
        				m_playerDialog.setLocation(x, y);
        				getDisplay().add(m_playerDialog);
        			}
        		}
        	}
        }
        //Left click
        if (button == 0){
        	//Get rid of the popup if you click outside of it
        	if (getDisplay().containsChild(m_playerDialog)){
        		if (x > m_playerDialog.getAbsoluteX() || x < m_playerDialog.getAbsoluteX()
        				+ m_playerDialog.getWidth()){
        			m_playerDialog.destroy();
        		} else if (y > m_playerDialog.getAbsoluteY() || y < m_playerDialog.getAbsoluteY() 
        				+ m_playerDialog.getHeight()){
        			m_playerDialog.destroy();
        		}
        	} 
        	if(getDisplay().containsChild(m_ui.getChat())){
        		m_ui.getChat().dropFocus();
        	}
        }
	}

	
	/**
	 * Connects to a selected server
	 */
	public void connect() {
        m_packetGen = new PacketGenerator();
		/*
		 * Connect via TCP
		 */
		NioSocketConnector connector = new NioSocketConnector();
		connector.getFilterChain().addLast("codec",
	              new ProtocolCodecFilter(
	                      new TextLineCodecFactory(Charset.forName("US-ASCII"))));
		connector.setHandler(new TcpProtocolHandler(this));
		ConnectFuture cf = connector.connect(new InetSocketAddress(m_host, 7002));
		cf.addListener(new IoFutureListener() {
			public void operationComplete(IoFuture s) {
				try {
					if(s.getSession() != null && s.getSession().isConnected()) {
						m_packetGen.setTcpSession(s.getSession());
					} else {
						messageDialog("Connection timed out.\n"
								+ "The server may be offline.\n"
								+ "Contact an administrator for assistance.", getDisplay());
						m_host = "";
					}
				} catch (Exception e) {
					e.printStackTrace();
					messageDialog("Unexpected exception.", getDisplay());
					m_host = "";
				}
			}
		});
        /*
         * Connect via UDP
         */
        NioDatagramConnector udp = new NioDatagramConnector();
        udp.getFilterChain().addLast("codec",
	              new ProtocolCodecFilter(
	                      new TextLineCodecFactory(Charset.forName("US-ASCII"))));
        udp.setHandler(new UdpProtocolHandler(this));
        cf = udp.connect(new InetSocketAddress(m_host, 7005));
        cf.addListener(new IoFutureListener() {
        	public void operationComplete(IoFuture s) {
        		try {
    				if(s.getSession().isConnected()) {
    					m_packetGen.setUdpSession(s.getSession());
    				} else {
    					messageDialog("Connection timed out.\n"
    							+ "The server may be offline.\n"
    							+ "Contact an administrator for assistance.", getDisplay());
    					m_host = "";
    				}
        		} catch (Exception e) {
        			e.printStackTrace();
					messageDialog("Unexpected exception.", getDisplay());
					m_host = "";
        		}
			}
		});
        /*
         * Show login screen
         */
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
		boolean fullscreen = false;
		try {
			fullscreen = Boolean.parseBoolean(options.get("fullScreen"));
		} catch (Exception e) {
			fullscreen = false;
		}
		try {
			gc = new AppGameContainer(new GameClient("Pokenet: Valiant Venonat"),
					800, 600, fullscreen);
			gc.setTargetFrameRate(50);
			gc.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * When the close button is pressed... 
	 * @param args
	 */
	public boolean closeRequested(){
		if (m_confirm == null){
			ActionListener yes = new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						System.exit(0);
						m_close = true;
					} catch (Exception e) {
						e.printStackTrace();
						m_close = true;
					}
				}
			};
			ActionListener no = new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					m_confirm.setVisible(false);
					getDisplay().remove(m_confirm);
					m_confirm = null;
					m_close = false;
				}
			};
			m_confirm = new ConfirmationDialog("Are you sure you want to exit?",yes,no);
			getUi().getDisplay().add(m_confirm);
		}		
		return m_close;
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
	
	public static Font getTrueTypeFont() {
		return m_trueTypeFont;
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
	 * Returns the weather service
	 * @return
	 */
	public WeatherService getWeatherService() {
		return m_weather;
	}
	
	/**
	 * Returns the time service
	 * @return
	 */
	public TimeService getTimeService() {
		return m_time;
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
	 * Resets the client back to the z
	 */
	public void reset() {
		m_packetGen = null;
		m_host = "";
		try {
			if(BattleManager.getInstance() != null)
				BattleManager.getInstance().endBattle();
			if(this.getUi().getNPCSpeech() != null)
				this.getUi().getNPCSpeech().setVisible(false);
			if(this.getUi().getChat() != null)
				this.getUi().getChat().setVisible(false);
			this.getUi().setVisible(false);
		} catch (Exception e) {}
		m_login.setVisible(true);
		m_login.showLanguageSelect();
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
		m_ui.getReqWindow().clearOffers();
		m_soundPlayer.setTrackByLocation(m_mapMatrix.getMapName(x, y));
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
	
	/**
	 * Returns the user interface
	 */
	public Ui getUi() {
		return m_ui;
	}
	
	/**
	 * Returns the options
	 */
    public static HashMap<String, String> getOptions() {
        return options;
    }

    /**
     * Reloads options
     */
    public static void reloadOptions() {
        try {
        options = new FileMuffin().loadFile("options.dat");
        if (options == null) options = new HashMap<String,String>();
        } catch (Exception e) {
                e.printStackTrace();
                System.exit(32);
        }
    }
    
    /**
     * Returns the sound player
     * @return
     */
    public static SoundManager getSoundPlayer() {
        return m_soundPlayer;
    }

    
    /**
     * Creates a message Box
     */
    public static void messageDialog(String message, Container container) {
        new MessageDialog(message.replace('~','\n'), container);
    }
    
    /**
     * Returns the display
     */
    public Display getDisplay(){
    	return m_display;
    }
    
    /**
     * Returns the language selection
     * @return
     */
    public static String getLanguage() {
    	return m_language;
    }
    /**
     * Sets the language selection
     * @return
     */
    public static String setLanguage(String lang) {
    	m_language = lang;
    	return m_language;
    }
    
    /**
     * Changes the playing track
     * @param fileKey
     */
	public static void changeTrack(String fileKey){
		m_soundPlayer.setTrack(fileKey);
	}
	
	/**
	 * Returns false if the user has disabled surrounding map loading
	 * @return
	 */
	public static boolean disableMaps() {
		return m_disableMaps;
	}
	
	/**
	 * Sets if the client should load surrounding maps
	 * @param b
	 */
	public static void setDisableMaps(boolean b) {
		m_disableMaps = b;
	}
    
    /**
    * Slick Native library finder.
    */
    /*static {
		String s = File.separator;
      	// Modify this to point to the location of the native libraries.
      	String newLibPath = System.getProperty("user.dir") + s + "lib" + s + "native";
      	System.setProperty("java.library.path", newLibPath);

      	Field fieldSysPath = null;
      	try {
        	fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
      	} catch (SecurityException e) {
        	e.printStackTrace();
      	} catch (NoSuchFieldException e) {
        	e.printStackTrace();
      	}

      	if (fieldSysPath != null) {
        	try {
          		fieldSysPath.setAccessible(true);
          		fieldSysPath.set(System.class.getClassLoader(), null);
        	} catch (IllegalArgumentException e) {
          		e.printStackTrace();
        	} catch (IllegalAccessException e) {
          		e.printStackTrace();
        	}
      	}
    }*/
}
