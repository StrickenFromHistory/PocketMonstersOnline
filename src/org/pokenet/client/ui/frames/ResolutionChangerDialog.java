package org.pokenet.client.ui.frames;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.muffin.FileMuffin;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.Translator;

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
	private int m_currentResolutionWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private int m_currentResolutionHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	private int m_currentGameWidth = GameClient.getInstance().getWidth();
	private int m_currentGameHeight = GameClient.getInstance().getHeight();
	private int m_buttonWidth = 100;
	private int m_margin = 8;
	private int m_buttonHeight = 32;
	private int m_gutter = m_margin * 4;
	private Color m_white = new Color(255, 255, 255);

	
	private static final int[] widths_4x3 = {800, 1024, 1280, 1400, 1600, 2048}; 
	private static final int[] heights_4x3 = {600, 768,  960, 1050, 1200, 1536};
	private static Button[] m_buttons_4x3 = new Button[widths_4x3.length];
	
	private static final int[] widths_16x10 = {1280, 1440, 1680, 1920, 2560};
	private static final int[] heights_16x10 = {800,  900, 1050, 1200, 1600};
	private static Button[] m_buttons_16x10 = new Button[widths_16x10.length];
	
	private static final int[] widths_16x9 = {854, 1280, 1366, 1920};
	private static final int[] heights_16x9 = {480, 720, 768, 1080};
	private static Button[] m_buttons_16x9 = new Button[widths_16x9.length];
	
	private static final int [][] widths = {widths_4x3, widths_16x10, widths_16x9};
	private static final int [][] heights = {heights_4x3, heights_16x10, heights_16x9};
	
	private Button[][] m_buttons = {m_buttons_4x3, m_buttons_16x10, m_buttons_16x9};
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

		this.setSize((m_buttonWidth * 2) + 2 * m_margin + m_gutter, 
				(widths_4x3.length + widths_16x10.length - 2) * (m_buttonHeight + m_margin) + m_margin);
		setCenter();
		
		// add the labels, telling them what ratio is which
		Label four_three = new Label("4x3");
		four_three.pack();
		four_three.setLocation(m_margin + m_buttonWidth / 2 - four_three.getWidth() / 2, m_margin);
		four_three.setVisible(true);
		four_three.setFont(GameClient.getFontSmall());
		four_three.setForeground(m_white);
		this.add(four_three);
		
		float header = m_margin + four_three.getHeight();

		Label sixteen_ten = new Label("16x10");
		sixteen_ten.pack();
		sixteen_ten.setLocation(m_margin + m_buttonWidth / 2 + m_buttonWidth + m_gutter - sixteen_ten.getWidth() / 2, m_margin);
		sixteen_ten.setVisible(true);
		sixteen_ten.setFont(GameClient.getFontSmall());
		sixteen_ten.setForeground(m_white);
		this.add(sixteen_ten);
		
		Label sixteen_nine = new Label("16x9");
		sixteen_nine.pack();
		sixteen_nine.setLocation(m_margin + m_buttonWidth / 2 + m_buttonWidth + m_gutter - sixteen_nine.getWidth() / 2, 
				 m_margin + (widths_16x9.length * m_buttonHeight + header));
		sixteen_nine.setVisible(true);
		sixteen_nine.setFont(GameClient.getFontSmall());
		sixteen_nine.setForeground(m_white);
		this.add(sixteen_nine);
		
		Line divider = new Line(m_margin + m_buttonWidth + m_gutter / 2, m_margin,
				this.getHeight() - m_margin, m_margin + m_buttonWidth + m_gutter / 2);
//		this.add(divider.get);
		
		// add the resolutions
		Button[] tempButtons;
		int[] tempWidths;
		int[] tempHeights;
		float yOffset = header;
		int x = m_margin;// + (int)four_three.getHeight() + margin;
		
		for (int j = 0; j < widths.length; j++){
			
			tempWidths = widths[j];
			tempHeights = heights[j];
			tempButtons = m_buttons[j];
			
			if(j == 1) {
				x = x + m_buttonWidth + m_gutter;
				yOffset = header;
			}
			if(j == 2) yOffset = m_buttonHeight * (heights_4x3.length - 1) + m_margin;
			
			for (int i = 0; i < tempWidths.length; i++) {
				if (tempWidths[i] <= m_currentResolutionWidth && tempHeights[i] <= m_currentResolutionHeight) {
					m_buttons[j][i] = new Button(tempWidths[i] + "x" + tempHeights[i]);
					m_buttons[j][i].setSize(m_buttonWidth, m_buttonHeight);
					m_buttons[j][i].setLocation(x, i * m_buttonHeight + yOffset + m_margin);
					m_buttons[j][i].setVisible(true);
					if (tempWidths[i] == m_currentGameWidth && tempHeights[i] == m_currentGameHeight) 
						m_buttons[j][i].setEnabled(false);
					this.add(m_buttons[j][i]);
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
		if(null != m_buttons_4x3[0]) m_buttons_4x3[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(800, 600);
			}
		});

		//1024x768
		if(null != m_buttons_4x3[1]) m_buttons_4x3[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1024, 768);
			}
		});
		
		//1280x960
		if(null != m_buttons_4x3[2]) m_buttons_4x3[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1280, 960);
			}
		});
		
		//1400x1050
		if(null != m_buttons_4x3[3]) m_buttons_4x3[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1400, 1050);
			}
		});
		
		//1600x1200
		if(null != m_buttons_4x3[4]) m_buttons_4x3[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1600, 1200);
			}
		});
		
		//2048x1536
		if(null != m_buttons_4x3[5]) m_buttons_4x3[5].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(2048, 1536);
			}
		});
		
		/* *********************************
		 *               16:10
		 ***********************************/
		
		//1280x800
		if(null != m_buttons_16x10[0]) m_buttons_16x10[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1280, 800);
			}
		});

		//1440x900
		if(null != m_buttons_16x10[1]) m_buttons_16x10[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1440, 900);
			}
		});
		
		//1680x1050
		if(null != m_buttons_16x10[2]) m_buttons_16x10[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1680, 1050);
			}
		});
		
		//1920x1200
		if(null != m_buttons_16x10[3]) m_buttons_16x10[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1920, 1200);
			}
		});
		
		//2560x1600
		if(null != m_buttons_16x10[4]) m_buttons_16x10[4].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(2560, 1600);
			}
		});
		
		/* *********************************
		 *               16:9
		 ***********************************/
		
		//854x480
		if(null != m_buttons_16x9[0]) m_buttons_16x9[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(854, 480);
			}
		});

		//1280x720
		if(null != m_buttons_16x9[1])m_buttons_16x9[1].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1280, 720);
			}
		});
		
		//1366x768
		if(null != m_buttons_16x9[2]) m_buttons_16x9[2].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1366, 768);
			}
		});
		
		//1920x1080
		if(null != m_buttons_16x9[3]) m_buttons_16x9[3].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setNewGameDimensions(1920, 1080);
			}
		});
		
	}


	protected void setNewGameDimensions(int width, int height) {
//		TODO: Used for dynamic changing of resolution: currently doesn't work
//		GameClient.getInstance().getDisplay().setWidth(width);
//		GameClient.getInstance().getDisplay().setHeight(height);
		this.setVisible(false);

		GameClient.messageDialog(Translator.translate("_GUI").get(19), getDisplay());
		
		
		GameClient.getInstance().getOptions().put("width", width + "");
		GameClient.getInstance().getOptions().put("height", height + "");
		FileMuffin fm = new FileMuffin();
		try {
			fm.saveFile(GameClient.getInstance().getOptions(), "options.dat");
		}
		catch (IOException exception) {
			// TODO Auto-generated catch-block stub.
			exception.printStackTrace();
		}		
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
