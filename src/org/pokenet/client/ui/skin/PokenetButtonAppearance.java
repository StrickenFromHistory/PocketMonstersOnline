package org.pokenet.client.ui.skin;

import mdes.slick.sui.Button;
import mdes.slick.sui.Component;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Theme;
import mdes.slick.sui.ToggleButton;
import mdes.slick.sui.skin.ComponentAppearance;
import mdes.slick.sui.skin.SkinUtil;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.gui.GUIContext;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetButtonAppearance extends PokenetComponentAppearance {
    
    private static final Color TRANSPARENT_COLOR = new Color(1f,1f,1f,0f);
    
    protected GradientFill grad;
    protected RoundedRectangle roundBounds;
    protected Button button;
    
    public PokenetButtonAppearance(Button button) {
        this.button = button;
        roundBounds = createRoundedBounds();
        
        //TODO: add CachedGradientFill
        grad = new GradientFill(0f,0f,Color.white,0f,0f,Color.white);
    }
            
    /**
     * Used by subclasses to create the bounds (set the corner/segments) of
     * this button. If <tt>null</tt> is returned, we assume the bounds of the
     * component (results in a non-rounded rectangle). Most subclasses will
     * use <tt>0.0f</tt> for size and location, and will only override this 
     * for different rounded corners and number of segments.
     *
     * @return the rounded rectangle for this button, or <tt>null</tt>
     */
    protected RoundedRectangle createRoundedBounds() {
        return new RoundedRectangle(0f,0f,0f,0f,5f,15);
    }
    
    protected void checkComponent(Component comp) {
        if (comp != this.button) 
            throw new IllegalStateException("PokenetSkin's button appearance " +
                            "only handles the button passed in its constructor");
    }
        
    public boolean contains(Component comp, float x, float y) {
        checkComponent(comp);
        
        //if we are checking corners
        if (PokenetSkin.isRoundRectanglesEnabled() && roundBounds!=null) {
            //updates bounds and checks for contains
            roundBounds.setBounds(comp.getAbsoluteBounds());
            return roundBounds.contains(x, y);
        } else //if we aren't checking corners
            return comp.inside(x, y);
    }
        
    public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
        checkComponent(comp);
        
        //renders base color
        SkinUtil.renderComponentBase(g, comp);
        
        Button btn = (Button)comp;
       
        
        //renders button state
        Rectangle bounds = btn.getAbsoluteBounds();
        Rectangle rect = bounds; //the bounds we will send
        
        //check for round rectangles
        if (roundBounds!=null && PokenetSkin.isRoundRectanglesEnabled()) {
            roundBounds.setBounds(bounds);
            rect = roundBounds;
        }
        
      
        
        // for now we just want to get teh button rendered.
        renderButtonState(g, theme, btn, rect, grad, (PokenetSkin)skin);
             
        //renders text/image
        SkinUtil.renderButtonBase(g, btn);
    }
    
    /**
     * Renders a button state (only gradient and border) based on the given params.
     * This method also checks for ToggleButton instances. If <tt>aRect</tt>
     * is null, we will assume the bounds of the passed button.
     * 
     * 
     * @param g the graphics to render with
     * @param theme the theme we are using
     * @param btn the button to render
     * @param aRect the rectangle we are drawing with, or null
     * @param grad the gradient fill instance to use
     */
    static void renderButtonState(Graphics g, Theme theme, Button btn, Rectangle aRect, GradientFill grad, PokenetSkin s) {
        Rectangle rect = aRect;
        if (rect==null)
            rect = btn.getAbsoluteBounds();
        
        int state = btn.getState();
        float x = aRect.getX();//btn.getAbsoluteX();
        float bottom, y = aRect.getY();//btn.getAbsoluteY();
        GradientFill top, midd, bot;
        
        Color lightTop, lightBot, borderLight, borderDark, base;
        
        borderLight = theme.getPrimaryBorder1();
        borderDark = theme.getPrimaryBorder2();
        
        if (btn instanceof ToggleButton && ((ToggleButton)btn).isSelected()) {
            base = theme.getSecondary1();
            lightTop = theme.getSecondary1();
            lightBot = state==Button.ROLLOVER ? theme.getSecondary1() : theme.getPrimary3();
        } else {
            switch (state) {
                default:
                case Button.UP:
                    base = theme.getPrimary1();
                    lightTop = theme.getPrimary2();
                    lightBot = theme.getPrimary3();
                    top = s.getButtonGradient(0,0,0,s.tM_button.getHeight(), 't', 'u');
                    midd = s.getButtonGradient(0,0,0, s.c_button.getHeight(), 'm', 'u');
                    bot = s.getButtonGradient(0,0,0,s.bM_button.getHeight(), 'b', 'u');
                    break;
                case Button.DOWN:
                    base = theme.getSecondary1();
                    lightTop = theme.getSecondary1();
                    lightBot = theme.getSecondary1();
                    top = s.getButtonGradient(0,0,0,s.tM_button.getHeight(), 't', 'd');
                    midd = s.getButtonGradient(0,0,0, s.c_button.getHeight(), 'm', 'd');
                    bot = s.getButtonGradient(0,0,0,s.bM_button.getHeight(), 'b', 'd');
                    break;
                case Button.ROLLOVER:
                    base = theme.getSecondary1();
                    lightTop = theme.getSecondary2();
                    lightBot = theme.getSecondary3();
                    top = s.getButtonGradient(0,0,0,s.tM_button.getHeight(), 't', 'h');
                    midd = s.getButtonGradient(0,0,0, s.c_button.getHeight(), 'm', 'h');
                    bot = s.getButtonGradient(0,0,0,s.bM_button.getHeight(), 'b', 'h');
                    break;
            }
            if (btn.isPressedOutside()) {
                base = theme.getSecondary1();
                lightTop = theme.getPrimary1();
            }
        }
                
        boolean oldAA = g.isAntiAlias();
        
        float mid = rect.getHeight()/2.0f;
                
        g.setAntiAlias(false);
        
//        grad.setStartColor(lightTop);
//        grad.setEndColor(base);
//        grad.setStart(0, -mid/1.5f);
//        grad.setEnd(0, mid/4);
//        g.fill(rect, grad);
        
        boolean enabled = btn.isEnabled();
        Color disabledColor = theme.getDisabledMask();
//        grad.setStartColor(enabled ? TRANSPARENT_COLOR : disabledColor);
//        grad.setEndColor(enabled ? lightBot : disabledColor);
//        grad.setStart(0, 0);
//        grad.setEnd(0, mid*2);
//        g.fill(rect, grad);
        
//        if (btn.isBorderRendered()) {
            if (aRect instanceof RoundedRectangle)
                g.setAntiAlias(true);
            grad.setStartColor(enabled ? new Color(0,0,0,0) : new Color(0,0,0,0));
            grad.setEndColor(new Color(0,0,0,0));
            grad.setStart(0, -mid);
            grad.setEnd(0, mid); 
            g.draw(rect, grad);
//        }
        
        g.setAntiAlias(oldAA); 
        
        //top 
        g.drawImage(s.tL_button, x, y);
        g.drawImage(s.tM_button, x + s.tL_button.getWidth(), y,
        		x + aRect.getWidth() - s.tR_button.getWidth(),
        		y + s.tM_button.getHeight(),
        		0,0,s.tM_button.getWidth(),s.tM_button.getHeight());
        g.drawImage(s.tR_button, x + aRect.getWidth() - s.tR_button.getWidth(), y);
        
//        middle - only draw if we need too
//        if(aRect.getHeight() > s.tM_button.getHeight() + s.bM_button.getHeight()){
        	y += s.tL_button.getHeight();
        	bottom = y + aRect.getHeight() - s.tL_button.getHeight() - s.bL_button.getHeight();
	        g.drawImage(s.lM_button, x, y,
	        		x + s.lM_button.getWidth(),
	        		bottom,
	        		0,1,s.lM_button.getWidth(), s.lM_button.getHeight() - 1);
	        g.drawImage(s.c_button, x + s.lM_button.getWidth(), y,
	        		x + aRect.getWidth() - s.rM_button.getWidth(),
	        		bottom,
	        		0,0,1,1);//s.c_button.getWidth(),s.c_button.getHeight());
	        g.drawImage(s.rM_button, x + aRect.getWidth() - s.rM_button.getWidth(), y,
	        		x + aRect.getWidth(), 
	        		bottom,
	        		0,1,s.rM_button.getWidth(),s.rM_button.getHeight() - 1);
//        }
        //bottom
        y = btn.getAbsoluteY() + aRect.getHeight() - s.bL_button.getHeight();
        g.drawImage(s.bL_button, x, y);
        g.drawImage(s.bM_button, x + s.bL_button.getWidth(), y,
        		x + aRect.getWidth() - s.bR_button.getWidth(),
        		y + s.bM_button.getHeight(),
        		0,0,s.bM_button.getWidth(),s.bM_button.getHeight());
        g.drawImage(s.bR_button, x + aRect.getWidth() - s.bR_button.getWidth(), y);
        
    }
}