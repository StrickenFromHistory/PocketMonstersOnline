package org.pokenet.client.ui.frames;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;

/**
 * Town Map
 * @author ZombieBear
 *
 */
public class TownMap extends Frame {
	private Label m_map;
	private Label m_mapName;
	private HashMap<String, Container> m_buttons;
	private List<String> m_locations;
	
	/**
	 * Default constructor
	 */
	public TownMap() {
		super("World Map");
		m_mapName = new Label();
		LoadingList.setDeferredLoading(true);
		try {
			m_map = new Label(new Image("/res/ui/KantoandJohto.png"));
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
		loadLocations();
		setResizable(false);
		setVisible(true);
		setAlwaysOnTop(true);
		GameClient.getInstance().getDisplay().add(this);
	}
	
	/**
	 * Reads the list of locations and adds them to the map
	 */
	public void loadLocations() {
		try {
			Scanner reader;
			try{
				reader = new Scanner(new File("res/language/" + GameClient.getLanguage()
						+ "/UI/_MAP.txt"));
			} catch (Exception e){
				reader = new Scanner(new File("res/language/english/UI/_MAP.txt"));
			}
			m_buttons = new HashMap<String, Container>();
			m_locations = new ArrayList<String>();
			
			String f = null;
			while (reader.hasNext()) {
				f = reader.nextLine();
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
					m_buttons.put(details[0], m_surface);
					add(m_buttons.get(details[0]));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to load locations");
		}
	}
}
