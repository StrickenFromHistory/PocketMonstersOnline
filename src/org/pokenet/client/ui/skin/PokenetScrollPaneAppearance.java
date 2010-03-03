package org.pokenet.client.ui.skin;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.GUIContext;

import mdes.slick.sui.Component;
import mdes.slick.sui.ScrollBar;
import mdes.slick.sui.ScrollPane;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Theme;
import mdes.slick.sui.skin.ScrollPaneAppearance;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetScrollPaneAppearance extends PokenetContainerAppearance implements ScrollPaneAppearance {
    
    public ScrollBar createScrollBar(ScrollPane pane, int orientation) {
        ScrollBar bar = new ScrollBar(orientation);
        bar.setSize(ScrollPane.CORNER_SIZE, ScrollPane.CORNER_SIZE);
        return bar;
    }
}