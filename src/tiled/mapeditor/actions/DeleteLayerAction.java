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

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.Resources;
import tiled.core.Map;

/**
 * Deletes the selected layer and selects the layer that takes the same index.
 *
 * @version $Id$
 */
public class DeleteLayerAction extends AbstractLayerAction
{
    public DeleteLayerAction(MapEditor editor) {
        super(editor,
              Resources.getString("action.layer.delete.name"),
              Resources.getString("action.layer.delete.tooltip"),
              Resources.getIcon("gnome-delete.png"));
    }

    protected void doPerformAction() {
        Map map = editor.getCurrentMap();
        int layerIndex = editor.getCurrentLayerIndex();
        int totalLayers = map.getTotalLayers();

        if (layerIndex >= 0) {
            map.removeLayer(layerIndex);

            // If the topmost layer was selected, the layer index is invalid
            // after removing that layer. The right thing to do is to reset it
            // to the new topmost layer.
            if (layerIndex == totalLayers - 1) {
                editor.setCurrentLayer(totalLayers - 2);
            }
        }
    }
}
