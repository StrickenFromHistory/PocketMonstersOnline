package org.pokenet.client.ui.skin;

import org.newdawn.slick.gui.GUIContext;

import mdes.slick.sui.Component;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Theme;
import mdes.slick.sui.skin.AbstractComponentAppearance;
import mdes.slick.sui.skin.SkinUtil;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public abstract class PokenetComponentAppearance extends AbstractComponentAppearance {
    
    public void update(GUIContext ctx, int delta, Component comp, Skin skin, Theme theme) {
        //do nothing
    }
    
    @Override
	public void install(Component comp, Skin skin, Theme theme) {
        SkinUtil.installFont(comp, ((PokenetSkin)skin).getFont());        
        SkinUtil.installColors(comp, theme.getBackground(), theme.getForeground());
    }
    
    @Override
	public void uninstall(Component comp, Skin skin, Theme theme) {   
    }
}