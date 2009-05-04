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
import javax.swing.JOptionPane;

import tiled.mapeditor.MapEditor;
import tiled.mapeditor.Resources;

/**
 * This abstract file action asks to save the map if it has been modified. It
 * should be subclassed by any action that would discard the currently loaded
 * map.
 *
 * @version $Id$
 */
public abstract class AbstractFileAction extends AbstractAction
{
    protected final MapEditor editor;
    private final SaveAsAction saveAction;

    private static final String SAVE_CHANGES_TEXT = Resources.getString("action.map.save.changes.text");
    private static final String SAVE_CHANGES_TITLE = Resources.getString("action.map.save.changes.title");

    protected AbstractFileAction(MapEditor editor,
                                 SaveAction saveAction,
                                 String name, String description)
    {
        super(name);
        this.editor = editor;
        this.saveAction = saveAction;
        putValue(SHORT_DESCRIPTION, description);
    }

    public final void actionPerformed(ActionEvent e) {
        if (editor.unsavedChanges()) {
            int ret = JOptionPane.showConfirmDialog(editor.getAppFrame(),
                                                    SAVE_CHANGES_TEXT,
                                                    SAVE_CHANGES_TITLE,
                                                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (ret == JOptionPane.YES_OPTION) {
                saveAction.actionPerformed(e);

                // If saving was not cancelled and there are not still unsaved
                // changes (which would indicate an error occured), continue
                // to perform the action.
                if (!saveAction.isSavingCancelled() && !editor.unsavedChanges())
                {
                    doPerformAction();
                }
            } else if (ret == JOptionPane.NO_OPTION){
                doPerformAction();
            }
        }
        else {
            doPerformAction();
        }
    }

    /**
     * Actually performs the action, in the confidence that any existing map
     * has been either saved or discarded.
     */
    protected abstract void doPerformAction();
}
