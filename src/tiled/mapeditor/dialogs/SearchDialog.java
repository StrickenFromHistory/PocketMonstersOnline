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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.*;

import tiled.core.*;
import tiled.mapeditor.selection.SelectionLayer;
import tiled.mapeditor.util.MultisetListRenderer;
import tiled.mapeditor.widget.VerticalStaticJPanel;
import tiled.mapeditor.Resources;

/**
 * @version $Id$
 */
public class SearchDialog extends JDialog implements ActionListener
{
    private final Map map;
    private JComboBox searchCBox, replaceCBox;
    private Point currentMatch;
    private SelectionLayer sl;
    private static final double LIST_TILE_SCALE = 0.5;

    private static final String DIALOG_TITLE = Resources.getString("dialog.search.title");
    private static final String FIND_LABEL = Resources.getString("dialog.search.find.label");
    private static final String REPLACE_LABEL = Resources.getString("dialog.search.replace.label");
    private static final String FIND_BUTTON = Resources.getString("dialog.search.find.button");
    private static final String FIND_ALL_BUTTON = Resources.getString("dialog.search.findall.button");
    private static final String REPLACE_BUTTON = Resources.getString("dialog.search.replace.button");
    private static final String REPLACE_ALL_BUTTON = Resources.getString("dialog.search.replaceall.button");
    private static final String CLOSE_BUTTON = Resources.getString("general.button.close");

    public SearchDialog(JFrame parent) {
        this(parent, null);
    }

    public SearchDialog(JFrame parent, Map map) {
        super(parent, DIALOG_TITLE, false);
        this.map = map;
        init();
        setLocationRelativeTo(parent);
    }

    private void init() {
        final MultisetListRenderer tileListRenderer;
        tileListRenderer = new MultisetListRenderer(LIST_TILE_SCALE);

        /* SEARCH PANEL */
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createEtchedBorder());
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 2; c.weighty = 1;
        searchPanel.add(new JLabel(FIND_LABEL), c);
        c.gridx = 1;
        searchCBox = new JComboBox();
        searchCBox.setRenderer(tileListRenderer);
        //searchCBox.setSelectedIndex(1);
        searchCBox.setEditable(false);
        searchPanel.add(searchCBox, c);
        c.gridy = 1;
        c.gridx = 0;
        searchPanel.add(new JLabel(REPLACE_LABEL), c);
        c.gridx = 1;
        replaceCBox = new JComboBox();
        replaceCBox.setRenderer(tileListRenderer);
        //searchCBox.setSelectedIndex(1);
        replaceCBox.setEditable(false);
        searchPanel.add(replaceCBox,c);
        queryTiles(searchCBox);
        //replaceCBox.addItem(null);
        queryTiles(replaceCBox);

        /* SCOPE PANEL */
        /*
        JPanel scopePanel = new JPanel();
        scopePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Scope"),
                    BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        */

        final JButton bFind = new JButton(FIND_BUTTON);
        final JButton bFindAll = new JButton(FIND_ALL_BUTTON);
        final JButton bReplace = new JButton(REPLACE_BUTTON);
        final JButton bReplaceAll = new JButton(REPLACE_ALL_BUTTON);
        final JButton bClose = new JButton(CLOSE_BUTTON);

        bFind.addActionListener(this);
        bFindAll.addActionListener(this);
        bReplace.addActionListener(this);
        bReplaceAll.addActionListener(this);
        bClose.addActionListener(this);


        /* BUTTONS PANEL */
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 5, 5));
        buttonPanel.add(bFind);
        buttonPanel.add(bFindAll);
        buttonPanel.add(bReplace);
        buttonPanel.add(bReplaceAll);

        JPanel closePanel = new VerticalStaticJPanel();
        closePanel.setLayout(new BorderLayout());
        closePanel.add(bClose, BorderLayout.EAST);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        //mainPanel.add(scopePanel);
        //mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(closePanel);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(bFind);
        pack();
    }

    private void queryTiles(JComboBox b) {
        for (TileSet set : map.getTilesets()) {
            b.addItem(set);

            final Iterator tileIterator = set.iterator();
            while (tileIterator.hasNext()) {
                Tile tile = (Tile) tileIterator.next();
                b.addItem(tile);
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals(CLOSE_BUTTON)) {
            map.removeLayerSpecial(sl);
            dispose();
        } else if (command.equals(FIND_BUTTON)) {
            if (searchCBox.getSelectedItem() instanceof Tile) {
                find((Tile)searchCBox.getSelectedItem());
            }
        } else if (command.equals(FIND_ALL_BUTTON)) {
            if (sl != null) {
                map.removeLayerSpecial(sl);
            }

            sl = new SelectionLayer(map.getWidth(), map.getHeight());
            Rectangle bounds = new Rectangle();
            final Iterator<MapLayer> itr = map.getLayers();
            while (itr.hasNext()) {
                MapLayer layer = itr.next();
                if (layer instanceof TileLayer) {
                    layer.getBounds(bounds);
                    for (int y = 0; y < bounds.height; y++) {
                        for (int x = 0; x < bounds.width; x++) {
                            if (((TileLayer)layer).getTileAt(x,y) == searchCBox.getSelectedItem()) {
                                sl.select(x,y);
                            }
                        }
                    }
                }
            }
            map.addLayerSpecial(sl);
            map.touch();

        } else if (command.equals(REPLACE_ALL_BUTTON)) {
            if (!(searchCBox.getSelectedItem() instanceof TileSet) && !(replaceCBox.getSelectedItem() instanceof TileSet))
                replaceAll((Tile) searchCBox.getSelectedItem(),(Tile) replaceCBox.getSelectedItem());
        } else if (command.equals(REPLACE_BUTTON)) {
            if (searchCBox.getSelectedItem() instanceof Tile && replaceCBox.getSelectedItem() instanceof Tile) {
                if (currentMatch == null) {
                    find((Tile)searchCBox.getSelectedItem());
                }

                // run through the layers, look for the first instance of the
                // tile we need to replace
                final Iterator<MapLayer> itr = map.getLayers();
                while (itr.hasNext()) {
                    MapLayer layer = itr.next();
                    if (layer instanceof TileLayer) {
                        if (((TileLayer)layer).getTileAt(currentMatch.x,currentMatch.y) == searchCBox.getSelectedItem()) {
                            ((TileLayer)layer).setTileAt(currentMatch.x,currentMatch.y, (Tile) replaceCBox.getSelectedItem());
                            break;
                        }
                    }
                }
                // find the next instance, effectively stepping forward in our
                // replace
                find((Tile)searchCBox.getSelectedItem());
            }
        }

    }

    private void replaceAll(Tile f, Tile r) {
        // TODO: Allow for "scopes" of one or more layers, rather than all layers
        final Iterator<MapLayer> itr = map.getLayers();
        while (itr.hasNext()) {
            MapLayer layer = itr.next();
            if (layer instanceof TileLayer) {
                ((TileLayer) layer).replaceTile(f,r);
            }
        }
        map.touch();
    }

    private void find(Tile f) {
        boolean bFound = false;

        if (sl != null) {
            map.removeLayerSpecial(sl);
            map.touch();
        }

        sl = new SelectionLayer(map.getWidth(), map.getHeight());
        Rectangle bounds = new Rectangle();

        int startx = currentMatch == null ? 0 : currentMatch.x;
        int starty = currentMatch == null ? 0 : currentMatch.y;

        for (int y = starty; y < map.getHeight() && !bFound; y++) {
            for (int x = startx; x < map.getWidth() && !bFound; x++) {
                final Iterator<MapLayer> itr = map.getLayers();
                while (itr.hasNext()) {
                    MapLayer layer = itr.next();

                    if (layer instanceof TileLayer) {
                        layer.getBounds(bounds);

                        if (((TileLayer)layer).getTileAt(x,y) == searchCBox.getSelectedItem()) {
                            if (currentMatch != null) {
                                if (currentMatch.equals(new Point(x,y))) {
                                    continue;
                                }
                            }
                            sl.select(x,y);
                            bFound = true;
                            currentMatch = new Point(x,y);
                            break;
                        }
                    }
                }
            }
        }

        if (bFound) {
            map.addLayerSpecial(sl);
            map.touch();
        }
    }
}
