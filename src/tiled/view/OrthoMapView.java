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
import java.util.Iterator;
import java.util.Properties;
import javax.swing.SwingConstants;

import tiled.core.*;
import tiled.mapeditor.selection.SelectionLayer;

/**
 * An orthographic map view.
 */
public class OrthoMapView extends MapView
{
    private Polygon propPoly;

    /**
     * Creates a new orthographic map view that displays the specified map.
     *
     * @param map the map to be displayed by this map view
     */
    public OrthoMapView(Map map) {
        super(map);

        propPoly = new Polygon();
        propPoly.addPoint(0, 0);
        propPoly.addPoint(12, 0);
        propPoly.addPoint(12, 12);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        Dimension tsize = getTileSize();

        if (orientation == SwingConstants.VERTICAL) {
            return (visibleRect.height / tsize.height) * tsize.height;
        }
        else {
            return (visibleRect.width / tsize.width) * tsize.width;
        }
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        Dimension tsize = getTileSize();
        if (orientation == SwingConstants.VERTICAL) {
            return tsize.height;
        }
        else {
            return tsize.width;
        }
    }

    public Dimension getPreferredSize() {
        Dimension tsize = getTileSize();

        return new Dimension(
                map.getWidth() * tsize.width,
                map.getHeight() * tsize.height);
    }

    protected void paintLayer(Graphics2D g2d, TileLayer layer) {
        // Determine tile size and offset
        Dimension tsize = getTileSize();
        if (tsize.width <= 0 || tsize.height <= 0) {
            return;
        }
        Polygon gridPoly = createGridPolygon(0, -tsize.height, 0);

        // Determine area to draw from clipping rectangle
        Rectangle clipRect = g2d.getClipBounds();
        int startX = clipRect.x / tsize.width;
        int startY = clipRect.y / tsize.height;
        int endX = (clipRect.x + clipRect.width) / tsize.width + 1;
        int endY = (clipRect.y + clipRect.height) / tsize.height + 3;
        // (endY +2 for high tiles, could be done more properly)

        // Draw this map layer
        for (int y = startY, gy = (startY + 1) * tsize.height;
                y < endY; y++, gy += tsize.height) {
            for (int x = startX, gx = startX * tsize.width;
                    x < endX; x++, gx += tsize.width) {
                Tile tile = layer.getTileAt(x, y);

                if (tile != null) {
                    if (layer instanceof SelectionLayer) {
                        gridPoly.translate(gx, gy);
                        g2d.fillPolygon(gridPoly);
                        gridPoly.translate(-gx, -gy);
                        //paintEdge(g, layer, gx, gy);
                    }
                    else {
                        tile.draw(g2d, gx, gy, zoom);
                    }
                }
            }
        }
    }

    protected void paintObjectGroup(Graphics2D g2d, ObjectGroup og) {
        final Dimension tsize = getTileSize();
        final Rectangle bounds = og.getBounds();
        Iterator<MapObject> itr = og.getObjects();
        g2d.translate(
                bounds.x * tsize.width,
                bounds.y * tsize.height);

        while (itr.hasNext()) {
            MapObject mo = itr.next();
            double ox = mo.getX() * zoom;
            double oy = mo.getY() * zoom;

            Image objectImage = mo.getImage(zoom);
            if (objectImage != null) {
                g2d.drawImage(objectImage, (int) ox, (int) oy, null);
            }

            if (mo.getWidth() == 0 || mo.getHeight() == 0) {
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.black);
                g2d.fillOval((int) ox + 1, (int) oy + 1,
                        (int) (10 * zoom), (int) (10 * zoom));
                g2d.setColor(Color.orange);
                g2d.fillOval((int) ox, (int) oy,
                        (int) (10 * zoom), (int) (10 * zoom));
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_OFF);
            } else {
                g2d.setColor(Color.black);
                g2d.drawRect((int) ox + 1, (int) oy + 1,
                    (int) (mo.getWidth() * zoom),
                    (int) (mo.getHeight() * zoom));
                g2d.setColor(Color.orange);
                g2d.drawRect((int) ox, (int) oy,
                    (int) (mo.getWidth() * zoom),
                    (int) (mo.getHeight() * zoom));
            }
            if (zoom > 0.0625) {
                final String s = mo.getName() != null ? mo.getName() : "(null)";
                g2d.setColor(Color.black);
                g2d.drawString(s, (int) (ox - 5) + 1, (int) (oy - 5) + 1);
                g2d.setColor(Color.white);
                g2d.drawString(s, (int) (ox - 5), (int) (oy - 5));
            }
        }

        g2d.translate(
                -bounds.x * tsize.width,
                -bounds.y * tsize.height);
    }

    protected void paintGrid(Graphics2D g2d) {
        // Determine tile size
        Dimension tsize = getTileSize();
        if (tsize.width <= 0 || tsize.height <= 0) {
            return;
        }

        // Determine lines to draw from clipping rectangle
        Rectangle clipRect = g2d.getClipBounds();
        int startX = clipRect.x / tsize.width * tsize.width;
        int startY = clipRect.y / tsize.height * tsize.height;
        int endX = clipRect.x + clipRect.width;
        int endY = clipRect.y + clipRect.height;

        for (int x = startX; x < endX; x += tsize.width) {
            g2d.drawLine(x, clipRect.y, x, clipRect.y + clipRect.height - 1);
        }
        for (int y = startY; y < endY; y += tsize.height) {
            g2d.drawLine(clipRect.x, y, clipRect.x + clipRect.width - 1, y);
        }
    }

    protected void paintCoordinates(Graphics2D g2d) {
        Dimension tsize = getTileSize();
        if (tsize.width <= 0 || tsize.height <= 0) {
            return;
        }
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Determine tile size and offset
        Font font = new Font("SansSerif", Font.PLAIN, tsize.height / 4);
        g2d.setFont(font);
        FontRenderContext fontRenderContext = g2d.getFontRenderContext();

        // Determine area to draw from clipping rectangle
        Rectangle clipRect = g2d.getClipBounds();
        int startX = clipRect.x / tsize.width;
        int startY = clipRect.y / tsize.height;
        int endX = (clipRect.x + clipRect.width) / tsize.width + 1;
        int endY = (clipRect.y + clipRect.height) / tsize.height + 1;

        // Draw the coordinates
        int gy = startY * tsize.height;
        for (int y = startY; y < endY; y++) {
            int gx = startX * tsize.width;
            for (int x = startX; x < endX; x++) {
                String coords = "(" + x + "," + y + ")";
                Rectangle2D textSize =
                        font.getStringBounds(coords, fontRenderContext);

                int fx = gx + (int) ((tsize.width - textSize.getWidth()) / 2);
                int fy = gy + (int) ((tsize.height + textSize.getHeight()) / 2);

                g2d.drawString(coords, fx, fy);
                gx += tsize.width;
            }
            gy += tsize.height;
        }
    }


    protected void paintPropertyFlags(Graphics2D g2d, TileLayer layer) {
        Dimension tsize = getTileSize();
        if (tsize.width <= 0 || tsize.height <= 0) {
            return;
        }
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setComposite(AlphaComposite.SrcAtop);

        //g2d.setColor(new Color(0.1f, 0.1f, 0.5f, 0.5f));
        g2d.setXORMode(new Color(0.9f, 0.9f, 0.9f, 0.5f));

        // Determine tile size and offset

        // Determine area to draw from clipping rectangle
        Rectangle clipRect = g2d.getClipBounds();
        int startX = clipRect.x / tsize.width;
        int startY = clipRect.y / tsize.height;
        int endX = (clipRect.x + clipRect.width) / tsize.width + 1;
        int endY = (clipRect.y + clipRect.height) / tsize.height + 1;

        int y = startY * tsize.height;

        for (int j = startY; j <= endY; j++) {
            int x = startX * tsize.width;

            for (int i = startX; i <= endX; i++) {
                try {
                    Properties p = layer.getTileInstancePropertiesAt(i, j);
                    if (p != null && !p.isEmpty()) {
                        //g2d.drawString( "PROP", x, y );
                        //g2d.drawImage(MapView.propertyFlagImage, x + (tsize.width - 12), y, null);
                        g2d.translate(x + (tsize.width - 13), y+1);
                        g2d.drawPolygon(propPoly);
                        g2d.translate(-(x + (tsize.width - 13)), -(y+1));
                    }
                }
                catch (Exception e) {
                    System.out.print("Exception\n");
                }

                x += tsize.width;
            }
            y += tsize.height;
        }
    }

    public void repaintRegion(Rectangle region) {
        Dimension tsize = getTileSize();
        if (tsize.width <= 0 || tsize.height <= 0) {
            return;
        }
        int maxExtraHeight =
                (int) (map.getTileHeightMax() * zoom - tsize.height);

        // Calculate the visible corners of the region
        int startX = region.x * tsize.width;
        int startY = region.y * tsize.height - maxExtraHeight;
        int endX = (region.x + region.width) * tsize.width;
        int endY = (region.y + region.height) * tsize.height;

        Rectangle dirty =
            new Rectangle(startX, startY, endX - startX, endY - startY);

        repaint(dirty);
    }

    public Point screenToTileCoords(int x, int y) {
        Dimension tsize = getTileSize();
        return new Point(x / tsize.width, y / tsize.height);
    }

    public Point screenToPixelCoords(int x, int y) {
        return new Point(
                (int) (x / zoom), (int) (y / zoom));
    }

    protected Dimension getTileSize() {
        return new Dimension(
                (int) (map.getTileWidth() * zoom),
                (int) (map.getTileHeight() * zoom));
    }

    protected Polygon createGridPolygon(int tx, int ty, int border) {
        Dimension tsize = getTileSize();

        Polygon poly = new Polygon();
        poly.addPoint(tx - border, ty - border);
        poly.addPoint(tx + tsize.width + border, ty - border);
        poly.addPoint(tx + tsize.width + border, ty + tsize.height + border);
        poly.addPoint(tx - border, ty + tsize.height + border);

        return poly;
    }

    public Point tileToScreenCoords(int x, int y) {
        Dimension tsize = getTileSize();
        return new Point(x * tsize.width, y * tsize.height);
    }
}
