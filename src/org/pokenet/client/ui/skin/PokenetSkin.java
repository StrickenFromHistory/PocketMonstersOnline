package org.pokenet.client.ui.skin;

import mdes.slick.sui.Button;
import mdes.slick.sui.CheckBox;
import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.List;
import mdes.slick.sui.ScrollBar;
import mdes.slick.sui.ScrollPane;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Slider;
import mdes.slick.sui.TextArea;
import mdes.slick.sui.TextField;
import mdes.slick.sui.ToggleButton;
import mdes.slick.sui.ToolTip;
import mdes.slick.sui.Window;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;
import mdes.slick.sui.event.MouseListener;
import mdes.slick.sui.skin.ComponentAppearance;
import mdes.slick.sui.skin.FontUIResource;
import mdes.slick.sui.skin.FrameAppearance;
import mdes.slick.sui.skin.ImageUIResource;
import mdes.slick.sui.skin.ScrollBarAppearance;
import mdes.slick.sui.skin.ScrollPaneAppearance;
import mdes.slick.sui.skin.SliderAppearance;

import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.util.Log;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetSkin implements Skin {
	private static final String skinImageLocations = "res/ui/skin/";
    
    private Image checkBoxImage;
    private Image closeButtonImage;
    private Image resizerImage;
    private Font font;
    private Cursor selectCursor;
    private Cursor resizeCursor;
    
    // for the frame
    static Image tL_frame, tM_frame, tR_frame;
    static Image lM_frame, c_frame,  rM_frame;
    static Image bL_frame, bM_frame, bR_frame;
    
    // for the buttons
    Image tL_button, tM_button, tR_button;
    Image lM_button, c_button,  rM_button;
    Image bL_button, bM_button, bR_button;

    // for the buttons
    Image tL_textField, tM_textField, tR_textField;
    Image lM_textField, c_textField,  rM_textField;
    Image bL_textField, bM_textField, bR_textField;
    
    // colors for gradients
    Color top;
	Color bottom;
	Color midTop;
	Color midBottom;
	
	Color topH;
	Color bottomH;
	Color midTopH;
	Color midBottomH;
    
    
    private boolean selectCursorFailed = false;
    private boolean resizeCursorFailed = false;
        
    private MouseListener selectCursorListener;
    private MouseListener resizeCursorListener;
    
    private static boolean roundRectanglesEnabled = true;
           
    //we can cache some of our appearances, others need to be created & attached to components
    private ComponentAppearance containerAppearance = new PokenetContainerAppearance();
    private ComponentAppearance toolTipAppearance = new PokenetToolTipAppearance();
    private ComponentAppearance labelAppearance = new PokenetLabelAppearance();
    private ComponentAppearance textAreaAppearance = new PokenetTextAreaAppearance();
    
    private ScrollPaneAppearance scrollPaneAppearance = new PokenetScrollPaneAppearance();
    private FrameAppearance frameAppearance = new PokenetFrameAppearance();
    private ScrollBarAppearance scrollBarAppearance = new PokenetScrollBarAppearance();
    private SliderAppearance sliderAppearance = new PokenetSliderAppearance();
        
    public static boolean isRoundRectanglesEnabled() {
        return roundRectanglesEnabled;
    }

    public static void setRoundRectanglesEnabled(boolean aRoundRectanglesEnabled) {
        roundRectanglesEnabled = aRoundRectanglesEnabled;
    }
    
    public String getName() {
        return "Pokenet";
    }
    
    public boolean isThemeable() {
        return true;
    }
    
    public void install() throws SlickException {
                ///////////////////
                // CACHE OBJECTS //
                ///////////////////
        
        //try loading
        //ResourceLoader will spit out a log message if there are problems
        loadImages();

        
        if (selectCursorListener==null && selectCursor!=null)
            selectCursorListener = new CursorListener(selectCursor);
        if (resizeCursorListener==null && resizeCursor!=null)
            resizeCursorListener = new CursorListener(resizeCursor);
        
        //fonts
        if (font==null)
            font = tryFont("res/skin/shared/verdana.fnt", "res/skin/shared/verdana.png");
    }
    
    /**
	 * TODO Put here a description of what this method does.
	 *
	 */
	private void loadImages() {
		if (checkBoxImage==null)
            checkBoxImage = tryImage("Checkbox.png");
        if (closeButtonImage==null)
            closeButtonImage = tryImage("CloseWindow.png");
        if (selectCursor==null) 
            selectCursor = tryCursor("res/skin/shared/cursor_select.png", 4, 8);
            //selectCursor = tryCursor("res/skin/shared/cursor_hand.png", 6, 0);
        //if (resizeCursor==null)
        //    resizeCursor = tryCursor("res/skin/shared/cursor_resize.png", 4, 4);

        
        //frame images
        if(tL_frame == null) tL_frame = tryImage("frame/topLeft.png");
        if(tM_frame == null) tM_frame = tryImage("frame/topMiddle.png");
        if(tR_frame == null) tR_frame = tryImage("frame/topRight.png");
        if(lM_frame == null) lM_frame = tryImage("frame/leftMiddle.png");
        if(rM_frame == null) rM_frame = tryImage("frame/rightMiddle.png");
        if(bL_frame == null) bL_frame = tryImage("frame/bottomLeft.png");
        if(bM_frame == null) bM_frame = tryImage("frame/bottomMiddle.png");
        if(bR_frame == null) bR_frame = tryImage("frame/bottomRight.png");
        
        //button images
        if(tL_button == null) tL_button = tryImage("button/topLeft.png");
        if(tM_button == null) tM_button = tryImage("button/topMiddle.png");
        if(tR_button == null) tR_button = tryImage("button/topRight.png");
        if(lM_button == null) lM_button = tryImage("button/leftMiddle.png");
        if(c_button == null)  c_button  = tryImage("button/center.png");
        if(rM_button == null) rM_button = tryImage("button/rightMiddle.png");
        if(bL_button == null) bL_button = tryImage("button/bottomLeft.png");
        if(bM_button == null) bM_button = tryImage("button/bottomMiddle.png");
        if(bR_button == null) bR_button = tryImage("button/bottomRight.png");

        //text field images
        if(tL_textField == null) tL_textField = tryImage("textfield/topLeft.png");
        if(tM_textField == null) tM_textField = tryImage("textfield/topMiddle.png");
        if(tR_textField == null) tR_textField = tryImage("textfield/topRight.png");
        if(lM_textField == null) lM_textField = tryImage("textfield/leftMiddle.png");
        if(c_textField == null)  c_textField  = tryImage("textfield/center.png");
        if(rM_textField == null) rM_textField = tryImage("textfield/rightMiddle.png");
        if(bL_textField == null) bL_textField = tryImage("textfield/bottomLeft.png");
        if(bM_textField == null) bM_textField = tryImage("textfield/bottomMiddle.png");
        if(bR_textField == null) bR_textField = tryImage("textfield/bottomRight.png");
        
        
        //set up gradients 
        if(top == null){
        	
        	// we need to split the gradient into 3 parts because of how 
        	// the images are split up
        	top = tM_frame.getColor(0,0); // start
        	bottom = bM_button.getColor(0, 10); // stop
        	midTop = new Color(top.r - ((top.r - bottom.r) / 3 * 2),
        			top.g - ((top.g - bottom.g) / 3 * 2 ), 
        			top.b - ((top.b - bottom.b) / 3 * 2));
        	midBottom = new Color(top.r - ((top.r - bottom.r) / 3),
        			top.g - ((top.g - bottom.g) / 3), 
        			top.b - ((top.b - bottom.b) / 3));
        	
        	// now we need to calculate the the down / hover colors
        	topH = new Color(top.r - 50, top.g - 50, top.b - 50); // start
        	bottomH = new Color(bottom.r - 50, bottom.g - 50, bottom.b - 50); // stop
        	midTopH = new Color(midTop.r - 50, midTop.g - 50, midTop.b - 50);
        	midBottomH = new Color(midBottom.r - 50, midBottom.g - 50, midTop.b - 50);
        	
        }
        	
	}

	private Cursor tryCursor(String ref, int x, int y) {
        try {
            return CursorLoader.get().getCursor(ref, x, y);
        } catch (Exception e) {
            Log.error("Failed to load and apply SUI 'select' cursor.", e);
            return null;
        }
    }
    
    private Image tryImage(String s) {
        try { return new ImageUIResource(skinImageLocations + s); }
        catch (Exception e) { return null; }
    }
    
    private Font tryFont(String s1, String s2) {
        try { return new FontUIResource.AngelCodeFont(s1, s2); }
        catch (Exception e) { return null; }
    }

    public void uninstall() throws SlickException {
    }
    
    public Image getCheckBoxImage() {
        return checkBoxImage;
    }
    
    public Image getCloseButtonImage() {
        return closeButtonImage;
    }
    
    public Font getFont() {
        return font;
    }
    
    public Cursor getSelectCursor() {
        return selectCursor;
    }

    public Cursor getResizeCursor() {
        return resizeCursor;
    }
    
    public MouseListener getSelectCursorListener() {
        return selectCursorListener;
    }

    public MouseListener getResizeCursorListener() {
        return resizeCursorListener;
    }
    
    public ComponentAppearance getContainerAppearance(Container comp) {
        return containerAppearance;
    }

    public ComponentAppearance getCheckBoxAppearance(CheckBox comp) {
        return new PokenetCheckBoxAppearance(comp);
    }

    public FrameAppearance getFrameAppearance(Frame comp) {
        return frameAppearance;
    }

    public ComponentAppearance getButtonAppearance(Button comp) {
        return new PokenetButtonAppearance(comp);
    }

    public ComponentAppearance getToolTipAppearance(ToolTip comp) {
        return toolTipAppearance;
    }

    public ComponentAppearance getLabelAppearance(Label comp) {
        return labelAppearance;
    }
    
    public ComponentAppearance getToggleButtonAppearance(ToggleButton comp) {
        return new PokenetButtonAppearance(comp);
    }

    public ScrollBarAppearance getScrollBarAppearance(ScrollBar comp) {
        return scrollBarAppearance;
    }

    public ScrollPaneAppearance getScrollPaneAppearance(ScrollPane comp) {
        return scrollPaneAppearance;
    }
    
    public SliderAppearance getSliderAppearance(Slider comp) {
        return sliderAppearance;
    }
    
    public ComponentAppearance getTextFieldAppearance(TextField comp) {
        return new PokenetTextFieldAppearance(comp);
    }
    
    public ComponentAppearance getTextAreaAppearance(TextArea comp) {
        return textAreaAppearance;
    }
    
    public ComponentAppearance getWindowAppearance(Window window) {
        return containerAppearance;
    }
    

    
    private class CursorListener extends MouseAdapter {
        
        private Cursor c;
        private boolean failed = false;
        private boolean dragging = false;
        private boolean inside = false;
        
        public CursorListener(Cursor c) {
            this.c = c;
        }
        
        public void mouseReleased(MouseEvent ev) {
            dragging = false;
            if (!inside)
                release();
        }
        
        public void mouseDragged(MouseEvent ev) {
            dragging = true;
        }
        
        public void mouseEntered(MouseEvent ev) {
            inside = true;
            if (!failed) {
                try { Mouse.setNativeCursor(c); }
                catch (Exception e) {
                    failed = true; 
                }
            }
        }
        
        public void mouseExited(MouseEvent ev) {
            inside = false;
            if (!dragging)
                release();
        }
        
        void release() {
            try { Mouse.setNativeCursor(null); }
            catch (Exception e) { }
        }
    }

	@Override
	public ComponentAppearance getListAppearance(mdes.slick.sui.List arg0) {
		// TODO Auto-generated method stub.
		return null;
	}
	
	/**
	 * 
	 * Generates a gradient for the buttons
	 * It's easier to generage these than to have to make new files for them every time
	 *
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param pos either t for top, m for middle, or b for bottom
	 * @param state h for hover, u for up, or d for down
	 */
	public GradientFill getButtonGradient(float startX, float startY, float endX, float endY, char pos, char state){
		switch (state){
			case 'h':
				switch (pos){
					case 't': return new GradientFill(startX, startY, topH, endX, endY, midTopH);
					case 'm': return new GradientFill(startX, startY, midTopH, endX, endY, midBottomH);
					case 'b': return new GradientFill(startX, startY, bottomH, endX, endY, midBottomH);
					default: return null;
					} 
			case 'd':
				switch (pos){
					case 't': return new GradientFill(startX, startY, topH, endX, endY, midTopH);
					case 'm': return new GradientFill(startX, startY, midTopH, endX, endY, midBottomH);
					case 'b': return new GradientFill(startX, startY, bottomH, endX, endY, midBottomH);
					default: return null;
					} 
			case 'u':
				switch (pos){
					case 't': return new GradientFill(startX, startY, top, endX, endY, midTop);
					case 'm': return new GradientFill(startX, startY, midTop, endX, endY, midBottom);
					case 'b': return new GradientFill(startX, startY, bottom, endX, endY, midBottom);
					default: return null;
					} 
			default: return null;
		}
		
	}
}