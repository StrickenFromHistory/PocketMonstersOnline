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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import javax.swing.*;

import tiled.core.Map;
import tiled.mapeditor.widget.IntegerSpinner;
import tiled.mapeditor.widget.VerticalStaticJPanel;
import tiled.mapeditor.Resources;
import tiled.util.TiledConfiguration;

public class NewMapDialog extends JDialog implements ActionListener
{
    private Map newMap;
    private IntegerSpinner mapWidth, mapHeight;
    private IntegerSpinner tileWidth, tileHeight;
    private JComboBox mapTypeChooser;

    private final Preferences prefs = TiledConfiguration.node("dialog/newmap");

    private static final String DIALOG_TITLE = Resources.getString("dialog.newmap.title");
    private static final String MAPSIZE_TITLE = Resources.getString("dialog.newmap.mapsize.title");
    private static final String TILESIZE_TITLE = Resources.getString("dialog.newmap.tilesize.title");
    private static final String WIDTH_LABEL = Resources.getString("dialog.newmap.width.label");
    private static final String HEIGHT_LABEL = Resources.getString("dialog.newmap.height.label");
    private static final String MAPTYPE_LABEL = Resources.getString("dialog.newmap.maptype.label");
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String CANCEL_BUTTON = Resources.getString("general.button.cancel");
    private static final String ISOMETRIC_MAPTYPE = Resources.getString("general.maptype.isometric");
    private static final String HEXAGONAL_MAPTYPE = Resources.getString("general.maptype.hexagonal");
    private static final String SHIFTED_MAPTYPE = Resources.getString("general.maptype.shifted");
    private static final String ORTHOGONAL_MAPTYPE = Resources.getString("general.maptype.orthogonal");

    public NewMapDialog(JFrame parent) {
        super(parent, DIALOG_TITLE, true);
        init();
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    private void init() {
        // Load dialog defaults

        int defaultMapWidth = prefs.getInt("mapWidth", 64);
        int defaultMapHeight = prefs.getInt("mapHeight", 64);
        int defaultTileWidth = prefs.getInt("tileWidth", 35);
        int defaultTileHeight = prefs.getInt("tileHeight", 35);

        // Create the primitives

        mapWidth = new IntegerSpinner(defaultMapWidth, 1);
        mapHeight = new IntegerSpinner(defaultMapHeight, 1);
        tileWidth = new IntegerSpinner(defaultTileWidth, 1);
        tileHeight = new IntegerSpinner(defaultTileHeight, 1);

        // Map size fields

        JPanel mapSize = new VerticalStaticJPanel();
        mapSize.setLayout(new GridBagLayout());
        mapSize.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(MAPSIZE_TITLE),
                    BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 0, 0, 5);
        mapSize.add(new JLabel(WIDTH_LABEL), c);
        c.gridy = 1;
        mapSize.add(new JLabel(HEIGHT_LABEL), c);
        c.insets = new Insets(5, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        mapSize.add(mapWidth, c);
        c.gridy = 1;
        mapSize.add(mapHeight, c);

        // Tile size fields

        JPanel tileSize = new VerticalStaticJPanel();
        tileSize.setLayout(new GridBagLayout());
        tileSize.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(TILESIZE_TITLE),
                    BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 0, 0, 5);
        tileSize.add(new JLabel(WIDTH_LABEL), c);
        c.gridy = 1;
        tileSize.add(new JLabel(HEIGHT_LABEL), c);
        c.insets = new Insets(5, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        tileSize.add(tileWidth, c);
        c.gridy = 1;
        tileSize.add(tileHeight, c);

        // OK and Cancel buttons

        JButton okButton = new JButton(OK_BUTTON);
        JButton cancelButton = new JButton(CANCEL_BUTTON);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        JPanel buttons = new VerticalStaticJPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createGlue());
        buttons.add(okButton);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(cancelButton);

        // Map type and name inputs

        mapTypeChooser = new JComboBox();
        mapTypeChooser.addItem(ORTHOGONAL_MAPTYPE);
        mapTypeChooser.addItem(ISOMETRIC_MAPTYPE);
        mapTypeChooser.addItem(HEXAGONAL_MAPTYPE);
        // TODO: Enable views when implemented decently
        //mapTypeChooser.addItem(SHIFTED_MAPTYPE);

        JPanel miscPropPanel = new VerticalStaticJPanel();
        miscPropPanel.setLayout(new GridBagLayout());
        miscPropPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 0, 0, 5);
        miscPropPanel.add(new JLabel(MAPTYPE_LABEL), c);
        c.insets = new Insets(5, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        miscPropPanel.add(mapTypeChooser, c);

        // Putting two size panels next to eachother

        JPanel sizePanels = new JPanel();
        sizePanels.setLayout(new BoxLayout(sizePanels, BoxLayout.X_AXIS));
        sizePanels.add(mapSize);
        sizePanels.add(Box.createRigidArea(new Dimension(5, 0)));
        sizePanels.add(tileSize);

        // Main panel

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(miscPropPanel);
        mainPanel.add(sizePanels);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(Box.createGlue());
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(okButton);
    }

    public Map create() {
        setVisible(true);
        return newMap;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(OK_BUTTON)) {
            int w = mapWidth.intValue();
            int h = mapHeight.intValue();
            int twidth = tileWidth.intValue();
            int theight = tileHeight.intValue();
            int orientation = Map.MDO_ORTHO;
            String mapTypeString = (String)mapTypeChooser.getSelectedItem();

            if (mapTypeString.equals(ISOMETRIC_MAPTYPE)) {
                orientation = Map.MDO_ISO;
            } else if (mapTypeString.equals(HEXAGONAL_MAPTYPE)) {
                orientation = Map.MDO_HEX;
            } else if (mapTypeString.equals(SHIFTED_MAPTYPE)) {
                orientation = Map.MDO_SHIFTED;
            }

            newMap = new Map(w, h);
            newMap.addLayer();
            newMap.setTileWidth(twidth);
            newMap.setTileHeight(theight);
            newMap.setOrientation(orientation);

            // Save dialog options

            prefs.putInt("mapWidth", mapWidth.intValue());
            prefs.putInt("mapHeight", mapHeight.intValue());
            prefs.putInt("tileWidth", tileWidth.intValue());
            prefs.putInt("tileHeight", tileHeight.intValue());
        }
        dispose();
    }
}
