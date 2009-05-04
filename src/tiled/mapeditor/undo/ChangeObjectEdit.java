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

import java.util.Properties;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import tiled.core.MapObject;
import tiled.mapeditor.Resources;

/**
 * Changes the attributes and properties of an object.
 */
public class ChangeObjectEdit extends AbstractUndoableEdit
{
    private final MapObject mapObject;

    private final String prevName;
    private final String prevType;
    private final String prevImageSource;
    private final int prevWidth;
    private final int prevHeight;
    private final Properties prevProperties = new Properties();

    private String newName;
    private String newType;
    private String newImageSource;
    private int newWidth;
    private int newHeight;
    private final Properties newProperties = new Properties();

    public ChangeObjectEdit(MapObject mapObject) {
        this.mapObject = mapObject;

        // Store the previous state so we can undo changes
        prevName = mapObject.getName();
        prevType = mapObject.getType();
        prevImageSource = mapObject.getImageSource();
        prevWidth = mapObject.getWidth();
        prevHeight = mapObject.getHeight();
        prevProperties.putAll(mapObject.getProperties());
    }

    public void undo() throws CannotUndoException {
        super.undo();

        // Store the current state so we can redo changes
        newName = mapObject.getName();
        newType = mapObject.getType();
        newImageSource = mapObject.getImageSource();
        newWidth = mapObject.getWidth();
        newHeight = mapObject.getHeight();
        newProperties.clear();
        newProperties.putAll(mapObject.getProperties());

        mapObject.setName(prevName);
        mapObject.setType(prevType);
        mapObject.setImageSource(prevImageSource);
        mapObject.setWidth(prevWidth);
        mapObject.setHeight(prevHeight);
        mapObject.getProperties().clear();
        mapObject.getProperties().putAll(prevProperties);
    }

    public void redo() throws CannotRedoException {
        super.redo();

        mapObject.setName(newName);
        mapObject.setType(newType);
        mapObject.setImageSource(newImageSource);
        mapObject.setWidth(newWidth);
        mapObject.setHeight(newHeight);
        mapObject.getProperties().clear();
        mapObject.getProperties().putAll(newProperties);
    }

    public String getPresentationName() {
        return Resources.getString("action.object.change.name");
    }
}
