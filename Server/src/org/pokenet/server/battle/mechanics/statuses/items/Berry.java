/*
 * HoldItem.java
 *
 * Created on July 27, 2007, 9:27:18 PM
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

package org.pokenet.server.battle.mechanics.statuses.items;
import org.pokenet.server.battle.Pokemon;

/**
 * An interface representing a berry item.
 * @author ben
 */
public interface Berry {

    /**
     * Execute the effects of the berry
     */
    public void executeEffects(Pokemon p);
}
