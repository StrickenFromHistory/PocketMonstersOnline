/*
 * WeatherEffect.java
 *
 * Created on January 6, 2007, 5:49 PM
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

package org.pokenet.server.battle.mechanics.statuses.field;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.PokemonType;

/**
 *
 * @author Colin
 */
public abstract class WeatherEffect extends FieldEffect {
    
    private int m_turns = 5;
    /** Stores types for Forecast
     *  Note: The same effect is applied to all pokemon on the field! You cannot
     *        do this naively with just a single array!
     */
    private PokemonType[][] m_types = new PokemonType[2][];
    protected boolean[] m_applied = new boolean[2];
    
    /**
     * Initialise the number of turns that this effect will remain active
     * for before it fades.
     */
    public WeatherEffect(int turns) {
        m_lock = WEATHER_EFFECT_LOCK;
        m_turns = turns;
    }
    
    /**
     * Handle the number of turns that this effect will last for. This method
     * actually deactivatves the effect if it is time for it to fade.
     * Subclasses should return super.tickField()!
     * @return whether the weather effect faded
     */
    protected final boolean tickField(BattleField field) {
        if ((m_turns != -1) && (--m_turns == 0)) {
            // Time to fade.
            field.removeEffect(this);
            return true;
        }
        tickWeather(field);
        return false;
    }
    
    /**
     * Tick the weather on this field.
     */
    protected abstract void tickWeather(BattleField field);
    
    /**
     * Weather effects are currently all in the third tier (zero is the first
     * tier, so returning two is indeed the third tier).
     */
    public int getTier() {
        return 2;
    }
    
    /**
     * Weather effects are move transformers.
     */
    public boolean isMoveTransformer(boolean enemy) {
        return !enemy;
    }
    
    /**
     * Whether the weather should have effects.
     */
    protected boolean hasEffects(BattleField field) {
        Pokemon[] pokemon = field.getActivePokemon();
        for (int i = 0; i < pokemon.length; ++i) {
            Pokemon p = pokemon[i];
            if (p.hasAbility("Air Lock") || p.hasAbility("Cloud Nine"))
                return false;
        }
        return true;
    }
    
    /**
     * Changes the types of a Pokemon (for Forecast).
     * @param apply Whether the types are being applied or unapplied.
     */
    protected void setTypes(Pokemon p, PokemonType[] types, boolean apply) {
        if (!p.hasAbility("Forecast")
                || !p.getOriginalAbility().getName().equals("Forecast")) return;
        int party = p.getParty();
        if (apply) {
            m_types[party] = p.getTypes();
            p.setType(types);
            // Assume only changing to a single type.
            p.getField().showMessage(p.getName() + " became the " + types[0] + " type!");
        } else if (m_types[party] != null) {
            p.setType(m_types[party]);
            m_types[party] = null;
        }
    }
    
}
