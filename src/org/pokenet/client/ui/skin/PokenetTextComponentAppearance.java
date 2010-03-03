package org.pokenet.client.ui.skin;

import mdes.slick.sui.Component;
import mdes.slick.sui.Point;
import mdes.slick.sui.Skin;
import mdes.slick.sui.TextComponent;
import mdes.slick.sui.Theme;
import mdes.slick.sui.Timer;
import mdes.slick.sui.event.ChangeEvent;
import mdes.slick.sui.event.ChangeListener;
import mdes.slick.sui.skin.TextComponentAppearance;

import org.newdawn.slick.gui.GUIContext;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetTextComponentAppearance extends PokenetContainerAppearance 
implements TextComponentAppearance {

protected Timer flashTimer = new Timer(500);

protected boolean renderCaret = false;
protected boolean still = false;

protected Timer delayTimer = new Timer(800);

protected ChangeListener change = new ChangeListener() {
public void stateChanged(ChangeEvent e) {
still = true;
delayTimer.restart();
}
};

public PokenetTextComponentAppearance() {
flashTimer.setRepeats(true);
delayTimer.setRepeats(false);
flashTimer.start();
}

public void update(GUIContext ctx, int delta, Component comp, Skin skin, Theme theme) {
super.update(ctx, delta, comp, skin, theme);
flashTimer.update(ctx, delta);
delayTimer.update(ctx, delta);

if (delayTimer.isAction()) {
still = false;
}

if (still)
renderCaret = true;
else if (flashTimer.isAction())
renderCaret = !renderCaret;
}

public void install(Component comp, Skin skin, Theme theme) {
super.install(comp, skin, theme);
if (skin instanceof PokenetSkin) {
comp.addMouseListener(((PokenetSkin)skin).getSelectCursorListener());
}
((TextComponent)comp).addChangeListener(change);
}

public void uninstall(Component comp, Skin skin, Theme theme) {
super.uninstall(comp, skin, theme);
if (skin instanceof PokenetSkin) {
comp.removeMouseListener(((PokenetSkin)skin).getSelectCursorListener());
}
((TextComponent)comp).removeChangeListener(change);
}


public int viewToModel(TextComponent comp, float x, float y) { 
return -1;
}

public Point modelToView(TextComponent comp, int pos) {
return null;
}
}