package org.pokenet.client.ui.skin;

import mdes.slick.sui.Button;
import mdes.slick.sui.CheckBox;
import mdes.slick.sui.Component;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Theme;
import mdes.slick.sui.skin.SkinUtil;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.gui.GUIContext;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetCheckBoxAppearance extends PokenetButtonAppearance {
    
    protected Image defaultImage;
    protected RoundedRectangle roundBoxBounds;
    
    private boolean drawOutline = true;
    
    public PokenetCheckBoxAppearance(CheckBox checkBox) {
        super(checkBox);
        this.roundBoxBounds = createRoundedBoxBounds();
        checkBox.setForeground(Color.white);
    }
    
    public void install(Component comp, Skin skin, Theme theme) {
        super.install(comp, skin, theme);
        if (skin instanceof PokenetSkin)
            this.defaultImage = ((PokenetSkin)skin).getCheckBoxImage();
    }
    
    //TODO: disabled colours
    
    protected RoundedRectangle createRoundedBounds() {
        return new RoundedRectangle(0f,0f,0f,0f,5f,25);
    }
    
    protected RoundedRectangle createRoundedBoxBounds() {
        return new RoundedRectangle(0f,0f,0f,0f,3f,50);
    }
    
    @SuppressWarnings("unused")
    public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
        //makes sure it's the same as what we're attached to
        checkComponent(comp);
        
        CheckBox check = (CheckBox)comp;
        Rectangle cachedRect = null;
        boolean roundRectEnabled = PokenetSkin.isRoundRectanglesEnabled();
        Rectangle r = new Rectangle(comp.getAbsoluteX(), comp.getAbsoluteY() + 2, 
        		comp.getHeight() - 3, comp.getHeight() - 2);
        
        //make sure we are showing outline
        //also, the outline will only render if we aren't rendering a background
        if (isShowOutline() && (!check.isOpaque() || check.getBackground()==null)) {
            //get the cached rectangle from the component bounds
            cachedRect = check.getAbsoluteBounds();
            Rectangle bounds = cachedRect;
            
            //if we have round rectangles, use them
            if (roundRectEnabled && roundBounds!=null) {
                roundBounds.setBounds(bounds);
                bounds = roundBounds;
            }
            
            Color oldCol = g.getColor();
            boolean oldAA = g.isAntiAlias();
			Color back;

            if (check.getState()!=Button.UP) //hover
                back = theme.getPrimary1();
            else //still
                back = theme.getPrimary3();

            g.setColor(new Color(0,0,0,0));
            g.setAntiAlias(true);
            g.fill(bounds);
            
            g.setAntiAlias(oldAA);
            g.setColor(oldCol);
        }
        

        
        //renders base
        SkinUtil.renderComponentBase(g, check);
                
        //renders text/image
        SkinUtil.renderCheckBoxBase(g, check);
        
        //get cached bounds from the "check" box button area
        Rectangle cachedBox = check.getAbsoluteBoxBounds();
        Rectangle btnRect = cachedBox;
        
        //try to use round rectangle
        if (roundRectEnabled && roundBoxBounds!=null) {
            roundBoxBounds.setBounds(cachedBox);
            btnRect = roundBoxBounds;
        }
        
     	//renders the actual button state for the small box area, using rounded edges
        PokenetButtonAppearance.renderButtonState(g, theme, check, r, grad, (PokenetSkin)skin);
        
        Image def = getDefaultImage();
        if (def!=null && check.isSelected()) {
            float x = r.getX() + (r.getWidth()/2f - def.getWidth()/2f);
            float y = r.getY() + (r.getHeight()/2f - def.getHeight()/2f);
            g.drawImage(def, (int)x, (int)y, check.getImageFilter());
        }
    }

    public Image getDefaultImage() {
        return defaultImage;
    }

    public boolean isShowOutline() {
        return drawOutline;
    }

    public void setShowOutline(boolean drawOutline) {
        this.drawOutline = drawOutline;
    }
}