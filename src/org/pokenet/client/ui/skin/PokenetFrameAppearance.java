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

    
    public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
        Color old = g.getColor();
        PokenetSkin s = (PokenetSkin) skin;
        
        //borders
        if (comp.isBorderRendered()) {
            Frame win = (Frame)comp;
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

//            float mid = rect.getWidth()/2f;
//
//            grad.setStartColor(topInnerColor);
//            grad.setEndColor(bottomInnterColor);
//            grad.setStart(-mid, 0);
//            grad.setEnd(mid, 0);
//            g.draw(rect, grad);
            g.drawImage(s.lM_frame, 
            		win.getAbsoluteX(), 
            		win.getAbsoluteY() + win.getTitleBar().getHeight(),
            		win.getAbsoluteX() + s.lM_frame.getWidth(),
            		win.getAbsoluteY() + win.getHeight(),
            		0,0,
            		s.lM_frame.getWidth(),
            		s.lM_frame.getHeight());
            g.drawImage(s.rM_frame, 
            		win.getAbsoluteX() + win.getWidth() - s.rM_frame.getWidth(),
            		win.getAbsoluteY() + win.getTitleBar().getHeight(),
            		win.getAbsoluteX() + win.getWidth(),
            		win.getAbsoluteY() + win.getHeight(),
            		0,0,
            		s.rM_frame.getWidth(),
            		s.rM_frame.getHeight());
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
            return new RoundedRectangle(0f,0f,0f,0f,3f,50);
        }

        public void install(Component comp, Skin skin, Theme theme) {
            super.install(comp, skin, theme);
            Button btn = (Button)comp;
            
            if (skin instanceof PokenetSkin) {
                Image img = ((PokenetSkin)skin).getCloseButtonImage();
                if (SkinUtil.installImage(btn, img)) {
                    btn.pack();
                }
            }
        }
    }
    
    protected class ResizerAppearance extends PokenetLabelAppearance {
            
        public void install(Component comp, Skin skin, Theme theme) {
            super.install(comp, skin, theme);
            if (skin instanceof PokenetSkin) {
                comp.addMouseListener(((PokenetSkin)skin).getResizeCursorListener());
            }
        }

        public void uninstall(Component comp, Skin skin, Theme theme) {
            super.uninstall(comp, skin, theme);
            if (skin instanceof PokenetSkin) {
                comp.removeMouseListener(((PokenetSkin)skin).getResizeCursorListener());
            }
        }
        
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
//			bar.setHeight(m_topLeft.getHeight());

            this.bar = bar;

        }
        
        public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
            Rectangle rect = comp.getAbsoluteBounds();
            PokenetSkin s = (PokenetSkin)skin;

            Color old = g.getColor();
            Frame.TitleBar t = (Frame.TitleBar)comp;
            
            float frameTopLeftX = t.getAbsoluteX();
            float frameTopLeftY = t.getAbsoluteY();
            float width=t.getWidth(), height=t.getHeight();

            //TODO: fix rectangle + 1

//            float mid = width/2.0f;

//            Color start, end;

//            boolean active = ((Frame)t.getParent()).isActive();

//            if (active) {
//                start = theme.getActiveTitleBar1();
//                end = theme.getActiveTitleBar2();
//            } else {
//                start = theme.getTitleBar1();
//                end = theme.getTitleBar2();
//            }
//
//            grad.setStartColor(start);
//            grad.setEndColor(end);
//            grad.setStart(-mid, 0);
//            grad.setEnd(mid, 0);
//            g.fill(rect, grad);
            
            // draw the top title bar thing.
            g.drawImage(s.tL_frame, frameTopLeftX, 
            		frameTopLeftY);
            
            g.drawImage(s.tR_frame, frameTopLeftX + t.getWidth() - s.tR_frame.getWidth(), 
            		frameTopLeftY);
            
            g.drawImage(s.tM_frame, 
            		frameTopLeftX + s.tL_frame.getWidth(), 
            		frameTopLeftY, 
            		frameTopLeftX + width - s.tL_frame.getWidth(),
            		frameTopLeftY + s.tM_frame.getHeight(),
            		0,0,1, 52);
          
            
           

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
