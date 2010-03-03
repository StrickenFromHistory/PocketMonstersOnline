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
    
    private Image topLeft, topMiddle, topRight;
    private Image leftMiddle, center, rightMiddle;
    private Image bottomLet, bottomMiddle, bottomRight;
    
    private ComponentAppearance resizerAppearance = new ResizerAppearance();
    
    public PokenetFrameAppearance(){
    	//set the frame images
        try {
        	leftMiddle = new ImageUIResource("res/ui/skin/leftMiddleFrame.png");
        	rightMiddle = new ImageUIResource("res/ui/skin/rightMiddleFrame.png");
            leftMiddle.setAlpha(1);
            rightMiddle.setAlpha(1);
            System.out.println("constructed");
		}
		catch (SlickException exception) {
			// if this file isn'tn found.. sad face
			System.err.println("Required GUI files not found...");
		}
    }
    
    public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
        Color old = g.getColor();
        
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
//            if (!win.getTitleBar().isVisible() || !win.containsChild(win.getTitleBar())) {
//                float h = win.getTitleBar().getHeight();
//                rect.setY(rect.getY()+h-1);
//                rect.setHeight(rect.getHeight()-h+1);
//            }

//            float mid = rect.getWidth()/2f;
//
//            grad.setStartColor(topInnerColor);
//            grad.setEndColor(bottomInnterColor);
//            grad.setStart(-mid, 0);
//            grad.setEnd(mid, 0);
//            g.draw(rect, grad);
      
            g.drawImage(leftMiddle, win.getAbsoluteX(), win.getAbsoluteY() + win.getTitleBar().getHeight());
            g.drawImage(rightMiddle, win.getAbsoluteX() + win.getWidth() - rightMiddle.getWidth(),
            		win.getAbsoluteY() + win.getTitleBar().getHeight());
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
        
        // images
        private Image m_topLeft, m_topMiddle, m_topRight;

        
        public TitleBarAppearance(Frame.TitleBar bar) {
            
            //set the frame images
            try {
            	PokenetFrameAppearance.this.topLeft = new ImageUIResource("res/ui/skin/topLeftFrame.png");
            	PokenetFrameAppearance.this.topMiddle = new ImageUIResource("res/ui/skin/topMiddleFrame.png");
            	PokenetFrameAppearance.this.topRight = new ImageUIResource("res/ui/skin/topRightFrame.png");
				m_topLeft = PokenetFrameAppearance.this.topLeft;
				m_topMiddle = PokenetFrameAppearance.this.topMiddle;
				m_topRight = PokenetFrameAppearance.this.topRight;
            	
            	m_topLeft.setAlpha(1);
				m_topMiddle.setAlpha(1);
				m_topRight.setAlpha(1);
			}
			catch (SlickException exception) {
				// if this file isn'tn found.. sad face
				System.err.println("Required GUI files not found...");
			}
			
			bar.setHeight(m_topLeft.getHeight());

            this.bar = bar;

        }
        
        public void render(GUIContext ctx, Graphics g, Component comp, Skin skin, Theme theme) {
            Rectangle rect = comp.getAbsoluteBounds();

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
            g.drawImage(m_topLeft, frameTopLeftX, 
            		frameTopLeftY);
            
            g.drawImage(m_topRight, frameTopLeftX + t.getWidth() - m_topRight.getWidth(), 
            		frameTopLeftY);
            
            g.drawImage(m_topMiddle, frameTopLeftX + m_topLeft.getWidth(), 
            		frameTopLeftY, 
            		frameTopLeftX + width - m_topLeft.getWidth(),
            		frameTopLeftY + m_topMiddle.getHeight(),
            		0,0,m_topMiddle.getWidth(), m_topMiddle.getHeight());
            
           

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
