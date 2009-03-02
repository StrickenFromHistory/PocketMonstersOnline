package org.pokenet.client;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.pokenet.client.backend.ClientMap;
import org.pokenet.client.backend.ClientMapMatrix;
import org.pokenet.client.backend.entity.OurPlayer;
import org.pokenet.client.ui.LoadingScreen;

/**
 * The game client
 * @author shadowkanji
 *
 */
public class GameClient extends BasicGame {
	private ClientMapMatrix m_mapMatrix;
	private OurPlayer m_ourPlayer;
	private boolean m_isPlaying = false;
	private boolean m_isNewMap = false;
	private LoadingScreen m_loading;

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
		
		m_mapMatrix = new ClientMapMatrix();
	}

	/**
	 * Updates the game window
	 */
	@Override
	public void update(GameContainer gc, int delta) throws SlickException {
		if(m_isNewMap && m_isPlaying) {
			/*
			 * Load the maps
			 */
		}
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
		if(m_isPlaying && !m_isNewMap && m_ourPlayer != null) {
			ClientMap thisMap;
			//g.setFont(getDPFont());
			g.scale(2, 2);
            for (int x = 0; x <= 2; x++) {
                     for (int y = 0; y <= 2; y++) {
                    		 thisMap = m_mapMatrix.getMap(x, y);
                             if (thisMap != null && thisMap.isRendering()) {
                            	 if(!(x == 1 && y == 1))
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
			AppGameContainer gc = new AppGameContainer(new GameClient("Pokenet (Beta 1)"), 800, 600, false);
			gc.setTargetFrameRate(50);
			gc.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
