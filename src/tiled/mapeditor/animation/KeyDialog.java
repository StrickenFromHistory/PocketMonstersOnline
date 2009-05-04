/*
 *  Tiled Map Editor, (c) 2005-2006
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
import java.awt.event.*;

import javax.swing.*;

import tiled.core.*;

public class KeyDialog extends JDialog implements ActionListener, MouseListener
{
    private Sprite sprite;
    private JDialog owner;
    private JList keysList;
    private JTextField tName, tStart, tFinish, tRate;
    private JRadioButton rbLoop, rbStop, rbReverse, rbAuto;

    public KeyDialog(JDialog o, Sprite s) {
        owner = o;
        sprite = s;
    }

    private void init() {
        JButton b;
        ButtonGroup bg = new ButtonGroup();
        setSize(380, 180);
        GridBagConstraints c = new GridBagConstraints();
        JScrollPane sp = new JScrollPane();
        c.fill=GridBagConstraints.BOTH;
        getContentPane().setLayout(new GridBagLayout());
        sp.setSize(50, 200);
        keysList = new JList();
        queryKeys();
        keysList.addMouseListener(this);
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 6;
        c.gridwidth = 2;
        sp.getViewport().setView(keysList);
        getContentPane().add(sp,c);
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = .5;
        c.weighty = .5;
        c.gridx = 2;
        tName = new JTextField(32);
        tName.setToolTipText("The key name ("+Sprite.KeyFrame.KEY_NAME_LENGTH_MAX+") max");
        getContentPane().add(tName,c);
        c.gridy = 1;
        tStart = new JTextField(4);
        tStart.setToolTipText("The first frame");
        getContentPane().add(tStart,c);
        c.gridy = 2;
        tFinish = new JTextField(4);
        tFinish.setToolTipText("The last frame");
        getContentPane().add(tFinish,c);
        c.gridy = 3;
        tRate = new JTextField(4);
        tRate.setToolTipText("The frame rate");
        getContentPane().add(tRate,c);
        c.gridy = 6;
        c.gridx = 0;
        b = new JButton("New Key");
        b.addActionListener(this);
        getContentPane().add(b, c);
        c.gridx=1;
        b = new JButton("Delete Key");
        b.addActionListener(this);
        getContentPane().add(b,c);
        c.gridx=2;
        b = new JButton("Apply");
        b.addActionListener(this);
        getContentPane().add(b,c);
        c.gridx=3;
        b = new JButton("OK");
        b.addActionListener(this);
        getContentPane().add(b,c);

        c.gridheight=1;
        c.gridwidth=1;
        c.gridx=3;
        c.gridy=0;

        rbLoop = new JRadioButton("Looping");
        rbLoop.setActionCommand("loop");
        rbLoop.addActionListener(this);
        getContentPane().add(rbLoop,c);
        bg.add(rbLoop);
        rbStop = new JRadioButton("Stop");
        rbStop.setActionCommand("stop");
        rbStop.addActionListener(this);
        c.gridy = 1;
        getContentPane().add(rbStop,c);
        bg.add(rbStop);
        rbReverse = new JRadioButton("Reverse");
        rbReverse.setActionCommand("reverse");
        rbReverse.addActionListener(this);
        c.gridy = 2;
        getContentPane().add(rbReverse,c);
        bg.add(rbReverse);
        rbAuto = new JRadioButton("Auto Jump");
        rbAuto.setActionCommand("auto");
        rbAuto.addActionListener(this);
        c.gridy = 3;
        getContentPane().add(rbAuto,c);
        bg.add(rbAuto);

    }

    private void updateFields() {
        Sprite.KeyFrame key = sprite.getKey((String)keysList.getSelectedValue());
        if (key != null){
            tName.setText(key.getName());
            //tStart.setText(""+key.getStartFrame());
            //tFinish.setText(""+key.getFinishFrame());
            tRate.setText(String.valueOf(key.getFrameRate()));
            rbLoop.setSelected(false);
            rbStop.setSelected(false);
            rbReverse.setSelected(false);
            rbAuto.setSelected(false);
            switch (key.getFlags()&Sprite.KeyFrame.MASK_ANIMATION) {
                case Sprite.KeyFrame.KEY_LOOP:
                    rbLoop.setSelected(true);
                    break;
                case Sprite.KeyFrame.KEY_STOP:
                    rbStop.setSelected(true);
                    break;
                case Sprite.KeyFrame.KEY_REVERSE:
                    rbReverse.setSelected(true);
                    break;
                case Sprite.KeyFrame.KEY_AUTO:
                    rbAuto.setSelected(true);
                    break;
            }
        }
    }

    private void queryKeys() {
        try{
            //keysList.setListData(sprite.getKeys());
            repaint();
        }catch(Exception e){
            System.out.println("Message: " + e.getMessage());
        }
    }

    public void doKeys() {
        init();
        setVisible(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("New Key")) {
            //sprite.createKey("(new)",0,0,Sprite.KeyFrame.KEY_LOOP);
            queryKeys();
            owner.repaint();
        } else if (e.getActionCommand().equalsIgnoreCase("Delete Key")) {
            owner.repaint();
        } else if (e.getActionCommand().equalsIgnoreCase("OK")) {
            dispose();
        } else if (e.getActionCommand().equalsIgnoreCase("Apply")) {
            Sprite.KeyFrame k = sprite.getKey((String)keysList.getSelectedValue());
            if (k != null) {
                k.setName(tName.getText());
                //k.setStartFinish(Integer.parseInt(tStart.getText()),Integer.parseInt(tFinish.getText()));
                k.setFrameRate(Float.parseFloat(tRate.getText()));
                if (rbLoop.isSelected()){
                    k.setFlags(Sprite.KeyFrame.KEY_LOOP);
                } else if (rbStop.isSelected()){
                    k.setFlags(Sprite.KeyFrame.KEY_STOP);
                } else if (rbReverse.isSelected()){
                    k.setFlags(Sprite.KeyFrame.KEY_REVERSE);
                } else if (rbAuto.isSelected()){
                    k.setFlags(Sprite.KeyFrame.KEY_AUTO);
                }
            }else{
                JOptionPane.showMessageDialog(this,"Selected key not found!","No Key",JOptionPane.ERROR_MESSAGE);
            }
            owner.repaint();
            queryKeys();
        }

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        updateFields();
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }
}
