package org.pokenet.client.ui.skin;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.GUIContext;

import mdes.slick.sui.Button;
import mdes.slick.sui.Component;
import mdes.slick.sui.ScrollBar;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Slider;
import mdes.slick.sui.Theme;
import mdes.slick.sui.skin.ScrollBarAppearance;
import mdes.slick.sui.skin.SkinUtil;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetScrollBarAppearance extends PokenetContainerAppearance implements ScrollBarAppearance {
    
    public void install(Component comp, Skin skin, Theme theme) {
        SkinUtil.installFont(comp, ((PokenetSkin)skin).getFont());    
        SkinUtil.installColors(comp, theme.getPrimary1(), theme.getForeground());
    }
    
    public Button createScrollButton(ScrollBar bar, int direction) {
        Button btn = createPokenetScrollButton(bar, direction);
        return btn;
    }
    
    public Slider createSlider(ScrollBar bar, int orientation) {
        Slider slider = new Slider(orientation);
        return slider;
    }
    
    /**
     * A utility method to create a scroll button based on the given
     * scroll bar's orientation, size and direction. This will set the
     * button's dimensions to the width or height (based on orientation)
     * of the given scroll bar.
     * 
     * 
     * 
     * @param bar the scroll bar parent
     * @param direction the direction the bar will scroll, either 
     *      ScrollBar.INCREMENT or ScrollBar.DECREMENT.
     * @return a new PokenetArrowButton based on the given parameters
     */
    protected Button createPokenetScrollButton(ScrollBar bar, int direction) {
        float angle = PokenetArrowButton.getScrollButtonAngle(bar, direction);
        int orientation = bar.getOrientation();
        float size = 0f;
        if (orientation==ScrollBar.HORIZONTAL) {
            size = bar.getHeight();
        } else
            size = bar.getWidth();
        if (size==0)
            size = ScrollBar.DEFAULT_SIZE;
        Button btn = new PokenetArrowButton(angle);
        btn.setSize(size, size);
        return btn;
    }
}