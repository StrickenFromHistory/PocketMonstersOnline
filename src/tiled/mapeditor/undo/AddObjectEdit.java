/*
 *  Tiled Map Editor, (c) 2008
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

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import tiled.core.MapObject;
import tiled.core.ObjectGroup;
import tiled.mapeditor.Resources;

/**
 * Adds an object to an object group.
 *
 * @version $Id$
 */
public class AddObjectEdit extends AbstractUndoableEdit
{
    private final ObjectGroup objectGroup;
    private final MapObject mapObject;

    public AddObjectEdit(ObjectGroup objectGroup, MapObject mapObject) {
        this.objectGroup = objectGroup;
        this.mapObject = mapObject;
    }

    public void undo() throws CannotUndoException {
        super.undo();
        objectGroup.removeObject(mapObject);
    }

    public void redo() throws CannotRedoException {
        super.redo();
        objectGroup.addObject(mapObject);
    }

    public String getPresentationName() {
        return Resources.getString("action.object.add.name");
    }
}
