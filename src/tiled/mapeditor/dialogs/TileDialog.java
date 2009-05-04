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
 *  Rainer Deyke <rainerd@eldwood.com>
 */

package tiled.mapeditor.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileSet;
import tiled.mapeditor.Resources;
import tiled.mapeditor.util.ImageCellRenderer;
import tiled.mapeditor.util.PropertiesTableModel;
import tiled.mapeditor.util.TileDialogListRenderer;
import tiled.mapeditor.widget.VerticalStaticJPanel;

/**
 * @version $Id$
 */
public class TileDialog extends JDialog
    implements ActionListener, ListSelectionListener
{
    private Tile currentTile;
    private TileSet tileset;
    private Map map;
    private JList tileList;
    private JList imageList;
    private JTable tileProperties;
    private JButton okButton;
    private JButton addImagesButton;
    private JButton deleteTileButton;
    private JButton changeImageButton;
    private JButton duplicateTileButton;
    private JButton createTileButton;
    //private JButton animationButton;
    private String location;
    private JTextField tilesetNameEntry;
    private JTabbedPane tabs;
    private int currentImageIndex = -1;

    private static final String DIALOG_TITLE = Resources.getString("dialog.tile.title");
    private static final String OK_BUTTON = Resources.getString("general.button.ok");
    private static final String DELETE_BUTTON = Resources.getString("dialog.tile.button.deletetile");
    private static final String CI_BUTTON = Resources.getString("dialog.tile.button.changeimage");
    private static final String ADD_IMAGES_BUTTON = Resources.getString("dialog.tile.button.addimages");
    private static final String CREATE_BUTTON = Resources.getString("dialog.tile.button.createtile");
    private static final String DUPLICATE_BUTTON = Resources.getString("dialog.tile.button.duptile");
    private static final String ANIMATION_BUTTON = Resources.getString("dialog.tile.button.animation");
    private static final String PREVIEW_TAB = Resources.getString("dialog.tile.tab.view");
    private static final String TILES_TAB = Resources.getString("general.tile.tiles");
    private static final String IMAGES_TAB = "Images";
    private static final String NAME_LABEL = Resources.getString("dialog.newtileset.name.label");
    private static final String ERROR_LOADING_IMAGE = Resources.getString("dialog.tile.image.load.error");
    private static final String TILES_CREATED_MESSAGE = Resources.getString("action.tile.create.done.message");
    private static final String TILES_CREATED_TITLE = Resources.getString("action.tile.create.done.title");


    public TileDialog(Dialog parent, TileSet tileset, Map map) {
        super(parent, DIALOG_TITLE + " '" + tileset.getName() + "'", true);
        location = "";
        this.tileset = tileset;    //unofficial
        this.map = map;        //also unofficial
        init();
        setTileset(tileset);
        setCurrentTile(null);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private JPanel createTilePanel() {
        // Create the buttons
        deleteTileButton = new JButton(DELETE_BUTTON);
        changeImageButton = new JButton(CI_BUTTON);
        duplicateTileButton = new JButton(DUPLICATE_BUTTON);
        //animationButton = new JButton(ANIMATION_BUTTON);

        deleteTileButton.addActionListener(this);
        changeImageButton.addActionListener(this);
        duplicateTileButton.addActionListener(this);
        //animationButton.addActionListener(this);

        // Tile properties table
        tileProperties = new JTable(new PropertiesTableModel());
        tileProperties.getSelectionModel().addListSelectionListener(this);
        JScrollPane propScrollPane = new JScrollPane(tileProperties);
        propScrollPane.setPreferredSize(new Dimension(150, 150));

        // Tile list
        tileList = new JList();
        tileList.setCellRenderer(new TileDialogListRenderer());
        tileList.addListSelectionListener(this);
        JScrollPane sp = new JScrollPane();
        sp.getViewport().setView(tileList);
        sp.setPreferredSize(new Dimension(150, 150));

        // The split pane
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        splitPane.setResizeWeight(0.25);
        splitPane.setLeftComponent(sp);
        splitPane.setRightComponent(propScrollPane);

        // The buttons
        JPanel buttons = new VerticalStaticJPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(deleteTileButton);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(changeImageButton);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(duplicateTileButton);
        //buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        //buttons.add(animationButton);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(Box.createGlue());

        // Putting it all together
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1; c.weighty = 1;
        mainPanel.add(splitPane, c);
        c.weightx = 0; c.weighty = 0; c.gridy = 1;
        mainPanel.add(buttons, c);

        return mainPanel;
    }

    private JPanel createImagePanel()
    {
        imageList = new JList();
        imageList.setCellRenderer(new ImageCellRenderer());
        imageList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        imageList.addListSelectionListener(this);
        JScrollPane sp = new JScrollPane(imageList);
        sp.setPreferredSize(new Dimension(250, 150));

        // Buttons
        createTileButton = new JButton(CREATE_BUTTON);
        addImagesButton = new JButton(ADD_IMAGES_BUTTON);
        createTileButton.addActionListener(this);
        addImagesButton.addActionListener(this);
        JPanel buttons = new VerticalStaticJPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(addImagesButton);
        buttons.add(Box.createRigidArea(new Dimension(5, 0)));
        buttons.add(createTileButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1; c.weighty = 1;
        mainPanel.add(sp, c);
        c.weightx = 0; c.weighty = 0; c.gridy = 1;
        mainPanel.add(buttons, c);
        return mainPanel;
    }

    private void init() {
        // Tileset name field at the top
        JLabel nameLabel = new JLabel(NAME_LABEL);
        tilesetNameEntry = new JTextField(32);
        JPanel namePanel = new VerticalStaticJPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.add(nameLabel);
        namePanel.add(Box.createRigidArea(new Dimension(5, 5)));
        namePanel.add(tilesetNameEntry);

        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.addTab(TILES_TAB, createTilePanel());
        tabs.addTab(IMAGES_TAB, createImagePanel());

        okButton = new JButton(OK_BUTTON);

        JPanel buttons = new VerticalStaticJPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.add(Box.createGlue());
        buttons.add(okButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(namePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(tabs);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(buttons);

        getContentPane().add(mainPanel);
        getRootPane().setDefaultButton(okButton);

        // Create actionlisteners
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                tileset.setName(tilesetNameEntry.getText());
                applyTileProperties();
                dispose();
            }
        });
    }

    private void changeImage() {
        if (currentTile == null) {
            return;
        }

        TileImageDialog dialog = new TileImageDialog(this, tileset,
            currentTile.getImageId());
        dialog.setVisible(true);
        if (dialog.getImageId() >= 0) {
            currentTile.setImage(dialog.getImageId());
        }
    }

    private void addImages() {
        File[] files;
        JFileChooser fc = new JFileChooser(location);
        fc.setMultiSelectionEnabled(true);

        int ret = fc.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            files = fc.getSelectedFiles();

            for (File file : files) {
                BufferedImage image;
                try {
                    image = ImageIO.read(file);
                    // TODO: Support for a transparent color
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(
                            this, e.getLocalizedMessage(),
                            "Error!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                tileset.addImage(image);
            }

            // Start here next time images are added
            if (files.length > 0) {
                location = files[0].getAbsolutePath();
            }
        }

        queryImages();
    }

    public void setTileset(TileSet set) {
        tileset = set;

        if (tileset != null) {
            // Find new tile images at the location of the tileset
            if (tileset.getSource() != null) {
                location = tileset.getSource();
            } else if (map != null) {
                location = map.getFilename();
            }
            tilesetNameEntry.setText(tileset.getName());
        }

        queryTiles();
        queryImages();
        updateEnabledState();
    }

    public void queryTiles() {
        Vector<Tile> listData;

        if (tileset != null && tileset.size() > 0) {
            listData = new Vector<Tile>();
            Iterator tileIterator = tileset.iterator();

            while (tileIterator.hasNext()) {
                Tile tile = (Tile) tileIterator.next();
                listData.add(tile);
            }

            tileList.setListData(listData);
        }

        if (currentTile != null) {
            tileList.setSelectedIndex(currentTile.getId() - 1);
            tileList.ensureIndexIsVisible(currentTile.getId() - 1);
        }
    }

    public void queryImages() {
        Vector<Image> listData = new Vector<Image>();

        Enumeration<String> ids = tileset.getImageIds();
        while(ids.hasMoreElements()) {
            Image img = tileset.getImageById(Integer.parseInt(ids.nextElement()));
            if (img != null)
                listData.add(img);
        }

        imageList.setListData(listData);
        if (currentImageIndex != -1) {
            imageList.setSelectedIndex(currentImageIndex);
            imageList.ensureIndexIsVisible(currentImageIndex);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == tileList) {
            setCurrentTile((Tile) tileList.getSelectedValue());
        } else if (e.getSource() == imageList) {
            setImageIndex(imageList.getSelectedIndex());
        }
    }

    private void setCurrentTile(Tile tile) {
        applyTileProperties();

        currentTile = tile;
        updateTileInfo();
        updateEnabledState();
    }

    private void applyTileProperties() {
        // Make sure there is no active cell editor anymore
        TableCellEditor editor = tileProperties.getCellEditor();
        if (editor != null) {
            editor.stopCellEditing();
        }

        // Apply possibly changed properties
        if (currentTile != null) {
            PropertiesTableModel model =
                    (PropertiesTableModel) tileProperties.getModel();
            currentTile.setProperties(model.getProperties());
        }
    }

    private void setImageIndex(int i) {
        currentImageIndex = i;
        updateEnabledState();
    }

    private void updateEnabledState() {
        boolean tilebmp = tileset.getTilebmpFile() != null;
        boolean tileSelected = currentTile != null;
        boolean atLeastOneSharedImage = tileset.getTotalImages() >= 1;
        boolean imageSelected = imageList.getSelectedValue() != null;

        deleteTileButton.setEnabled(!tilebmp && tileSelected);
        changeImageButton.setEnabled(atLeastOneSharedImage && !tilebmp
            && tileSelected);
        duplicateTileButton.setEnabled(!tilebmp && tileSelected);
        //animationButton.setEnabled(!tilebmp && tileSelected &&
        //        currentTile instanceof AnimatedTile);
        tileProperties.setEnabled(tileSelected);
        addImagesButton.setEnabled(!tilebmp);
        createTileButton.setEnabled(!tilebmp && imageSelected);
    }

    /**
     * Updates the properties table with the properties of the current tile.
     */
    private void updateTileInfo() {
        PropertiesTableModel model =
                (PropertiesTableModel) tileProperties.getModel();

        if (currentTile == null) {
            model.setProperties(new Properties());
        }
        else {
            model.setProperties(currentTile.getProperties());
        }
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == deleteTileButton) {
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    Resources.getString("action.tile.delete.confirm.message"),
                    Resources.getString("action.tile.delete.confirm.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                Tile tile = (Tile)tileList.getSelectedValue();
                if (tile != null) {
                    tileset.removeTile(tile.getId());
                }
                queryTiles();
            }
        } else if (source == changeImageButton) {
            changeImage();
        } else if (source == addImagesButton) {
            addImages();
        } else if (source == duplicateTileButton) {
            Tile newTile = new Tile(currentTile);
            tileset.addNewTile(newTile);
            queryTiles();
            // Select the last (cloned) tile
            tileList.setSelectedIndex(tileset.size() - 1);
            tileList.ensureIndexIsVisible(tileset.size() - 1);
        //} else if (source == animationButton) {
        //    AnimationDialog ad = new AnimationDialog(this, ((AnimatedTile)currentTile).getSprite());
        //    ad.setVisible(true);
        }
        /*
        else if (source == setImagesCheck) {
            if (setImagesCheck.isSelected()) {
                tileset.enablesetImages();
                updateEnabledState();
            } else {
                int answer = JOptionPane.YES_OPTION;
                if (!tileset.safeToDisablesetImages()) {
                    answer = JOptionPane.showConfirmDialog(
                        this, "This tileset uses features that require the "
                        + "use of shared images.  Disable the use of shared "
                        + "images?",
                        "Are you sure?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                }
                if (answer == JOptionPane.YES_OPTION) {
                    tileset.disablesetImages();
                    updateEnabledState();
                } else {
                    setImagesCheck.setSelected(true);
                }
            }
        }
        */
        else if (source == createTileButton) {
            Object[] imgs = imageList.getSelectedValues();
            if (imgs.length == 0)
                return;

            for (Object img : imgs) {
                Tile newTile = new Tile(tileset);
                newTile.setImage(tileset.getIdByImage((Image) img));
                tileset.addNewTile(newTile);
            }

            queryTiles();
            // Select the last (cloned) tile
            tileList.setSelectedIndex(tileset.size() - 1);
            tileList.ensureIndexIsVisible(tileset.size() - 1);
            JOptionPane.showMessageDialog(
                    this,
                    MessageFormat.format(TILES_CREATED_MESSAGE, imgs.length),
                    TILES_CREATED_TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
        }

        repaint();
    }
}
