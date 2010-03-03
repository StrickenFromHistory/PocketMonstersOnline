package org.pokenet.client.backend;

import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.pokenet.client.backend.entity.Player.Direction;

/**
* Handles overworld sprites
* @author shinobi
*
*/
public class SpriteFactory {
	private HashMap<Integer, SpriteSheet> spriteSheets;
	
	/**
	 * Returns the requested sprite
	 * @param dir
	 * @param isMoving
	 * @param isLeftFoot
	 * @param sprite
	 * @return
	 */
	public Image getSprite(Direction dir, boolean isMoving, 
			boolean isLeftFoot, int sprite) {
		SpriteSheet sheet = spriteSheets.get(sprite);
		if (isMoving) {
			if (isLeftFoot) {
				switch (dir) {
				case Up:
					return sheet.getSprite(0, 0);
				case Down:
					return sheet.getSprite(0, 2);
				case Left:
					return sheet.getSprite(0, 3);
				case Right:
					return sheet.getSprite(0, 1);
				}
			} else {
				switch (dir) {
				case Up:
					return sheet.getSprite(2, 0);
				case Down:
					return sheet.getSprite(2, 2);
				case Left:
					return sheet.getSprite(2, 3);
				case Right:
					return sheet.getSprite(2, 1);
				}
			}
		} else {
			switch (dir) {
			case Up:
				return sheet.getSprite(1, 0);
			case Down:
				return sheet.getSprite(1, 2);
			case Left:
				return sheet.getSprite(1, 3);
			case Right:
				return sheet.getSprite(1, 1);
			}
		}
		return null;
	}
	
	/**
	 * Initialises the database of sprites
	 */
	public SpriteFactory() {
		
		spriteSheets = new HashMap<Integer, SpriteSheet>();	
		try {
			String location;
			String respath = System.getProperty("res.path");
			if(respath==null)
				respath="";
			/*
			 * WARNING: Change 224 to the amount of sprites we have in client
			 */
			for(int i = -5; i < 224; i++) {
				try {
					location = respath+"res/characters/" + String.valueOf(i) + ".png";
					spriteSheets.put(i, new SpriteSheet(new Image(location), 41, 51));
				} catch (Exception e) {}
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}
}
