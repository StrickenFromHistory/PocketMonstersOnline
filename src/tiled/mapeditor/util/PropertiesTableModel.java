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

import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.table.AbstractTableModel;

import tiled.mapeditor.Resources;

/**
 * @version $Id$
 */
public class PropertiesTableModel extends AbstractTableModel
{
    private SortedMap properties;

    private static final String[] columnNames = {
            Resources.getString("dialog.properties.column.name"),
            Resources.getString("dialog.properties.column.value")
    };

    public PropertiesTableModel() {
        properties = new TreeMap();
    }

    public int getRowCount() {
        return properties.size() + 1;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns wether the given position in the table is editable. Values can
     * only be edited when they have a name.
     */
    public boolean isCellEditable(int row, int col) {
        return col == 0 || col == 1 && getValueAt(row, 0) != null;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] array = properties.keySet().toArray();
        if (rowIndex >= 0 && rowIndex < properties.size()) {
            if (columnIndex == 0) {
                return array[rowIndex];
            } else if (columnIndex == 1) {
                return properties.get(array[rowIndex]);
            }
        }
        return null;
    }

    public void setValueAt(Object value, int row, int col) {
        if (row >= properties.size() && col == 0) {
            if (((String) value).length() > 0) {
                properties.put(value, "");
                fireTableDataChanged();
            }
        } else {
            if (col == 1) {
                properties.put(getValueAt(row, 0), value);
                fireTableCellUpdated(row, col);
            } else if (col == 0) {
                String val = (String) getValueAt(row, 1);
                if (getValueAt(row, col) != null) {
                    properties.remove(getValueAt(row, col));
                }
                if (((String) value).length() > 0) {
                    properties.put(value, val);
                }
                fireTableDataChanged();
            }
        }
    }

    public void remove(Object key) {
        properties.remove(key);
        fireTableDataChanged();
    }

    public void setProperties(Properties props) {
        properties.clear();
        properties.putAll(props);
        fireTableDataChanged();
    }

    public Properties getProperties() {
        Properties props = new Properties();
        props.putAll(properties);
        return props;
    }
}
