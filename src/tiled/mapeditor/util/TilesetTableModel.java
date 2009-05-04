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

package tiled.mapeditor.util;

import java.util.Iterator;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

import tiled.core.*;
import tiled.mapeditor.Resources;

public class TilesetTableModel extends AbstractTableModel implements MapChangeListener, TilesetChangeListener
{
    private Map map;
    private static final String[] columnNames = { Resources.getString("dialog.tilesetmanager.table.name"),
        Resources.getString("dialog.tilesetmanager.table.source") };

    private static final String EMBEDDED = Resources.getString("dialog.tilesetmanager.embedded");

    public TilesetTableModel(Map map) {
        this.map = map;

        for (TileSet tileset : map.getTilesets()) {
            tileset.addTilesetChangeListener(this);
        }
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public int getRowCount() {
        if (map != null) {
            return map.getTilesets().size();
        } else {
            return 0;
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int row, int col) {
        Vector tilesets = map.getTilesets();
        if (row >= 0 && row < tilesets.size()) {
            TileSet tileset = (TileSet)tilesets.get(row);
            if (col == 0) {
                return tileset.getName();
            } else {
                String ret = tileset.getSource();

                if (ret == null) {
                    ret = EMBEDDED;
                }

                return ret;
            }
        } else {
            return null;
        }
    }

    public boolean isCellEditable(int row, int col) {
        return col == 0;
    }

    public void setValueAt(Object value, int row, int col) {
        if (col != 0) return;

        Vector tilesets = map.getTilesets();
        if (row >= 0 && row < tilesets.size()) {
            TileSet tileset = (TileSet)tilesets.get(row);
            if (col == 0) {
                tileset.setName(value.toString());
            }
            fireTableCellUpdated(row, col);
        }
    }

    private int checkSetUsage(TileSet set) {
        int used = 0;
        Iterator tileIterator = set.iterator();

        while (tileIterator.hasNext()) {
            Tile tile = (Tile)tileIterator.next();
            Iterator itr = map.getLayers();

            while (itr.hasNext()) {
                MapLayer ml = (MapLayer)itr.next();

                if (ml instanceof TileLayer) {
                    if (((TileLayer) ml).isUsed(tile)) {
                        used++;
                        break;
                    }
                }
            }
        }

        return used;
    }

    public void mapChanged(MapChangedEvent event) {
    }

    public void tilesetAdded(MapChangedEvent event, TileSet tileset) {
        int index = map.getTilesets().indexOf(tileset);

        if (index == -1) return;

        tileset.addTilesetChangeListener(this);

        fireTableRowsInserted(index, index);
    }

    public void tilesetRemoved(MapChangedEvent event, int index) {
        fireTableRowsDeleted(index - 1, index);
    }

    public void tilesetsSwapped(MapChangedEvent event, int index0, int index1) {
        fireTableRowsUpdated(index0, index1);
    }

    public void tilesetChanged(TilesetChangedEvent event) {
    }

    public void nameChanged(TilesetChangedEvent event, String oldName, String newName) {
        int index = map.getTilesets().indexOf(event.getTileset());

        if (index == -1) return;

        fireTableCellUpdated(index, 0);
    }

    public void sourceChanged(TilesetChangedEvent event, String oldSource, String newSource) {
        int index = map.getTilesets().indexOf(event.getTileset());

        if (index == -1) return;

        fireTableCellUpdated(index, 1);
    }

    public void clearListeners() {
        for (Iterator it = map.getTilesets().iterator(); it.hasNext();) {
            ((TileSet) it.next()).removeTilesetChangeListener(this);
        }
    }
}
