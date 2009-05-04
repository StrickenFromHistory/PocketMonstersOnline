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

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import tiled.io.MapReader;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.Resources;
import tiled.mapeditor.util.TiledFileFilter;
import tiled.util.TiledConfiguration;

/**
 * Opens the map open dialog.
 *
 * @version $Id$
 */
public class OpenMapAction extends AbstractFileAction
{
    private static final String OPEN_ERROR_TITLE = Resources.getString("dialog.saveas.error.title");

    public OpenMapAction(MapEditor editor, SaveAction saveAction) {
        super(editor, saveAction,
              Resources.getString("action.map.open.name"),
              Resources.getString("action.map.open.tooltip"));

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
    }

    protected void doPerformAction() {
        // Start at the location of the most recently loaded map file
        String startLocation = TiledConfiguration.fileDialogStartLocation();

        JFileChooser chooser = new JFileChooser(startLocation);

        try {
            MapReader[] readers = editor.getPluginLoader().getReaders();
            for (MapReader reader : readers) {
                chooser.addChoosableFileFilter(new TiledFileFilter(
                        reader.getFilter(), reader.getName()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(editor.getAppFrame(),
                    "Error while loading plugins: " + e.getLocalizedMessage(),
                    OPEN_ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        chooser.addChoosableFileFilter(
                new TiledFileFilter(TiledFileFilter.FILTER_TMX));

        int ret = chooser.showOpenDialog(editor.getAppFrame());
        if (ret == JFileChooser.APPROVE_OPTION) {
            editor.loadMap(chooser.getSelectedFile().getAbsolutePath());
        }
    }
}
