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

package tiled.mapeditor.undo;

import java.util.Iterator;
import java.awt.event.ActionEvent;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;

import tiled.util.TiledConfiguration;
import tiled.mapeditor.MapEditor;

/**
 * @version $Id$
 */
public class UndoHandler extends UndoManager
{
    UndoableEdit savedAt;

    private final Action undoAction = new UndoAction();
    private final Action redoAction = new RedoAction();
    private final MapEditor editor;

    public UndoHandler(MapEditor editor) {
        this.editor = editor;
        setLimit(TiledConfiguration.root().getInt("undoDepth", 30));
        updateActions();
    }

    /**
     * Overridden to update the undo/redo actions.
     * @see UndoManager#discardAllEdits()
     */
    public synchronized void discardAllEdits()
    {
        super.discardAllEdits();
        updateActions();
    }

    /**
     * Overridden to update the undo/redo actions.
     * @see UndoManager#undo()
     * @throws CannotUndoException
     */
    public synchronized void undo() throws CannotUndoException {
        super.undo();
        updateActions();
        editor.updateTitle();

        // todo: Updating of the mapview should ultimately happen
        // todo: automatically based on the changes made to the map.
        editor.getMapView().repaint();
    }

    /**
     * Overridden to update the undo/redo actions.
     * @see UndoManager#redo()
     * @throws CannotRedoException
     */
    public synchronized void redo() throws CannotRedoException {
        super.redo();
        updateActions();
        editor.updateTitle();
        editor.getMapView().repaint();
    }

    public void undoableEditHappened(UndoableEditEvent e) {
        super.undoableEditHappened(e);
        updateActions();
        editor.updateTitle();
    }

    public boolean isAllSaved() {
        return editToBeUndone() == savedAt;
    }

    public void commitSave() {
        savedAt = editToBeUndone();
    }

    public String[] getEdits() {
        String[] list = new String[edits.size()];
        Iterator<UndoableEdit> itr = edits.iterator();
        int i = 0;

        while (itr.hasNext()) {
            UndoableEdit e = itr.next();
            list[i++] = e.getPresentationName();
        }

        return list;
    }

    /**
     * Returns the redo action.
     * @return the redo action.
     */
    public Action getRedoAction() {
        return redoAction;
    }

    /**
     * Returns the undo action.
     * @return the undo action
     */
    public Action getUndoAction() {
        return undoAction;
    }

    private void updateActions() {
        undoAction.setEnabled(canUndo());
        redoAction.setEnabled(canRedo());
        undoAction.putValue(Action.NAME, getUndoPresentationName());
        redoAction.putValue(Action.NAME, getRedoPresentationName());
    }

    private class UndoAction extends AbstractAction {
        public UndoAction() {
            super(getUndoPresentationName());
            putValue(SHORT_DESCRIPTION, "Undo one action");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));
        }
        public void actionPerformed(ActionEvent evt) {
            undo();
        }
    }

    private class RedoAction extends AbstractAction {
        public RedoAction() {
            super(getRedoPresentationName());
            putValue(SHORT_DESCRIPTION, "Redo one action");
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Y"));
        }
        public void actionPerformed(ActionEvent evt) {
            redo();
        }
    }
}
