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

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import tiled.mapeditor.Resources;

/**
 * This file chooser extends the {@link javax.swing.JFileChooser} in a number
 * of ways.
 * <ul>
 *   <li>Adds an extention to the filename based on the file filter, when
 *       the user didn't specify any.</li>
 *   <li>If the file to be saved is not accepted by the chosen file filter,
 *       it confirms that the user really wants to do this. This is done
 *       because the same file filter is used to determine with which
 *       plugin to load the file.</li>
 *   <li>Confirms before overwriting an existing file.</li>
 * </ul>
 * Automatic adding of file extension only works with
 * {@link tiled.mapeditor.util.ConfirmableFileFilter}.
 */
public final class ConfirmingFileChooser extends JFileChooser
{
    private static final String UNKNOWN_TYPE_MESSAGE = Resources.getString("dialog.saveas.unknown-type.message");
    private static final String CONFIRM_MISMATCH = Resources.getString("dialog.saveas.confirm.mismatch");
    private static final String CONFIRM_MISMATCH_TITLE = Resources.getString("dialog.saveas.confirm.mismatch.title");
    private static final String FILE_EXISTS_MESSAGE = Resources.getString("general.file.exists.message");
    private static final String FILE_EXISTS_TITLE = Resources.getString("general.file.exists.title");

    public ConfirmingFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    public int showSaveDialog(Component component) throws HeadlessException {
        setDialogTitle(Resources.getString("dialog.saveas.title"));
        return super.showSaveDialog(component);
    }

    public ConfirmingFileChooser() {
        this(null);
    }

    public void approveSelection ()
    {
        // When it's an open dialog, we don't need the extension or overwrite
        // checks. Probably you should just be using JFileChooser.
        if (getDialogType() == OPEN_DIALOG) {
            super.approveSelection();
            return;
        }

        File file = new File(getSelectedFile().getAbsolutePath());

        // If the file does not have an extention, append the default
        // extension specified by the file filter.
        String filename = file.getName();
        int lastDot = filename.lastIndexOf('.');

        if ((lastDot == -1 || lastDot == filename.length() - 1) &&
                getFileFilter() instanceof ConfirmableFileFilter) {
            ConfirmableFileFilter filter =
                    (ConfirmableFileFilter) getFileFilter();
            String extension = filter.getDefaultExtension();

            if (extension == null) {
                // Impossible to determine extension with this filter
                JOptionPane.showMessageDialog(this, UNKNOWN_TYPE_MESSAGE);
                return;
            }

            String newFilePath = file.getAbsolutePath();

            // Add a dot if it wasn't at the end already
            if (lastDot != filename.length() - 1) {
                newFilePath += ".";
            }

            file = new File(newFilePath + extension);
            setSelectedFile(file);
        }

        // Check that chosen plugin accepts the file. It is a good idea to
        // warn the user when this is not the case, because loading the map
        // becomes a problem.
        if (!getFileFilter().accept(file)) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    CONFIRM_MISMATCH, CONFIRM_MISMATCH_TITLE,
                    JOptionPane.YES_NO_OPTION);

            if (result != JOptionPane.OK_OPTION) {
                return;
            }
        }

        // Confirm overwrite if the file happens to exist already
        if (file.exists())
        {
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    FILE_EXISTS_MESSAGE, FILE_EXISTS_TITLE,
                    JOptionPane.YES_NO_OPTION);

            if (answer == JOptionPane.YES_OPTION)
            {
                super.approveSelection();
            }
        }
        else {
            super.approveSelection();
        }
    }
}
