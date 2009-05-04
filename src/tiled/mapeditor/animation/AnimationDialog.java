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

package tiled.mapeditor.animation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.*;

import tiled.core.Sprite;
import tiled.core.MapObject;
import tiled.mapeditor.Resources;

/**
 * This is the multi-purpose animation dialog, which can handle any
 * {@link Sprite} based animations. (Good for animated tiles, and
 * {@link MapObject}s)
 *
 * @see Sprite
 */
public class AnimationDialog extends JDialog implements ActionListener
{
    private static final int PLAYING = 1;
    private static final int STOPPED = 0;

    private Sprite currentSprite;
    private SpriteCanvas canvas;
    private int state = STOPPED;
    private JButton playstop;
    private JComboBox keyframe;
    private JLabel lCurrentFrame;
    private JLabel lFrameRate;
    private JLabel lFrameRange;

    private Icon playIcon, stopIcon;

    public AnimationDialog(Dialog parent, Sprite sprite) {
        super(parent, "Animation", true);
        currentSprite = sprite;

        playIcon = Resources.getIcon("play.png");
        stopIcon = Resources.getIcon("stop.png");

        makeUp();
        pack();
        setLocationRelativeTo(getOwner());
    }

    public JPanel makeUp() {
        JButton b;
        JLabel l;
        JPanel main = new JPanel(new BorderLayout());
        JPanel data = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new GridBagLayout());
        JPanel opt = new JPanel(new GridBagLayout());
        canvas = new SpriteCanvas(this);


        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        b = new JButton("", Resources.getIcon("startframe.png"));
        b.setActionCommand("startframe");
        b.setMargin(new Insets(0, 0, 0, 0));
        b.addActionListener(this);
        buttons.add(b, c);

        c.gridx = 1;
        b = new JButton("", Resources.getIcon("back.png"));
        b.setActionCommand("back");
        b.setMargin(new Insets(0, 0, 0, 0));
        b.addActionListener(this);
        buttons.add(b, c);

        c.gridx = 2;
        playstop = new JButton("", Resources.getIcon("play.png"));
        playstop.setActionCommand("playstop");
        playstop.setMargin(new Insets(0, 0, 0, 0));
        playstop.addActionListener(this);
        buttons.add(playstop, c);

        c.gridx = 3;
        b = new JButton("", Resources.getIcon("forward.png"));
        b.setActionCommand("forward");
        b.setMargin(new Insets(0, 0, 0, 0));
        b.addActionListener(this);
        buttons.add(b, c);

        c.gridx = 4;
        b = new JButton("", Resources.getIcon("lastframe.png"));
        b.setActionCommand("lastframe");
        b.setMargin(new Insets(0, 0, 0, 0));
        b.addActionListener(this);
        buttons.add(b, c);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        String[] defaultArr = {"None"};
        keyframe = new JComboBox(defaultArr);
        keyframe.addActionListener(this);
        opt.add(keyframe, c);

        c.gridwidth = 1;
        c.gridy = 1;
        l = new JLabel("Range:");
        opt.add(l, c);

        c.gridx = 1;
        lFrameRange = new JLabel("-");
        opt.add(lFrameRange, c);

        c.gridy = 2;
        c.gridx = 0;
        l = new JLabel("Current:");
        opt.add(l, c);

        c.gridx = 1;
        lCurrentFrame = new JLabel("?");
        opt.add(lCurrentFrame, c);

        c.gridy = 3;
        c.gridx = 0;
        l = new JLabel("Rate:");
        opt.add(l, c);

        c.gridx = 1;
        lFrameRate = new JLabel("?");
        opt.add(lFrameRate, c);

        data.add(canvas, BorderLayout.CENTER);
        data.add(buttons, BorderLayout.SOUTH);

        main.add(data, BorderLayout.CENTER);
        main.add(opt, BorderLayout.EAST);

        return main;
    }

    public void paintSprite(Graphics g) {
        currentSprite.getCurrentFrame().draw(g, 0, 0, 1.0f);

        if (state == PLAYING) {
            if (currentSprite != null) {
                if (currentSprite.getCurrentKey() == null) {
                    JOptionPane.showMessageDialog(this,
                                                  "There are no keys defined!",
                                                  "No Keys",
                                                  JOptionPane.ERROR_MESSAGE);
                    state = STOPPED;
                    return;
                }
                currentSprite.iterateFrame();
                updateStats();
                canvas.repaint();
                repaint();
            }
        }
    }

    public void draw(Graphics g) {
        canvas.repaint();
        updateList();
    }

    public void updateList() {
        if (currentSprite != null) {
            keyframe.removeAllItems();
            try {
                Iterator itr = currentSprite.getKeys();
                while (itr.hasNext()) {
                    keyframe.addItem(((Sprite.KeyFrame) itr.next()).getName());
                }
            }
            catch (Exception e) {
            }
        }
    }

    public void updateStats() {
        if (currentSprite != null) {
            Sprite.KeyFrame k =
                    currentSprite.getKey((String) keyframe.getSelectedItem());
            if (k != null) {
                lCurrentFrame.setText(String.valueOf(currentSprite.getCurrentFrame()));
                lFrameRate.setText(String.valueOf(k.getFrameRate()));
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("New Key...") ||
                e.getActionCommand().equalsIgnoreCase("Modify Key...")) {
            KeyDialog kd = new KeyDialog(this, currentSprite);
            kd.doKeys();
        }
        else if (e.getActionCommand().equalsIgnoreCase("Delete Key")) {
            if (currentSprite != null) {
                currentSprite.removeKey((String) keyframe.getSelectedItem());
            }
        }
        else if (e.getActionCommand().equalsIgnoreCase("comboBoxChanged")) {
            if (currentSprite != null) {
                currentSprite
                        .setKeyFrameTo((String) keyframe.getSelectedItem());
                updateStats();
                canvas.repaint();
            }
        }
        else {
            handleFrames(e);
        }
        updateList();
        updateStats();
    }

    public void handleFrames(ActionEvent e) {
        if (currentSprite == null) {
            return;
        }

        if (e.getActionCommand().equalsIgnoreCase("startframe")) {
            if (currentSprite.getCurrentKey() == null) {
                JOptionPane.showMessageDialog(this,
                                              "There are no keys defined!",
                                              "No Keys",
                                              JOptionPane.ERROR_MESSAGE);
            }
            else {
                currentSprite.keySetFrame(0);
                canvas.repaint();
            }
        }
        else if (e.getActionCommand().equalsIgnoreCase("back")) {
            if (currentSprite.getCurrentKey() == null) {
                JOptionPane.showMessageDialog(this,
                                              "There are no keys defined!",
                                              "No Keys",
                                              JOptionPane.ERROR_MESSAGE);
            }
            else {
                currentSprite.keyStepBack(1);
                canvas.repaint();
            }
        }
        else if (e.getActionCommand().equalsIgnoreCase("playstop")) {
            if (currentSprite.getCurrentKey() == null) {
                JOptionPane.showMessageDialog(this,
                                              "There are no keys defined!",
                                              "No Keys",
                                              JOptionPane.ERROR_MESSAGE);
            }
            else {
                if (state == PLAYING) {
                    state = STOPPED;
                    currentSprite.stop();
                    playstop.setIcon(playIcon);
                }
                else {
                    state = PLAYING;
                    currentSprite.play();
                    playstop.setIcon(stopIcon);
                }
                canvas.repaint();
            }
        }
        else if (e.getActionCommand().equalsIgnoreCase("forward")) {
            if (currentSprite.getCurrentKey() == null) {
                JOptionPane.showMessageDialog(this,
                                              "There are no keys defined!",
                                              "No Keys",
                                              JOptionPane.ERROR_MESSAGE);
            }
            else {
                currentSprite.keyStepForward(1);
                canvas.repaint();
            }
        }
        else if (e.getActionCommand().equalsIgnoreCase("lastframe")) {
            if (currentSprite.getCurrentKey() == null) {
                JOptionPane.showMessageDialog(this,
                                              "There are no keys defined!",
                                              "No Keys",
                                              JOptionPane.ERROR_MESSAGE);
            }
            else {
                currentSprite.keyStepForward(10000);
                canvas.repaint();
            }
        }
    }
}

class SpriteCanvas extends JPanel
{
    private AnimationDialog owner;
    private Image buffer;
    private Dimension osd;

    SpriteCanvas(AnimationDialog o) {
        owner = o;
    }

    /**
     * Draws checkerboard background.
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

    public void paint(Graphics g) {
        if (owner != null) {
            Dimension d = getSize();
            if (buffer == null) {
                osd = d;
                buffer = createImage(d.width, d.height);
            }

            if (d.width != osd.width || d.height != osd.height) {
                osd = d;
                buffer = createImage(d.width, d.height);
            }
            paintBackground(g);
            if (buffer != null) {
                Graphics osg = buffer.getGraphics();
                owner.paintSprite(osg);
                g.drawImage(buffer, 0, 0, null);
            }
            else {
                owner.paintSprite(g);
            }
        }
    }
}
