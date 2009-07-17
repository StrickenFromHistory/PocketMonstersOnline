package org.pokenet.server.battle.mechanics.statuses;

import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;

/*
 * ChargeEffect.java
 *
 * Created on January 10, 2007, 2:59 PM
 *
 * This file is a part of Shoddy Battle.
 * Copyright (C) 2006  Colin Fitzpatrick
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */


/**
 *
 * @author Colin
 */
public class ChargeEffect extends StatusEffect {
    
    private int m_turns = 0;
    private MoveListEntry m_move = null;
    private String m_msg = null;
    
    /**
     * Initialise a new charge effect.
     * @param turns the number of turns to charge for
     * @param msg message to display when effect is applied
     * @param move move to use after charge is complete
     */
    public ChargeEffect(int turns, String msg, MoveListEntry move) {
        m_turns = turns;
        m_move = move;
        m_msg = msg;
    }
    
    public String getName() {
        return "Charge";
    }
    
    public void setTurns(int turns) {
        m_turns = turns;
    }
    
    public boolean isMoveTransformer(boolean enemy) {
        return !enemy;
    }
    
    public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
        if (m_turns == 0) {
            p.removeStatus(this);
            return m_move;            
        }
        p.getField().showMessage(p.getName() + " " + m_msg);
        return null;
    }
    
    public boolean apply(Pokemon p) {
        if (m_turns != 0) {
            p.getField().showMessage(p.getName() + " " + m_msg);
            return true;
        }
        p.useMove(m_move.getMove(), p.getOpponent());
        return false;
    }
    
    public void unapply(Pokemon p) {
        
    }
    
    public boolean canSwitch(Pokemon p) {
        return false;
    }
    
    public int getTier() {
        // Tier does not really matter.
        return 0;
    }
    
    public boolean tick(Pokemon p) {
        --m_turns;
        return false;
    }
    
    public String getDescription() {
        return null;
    }
    
    /**
     * Return the move that will be used after the charge finishes.
     * @return the move
     */
    public MoveListEntry getMove() {
        return m_move;
    }
}
