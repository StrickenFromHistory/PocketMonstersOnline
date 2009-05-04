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

import java.io.File;

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.Resources;

/**
 * Opens one of the recently open maps.
 *
 * @version $Id$
 */
public class OpenRecentAction extends AbstractFileAction
{
    private final String path;

    public OpenRecentAction(MapEditor editor, SaveAction saveAction, String path) {
        super(editor, saveAction,
              path.substring(path.lastIndexOf(File.separatorChar) + 1),
              Resources.getString("action.map.open.tooltip"));

        this.path = path;
    }

    protected void doPerformAction() {
        editor.loadMap(path);
    }
}
