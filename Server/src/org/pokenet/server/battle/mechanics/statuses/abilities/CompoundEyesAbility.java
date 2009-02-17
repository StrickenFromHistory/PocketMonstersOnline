/*
 * CompoundEyesAbility.java
 *
 * Created on January 6, 2007, 6:28 PM
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

package org.pokenet.server.battle.mechanics.statuses.abilities;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;

/**
 *
 * @author Colin
 */
public class CompoundEyesAbility extends IntrinsicAbility {
    
    public CompoundEyesAbility() {
        super("Compoundeyes");
    }
    
    public boolean isMoveTransformer(boolean enemy) {
        return !enemy;
    }
    
    /**
     * Compound Eyes transforms moves by giving them 30% higher accuracy.
     */
    public MoveListEntry getTransformedMove(Pokemon poke, MoveListEntry entry) {
        PokemonMove move = entry.getMove();
        move.setAccuracy(move.getAccuracy() * 1.3);
        return entry;
    }
}
