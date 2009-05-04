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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.undo.UndoableEditSupport;

import tiled.core.MapObject;
import tiled.mapeditor.Resources;
import tiled.mapeditor.undo.ChangeObjectEdit;
import tiled.mapeditor.widget.IntegerSpinner;
import tiled.mapeditor.widget.VerticalStaticJPanel;
import tiled.util.TiledConfiguration;

/**
 * A dialog for editing the name, type, size and properties of an object.
 *
 * @version $Id$
 */
public class ObjectDialog extends PropertiesDialog
{
    private JTextField objectName, objectType;
    private JTextField objectImageSource;
    private IntegerSpinner objectWidth, objectHeight;
    private final MapObject object;
    private final UndoableEditSupport undoSupport;

    private static String path;

    /* LANGUAGE PACK */
    private static final String DIALOG_TITLE = Resources.getString("dialog.object.title");
    private static final String NAME_LABEL = Resources.getString("dialog.object.name.label");
    private static final String TYPE_LABEL = Resources.getString("dialog.object.type.label");
    private static final String IMAGE_LABEL = Resources.getString("dialog.object.image.label");
    private static final String WIDTH_LABEL = Resources.getString("dialog.object.width.label");
    private static final String HEIGHT_LABEL = Resources.getString("dialog.object.height.label");
    private static final String UNTITLED_OBJECT = Resources.getString("general.object.object");
    private static final String BROWSE_BUTTON = Resources.getString("general.button.browse");

    public ObjectDialog(JFrame parent, MapObject object, UndoableEditSupport undoSupport) {
        super(parent, object.getProperties());
        this.object = object;
        this.undoSupport = undoSupport;
        setTitle(DIALOG_TITLE);
        pack();
        setLocationRelativeTo(parent);
    }

    public void init() {
        super.init();
        JLabel nameLabel = new JLabel(NAME_LABEL);
        JLabel typeLabel = new JLabel(TYPE_LABEL);
        JLabel imageLabel = new JLabel(IMAGE_LABEL);
        JLabel widthLabel = new JLabel(WIDTH_LABEL);
        JLabel heightLabel = new JLabel(HEIGHT_LABEL);

        objectName = new JTextField(UNTITLED_OBJECT);
        objectType = new JTextField();
        objectImageSource = new JTextField();
        objectWidth = new IntegerSpinner(0, 0, 1024);
        objectHeight = new IntegerSpinner(0, 0, 1024);

        final JButton browseButton = new JButton(BROWSE_BUTTON);
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String startLocation = path;
                if (startLocation == null) {
                    startLocation = TiledConfiguration.fileDialogStartLocation();
                }
                JFileChooser ch = new JFileChooser(startLocation);

                int ret = ch.showOpenDialog(ObjectDialog.this);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    path = ch.getSelectedFile().getAbsolutePath();
                    objectImageSource.setText(path);
                }
            }
        });

        // Combine browse button and image source text field
        JPanel imageSourcePanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        imageSourcePanel.add(objectImageSource, c);
        c.gridx = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 5, 0, 0);
        imageSourcePanel.add(browseButton, c);

        JPanel miscPropPanel = new VerticalStaticJPanel();
        miscPropPanel.setLayout(new GridBagLayout());
        miscPropPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 0, 0, 5);
        miscPropPanel.add(nameLabel, c);
        c.gridy = 1;
        miscPropPanel.add(typeLabel, c);
        c.gridy = 2;
        miscPropPanel.add(imageLabel, c);
        c.gridy = 3;
        miscPropPanel.add(widthLabel, c);
        c.gridy = 4;
        miscPropPanel.add(heightLabel, c);
        c.insets = new Insets(5, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        miscPropPanel.add(objectName, c);
        c.gridy = 1;
        miscPropPanel.add(objectType, c);
        c.gridy = 2;
        miscPropPanel.add(imageSourcePanel, c);
        c.gridy = 3;
        miscPropPanel.add(objectWidth, c);
        c.gridy = 4;
        miscPropPanel.add(objectHeight, c);

        mainPanel.add(miscPropPanel, 0);
    }

    public void updateInfo() {
        super.updateInfo();
        objectName.setText(object.getName());
        objectType.setText(object.getType());
        objectImageSource.setText(object.getImageSource());
        objectWidth.setValue(object.getWidth());
        objectHeight.setValue(object.getHeight());
    }

    protected void buildPropertiesAndDispose() {
        // Make sure the changes to the object can be undone
        undoSupport.postEdit(new ChangeObjectEdit(object));

        object.setName(objectName.getText());
        object.setType(objectType.getText());
        object.setImageSource(objectImageSource.getText());
        object.setWidth(objectWidth.intValue());
        object.setHeight(objectHeight.intValue());
        super.buildPropertiesAndDispose();
    }
}
