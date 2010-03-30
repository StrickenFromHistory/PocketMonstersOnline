package org.pokenet.client.ui.skin;

import mdes.slick.sui.Button;
import mdes.slick.sui.Component;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.Skin;
import mdes.slick.sui.Sui;
import mdes.slick.sui.Theme;
import mdes.slick.sui.Frame.CloseButton;
import mdes.slick.sui.skin.ComponentAppearance;
import mdes.slick.sui.skin.FrameAppearance;
import mdes.slick.sui.skin.SkinUtil;
import mdes.slick.sui.skin.ImageUIResource;



import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.opengl.SlickCallable;

import com.sun.org.apache.bcel.internal.generic.LMUL;

/**
 * How the frame looks
 *
 * @author lprestonsegoiii.
 *         Created Mar 2, 2010.
 */
public class PokenetFrameAppearance extends PokenetContainerAppearance implements FrameAppearance {
        
    private static GradientFill grad = new GradientFill(0f,0f,Color.white,0f,0f,Color.white);
    private static Color topInnerColor = new Color(96, 144, 191);
    private static Color bottomInnterColor = new Color(206, 231, 250);    

    
    private ComponentAppearance resizerAppearance = new ResizerAppearance();
    Frame f;
  
    
    @SuppressWarnings("static-access")
	@Override
	public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
        Color old = g.getColor();
        PokenetSkin s = (PokenetSkin) skin;
        float left, right, top, bottom;
        
        
        //borders
        if (comp.isBorderRendered()) {
            Frame win = (Frame)comp;
            f = win;
            win.setPadding(4);
//            Color light = theme.getSecondaryBorder1();
//            Color dark = theme.getSecondaryBorder1();
//            win.
            Rectangle rect = win.getAbsoluteBounds();
            //TODO: this is a hack, fix it
            //HACK: fix window title bar (removed) hack
            if (!win.getTitleBar().isVisible() || !win.containsChild(win.getTitleBar())) {
                float h = win.getTitleBar().getHeight();
                rect.setY(rect.getY()+h-1);
                rect.setHeight(rect.getHeight()-h+1);
            }
            
            
            if(!win.isActive()){
            	s.lM_frame.setAlpha(.70f);
            	s.rM_frame.setAlpha(.70f);
            	s.tL_frame.setAlpha(.70f);
            	s.tM_frame.setAlpha(.70f);
            	s.tM_frame.setColor(0, 50, 50, 50, 1f);
            	s.tR_frame.setAlpha(.70f);
            	s.bL_frame.setAlpha(.70f);
            	s.bR_frame.setAlpha(.70f);
            	s.bM_frame.setAlpha(.70f);
            }else{
            	s.lM_frame.setAlpha(1f);
            	s.rM_frame.setAlpha(1f);
            	s.tL_frame.setAlpha(1f);
            	s.tM_frame.setAlpha(1f);
            	s.tR_frame.setAlpha(1f);
            	s.bL_frame.setAlpha(1f);
            	s.bR_frame.setAlpha(1f);
            	s.bM_frame.setAlpha(1f);            }

//            float mid = rect.getWidth()/2f;
//
//            grad.setStartColor(topInnerColor);
//            grad.setEndColor(bottomInnterColor);
//            grad.setStart(-mid, 0);
//            grad.setEnd(mid, 0);
//            g.draw(rect, grad);
            
            if(comp.isBorderRendered()){
            	 // set some vars we'll need to make the following cleaner
                left = win.getAbsoluteX() - s.lM_frame.getWidth() / 2;
                right = left + win.getWidth();
                bottom = win.getAbsoluteY() + win.getHeight() - s.bM_frame.getHeight() / 2;
                top = win.getAbsoluteY() + win.getTitleBar().getHeight() + 6; // hack
                
                // draw sides
                g.drawImage(s.lM_frame, left, top,
                		left + s.lM_frame.getWidth(),
                		bottom,
                		0, 1, s.lM_frame.getWidth(), s.lM_frame.getHeight() - 1);
                g.drawImage(s.rM_frame, left + win.getWidth(), top,
                		left + win.getWidth() + s.rM_frame.getWidth(),
                		bottom,
                		0, 1, s.rM_frame.getWidth(), s.rM_frame.getHeight() - 1);
                
                // draw the bottom
                g.drawImage(s.bL_frame, left, bottom);
                g.drawImage(s.bR_frame, left + win.getWidth(), bottom);
                g.drawImage(s.bM_frame,
                		left + s.bL_frame.getWidth(),
                		bottom,
                		left + win.getWidth(),
                		bottom + s.bM_frame.getHeight(),
                		0,0,s.bM_button.getWidth(), s.bM_frame.getHeight());
                
                if(win.getTitleBar().isVisible()){
                	// draw the top
                    g.drawImage(s.tL_frame, left,  top - s.tL_frame.getHeight());
                    g.drawImage(s.tR_frame, right,  top - s.tL_frame.getHeight());
                    g.drawImage(s.tM_frame, 
                    		left + s.tL_frame.getWidth(), top - s.tL_frame.getHeight(), 
                    		right, top,
                    		0,0,s.tM_frame.getWidth(), s.tM_frame.getHeight()); 	
                }
                
            }
           
        }
    }

    public ComponentAppearance getCloseButtonAppearance(Button closeButton) {
        return new CloseButtonAppearance(closeButton);
    }

    public ComponentAppearance getTitleBarAppearance(Frame.TitleBar titleBar) {
        return new TitleBarAppearance(titleBar);
    }

    public ComponentAppearance getResizerAppearance(Frame.Resizer resizer) {
        return resizerAppearance;
    }
    
    protected class CloseButtonAppearance extends PokenetButtonAppearance {
        
        public CloseButtonAppearance(Button button) {
            super(button);
        }
        
        @Override
		protected RoundedRectangle createRoundedBounds() {
            return new RoundedRectangle(0f,0f,24f,24f,3f);
        }

        @Override
		public void install(Component comp, Skin skin, Theme theme) {
            super.install(comp, skin, theme);
            Button btn = (Button)comp;

            if (skin instanceof PokenetSkin) {
                Image img = ((PokenetSkin)skin).getCloseButtonImage();
                if (SkinUtil.installImage(btn, img)) {
//                    btn.pack();
                }
            }
        }
    }
    
    protected class ResizerAppearance extends PokenetLabelAppearance {
            
        @Override
		public void install(Component comp, Skin skin, Theme theme) {
            super.install(comp, skin, theme);
            if (skin instanceof PokenetSkin) {
                comp.addMouseListener(((PokenetSkin)skin).getResizeCursorListener());
            }
        }

        @Override
		public void uninstall(Component comp, Skin skin, Theme theme) {
            super.uninstall(comp, skin, theme);
            if (skin instanceof PokenetSkin) {
                comp.removeMouseListener(((PokenetSkin)skin).getResizeCursorListener());
            }
        }
        
        @Override
		public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
            Frame win = ((Frame.Resizer)comp).getWindow();
            if (!win.isResizable())
                return;

            if (((Label)comp).getImage()==null) {
                SlickCallable.enterSafeBlock();
                Color t = Sui.getTheme().getSecondaryBorder1();

                //bind texture & color before entering gl
                t.bind();

                float x = comp.getAbsoluteX()-2 , y = comp.getAbsoluteY()-2;
                float w = comp.getWidth(), h = comp.getHeight();

                //begin drawing the triangle
                GL11.glBegin(GL11.GL_TRIANGLES);
                    GL11.glVertex3f(x+w, y, 0);
                    GL11.glVertex3f(x+w, y+h, 0);
                    GL11.glVertex3f(x, y+h, 0);
                GL11.glEnd();
                SlickCallable.leaveSafeBlock();
            } else {
                super.render(ctx, g, comp, skin, theme);
            }
        }
    }
    
    protected class TitleBarAppearance extends PokenetLabelAppearance {

        private Frame.TitleBar bar;
        
        public TitleBarAppearance(Frame.TitleBar bar) {
            this.bar = bar;
//            this.bar.setWidth(this.bar.getWidth() + PokenetSkin.tL_frame.getWidth());
            // readjust for the difference in the title bar height with
            // the image for the title bar
            // doesn't work
//            this.bar.setY(this.bar.getAbsoluteY() - Math.abs(PokenetSkin.this.tM_frame.getHeight() - this.bar.getHeight()));

            
        }
       
        @SuppressWarnings("static-access")
		@Override
		public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
            Rectangle rect = comp.getAbsoluteBounds();
            PokenetSkin s = (PokenetSkin)skin;
            float left, right, top, bottom;
            Color old = g.getColor();
            Frame.TitleBar t = (Frame.TitleBar)comp;
            t.setForeground(Color.white);


            //TODO: fix rectangle + 1

//            float mid = width/2.0f;

//            Color start, end;

            boolean active = ((Frame)t.getParent()).isActive();


//            grad.setStartColor(start);
//            grad.setEndColor(end);
//            grad.setStart(-mid, 0);
//            grad.setEnd(mid, 0);
//            g.fill(rect, grad);
//            if(f != null){
//            	// some vars
//                left = f.getAbsoluteX() - s.tL_frame.getWidth() / 2; // TODO hack, fix later
//                right = left + f.getWidth() - s.tR_frame.getWidth() / 2;
//                top = f.getAbsoluteY() - 2;//s.tM_frame.getHeight() - 2;
//                bottom = top + s.tM_frame.getHeight();
//
//                // draw the top title bar thing.
//                g.drawImage(s.tL_frame, left,  top);
//                g.drawImage(s.tR_frame, right,  top);
//                g.drawImage(s.tM_frame, 
//                		left + s.tL_frame.getWidth(), top, 
//                		right, bottom,
//                		0,0,s.tM_frame.getWidth(), s.tM_frame.getHeight());
//            }
//            
          
//            
//            if (active) {
//          } else {
//        	  // draw dark overlay
//        	 
//          }
////

//            if (t.isBorderRendered()) {
//                //borders
//                Color light = theme.getSecondaryBorder1();
//                Color dark = theme.getSecondaryBorder1();
//
//                grad.setStartColor(light);
//                grad.setEndColor(dark);
//                grad.setStart(-mid, 0);
//                grad.setEnd(mid, 0);
//                g.draw(rect, grad);
//            }
            
            //g.setColor(old);
            
            SkinUtil.renderLabelBase(g, t);
        }
    }
}
