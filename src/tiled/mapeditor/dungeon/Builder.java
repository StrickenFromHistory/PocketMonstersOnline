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

package tiled.mapeditor.dungeon;

import tiled.core.Map;

abstract public class Builder
{
    public static final int NORTH = 1;
    public static final int EAST = 2;
    public static final int SOUTH = 3;
    public static final int WEST = 4;

    protected int movesPerIteration = 0;
    protected int direction = NORTH;
    protected int wallTileId = 0,
              floorTileId = 0,
              doorTileId = 0;
    private int ttl;
    protected int mapx, mapy;

    public Builder() {
    }

    public Builder(int x, int y, int dir) {
        mapx = x;
        mapy = y;
        direction = dir;
    }

    public void decrementTtl() {
        ttl--;
    }

    public int getTtl() {
        return ttl;
    }

    public abstract void iterate();
    public abstract Builder spawn();
    public abstract void store(Map m);
}
