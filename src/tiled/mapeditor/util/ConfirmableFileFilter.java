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

import javax.swing.filechooser.FileFilter;

/**
 * A file filter with an additional method to query the default extension used
 * by files filtered with this file filter. Used by the
 * {@link ConfirmingFileChooser} to add this default extension when left out.
 *
 * @version $Id$
 */
public abstract class ConfirmableFileFilter extends FileFilter
{
    /**
     * Returns the default extension used by files filtered with this file
     * filter.
     *
     * @return a string representing the default extension (ie. "tmx"), or
     *         <code>null</code> when a default extension doesn't exist (ie.
     *         when the filter determines the type using the extension)
     */
    public abstract String getDefaultExtension();
}
