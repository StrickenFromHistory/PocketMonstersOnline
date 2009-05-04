/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <bjorn@lindeijer.nl>
 */

package tiled.mapeditor.widget;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

/**
 * Tiled menu item extends on JMenuItem in that it allows for not accepting
 * any icon from an attached action.
 *
 * @version $Id$
 */
public class TMenuItem extends JMenuItem
{
    private boolean showIcon;

    public TMenuItem(boolean showIcon) {
        this.showIcon = showIcon;
    }

    public TMenuItem(Action action) {
        this(action, false);
    }

    public TMenuItem(Action action, boolean showIcon) {
        this(showIcon);
        setAction(action);
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }

    public void setIcon(Icon icon) {
        if (showIcon) {
            super.setIcon(icon);
        }
    }
}
