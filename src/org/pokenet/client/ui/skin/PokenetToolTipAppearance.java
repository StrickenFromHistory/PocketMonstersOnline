package org.pokenet.client.ui.skin;

import mdes.slick.sui.Component;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Theme;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.GUIContext;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetToolTipAppearance extends PokenetLabelAppearance {
    
    public void install(Component comp, Skin skin, Theme theme) {
        super.install(comp, skin, theme);
        comp.setOpaque(true);
        comp.getPadding().set(2, 3, 2, 3);
    }
    
    public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
        super.render(ctx, g, comp, skin, theme);
        
        if (comp.isBorderRendered()) {
            g.setColor(theme.getPrimaryBorder2());
            g.draw(comp.getAbsoluteBounds());
        }
    }
}