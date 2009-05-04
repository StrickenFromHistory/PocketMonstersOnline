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

/**
 * @version $Id$
 */
public class TiledProperty
{
    private String value;
    private String min;
    private String max;
    private String type;

    public TiledProperty() {
    }

    public TiledProperty(String value, String min, String max, String type) {
        set(value, min, max, type);
    }

    public void set(String value, String min, String max, String type) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.type = type;
    }
}
