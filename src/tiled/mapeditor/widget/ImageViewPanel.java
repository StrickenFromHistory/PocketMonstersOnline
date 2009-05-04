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

import javax.swing.JPanel;

public class ImageViewPanel extends JPanel
{
    private final Image image;

    public ImageViewPanel(Image image) {
        this.image = image;
    }

    public Dimension getPreferredSize() {
        return new Dimension(150, 150);
    }

    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}
