package org.pokenet.server.battle.mechanics.intelligence;

import java.io.Serializable;

import org.pokenet.server.battle.mechanics.PokemonType;

/**
 *
 * @author Colin
 */
@SuppressWarnings("serial")
public class Memory implements Serializable {
    
    protected PokemonType m_me, m_opponent;
    protected boolean m_low;
    protected int m_score = 1;
    protected String m_move = null;
    
    /**
     * @param me my type
     * @param opponent the type of my opponent
     * @param low whether my health is low (as defined by my own rules)
     */
    public Memory(PokemonType me, PokemonType opponent, boolean low, String move) {
        m_me = me;
        m_opponent = opponent;
        m_low = low;
        m_move = move;
    }
    
    /**
     * Return whether two memories are semantically equal.
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!obj.getClass().getName().equals(getClass().getName())) {
            return false;
        }
        Memory mem = (Memory)obj;
        
        if ((mem.m_me == null) || (mem.m_move == null) || (mem.m_opponent == null)) {
            return false;
        }
        
        return (mem.m_me.equals(m_me)
            && mem.m_opponent.equals(m_opponent)
            && (mem.m_low == m_low)
            && (mem.m_move.equals(m_move)));
    }
}
