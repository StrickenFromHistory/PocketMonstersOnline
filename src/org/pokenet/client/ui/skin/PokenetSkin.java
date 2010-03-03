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
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.CursorLoader;
import org.newdawn.slick.util.Log;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetSkin implements Skin {
    
    private Image checkBoxImage;
    private Image closeButtonImage;
    private Image resizerImage;
    private Font font;
    private Cursor selectCursor;
    private Cursor resizeCursor;
    
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
        
        //images
        if (checkBoxImage==null)
            checkBoxImage = tryImage("res/skin/Pokenet/checkbox.png");
        if (closeButtonImage==null)
            closeButtonImage = tryImage("res/skin/Pokenet/closewindow.png");
        if (selectCursor==null) 
            selectCursor = tryCursor("res/skin/shared/cursor_select.png", 4, 8);
            //selectCursor = tryCursor("res/skin/shared/cursor_hand.png", 6, 0);
        //if (resizeCursor==null)
        //    resizeCursor = tryCursor("res/skin/shared/cursor_resize.png", 4, 4);
        
        if (selectCursorListener==null && selectCursor!=null)
            selectCursorListener = new CursorListener(selectCursor);
        if (resizeCursorListener==null && resizeCursor!=null)
            resizeCursorListener = new CursorListener(resizeCursor);
        
        //fonts
        if (font==null)
            font = tryFont("res/skin/shared/verdana.fnt", "res/skin/shared/verdana.png");
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
        try { return new ImageUIResource(s); }
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
}