/*
 * FlinchEffect.java
 *
 * Created on December 23, 2006, 12:12 PM
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, visit the Free Software Foundation, Inc.
 * online at http://gnu.org.
 */

package org.pokenet.server.battle.mechanics.statuses;

import org.pokenet.server.battle.Pokemon;

/**
 *
 * @author Colin
 */
public class FlinchEffect extends StatusEffect {
    
    private int m_rounds = 1;
    
    /** Creates a new instance of ConfuseEffect */
    public FlinchEffect() {
    }
    
    public String getName() {
        return "Flinch";
    }
    
    /**
     * Normally 'rounds' = 1, but hyper beam can be considered a flinch effect
     * for two rounds, since the first round it will not have any effect.
     */
    public FlinchEffect(int rounds) {
        m_rounds = rounds;
    }
    
    public boolean tick(Pokemon p) {
        if (--m_rounds == 0) {
            p.removeStatus(this);
            return true;
        }
        return false;
    }
    
    public int getTier() {
        return 0;
    }
    
    public boolean switchOut(Pokemon p) {
        return true;
    }
    
    public boolean apply(Pokemon p) {
        if (p.hasAbility("Inner Focus")) {
            return false;
        }
        return true;
    }
    
    public void unapply(Pokemon p) {
    }
    
    public String getDescription() {
        return null;
    }
    
    public boolean immobilises(Pokemon p) {
        p.getField().showMessage(p.getName() + " flinched!");
        if (p.hasAbility("Steadfast")) {
            p.addStatus(p, new StatChangeEffect(Pokemon.S_SPEED, true));
        }
        return true;
    }
    
}
