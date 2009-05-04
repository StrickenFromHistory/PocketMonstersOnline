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

package tiled.mapeditor.actions;

import tiled.core.Map;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.Resources;

/**
 * Adds a object group layer to the current map and selects it.
 *
 * @version $Id$
 */
public class AddObjectGroupAction extends AbstractLayerAction
{
    public AddObjectGroupAction(MapEditor editor) {
        super(editor,
              Resources.getString("action.objectgroup.add.name"),
              Resources.getString("action.objectgroup.add.tooltip"),
              Resources.getIcon("gnome-new.png"));
    }

    protected void doPerformAction() {
        Map currentMap = editor.getCurrentMap();
        currentMap.addObjectGroup();
        editor.setCurrentLayer(currentMap.getTotalLayers() - 1);
    }
}
