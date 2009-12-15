package org.pokenet.client.ui.frames;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.TextArea;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.FileLoader;

/**
 * Base for speech pop-ups
 * @author ZombieBear
 *
 */
public class SpeechFrame extends Frame {
    Queue<String> speechQueue;

    TextArea speechDisplay;

    Timer printingTimer = new Timer();
    TimerTask animAction;

    Polygon triangle;

    Image bg;
    boolean isGoingDown = true;
    protected String stringToPrint;
    
    /**
     * Default constructor
     * @param speech
     */
    public SpeechFrame(String speech) {
    	getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		this.setBorderRendered(false);
		speechQueue = new LinkedList<String>();
		for (String line : speech.split("/n")) {
			speechQueue.add(line);
		}
		triangulate();
		initGUI();
    }
    
    /**
     * Constructs a SpeechFrame, uses Seconds between next line. 
     * @param speech
     * @param seconds
     */
    public SpeechFrame(String speech, int seconds) {
    	speechQueue = new LinkedList<String>();
    	for (String line : speech.split("/n")) {
    		speechQueue.add(line);
    	}
    	triangulate();
    	initGUI();
    	this.advance();
//            this.advance();
//            while(canAdvance()){
//	            try {
//					wait(seconds*1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//	            advance();
//            }
    }
    /**
     * Sends a packet when finished showing dialog
     * @param printed
     */
    public void advancedPast(String printed) {
            
    }
    
    /**
     * Initializes the interface
     */
    public void initGUI() {
    	InputStream f;
    	try {
    		LoadingList.setDeferredLoading(true);
    		String respath = System.getProperty("res.path");
    		if(respath==null)
    			respath="";
    		f = FileLoader.loadFile(respath+"res/ui/speechbox.png");
    		bg = new Image(f, respath+"res/ui/speechbox.png", false);
    		LoadingList.setDeferredLoading(false);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	Label bg = new Label(this.bg);
    	bg.setSize(400, 100);
    	bg.setLocation(0, -11);
    	speechDisplay = new TextArea();
    	
    	speechDisplay.setFocusable(false);
    	speechDisplay.setSize(384, 100);
    	speechDisplay.setLocation(16, 5);
    	speechDisplay.setBorderRendered(false);
    	speechDisplay.setFont(GameClient.getFontLarge());
    	speechDisplay.setOpaque(false);
    	this.getContentPane().add(bg);
    	this.getContentPane().add(speechDisplay);
    	
    	this.setWidth(400);
    	this.setHeight(100);
    	this.setX((GameClient.getInstance().getDisplay().getWidth() / 2) - getWidth() / 2);
    	this.setY((GameClient.getInstance().getDisplay().getHeight() / 2) + getWidth() / 2);
    	this.getTitleBar().setVisible(false);
    	this.setResizable(false);
    	
    	this.setFocusable(false);
    	
    	this.setAlwaysOnTop(true);
    	advance();
    }

    /**
     * ???
     * @param done
     */
    public void advanced(String done) {

    }
    
    /**
     * Advances to next message
     */
    public void advance() {
    	triangle = null;

    	if (animAction == null) {
    		if (canAdvance()) {
    			speechDisplay.setText("");
    			if (stringToPrint != null)
    				advancedPast(stringToPrint);
    				stringToPrint = speechQueue.poll();
    				if (stringToPrint != null) {
    					animAction = new TimerTask() {
    						public void run() {
    							if (speechDisplay.getText().equals(stringToPrint)) {
    								animAction = null;
    								try {
    									cancel();
    								} catch (IllegalStateException e) { }
    								triangulate();
    							} else {
    								try {
    									speechDisplay.setText(stringToPrint.substring(0, speechDisplay
    											.getText().length() + 1));
    								} catch (StringIndexOutOfBoundsException e) {
    									speechDisplay.setText(stringToPrint);
    								}
    						}
    					}};	
    						try {
    							printingTimer.schedule(animAction, 0, 30);}
    						catch (Exception e) { 
    							animAction = null;
    							e.printStackTrace();
    						}
    						advancing(stringToPrint);
    				}
    		}
    	} else {
    		speechDisplay.setText("");
    		animAction.cancel();
    		animAction = null;
    		speechDisplay.setText(stringToPrint);
    		triangulate();
    	}
    }
    
    /**
     * Returns true if the player can advance
     * @return
     */
    public boolean canAdvance() {
            return true;
    }
    
    /**
     * Generates the triangle to show when you can continue
     */
    public void triangulate() {
    	triangle = new Polygon();
    	triangle.addPoint(getWidth() - 30 + getX(), 60 +getY());
    	triangle.addPoint(getWidth() - 30 + getX() + 10, 60 + getY());
    	triangle.addPoint(getWidth() - 30  + getX() + 5, 60 + getY() + 10);
    }
    
    /**
     * ?????
     * @param toPrint
     */
    public void advancing(String toPrint) {

    }
    
    /**
     * Renders
     */
    @Override
    public void render(GUIContext container, Graphics g) {
    	super.render(container, g);

    	if (triangle != null) {
    		if (canAdvance()) {
    			g.setColor(Color.red);
    			g.fill(triangle);
    			if (Math.round(triangle.getCenterY()) > 584)
    				triangle.setCenterY(584);
    			else if (Math.round(triangle.getCenterY()) < 574)
    				triangle.setCenterY(574);
    			if (Math.round(triangle.getCenterY()) == 574) {
    				isGoingDown = true;
    			} else if (Math.round(triangle.getCenterY()) == 584) {
    				isGoingDown = false;
    			}
    			if (isGoingDown) {
    				triangle.setCenterY(triangle.getCenterY() + .5f);
    			} else {
    				triangle.setCenterY(triangle.getCenterY() - .5f);
    			}
    		}
    	}
    }
    
    /**
     * Adds a line to the queue
     * @param speech
     */
    public void addSpeech(String speech) {
    	speechQueue.add(speech);
    }
}
