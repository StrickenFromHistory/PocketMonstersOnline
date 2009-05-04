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

package tiled.mapeditor.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

import tiled.mapeditor.Resources;
import tiled.mapeditor.util.PropertiesTableModel;
import tiled.mapeditor.widget.VerticalStaticJPanel;

/**
 * @version $Id$
 */
public class PropertiesDialog extends JDialog
{
    protected JTable propertiesTable;
    protected final Properties properties;
    protected final PropertiesTableModel tableModel = new PropertiesTableModel();
    protected JPanel mainPanel;

    private static final String DIALOG_TITLE = Resources.getString("dialog.properties.title");
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String DELETE_BUTTON = Resources.getString("general.button.delete");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");


    public PropertiesDialog(JFrame parent, Properties p) {
        super(parent, DIALOG_TITLE, true);
        properties = p;
        init();
        pack();
        setLocationRelativeTo(getOwner());
    }

    protected void init() {
        propertiesTable = new JTable(tableModel);
        JScrollPane propScrollPane = new JScrollPane(propertiesTable);
        propScrollPane.setPreferredSize(new Dimension(200, 150));

        JButton okButton = new JButton(OK_BUTTON);
        JButton cancelButton = new JButton(CANCEL_BUTTON);
        JButton deleteButton = new JButton(Resources.getIcon("gnome-delete.png"));
        deleteButton.setToolTipText(DELETE_BUTTON);

        JPanel user = new VerticalStaticJPanel();
        user.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        user.setLayout(new BoxLayout(user, BoxLayout.X_AXIS));
        user.add(Box.createGlue());
        user.add(Box.createRigidArea(new Dimension(5, 0)));
        user.add(deleteButton);

        JPanel buttons = new VerticalStaticJPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createGlue());
        buttons.add(okButton);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(cancelButton);

        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(propScrollPane);
        mainPanel.add(user);
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(okButton);

        //create actionlisteners
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                buildPropertiesAndDispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                dispose();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                deleteSelected();
            }
        });
    }

    public void updateInfo() {
        tableModel.setProperties(properties);
    }

    public void getProps() {
        updateInfo();
        setVisible(true);
    }

    protected void buildPropertiesAndDispose() {
        // Make sure there is no active cell editor anymore
        TableCellEditor editor = propertiesTable.getCellEditor();
        if (editor != null) {
            editor.stopCellEditing();
        }

        // Apply possibly changed properties.
        properties.clear();
        properties.putAll(tableModel.getProperties());

        dispose();
    }

    protected void deleteSelected() {
        int total = propertiesTable.getSelectedRowCount();
        Object[] keys = new Object[total];
        int[] selRows = propertiesTable.getSelectedRows();

        for(int i = 0; i < total; i++) {
            keys[i] = propertiesTable.getValueAt(selRows[i], 0);
        }

        for (int i = 0; i < total; i++) {
            if (keys[i] != null) {
                tableModel.remove(keys[i]);
            }
        }
    }
}
