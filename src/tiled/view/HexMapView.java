/*
 *  Tiled Map Editor, (c) 2004-2008
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

// for console logging

import java.awt.*;
import java.util.Iterator;

import javax.swing.SwingConstants;

import tiled.core.*;
//import tiled.io.TiledLogger;
import tiled.mapeditor.selection.SelectionLayer;

/**
 * A View for displaying Hex based maps.
 * There are four possible layouts for the hexes. These are called
 * tile alignment and are named 'top', 'bottom', 'left' and 'right'.
 * The name designates the border where the first row or column of
 * hexes is aligned with a flat side. I.e. 'left' and 'right' result
 * in hexes with the pointy sides up and down and the first row
 * either aligned left or right:
 * <pre>
 *   /\
 *  |  |
 *   \/
 * </pre>
 * And 'top' and 'bottom' result in hexes with the pointy sides to
 * the left and right and the first column either aligned top or bottom:
 * <pre>
 *   __
 *  /  \
 *  \__/
 *
 * </pre>

 * <p>Here is an example 2x2 map with top alignment:
 * <pre>
 *   ___
 *  /0,0\___
 *  \___/1,0\
 *  /0,1\___/
 *  \___/1,1\
 *      \___/
 * </pre>
 *
 * <p>The icon width and height refer to the total width and height
 * of a hex (i.e the size of the enclosing rectangle).
 *
 * @version $Id$
 */
public class HexMapView extends MapView
{
    public static final int ALIGN_TOP = 1;
    public static final int ALIGN_BOTTOM = 2;
    public static final int ALIGN_RIGHT = 3;
    public static final int ALIGN_LEFT = 4;

    private static final double HEX_SLOPE = Math.tan(Math.toRadians(60));

    private int mapAlignment;
    /* hexEdgesToTheLeft:
     * This means a layout like this:     __
     *                                   /  \
     *                                   \__/
     * as opposed to this:     /\
     *                        |  |
     *                         \/
     */
    private boolean hexEdgesToTheLeft;
    private boolean alignedToBottomOrRight;

    /**
     * Creates a new hexagonal map view that displays the specified map.
     *
     * @param map The map to be displayed by this map view.
     */
    public HexMapView(Map map) {
        super(map);

        //mapAlignment = map.getAlignment();
        mapAlignment = ALIGN_TOP;
        hexEdgesToTheLeft = false;
        if ( mapAlignment == ALIGN_TOP
            || mapAlignment == ALIGN_BOTTOM ) {
            hexEdgesToTheLeft = true;
        }
        alignedToBottomOrRight = false;
        if ( mapAlignment == ALIGN_BOTTOM
            || mapAlignment == ALIGN_RIGHT ) {
            alignedToBottomOrRight = true;
        }

        //TiledLogger.getLogger().info("HexMapView created");
    }

    /**
     * The scroll increment when clicking in the trough of a scrollbar,
     * that is scrolling a page. The amount is set to the size
     * of the completely visible hexes in the viewport.
     *
     * @param visibleRect Current viewport rectangle.
     * @param orientation SwingConstants.VERTICAL or HORIZONTAL.
     * @param direction > 0 = scrolling down; < 0 = scrolling up.
     *
     * @return Scroll amount in pixels.
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        Dimension tsize = getEffectiveTileSize();
        int border = showGrid ? 1 : 0;
        int tq = getThreeQuarterHex();
        int hWidth = (int)(tsize.width / 2 + 0.49) + border;
        int hHeight = (int)(tsize.height / 2 + 0.49) + border;

        //TiledLogger.getLogger().info(
        //    "ScrollBlock " + orientation + "/" + direction);
        //TiledLogger.getLogger().info(
        //    "visibleRect " + visibleRect.width + "," + visibleRect.height );
        //TiledLogger.getLogger().info(
        //    "tq " + tq + " border " + border + " height " + tsize.height);
        //TiledLogger.getLogger().info(
        //    "BlockInc w "
        //    + ((int)(visibleRect.width / (tq + border)) * (tq + border))
        //    + ", h "
        //    + ((int)(visibleRect.height / (tsize.height + border))
        //        * (tsize.height + border)));

        if (orientation == SwingConstants.VERTICAL ) {
            if ( hexEdgesToTheLeft ) {
                return (visibleRect.height - hHeight)
                    / (tsize.height + border)
                    * (tsize.height + border);
            } else {
                return (visibleRect.height -hHeight) / (tq + border)
                    * (tq + border);
            }
        } else {
            if ( hexEdgesToTheLeft ) {
                return (visibleRect.width - hWidth) / (tq + border)
                    * (tq + border);
            } else {
                return (visibleRect.width - hWidth)
                    / (tsize.width + border)
                    * (tsize.width + border);
            }
        }
    }

    /**
     * The scroll increment when clicking on the arrows of a scrollbar,
     * that is scrolling one hex horizontically or vertically.
     *
     * @param visibleRect Current viewport rectangle.
     * @param orientation SwingConstants.VERTICAL or HORIZONTAL.
     * @param direction > 0 = scrolling down; < 0 = scrolling up.
     *
     * @return Scroll amount in pixels.
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        //TiledLogger.getLogger().info(
        //    "ScrollUnit " + orientation + "/" + direction);
        Dimension tsize = getEffectiveTileSize();
        int border = showGrid ? 1 : 0;
        int tq = getThreeQuarterHex();
        if (orientation == SwingConstants.VERTICAL ) {
            if ( hexEdgesToTheLeft ) {
                return tsize.height + border;
            } else {
                return tq + border;
            }
        } else {
            if ( hexEdgesToTheLeft ) {
                return tq + border;
            } else {
                return tsize.width + border;
            }
        }
    }

    /**
     * The total size of the viewport so that all tiles of
     * the map fit in.
     *
     * @return Width and Height as Dimension.
     */
    public Dimension getPreferredSize() {
        Dimension tsize = getEffectiveTileSize();
        int w;
        int h;
        int border = showGrid ? 1 : 0;
        int tq = getThreeQuarterHex();
        int oq = getOneQuarterHex();

        if ( hexEdgesToTheLeft ) {
            //TiledLogger.getLogger().info(
            //    " twidth*3/4 " + tq
            //    + " twidth/4 " + oq
            //    + " theight/2 " + ((int)(tsize.height / 2 + 0.49)));

            w = map.getWidth() * (tq + border) + oq + border;
            h = map.getHeight() * (tsize.height + border)
                + (int)(tsize.height / 2 + 0.49) + border;
        } else {
            w = map.getWidth() * (tsize.width + border)
                + (int)(tsize.width / 2 + 0.49) + border;
            h = map.getHeight() * (tq + border) + oq + border;
        }

        //TiledLogger.getLogger().info("size " + w + "," + h);

        return new Dimension(w, h);
    }

    /**
     * Paint one layer to the viewport.
     *
     * @param g2d The graphics context, i.e. where to paint.
     * @param layer The layer to paint. Can be a special layer
     *        like the selection layer.
     */
    protected void paintLayer(Graphics2D g2d, TileLayer layer) {
        // Determine area to draw from clipping rectangle
        Dimension tsize = getEffectiveTileSize();
        // int toffset = showGrid ? 1 : 0;

        Rectangle clipRect = g2d.getClipBounds();

        //TiledLogger.getLogger().info("clip " + clipRect.x + "," + clipRect.y
        //    + "-" + clipRect.width + "," + clipRect.height);

        Point topLeft = screenToTileCoords(
                (int)clipRect.getMinX(), (int)clipRect.getMinY());
        Point bottomRight = screenToTileCoords(
                (int)clipRect.getMaxX(), (int)clipRect.getMaxY());
        int startX = (int)topLeft.getX();
        int startY = (int)topLeft.getY();
        int endX = (int)(bottomRight.getX());
        int endY = (int)(bottomRight.getY());
        if ( startX < 0 ) {
            startX = 0;
        }
        if ( startY < 0 ) {
            startY = 0;
        }
        if ( endX >= map.getWidth()) {
            endX = map.getWidth() - 1;
        }
        if ( endY >= map.getHeight()) {
            endY = map.getHeight() - 1;
        }

        //TiledLogger.getLogger().info("index " + startX + "," + startY
        //    + "-" + endX + "," + endY);

        Polygon gridPoly;
        double gx;
        double gy;
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Tile t = layer.getTileAt(x, y);

                if (t != null) {
                    if (layer.getClass() == SelectionLayer.class) {
                        //TiledLogger.getLogger().info(
                        //    "selection tile at " + x + "," + y);
                        gridPoly = createGridPolygon(x, y, 0);
                        g2d.fillPolygon(gridPoly);
                    } else {
                        Point screenCoords = getTopLeftCornerOfTile(x, y);
                        gx = screenCoords.getX();
                        gy = screenCoords.getY();
                        //TiledLogger.getLogger().info(
                        //    "image tile at " + x + "," + y
                        //    + " at " + gx + "," + gy);
                        t.draw(g2d, (int)gx, (int)(gy + tsize.height),
                            zoom);
                    }
                }
            }
        }
    }

    /**
     * Paint one layer to the viewport. Is this function used?
     * Not implemented!
     *
     * @param g2d The graphics context, i.e. where to paint.
     * @param og What is that?
     */
    protected void paintLayer(Graphics2D g2d, ObjectGroup og) {
        //TiledLogger.getLogger().info("called ?");
    }

    /**
     * @return The tile size in the view without border as Dimension.
     */
    private Dimension getEffectiveTileSize() {
        //TiledLogger.getLogger().info("size "
        //    + ((int)(map.getTileWidth() * zoom + 0.999)) + ","
        //    + ((int)(map.getTileHeight() * zoom + 0.999)));
        return new Dimension((int)(map.getTileWidth() * zoom + 0.999),
            (int)(map.getTileHeight() * zoom + 0.999));
    }

    /**
     * Together with getOneQuarterHex this gives the sizes of
     * one and three quarters in pixels in the interesting dimension.
     * If the layout is such that the hex edges point left and right
     * the interesting dimension is the width, otherwise it is the height.
     * The sum of one and three quarters equals always the total size
     * of the hex in this dimension.
     *
     * @return Three quarter of the tile size width or height (see above)
     * as integer.
     */
    private int getThreeQuarterHex() {
        int tq;
        if ( hexEdgesToTheLeft ) {
            tq = (int)(getEffectiveTileSize().width * 3.0 / 4.0 + 0.49);
        } else {
            tq = (int)(getEffectiveTileSize().height * 3.0 / 4.0 + 0.49);
        }

        return tq;
    }

    /**
     * Together with getThreeQuarterHex this gives the sizes of
     * one and three quarters in pixels in the interesting dimension.
     * If the layout is such that the hex edges point left and right
     * the interesting dimension is the width, otherwise it is the height.
     * The sum of one and three quarters equals always the total size
     * of the hex in this dimension.
     *
     * @return One quarter of the tile size width or height (see above)
     * as integer.
     */
    private int getOneQuarterHex() {
        int oq;
        if ( hexEdgesToTheLeft ) {
            oq = getEffectiveTileSize().width;
        } else {
            oq = getEffectiveTileSize().height;
        }

        return oq - getThreeQuarterHex();
    }

    /**
     * Paint the grid to the viewport.
     *
     * @param g2d The graphics context, i.e. where to paint.
     */
    protected void paintGrid(Graphics2D g2d) {
        g2d.setColor(Color.black);
        Dimension tileSize = getEffectiveTileSize();

        // Determine area to draw from clipping rectangle
        Rectangle clipRect = g2d.getClipBounds();
        Point topLeft = screenToTileCoords(
                (int)clipRect.getMinX(), (int)clipRect.getMinY());
        Point bottomRight = screenToTileCoords(
                (int)clipRect.getMaxX(), (int)clipRect.getMaxY());
        int startX = (int)topLeft.getX();
        int startY = (int)topLeft.getY();
        int endX = (int)(bottomRight.getX());
        int endY = (int)(bottomRight.getY());

        //TiledLogger.getLogger().info(
        //    "  scrn " + clipRect.getMinX() + "," + clipRect.getMinY()
        //    + "-" + clipRect.getMaxX() + "," + clipRect.getMaxY());
        //TiledLogger.getLogger().info(
        //    "  tile " + startX + "," + startY + "-" + endX + "," + endY);

        if ( startX < 0 ) {
            startX = 0;
        }
        if ( startY < 0 ) {
            startY = 0;
        }
        if ( endX >= map.getWidth()) {
            endX = map.getWidth() - 1;
        }
        if ( endY >= map.getHeight()) {
            endY = map.getHeight() - 1;
        }

        //TiledLogger.getLogger().info("  tile " + startX + "," + startY
        //    + "-" + endX + "," + endY);

        int dy = 0;
        int dx = 0;
        Polygon grid;

        if ( hexEdgesToTheLeft ) {
            for (int x = startX; x <= endX; x++) {
                grid = createGridPolygon(x, startY, 1);
                for (int y = startY; y <= endY; y++) {
                    g2d.drawPolygon(grid);
                    grid.translate(0, tileSize.height + 1);
                }
            }
        } else {
            for (int y = startY; y <= endY; y++) {
                grid = createGridPolygon(startX, y, 1);
                for (int x = startX; x <= endX; x++) {
                    g2d.drawPolygon(grid);
                    grid.translate(tileSize.width + 1, 0);
                }
            }
        }
    }

    /**
     * Paint coordinates.
     * This should draw tiny coordinates in every hex tile.
     * Not implemented.
     *
     * @param g2d The graphics context, i.e. where to paint.
     */
    protected void paintCoordinates(Graphics2D g2d) {
        // TODO: Implement paintCoordinates for HexMapView
        //TiledLogger.getLogger().info("NOT IMPLEMENTED");
    }

    /**
     * Compute the resulting tile coords, i.e. map coordinates,
     * from a point in the viewport. This function works for some
     * coords off the map, i.e. it works for the tile coord -1
     * and for coords larger than the map size.
     *
     * @param screenX The x coordinate of a point in the viewport.
     * @param screenY The y coordinate of a point in the viewport.
     *
     * @return The corresponding tile coords as Point.
     */
    public Point screenToTileCoords(int screenX, int screenY) {
        //TiledLogger.getLogger().info(
          //  "screen coords " + screenX + "," + screenY);

        int tx = 0;
        int ty = 0;
        int border = showGrid ? 1 : 0;
        Dimension tileSize = getEffectiveTileSize();
        int tileWidth = tileSize.width + border;
        int tileHeight = tileSize.height + border;
        int hWidth = (int)(tileWidth / 2 + 0.49) + border;
        int hHeight = (int)(tileHeight / 2 + 0.49) + border;
        Point [] fourPoints = new Point [4];
        Point [] fourTiles = new Point [4];

        final int x = screenX;
        final int y = screenY;

        // determine the two columns of hexes we are between
        // we are between col and col+1.
        // col == -1 means we are in the strip to the left
        //   of the centers of the hexes of column 0.
        int col = 0;
        if ( x < hWidth ) {
            col = -1;
        } else {
            if ( hexEdgesToTheLeft ) {
                col = (int)((x - hWidth)
                    / (double)(getThreeQuarterHex() + border) + 0.001);
            } else {
                col = (int)((x - hWidth) / (double)tileWidth + 0.001);
            }
        }

        // determine the two rows of hexes we are between
        int row = 0;
        if ( y < hHeight ) {
            row = -1;
        } else {
            if ( hexEdgesToTheLeft ) {
                row = (int)((y - hHeight) / (double)tileHeight + 0.001);
            } else {
                row = (int)((y - hHeight)
                    / (double)(getThreeQuarterHex() + border) + 0.001);
            }
        }

        //TiledLogger.getLogger().info("  columns " + col + "/" + (col + 1));
        //TiledLogger.getLogger().info("  rows " + row + "/" + (row + 1));

        // now take the four surrounding points and
        // find the one having the minimum distance to x,y
        fourTiles [0] = new Point(col, row);
        fourTiles [1] = new Point(col, row + 1);
        fourTiles [2] = new Point(col + 1, row);
        fourTiles [3] = new Point(col + 1, row + 1);

        fourPoints [0] = tileToScreenCoords(col, row);
        fourPoints [1] = tileToScreenCoords(col, row + 1);
        fourPoints [2] = tileToScreenCoords(col + 1, row);
        fourPoints [3] = tileToScreenCoords(col + 1, row + 1);

        // find point with min.distance
        double minDist = 2 * (map.getTileWidth() + map.getTileHeight());
        int minI = 5;
        //TiledLogger.getLogger().info("  init Min " + minDist);
        for ( int i = 0; i < fourPoints.length; i++ ) {
            if ( fourPoints [i].distance(x, y) < minDist ) {
                minDist = fourPoints [i].distance(x, y);
                minI = i;
            }

            //TiledLogger.getLogger().info("  min.pt " + fourPoints [minI].getX()
            //    + "," + fourPoints [minI].getY() + " index " + minI
            //    + " at dist " + minDist);
        }

        // get min point
        tx = (int)(fourTiles [minI].getX());
        ty = (int)(fourTiles [minI].getY());

        //TiledLogger.getLogger().info("  -> tile coords " + tx + "," + ty);

        return new Point(tx, ty);
    }

    /**
     * Repaint a region of the viewport.
     * This function has been disabled. I have tried it
     * but it seems to repaint with some offset.
     *
     * @param region The rectangle of the viewport to be repainted.
     */
    public void repaintRegion(Rectangle region) {
        super.repaintRegion(region);

        //TiledLogger.getLogger().info(
        //    "region " + region.getMinX() + "," + region.getMinY()
        //    + "-" + region.getMaxX() + "," + region.getMaxY()
        //    + " zoom " + zoom);

        // // This code should work. I've disabled it because of general problems with the view refresh.
        // // Point2D topLeft=getTopLeftCornerOfTile((int) region.getMinX(),(int) region.getMinY(),zoom);
        // // Point2D bottomRight=getTopLeftCornerOfTile((int) region.getMaxX(),(int) region.getMaxY(),zoom);
        // Point2D topLeft=getTopLeftCornerOfTile((int) region.getMinX(),(int) region.getMinY());
        // Point2D bottomRight=getTopLeftCornerOfTile((int) region.getMaxX(),(int) region.getMaxY());
        //
        // // Dimension tileSize=getTileSize(zoom);
        // Dimension tileSize=getTileSize();
        // int width=(int) (bottomRight.getX()-topLeft.getX()+tileSize.getWidth());
        // int height=(int) (bottomRight.getY()-topLeft.getY()+tileSize.getHeight());
        //
        // Rectangle dirty=new Rectangle((int) topLeft.getX(),(int) topLeft.getY(),width,height);
        //
        // repaint(dirty);
    }


    /**
     * Returns a hexagon at the given tile coordinates.
     *
     * @param tx The x coordinate of the tile.
     * @param ty The y coordinate of the tile.
     * @param border 0 = no border, 1 = with border line.
     *
     * @return A hexagon structure as Polygon.
     */
    protected Polygon createGridPolygon(int tx, int ty, int border) {
        Dimension tileSize = getEffectiveTileSize();
        Polygon poly = new Polygon();
        Point p = getTopLeftCornerOfTile(tx, ty);
        int topLeftX = (int)(p.getX());
        int topLeftY = (int)(p.getY());

        //TiledLogger.getLogger().info("hex at " + topLeftX + "," + topLeftY);

        int tq = getThreeQuarterHex();
        int oq = getOneQuarterHex();

        // Go round the sides clockwise
        if ( hexEdgesToTheLeft ) {
            int hh = (int)(tileSize.height / 2 + 0.49);

            poly.addPoint(topLeftX - 1, topLeftY + hh - 1);
            poly.addPoint(topLeftX + oq - 1, topLeftY - 1);
            poly.addPoint(topLeftX + tq, topLeftY - 1);
            poly.addPoint(topLeftX + tileSize.width, topLeftY + hh - 1);
            poly.addPoint(topLeftX + tq, topLeftY + tileSize.height);
            poly.addPoint(topLeftX + oq - 1, topLeftY + tileSize.height);
        } else {
            int hh = (int)(tileSize.width / 2 + 0.49);

            poly.addPoint(topLeftX + hh - 1, topLeftY - 1);
            poly.addPoint(topLeftX + tileSize.width, topLeftY + oq - 1);
            poly.addPoint(topLeftX + tileSize.width, topLeftY + tq);
            poly.addPoint(topLeftX + hh - 1, topLeftY + tileSize.height);
            poly.addPoint(topLeftX - 1, topLeftY + tq);
            poly.addPoint(topLeftX - 1, topLeftY + oq - 1);
        }

        return poly;
    }

    /**
     * Get the point at the top left corner of the bounding rectangle of this
     * hex.
     *
     * @param x The x coordinate of the tile.
     * @param y The y coordinate of the tile.
     *
     * @return The top left corner of the enclosing rectangle of the hex
     *         in screen coordinates as Point.
     */
    private Point getTopLeftCornerOfTile(int x, int y) {
        //TiledLogger.getLogger().info("tile coords " + x + "," + y);

        Dimension tileSize = getEffectiveTileSize();
        int w = tileSize.width;
        int h = tileSize.height;
        int xx;
        int yy;

        if ( hexEdgesToTheLeft ) {
            xx = x * getThreeQuarterHex();
            yy = y * h;
        } else {
            xx = x * w;
            yy = y * getThreeQuarterHex();
        }

        if ( showGrid ) {
            xx += x + 1;
            yy += y + 1;
        }

        if ((Math.abs(x % 2) == 1 && mapAlignment == ALIGN_TOP)
            || (x % 2 == 0 && mapAlignment == ALIGN_BOTTOM)) {
            yy += (int)(h / 2.0 + 0.49);
        }
        if ((Math.abs(y % 2) == 1 && mapAlignment == ALIGN_LEFT)
            || (y % 2 == 0 && mapAlignment == ALIGN_RIGHT)) {
            xx += (int)(w / 2.0 + 0.49);
        }

        //TiledLogger.getLogger().info(
        //    "  -> screen coords " + xx + "," + yy + " zoom " + zoom);

        return new Point(xx, yy);
    }

    /**
     * Returns the location (center) on screen for the given tile.
     * Works also for hypothetical tiles off the map.
     * The zoom is accounted for.
     *
     * @param x The x coordinate of the tile.
     * @param y The y coordinate of the tile.
     *
     * @return The point at the centre of the Hex as Point.
     */
    public Point tileToScreenCoords(int x, int y) {
        Point p = getTopLeftCornerOfTile(x, y);
        Dimension tileSize = getEffectiveTileSize();
        return new Point(
            (int)(p.getX()) + (int)(tileSize.width / 2 + 0.49),
            (int)(p.getY()) + (int)(tileSize.height / 2 + 0.49));
    }

    public Point screenToPixelCoords(int x, int y) {
        return new Point(
                (int) (x / zoom), (int) (y / zoom));
    }

    protected void paintPropertyFlags(Graphics2D g2d, TileLayer layer) {
        // TODO: Implement property flags painting for HexMapView
    }

    protected void paintObjectGroup(Graphics2D g, ObjectGroup og) {
        // NOTE: Direct copy from OrthoMapView (candidate for generalization)
        Iterator itr = og.getObjects();

        while (itr.hasNext()) {
            MapObject mo = (MapObject) itr.next();
            double ox = mo.getX() * zoom;
            double oy = mo.getY() * zoom;

            if (mo.getWidth() == 0 || mo.getHeight() == 0) {
                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Color.black);
                g.fillOval((int) ox + 1, (int) oy + 1,
                        (int) (10 * zoom), (int) (10 * zoom));
                g.setColor(Color.orange);
                g.fillOval((int) ox, (int) oy,
                        (int) (10 * zoom), (int) (10 * zoom));
                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_OFF);
            } else {
                g.setColor(Color.black);
                g.drawRect((int) ox + 1, (int) oy + 1,
                    (int) (mo.getWidth() * zoom),
                    (int) (mo.getHeight() * zoom));
                g.setColor(Color.orange);
                g.drawRect((int) ox, (int) oy,
                    (int) (mo.getWidth() * zoom),
                    (int) (mo.getHeight() * zoom));
            }
            if (zoom > 0.0625) {
                final String s = mo.getName() != null ? mo.getName() : "(null)";
                g.setColor(Color.black);
                g.drawString(s, (int) (ox - 5) + 1, (int) (oy - 5) + 1);
                g.setColor(Color.white);
                g.drawString(s, (int) (ox - 5), (int) (oy - 5));
            }
        }
    }
}
