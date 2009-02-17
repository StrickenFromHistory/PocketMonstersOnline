/*
 * SleepEffect.java
 *
 * Created on December 23, 2006, 12:11 PM
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
import org.pokenet.server.battle.mechanics.moves.MoveList.FixedAttackEffect;

/**
 *
 * @author Colin
 */
public class SleepEffect extends StatusEffect {

    private int m_turns = 0;
    
    /** Creates a new instance of SleepEffect */
    public SleepEffect() {
        m_lock = SPECIAL_EFFECT_LOCK;
        m_turns = 0;
    }
    
    public SleepEffect(int turns) {
        m_lock = SPECIAL_EFFECT_LOCK;
        m_turns = turns;
    }
    
    public String getName() {
        return "Sleep";
    }
    
    public boolean tick(Pokemon p) {
        return false;
    }
    
    public int getTier() {
        return 0;
    }
    
    public boolean switchOut(Pokemon p) {
        return false;
    }
    
    public boolean apply(Pokemon p) {    
        if (p.hasAbility("Insomnia") || p.hasAbility("Vital Spirit")) {
            return false;
        }
        if (p.hasEffect(FixedAttackEffect.class)) {
            StatusEffect effect = p.getEffect(FixedAttackEffect.class);
            if (effect.getName().equals("Uproar")) {
                p.getField().showMessage("But it failed!");
                return false;
            }
        }
        if (m_turns == 0) {
            m_turns = p.getField().getRandom().nextInt(4) + 2;
        }
        if (p.hasAbility("Early Bird")) {
            m_turns = m_turns / 2 + 1;
        }
        return true;
    }
    
    public void unapply(Pokemon p) {
    }
    
    public String getDescription() {
        return " fell asleep!";
    }
    
    /**
     * Sleep immolilises the pokemon.
     */
    public boolean immobilises(Pokemon poke) {
        if (--m_turns <= 0) {
            poke.removeStatus(this);
            poke.getField().showMessage(poke.getName() + " woke up!");
            return false;
        }
        poke.getField().showMessage(poke.getName() + " is fast asleep!");
        return true;
    }
}
