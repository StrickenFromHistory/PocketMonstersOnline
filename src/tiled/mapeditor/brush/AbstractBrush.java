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

import java.awt.*;

import tiled.core.MultilayerPlane;
import tiled.view.MapView;


public abstract class AbstractBrush extends MultilayerPlane implements Brush
{
    protected int numLayers = 1;
    protected MultilayerPlane affectedMp;
    protected boolean paintingStarted = false;
    protected int initLayer;

    public AbstractBrush() {
    }

    public AbstractBrush(AbstractBrush ab) {
        numLayers = ab.numLayers;
    }

    /**
     * This will set the number of layers to affect, the default is 1 - the
     * layer specified in commitPaint.
     *
     * @see Brush#doPaint(int, int)
     * @param num   the number of layers to affect.
     */
    public void setAffectedLayers(int num) {
        numLayers = num;
    }

    public int getAffectedLayers() {
        return numLayers;
    }

    public void startPaint(MultilayerPlane mp, int x, int y, int button, int layer) {
        affectedMp = mp;
        initLayer = layer;
        paintingStarted = true;
    }

    public Rectangle doPaint(int x, int y) throws Exception {
        if (!paintingStarted) throw new Exception("Attempted to call doPaint() without calling startPaint()!");
        return null;
    }

    public void endPaint() {
        paintingStarted = false;
    }

    public void drawPreview(Graphics2D g2d, Dimension dimension, MapView mv) {
        // todo: draw an off-map preview here
    }

    public abstract Shape getShape();
}
