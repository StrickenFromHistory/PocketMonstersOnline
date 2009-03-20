package org.pokenet.client.ui.base;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.pokenet.client.GameClient;

public class BattleButtonFactory {
    static {
            try {
                    //LoadingList.setDeferredLoading(true);
                    normal = new Image(
                            "/res/ui/button.png");     
                    normalDown = new Image(
                            "/res/ui/button_pressed.png");     
                    
                    small = new Image(
                            "/res/ui/button_small.png");       
                    smallDown = new Image(
                            "/res/ui/button_small_pressed.png");
                            
                    font = GameClient.getFontSmall();
                    //LoadingList.setDeferredLoading(false);
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
