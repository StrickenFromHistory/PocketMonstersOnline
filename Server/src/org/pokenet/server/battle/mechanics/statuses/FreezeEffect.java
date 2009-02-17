/*
 * FreezeEffect.java
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
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.PokemonType;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;
import org.pokenet.server.battle.mechanics.statuses.field.SunEffect;

/**
 * TODO: Account for fire moves thawing out pokemon.
 * @author Colin
 */
public class FreezeEffect extends StatusEffect {

    public FreezeEffect() {
        m_lock = SPECIAL_EFFECT_LOCK;
    }
    
    public String getName() {
        return "Freeze";
    }
    
    public boolean tick(Pokemon p) {
        return false;
    }
    
    public int getTier() {
        // Not applicable.
        return -1;
    }
    
    /**
     * A frozen pokemon is not cured by being switched out.
     */
    public boolean switchOut(Pokemon p) {
        return false;
    }
    
    /**
     * Ice pokemon cannot be frozen.
     */
    public boolean apply(Pokemon p) {
        if (p.hasAbility("Magma Armor")) {
            return false;
        }
        if (p.isType(PokemonType.T_ICE))
            return false;
        if (p.getField().getEffectByType(SunEffect.class) != null)
            return false;
        return true;
    }
    
    public void unapply(Pokemon p) {
        p.getField().showMessage(p.getName() + " was defrosted!");
    }
    
    public String getDescription() {
        return " was frozen solid!";
    }
    
    public boolean isListener() {
        return true;
    }
    
    public void executeTurn(Pokemon p, BattleTurn turn) {
        String name = turn.getMove(p).getMoveListEntry().getName();
        if (name.equals("Flame Wheel") || name.equals("Sacred Fire")) {
            p.removeStatus(this);
        }
    }
    
    public void informDamaged(Pokemon source, Pokemon target, MoveListEntry entry, int damage) {
        PokemonMove move = entry.getMove();
        if (move != null) {
            if (move.getType().equals(PokemonType.T_FIRE)) {
                target.removeStatus(this);
            }
        }
    }
    
    /**
     * Freeze immolilises the pokemon.
     */
    public boolean immobilises(Pokemon poke) {
        BattleField field = poke.getField();
        if (field.getRandom().nextDouble() <= 0.25) {
            poke.removeStatus(this);
            return false;
        }
        field.showMessage(poke.getName() + " is frozen solid!");
        return true;
    }
    
}
