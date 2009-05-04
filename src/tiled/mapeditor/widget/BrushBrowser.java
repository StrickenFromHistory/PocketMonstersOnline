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
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import tiled.mapeditor.brush.Brush;
import tiled.mapeditor.brush.ShapeBrush;

/**
 * A panel that allows selecting a brush from a set of presets.
 */
public class BrushBrowser extends JPanel
{
    private int maxWidth = 25;
    private Brush selectedBrush;
    private final LinkedList<Brush> brushes;

    public BrushBrowser() {
        brushes = new LinkedList<Brush>();
        initPresets();

        MouseInputAdapter listener = new MouseInputAdapter() {
            public void mousePressed(MouseEvent e) {
                int perLine = getWidth() / maxWidth;
                int x = e.getX() / maxWidth;
                int y = e.getY() / maxWidth;
                int selectedIndex =
                    y * perLine + (x > perLine - 1 ? perLine - 1 : x);

                if (selectedIndex >= 0 && selectedIndex < brushes.size()) {
                    Brush previousBrush = selectedBrush;
                    selectedBrush = brushes.get(selectedIndex);
                    firePropertyChange("selectedbrush", previousBrush, selectedBrush);
                    repaint();
                }
            }

            public void mouseDragged(MouseEvent e) {
                mousePressed(e);
            }
        };

        addMouseListener(listener);
        addMouseMotionListener(listener);
    }

    public Dimension getPreferredSize() {
        int perLine = getWidth() / maxWidth;
        if (perLine > 0) {
            int lines = (brushes.size() + (perLine - 1)) / perLine;
            return new Dimension(maxWidth, maxWidth * lines);
        } else {
            return new Dimension(maxWidth, 150);
        }
    }

    private void initPresets() {
        int[] dimensions = { 1, 2, 4, 8, 12, 20 };

        for (int n = 1; n < dimensions.length; n++) {
            ShapeBrush brush = new ShapeBrush();
            brush.makeCircleBrush(dimensions[n] / 2);
            brushes.add(brush);
        }

        for (int dimension : dimensions) {
            ShapeBrush brush = new ShapeBrush();
            brush.makeQuadBrush(new Rectangle(0, 0, dimension, dimension));
            brushes.add(brush);
        }
    }

    public void paint(Graphics g) {
        Rectangle clipRect = g.getClipBounds();
        g.setColor(Color.white);
        g.fillRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                          RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.black);

        // Draw the brushes
        Iterator<Brush> itr = brushes.iterator();
        int x = 0;
        while (itr.hasNext()) {
            Brush brush = itr.next();
            Rectangle bb = brush.getBounds();
            float o = maxWidth / 2.0f - bb.width / 2.0f;
            g.translate((int) o, (int) o);
            brush.drawPreview((Graphics2D) g, new Dimension(maxWidth, maxWidth), null);
            g.translate((int) -o, (int) -o);

            if (brush == selectedBrush) {
                g.drawRect(0, 0, maxWidth, maxWidth);
            }

            g.translate(maxWidth,0);
            x += maxWidth;
            if (x + maxWidth > getWidth()) {
                g.translate(-x, maxWidth);
                x = 0;
            }
        }
    }

    public void setSelectedBrush(Brush selectedBrush) {
        for (Brush brush : brushes) {
            if (brush.equals(selectedBrush)) {
                this.selectedBrush = brush;
                break;
            }
        }
    }

    public Brush getSelectedBrush() {
        return selectedBrush;
    }
}
