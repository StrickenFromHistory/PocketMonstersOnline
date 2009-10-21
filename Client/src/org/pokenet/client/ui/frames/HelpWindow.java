package org.pokenet.client.ui.frames;

import java.util.List;

import mdes.slick.sui.Frame;
import mdes.slick.sui.TextArea;

import org.newdawn.slick.Color;
import org.pokenet.client.backend.Translator;

/**
 * Instructions for new players
 * @author ZombieBear
 *
 */
public class HelpWindow extends Frame{
    
	private TextArea helptext;
   
    /**
     * Default constructor
     */
	public HelpWindow(){
            initGUI();
    }
	
	/**
	 * Initializes the interface
	 */
    private void initGUI() {
    	getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
    	List<String> translated = Translator.translate("_GUI");
    	this.getTitleBar().getCloseButton().setVisible(false);
    	this.setTitle(translated.get(20));
    	this.setBackground(new Color(0, 0, 0, 85));
    	this.setForeground(new Color(255, 255, 255));
           
    	this.setLocation(200, 0);
    	this.setResizable(false);
           
    	helptext = new TextArea();
    	helptext.setSize(355, 455);
    	//setText Mover stuff to help panel.
    	helptext.setText(translated.get(21) +
    			translated.get(22) +
    			translated.get(23) +
    			translated.get(24) +
    			translated.get(25) +
    			translated.get(26));
//    	helptext.setFont(GlobalGame.getDPFontSmall());
    	helptext.setForeground(new Color(255, 255, 255));
    	helptext.setBackground(new Color(0, 0, 0, 18));
    	helptext.setBorderRendered(false);
    	helptext.setEditable(false);
    	helptext.setWrapEnabled(true);
    	this.setSize(360, 460);
    	this.add(helptext);
    	setDraggable(false);
    }
       
    /**
     * Sets the size
     */
    @Override
    public void setSize(float width, float height) {
            super.setSize(width, height);
            if (helptext != null) helptext.setSize(width -5, height -5);
    }
   
    /**
     * Sets the width
     */
    @Override
    public void setWidth(float width) {
            super.setWidth(width);
            if (helptext != null) helptext.setWidth(width -5);
    }
   
    /**
     * Sets the height
     */
    @Override
    public void setHeight(float height) {
            super.setHeight(height);
            if (helptext != null) helptext.setHeight(height -5);
    }
}

