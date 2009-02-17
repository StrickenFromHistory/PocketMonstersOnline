package org.pokenet.server.battle.mechanics.statuses;

import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.PokemonType;

/*
 * BurnEffect.java
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


/**
 *
 * @author Colin
 */
public class BurnEffect extends StatusEffect {
    
    /** Creates a new instance of BurnEffect */
    public BurnEffect() {
        m_lock = SPECIAL_EFFECT_LOCK;
    }
    
    public String getName() {
        return "Burn";
    }
    
    /**
     * Burn stays through switching out.
     */
    public boolean switchOut(Pokemon p) {
        return false;
    }
    
    /**
     * Burn removes 1/8 max health each round.
     */
    public boolean tick(Pokemon p) {
        if (p.hasAbility("Water Veil")) {
            return false;
        }
        double maximum = (double)p.getStat(Pokemon.S_HP);
        double loss = maximum / 8.0;
        int floor = (int)loss;
        if (floor < 1) {
            floor = 1;
        }
        p.getField().showMessage(p.getName() + " was hurt by its burn!");
        p.changeHealth(-floor, true);
        return false;
    }
    
    /**
     * Burn is in the fourth tier.
     */
    public int getTier() {
        return 3;
    }
    
    /**
     * Note that fire pokemon are immune to burn.
     */
    public boolean apply(Pokemon p) {    
        PokemonType[] type = p.getTypes();
        for (int i = 0; i < type.length; ++i) {
            if (type[i].equals(PokemonType.T_FIRE)) {
                return false;
            }
        }
        if (!p.hasAbility("Guts")) {
            p.getMultiplier(Pokemon.S_ATTACK).divideBy(2.0);
        }
        return true;
    }
    
    public void unapply(Pokemon p) {
        if (!p.hasAbility("Guts")) {
            p.getMultiplier(Pokemon.S_ATTACK).multiplyBy(2.0);
        }
    }
    
    public String getDescription() {
        return " was burned!";
    }
    
    /**
     * Burn does not immobilise.
     */
    public boolean immobilises(Pokemon poke) {
        return false;
    }
    
}
