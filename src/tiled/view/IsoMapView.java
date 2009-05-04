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

package tiled.view;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.SwingConstants;

import tiled.core.Map;
import tiled.core.ObjectGroup;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.mapeditor.selection.SelectionLayer;

/**
 * @version $Id$
 */
public class IsoMapView extends MapView
{
    /**
     * Creates a new isometric map view that displays the specified map.
     *
     * @param map the map to be displayed by this map view
     */
    public IsoMapView(Map map) {
        super(map);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        Dimension tsize = getTileSize();
        if (orientation == SwingConstants.VERTICAL) {
            return (visibleRect.height / tsize.height) * tsize.height;
        } else {
            return (visibleRect.width / tsize.width) * tsize.width;
        }
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        Dimension tsize = getTileSize();
        if (orientation == SwingConstants.VERTICAL) {
            return tsize.height;
        } else {
            return tsize.width;
        }
    }

    protected void paintLayer(Graphics2D g2d, TileLayer layer) {
        // Turn anti alias on for selection drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle clipRect = g2d.getClipBounds();
        Dimension tileSize = getTileSize();
        int tileStepY = tileSize.height / 2 == 0 ? 1 : tileSize.height / 2;
        Polygon gridPoly = createGridPolygon(0, -tileSize.height, 0);

        Point rowItr = screenToTileCoords(clipRect.x, clipRect.y);
        rowItr.x--;
        Point drawLoc = tileToScreenCoords(rowItr.x, rowItr.y);
        drawLoc.x -= tileSize.width / 2;
        drawLoc.y += tileSize.height;

        // Determine area to draw from clipping rectangle
        int columns = clipRect.width / tileSize.width + 3;
        int rows = (clipRect.height + (int)(map.getTileHeightMax() * zoom)) /
            tileStepY + 4;

        // Draw this map layer
        for (int y = 0; y < rows; y++) {
            Point columnItr = new Point(rowItr);

            for (int x = 0; x < columns; x++) {
                Tile tile = layer.getTileAt(columnItr.x, columnItr.y);

                if (tile != null) {
                    if (layer instanceof SelectionLayer) {
                        //Polygon gridPoly = createGridPolygon(
                                //drawLoc.x, drawLoc.y - tileSize.height, 0);
                        gridPoly.translate(drawLoc.x, drawLoc.y);
                        g2d.fillPolygon(gridPoly);
                        gridPoly.translate(-drawLoc.x, -drawLoc.y);
                        //paintEdge(g2d, layer, drawLoc.x, drawLoc.y);
                    } else {
                        tile.draw(g2d, drawLoc.x, drawLoc.y, zoom);
                    }
                }

                // Advance to the next tile
                columnItr.x++;
                columnItr.y--;
                drawLoc.x += tileSize.width;
            }

            // Advance to the next row
            if ((y & 1) > 0) {
                rowItr.x++;
                drawLoc.x += tileSize.width / 2;
            } else {
                rowItr.y++;
                drawLoc.x -= tileSize.width / 2;
            }
            drawLoc.x -= columns * tileSize.width;
            drawLoc.y += tileStepY;
        }
    }

    protected void paintObjectGroup(Graphics2D g2d, ObjectGroup og) {
        // TODO: Implement objectgroup painting for IsoMapView
    }

    protected void paintGrid(Graphics2D g2d) {
        Dimension tileSize = getTileSize();
        Rectangle clipRect = g2d.getClipBounds();

        clipRect.x -= tileSize.width / 2;
        clipRect.width += tileSize.width;
        clipRect.height += tileSize.height / 2;

        int startX = Math.max(0, screenToTileCoords(clipRect.x, clipRect.y).x);
        int startY = Math.max(0, screenToTileCoords(
                    clipRect.x + clipRect.width, clipRect.y).y);
        int endX = Math.min(map.getWidth(), screenToTileCoords(
                    clipRect.x + clipRect.width,
                    clipRect.y + clipRect.height).x);
        int endY = Math.min(map.getHeight(), screenToTileCoords(
                    clipRect.x, clipRect.y + clipRect.height).y);

        for (int y = startY; y <= endY; y++) {
            Point start = tileToScreenCoords(startX, y);
            Point end = tileToScreenCoords(endX, y);
            g2d.drawLine(start.x, start.y, end.x, end.y);
        }
        for (int x = startX; x <= endX; x++) {
            Point start = tileToScreenCoords(x, startY);
            Point end = tileToScreenCoords(x, endY);
            g2d.drawLine(start.x, start.y, end.x, end.y);
        }
    }

    protected void paintCoordinates(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Rectangle clipRect = g2d.getClipBounds();
        Dimension tileSize = getTileSize();
        int tileStepY = tileSize.height / 2 == 0 ? 1 : tileSize.height / 2;
        Font font = new Font("SansSerif", Font.PLAIN, tileSize.height / 4);
        g2d.setFont(font);
        FontRenderContext fontRenderContext = g2d.getFontRenderContext();

        Point rowItr = screenToTileCoords(clipRect.x, clipRect.y);
        rowItr.x--;
        Point drawLoc = tileToScreenCoords(rowItr.x, rowItr.y);
        drawLoc.y += tileSize.height / 2;

        // Determine area to draw from clipping rectangle
        int columns = clipRect.width / tileSize.width + 3;
        int rows = clipRect.height / tileStepY + 4;

        // Draw the coordinates
        for (int y = 0; y < rows; y++) {
            Point columnItr = new Point(rowItr);

            for (int x = 0; x < columns; x++) {
                if (map.contains(columnItr.x, columnItr.y)) {
                    String coords =
                        "(" + columnItr.x + "," + columnItr.y + ")";
                    Rectangle2D textSize =
                        font.getStringBounds(coords, fontRenderContext);

                    int fx = drawLoc.x - (int)(textSize.getWidth() / 2);
                    int fy = drawLoc.y + (int)(textSize.getHeight() / 2);

                    g2d.drawString(coords, fx, fy);
                }

                // Advance to the next tile
                columnItr.x++;
                columnItr.y--;
                drawLoc.x += tileSize.width;
            }

            // Advance to the next row
            if ((y & 1) > 0) {
                rowItr.x++;
                drawLoc.x += tileSize.width / 2;
            } else {
                rowItr.y++;
                drawLoc.x -= tileSize.width / 2;
            }
            drawLoc.x -= columns * tileSize.width;
            drawLoc.y += tileStepY;
        }
    }

    protected void paintPropertyFlags(Graphics2D g2d, TileLayer layer) {
        throw new RuntimeException("Not yet implemented");    // todo
    }

    public void repaintRegion(Rectangle region) {
        Dimension tileSize = getTileSize();
        int maxExtraHeight =
            (int)(map.getTileHeightMax() * zoom) - tileSize.height;

        int mapX1 = region.x;
        int mapY1 = region.y;
        int mapX2 = mapX1 + region.width;
        int mapY2 = mapY1 + region.height;

        int x1 = tileToScreenCoords(mapX1, mapY2).x;
        int y1 = tileToScreenCoords(mapX1, mapY1).y - maxExtraHeight;
        int x2 = tileToScreenCoords(mapX2, mapY1).x;
        int y2 = tileToScreenCoords(mapX2, mapY2).y;

        repaint(new Rectangle(x1, y1, x2 - x1, y2 - y1));
    }

    public Dimension getPreferredSize() {
        Dimension tileSize = getTileSize();
        int border = showGrid ? 1 : 0;
        int mapSides = map.getHeight() + map.getWidth();

        return new Dimension(
                (mapSides * tileSize.width) / 2 + border,
                (mapSides * tileSize.height) / 2 + border);
    }

    /**
     * Returns the coordinates of the tile at the given screen coordinates.
     */
    public Point screenToTileCoords(int x, int y) {
        Dimension tileSize = getTileSize();
        double r = getTileRatio();

        // Translate origin to top-center
        x -= map.getHeight() * (tileSize.width / 2);
        int mx = y + (int)(x / r);
        int my = y - (int)(x / r);

        // Calculate map coords and divide by tile size (tiles assumed to
        // be square in normal projection)
        return new Point(
                (mx < 0 ? mx - tileSize.height : mx) / tileSize.height,
                (my < 0 ? my - tileSize.height : my) / tileSize.height);
    }

    public Point screenToPixelCoords(int x, int y) {
        // TODO: add proper implementation
        return new Point();
    }

    protected Polygon createGridPolygon(int tx, int ty, int border) {
        Dimension tileSize = getTileSize();
        tileSize.width -= border * 2;
        tileSize.height -= border * 2;

        Polygon poly = new Polygon();
        poly.addPoint(tx + tileSize.width / 2 + border, ty + border);
        poly.addPoint(tx + tileSize.width, ty + tileSize.height / 2 + border);
        poly.addPoint(tx + tileSize.width / 2 + border,
                ty + tileSize.height + border);
        poly.addPoint(tx + border, ty + tileSize.height / 2 + border);
        return poly;
    }

    protected Dimension getTileSize() {
        return new Dimension(
                (int)(map.getTileWidth() * zoom),
                (int)(map.getTileHeight() * zoom));
    }

    protected double getTileRatio() {
        return (double)map.getTileWidth() / (double)map.getTileHeight();
    }

    /**
     * Returns the location on the screen of the top corner of a tile.
     */
    public Point tileToScreenCoords(int x, int y) {
        Dimension tileSize = getTileSize();
        int originX = (map.getHeight() * tileSize.width) / 2;
        return new Point(
                ((x - y) * tileSize.width / 2) + originX,
                ((x + y) * tileSize.height / 2));
    }
}
