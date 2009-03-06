package org.pokenet.client.ui;

import mdes.slick.sui.Theme;
import mdes.slick.sui.skin.ColorUIResource;

import org.newdawn.slick.Color;
/**
*
* @author TMKCodes
*/
public class PokenetTheme implements Theme {
   
   private Color buttonBase2 = new ColorUIResource(0, 0, 0, 90);  
   private Color lightTop2 = new ColorUIResource(255, 51, 0);    
   private Color lightBottom2 = new ColorUIResource(255, 51, 0); 
   
   private Color buttonBase = new ColorUIResource(255, 0, 0); 
   private Color lightTop = new ColorUIResource(255, 51, 0); 
   private Color lightBottom = new ColorUIResource(255, 51, 0);
   
   private Color borderDark = new ColorUIResource(245, 122, 0); 
   private Color borderLight = new ColorUIResource(64, 66, 70);
   private Color background = new ColorUIResource(0, 0, 0, 90);
   
   private Color winBorderLight = new ColorUIResource(245, 46, 0);
   private Color winBorderDark = new ColorUIResource(240, 46, 0);
   private Color winTitleStart = new ColorUIResource(255, 51, 51);
   private Color winTitleEnd = new ColorUIResource(255, 51, 51);
   
   private Color foreground = new ColorUIResource(255, 255, 255); 
   private Color disabledMask = new ColorUIResource(.55f, .55f, .55f, .5f);
   
   public String getName() {
       return "Red Theme";
   }
       
   public Color getPrimaryBorder1() {
       return borderLight;
   }
   
   public Color getPrimaryBorder2() {
       return borderDark;
   }
   
   public Color getPrimary1() {
       return buttonBase;
   }
   
   public Color getPrimary2() {
       return lightTop;
   }
   
   public Color getPrimary3() {
       return lightBottom;
   }
   
   public Color getSecondary1() {
       return buttonBase2;
   }
   
   public Color getSecondary2() {
       return lightTop2;
   }
   
   public Color getSecondary3() {
       return lightBottom2;
   }
   
   public Color getBackground() {
       return background;
   }
   
   public Color getForeground() {
       return foreground;
   }

   public Color getSecondaryBorder1() {
       return winBorderLight;
   }

   public Color getSecondaryBorder2() {
       return winBorderDark;
   }

   public Color getTitleBar1() {
       return winTitleStart;
   }

   public Color getTitleBar2() {
       return winTitleEnd;
   }
   
   public Color getActiveTitleBar1() {
       return buttonBase2;
   }
   
   public Color getActiveTitleBar2() {
       return winTitleEnd;
   }
   
   public Color getDisabledMask() {
       return disabledMask;
   }
}
