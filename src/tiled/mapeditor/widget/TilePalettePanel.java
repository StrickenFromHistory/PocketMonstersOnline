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

package tiled.mapeditor.widget;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.event.MouseInputAdapter;

import tiled.core.*;
import tiled.mapeditor.util.TileRegionSelectionEvent;
import tiled.mapeditor.util.TileSelectionEvent;
import tiled.mapeditor.util.TileSelectionListener;

/**
 * Displays a tileset and allows selecting a specific tile as well as
 * selecting several tiles for the creation of a stamp brush.
 */
public class TilePalettePanel extends JPanel implements Scrollable,
       TilesetChangeListener
{
    private static final int TILES_PER_ROW = 4;
    private TileSet tileset;
    private List<TileSelectionListener> tileSelectionListeners;
    private Vector<Tile> tilesetMap;
    private Rectangle selection;

    /**
     * Constructs an empty tile palette panel.
     */
    public TilePalettePanel() {
        tileSelectionListeners = new LinkedList();

        MouseInputAdapter mouseInputAdapter = new MouseInputAdapter() {
            private Point origin;

            public void mousePressed(MouseEvent e) {
                origin = getTileCoordinates(e.getX(), e.getY());
                setSelection(new Rectangle(origin.x, origin.y, 0, 0));
                scrollTileToVisible(origin);
                Tile clickedTile = getTileAt(origin.x, origin.y);
                if (clickedTile != null) {
                    fireTileSelectionEvent(clickedTile);
                }
            }

            public void mouseDragged(MouseEvent e) {
                Point point = getTileCoordinates(e.getX(), e.getY());
                Rectangle select = new Rectangle(origin.x, origin.y, 0, 0);
                select.add(point);
                if (!select.equals(selection)) {
                    setSelection(select);
                    scrollTileToVisible(point);
                }
                if (selection.getWidth() > 0 || selection.getHeight() > 0)
                    fireTileRegionSelectionEvent(selection);
            }
        };
        addMouseListener(mouseInputAdapter);
        addMouseMotionListener(mouseInputAdapter);
    }

    /**
     * Adds tile selection listener. The listener will be notified when the
     * user selects a tile.
     *
     * @param listener the listener to add
     */
    public void addTileSelectionListener(TileSelectionListener listener) {
        tileSelectionListeners.add(listener);
    }

    /**
     * Removes tile selection listener.
     *
     * @param listener the listener to remove
     */
    public void removeTileSelectionListener(TileSelectionListener listener) {
        tileSelectionListeners.remove(listener);
    }

    private void fireTileSelectionEvent(Tile selectedTile) {
        TileSelectionEvent event = new TileSelectionEvent(this, selectedTile);
        for (TileSelectionListener listener : tileSelectionListeners) {
            listener.tileSelected(event);
        }
    }

    private void fireTileRegionSelectionEvent(Rectangle selection) {
        TileLayer region = createTileLayerFromRegion(selection);
        TileRegionSelectionEvent event = new TileRegionSelectionEvent(this, region);
        for (TileSelectionListener listener : tileSelectionListeners) {
            listener.tileRegionSelected(event);
        }
    }

    /**
     * Creates a tile layer from a certain region of the tile palette.
     *
     * @param rect the rectangular region from which a tile layer is created
     * @return the created tile layer
     */
    private TileLayer createTileLayerFromRegion(Rectangle rect) {
        TileLayer layer = new TileLayer(rect.width + 1, rect.height + 1);

        // Copy the tiles in the region to the tile layer
        for (int y = rect.y; y <= rect.y + rect.height; y++) {
            for (int x = rect.x; x <= rect.x + rect.width; x++) {
                layer.setTileAt(x - rect.x, y - rect.y, getTileAt(x, y));
            }
        }

        return layer;
    }

    /**
     * Change the tileset displayed by this palette panel.
     *
     * @param tileset the tileset to be displayed by this palette panel
     */
    public void setTileset(TileSet tileset) {
        // Remove any existing listener
        if (this.tileset != null) {
            this.tileset.removeTilesetChangeListener(this);
        }

        this.tileset = tileset;

        // Listen to changes in the new tileset
        if (this.tileset != null) {
            this.tileset.addTilesetChangeListener(this);
        }

        if (tileset != null) tilesetMap = tileset.generateGaplessVector();
        revalidate();
        repaint();
    }

    public TileSet getTileset() {
        return tileset;
    }

    public void tilesetChanged(TilesetChangedEvent event) {
        tilesetMap = tileset.generateGaplessVector();
        revalidate();
        repaint();
    }

    public void nameChanged(TilesetChangedEvent event, String oldName, String newName) {
    }

    public void sourceChanged(TilesetChangedEvent event, String oldSource, String newSource) {
    }

    /**
     * Converts pixel coordinates to tile coordinates. The returned coordinates
     * are at least 0 and adjusted with respect to the number of tiles per row
     * and the number of rows.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return tile coordinates
     */
    private Point getTileCoordinates(int x, int y) {
        int twidth = tileset.getTileWidth() + 1;
        int theight = tileset.getTileHeight() + 1;
        int tileCount = tilesetMap.size();
        int tilesPerRow = getTilesPerRow();
        int rows = tileCount / tilesPerRow +
                (tileCount % tilesPerRow > 0 ? 1 : 0);

        int tileX = Math.max(0, Math.min(x / twidth, tilesPerRow - 1));
        int tileY = Math.max(0, Math.min(y / theight, rows - 1));

        return new Point(tileX, tileY);
    }

    /**
     * Retrieves the tile at the given tile coordinates. It assumes the tile
     * coordinates are adjusted to the number of tiles per row.
     *
     * @param x x tile coordinate
     * @param y y tile coordinate
     * @return the tile at the given tile coordinates, or <code>null</code>
     *         if the index is out of range
     */
    private Tile getTileAt(int x, int y) {
        int tilesPerRow = getTilesPerRow();
        int tileAt = y * tilesPerRow + x;

        if (tileAt >= tilesetMap.size()) {
            return null;
        } else {
            return tilesetMap.get(tileAt);
        }
    }

    /**
     * Returns the number of tiles to display per row. This gets calculated
     * dynamically unless the tileset specifies this value.
     *
     * @return the number of tiles to display per row, is at least 1
     */
    private int getTilesPerRow() {
        // todo: It should be an option to follow the tiles per row given
        // todo: by the tileset.
        if (tileset.getTilesPerRow() == 0) {
            int twidth = tileset.getTileWidth() + 1;
            return Math.max(1, (getWidth() - 1) / twidth);
        } else {
            return tileset.getTilesPerRow();
        }
    }

    private void setSelection(Rectangle rect) {
        repaintSelection();
        selection = rect;
        repaintSelection();
    }

    private void repaintSelection() {
        if (selection != null) {
            int twidth = tileset.getTileWidth() + 1;
            int theight = tileset.getTileHeight() + 1;

            repaint(selection.x * twidth, selection.y * theight,
                    (selection.width + 1) * twidth + 1,
                    (selection.height + 1) * theight + 1);
        }
    }

    private void scrollTileToVisible(Point tile) {
        int twidth = tileset.getTileWidth() + 1;
        int theight = tileset.getTileHeight() + 1;

        scrollRectToVisible(new Rectangle(
                tile.x * twidth,
                tile.y * theight,
                twidth + 1, theight + 1));
    }

    public void paint(Graphics g) {
        Rectangle clip = g.getClipBounds();

        paintBackground(g);

        if (tileset != null) {
            // Draw the tiles
            int twidth = tileset.getTileWidth() + 1;
            int theight = tileset.getTileHeight() + 1;
            int tilesPerRow = getTilesPerRow();

            int startY = clip.y / theight;
            int endY = (clip.y + clip.height) / theight + 1;
            int tileAt = tilesPerRow * startY;
            int gx;
            int gy = startY * theight;

            for (int y = startY; y < endY; y++) {
                gx = 1;

                for (int x = 0;
                     x < tilesPerRow && tileAt < tilesetMap.size();
                     x++, tileAt++)
                {
                    Tile tile = tilesetMap.get(tileAt);

                    if (tile != null) {
                        tile.drawRaw(g, gx, gy + theight, 1.0);
                    }
                    gx += twidth;
                }
                gy += theight;
            }

            // Draw the selection
            if (selection != null) {
                g.setColor(new Color(100, 100, 255));
                g.draw3DRect(
                        selection.x * twidth, selection.y * theight,
                        (selection.width + 1) * twidth,
                        (selection.height + 1) * theight,
                        false);
                ((Graphics2D) g).setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_ATOP, 0.2f));
                g.fillRect(
                        selection.x * twidth + 1, selection.y * theight + 1,
                        (selection.width + 1) * twidth - 1,
                        (selection.height + 1) * theight - 1);
            }
        }
    }

    /**
     * Draws checkerboard background.
     *
     * @param g the {@link Graphics} instance to draw on
     */
    private static void paintBackground(Graphics g) {
        Rectangle clip = g.getClipBounds();
        int side = 10;

        int startX = clip.x / side;
        int startY = clip.y / side;
        int endX = (clip.x + clip.width) / side + 1;
        int endY = (clip.y + clip.height) / side + 1;

        // Fill with white background
        g.setColor(Color.WHITE);
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        // Draw darker squares
        g.setColor(Color.LIGHT_GRAY);
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                if ((y + x) % 2 == 1) {
                    g.fillRect(x * side, y * side, side, side);
                }
            }
        }
    }

    public Dimension getPreferredSize() {
        if (tileset == null) {
            return new Dimension(0, 0);
        }
        else {
            int twidth = tileset.getTileWidth() + 1;
            int theight = tileset.getTileHeight() + 1;
            int tileCount = tilesetMap.size();
            int tilesPerRow = getTilesPerRow();
            int rows = tileCount / tilesPerRow +
                    (tileCount % tilesPerRow > 0 ? 1 : 0);

            return new Dimension(tilesPerRow * twidth + 1, rows * theight + 1);
        }
    }


    // Scrollable interface

    public Dimension getPreferredScrollableViewportSize() {
        if (tileset != null) {
            int twidth = tileset.getTileWidth() + 1;
            return new Dimension(TILES_PER_ROW * twidth + 1, 200);
        } else {
            return new Dimension(0, 0);
        }
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        if (tileset != null) {
            return tileset.getTileWidth();
        } else {
            return 0;
        }
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        if (tileset != null) {
            return tileset.getTileWidth();
        } else {
            return 0;
        }
    }

    public boolean getScrollableTracksViewportWidth() {
        // todo: Update when this has become an option
        return tileset == null || tileset.getTilesPerRow() == 0;
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}
