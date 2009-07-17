package org.pokenet.server.battle.mechanics;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * This class represents a stat multiplier. Such a multiplier might be instated
 * because of a move, but hold items and intrinsic abilities do not use this
 * class. They might later, but they don't now.
 * @author Colin
 */
@Root
public class StatMultiplier {
    
    /**
     * Multipliers used for statistics.
     */
    private static final double[] m_stats = new double[] {
      4.0, 3.5, 3.0, 2.5, 2.0, 1.5, 1.0, 2.0/3.0, 0.5, 0.4, 1.0/3.0, 2.0/7.0, 0.25  
    };
    
    /**
     * Multipliers used for accuracy and evasion.
     */
    private static final double[] m_acc = new double[] {
      3.0, 8.0/3.0, 7.0/3.0, 2.0, 5.0/3.0, 4.0/3.0, 1.0, 0.75, 0.6, 0.5, 3.0/7.0, 3.0/8.0, 1.0/3.0  
    };
    
    @Element
    private int m_position = 6; // Centre of the stat multipliers.
    @Element
    private double[] m_multipliers;
    @Element
    private double m_secondary = 1.0;
    
    public StatMultiplier() {
        m_multipliers = m_stats;
    }
    
    public StatMultiplier(boolean bAccuracy) {
        m_multipliers = (bAccuracy ? m_acc : m_stats);
    }
    
    public void multiplyBy(double factor) {
        m_secondary *= factor;
    }
    
    public void divideBy(double factor) {
        m_secondary /= factor;
    }
    
    public void setSecondaryMultiplier(double m) {
        m_secondary = m;
    }
    
    public double getSecondaryMultiplier() {
        return m_secondary;
    }
    
    public double getMultiplier() {
        return (m_multipliers[m_position] * m_secondary);
    }
    
    public boolean decreaseMultiplier() {
        if (m_position == (m_multipliers.length - 1)) return false;
        ++m_position;
        return true;
    }
    
    public boolean increaseMultiplier() {
        if (m_position == 0) return false;
        --m_position;
        return true;
    }
    
}
