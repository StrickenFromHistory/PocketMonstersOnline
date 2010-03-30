package org.pokenet.client.ui.skin;

import mdes.slick.sui.Component;
import mdes.slick.sui.Label;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Theme;
import mdes.slick.sui.skin.SkinUtil;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.GUIContext;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetLabelAppearance extends PokenetContainerAppearance {
    
    public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
        super.render(ctx, g, comp, skin, theme);
        SkinUtil.renderLabelBase(g, (Label)comp);
    }
}