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

package tiled.mapeditor.brush;

import java.awt.Rectangle;
import java.awt.geom.Area;

import tiled.core.MultilayerPlane;
import tiled.core.TileLayer;
import tiled.util.MersenneTwister;

/**
 * @version $Id$
 */
public class RandomBrush extends ShapeBrush
{
    private final MersenneTwister mt;
    private double ratio = 0.5;

    public RandomBrush(Area shape) {
        super(shape);
        mt = new MersenneTwister(System.currentTimeMillis());
    }

    public RandomBrush(AbstractBrush sb) {
        super(sb);
        mt = new MersenneTwister(System.currentTimeMillis());
        if (sb instanceof RandomBrush) {
            ratio = ((RandomBrush)sb).ratio;
        }
    }

    public void setRatio(double r) {
        ratio = r;
    }

    public double getRatio() {
        return ratio;
    }

    /**
     * Uses the MersenneTwister to fill in a random amount of the area
     * of the brush. Uses the formula: x % 101 &lt;= 100*ratio where, 'x'
     * is a random number, to determine if a specific tile should be
     * painted or not
     *
     * @see ShapeBrush#doPaint
     * @return a Rectangle of the bounds of the area that was modified
     * @param x  The x-coordinate where the click occurred.
     * @param y  The y-coordinate where the click occurred.
     */
    public Rectangle doPaint(int x, int y)
    {
        Rectangle shapeBounds = shape.getBounds();
        int centerx = x - shapeBounds.width / 2;
        int centery = y - shapeBounds.height / 2;

        for (int i = 0; i < numLayers; i++) {
            TileLayer tl = (TileLayer) affectedMp.getLayer(initLayer - i);
            if (tl != null) {
                for (int cy = 0; cy <= shapeBounds.height; cy++) {
                    for (int cx = 0; cx < shapeBounds.width; cx++) {
                        if (shape.contains(cx, cy) &&
                                mt.genrand() % 101 <= 100 * ratio)
                        {
                            tl.setTileAt(
                                    cx + centerx, cy + centery, paintTile);
                        }
                    }
                }
            }
        }

        return new Rectangle(
                centerx, centery, shapeBounds.width, shapeBounds.height);
    }
}
