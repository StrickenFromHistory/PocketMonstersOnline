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

package tiled.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * This class provides access to nodes in the user tiled preferences tree. In
 * addition it provides a number of related convenience methods.
 *
 * @version $Id$
 */
public final class TiledConfiguration
{
    public static final int RECENT_FILE_COUNT = 8;

    private static final Preferences prefs = Preferences.userRoot().node("tiled");

    // Prevent instanciation
    private TiledConfiguration() {
    }

    /**
     * Returns the node with the given path name relative from the root of
     * Tiled configuration.
     *
     * @param pathName the path name relative from the root
     * @return the requested preferences node
     */
    public static Preferences node(String pathName) {
        return prefs.node(pathName);
    }

    /**
     * Returns the root node for Tiled configuration.
     *
     * @return the root node for Tiled configuration
     */
    public static Preferences root() {
        return prefs;
    }

    /**
     * Adds the given filename to the top of the recent file list. It also
     * makes sure it does not occur further down the list.
     *
     * @param mapFile the absolute path of the file to add, must not be
     *                <code>null</code>
     */
    public static void addToRecentFiles(String mapFile) {
        assert mapFile != null;

        // Get the existing recent file list
        List<String> recent = getRecentFiles();

        // Remove all existing occurences of the file
        Iterator iterator = recent.iterator();
        while (iterator.hasNext()) {
            String filename = (String) iterator.next();
            if (filename.equals(mapFile)) {
                iterator.remove();
            }
        }

        // Add the given map file to the top
        recent.add(0, mapFile);

        // Store the new recent file listing
        Preferences recentNode = prefs.node("recent");
        for (int i = 0; i < RECENT_FILE_COUNT && i < recent.size(); i++)
        {
            String recentFile = recent.get(i);
            recentNode.put("file" + i, recentFile);
        }
    }

    /**
     * Returns the list of recently used files.
     *
     * @return the list of recently used files
     */
    public static List<String> getRecentFiles() {
        List<String> recent = new ArrayList<String>(RECENT_FILE_COUNT);
        Preferences recentNode = prefs.node("recent");
        for (int i = 0; i < RECENT_FILE_COUNT; i++)
        {
            String recentFile = recentNode.get("file" + i, "");
            if (recentFile.length() > 0) {
                recent.add(recentFile);
            }
        }
        return recent;
    }

    public static String fileDialogStartLocation() {
        return node("recent").get("file0", null);
    }
}
