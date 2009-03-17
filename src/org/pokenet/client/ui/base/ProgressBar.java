package org.pokenet.client.ui.base;

import mdes.slick.sui.Label;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.GUIContext;

/**
 * ProgressBar used to show the health of pokemon in battle
 * @author ZombieBear
 *
 */
public class ProgressBar extends Label {
        private int m_value;
        private int m_minVal;
        private int m_maxVal;

        /**
         * Default constructor
         * @param min
         * @param max
         */
        public ProgressBar(int m_min, int m_max){
                super();
                m_value = m_maxVal;
                m_minVal = m_min;
                m_maxVal = m_max;
                setOpaque(true);
        }
        
        /**
         * Sets text to be displayed inside the progress bar
         * @param string
         */
        public void setString(String string) {
                setText(string);
        }
        
        /**
         * Returns the text held in the progress bar
         * @return
         */
        public String getString() {
                return getText();
        }
        
        /**
         * Returns a double showing the percentage of completion
         * @return
         */
        public double getPercentComplete() {
                double percent = (getValue() - getMinimum()) /
                                                (getMaximum() - getMinimum());
                return percent;
        }

        /**
         * Returns minimum value
         * @return
         */
        public int getMinimum() {
                return m_minVal;
        }
        
        /**
         * Sets the minimum value
         * @param m_minVal
         */
        public void setMinimum(int m_minVal) {
                this.m_minVal = m_minVal;
        }
        
        /**
         * Returns the maximum value
         * @return
         */
        public int getMaximum() {
                return m_maxVal;
        }
        
        /**
         * Sets the maximum value
         * @param m_maxVal
         */
        public void setMaximum(int m_maxVal) {
                this.m_maxVal = m_maxVal;
        }
        
        /**
         * Sets the current value
         * @param m_curVal
         */
        public void setValue(int m_curVal){
                m_value = m_curVal;
        }
        
        /**
         * Returns the current value
         * @return
         */
        public int getValue() {
                return m_value;
        }
        
        /**
         * Renders the progress bar
         */
        @Override
        public void render(GUIContext ctx, Graphics g) {
                super.render(ctx, g);
                g.setColor(getForeground());
                int barWidth = (int)(m_value * (getWidth() / (m_maxVal - m_minVal)));
                g.fillRect(getAbsoluteX(), getAbsoluteY(),
                                (barWidth > getWidth() ? getWidth() : barWidth),
                                getHeight() - 1);
                if (getText() != null && !getText().equals(""))
                        g.drawString(getText(), getAbsoluteX() +
                                        ((getWidth() / 2) -
                                                        (getFont().getWidth(getText()) / 2)),
                                        getAbsoluteY() +
                                                        ((getHeight() / 2) -
                                                                        (getFont().getHeight(getText()) / 2)));
        }
}

