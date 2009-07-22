/*
 * RecoilMove.java
 *
 * Created on January 18, 2006, 7:04 PM
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

package org.pokenet.server.battle.mechanics.moves;

import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.PokemonType;

/**
 * Creates a move that does damage to the user equal to a percentage of the
 * damage done to the opponent.
 * @author Ben
 */
public class RecoilMove extends PokemonMove {

	private double m_recoil;

	/**
	 * Creates a new instance of RecoilMove
	 */
	public RecoilMove(PokemonType type,
			int power,
			double accuracy,
			int pp,
			double recoil) {

		super(type, power, accuracy, pp);
		m_recoil = recoil;
	}

	public int getRecoil(Pokemon p, int damage) {
		int recoil = (int)(m_recoil * (double)damage);
		if (recoil < 1) recoil = 1;
		return recoil;
	}

	public boolean isProtected(Pokemon p) {
		return p.hasAbility("Rock Head");
	}

	public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
		try {
			if(user == null || mech == null)
				return 0;
			int health = target.getHealth();
			int damage = mech.calculateDamage(this, user, target);
			target.changeHealth(-damage);
			health -= target.getHealth();
			if (damage == 0)
				return 0;

			if (!isProtected(user)) {
				int recoil = getRecoil(user, health);
				if (recoil < 1) 
					recoil = 1;
				if(user.getField() != null || user.getName() != null)
					user.getField().showMessage(user.getName() + " was hit by recoil!");
				user.changeHealth(-recoil, true);
			}
			return damage;
		} catch(NullPointerException npe) {
			npe.printStackTrace();
		}
		return 0;
	}
}
