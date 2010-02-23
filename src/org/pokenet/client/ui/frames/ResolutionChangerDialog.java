package org.pokenet.client.ui.frames;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Line;
import org.pokenet.client.GameClient;

import mdes.slick.sui.Button;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

/**
 *Shows all available resolutions for the current monitor
 *
 * @author lprestonsegoiii.
 *         Created Feb 22, 2010.
 */
public class ResolutionChangerDialog extends Frame{
	private int currentResolutionWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private int currentResolutionHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	private int buttonWidth = 100;
	private int margin = 8;
	private int buttonHeight = 32;
	private int gutter = margin * 4;
	private Color m_white = new Color(255, 255, 255);

	
	private static final int[] widths_4x3 = {800, 1024, 1280, 1400, 1600, 2048}; 
	private static final int[] heights_4x3 = {600, 768,  960, 1050, 1200, 1536};
	private static Button[] buttons_4x3 = new Button[widths_4x3.length];
	
	private static final int[] widths_16x10 = {1280, 1440, 1680, 1920, 2560};
	private static final int[] heights_16x10 = {800,  900, 1050, 1200, 1600};
	private static Button[] buttons_16x10 = new Button[widths_16x10.length];
	
	private static final int[] widths_16x9 = {854, 1280, 1366, 1920};
	private static final int[] heights_16x9 = {480, 720, 768, 1080};
	private static Button[] buttons_16x9 = new Button[widths_16x9.length];
	
	private static final int [][] widths = {widths_4x3, widths_16x10, widths_16x9};
	private static final int [][] heights = {heights_4x3, heights_16x10, heights_16x9};
	
	private Button[][] buttons = {buttons_4x3, buttons_16x10, buttons_16x9};
	/**
	 * Default constructor
	 */
	public ResolutionChangerDialog() {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		this.setBackground(new Color(0,0,0,140));
		this.getTitleBar().setForeground(new Color(0, 0, 0));
		this.setDraggable(true);
		this.setResizable(false);
		this.setTitle("Change Game Resolution");
		GameClient.getInstance().getDisplay().add(this);

		this.setSize((buttonWidth * 2) + 2 * margin + gutter, 
				(widths_4x3.length + widths_16x10.length - 2) * (buttonHeight + margin) + margin);
		setCenter();
		
		// add the labels, telling them what ratio is which
		Label four_three = new Label("4x3");
		four_three.pack();
		four_three.setLocation(margin + buttonWidth / 2 - four_three.getWidth() / 2, margin);
		four_three.setVisible(true);
		four_three.setFont(GameClient.getFontSmall());
		four_three.setForeground(m_white);
		this.add(four_three);
		
		float header = margin + four_three.getHeight();

		Label sixteen_ten = new Label("16x10");
		sixteen_ten.pack();
		sixteen_ten.setLocation(margin + buttonWidth / 2 + buttonWidth + gutter - sixteen_ten.getWidth() / 2, margin);
		sixteen_ten.setVisible(true);
		sixteen_ten.setFont(GameClient.getFontSmall());
		sixteen_ten.setForeground(m_white);
		this.add(sixteen_ten);
		
		Label sixteen_nine = new Label("16x9");
		sixteen_nine.pack();
		sixteen_nine.setLocation(margin + buttonWidth / 2 + buttonWidth + gutter - sixteen_nine.getWidth() / 2, 
				 margin + (widths_16x9.length * buttonHeight + header));
		sixteen_nine.setVisible(true);
		sixteen_nine.setFont(GameClient.getFontSmall());
		sixteen_nine.setForeground(m_white);
		this.add(sixteen_nine);
		
		Line divider = new Line(margin + buttonWidth + gutter / 2, margin,
				this.getHeight() - margin, margin + buttonWidth + gutter / 2);
//		this.add(divider.get);
		
		// add the resolutions
		Button[] tempButtons;
		int[] tempWidths;
		int[] tempHeights;
		float yOffset = header;
		int x = margin;// + (int)four_three.getHeight() + margin;
		
		for (int j = 0; j < widths.length; j++){
			
			tempWidths = widths[j];
			tempHeights = heights[j];
			tempButtons = buttons[j];
			
			if(j == 1) {
				x = x + buttonWidth + gutter;
				yOffset = header;
			}
			if(j == 2) yOffset = buttonHeight * (heights_4x3.length - 1) + margin;
			
			for (int i = 0; i < tempWidths.length; i++) {
				if (tempWidths[i] <= currentResolutionWidth && tempHeights[i] <= currentResolutionHeight) {
					buttons[j][i] = new Button(tempWidths[i] + "x" + tempHeights[i]);
					buttons[j][i].setSize(buttonWidth, buttonHeight);
					buttons[j][i].setLocation(x, i * buttonHeight + yOffset + margin);
					buttons[j][i].setVisible(true);
					this.add(buttons[j][i]);
				}
			}
		}
		
		
		
		// add the actionListeners
		addActionListeners();
		
		this.setVisible(false);
	}
	
	
	private void addActionListeners() {
		/* *********************************
		 *               4:3
		 ***********************************/
		
		//800x600
		if(null != buttons_4x3[0]) buttons_4x3[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(800, 600);
			}
		});

		//1024x768
		if(null != buttons_4x3[1]) buttons_4x3[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1024, 768);
			}
		});
		
		//1280x960
		if(null != buttons_4x3[2]) buttons_4x3[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1280, 960);
			}
		});
		
		//1400x1050
		if(null != buttons_4x3[3]) buttons_4x3[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1400, 1050);
			}
		});
		
		//1600x1200
		if(null != buttons_4x3[4]) buttons_4x3[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1600, 1200);
			}
		});
		
		//2048x1536
		if(null != buttons_4x3[5]) buttons_4x3[5].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(2048, 1536);
			}
		});
		
		/* *********************************
		 *               16:10
		 ***********************************/
		
		//1280x800
		if(null != buttons_16x10[0]) buttons_16x10[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1280, 800);
			}
		});

		//1440x900
		if(null != buttons_16x10[1]) buttons_16x10[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1440, 900);
			}
		});
		
		//1680x1050
		if(null != buttons_16x10[2]) buttons_16x10[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1680, 1050);
			}
		});
		
		//1920x1200
		if(null != buttons_16x10[3]) buttons_16x10[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1920, 1200);
			}
		});
		
		//2560x1600
		if(null != buttons_16x10[4]) buttons_16x10[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(2560, 1600);
			}
		});
		
		/* *********************************
		 *               16:9
		 ***********************************/
		
		//854x480
		if(null != buttons_16x9[0]) buttons_16x9[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(854, 480);
			}
		});

		//1280x720
		if(null != buttons_16x9[1])buttons_16x9[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1280, 720);
			}
		});
		
		//1366x768
		if(null != buttons_16x9[2]) buttons_16x9[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1366, 768);
			}
		});
		
		//1920x1080
		if(null != buttons_16x9[3]) buttons_16x9[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1920, 1080);
			}
		});
		
	}


	protected void setNewGameDimensions(int width, int height) {
		GameClient.getInstance().getDisplay().setWidth(width);
		GameClient.getInstance().getDisplay().setHeight(height);
	}


	/**
	 * Centers the frame
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - ((int)getWidth()/2);
		int y = (height / 2) - ((int)getHeight()/2);
		this.setLocation(x, y);
	}
}
