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

import java.util.EventObject;

import tiled.core.TileLayer;

/**
 * An event that describes the selection of a tile region.
 *
 * @version $Id$
 */
public class TileRegionSelectionEvent extends EventObject
{
    private final TileLayer tileLayer;

    public TileRegionSelectionEvent(Object source, TileLayer tileLayer) {
        super(source);
        this.tileLayer = tileLayer;
    }

    public TileLayer getTileRegion() {
        return tileLayer;
    }
}
