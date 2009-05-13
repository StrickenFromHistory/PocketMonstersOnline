package org.pokenet.client.ui.frames;

import java.util.ArrayList;
import java.util.List;

import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.pokenet.client.ui.base.ListBox;

public class SpriteChooserDialog extends Frame {
	protected ListBox m_spriteList;
	protected Label m_spriteDisplay;
    protected String mustLoadSprite;
    private List<String> m_sprites;
    
    public SpriteChooserDialog() {
    	m_sprites = new ArrayList<String>();
    	for (int i = 0; i <= 218; i++){
    		m_sprites.add(String.valueOf(i));
    	}
    	
    	m_spriteDisplay = new Label();
    	m_spriteDisplay.setSize(124, 204);
    	m_spriteDisplay.setLocation(105, 20);
    	getContentPane().add(m_spriteDisplay);

    	m_spriteList = new ListBox(m_sprites, false) {
    		@Override
    		protected void itemClicked(String itemName, int idx) {
    			super.itemClicked(itemName, idx);
    			mustLoadSprite = "res/characters/" + itemName + ".png";
    		}
    	};
    	m_spriteList.setSize(105, 317);
    	getContentPane().add(m_spriteList);

    	setTitle("Please choose your character..");
    	getCloseButton().setVisible(false);
    	setSize(265, 340);
    	setResizable(false);
    	setDraggable(false);
    	setVisible(true);
    	initUse();
    }

    public void initUse() {
           
    }

    public int getChoice() {
    	return m_spriteList.getSelectedIndex();
    }

    @Override
    public void render(GUIContext container, Graphics g) {
    	super.render(container, g);
    	if (mustLoadSprite != null) {
    		try {
    			m_spriteDisplay.setImage(new Image(mustLoadSprite));}
    		catch (SlickException e) {
    			e.printStackTrace();
    		}
    		mustLoadSprite = null;
    	}
    }
}