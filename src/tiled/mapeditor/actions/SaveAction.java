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

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.Resources;
import tiled.mapeditor.util.TiledFileFilter;
import tiled.core.Map;

/**
 * Tries to save the file if a filepath is already set in the main app,
 * otherwise prompts for a filename.
 *
 * @version $Id$
 */
public class SaveAction extends SaveAsAction
{
    public SaveAction(MapEditor editor) {
        super(editor);
        putValue(NAME, Resources.getString("action.map.save.name"));
        putValue(SHORT_DESCRIPTION, Resources.getString("action.map.save.tooltip"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
    }

    public void actionPerformed (ActionEvent e)
    {
        Map currentMap = editor.getCurrentMap();
        String filePath = currentMap.getFilename();

        // todo: Fix the case where the plugin cannot be determined by the
        // todo: current filename. This can happen when the user has used
        // todo: "Save As" to save the file using a non-standard extension.
        if (filePath != null) {
            // The plugin is determined by the extention.
            saveFile(new TiledFileFilter(TiledFileFilter.FILTER_EXT),
                     filePath);
        }
        else {
            super.actionPerformed(e);
        }
    }
}
