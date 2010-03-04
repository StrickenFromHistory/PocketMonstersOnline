package org.pokenet.client.ui.skin;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.GUIContext;

import mdes.slick.sui.Component;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Theme;
import mdes.slick.sui.skin.SkinUtil;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetContainerAppearance extends PokenetComponentAppearance {
    public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
        SkinUtil.renderComponentBase(g, comp);
 
    }
}
