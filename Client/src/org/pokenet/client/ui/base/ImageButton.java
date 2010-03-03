package org.pokenet.client.ui.base;

import mdes.slick.sui.Button;

import org.newdawn.slick.Image;

/**
 * Creates an imagebutton
 * @author shaowkanji
 *
 */
public class ImageButton extends Button {
	/**
	 * Constructor
	 * @param normal
	 * @param hover
	 * @param down
	 */
	public ImageButton(Image normal, Image hover, Image down) {
		super();
		setImage(normal);
		setRolloverImage(hover);
		setDownImage(down);
		setDisabledImage(down); //Temporarily used the button pressed image as disabled image
		setPadding(0);
		setOpaque(false);
	}
	
	/**
	 * Default constuctor
	 */
	public ImageButton() {
		super();
		setPadding(0);
		setOpaque(false);
	}
	
	/**
	 * Sets this button's images
	 * @param normal
	 * @param hover
	 * @param down
	 */
	public void setImages(Image normal, Image hover, Image down) {
		setImage(normal);
		setRolloverImage(hover);
		setDownImage(down);
	}
}