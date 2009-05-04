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
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import tiled.io.MapHelper;
import tiled.io.MapWriter;
import tiled.mapeditor.MapEditor;
import tiled.mapeditor.Resources;
import tiled.mapeditor.util.TiledFileFilter;
import tiled.mapeditor.util.ConfirmingFileChooser;
import tiled.util.TiledConfiguration;

/**
 * A save action that always shows a file chooser.
 *
 * @version $Id$
 */
public class SaveAsAction extends AbstractAction
{
    protected MapEditor editor;
    private boolean savingCancelled;

    private static final String ACTION_NAME = Resources.getString("action.map.saveas.name");
    private static final String ACTION_TOOLTIP = Resources.getString("action.map.saveas.tooltip");
    private static final String SAVEAS_ERROR_MESSAGE = Resources.getString("dialog.saveas.error.message");
    private static final String SAVEAS_ERROR_TITLE = Resources.getString("dialog.saveas.error.title");

    public SaveAsAction(MapEditor editor) {
        super(ACTION_NAME);
        putValue(SHORT_DESCRIPTION, ACTION_TOOLTIP);
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control shift S"));
        this.editor = editor;
    }

    public void actionPerformed(ActionEvent e) {
        showFileChooser();
    }

    /**
     * Shows the confirming file chooser and proceeds with saving the map when
     * a filename was approved.
     */
    protected void showFileChooser()
    {
        // Start at the location of the most recently loaded map file
        String startLocation =
                TiledConfiguration.node("recent").get("file0", null);

        TiledFileFilter byExtensionFilter =
                new TiledFileFilter(TiledFileFilter.FILTER_EXT);
        TiledFileFilter tmxFilter =
                new TiledFileFilter(TiledFileFilter.FILTER_TMX);

        JFileChooser chooser = new ConfirmingFileChooser(startLocation);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(byExtensionFilter);
        chooser.addChoosableFileFilter(tmxFilter);

        MapWriter[] writers = editor.getPluginLoader().getWriters();
        for (MapWriter writer : writers) {
            try {
                chooser.addChoosableFileFilter(new TiledFileFilter(writer));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        chooser.setFileFilter(byExtensionFilter);

        int result = chooser.showSaveDialog(editor.getAppFrame());
        if (result == JFileChooser.APPROVE_OPTION)
        {
            savingCancelled = false;
            TiledFileFilter saver = (TiledFileFilter) chooser.getFileFilter();
            String selectedFile = chooser.getSelectedFile().getAbsolutePath();
            saveFile(saver, selectedFile);
        }
        else {
            savingCancelled = true;
        }
    }

    /**
     * Actually saves the map.
     *
     * @param saver the file filter selected when the filename was chosen
     * @param filename the filename to save the map to
     */
    protected void saveFile(TiledFileFilter saver, String filename)
    {
        try {
            // Either select the format by extension or use a specific format
            // when selected.
            if (saver.getType() == TiledFileFilter.FILTER_EXT) {
                MapHelper.saveMap(editor.getCurrentMap(), filename);
            } else {
                MapHelper.saveMap(editor.getCurrentMap(), saver.getPlugin(),
                                  filename);
            }

            // The file was saved successfully, update some things.
            // todo: this could probably be done a bit neater
            editor.getCurrentMap().setFilename(filename);
            editor.updateRecent(filename);
            editor.getUndoHandler().commitSave();
            editor.updateTitle();
        }
        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(editor.getAppFrame(),
                                          SAVEAS_ERROR_MESSAGE + " " +
                                                  filename + ": " +
                                                  e.getLocalizedMessage(),
                                          SAVEAS_ERROR_TITLE,
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSavingCancelled ()
    {
        return savingCancelled;
    }
}
