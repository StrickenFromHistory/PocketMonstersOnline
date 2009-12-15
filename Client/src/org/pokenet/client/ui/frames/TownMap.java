package org.pokenet.client.ui.frames;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;

import org.lwjgl.util.Timer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.FileLoader;

/**
 * Town Map
 * @author ZombieBear
 *
 */
public class TownMap extends Frame {
	private Label m_map;
	private Label m_mapName;
	private HashMap<String, Container> m_containers;
	private List<String> m_locations;
	private Label m_playerLoc;
	private Timer m_timer;
	
	/**
	 * Default constructor
	 */
	public TownMap() {
		super("World Map");
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		m_mapName = new Label();
		m_playerLoc = new Label();
		m_timer = new Timer();
		
		LoadingList.setDeferredLoading(true);
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		try {
			m_map = new Label(new Image(respath+"res/ui/KantoandJohto.png", false));
		} catch (SlickException e) {}
		LoadingList.setDeferredLoading(false);

		m_map.setSize(534, 264);
		m_map.setLocation(0, 0);
		m_mapName.setFont(GameClient.getFontLarge());
		m_mapName.setForeground(Color.white);
		m_mapName.setX(10);

		add(m_map);
		add(m_mapName);

		setSize(536, 265 + getTitleBar().getHeight());
		getTitleBar().getCloseButton().setVisible(false);
		loadLocations();
		setResizable(false);
		setVisible(true);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void update(GUIContext container, int delta){
		super.update(container, delta);
		if (isVisible()){
			m_timer.tick();
			if (m_timer.getTime() >= 0.5) {
				if (m_playerLoc.isVisible())
					m_playerLoc.setVisible(false);
				else
					m_playerLoc.setVisible(true);
				m_timer.reset();
			}
		}
	}
	
	/**
	 * Reads the list of locations and adds them to the map
	 */
	public void loadLocations() {
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		try {
			BufferedReader reader;
			
			try{
				reader = new BufferedReader(new InputStreamReader(FileLoader.loadFile(respath+"res/language/" 
						+ GameClient.getLanguage() + "/UI/_MAP.txt")));
			} catch (Exception e){
				reader = new BufferedReader(new InputStreamReader(FileLoader.loadFile(respath+
						"res/language/english/UI/_MAP.txt")));
			}
			m_containers = new HashMap<String, Container>();
			m_locations = new ArrayList<String>();
			
			String f;
			while ((f = reader.readLine()) != null) {
				if (f.charAt(0) != '*'){
					final String[] details = f.split(",");
					m_locations.add(details[0]);
					Container m_surface = new Container();
					m_surface.setWidth(Integer.parseInt(details[1]));
					m_surface.setHeight(Integer.parseInt(details[2]));
					m_surface.setX(Integer.parseInt(details[3]) * 8);
					m_surface.setY(Integer.parseInt(details[4]) * 8);
					m_surface.addMouseListener(new MouseAdapter() {

						@Override
						public void mouseReleased(MouseEvent e) {
							super.mouseReleased(e);
						}

						@Override
						public void mouseEntered(MouseEvent e) {
							super.mouseEntered(e);
							m_mapName.setText(details[0]);
							m_mapName.pack();
						}

						@Override
						public void mouseExited(MouseEvent e) {
							super.mouseExited(e);
							m_mapName.setText("");
						}

					});
					m_containers.put(details[0], m_surface);
					add(m_containers.get(details[0]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to load locations");
		}
	}
	
	/**
	 * Set's the players current location
	 */
	public void setPlayerLocation() {
		try {
			remove(m_playerLoc);
			m_playerLoc = new Label();
		} catch (Exception e) {}
		String currentLoc = GameClient.getInstance().getMapMatrix().getCurrentMap().getName();
		m_playerLoc.setOpaque(true);
		m_playerLoc.setBackground(new Color(255, 0, 0, 130));
		try {
			m_playerLoc.setSize(m_containers.get(currentLoc).getSize());
			m_playerLoc.setLocation(m_containers.get(currentLoc).getLocation());
			m_playerLoc.setGlassPane(true);
			add(m_playerLoc);
		} catch (Exception e) {}
	}
}
