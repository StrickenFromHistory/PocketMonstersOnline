/*
 * MultipleStatChangeEffect.java
 *
 * Created on January 19, 2007, 3:59 PM
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
 * Applies a set of StatChange Effects
 * @author Ben
 */
public class MultipleStatChangeEffect extends StatusEffect {
    
    private int[] m_stats;
    
    /** Creates a new instance of MultipleStatChangeEffect */
    public MultipleStatChangeEffect(int[] stats) {
        m_stats = stats;
    } 
    
    public String getName() {
        return "Statuses";
    }
    
    public boolean immobilises(Pokemon poke) {
        return false;
    }
    
    public boolean tick(Pokemon p) {
        return false;
    }
    
    public int getTier() {
        return -1;
    }
    
    public boolean switchOut(Pokemon p) {
        return false;
    }
    
    public boolean apply(Pokemon p) {
        for (int i = 0; i < m_stats.length; ++i) {
            p.addStatus(getInducer(), new StatChangeEffect(m_stats[i], true, 1));
        }
        return false;
    }
    
    public void unapply(Pokemon p) {
    }
    
    public String getDescription() {
        return null;
    }
}