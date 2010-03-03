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
        
        renderButtonState(g, theme, btn, rect, grad);
             
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
    static void renderButtonState(Graphics g, Theme theme, Button btn, Rectangle aRect, GradientFill grad) {
        Rectangle rect = aRect;
        if (rect==null)
            rect = btn.getAbsoluteBounds();
        
        int state = btn.getState();
        
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
                    break;
                case Button.DOWN:
                    base = theme.getSecondary1();
                    lightTop = theme.getSecondary1();
                    lightBot = theme.getSecondary1();
                    break;
                case Button.ROLLOVER:
                    base = theme.getSecondary1();
                    lightTop = theme.getSecondary2();
                    lightBot = theme.getSecondary3();
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
        
        grad.setStartColor(lightTop);
        grad.setEndColor(base);
        grad.setStart(0, -mid/1.5f);
        grad.setEnd(0, mid/4);
        g.fill(rect, grad);
        
        boolean enabled = btn.isEnabled();
        Color disabledColor = theme.getDisabledMask();
        grad.setStartColor(enabled ? TRANSPARENT_COLOR : disabledColor);
        grad.setEndColor(enabled ? lightBot : disabledColor);
        grad.setStart(0, 0);
        grad.setEnd(0, mid*2);
        g.fill(rect, grad);
        
        if (btn.isBorderRendered()) {
            if (aRect instanceof RoundedRectangle)
                g.setAntiAlias(true);
            grad.setStartColor(enabled ? borderLight : disabledColor);
            grad.setEndColor(borderDark);
            grad.setStart(0, -mid);
            grad.setEnd(0, mid); 
            g.draw(rect, grad);
        }
        
        g.setAntiAlias(oldAA);        
    }
}