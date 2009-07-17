/*
 Pokemon Global. A Pokemon MMO based on the series of games made by Nintendo.
 Copyright � 2007-2008 Pokemon Global Team

 This file is part of Pokemon Global.

 Pokemon Global is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Pokemon Global is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Pokemon Global.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.pokenet.server.backend.entity;

import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.Pokemon;
import org.simpleframework.xml.Root;

/**
 * Provides an interface for all game objects that can be battled
 * @author shadowkanji
 *
 */
@Root
public interface Battleable {
	public boolean isBattling();

	public String getName();
	public Pokemon[] getParty();
	public int getBattleId();
	
	public Battleable getOpponent();
	public BattleField getBattleField();
	public void setBattleField(BattleField b);
	public void setParty(Pokemon[] team);
	public void setBattleId(int battleID);
}
