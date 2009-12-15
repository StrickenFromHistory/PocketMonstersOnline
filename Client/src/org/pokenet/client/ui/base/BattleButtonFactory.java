package org.pokenet.client.ui.base;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.pokenet.client.GameClient;

public class BattleButtonFactory {
    static {
    	try {
    		String respath = System.getProperty("res.path");
    		if(respath==null)
    			respath="";
    		String path = respath+"res/ui/";

    		normal = new Image(	path + "button.png", false);
    		normalDown = new Image(path + "button_pressed.png", false);
    		small = new Image(path + "button_small.png", false);
    		smallDown = new Image(path + "button_small_pressed.png", false);
    		
    		font = GameClient.getFontSmall();
    	} catch (SlickException e) {
    		e.printStackTrace();
    		assert(false);
    	}
    }
    private static Image small;
    private static Image normal;
    private static Image normalDown;
    private static Image smallDown;

    private static Font font;
    public static ImageButton getButton(String text) {
            ImageButton out = new ImageButton(normal, normal, normalDown);
            out.setFont(font);
            out.setForeground(Color.white);
            out.setText(text);
            return out;
    }
    public static ImageButton getSmallButton(String text) {
            ImageButton out = new ImageButton(small, small, smallDown);
            out.setFont(font);
            out.setForeground(Color.white);
            out.setText(text);
            return out;
    }
}
