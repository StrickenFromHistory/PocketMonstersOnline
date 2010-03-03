package org.pokenet.client.ui.skin;

import mdes.slick.sui.Button;
import mdes.slick.sui.Component;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Slider;
import mdes.slick.sui.Theme;
import mdes.slick.sui.skin.SkinUtil;
import mdes.slick.sui.skin.SliderAppearance;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.gui.GUIContext;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetSliderAppearance extends PokenetContainerAppearance implements SliderAppearance {
    
    public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
        Slider slider = (Slider)comp;
         
        SkinUtil.renderComponentBase(g, slider);
        Rectangle bounds = slider.getAbsoluteBounds();
        if (!slider.isOpaque() || slider.getBackground()==null) {
            Color back = theme.getPrimary3();
            g.setColor(back);
            g.fill(bounds);
            Color down = theme.getPrimaryBorder1();
            if (slider.isTrackDown()) {
                g.setColor(down);
                Rectangle selection = slider.getAbsoluteTrackSelectionBounds();
                g.fill(selection);
            }
        }
        
        if (slider.isBorderRendered()) {
            g.setColor(theme.getPrimaryBorder2());
            g.draw(bounds);
        }
    }
    
    /**
     * This is the knob or thumb button whic appears on the slider.
     */
    public Button createThumbButton(Slider slider) {
        Button btn = new Button() {
            public void updateAppearance() {
                setAppearance(new PokenetButtonAppearance(this) {
                    protected RoundedRectangle createRoundedBounds() {
                        return new RoundedRectangle(0f,0f,0f,0f,3f,50);
                    }
                });
            }
        };
        btn.setSize(26, 26);
        return btn;
    }
}