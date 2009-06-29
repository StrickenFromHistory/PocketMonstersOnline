package org.pokenet.server.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Stores a list of sprites not buyable
 * Reads from /res/sprites.txt
 * @author shadowkanji
 *
 */
public class SpriteList {
	private ArrayList<Integer> m_sprites;
	
	/**
	 * Constructor
	 */
	public SpriteList() {
		m_sprites = new ArrayList<Integer>();
	}
	
	/**
	 * Returns the list of unbuyable sprites
	 * @return
	 */
	public ArrayList<Integer> getUnbuyableSprites() {
		return m_sprites;
	}
	
	/**
	 * Initialises the list
	 */
	public void initialise() {
		File f = new File("./res/sprites.txt");
		if(f.exists()) {
			try {
				Scanner s = new Scanner(f);
				while(s.hasNextLine()) {
					m_sprites.add(Integer.parseInt(s.nextLine()));
				}
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
