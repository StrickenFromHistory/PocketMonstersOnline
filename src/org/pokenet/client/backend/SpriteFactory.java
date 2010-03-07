package org.pokenet.client.backend;

import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.svg.Loader;
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
			
			Image[] imgArray = new Image[231];
				// we have to do the loops separately, because making a spritesheet messes
				// up deferred loading
				// but this way uses a lot more ram.... sad face
			/*
			 * try-catch throws off the deferred loading for some reason... 
			 * don't know why... so.. we need to make sure all the images are in the characters folder...
			 * otherwise the exception is caught outside of the forloop, and we die essentially 
			 */
				for (int i = 0; i < 225 + 5; i++){
					try{
//						LoadingList.setDeferredLoading(true);
						imgArray[i] = new Image("res/characters/" + (i - 5) + ".png");	
					} catch (Exception e){System.err.println(e.getMessage());}

				}
				
				
			
			/*
			 * WARNING: Change 224 to the amount of sprites we have in client
			 */
				for(int i = -5; i < 225; i++) {
					if(imgArray[i + 5] != null){
						spriteSheets.put(i, new SpriteSheet(imgArray[i + 5], 41, 51));
					}
	
				}
		} catch (Exception e) { 
			System.err.println(e.getMessage());
		}
	}
}
