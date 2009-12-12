/*
 * MoveList.java
 *
 * Created on December 16, 2006, 1:51 PM
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
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.JewelMechanics;
import org.pokenet.server.battle.mechanics.PokemonType;
import org.pokenet.server.battle.mechanics.StatMultiplier;
import org.pokenet.server.battle.mechanics.statuses.BurnEffect;
import org.pokenet.server.battle.mechanics.statuses.ChargeEffect;
import org.pokenet.server.battle.mechanics.statuses.ConfuseEffect;
import org.pokenet.server.battle.mechanics.statuses.FlinchEffect;
import org.pokenet.server.battle.mechanics.statuses.FreezeEffect;
import org.pokenet.server.battle.mechanics.statuses.MultipleStatChangeEffect;
import org.pokenet.server.battle.mechanics.statuses.ParalysisEffect;
import org.pokenet.server.battle.mechanics.statuses.PercentEffect;
import org.pokenet.server.battle.mechanics.statuses.PoisonEffect;
import org.pokenet.server.battle.mechanics.statuses.SleepEffect;
import org.pokenet.server.battle.mechanics.statuses.StatChangeEffect;
import org.pokenet.server.battle.mechanics.statuses.StatusEffect;
import org.pokenet.server.battle.mechanics.statuses.ToxicEffect;
import org.pokenet.server.battle.mechanics.statuses.abilities.IntrinsicAbility;
import org.pokenet.server.battle.mechanics.statuses.field.FieldEffect;
import org.pokenet.server.battle.mechanics.statuses.field.HailEffect;
import org.pokenet.server.battle.mechanics.statuses.field.RainEffect;
import org.pokenet.server.battle.mechanics.statuses.field.SandstormEffect;
import org.pokenet.server.battle.mechanics.statuses.field.SunEffect;
import org.pokenet.server.battle.mechanics.statuses.items.Berry;
import org.pokenet.server.battle.mechanics.statuses.items.ChoiceBandItem;
import org.pokenet.server.battle.mechanics.statuses.items.HoldItem;


/**
 * This class contains static methods for maintaining the server's list of
 * moves that pokemon can have.
 * @author Ben
 * @author Colin
 */
public class MoveList {

	private static MoveList m_inst = new MoveList(true);
	private ArrayList<MoveListEntry> m_moves;

	/**
	 * Get the default MoveList.
	 */
	public static MoveList getDefaultData() {
		return m_inst;
	}

	public boolean containsMove(String moveName) {
		for(int i = 0; i < m_moves.size() / 2; i++) {
			if(m_moves.get(i).getName().equalsIgnoreCase(moveName))
				return true;
			else if(m_moves.get(m_moves.size() - 1 - i).getName().equalsIgnoreCase(moveName))
				return true;
		}
		return false;
	}

	/**
	 * Initialise all moves that do not have a special effect.
	 */
	private void initNonStatusMoves() {
		m_moves.add(new MoveListEntry("Cut",
				new PokemonMove(PokemonType.T_NORMAL, 50, 0.95, 30)));

		m_moves.add(new MoveListEntry("Dragon Claw",
				new PokemonMove(PokemonType.T_DRAGON, 80, 1.0, 15)));

		m_moves.add(new MoveListEntry("Drill Peck",
				new PokemonMove(PokemonType.T_FLYING, 80, 1.0, 20)));

		m_moves.add(new MoveListEntry("Earthquake",
				new PokemonMove(PokemonType.T_GROUND, 100, 1.0, 10)));

		m_moves.add(new MoveListEntry("Egg Bomb",
				new PokemonMove(PokemonType.T_NORMAL, 75, 1.0, 10)));

		m_moves.add(new MoveListEntry("Gust",
				new PokemonMove(PokemonType.T_FLYING, 35, 1.0, 35)));

		m_moves.add(new MoveListEntry("Horn Attack",
				new PokemonMove(PokemonType.T_NORMAL, 65, 1.0, 25)));

		m_moves.add(new MoveListEntry("Hydro Pump",
				new PokemonMove(PokemonType.T_WATER, 120, 0.8, 5)));

		m_moves.add(new MoveListEntry("Hyper Voice",
				new PokemonMove(PokemonType.T_NORMAL, 90, 1.0, 10)));

		m_moves.add(new MoveListEntry("Megahorn",
				new PokemonMove(PokemonType.T_BUG, 120, 0.85, 10)));

		m_moves.add(new MoveListEntry("Mega Punch",
				new PokemonMove(PokemonType.T_NORMAL, 80, 0.85, 20)));

		m_moves.add(new MoveListEntry("Mega Kick",
				new PokemonMove(PokemonType.T_NORMAL, 120, 0.75, 5)));

		m_moves.add(new MoveListEntry("Peck",
				new PokemonMove(PokemonType.T_FLYING, 35, 1.0, 35)));

		m_moves.add(new MoveListEntry("Pound",
				new PokemonMove(PokemonType.T_NORMAL, 40, 1.0, 35)));

		m_moves.add(new MoveListEntry("Rock Throw",
				new PokemonMove(PokemonType.T_ROCK, 50, 0.9, 15)));

		m_moves.add(new MoveListEntry("Rolling Kick",
				new PokemonMove(PokemonType.T_NORMAL, 60, 0.85, 15)));

		m_moves.add(new MoveListEntry("Scratch",
				new PokemonMove(PokemonType.T_NORMAL, 40, 1.0, 35)));

		m_moves.add(new MoveListEntry("Sky Uppercut",
				new PokemonMove(PokemonType.T_FIGHTING, 85, 0.9, 15)));

		m_moves.add(new MoveListEntry("Slam",
				new PokemonMove(PokemonType.T_NORMAL, 80, 0.75, 20)));

		m_moves.add(new MoveListEntry("Surf",
				new PokemonMove(PokemonType.T_WATER, 95, 1.0, 15)));

		m_moves.add(new MoveListEntry("Tackle",
				new PokemonMove(PokemonType.T_NORMAL, 35, 0.95, 35)));

		m_moves.add(new MoveListEntry("Vicegrip",
				new PokemonMove(PokemonType.T_NORMAL, 55, 1.0, 30)));

		m_moves.add(new MoveListEntry("Vine Whip",
				new PokemonMove(PokemonType.T_GRASS, 35, 1.0, 10)));

		m_moves.add(new MoveListEntry("Water Gun",
				new PokemonMove(PokemonType.T_WATER, 40, 1.0, 25)));

		m_moves.add(new MoveListEntry("Waterfall",
				new PokemonMove(PokemonType.T_WATER, 80, 1.0, 15)));

		m_moves.add(new MoveListEntry("Wing Attack",
				new PokemonMove(PokemonType.T_FLYING, 60, 1.0, 35)));

		m_moves.add(new MoveListEntry("Strength",
				new PokemonMove(PokemonType.T_NORMAL, 80, 1.0, 15)));

		m_moves.add(new MoveListEntry("Return",
				new PokemonMove(PokemonType.T_NORMAL, 102, 1.0, 20)));

		m_moves.add(new MoveListEntry("Frustration",
				new PokemonMove(PokemonType.T_NORMAL, 102, 1.0, 20)));

		m_moves.add(new MoveListEntry("Weather Ball",
				new PokemonMove(PokemonType.T_NORMAL, 50, 1.0, 10)));
	}

	private void initStatusMoves() {        
		m_moves.add(new MoveListEntry("Acid", new StatusMove(
				PokemonType.T_POISON, 40, 1.0, 30, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Agility", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 30, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, true, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Acid Armor", new StatusMove(
				PokemonType.T_POISON, 0, 1.0, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, true, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Amnesia", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, true, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Astonish", new StatusMove(
				PokemonType.T_GHOST, 30, 1.0, 15, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Attract", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 15, new StatusEffect[] {
						new AttractEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Aurora Beam", new StatusMove(
				PokemonType.T_ICE, 65, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, false)
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Barrier", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 30, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, true, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Bite", new StatusMove(
				PokemonType.T_DARK, 60, 1.0, 25, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Blaze Kick", new StatusMove(
				PokemonType.T_FIRE, 85, 0.9, 10, new StatusEffect[] {
						new BurnEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Blizzard", new StatusMove(
				PokemonType.T_ICE, 120, 0.7, 5, new StatusEffect[] {
						new FreezeEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Body Slam", new StatusMove(
				PokemonType.T_NORMAL, 85, 1.0, 15, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Bone Club", new StatusMove(
				PokemonType.T_GROUND, 65, 0.85, 20, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Bubble", new StatusMove(
				PokemonType.T_WATER, 20, 1.0, 30, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, false)
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Bubblebeam", new StatusMove(
				PokemonType.T_WATER, 65, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, false)
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Bulk Up", new StatusMove(
				PokemonType.T_FIGHTING, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, true),
						new StatChangeEffect(Pokemon.S_DEFENCE, true)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Calm Mind", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, true),
						new StatChangeEffect(Pokemon.S_SPDEFENCE, true)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Charm", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, false, 2)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Constrict", new StatusMove(
				PokemonType.T_NORMAL, 10, 1.0, 35, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Confuse Ray", new StatusMove(
				PokemonType.T_GHOST, 0, 1.0, 10, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Confusion", new StatusMove(
				PokemonType.T_PSYCHIC, 50, 1.0, 25, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Cosmic Power", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, true),
						new StatChangeEffect(Pokemon.S_SPDEFENCE, true)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Cotton Spore", new StatusMove(
				PokemonType.T_GRASS, 0, 0.85, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, false, 2)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		// TBD: SHOULD BE SPECIAL DEFENCE IN ADVANCE
		m_moves.add(new MoveListEntry("Crunch", new StatusMove(
				PokemonType.T_DARK, 80, 1.0, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.2 }
		)));

		m_moves.add(new MoveListEntry("Crush Claw", new StatusMove(
				PokemonType.T_NORMAL, 75, 0.95, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Defense Curl", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, true),
						new DefenseCurlEffect()           
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Dizzy Punch", new StatusMove(
				PokemonType.T_NORMAL, 70, 1.0, 10, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Double Team", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_EVASION, true)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Dragonbreath", new StatusMove(
				PokemonType.T_DRAGON, 60, 1.0, 20, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Dragon Dance", new StatusMove(
				PokemonType.T_DRAGON, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, true),
						new StatChangeEffect(Pokemon.S_SPEED, true)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Dynamicpunch", new StatusMove(
				PokemonType.T_FIGHTING, 100, 0.5, 5, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Ember", new StatusMove(
				PokemonType.T_FIRE, 40, 1.0, 25, new StatusEffect[] {
						new BurnEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Extrasensory", new StatusMove(
				PokemonType.T_PSYCHIC, 80, 1.0, 30, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Fake Tears", new StatusMove(
				PokemonType.T_DARK, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false, 2)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Featherdance", new StatusMove(
				PokemonType.T_FLYING, 0, 1.0, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, false, 2)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Fire Blast", new StatusMove(
				PokemonType.T_FIRE, 120, 0.85, 5, new StatusEffect[] {
						new BurnEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Fire Punch", new StatusMove(
				PokemonType.T_FIRE, 75, 1.0, 15, new StatusEffect[] {
						new BurnEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Flamethrower", new StatusMove(
				PokemonType.T_FIRE, 95, 1.0, 15, new StatusEffect[] {
						new BurnEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Flame Wheel", new StatusMove(
				PokemonType.T_FIRE, 60, 1.0, 25, new StatusEffect[] {
						new BurnEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Flash", new StatusMove(
				PokemonType.T_NORMAL, 0, 0.7, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ACCURACY, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				setAccuracy((mech instanceof JewelMechanics) ? 1.0 : 0.7);
				return super.attemptHit(mech, user, target);
			}
		}));

		m_moves.add(new MoveListEntry("Flatter", new StatusMove(
				PokemonType.T_DARK, 0, 1.0, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, true, 2),
						new ConfuseEffect()
				},
				new boolean[] { false, false },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Glare", new StatusMove(
				PokemonType.T_NORMAL, 0, 0.75, 30, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		) {
			public boolean isEffective(Pokemon target) {
				if (!(target.getField().getMechanics() instanceof JewelMechanics))
					return super.isEffective(target);
				/** Believe it or not, in D/P, this move completely ignores
				 *  type immunities!
				 */
				return true;
			}
		}));

		m_moves.add(new MoveListEntry("Grasswhistle", new StatusMove(
				PokemonType.T_GRASS, 0, 0.55, 15, new StatusEffect[] {
						new SleepEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Growl", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Growth", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, true)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Harden", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 30, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, true)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Headbutt", new StatusMove(
				PokemonType.T_NORMAL, 70, 1.0, 15, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Heat Wave", new StatusMove(
				PokemonType.T_FIRE, 100, 0.9, 10, new StatusEffect[] {
						new BurnEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Howl", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, true)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Hyper Fang", new StatusMove(
				PokemonType.T_NORMAL, 80, 0.9, 15, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { true },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Hypnosis", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 0.6, 20, new StatusEffect[] {
						new SleepEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Ice Beam", new StatusMove(
				PokemonType.T_ICE, 95, 1.0, 10, new StatusEffect[] {
						new FreezeEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Ice Punch", new StatusMove(
				PokemonType.T_ICE, 75, 1.0, 15, new StatusEffect[] {
						new FreezeEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Icy Wind", new StatusMove(
				PokemonType.T_ICE, 55, 0.95, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Iron Defense", new StatusMove(
				PokemonType.T_STEEL, 0, 1.0, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, true, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Iron Tail", new StatusMove(
				PokemonType.T_STEEL, 100, 0.75, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Kinesis", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 0.8, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ACCURACY, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Leer", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 30, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Lick", new StatusMove(
				PokemonType.T_GHOST, 20, 1.0, 20, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Lovely Kiss", new StatusMove(
				PokemonType.T_NORMAL, 0, 0.75, 10, new StatusEffect[] {
						new SleepEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Luster Purge", new StatusMove(
				PokemonType.T_PSYCHIC, 70, 1.0, 5, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.5 }
		)));

		m_moves.add(new MoveListEntry("Meditate", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, true)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Metal Claw", new StatusMove(
				PokemonType.T_STEEL, 50, 1.0, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, true)
				},
				new boolean[] { true },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Metal Sound", new StatusMove(
				PokemonType.T_STEEL, 0, 0.85, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false, 2)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Meteor Mash", new StatusMove(
				PokemonType.T_STEEL, 100, 0.85, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, true)
				},
				new boolean[] { true },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Minimize", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_EVASION, true),
						new MinimizeEffect()
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Mist Ball", new StatusMove(
				PokemonType.T_PSYCHIC, 70, 1.0, 5, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.5 }
		)));

		m_moves.add(new MoveListEntry("Mud Shot", new StatusMove(
				PokemonType.T_GROUND, 55, 0.95, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Mud-Slap", new StatusMove(
				PokemonType.T_GROUND, 20, 1.0, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ACCURACY, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Muddy Water", new StatusMove(
				PokemonType.T_WATER, 95, 0.85, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ACCURACY, false)
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Mud-Slap", new StatusMove(
				PokemonType.T_GROUND, 20, 1.0, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ACCURACY, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Needle Arm", new StatusMove(
				PokemonType.T_GRASS, 60, 1.0, 15, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Octazooka", new StatusMove(
				PokemonType.T_WATER, 65, 0.85, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ACCURACY, false)
				},
				new boolean[] { false },
				new double[] { 0.5 }
		)));

		m_moves.add(new MoveListEntry("Overheat", new StatusMove(
				PokemonType.T_FIRE, 140, 0.90, 5, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, false, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Poison Gas", new StatusMove(
				PokemonType.T_POISON, 0, 0.55, 40, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Poison Sting", new StatusMove(
				PokemonType.T_POISON, 15, 1.0, 35, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Poisonpowder", new StatusMove(
				PokemonType.T_POISON, 0, 0.75, 35, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Toxic", new StatusMove(
				PokemonType.T_POISON, 0, 0.85, 10, new StatusEffect[] {
						new ToxicEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Poison Fang", new StatusMove(
				PokemonType.T_POISON, 50, 1.0, 15, new StatusEffect[] {
						new ToxicEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Powder Snow", new StatusMove(
				PokemonType.T_ICE, 40, 1.0, 25, new StatusEffect[] {
						new FreezeEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Psybeam", new StatusMove(
				PokemonType.T_PSYCHIC, 65, 1.0, 20, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Psychic", new StatusMove(
				PokemonType.T_PSYCHIC, 90, 1.0, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Psycho Boost", new StatusMove(
				PokemonType.T_PSYCHIC, 140, 0.9, 5, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, false, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Rock Slide", new StatusMove(
				PokemonType.T_ROCK, 75, 0.9, 10, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Rock Smash", new StatusMove(
				PokemonType.T_FIGHTING, 20, 1.0, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.3 }
		) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				setPower(mech instanceof JewelMechanics ? 40 : 20);
				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Rock Tomb", new StatusMove(
				PokemonType.T_ROCK, 50, 0.8, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Sand-Attack", new StatusMove(
				PokemonType.T_GROUND, 0, 1.0, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ACCURACY, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Sacred Fire", new StatusMove(
				PokemonType.T_FIRE, 100, 0.95, 5, new StatusEffect[] {
						new BurnEffect()
				},
				new boolean[] { false },
				new double[] { 0.5 }
		)));

		m_moves.add(new MoveListEntry("Scary Face", new StatusMove(
				PokemonType.T_NORMAL, 0, 0.9, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, false, 2)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Screech", new StatusMove(
				PokemonType.T_NORMAL, 0, 0.85, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, false, 2)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Shadow Ball", new StatusMove(
				PokemonType.T_GHOST, 80, 1.0, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.2 }
		)));

		m_moves.add(new MoveListEntry("Sharpen", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 30, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, true, 1)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Signal Beam", new StatusMove(
				PokemonType.T_BUG, 75, 1.0, 15, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Sing", new StatusMove(
				PokemonType.T_NORMAL, 0, 0.55, 15, new StatusEffect[] {
						new SleepEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Sleep Powder", new StatusMove(
				PokemonType.T_GRASS, 0, 0.75, 15, new StatusEffect[] {
						new SleepEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Sludge", new StatusMove(
				PokemonType.T_POISON, 65, 1.0, 20, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Sludge Bomb", new StatusMove(
				PokemonType.T_POISON, 90, 1.0, 10, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Smog", new StatusMove(
				PokemonType.T_POISON, 20, 0.7, 20, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Smokescreen", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ACCURACY, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Spark", new StatusMove(
				PokemonType.T_ELECTRIC, 65, 1.0, 20, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Spore", new StatusMove(
				PokemonType.T_GRASS, 0, 1.0, 15, new StatusEffect[] {
						new SleepEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Steel Wing", new StatusMove(
				PokemonType.T_STEEL, 70, 0.9, 25, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, true)
				},
				new boolean[] { true },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Stomp", new StatusMove(
				PokemonType.T_NORMAL, 65, 1.0, 20, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("String Shot", new StatusMove(
				PokemonType.T_BUG, 0, 0.95, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Stun Spore", new StatusMove(
				PokemonType.T_GRASS, 0, 0.75, 30, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Superpower", new StatusMove(
				PokemonType.T_FIGHTING, 120, 1.0, 5, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, false),
						new StatChangeEffect(Pokemon.S_DEFENCE, false)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Supersonic", new StatusMove(
				PokemonType.T_NORMAL, 0, 0.55, 30, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Swagger", new StatusMove(
				PokemonType.T_NORMAL, 0, 0.9, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, true, 2),
						new ConfuseEffect()
				},
				new boolean[] { false, false },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Sweet Kiss", new StatusMove(
				PokemonType.T_NORMAL, 0, 0.75, 10, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Sweet Scent", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_EVASION, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Swords Dance", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 30, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, true, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Tail Glow", new StatusMove(
				PokemonType.T_BUG, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, true, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Tail Whip", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 30, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Teeter Dance", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 20, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Thunder", new StatusMove(
				PokemonType.T_ELECTRIC, 120, 0.7, 10, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Thunder Wave", new StatusMove(
				PokemonType.T_ELECTRIC, 0, 1.0, 20, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Thunderbolt", new StatusMove(
				PokemonType.T_ELECTRIC, 95, 1.0, 15, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Thunderpunch", new StatusMove(
				PokemonType.T_ELECTRIC, 75, 1.0, 15, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Thundershock", new StatusMove(
				PokemonType.T_ELECTRIC, 40, 1.0, 30, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		// TODO: Hits through substitutes in Advance, but not in D/P.
		m_moves.add(new MoveListEntry("Tickle", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, false),
						new StatChangeEffect(Pokemon.S_DEFENCE, false)
				},
				new boolean[] { false, false },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Tri Attack", new StatusMove(
				PokemonType.T_NORMAL, 80, 1.0, 10, new StatusEffect[] {
						new ParalysisEffect(),
						new BurnEffect(),
						new FreezeEffect()
				},
				new boolean[] { false, false, false },
				new double[] { 0.1, 0.1, 0.1 }
		)));

		m_moves.add(new MoveListEntry("Twister", new StatusMove(
				PokemonType.T_DRAGON, 40, 1.0, 20, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Water Pulse", new StatusMove(
				PokemonType.T_WATER, 60, 1.0, 20, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 0.2 }
		)));

		m_moves.add(new MoveListEntry("Will-o-wisp", new StatusMove(
				PokemonType.T_FIRE, 0, 0.75, 15, new StatusEffect[] {
						new BurnEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Withdraw", new StatusMove(
				PokemonType.T_WATER, 0, 1.0, 40, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, true)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Zap Cannon", new StatusMove(
				PokemonType.T_ELECTRIC, 100, 0.5, 5, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Milk Drink", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 10, new StatusEffect[] {
						new PercentEffect(0.5, false, -1, null)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		//todo: this should have 20pp in advance and 10pp in d/p
		m_moves.add(new MoveListEntry("Recover", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 10, new StatusEffect[] {
						new PercentEffect(0.5, false, -1, null)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Softboiled", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 10, new StatusEffect[] {
						new PercentEffect(0.5, false, -1, null)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		{
			StatusMove healing = new StatusMove(
					PokemonType.T_NORMAL, 0, 1.0, 5, new StatusEffect[] {
							new PercentEffect(0.5, false, -1, null)
					},
					new boolean[] { true },
					new double[] { 1.0 }
			);
			m_moves.add(new MoveListEntry("Moonlight", (PokemonMove)healing.clone()));
			m_moves.add(new MoveListEntry("Morning Sun", (PokemonMove)healing.clone()));
			m_moves.add(new MoveListEntry("Synthesis", (PokemonMove)healing.clone()));
		}

		m_moves.add(new MoveListEntry("Solarbeam", new StatusMove(
				PokemonType.T_GRASS, 0, 1.0, 10, new StatusEffect[] {
						new ChargeEffect(1, "takes in sunlight!", new MoveListEntry(
								"Solarbeam",
								new PokemonMove(
										PokemonType.T_GRASS, 120, 1.0, 10
								)
						)
						)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)   {
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Sunny Day", new WeatherMove(
				PokemonType.T_FIRE, 5,
				new Class[] { SunEffect.class },
				"Heat Rock"
		)));

		m_moves.add(new MoveListEntry("Rain Dance", new WeatherMove(
				PokemonType.T_WATER, 5,
				new Class[] { RainEffect.class },
				"Damp Rock"
		)));

		m_moves.add(new MoveListEntry("Hail", new WeatherMove(
				PokemonType.T_ICE, 5,
				new Class[] { HailEffect.class },
				"Icy Rock"
		)));

		m_moves.add(new MoveListEntry("Sandstorm", new WeatherMove(
				PokemonType.T_ROCK, 5,
				new Class[] { SandstormEffect.class },
				"Smooth Rock"
		)));

		m_moves.add(new MoveListEntry("Seismic Toss",
				new PokemonMove(PokemonType.T_FIGHTING, 0, 1.0, 20) {
			public boolean isAttack() {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (getEffectiveness(user, target) == 0.0) {
					user.getField().showMessage("It doesn't affect "
							+ target.getName() + "...");
					return 0;
				}
				int damage = user.getLevel();
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Night Shade",
				new PokemonMove(PokemonType.T_GHOST, 0, 1.0, 15) {
			public boolean isAttack() {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (getEffectiveness(user, target) == 0.0) {
					user.getField().showMessage("It doesn't affect "
							+ target.getName() + "...");
					return 0;
				}
				int damage = user.getLevel();
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Sonicboom",
				new PokemonMove(PokemonType.T_NORMAL, 0, 0.9, 20) {
			public boolean isAttack() {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (getEffectiveness(user, target) == 0.0) {
					user.getField().showMessage("It doesn't affect "
							+ target.getName() + "...");
					return 0;
				}
				final int damage = 20;
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Dragon Rage",
				new PokemonMove(PokemonType.T_DRAGON, 0, 1.0, 10) {
			public boolean isAttack() {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				final int damage = 40;
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Double-edge",
				new RecoilMove(PokemonType.T_NORMAL, 120, 1.0, 20, 0.125)));

		m_moves.add(new MoveListEntry("Submission",
				new RecoilMove(PokemonType.T_FIGHTING, 80, 0.8, 25, 0.25)));

		m_moves.add(new MoveListEntry("Take Down",
				new RecoilMove(PokemonType.T_NORMAL, 90, 0.85, 20, 0.25)));

		m_moves.add(new MoveListEntry("Volt Tackle",
				new RecoilMove(PokemonType.T_ELECTRIC, 120, 1.0, 15, 1.0/3.0)));

		m_moves.add(new MoveListEntry("Struggle",
				new RecoilMove(PokemonType.T_TYPELESS, 50, 1.0, 1, 0.50) {
			public boolean isProtected(Pokemon p) {
				return false;
			}
			public int getRecoil(Pokemon p, int damage) {
				if (p.getField().getMechanics() instanceof JewelMechanics) {
					return p.getStat(Pokemon.S_HP) / 4;
				}
				return super.getRecoil(p, damage);
			}
		}));

		m_moves.add(new MoveListEntry("Arm Thrust",
				new MultipleHitMove(PokemonType.T_FIGHTING, 15, 1.0, 20)));

		m_moves.add(new MoveListEntry("Barrage",
				new MultipleHitMove(PokemonType.T_NORMAL, 15, 0.85, 20)));

		m_moves.add(new MoveListEntry("Bone Rush",
				new MultipleHitMove(PokemonType.T_GROUND, 25, 0.8, 10)));

		m_moves.add(new MoveListEntry("Bullet Seed",
				new MultipleHitMove(PokemonType.T_GRASS, 10, 1.0, 30)));

		m_moves.add(new MoveListEntry("Comet Punch",
				new MultipleHitMove(PokemonType.T_NORMAL, 18, 0.85, 15)));

		m_moves.add(new MoveListEntry("Doubleslap",
				new MultipleHitMove(PokemonType.T_NORMAL, 15, 0.85, 10)));

		m_moves.add(new MoveListEntry("Fury Attack",
				new MultipleHitMove(PokemonType.T_NORMAL, 15, 0.85, 20)));

		m_moves.add(new MoveListEntry("Fury Swipes",
				new MultipleHitMove(PokemonType.T_NORMAL, 18, 0.8, 15)));

		m_moves.add(new MoveListEntry("Icicle Spear",
				new MultipleHitMove(PokemonType.T_ICE, 10, 1.0, 30)));

		m_moves.add(new MoveListEntry("Pin Missile",
				new MultipleHitMove(PokemonType.T_BUG, 14, 0.85, 20)));

		m_moves.add(new MoveListEntry("Rock Blast",
				new MultipleHitMove(PokemonType.T_ROCK, 25, 0.8, 10)));

		m_moves.add(new MoveListEntry("Spike Cannon",
				new MultipleHitMove(PokemonType.T_NORMAL, 20, 1.0, 15)));

		m_moves.add(new MoveListEntry("Slack Off", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 10, new StatusEffect[] {
						new PercentEffect(0.5, false, -1, null)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Absorb",
				new AbsorbMove(PokemonType.T_GRASS, 20, 1.0, 20, 0.5)));

		/** TBD (TODO): Has only 5 PP in Advance, but 10 in D/P. */
		m_moves.add(new MoveListEntry("Giga Drain",
				new AbsorbMove(PokemonType.T_GRASS, 60, 1.0, 10, 0.5)));

		m_moves.add(new MoveListEntry("Leech Life",
				new AbsorbMove(PokemonType.T_BUG, 20, 1.0, 15, 0.5)));

		m_moves.add(new MoveListEntry("Mega Drain",
				new AbsorbMove(PokemonType.T_GRASS, 40, 1.0, 10, 0.5)));

		m_moves.add(new MoveListEntry("Ancientpower", new StatusMove(
				PokemonType.T_ROCK, 60, 1.0, 5, new StatusEffect[] {
						new MultipleStatChangeEffect(new int[] {
								Pokemon.S_ATTACK,
								Pokemon.S_DEFENCE,
								Pokemon.S_SPEED,
								Pokemon.S_SPATTACK,
								Pokemon.S_SPDEFENCE
						}
						)
				},
				new boolean[] { true },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Silver Wind", new StatusMove(
				PokemonType.T_BUG, 60, 1.0, 5, new StatusEffect[] {
						new MultipleStatChangeEffect(new int[] {
								Pokemon.S_ATTACK,
								Pokemon.S_DEFENCE,
								Pokemon.S_SPEED,
								Pokemon.S_SPATTACK,
								Pokemon.S_SPDEFENCE
						}
						)
				},
				new boolean[] { true },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Belly Drum", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 10, new StatusEffect[] {
						// new PercentEffect(-0.5, false, -1, null),
						new StatChangeEffect(Pokemon.S_ATTACK, true, 12)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage = (user.getStat(Pokemon.S_HP) / 2);
				if (user.getHealth() <= damage) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				IntrinsicAbility ability = user.getAbility();
				if (user.hasAbility("Magic Guard")) {
					ability.deactivate();
				} else {
					ability = null;
				}
				user.changeHealth(-damage, true);
				if (ability != null) {
					ability.activate();
				}
				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Reversal",
				new HealthBasedMove(PokemonType.T_FIGHTING, 1.0, 15, false)));

		m_moves.add(new MoveListEntry("Flail",
				new HealthBasedMove(PokemonType.T_NORMAL, 1.0, 15, false)));

		m_moves.add(new MoveListEntry("Eruption",
				new HealthBasedMove(PokemonType.T_FIRE, 1.0, 5, true)));

		m_moves.add(new MoveListEntry("Water Spout",
				new HealthBasedMove(PokemonType.T_WATER, 1.0, 5, true)));

		m_moves.add(new MoveListEntry("Aeroblast",
				new HighCriticalHitMove(PokemonType.T_FLYING, 120, 0.95, 5)));

		m_moves.add(new MoveListEntry("Air Cutter",
				new HighCriticalHitMove(PokemonType.T_FLYING, 55, 0.95, 25)));

		m_moves.add(new MoveListEntry("Crabhammer",
				new HighCriticalHitMove(PokemonType.T_WATER, 90, 0.85, 10)));

		m_moves.add(new MoveListEntry("Cross Chop",
				new HighCriticalHitMove(PokemonType.T_FIGHTING, 100, 0.8, 5)));

		m_moves.add(new MoveListEntry("Karate Chop",
				new HighCriticalHitMove(PokemonType.T_FIGHTING, 50,  1.0, 25)));

		m_moves.add(new MoveListEntry("Leaf Blade",
				new HighCriticalHitMove(PokemonType.T_GRASS, 70, 1.0, 15) {
			public int use(BattleMechanics mech, Pokemon source, Pokemon target) {
				setPower((mech instanceof JewelMechanics) ? 90 : 70);
				return super.use(mech, source, target);
			}
		}));

		m_moves.add(new MoveListEntry("Poison Tail",
				new StatusMove(PokemonType.T_POISON, 50, 1.0, 25, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
				) {
			public boolean hasHighCriticalHitRate() { return true; }
		}
		));

		m_moves.add(new MoveListEntry("Razor Leaf",
				new HighCriticalHitMove(PokemonType.T_GRASS, 55, 0.95, 25)));

		m_moves.add(new MoveListEntry("Slash",
				new HighCriticalHitMove(PokemonType.T_NORMAL, 70, 1.0, 20)));         

		m_moves.add(new MoveListEntry("Aerial Ace",
				new PerfectAccuracyMove(PokemonType.T_FLYING, 60, 20)));

		m_moves.add(new MoveListEntry("Faint Attack",
				new PerfectAccuracyMove(PokemonType.T_DARK, 60, 20)));

		m_moves.add(new MoveListEntry("Magical Leaf",
				new PerfectAccuracyMove(PokemonType.T_GRASS, 60, 20)));

		m_moves.add(new MoveListEntry("Shadow Punch",
				new PerfectAccuracyMove(PokemonType.T_GHOST, 60, 20)));

		m_moves.add(new MoveListEntry("Swift",
				new PerfectAccuracyMove(PokemonType.T_NORMAL, 60, 20)));

		m_moves.add(new MoveListEntry("Shock Wave",
				new PerfectAccuracyMove(PokemonType.T_ELECTRIC, 60, 20)));

		m_moves.add(new MoveListEntry("Bonemerang",
				new PokemonMove(PokemonType.T_GROUND, 50, 0.9, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage = 0;
				for (int i = 0; i < 2; ++i) {
					final int partial = mech.calculateDamage(this, user, target);
					target.changeHealth(-partial);
					damage += partial;
				}
				user.getField().showMessage("Hit 2 time(s)!");
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Double Kick",
				new PokemonMove(PokemonType.T_FIGHTING, 30, 1.0, 30) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage = 0;
				for (int i = 0; i < 2; ++i) {
					final int partial = mech.calculateDamage(this, user, target);
					target.changeHealth(-partial);
					damage += partial;
				}
				user.getField().showMessage("Hit 2 time(s)!");
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Twineedle",
				new StatusMove(PokemonType.T_BUG, 25, 1.0, 15, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
				) 
		{
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage = 0;
				for (int i = 0; i < 2; ++i) {
					damage += super.use(mech, user, target);
				}
				return damage;
			}
		}
		));

		PokemonMove healBell = new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 5) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				boolean sound = getMoveListEntry().getName().equals("Heal Bell");
				Pokemon[] teammates = user.getTeammates();
				for (int i = 0; i < teammates.length; ++i) {
					Pokemon p = teammates[i];
					if (p == null || p.isFainted() || (sound && p.hasAbility("Soundproof")))
						continue;
					p.removeStatus(StatusEffect.SPECIAL_EFFECT_LOCK);
				}
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		};

		PokemonMove aromatherapy = (PokemonMove)healBell.clone();
		aromatherapy.setType(PokemonType.T_GRASS);

		m_moves.add(new MoveListEntry("Aromatherapy", aromatherapy));

		m_moves.add(new MoveListEntry("Heal Bell", healBell));

		m_moves.add(new MoveListEntry("Assist",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 15) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				Pokemon[] teammates = user.getTeammates();
				if (teammates.length < 2) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				Random random = mech.getRandom();
				Pokemon teammate;
				do {
					teammate = teammates[random.nextInt(teammates.length)];
				} while (teammate == user);
				MoveListEntry move;
				do {
					move = teammate.getMove(random.nextInt(4));
				} while (move == null);
				if (move.getName().equals("Focus Punch")) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				return user.useMove(move, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Facade",
				new PokemonMove(PokemonType.T_NORMAL, 70, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int power = getPower();
				if (user.hasEffect(StatusEffect.SPECIAL_EFFECT_LOCK)) {
					setPower(2 * power);
				}
				int damage = mech.calculateDamage(this, user, target);
				setPower(power);
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("False Swipe",
				new PokemonMove(PokemonType.T_NORMAL, 40, 1.0, 40) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int targetHealth = target.getHealth();
				int damage = mech.calculateDamage(this, user, target);
				if (damage >= targetHealth) {
					damage = targetHealth - 1;
				}
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Psywave",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 0.8, 15) {
			public boolean isAttack() {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				Random random = mech.getRandom();
				int damage = user.getLevel() * (random.nextInt(11) * 10 + 50) / 100;
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Jump Kick",
				new JumpKickMove(PokemonType.T_FIGHTING, 70, 85, 0.95, 20)
		));

		m_moves.add(new MoveListEntry("Hi Jump Kick",
				new JumpKickMove(PokemonType.T_FIGHTING, 85, 100, 0.90, 20) 
		));

		m_moves.add(new MoveListEntry("Refresh",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.removeStatus(StatusEffect.SPECIAL_EFFECT_LOCK);
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Smellingsalt",
				new PokemonMove(PokemonType.T_NORMAL, 60, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int power = getPower();
				if (!target.hasSubstitute() && target.hasEffect(ParalysisEffect.class)) {
					setPower(2 * power);
					target.removeStatus(StatusEffect.SPECIAL_EFFECT_LOCK);
					user.getField().showMessage(target.getName() + " was cured of paralysis!");
				}
				int damage = mech.calculateDamage(this, user, target);
				setPower(power);
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Splash",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 40) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().showMessage("But nothing happened!");
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Teleport",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().showMessage("But it failed!");
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Triple Kick",
				new PokemonMove(PokemonType.T_FIGHTING, 60, 0.9, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage = 0;
				PokemonMove move = new PokemonMove(PokemonType.T_FIGHTING, 0, 1.0, 1);
				for (int i = 1; i < 4; ++i) {
					move.setPower(i * 10);
					damage += user.useMove(move, target);
				}
				user.getField().showMessage("Hit 3 time(s)!");
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Razor Wind", new StatusMove(
				PokemonType.T_FLYING, 0, 1.0, 10, new StatusEffect[] {
						new ChargeEffect(1, "created a whirlwind!", new MoveListEntry(
								"Razor Wind",
								new HighCriticalHitMove(
										PokemonType.T_FLYING, 80, 1.0, 10
								)
						)
						)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)   {
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Skull Bash", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, true, 1),
						new ChargeEffect(1, "lowered its head!", new MoveListEntry(
								"Skull Bash",
								new PokemonMove(
										PokemonType.T_NORMAL, 100, 1.0, 15
								)
						)
						)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)   {
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Sky Attack", new StatusMove(
				PokemonType.T_FLYING, 0, 1.0, 5, new StatusEffect[] {
						new ChargeEffect(1, "is glowing!", new MoveListEntry(
								"Sky Attack",
								new PokemonMove(
										PokemonType.T_FLYING, 140, 0.9, 5
								)
						)
						)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)   {
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Pay Day",
				new PokemonMove(PokemonType.T_NORMAL, 40, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().showMessage("Coins scattered everywhere!");
				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Present",
				new PokemonMove(PokemonType.T_NORMAL, 0, 0.9, 15) {
			public boolean isAttack() {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				double random = mech.getRandom().nextDouble();
				int power;
				if (random <= 0.4) {
					power = 40;
				} else if (random <= 0.7) {
					power = 80;
				} else if (random <= 0.8) {
					power = 120;
				} else {
					double maximum = (double)target.getStat(Pokemon.S_HP);
					int restore = (int)(maximum * 0.2);
					target.changeHealth(restore);
					return restore;
				}
				setPower(power);
				int damage = mech.calculateDamage(this, user, target);
				setPower(0);
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Dream Eater",
				new AbsorbMove(PokemonType.T_PSYCHIC, 100, 1.0, 15, 0.5) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasEffect(SleepEffect.class) && !target.hasSubstitute()) {
					return super.use(mech, user, target);
				}
				user.getField().showMessage("But it failed!");
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Haze",
				new PokemonMove(PokemonType.T_ICE, 0, 1.0, 30) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.removeStatus(StatChangeEffect.class);
				target.removeStatus(StatChangeEffect.class);
				return 0;
			}
		}
		));

		PokemonMove foresight = new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 40) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				List<StatusEffect> statuses = target.getNormalStatuses(0);
				Iterator<StatusEffect> i = statuses.iterator();
				while (i.hasNext()) {
					StatusEffect effect = (StatusEffect)i.next();
					if (!(effect instanceof StatChangeEffect)) continue;
					StatChangeEffect eff = (StatChangeEffect)effect;
					if (eff.getStat() == Pokemon.S_EVASION) {
						target.removeStatus(eff);
					}
				}
				statuses = user.getNormalStatuses(0);
				i = statuses.iterator();
				while (i.hasNext()) {
					StatusEffect effect = (StatusEffect)i.next();
					if (!(effect instanceof StatChangeEffect)) continue;
					StatChangeEffect eff = (StatChangeEffect)effect;
					if (eff.getStat() == Pokemon.S_ACCURACY) {
						user.removeStatus(eff);
					}
				}
				user.getAccuracy().setSecondaryMultiplier(1);
				target.getEvasion().setSecondaryMultiplier(1);
				return 0;
			}
		};

		m_moves.add(new MoveListEntry("Foresight", (PokemonMove)foresight.clone()));
		m_moves.add(new MoveListEntry("Odor Sleuth", (PokemonMove)foresight.clone()));

		m_moves.add(new MoveListEntry("Fissure",
				new OneHitKillMove(PokemonType.T_GROUND, 5)));

		m_moves.add(new MoveListEntry("Horn Drill",
				new OneHitKillMove(PokemonType.T_NORMAL, 5)));

		m_moves.add(new MoveListEntry("Guillotine",
				new OneHitKillMove(PokemonType.T_NORMAL, 5)));

		m_moves.add(new MoveListEntry("Sheer Cold",
				new OneHitKillMove(PokemonType.T_ICE, 5)));

		m_moves.add(new MoveListEntry("Memento",
				new StatusMove(PokemonType.T_DARK, 0, 1.0, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ATTACK, false, 2),
						new StatChangeEffect(Pokemon.S_SPATTACK, false, 2)
				},
				new boolean[] { false, false },
				new double[] { 1.0, 1.0 }
				) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (mech.attemptHit(this, user, target)) {
					super.use(mech, user, target);
				}
				user.faint();
				return 0;
			}             
		}
		));

		PokemonMove explosion = new PokemonMove(PokemonType.T_NORMAL, 250, 1.0, 5) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasAbility("Damp")) {
					user.getField().showMessage(target.getName() + "'s Damp prevents explosions!");
					return 0;
				}
				int damage = 0;
				if (target.hasEffect(ProtectEffect.class)) {
					List<StatusEffect> list = target.getNormalStatuses(0);
					Iterator<StatusEffect> i = list.iterator();
					while (i.hasNext()) {
						Object o = i.next();
						if (o instanceof ProtectEffect) {
							ProtectEffect eff = (ProtectEffect)o;
							user.getField().showMessage(target.getName() + eff.getDescription());
							break;
						}
					}
				} else if (mech.attemptHit(this, user, target)) {
					StatMultiplier mul = target.getMultiplier(Pokemon.S_DEFENCE);
					double value = mul.getSecondaryMultiplier();
					mul.setSecondaryMultiplier(value / 2.0);
					damage = mech.calculateDamage(this, user, target);
					mul.setSecondaryMultiplier(value);
					target.changeHealth(-damage);
				}
				user.faint();
				return damage;
			}
		};

		PokemonMove selfDestruct = (PokemonMove)explosion.clone();
		selfDestruct.setPower(200);
		m_moves.add(new MoveListEntry("Selfdestruct", selfDestruct));
		m_moves.add(new MoveListEntry("Explosion", explosion));

		m_moves.add(new MoveListEntry("Nightmare",
				new StatusMove(PokemonType.T_GHOST, 0, 1.0, 15, new StatusEffect[] {
						new StatusEffect() {
							public String getName() {
								return "Nightmare";
							}
							public boolean apply(Pokemon p) {
								if (!p.hasEffect(SleepEffect.class)) {
									p.getField().showMessage("But it failed!");
									return false;
								}
								return true;
							}
							public String getDescription() {
								return " fell into a nightmare!";
							}
							public int getTier() {
								return 4;
							}
							public boolean tick(Pokemon p) {
								if (!p.hasEffect(SleepEffect.class)) {
									p.removeStatus(this);
									return true;
								}
								double maximum = (double)p.getStat(Pokemon.S_HP);
								int loss = (int)(maximum / 4.0);
								if (loss < 1) loss = 1;
								p.getField().showMessage(p.getName() + " is having a nightmare!");
								p.changeHealth(-loss);
								return false;
							}
						}
				},
				new boolean[] { false },
				new double[] { 1.0 }
				)));

		m_moves.add(new MoveListEntry("Snore",
				new PokemonMove(PokemonType.T_NORMAL, 40, 1.0, 15) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (!user.hasEffect(SleepEffect.class)) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				int damage = mech.calculateDamage(this, user, target);
				target.changeHealth(-damage);
				return damage;
			}
			public Class<?> getStatusException() {
				return SleepEffect.class;
			}
		}
		));

		m_moves.add(new MoveListEntry("Sleep Talk",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (!user.hasEffect(SleepEffect.class)) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				Random random = mech.getRandom();
				MoveListEntry move;

				boolean hasOtherMoves = false;
				for (int i = 0; i < 4; ++i) {
					if ((move = user.getMove(i)) != null) {
						if (!move.getName().equals("Sleep Talk")) {
							hasOtherMoves = true;
							break;
						}
					}
				}

				if (!hasOtherMoves) {
					user.getField().showMessage("But it failed!");
					return 0;
				}

				String name = null;
				do {
					move = user.getMove(random.nextInt(4));
					if (move != null) {
						if ((name = move.getName()) == null)
							continue;
					} else {
						continue;
					}
				} while (name.equals("Sleep Talk"));

				if (name.equals("Focus Punch")) {
					user.getField().showMessage("But it failed!");
					return 0;
				}

				return user.useMove(move, target);
			}

			public Class<?> getStatusException() {
				return SleepEffect.class;
			}
		}
		));

		m_moves.add(new MoveListEntry("Hidden Power", new HiddenPowerMove()));

		m_moves.add(new MoveListEntry("Endeavor",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 5) {
			public boolean isAttack() {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (getEffectiveness(user, target) == 0.0) {
					user.getField().showMessage("It doesn't affect "
							+ target.getName() + "...");
					return 0;
				}
				if (user.getHealth() >= target.getHealth()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				int damage = target.getHealth() - user.getHealth();
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Pain Split",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasSubstitute()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				final int userHp = user.getHealth();
				final int targetHp = target.getHealth();
				final int newHealth = (userHp + targetHp) / 2;
				user.changeHealth(newHealth - userHp, true);
				target.changeHealth(newHealth - targetHp, true);
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Super Fang",
				new PokemonMove(PokemonType.T_NORMAL, 0, 0.9, 10) {
			public boolean isAttack() {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage = target.getHealth() / 2;
				if (damage == 0) damage = 1;
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Extremespeed",
				new PriorityMove(PokemonType.T_NORMAL, 80, 1.0, 5, 1)));

		m_moves.add(new MoveListEntry("Mach Punch",
				new PriorityMove(PokemonType.T_FIGHTING, 40, 1.0, 30, 1)));

		m_moves.add(new MoveListEntry("Quick Attack",
				new PriorityMove(PokemonType.T_NORMAL, 40, 1.0, 30, 1)));

		m_moves.add(new MoveListEntry("Vital Throw",
				new PerfectAccuracyMove(PokemonType.T_FIGHTING, 70, 10) {
			public int getPriority() {
				return -1;
			}
		}
		));

		m_moves.add(new MoveListEntry("Leech Seed", new StatusMove(
				PokemonType.T_GRASS, 0, 0.9, 10, new StatusEffect[] {
						new LeechSeedEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Metronome", new PokemonMove(
				PokemonType.T_NORMAL, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				ArrayList<MoveListEntry> moves = getMoveList();
				int size = moves.size();
				Random random = mech.getRandom();
				MoveListEntry entry;
				do {
					entry = (MoveListEntry)moves.get(random.nextInt(size));
					String name = entry.getName();
					if (name.equals("Focus Punch") || (name.equals("Metronome"))) continue;
				} while (false);
				return user.useMove(entry, target);
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Perish Song", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 5, new StatusEffect[] {
						new PerishSongEffect(true),
						new PerishSongEffect(true)
				},
				new boolean[] { true, false },
				new double[] { 1.0, 1.0 }
		) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().showMessage("Both Pokemon will faint in 3 turn(s)");
				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Beat Up",
				new PokemonMove(PokemonType.T_NORMAL, 10, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				Pokemon[] team = user.getTeammates();
				final int baseDefence = target.getBase(Pokemon.S_DEFENCE);
				final int power = getPower();
				int totalDamage = 0;
				for (int i = 0; i < team.length; ++i) {
					Pokemon pokemon = team[i];
					if (pokemon.isFainted() || pokemon.hasEffect(StatusEffect.SPECIAL_EFFECT_LOCK)) {
						continue;
					}
					final int baseAttack = pokemon.getBase(Pokemon.S_ATTACK);
					boolean stab = pokemon.isType(getType());              
					int damage = (int)(((int)((int)(((int)((2 * pokemon.getLevel()) / 5.0 + 2.0)
							* baseAttack
							* power)
							/ baseDefence)
							/ 50.0)
							+ 2)
							* (stab ? 1.5 : 1.0));
					user.getField().showMessage(pokemon.getName() + "'s attack!");
					target.changeHealth(-damage);
					totalDamage += damage;
				}
				return totalDamage;    
			}
		}
		));

		m_moves.add(new MoveListEntry("Curse",
				new PokemonMove(PokemonType.T_TYPELESS, 0, 1.0, 10) {
			public boolean attemptHit(BattleMechanics mech, Pokemon source, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (user.isType(PokemonType.T_GHOST)) {
					user.addStatus(user, new PercentEffect(-0.5, false, -1, null));
					target.addStatus(user, new PercentEffect(-0.25, false, 3, " is afflicted by the curse!"));
					user.getField().showMessage(user.getName() + " cut its health"
							+ " and laid a curse on " + target.getName() + "!");
				} else {
					user.addStatus(user, new StatChangeEffect(Pokemon.S_SPEED, false, 1));
					user.addStatus(user, new StatChangeEffect(Pokemon.S_ATTACK, true, 1));
					user.addStatus(user, new StatChangeEffect(Pokemon.S_DEFENCE, true, 1));
				}
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Conversion", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 30, new StatusEffect[] {
						new StatusEffect() {
							private PokemonType[] m_types;

							public String getName() {
								return "Conversion";
							}
							public String getDescription() {
								return null;
							}
							public int getTier() {
								return -1;
							}
							public boolean tick(Pokemon p) {
								return false;
							}
							public boolean apply(Pokemon p) {
								m_types = p.getTypes();
								Random random = p.getField().getRandom();
								PokemonType moveType = null;
								MoveListEntry move;
								boolean fail = true;
								for (int i = 0; i < 4; ++i) {
									move = p.getMove(i);
									if ((move != null)
											&& !move.getMove().getType().equals(PokemonType.T_TYPELESS)
											&& !move.getName().equals("Conversion")) {
										fail = false;
										break;
									}
								}
								if (fail) {
									return false;
								}
								do {
									move = p.getMove(random.nextInt(4));
									if ((move == null) || move.getName().equals("Conversion")) {
										continue;
									}
									moveType = move.getMove().getType();
								} while ((moveType == null) || moveType.equals(PokemonType.T_TYPELESS));
								p.setType(new PokemonType[] { moveType });
								p.getField().showMessage(p.getName() + " became the "
										+ moveType + " type!");
								return true;
							}
							public void unapply(Pokemon p) {
								p.setType(m_types);
							}
							public boolean switchOut(Pokemon p) {
								p.setType(m_types);
								return true;
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Mud Sport", new TypeCutMove(
				PokemonType.T_GROUND, 15, PokemonType.T_ELECTRIC, "Mud Sport"
		)));

		m_moves.add(new MoveListEntry("Water Sport", new TypeCutMove(
				PokemonType.T_GROUND, 15, PokemonType.T_FIRE, "Water Sport"
		)));

		PokemonMove thief = new PokemonMove(PokemonType.T_DARK, 40, 1.0, 40) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasSubstitute()) {
					return super.use(mech, user, target);
				}
				HoldItem enemyItem = target.getItem();                  
				if ((enemyItem != null) && (user.getItem() == null) && !(target.hasAbility("Sticky Hold"))) {
					user.setItem(enemyItem);
					target.setItem(null);
					user.getField().showMessage(user.getName() + " stole " + target.getName()
							+ "'s " + enemyItem.getName() + "!");
				} else if (target.hasAbility("Sticky Hold")) {
					user.getField().showMessage(target.getName() + " held on with its Sticky Hold!");
				}
				return super.use(mech, user, target);
			}
		};

		PokemonMove covet = (PokemonMove)thief.clone();
		covet.setType(PokemonType.T_NORMAL);

		m_moves.add(new MoveListEntry("Thief", thief));
		m_moves.add(new MoveListEntry("Covet", covet));

		m_moves.add(new MoveListEntry("Knock Off",
				new PokemonMove(PokemonType.T_DARK, 20, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasSubstitute()) {
					return super.use(mech, user, target);
				}
				HoldItem item = target.getItem();
				if ((item != null) && !(target.hasAbility("Sticky Hold"))) {
					target.setItem(null);
					user.getField().showMessage(user.getName() + " knocked off foe "
							+ target.getName() + "'s " + item.getName() + "!");
				} else if (target.hasAbility("Sticky Hold")) {
					user.getField().showMessage(target.getName() + " hung on with its Sticky Hold!");
				}

				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Trick",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasSubstitute()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}

				if ((target.hasAbility("Sticky Hold")) || (user.hasAbility("Sticky Hold"))) {
					if (user.hasAbility("Sticky Hold")) {
						user.getField().showMessage(user.getName() + " hung on with its Sticky Hold!");
					}
					if (target.hasAbility("Sticky Hold")) {
						user.getField().showMessage(target.getName() + " hung on with its Sticky Hold!");
					}
					return 0;
				}

				HoldItem targetItem = target.getItem();
				HoldItem item = user.getItem();
				/**if (item == null) {
                        user.getField().showMessage("But it failed!");
                        return 0;
                    }**/

				HoldItem userItem = (item == null) ? null : (HoldItem)item.clone();
				user.setItem(targetItem);
				target.setItem(userItem);
				if (targetItem != null) {
					user.getField().showMessage(user.getName() + " obtained " + targetItem.getName() + "!");
				}
				if (userItem != null) {
					user.getField().showMessage(target.getName() + " obtained " + userItem.getName() + "!");
				}
				return 0;
			}
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return false;
			}
		}
		));

		m_moves.add(new MoveListEntry("Dig", new StatusMove(
				PokemonType.T_GROUND, 0, 1.0, 10, new StatusEffect[] {
						new InvulnerableStateEffect(new String[] { "Earthquake", "Fissure", "Magnitude" } ),
						new ChargeEffect(1, "dug a hole!", new MoveListEntry(
								"Dig",
								new PokemonMove(
										PokemonType.T_GROUND, 60, 1.0, 10
								) {
									public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
										setPower(mech instanceof JewelMechanics ? 80 : 60);
										return super.use(mech, user, target);
									}
								}
						)
						)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)   {
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Fly", new StatusMove(
				PokemonType.T_FLYING, 0, 0.95, 15, new StatusEffect[] {
						new InvulnerableStateEffect(new String[] { "Thunder", "Twister", "Gust", "Sky Uppercut" } ),
						new ChargeEffect(1, "flew up high!", new MoveListEntry(
								"Fly",
								new PokemonMove(
										PokemonType.T_FLYING, 70, 0.95, 15
								)
						)
						)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)   {
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Dive", new StatusMove(
				PokemonType.T_WATER, 0, 1.0, 10, new StatusEffect[] {
						new InvulnerableStateEffect(new String[] { "Surf" } ),
						new ChargeEffect(1, "dove underwater!", new MoveListEntry(
								"Dive",
								new PokemonMove(
										PokemonType.T_WATER, 60, 1.0, 10
								)
						)
						)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)   {
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Bounce", new StatusMove(
				PokemonType.T_FLYING, 0, 0.85, 5, new StatusEffect[] {
						new InvulnerableStateEffect(new String[0]),
						new ChargeEffect(1, "bounced up!", new MoveListEntry(
								"Bounce",
								new StatusMove(
										PokemonType.T_FLYING, 85, 0.85, 5, new StatusEffect[] {
												new ParalysisEffect()
										},
										new boolean[] { false },
										new double[] { 0.3 }
								)
						)
						)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)   {
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Ingrain", new StatusMove(
				PokemonType.T_GRASS, 0, 1.0, 20, new StatusEffect[] {
						new IngrainEffect()
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Magnitude",
				new PokemonMove(PokemonType.T_GROUND, 0, 1.0, 30) {
			public boolean isAttack() {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				double chance = mech.getRandom().nextDouble();
				int power;
				int magnitude;
				if (chance <= 0.05) {
					power = 10;
					magnitude = 4;
				} else if (chance <= 0.15) {
					power = 30;
					magnitude = 5;
				} else if (chance <= 0.35) {
					power = 50;
					magnitude = 6;
				} else if (chance <= 0.65) {
					power = 70;
					magnitude = 7;
				} else if (chance <= 0.85) {
					power = 90;
					magnitude = 8;
				} else if (chance <= 0.95) {
					power = 110;
					magnitude = 9;
				} else {
					power = 150;
					magnitude = 10;
				}
				user.getField().showMessage("Magnitude " + magnitude + "!");
				setPower(power);
				int damage = mech.calculateDamage(this, user, target);
				setPower(0);
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Bind", new StatusMove(
				PokemonType.T_NORMAL, 15, 0.75, 20, new StatusEffect[] {
						new RestrainingEffect("Bind", "bound")
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Clamp", new StatusMove(
				PokemonType.T_WATER, 35, 0.75, 10, new StatusEffect[] {
						new RestrainingEffect("Clamp", "clamped")
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Fire Spin", new StatusMove(
				PokemonType.T_FIRE, 15, 0.70, 15, new StatusEffect[] {
						new RestrainingEffect("Fire Spin", "trapped in a vortex")
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Sand Tomb", new StatusMove(
				PokemonType.T_GROUND, 15, 0.70, 10, new StatusEffect[] {
						new RestrainingEffect("Sand Tomb", "trapped in a vortex")
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Whirlpool", new StatusMove(
				PokemonType.T_WATER, 15, 0.70, 15, new StatusEffect[] {
						new RestrainingEffect("Whirlpool", "trapped in a vortex")
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Wrap", new StatusMove(
				PokemonType.T_NORMAL, 15, 0.85, 20, new StatusEffect[] {
						new RestrainingEffect("Wrap", "wrapped")
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Block", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 5, new StatusEffect[] {
						new TrappingEffect("Block")
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Mean Look", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 5, new StatusEffect[] {
						new TrappingEffect("Mean Look")
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Spider Web", new StatusMove(
				PokemonType.T_BUG, 0, 1.0, 5, new StatusEffect[] {
						new TrappingEffect("Spider Web")
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Yawn", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 10, new StatusEffect[] {
						new StatusEffect() {
							private int m_turns = 1;
							public String getName() {
								return "Yawn";
							}
							public String getDescription() {
								return " became drowsy!";
							}
							public int getTier() {
								return 5;
							}
							public boolean hitsThroughSubstitute() {
								return false;
							}
							public boolean isPassable() {
								/** According to the smogon research thread,
								 *  this is not passed. This was also confirmed
								 *  was an additional in game test.
								 */
								return false;
							}
							public boolean tick(Pokemon p) {
								if (m_turns-- <= 0) {
									p.addStatus(p.getOpponent(), new SleepEffect() {
										public boolean hitsThroughSubstitute() {
											return true;
										}
									});
									p.removeStatus(this);
									return false;
								}
								return true;
							}
						}
				},
				new boolean[] { false },
				new double[] { 1.0 }
		) {
			public boolean attemptHit(BattleMechanics mech, Pokemon source, Pokemon target) {
				return true;
			}
		}));

		m_moves.add(new MoveListEntry("Skill Swap",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				IntrinsicAbility userAbility = (IntrinsicAbility)user.getAbility().clone();
				IntrinsicAbility targetAbility = target.getAbility();
				if (!userAbility.isSwappable() || !targetAbility.isSwappable()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				user.setAbility(targetAbility, false);
				target.setAbility(userAbility, false);
				user.getField().showMessage(user.getName() + " swapped abilities with its opponent!");
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Role Play",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				IntrinsicAbility abl = target.getAbility();
				if (!abl.isSwappable()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				user.setAbility(abl, false);
				user.getField().showMessage(user.getName() + " copied its opponent's ability!");
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Psych Up",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.removeStatus(StatChangeEffect.class);

				List<?> statuses = target.getNormalStatuses(0);
				Iterator<?> i = statuses.iterator();
				while (i.hasNext()) {
					StatusEffect effect = (StatusEffect)i.next();
					if (effect instanceof StatChangeEffect) {
						StatChangeEffect effectClone = (StatChangeEffect)effect.clone();
						effectClone.setDescription(null);
						// TODO: Open question: does Clear Body protect
						// against lowering stats by using this move?
								user.addStatus(target, effectClone);
					}
				}
				user.getField().showMessage(user.getName() + " copied the opponent's stat changes!");
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Hyper Beam", new StatusMove(
				PokemonType.T_NORMAL, 150, 0.9, 5, new StatusEffect[] {
						new RechargeEffect(1)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Frenzy Plant", new StatusMove(
				PokemonType.T_GRASS, 150, 0.9, 5, new StatusEffect[] {
						new RechargeEffect(1)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Blast Burn", new StatusMove(
				PokemonType.T_FIRE, 150, 0.9, 5, new StatusEffect[] {
						new RechargeEffect(1)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Hydro Cannon", new StatusMove(
				PokemonType.T_WATER, 150, 0.9, 5, new StatusEffect[] {
						new RechargeEffect(1)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Spikes",
				new PokemonMove(PokemonType.T_GROUND, 0, 1.0, 20) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				BattleField field = user.getField();
				SpikesEffect spikes = SpikesEffect.getSpikes(field, SpikesEffect.class);
				if (spikes == null) {
					spikes = new SpikesEffect();
					field.applyEffect(spikes);
				}
				spikes.addSpikes(target);
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Rapid Spin",
				new PokemonMove(PokemonType.T_NORMAL, 20, 1.0, 40) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (getEffectiveness(user, target) == 0.0) {
					user.getField().showMessage("It doesn't affect "
							+ target.getName() + "...");
					return 0;
				}
				if (user.hasEffect(RestrainingEffect.class) || user.hasEffect(LeechSeedEffect.class)) {
					user.removeStatus(RestrainingEffect.class);
					user.removeStatus(LeechSeedEffect.class);
					user.getField().showMessage(user.getName() + " was released!");
				}
				ArrayList<FieldEffect> spikes = user.getField().getEffectsByType(SpikesEffect.class);
				Iterator<FieldEffect> i = spikes.iterator();
				boolean blewAway = false;
				while (i.hasNext()) {
					SpikesEffect eff = (SpikesEffect)i.next();
					if (eff != null && eff.getLayers(user) >= 1) {
						eff.removeSpikes(user);
						blewAway = true;
					}
				}
				if (blewAway) {
					user.getField().showMessage(user.getName() + " blew away the spikes!");
				}
				return super.use(mech, user, target);                  
			}
		}
		));

		m_moves.add(new MoveListEntry("Charge", new StatusMove(
				PokemonType.T_ELECTRIC, 0, 1.0, 20, new StatusEffect[] {
						new StatusEffect() {
							private int m_turns = 2;
							public String getName() {
								return "Charge";
							}
							public boolean apply(Pokemon p) {
								if (p.getField().getMechanics() instanceof JewelMechanics) {
									p.addStatus(p, new StatChangeEffect(Pokemon.S_SPDEFENCE, true, 1));
								}
								return true;
							}
							//tier is not very important
							public int getTier() {
								return 1;
							}
							public String getDescription() {
								return " is charging power!";
							}
							public boolean tick(Pokemon p) {
								if (--m_turns <= 0) {
									p.removeStatus(this);
									return true;
								}
								return false;
							}
							public boolean isMoveTransformer(boolean enemy) {
								return !enemy;
							}
							public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
								PokemonMove move = entry.getMove();
								if (move.getType().equals(PokemonType.T_ELECTRIC)) {
									int power = move.getPower();
									move.setPower(power * 2);
								}
								return entry;
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Rest",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 10) {
			public boolean attemptHit(BattleMechanics mech, Pokemon source, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (user.hasAbility("Insomnia")
						|| user.hasAbility("Vital Spirit")
						|| user.hasEffect(SleepEffect.class)
						|| (user.getHealth() == user.getStat(Pokemon.S_HP))) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				user.removeStatus(StatusEffect.SPECIAL_EFFECT_LOCK);
				// The turn it is applied still counts as a turn
				user.addStatus(user, new SleepEffect(3));                    
				int change = user.getStat(Pokemon.S_HP) - user.getHealth();
				user.changeHealth(change);
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Reflect",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (!user.getField().applyEffect(new StatCutEffect(Pokemon.S_DEFENCE, 
						user.getParty(), "reflect"))) {
					user.getField().showMessage("But it failed!");
				}
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Light Screen",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (!user.getField().applyEffect(new StatCutEffect(Pokemon.S_SPDEFENCE, 
						user.getParty(), "light screen"))) {
					user.getField().showMessage("But it failed!");
				}
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Brick Break",
				new PokemonMove(PokemonType.T_FIGHTING, 75, 1.0, 15) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				ArrayList<FieldEffect> effects = user.getField().getEffectsByType(StatCutEffect.class);
				Iterator<FieldEffect> i = effects.iterator();
				while (i.hasNext()) {
					StatCutEffect eff = (StatCutEffect)i.next();
					if (eff.getParty() == target.getParty()) {
						user.getField().removeEffect(eff);
						user.getField().showMessage("The wall shattered!");
					}
				}
				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Camouflage", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 20, new StatusEffect[] {
						new StatusEffect() {
							private PokemonType[] types;

							public String getName() {
								return "Camouflage";
							}
							public String getDescription() {
								return null;
							}
							public int getTier() {
								return -1;
							}
							public boolean tick(Pokemon p) {
								return false;
							}
							public boolean apply(Pokemon p) {
								types = p.getTypes();

								//todo: this should be based on the terrain but it isn't implemented yet
								PokemonType type = PokemonType.T_NORMAL;

								p.setType(new PokemonType[] { type });
								p.getField().showMessage(p.getName() + " became the "
										+ type + " type!");
								return true;
							}
							public boolean switchOut(Pokemon p) {
								p.setType(types);
								return true;
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}));

		m_moves.add(new MoveListEntry("Nature Power",
				new PokemonMove(PokemonType.T_NORMAL, 0, 0.95, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				//todo: this should also be based on terrain
				if (mech instanceof JewelMechanics) {
					return user.useMove(getMove("Swift"), target);
				} else {
					return user.useMove(getMove("Tri Attack"), target);
				}
			}
			public boolean isAttack() {
				return false;
			}
		}
		));

		m_moves.add(new MoveListEntry("Secret Power",
				new PokemonMove(PokemonType.T_NORMAL, 70, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage = mech.calculateDamage(this, user, target);
				target.changeHealth(-damage);

				double random = mech.getRandom().nextDouble();
				//todo: this should also be based on terrain
				if (random <= 0.3) {
					target.addStatus(user, new ParalysisEffect());
				}
				return damage;
			}
		}
		));

		//Doesn't do anything in 1 vs 1
		m_moves.add(new MoveListEntry("Follow Me",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().showMessage(user.getName() + " became the centre of attention!");
				return 0;
			}
			public int getPriority() {
				return 4;
			}
		}
		));

		//Doesn't do anything in 1 vs 1
		m_moves.add(new MoveListEntry("Helping Hand",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().showMessage("But it failed!");
				return 0;
			}
			public int getPriority() {
				return 6;
			}
		}
		));

		m_moves.add(new MoveListEntry("Doom Desire",
				new PokemonMove(PokemonType.T_STEEL, 120, 0.85, 5) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage;
				if (!super.attemptHit(mech, user, target)) {
					damage = 0;
				} else {
					PokemonMove move = new PokemonMove(PokemonType.T_TYPELESS, this.getPower(), 1.0, 1);
					move.setMoveListEntry(getMoveListEntry());
					damage = mech.calculateDamage(move, user, target);
				}

				user.getField().applyEffect(
						new DelayedDamageEffect(damage, target.getParty(), 3));
				return 0;                    
			}
		}
		));

		m_moves.add(new MoveListEntry("Future Sight",
				new PokemonMove(PokemonType.T_PSYCHIC, 80, 0.9, 15) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage;
				if (!super.attemptHit(mech, user, target)) {
					damage = 0;
				} else {
					damage = mech.calculateDamage(
							new PokemonMove(PokemonType.T_TYPELESS, this.getPower(), 1.0, 1), user, target);
				}

				user.getField().applyEffect(
						new DelayedDamageEffect(damage, target.getParty(), 3));
				return 0;                    
			}
		}
		));

		m_moves.add(new MoveListEntry("Focus Punch",
				new DamageListenerMove(PokemonType.T_FIGHTING, 150, 1.0, 20) {           
			public void beginTurn(BattleTurn[] turn, int index, Pokemon source) {
				if (!source.hasEffect(SleepEffect.class) && !source.hasEffect(FreezeEffect.class)) {
					source.getField().showMessage(source.getName() + " is tightening its focus!");
				}
				super.beginTurn(turn, index, source);
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				DamageListenerEffect listener = getListener(user);
				if (listener == null) return 0;
				if (listener.getDamage() > 0) {
					user.getField().showMessage(user.getName() + " lost its focus and couldn't move!");
					return 0;
				}
				return super.use(mech, user, target);
			}
			public int getPriority() {
				return -2;
			}
		}
		));

		m_moves.add(new MoveListEntry("Low Kick",
				new MassBasedMove(PokemonType.T_FIGHTING, 1.0, 20)));

		m_moves.add(new MoveListEntry("Baton Pass",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 40) {
			@SuppressWarnings("unchecked")
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				BattleField field = user.getField();
				int party = user.getParty();
				if (field.getAliveCount(party) == 1) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				List<StatusEffect> list = user.getNormalStatuses(0);
				List applied = new ArrayList();
				Iterator<StatusEffect>  i = list.iterator();
				while (i.hasNext()) {
					StatusEffect effect = (StatusEffect)i.next();
					if (effect.getLock() != StatusEffect.SPECIAL_EFFECT_LOCK) {
						if (effect.isPassable()) {
							if (effect instanceof LeechSeedEffect) {
								applied.add(new LeechSeedEffect(false));
							} else if (effect instanceof PerishSongEffect) {
								applied.add(new PerishSongEffect(false));
							} else {
								applied.add(effect.clone());
							}
							if (effect instanceof CoEffect) {
								CoEffect coeffect = (CoEffect)effect;
								if (!coeffect.getType().equals(AttractEffect.class)) {
									effect.disable();
								}
							}
						}
					}
				}
				int substitute = user.getSubstitute();
				field.requestAndWaitForSwitch(party);
				target = field.getActivePokemon()[party];
				target.setSubstitute(substitute);
				i = applied.iterator();
				field.setNarrationEnabled(false);
				while (i.hasNext()) {
					StatusEffect effect = (StatusEffect)i.next();
					target.addStatus(target, effect);
				}
				field.setNarrationEnabled(true);
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon source, Pokemon target) {
				return true;
			}
		}));

		PokemonMove roar = new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasAbility("Suction Cups") || target.hasEffect(IngrainEffect.class)) {
					user.getField().showMessage("But it failed!");
					return 0;
				}

				if (target.isFainted())
					return 0;

				ArrayList<Pokemon> party = new ArrayList<Pokemon>(Arrays.asList(target.getTeammates()));
				Iterator<Pokemon> i = party.iterator();
				while (i.hasNext()) {
					Pokemon p = (Pokemon)i.next();
					if (p == null || p.isFainted() || (p == target)) {
						i.remove();
					}
				}
				if (party.size() == 0) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				Pokemon p = (Pokemon)party.get(mech.getRandom().nextInt(party.size()));
				p.getField().switchInPokemon(p.getParty(), p.getId());
				p.addStatus(user, new StatusEffect() {
					public int getTier() {
						return 1;
					} 
					public boolean tick(Pokemon p) {
						p.removeStatus(this);
						return true;
					}
					public boolean isMoveTransformer(boolean enemy) {
						return !enemy;
					}
					public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
						return null;
					}
					public String getDescription() {
						return null;
					}
					public String getName() {
						return null;
					}
				});
				return 0;
			}
			public int getPriority() {
				return -5;
			}

		};

		PokemonMove whirlwind = (PokemonMove)roar.clone();
		m_moves.add(new MoveListEntry("Roar", roar));
		m_moves.add(new MoveListEntry("Whirlwind", whirlwind));

		m_moves.add(new MoveListEntry("Wish",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().applyEffect(new DelayedStatusEffect(
						new PercentEffect(0.5, false, -1, "The wish came true!"), 
						user.getParty(), 2, " made a wish!"));
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Counter",
				new CounterMove(PokemonType.T_FIGHTING, 1.0, 20, 2)));
		m_moves.add(new MoveListEntry("Mirror Coat",
				new CounterMove(PokemonType.T_PSYCHIC, 1.0, 20, 1)));

		m_moves.add(new MoveListEntry("Pursuit",
				new PokemonMove(PokemonType.T_DARK, 40, 1.0, 20) {
			public void beginTurn(BattleTurn[] turn, int index, Pokemon source) {
				// Note: assumes two pokemon.
				BattleTurn opp = turn[1 - index];
				Pokemon target = source.getOpponent();
				boolean damageNow = false;
				if (!opp.isMoveTurn()) {
					damageNow = true;
				} else {
					MoveListEntry entry = target.getMove(opp.getId());
					if (entry.getName().equals("U-turn")) {
						if (target.getStat(Pokemon.S_SPEED) > source.getStat(Pokemon.S_SPEED)) {
							damageNow = true;
						}
					}
				}

				if (!damageNow)
					return;

				// Prevent this attack from occurring later in the turn.
				turn[index] = null;

				if (source.isImmobilised(null))
					return;

				int power = getPower();
				setPower(power * 2);
				source.useMove(new MoveListEntry("Pursuit", (PokemonMove)clone()), target);
				setPower(power);
			}
		}
		));

		m_moves.add(new MoveListEntry("Revenge",
				new DamageListenerMove(PokemonType.T_FIGHTING, 60, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				DamageListenerEffect listener = getListener(user);
				if (listener == null) return 0;
				if (listener.getDamage() <= 0) {
					return super.use(mech, user, target);
				}
				int power = 60;
				setPower(power * 2);
				int damage = super.use(mech, user, target);
				setPower(power);
				return damage;
			}
			public int getPriority() {
				return -3;
			}
		}
		));

		/********************************************************************
		 * DP moves start
		 ********************************************************************/

		m_moves.add(new MoveListEntry("U-turn",
				new PokemonMove(PokemonType.T_BUG, 70, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage = mech.calculateDamage(this, user, target);
				target.changeHealth(-damage);

				int party = user.getParty();
				BattleField field = user.getField();
				if (field.getAliveCount(party) > 1) {
					field.requestAndWaitForSwitch(party);
					target = field.getActivePokemon()[party];
					HoldItem item = target.getItem();
					if (item instanceof ChoiceBandItem) {
						for (int i = 0; i < 4; ++i) {
							MoveListEntry entry = target.getMove(i);
							if ((entry != null) && entry.getName().equals("U-turn")) {
								((ChoiceBandItem)item).setChoice(target, mech, entry);
								break;
							}
						}
					}
				}
				return damage;
			}
		}));

		m_moves.add(new MoveListEntry("Lunar Dance",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int party = user.getParty();
				BattleField field = user.getField();
				if (field.getAliveCount(party) == 1) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				user.faint();
				field.requestAndWaitForSwitch(party);
				target = field.getActivePokemon()[party];
				target.changeHealth(target.getStat(Pokemon.S_HP));
				for (int i = 0; i < 4; ++i) {
					MoveListEntry entry = target.getMove(i);
					if (entry != null) {
						PokemonMove move = entry.getMove();
						if (move != null) {
							target.setPp(i, target.getMaxPp(i));
						}
					}
				}
				target.removeStatus(StatusEffect.SPECIAL_EFFECT_LOCK);
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon source, Pokemon target) {
				return true;
			}
		}));

		m_moves.add(new MoveListEntry("Worry Seed",
				new PokemonMove(PokemonType.T_GRASS, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasAbility("Multitype") || target.hasSubstitute()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				target.setAbility(IntrinsicAbility.getInstance("Insomnia"), false);
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Psycho Shift",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasSubstitute()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				StatusEffect eff = user.getEffect(StatusEffect.SPECIAL_EFFECT_LOCK);
				if (eff == null)
					return 0;
				StatusEffect clone = (StatusEffect)eff.clone();
				if (!target.hasEffect(StatusEffect.SPECIAL_EFFECT_LOCK)) {
					user.removeStatus(eff);
					target.addStatus(user, clone);
				}
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Trick Room",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 5) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				BattleField field = user.getField();
				FieldEffect effect = field.getEffectByType(SpeedSwapEffect.class);
				if (effect != null) {
					field.removeEffect(effect);
				} else {
					field.showMessage(user.getName() + " twisted the dimensions!");
					field.applyEffect(new SpeedSwapEffect());
				}
				return 0;
			}
			public int getPriority() {
				return -5;
			}
		}
		));

		m_moves.add(new MoveListEntry("Gyro Ball",
				new PokemonMove(PokemonType.T_STEEL, 100, 1.0, 5) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int power = 51 * target.getStat(Pokemon.S_SPEED) /
				user.getStat(Pokemon.S_SPEED) / 2;
				if (power > 150) power = 150;
				setPower(getPower() * power / 100);
				int damage = mech.calculateDamage(this, user, target);
				target.changeHealth(-damage);
				setPower(100);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Wake-up Slap",
				new PokemonMove(PokemonType.T_FIGHTING, 60, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int power = getPower();
				if (!target.hasSubstitute() && target.hasEffect(SleepEffect.class)) {
					setPower(2 * power);
					target.removeStatus(StatusEffect.SPECIAL_EFFECT_LOCK);
					user.getField().showMessage(target.getName() + " woke up!");
				}
				int damage = mech.calculateDamage(this, user, target);
				setPower(power);
				target.changeHealth(-damage);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Hammer Arm", new StatusMove(
				PokemonType.T_FIGHTING, 100, 0.9, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, false)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Brine",
				new PokemonMove(PokemonType.T_WATER, 65, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int power = getPower();
				if (target.getHealth() < (target.getStat(Pokemon.S_HP) / 2)) {
					setPower(power * 2);
				}
				int damage = super.use(mech, user, target);
				setPower(power);
				return damage;
			}
		}
		));

		m_moves.add(new MoveListEntry("Acupressure",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 30) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int random = mech.getRandom().nextInt(7) + 1;
				user.addStatus(user, new StatChangeEffect(random, true, 2));
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Close Combat", new StatusMove(
				PokemonType.T_FIGHTING, 120, 1.0, 5, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, false),
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)));

		//todo: get right tier    
		m_moves.add(new MoveListEntry("Aqua Ring", new StatusMove(
				PokemonType.T_WATER, 0, 1.0, 20, new StatusEffect[] {
						new PercentEffect(0.0625, false, 3, "'s Aqua Ring restored health!") {
							public boolean isSingleton() {
								return true;
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		//todo: find correct rate for all of these
		m_moves.add(new MoveListEntry("Flare Blitz",
				new RecoilMove(PokemonType.T_FIRE, 120, 1.0, 15, 1.0/3.0)));

		m_moves.add(new MoveListEntry("Brave Bird",
				new RecoilMove(PokemonType.T_FLYING, 120, 1.0, 15, 1.0/3.0)));

		m_moves.add(new MoveListEntry("Wood Hammer",
				new RecoilMove(PokemonType.T_GRASS, 120, 1.0, 15, 1.0/3.0)));

		m_moves.add(new MoveListEntry("Head Smash",
				new RecoilMove(PokemonType.T_ROCK, 150, 0.8, 5, 0.5)));

		m_moves.add(new MoveListEntry("Force Palm", new StatusMove(
				PokemonType.T_FIGHTING, 60, 1.0, 10, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Aura Sphere",
				new PerfectAccuracyMove(PokemonType.T_FIGHTING, 90, 20)));

		m_moves.add(new MoveListEntry("Magnet Bomb",
				new PerfectAccuracyMove(PokemonType.T_STEEL, 60, 20)));

		m_moves.add(new MoveListEntry("Rock Polish", new StatusMove(
				PokemonType.T_ROCK, 0, 1.0, 30, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPEED, true, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Poison Jab", new StatusMove(
				PokemonType.T_POISON, 80, 1.0, 20, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Dark Pulse", new StatusMove(
				PokemonType.T_DARK, 80, 1.0, 15, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.2 }
		)));

		m_moves.add(new MoveListEntry("Night Slash",
				new HighCriticalHitMove(PokemonType.T_DARK, 70, 1.0, 15)));

		m_moves.add(new MoveListEntry("Shadow Claw",
				new HighCriticalHitMove(PokemonType.T_GHOST, 70, 1.0, 15)));

		m_moves.add(new MoveListEntry("Psycho Cut",
				new HighCriticalHitMove(PokemonType.T_PSYCHIC, 70, 1.0, 20)));

		m_moves.add(new MoveListEntry("Stone Edge",
				new HighCriticalHitMove(PokemonType.T_ROCK, 100, 0.8, 5)));

		m_moves.add(new MoveListEntry("Attack Order",
				new HighCriticalHitMove(PokemonType.T_BUG, 90, 1.0, 15)));

		m_moves.add(new MoveListEntry("Spacial Rend",
				new HighCriticalHitMove(PokemonType.T_DRAGON, 100, 0.95, 5)));

		m_moves.add(new MoveListEntry("Aqua Tail",
				new PokemonMove(PokemonType.T_WATER, 90, 0.9, 10)));

		m_moves.add(new MoveListEntry("Seed Bomb",
				new PokemonMove(PokemonType.T_GRASS, 80, 1.0, 15)));

		m_moves.add(new MoveListEntry("X-Scissor",
				new PokemonMove(PokemonType.T_BUG, 80, 1.0, 15)));

		m_moves.add(new MoveListEntry("Dragon Pulse",
				new PokemonMove(PokemonType.T_DRAGON, 90, 1.0, 10)));

		m_moves.add(new MoveListEntry("Power Gem",
				new PokemonMove(PokemonType.T_ROCK, 70, 1.0, 20)));

		m_moves.add(new MoveListEntry("Power Whip",
				new PokemonMove(PokemonType.T_GRASS, 120, 0.85, 10)));

		m_moves.add(new MoveListEntry("Air Slash", new StatusMove(
				PokemonType.T_FLYING, 75, 0.95, 20, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Zen Headbutt", new StatusMove(
				PokemonType.T_PSYCHIC, 80, 0.9, 15, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.2 }
		)));

		m_moves.add(new MoveListEntry("Dragon Rush", new StatusMove(
				PokemonType.T_DRAGON, 100, 0.75, 10, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.2 }
		)));

		m_moves.add(new MoveListEntry("Iron Head", new StatusMove(
				PokemonType.T_STEEL, 80, 1.0, 15, new StatusEffect[] {
						new FlinchEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Bug Buzz", new StatusMove(
				PokemonType.T_BUG, 90, 1.0, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Drain Punch",
				new AbsorbMove(PokemonType.T_FIGHTING, 60, 1.0, 5, 0.5)));

		m_moves.add(new MoveListEntry("Vacuum Wave",
				new PriorityMove(PokemonType.T_FIGHTING, 40, 1.0, 30, 1)));

		m_moves.add(new MoveListEntry("Bullet Punch",
				new PriorityMove(PokemonType.T_STEEL, 40, 1.0, 30, 1)));

		m_moves.add(new MoveListEntry("Ice Shard",
				new PriorityMove(PokemonType.T_ICE, 40, 1.0, 30, 1)));

		m_moves.add(new MoveListEntry("Shadow Sneak",
				new PriorityMove(PokemonType.T_GHOST, 40, 1.0, 30, 1)));

		m_moves.add(new MoveListEntry("Aqua Jet",
				new PriorityMove(PokemonType.T_WATER, 40, 1.0, 30, 1)));

		m_moves.add(new MoveListEntry("Focus Blast", new StatusMove(
				PokemonType.T_FIGHTING, 120, 0.7, 5, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Energy Ball", new StatusMove(
				PokemonType.T_GRASS, 80, 1.0, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Earth Power", new StatusMove(
				PokemonType.T_GROUND, 90, 1.0, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Mirror Shot", new StatusMove(
				PokemonType.T_STEEL, 65, 0.85, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ACCURACY, false)
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Flash Cannon", new StatusMove(
				PokemonType.T_STEEL, 80, 1.0, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, false)
				},
				new boolean[] { false },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Rock Climb", new StatusMove(
				PokemonType.T_NORMAL, 90, 0.85, 20, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 0.2 }
		)));

		m_moves.add(new MoveListEntry("Switcheroo",
				new PokemonMove(PokemonType.T_DARK, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasSubstitute()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				if ((target.hasAbility("Sticky Hold")) || (user.hasAbility("Sticky Hold"))) {
					if (user.hasAbility("Sticky Hold")) {
						user.getField().showMessage(user.getName() + " hung on with its Sticky Hold!");
					}
					if (target.hasAbility("Sticky Hold")) {
						user.getField().showMessage(target.getName() + " hung on with its Sticky Hold!");
					}
					return 0;
				}

				HoldItem targetItem = target.getItem();
				HoldItem item = user.getItem();
				/**if (item == null) {
                        user.getField().showMessage("But it failed!");
                        return 0;
                    }**/

				HoldItem userItem = (item == null) ? null : (HoldItem)item.clone();
				user.setItem(targetItem);
				target.setItem(userItem);
				if (targetItem != null) {
					user.getField().showMessage(user.getName() + " obtained " + targetItem.getName() + "!");
				}
				if (userItem != null) {
					user.getField().showMessage(target.getName() + " obtained " + userItem.getName() + "!");
				}
				return 0;
			}
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return false;
			}
		}
		));

		m_moves.add(new MoveListEntry("Giga Impact", new StatusMove(
				PokemonType.T_NORMAL, 150, 0.9, 5, new StatusEffect[] {
						new RechargeEffect(1)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Rock Wrecker", new StatusMove(
				PokemonType.T_ROCK, 150, 0.9, 5, new StatusEffect[] {
						new RechargeEffect(1)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Roar Of Time", new StatusMove(
				PokemonType.T_DRAGON, 150, 0.9, 5, new StatusEffect[] {
						new RechargeEffect(1)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Nasty Plot", new StatusMove(
				PokemonType.T_DARK, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, true, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		//todo: correct probabilities?
		m_moves.add(new MoveListEntry("Thunder Fang", new StatusMove(
				PokemonType.T_ELECTRIC, 65, 0.95, 15, new StatusEffect[] {
						new FlinchEffect(),
						new ParalysisEffect()
				},
				new boolean[] { false, false },
				new double[] { 0.1, 0.1 }
		)));

		m_moves.add(new MoveListEntry("Ice Fang", new StatusMove(
				PokemonType.T_ICE, 65, 0.95, 15, new StatusEffect[] {
						new FlinchEffect(),
						new FreezeEffect()
				},
				new boolean[] { false, false },
				new double[] { 0.1, 0.1 }
		)));

		m_moves.add(new MoveListEntry("Fire Fang", new StatusMove(
				PokemonType.T_FIRE, 65, 0.95, 15, new StatusEffect[] {
						new FlinchEffect(),
						new BurnEffect()
				},
				new boolean[] { false, false },
				new double[] { 0.1, 0.1 }
		)));

		m_moves.add(new MoveListEntry("Mud Bomb", new StatusMove(
				PokemonType.T_GROUND, 65, 0.85, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_ACCURACY, false)
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Defog", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 15, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_EVASION, false)
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Draco Meteor", new StatusMove(
				PokemonType.T_DRAGON, 140, 0.90, 5, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, false, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Leaf Storm", new StatusMove(
				PokemonType.T_GRASS, 140, 0.90, 5, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, false, 2)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Discharge", new StatusMove(
				PokemonType.T_ELECTRIC, 80, 1.0, 15, new StatusEffect[] {
						new ParalysisEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Lava Plume", new StatusMove(
				PokemonType.T_FIRE, 80, 1.0, 15, new StatusEffect[] {
						new BurnEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Cross Poison",
				new StatusMove(PokemonType.T_POISON, 70, 1.0, 20, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 0.1 }
				) {
			public boolean hasHighCriticalHitRate() { return true; }
		}
		));

		m_moves.add(new MoveListEntry("Gunk Shot", new StatusMove(
				PokemonType.T_POISON, 120, 0.7, 5, new StatusEffect[] {
						new PoisonEffect()
				},
				new boolean[] { false },
				new double[] { 0.3 }
		)));

		m_moves.add(new MoveListEntry("Captivate",
				new StatusMove(PokemonType.T_NORMAL, 0, 1.0, 20, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, false, 2)
				},
				new boolean[] { false },
				new double[] { 1.0 }
				) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int userGender = user.getGender();
				int targetGender = target.getGender();
				if ((userGender == targetGender) || (userGender == PokemonSpecies.GENDER_NONE)
						|| (targetGender == PokemonSpecies.GENDER_NONE)) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Chatter", new StatusMove(
				PokemonType.T_FLYING, 60, 1.0, 20, new StatusEffect[] {
						new ConfuseEffect()
				},
				new boolean[] { false },
				new double[] { 0.31 }
		)));

		m_moves.add(new MoveListEntry("Charge Beam", new StatusMove(
				PokemonType.T_ELECTRIC, 50, 0.9, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPATTACK, true)
				},
				new boolean[] { true },
				new double[] { 0.7 }
		)));

		m_moves.add(new MoveListEntry("Defend Order", new StatusMove(
				PokemonType.T_BUG, 0, 1.0, 10, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_DEFENCE, true),
						new StatChangeEffect(Pokemon.S_SPDEFENCE, true)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Substitute",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (!user.createSubstitute()) {
					user.getField().showMessage("But it failed!");
				} else {
					user.addStatus(user, new SubstituteEffect());
				}
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}));

		m_moves.add(new MoveListEntry("Heal Order", new StatusMove(
				PokemonType.T_BUG, 0, 1.0, 10, new StatusEffect[] {
						new PercentEffect(0.5, false, -1, null)
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Double Hit",
				new PokemonMove(PokemonType.T_NORMAL, 35, 0.9, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int damage = 0;
				for (int i = 0; i < 2; ++i) {
					final int partial = mech.calculateDamage(this, user, target);
					target.changeHealth(-partial);
					damage += partial;
				}
				user.getField().showMessage("Hit 2 time(s)!");
				return damage;
			}
		}
		));

		PokemonMove crushGrip = new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 5) {
			//todo: this formula may not be exactly correct
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int power = (int)(110.0 *
						(((double)target.getHealth())
								/ ((double)target.getStat(Pokemon.S_HP))));

				setPower(power);
				int damage = mech.calculateDamage(this, user, target);
				target.changeHealth(-damage);
				return damage;
			}
			public boolean isAttack() {
				return true;
			}
		};

		PokemonMove wringOut = (PokemonMove)crushGrip.clone();
		m_moves.add(new MoveListEntry("Crush Grip", crushGrip));
		m_moves.add(new MoveListEntry("Wring Out", wringOut));

		m_moves.add(new MoveListEntry("Feint",
				new PokemonMove(PokemonType.T_NORMAL, 50, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (!target.hasEffect(CounterEffect.class)) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				return super.use(mech, user, target);                    
			}
		}
		));

		m_moves.add(new MoveListEntry("Trump Card",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 5) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				//todo: these are estimated values for the powers
				int pp = 5;
				for (int i = 0; i < 4; ++i) {
					MoveListEntry entry = user.getMove(i);
					if (entry == null) continue;
					if (entry.getName().equals("Trump Card")) {
						pp = user.getPp(i);
						break;
					}
				}
				if (pp > 4) {
					setPower(35);
				} else if (pp == 4) {
					setPower(50);
				} else if (pp == 3) {
					setPower(60);
				} else if (pp == 2) {
					setPower(75);
				} else {
					setPower(190);
				}
				int damage = super.use(mech, user, target);
				setPower(0);
				return damage;
			}
			public boolean isAttack() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Punishment",
				new PokemonMove(PokemonType.T_DARK, 0, 1.0, 5) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int raises = 0;
				List<StatusEffect> statuses = target.getNormalStatuses(0);
				Iterator<StatusEffect> i = statuses.iterator();
				while (i.hasNext()) {
					StatusEffect effect = (StatusEffect)i.next();
					if (!(effect instanceof StatChangeEffect)) continue;
					if (((StatChangeEffect)effect).isRaise()) {
						raises++;
					}
				}
				setPower(60 + 20 * raises);
				int damage = super.use(mech, user, target);
				setPower(0);
				return damage;
			}
			public boolean isAttack() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Last Resort",
				new PokemonMove(PokemonType.T_NORMAL, 130, 1.0, 5) {
			class LastResortEffect extends StatusEffect {
				int[] m_pp;
				public LastResortEffect(int[] pp) {
					m_pp = pp;
				}
				public int getPp(int i) {
					return m_pp[i];
				}
			}
			public void switchIn(Pokemon p) {
				int[] pp = new int[4];
				for (int i = 0; i < 4; ++i) {
					pp[i] = p.getPp(i);
				}
				p.addStatus(p, new LastResortEffect(pp));
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				LastResortEffect effect = (LastResortEffect)
				user.getEffect(LastResortEffect.class);
				if (effect == null) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				boolean hasOtherMoves = false;
				for (int i = 0; i < 4; ++i) {
					MoveListEntry entry = user.getMove(i);
					if (entry == null) continue;
					if (!entry.getName().equals("Last Resort")) {
						hasOtherMoves = true;
						if (user.getPp(i) >= effect.getPp(i)) {
							hasOtherMoves = false;
							break;
						}
					}
				}
				if (!hasOtherMoves) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Magma Storm", new StatusMove(
				PokemonType.T_FIRE, 120, 0.70, 5, new StatusEffect[] {
						new RestrainingEffect("Magma Storm", "trapped in a vortex!")
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Dark Void", new StatusMove(
				PokemonType.T_DARK, 0, 0.8, 10, new StatusEffect[] {
						new SleepEffect()
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Seed Flare", new StatusMove(
				PokemonType.T_GRASS, 120, 0.85, 5, new StatusEffect[] {
						new StatChangeEffect(Pokemon.S_SPDEFENCE, false)
				},
				new boolean[] { false },
				new double[] { 0.4 }
		)));

		m_moves.add(new MoveListEntry("Protect", new ProtectMove(
				PokemonType.T_NORMAL, 10, new ProtectEffect() {
					public String getDescription() {
						return " protected itself!";
					}
				})));

		m_moves.add(new MoveListEntry("Endure", new ProtectMove(
				PokemonType.T_NORMAL, 10, new EndureEffect()
		)));

		m_moves.add(new MoveListEntry("Detect", new ProtectMove(
				PokemonType.T_FIGHTING, 5, new ProtectEffect() {
					public String getDescription() {
						return " braced itself!";
					}
				})));

		m_moves.add(new MoveListEntry("Taunt", new StatusMove(
				PokemonType.T_DARK, 0, 1.0, 20, new StatusEffect[] {
						new StatusEffect() {
							private int m_turns;
							public boolean apply(Pokemon p) {
								Random r = p.getField().getMechanics().getRandom();
								m_turns = r.nextInt(3) + 3;
								return true;
							}
							public String getDescription() {
								return " fell for the taunt!";
							}
							public String getName() {
								return "Taunt";
							}
							public int getTier() {
								return 1;
							}
							public boolean tick(Pokemon p) {
								if (--m_turns == 0) {
									p.removeStatus(this);
									p.getField().showMessage(p.getName() + "'s taunt wore off!");
									return true;
								}
								return false;
							}
							public boolean isMoveTransformer(boolean enemy) {
								return !enemy;
							}
							public boolean hitsThroughSubstitute() {
								// TODO: NOTE: Does not hit through in advance!
								return true;
							}
							public boolean vetoesMove(Pokemon p, MoveListEntry entry) {
								String name = entry.getName();
								if (name.equals("Struggle")) {
									return false;
								}
								HashSet<String> set = new HashSet<String>(Arrays.asList(new String[] {
										"Nature Power", "Sleep Talk", "Assist", "Metronome"
								}));
								if (set.contains(name)) {
									return true;
								}
								return !entry.getMove().isDamaging();
							}
							public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
								if (vetoesMove(p, entry)) {
									BattleField field = p.getField();
									String move = entry.getName();
									field.informUseMove(p, move);
									field.showMessage(p.getName() + " can't use " + move + " after the taunt!");
									return null;
								}
								return entry;
							}
						}
				},
				new boolean[] { false },
				new double[] { 1.0 }
		) {
			public boolean attemptHit(BattleMechanics mech, Pokemon source, Pokemon target) {
				return true;
			}
		}));

		m_moves.add(new MoveListEntry("Shadow Force", new StatusMove(
				PokemonType.T_GHOST, 0, 1.0, 5, new StatusEffect[] {
						new InvulnerableStateEffect(new String[0]),
						new ChargeEffect(1, "dissappeared from sight!", new MoveListEntry(
								"Shadow Force",
								new PokemonMove(PokemonType.T_GHOST, 120, 1.0, 5)
						)
						)
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)   {
			public boolean isAttack() {
				return true;
			}
			public boolean isDamaging() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Ominous Wind", new StatusMove(
				PokemonType.T_GHOST, 60, 1.0, 5, new StatusEffect[] {
						new MultipleStatChangeEffect(new int[] {
								Pokemon.S_ATTACK,
								Pokemon.S_DEFENCE,
								Pokemon.S_SPEED,
								Pokemon.S_SPATTACK,
								Pokemon.S_SPDEFENCE
						}
						)
				},
				new boolean[] { true },
				new double[] { 0.1 }
		)));

		m_moves.add(new MoveListEntry("Toxic Spikes",
				new PokemonMove(PokemonType.T_POISON, 0, 1.0, 20) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				BattleField field = user.getField();
				ToxicSpikesEffect spikes = (ToxicSpikesEffect)SpikesEffect.getSpikes(field, ToxicSpikesEffect.class);
				if (spikes == null) {
					spikes = new ToxicSpikesEffect();
					field.applyEffect(spikes);
				}
				spikes.addSpikes(target);
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Stealth Rock",
				new PokemonMove(PokemonType.T_ROCK, 0, 1.0, 20) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				BattleField field = user.getField();
				StealthRockEffect spikes = (StealthRockEffect)SpikesEffect.getSpikes(field, StealthRockEffect.class);
				if (spikes == null) {
					spikes = new StealthRockEffect();
					field.applyEffect(spikes);
				}
				spikes.addSpikes(target);
				return 0;
			}
		}
		));

		class MeFirstEffect extends StatusEffect {
			private MoveListEntry m_move;
			public MeFirstEffect(MoveListEntry move) {
				m_move = move;
			}
			public boolean isMoveTransformer(boolean enemy) {
				return !enemy;
			}
			public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
				p.getField().informUseMove(p, "Me First");
				return m_move;
			}
			public int getTier() {
				return 1;
			}
			public boolean tick(Pokemon p) {
				p.removeStatus(this);
				return true;
			}
			public String getName() {
				return null;
			}
			public String getDescription() {
				return null;
			}
		}

		m_moves.add(new MoveListEntry("Me First",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 20) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public void beginTurn(BattleTurn[] turn, int index, Pokemon source) {
				// Assume two pokemon
				/** Note: You cannot give PokemonMoves states
				 * outside of their nature as a move - there is only one
				 * copy for the whole program, not for each pokemon who
				 * has the move! The latter would be a massive waste of
				 * memory.
				 */
				if ((index == 1) || source.hasEffect(SleepEffect.class) || source.hasEffect(FreezeEffect.class)) {
					return;
				}
				BattleTurn opp = turn[1 - index];
				Pokemon target = source.getOpponent();
				if (!opp.isMoveTurn()) return;
				MoveListEntry entry = (MoveListEntry)target.getMove(opp.getId()).clone();
				PokemonMove move = entry.getMove();
				int power = move.getPower();
				if (!move.isDamaging()) return;
				move.setPower(power * 3 / 2);
				source.addStatus(source, new MeFirstEffect(entry));
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().showMessage("But it failed!");
				return 0;
			}
		}
		));

		class SuckerPunchEffect extends StatusEffect {
			public int getTier() {
				return 1;
			}
			public boolean tick(Pokemon p) {
				p.removeStatus(this);
				return true;
			}
			public String getName() {
				return null;
			}
			public String getDescription() {
				return null;
			}
		}

		m_moves.add(new MoveListEntry("Sucker Punch",
				new PokemonMove(PokemonType.T_DARK, 80, 1.0, 5) {
			public void beginTurn(BattleTurn[] turn, int index, Pokemon source) {
				// Assume two pokemon
				if (index == 1) {
					// User must be going first.
					return;
				}
				BattleTurn opp = turn[1 - index];
				if (opp.isMoveTurn() && opp.getMove(source.getOpponent()).isDamaging()) {
					source.addStatus(source, new SuckerPunchEffect());
				}
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (!user.hasEffect(SuckerPunchEffect.class)) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				return super.use(mech, user, target);
			}
			public int getPriority() {
				return 1;
			}
		}
		));

		class AssuranceEffect extends StatusEffect {
			private int m_health;
			public boolean apply(Pokemon p) {
				m_health = p.getOpponent().getHealth();
				return true;
			}
			public int getHealth() {
				return m_health;
			}
			public int getTier() {
				return 5;
			}
			public boolean tick(Pokemon p) {
				p.removeStatus(this);
				return true;
			}
		}

		m_moves.add(new MoveListEntry("Assurance",
				new PokemonMove(PokemonType.T_DARK, 50, 1.0, 10) { 
			public void beginTurn(BattleTurn[] turn, int index, Pokemon source) {
				source.getOpponent().addStatus(source, new AssuranceEffect());
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int power = getPower();
				if (!target.hasEffect(AssuranceEffect.class)) {
					StatusEffect effect = target.getEffect(AssuranceEffect.class);
					AssuranceEffect eff = (AssuranceEffect) effect;
					if (target.getHealth() < eff.getHealth()) {
						setPower(power * 2);
					}
					int damage = super.use(mech, user, target);
					setPower(power);
					return damage;
				} else {
					return super.use(mech, user, target);
				}
			}
		}
		));



		m_moves.add(new MoveListEntry("Judgement",
				new PokemonMove(PokemonType.T_NORMAL, 100, 1.0, 10)));

		m_moves.add(new MoveListEntry("Metal Burst",
				new CounterMove(PokemonType.T_STEEL, 1.0, 10, 3) {
			public int getPriority() {
				return 0;
			}
		}
		));

		DamageListenerMove payback = new DamageListenerMove(PokemonType.T_DARK, 50, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				DamageListenerEffect listener = getListener(user);
				if ((listener == null) || (listener.getDamage() <= 0)) {
					return super.use(mech, user, target);
				}
				int power = getPower();
				setPower(power * 2);
				int damage = super.use(mech, user, target);
				setPower(power);
				return damage;
			}
		};

		DamageListenerMove avalanche = (DamageListenerMove)payback.clone();
		avalanche.setType(PokemonType.T_ICE);
		avalanche.setPower(60);
		avalanche.setPriority(-3);
		m_moves.add(new MoveListEntry("Payback", payback));
		m_moves.add(new MoveListEntry("Avalanche", avalanche));

		m_moves.add(new MoveListEntry("Roost", new StatusMove(
				PokemonType.T_FLYING, 0, 1.0, 10, new StatusEffect[] {
						new StatusEffect() {
							private PokemonType[] m_types;
							public boolean apply(Pokemon p) {
								m_types = p.getTypes();
								ArrayList<PokemonType> types = new ArrayList<PokemonType>(Arrays.asList(m_types));
								Iterator<PokemonType> i = types.iterator();
								while (i.hasNext()) {
									PokemonType type = (PokemonType)i.next();
									if (type.equals(PokemonType.T_FLYING)) {
										i.remove();
									}
								}
								p.setType((PokemonType[])types.toArray(new PokemonType[types.size()]));
								return true;
							}
							public String getName() {
								return "Roosting";
							}
							public int getTier() {
								return 5;
							}
							public String getDescription() {
								return null;
							}
							public void unapply(Pokemon p) {
								p.setType(m_types);
							}
							public boolean tick(Pokemon p) {
								p.removeStatus(this);
								return true;
							}
						}, new PercentEffect(0.5, false, -1, null) 
				},
				new boolean[] { true, true },
				new double[] { 1.0, 1.0 }
		)));

		m_moves.add(new MoveListEntry("Grass Knot",
				new MassBasedMove(PokemonType.T_GRASS, 1.0, 20)));

		m_moves.add(new MoveListEntry("Guard Swap",
				new StatChangeSwapMove(PokemonType.T_PSYCHIC, 10, new int[] { 
						Pokemon.S_DEFENCE, 
						Pokemon.S_SPDEFENCE
				}
				)));

		m_moves.add(new MoveListEntry("Power Swap",
				new StatChangeSwapMove(PokemonType.T_PSYCHIC, 10, new int[] { 
						Pokemon.S_ATTACK, 
						Pokemon.S_SPATTACK
				}
				)));

		m_moves.add(new MoveListEntry("Heart Swap",
				new StatChangeSwapMove(PokemonType.T_PSYCHIC, 10, new int[] { 
						Pokemon.S_DEFENCE, 
						Pokemon.S_SPDEFENCE,
						Pokemon.S_ATTACK, 
						Pokemon.S_SPATTACK,
						Pokemon.S_SPEED,
						Pokemon.S_ACCURACY,
						Pokemon.S_EVASION
				}
				)));

		m_moves.add(new MoveListEntry("Outrage",
				new RampageMove(PokemonType.T_DRAGON, 120, 1.0, 15) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				setPower((int)(((double)getPower()) / 120.0 * ((mech instanceof JewelMechanics) ? 120.0 : 90.0)));
				return super.use(mech, user, target);
			}
		}));

		m_moves.add(new MoveListEntry("Petal Dance",
				new RampageMove(PokemonType.T_GRASS, 70, 1.0, 20)
		));

		m_moves.add(new MoveListEntry("Thrash",
				new RampageMove(PokemonType.T_NORMAL, 90, 1.0, 20)
		));

		m_moves.add(new MoveListEntry("Fake Out",
				new PokemonMove(PokemonType.T_NORMAL, 40, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (!user.isFirstTurn()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				if ((getEffectiveness(user, target) != 0.0) && !target.hasSubstitute()) {
					target.addStatus(user, new FlinchEffect());
				}
				return super.use(mech, user, target);
			}
			public int getPriority() {
				return 1;
			}
		}
		));

		m_moves.add(new MoveListEntry("Magnet Rise",
				new PokemonMove(PokemonType.T_ELECTRIC, 0, 0, 10) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				StatusEffect eff = user.addStatus(user, new MagnetRiseEffect());
				if (eff == null) {
					user.getField().showMessage("But it failed!");
				}
				return 0;
			}
		}
		));

		PokemonMove mirrorMove = new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.getLastMove() == null) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				return user.useMove(target.getLastMove(), target);
			}
		};

		m_moves.add(new MoveListEntry("Mirror Move", (PokemonMove)mirrorMove.clone()));
		m_moves.add(new MoveListEntry("Copycat", (PokemonMove)mirrorMove.clone()));

		m_moves.add(new MoveListEntry("Spite",
				new PokemonMove(PokemonType.T_GHOST, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				MoveListEntry move = target.getLastMove();
				if (move == null) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				for (int i = 0; i < 4; ++i) {
					if (move.equals(target.getMove(i))) {
						int number = (mech instanceof JewelMechanics) ? 4 : 
							mech.getRandom().nextInt(3) + 2;
						target.setPp(i, target.getPp(i) - number);
						return 0;
					}
				}
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Destiny Bond", new StatusMove(
				PokemonType.T_GHOST, 0, 1.0, 5, new StatusEffect[] {
						new StatusEffect() {
							public String getName() {
								return "Destiny bond";
							}
							public String getDescription() {
								return " is trying to take its foe with it!";
							}
							public int getTier() {
								return -1;
							}
							public boolean tick(Pokemon p) {
								return false;
							}
							public void executeTurn(Pokemon p, BattleTurn turn) {
								p.removeStatus(this);
							}
							public boolean isListener() {
								return true;
							}
							public boolean hitsThroughSubstitute() {
								return true;
							}
							public void informDamaged(Pokemon source, Pokemon target, MoveListEntry move, int damage) {
								if (target.getHealth() <= 0) {
									target.getField().showMessage(target.getName() + " took " + 
											source.getName() + " with it!");
									source.faint();
								}
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Grudge", new StatusMove(
				PokemonType.T_GHOST, 0, 1.0, 5, new StatusEffect[] {
						new StatusEffect() {
							public String getName() {
								return "Grudge";
							}
							public String getDescription() {
								return " is bearing a Grudge!";
							}
							public int getTier() {
								return -1;
							}
							public boolean tick(Pokemon p) {
								return false;
							}
							public boolean isListener() {
								return true;
							}
							public boolean hitsThroughSubstitute() {
								return true;
							}
							public void informDamaged(Pokemon source, Pokemon target, MoveListEntry move, int damage) {
								if (target.getHealth() <= 0) {
									for (int i = 0; i < 4; i++) {
										if (move.equals(source.getMove(i))) {
											source.getField().showMessage(
													move.getName() + " lost its PP due to the Grudge!");
											source.setPp(i, 0);
											break;
										}
									}
								}
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Miracle Eye", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 40, new StatusEffect[] {
						new StatusEffect() {
							public String getName() {
								return "Miracle";
							}
							public String getDescription() {
								return " indentified the enemy Pokemon!";
							}
							public int getTier() {
								return -1;
							}
							public boolean tick(Pokemon p) {
								return false;
							}
							public boolean isEffectivenessTransformer(boolean enemy) {
								return !enemy;
							}
							public double getTransformedEffectiveness(PokemonType move, PokemonType pokemon) {
								if (move.equals(PokemonType.T_PSYCHIC) && pokemon.equals(PokemonType.T_DARK)) {
									return 1.0;
								}
								return super.getTransformedEffectiveness(move, pokemon);
							}
						}
				},
				new boolean[] { false },
				new double[] { 1.0 }
		) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				List<StatusEffect> statuses = target.getNormalStatuses(0);
				Iterator<StatusEffect> i = statuses.iterator();
				while (i.hasNext()) {
					StatusEffect effect = (StatusEffect)i.next();
					if (!(effect instanceof StatChangeEffect)) continue;
					StatChangeEffect eff = (StatChangeEffect)effect;
					if (eff.getStat() == Pokemon.S_EVASION) {
						target.removeStatus(eff);
					}
				}
				target.getEvasion().setSecondaryMultiplier(1);
				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Torment", new StatusMove(
				PokemonType.T_DARK, 0, 1.0, 15, new StatusEffect[] {
						new StatusEffect() {
							private MoveListEntry m_entry;
							public String getName() {
								return "Torment";
							}
							public String getDescription() {
								return " was subjected to Torment!";
							}
							public int getTier() {
								return -1;
							}
							public boolean tick(Pokemon p) {
								return false;
							}
							public boolean apply(Pokemon p) {
								m_entry = p.getLastMove();
								return super.apply(p);
							}
							public boolean isMoveTransformer(boolean enemy) {
								return !enemy;
							}
							public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
								if (entry.equals(m_entry)) {
									p.getField().showMessage(p.getName() + " couldn't use the move after" +
											" the torment!");
									return null;
								}
								m_entry = entry;
								return entry;
							}
							public boolean vetoesMove(Pokemon p, MoveListEntry entry) {
								if (m_entry == null) {
									return false;
								}
								return m_entry.equals(entry);
							}
						}
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Encore", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 5, new StatusEffect[] {
						new StatusEffect() {
							private int m_turns;
							private boolean m_transform = true;
							private MoveListEntry m_entry;
							public String getName() {
								return "Encore";
							}
							public String getDescription() {
								return " got an encore!";
							}
							public int getTier() {
								return 5;
							}
							public boolean hitsThroughSubstitute() {
								return true;
							}
							public boolean apply(Pokemon p) {
								MoveListEntry entry = p.getLastMove();
								if (entry == null) {
									p.getField().showMessage("But it failed!");
									return false;
								}
								m_entry = entry;
								m_turns = p.getField().getRandom().nextInt(5) + 5;
								return true;
							}

							public boolean tick(Pokemon p) {
								if (--m_turns <= 0) {
									p.getField().showMessage(p.getName() + "'s encore ended.");
									p.removeStatus(this);
									return true;
								}
								return false;
							}
							public boolean isMoveTransformer(boolean enemy) {
								return !enemy;
							}
							public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
								if (m_transform) {
									m_transform = false;
									return m_entry;
								}
								return entry;
							}
							public boolean vetoesMove(Pokemon p, MoveListEntry entry) {
								return !entry.equals(m_entry);
							}
						}
				},
				new boolean[] { false },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Disable", new StatusMove(
				PokemonType.T_NORMAL, 0, 0.55, 20, new StatusEffect[] {
						new StatusEffect() {
							private int m_turns;
							private MoveListEntry m_entry;
							public String getName() {
								return "Disabled: " + m_entry.getName();
							}
							public String getDescription() {
								return " was disabled!";
							}
							public int getTier() {
								return 5;
							}
							public boolean apply(Pokemon p) {
								MoveListEntry entry = p.getLastMove();
								if (entry == null) {
									p.getField().showMessage("But it failed!");
									return false;
								}
								m_entry = entry;
								m_turns = p.getField().getRandom().nextInt(3) + 2;
								return true;
							}
							public boolean isMoveTransformer(boolean enemy) {
								return !enemy;
							}
							public MoveListEntry getEnemyTransformedMove(Pokemon p, MoveListEntry entry) {
								if (entry.equals(m_entry)) {
									p.getField().showMessage(p.getName() + "'s " + entry.getName() +
									" is disabled!");
									return null;
								} 
								return entry;
							}
							public boolean tick(Pokemon p) {
								if (--m_turns <= 0) {
									p.removeStatus(this);
									return true;
								}
								return false;
							}
							public boolean vetoesMove(Pokemon p, MoveListEntry entry) {
								return entry.equals(m_entry);
							}
							public boolean hitsThroughSubstitute() {
								return true;
							}
						}
				},
				new boolean[] { false },
				new double[] { 1.0 }
		) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				setAccuracy(mech instanceof JewelMechanics ? 80 : 55);
				return super.attemptHit(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Imprison", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 15, new StatusEffect[] {
						new StatusEffect() {
							public String getName() {
								return "Imprison";
							}
							public String getDescription() {
								return "'s moves were sealed!";
							}
							public int getTier() {
								return -1;
							}
							public boolean tick(Pokemon p) {
								return false;
							}
							public boolean vetoesMove(Pokemon p, MoveListEntry entry) {
								Pokemon target = p.getOpponent();
								for (int i = 0; i < 4; ++i) {
									MoveListEntry move = target.getMove(i);
									if ((move != null) && move.equals(entry)) {
										return true;
									}
								}
								return false;
							}
							public boolean hitsThroughSubstitute() {
								return true;
							}
						}
				},
				new boolean[] { false },
				new double[] { 1.0 }
		) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Stockpile", new PokemonMove(
				PokemonType.T_NORMAL, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				StockpileEffect eff = (StockpileEffect)(user.getEffect(StockpileEffect.class));
				if (eff == null) {
					eff = (StockpileEffect)user.addStatus(user, new StockpileEffect());
				}
				eff.incrementLevel(user);
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Spit Up",
				new StockpileMove(PokemonType.T_NORMAL, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int levels = getLevels(user);
				if (levels <= 0) {
					user.getField().showMessage("But it failed to spit up anything!");
					return 0;
				}
				setPower(100 * levels);
				int damage = super.use(mech, user, target);
				setPower(0);
				user.removeStatus(getStockpileEffect(user));
				return damage;
			}
			public boolean isAttack() {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Swallow",
				new StockpileMove(PokemonType.T_NORMAL, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int levels = getLevels(user);
				if (levels <= 0) {
					user.getField().showMessage("But it failed to swallow anything!");
					return 0;
				}
				double[] percents = new double[] {0.25, 0.5, 1.0};
				double percent = percents[levels];

				user.addStatus(user, new PercentEffect(percent, false, -1, null));
				user.removeStatus(getStockpileEffect(user));
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Tailwind",
				new PokemonMove(PokemonType.T_FLYING, 0, 1.0, 30) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().applyEffect(new TailwindEffect(user.getParty()));
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Bide", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 10, new StatusEffect[] {
						new StatusEffect() {
							private int m_damage = 0;
							private int m_turns = 2;
							public String getName() {
								return "Bide";
							}
							public String getDescription() {
								return " is storing energy!";
							}
							public int getTier() {
								return -1;
							}
							public boolean deactivates(Pokemon p) {
								return true;
							}
							public boolean isListener() {
								return true;
							}
							public void informDamaged(Pokemon source, Pokemon target, MoveListEntry move, int damage) {
								m_damage += damage;
							}
							public boolean immobilises(Pokemon p) {
								if (--m_turns <= 0) {
									p.getField().showMessage(p.getName() + " unleashed energy!");
									p.useMove(new PokemonMove(PokemonType.T_TYPELESS, 0, 1.0, 1) {
										public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
											int change = 2 * m_damage;
											target.changeHealth(-change);
											return change;
										}
									}, p.getOpponent());
									p.removeStatus(this);
								}
								return true;
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		) {
			public int getPriority() {
				// todo: This should also only be in D/P.
				return 1;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));

		class MimicEffect extends StatusEffect {
			private MoveListEntry m_entry;
			public MimicEffect(MoveListEntry entry) {
				m_entry = entry;
			}
			public String getName() {
				return "Mimicking " + m_entry.getName();
			}
			public String getDescription() {
				return " mimicked the foe's move!";
			}
			public boolean isMoveTransformer(boolean enemy) {
				return !enemy;
			}
			public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
				if (entry.getName().equals("Mimic")) {
					return m_entry;
				}
				return entry;
			}
		}

		m_moves.add(new MoveListEntry("Mimic",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				MoveListEntry move = target.getLastMove();
				if (move == null) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				user.addStatus(user, new MimicEffect(move));
				return 0;
			}
		}
		));

		m_moves.add(new MoveListEntry("Mist",
				new PokemonMove(PokemonType.T_ICE, 0, 1.0, 30) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				StatusEffect eff = user.getField().getEffectByType(MistEffect.class);
				if (eff == null) {
					user.getField().applyEffect(new MistEffect());
				}
				eff = user.getField().getEffectByType(MistEffect.class);
				if (eff != null) {
					MistEffect effect = (MistEffect)eff;
					effect.activateParty(user);
				}
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Safeguard",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 25) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				StatusEffect eff = user.getField().getEffectByType(SafeguardEffect.class);
				if (eff == null) {
					user.getField().applyEffect(new SafeguardEffect());
				}
				eff = user.getField().getEffectByType(SafeguardEffect.class);
				if (eff != null) {
					SafeguardEffect effect = (SafeguardEffect)eff;
					effect.activateParty(user);
				}
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Gastro Acid",
				new PokemonMove(PokemonType.T_POISON, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasSubstitute()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				IntrinsicAbility ability = target.getAbility();
				if ((ability != null) && ability.isActive()) {
					ability.unapply(target);
					ability.deactivate();
				}
				user.getField().showMessage(target.getName() + "'s ability was nullified.");
				target.addStatus(user, new StatusEffect() {
					public String getName() {
						return "Gastro Acid";
					}
				});
				return 0;
			}
		}));

		m_moves.add(new MoveListEntry("Gravity",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 5) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().applyEffect(new FieldEffect() {
					private int m_turns = 5;
					public int getTier() {
						return 1;
					}
					public boolean applyToField(BattleField field) {
						// Completely made up message.
						field.showMessage("Gravity intensified!");
						return true;
					}
					public boolean apply(Pokemon p) {
						if (p.hasAbility("Levitate")) {
							p.getAbility().deactivate();
						}
						BattleField field = p.getField();
						field.setNarrationEnabled(false);
						p.removeStatus(MagnetRiseEffect.class);
						field.setNarrationEnabled(true);
						p.getMultiplier(Pokemon.S_ACCURACY).multiplyBy(1.7);
						return true;
					}
					public void unapply(Pokemon p) {
						/** This will interfere with mold breaker and gastro acid,
						 *  but we will address that later. */
						IntrinsicAbility ability = p.getAbility();
						if ((ability != null) && ability.getName().equals("Levitate")) {
							ability.activate();
						}
						p.getMultiplier(Pokemon.S_ACCURACY).divideBy(1.7);
					}
					public boolean tickPokemon(Pokemon p) {
						return false;
					}
					public boolean isEffectivenessTransformer(boolean enemy) {
						return !enemy;
					}
					public double getTransformedEffectiveness(PokemonType move, PokemonType defender) {
						if (PokemonType.T_GROUND.equals(move) && PokemonType.T_FLYING.equals(defender)) {
							// Ground is neutral aganist flying under the effects of gravity.
							return 1.0;
						}
						return super.getTransformedEffectiveness(move, defender);
					}
					public boolean isMoveTransformer(boolean enemy) {
						return !enemy;
					}
					public boolean vetoesMove(Pokemon p, MoveListEntry entry) {
						return !canUseMove(entry.getName());
					}
					private boolean canUseMove(String move) {
						// Moves that involve going up into the air are forbidden.
						Set<String> forbidden = new HashSet<String>(Arrays.asList(new String[] {
								"Fly", "Bounce", "Hi Jump Kick", "Jump Kick", "Magnet Rise"
						}));
						return !forbidden.contains(move);
					}
					public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
						String name = entry.getName();
						if (!canUseMove(name)) {
							BattleField field = p.getField();
							field.showMessage(p.getName() + " can't use " + name + " because of the Gravity.");
							return null;
						}
						return entry;
					}

					public boolean tickField(BattleField field) {
						if (--m_turns == 0) {
							field.removeEffect(this);
							return true;
						}
						return false;
					}
					public String getName() {
						return "Gravity";
					}
					public void unapplyToField(BattleField field) {
						field.showMessage("Gravity returned to normal!");
					}
				});
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}));

		m_moves.add(new MoveListEntry("Embargo",
				new PokemonMove(PokemonType.T_DARK, 0, 1.0, 15) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (target.hasSubstitute()) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				target.addStatus(user, new StatusEffect() {
					public boolean apply(Pokemon p) {
						HoldItem item = p.getItem();
						if ((item != null) && item.isActive()) {
							item.unapply(p);
							item.deactivate();
						}
						return true;
					}
					public String getDescription() {
						return "'s item was nullified.";
					}
					public void unapply(Pokemon p) {
						HoldItem item = p.getItem();
						if ((item != null) && !item.isRemovable()) {
							item.activate();
							item.apply(p);
						}
					}
					public String getName() {
						return "Embargo";
					}
				});
				return 0;
			}
		}));

		class SnatchEffect extends StatusEffect {
			private StatusEffect[] m_effects;
			public SnatchEffect(StatusEffect[] effects) {
				m_effects = effects;
			}
			public String getName() {
				return "Snatch";
			}
			public String getDescription() {
				return null;
			}
			public boolean tick(Pokemon p) {
				p.removeStatus(this);
				return true;
			}
			public boolean isMoveTransformer(boolean enemy) {
				return enemy;
			}
			public MoveListEntry getEnemyTransformedMove(Pokemon p, MoveListEntry entry) {
				return null;
			}
			public StatusEffect[] getEffects() {
				return m_effects;
			}
		}

		m_moves.add(new MoveListEntry("Snatch",
				new PokemonMove(PokemonType.T_DARK, 0, 1.0, 10) {
			@SuppressWarnings("unused")
			public void beginTurn(BattleTurn[] turn, Pokemon p, int index) {
				if (p.hasEffect(SleepEffect.class) || p.hasEffect(FreezeEffect.class)) {
					return;
				}
				BattleTurn opp = turn[1 - index];
				if (!opp.isMoveTurn()) return;
				MoveListEntry entry = p.getOpponent().getMove(opp.getId());
				PokemonMove move = entry.getMove();
				if (!(move instanceof StatusMove)) return;
				StatusMove statusMove = (StatusMove)move;
				if (statusMove.isAttack()) return;
				StatusEffect[] effects = statusMove.getEffects();
				p.addStatus(p, new SnatchEffect(effects));
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().showMessage(user.getName() + " awaits the foe's move!");
				SnatchEffect eff = (SnatchEffect)user.getEffect(SnatchEffect.class);
				if (eff == null) {
					return 0;
				}
				StatusEffect[] effects = eff.getEffects();
				if (effects.length > 0) {
					user.getField().showMessage(user.getName() + " snatched the foe's effects!");
					for (int i = 0; i < effects.length; ++i) {
						user.addStatus(user, effects[i]);
					}
				}
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int getPriority() {
				return 3;
			}
		}
		));

		PokemonMove lockOn = new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 5) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.addStatus(user, new LockOnEffect());
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		};
		m_moves.add(new MoveListEntry("Lock-on", (PokemonMove)lockOn.clone()));
		m_moves.add(new MoveListEntry("Mind Reader", (PokemonMove)lockOn.clone()));

		m_moves.add(new MoveListEntry("Conversion 2", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 30, new StatusEffect[] {
						new StatusEffect() {
							private PokemonType[] m_types;

							public String getName() {
								return "Conversion 2";
							}
							public String getDescription() {
								return null;
							}
							public int getTier() {
								return -1;
							}
							public boolean tick(Pokemon p) {
								return false;
							}
							public boolean apply(Pokemon p) {
								m_types = p.getTypes();
								MoveListEntry entry = p.getOpponent().getLastMove();
								if (entry == null) {
									p.getField().showMessage("But it failed!");
									return false;
								}
								PokemonType moveType = entry.getMove().getType();
								ArrayList<PokemonType> types = new ArrayList<PokemonType>(Arrays.asList(PokemonType.getTypes()));
								Iterator<PokemonType> i = types.iterator();
								while (i.hasNext()) {
									PokemonType type = (PokemonType)i.next();
									if (moveType.getMultiplier(type) >= 1) {
										i.remove();
									}
								}
								int random = p.getField().getRandom().nextInt(types.size());
								p.setType(new PokemonType[] { (PokemonType)types.get(random) });
								return true;                            
							}
							public void unapply(Pokemon p) {
								p.setType(m_types);
							}
							public boolean switchOut(Pokemon p) {
								p.setType(m_types);
								return true;
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Rage", new StatusMove(
				PokemonType.T_NORMAL, 20, 1.0, 20, new StatusEffect[] {
						new StatusEffect() {                        
							public String getName() {
								return "Rage";
							}
							public String getDescription() {
								return " went into a rage!";
							}
							public int getTier() {
								return -1;
							}
							public boolean tick(Pokemon p) {
								return false;
							}
							public void executeTurn(Pokemon p, BattleTurn turn) {
								p.removeStatus(this);
							}
							public boolean isListener() {
								return true;
							}
							public void informDamaged(Pokemon source, Pokemon target, MoveListEntry move, int damage) {
								target.getField().showMessage(target.getName() + " is angry!");
								target.addStatus(target, new StatChangeEffect(Pokemon.S_ATTACK, true));
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		class FuryCutterEffect extends StatusEffect {
			public int m_turns = 0;
			public String getName() {
				return "Fury cutter";
			}
			public String getDescription() {
				return null;
			}
			public int getTier() {
				return -1;
			}
			public boolean tick(Pokemon p) {
				m_turns++;
				return false;
			}
			public void executeTurn(Pokemon p, BattleTurn turn) {
				if (p.getField().getMechanics() instanceof JewelMechanics) {
					return;
				}
				if (!p.getMove(turn.getId()).getName().equals("Fury Cutter")) {
					p.removeStatus(this);
				}
			}
			public boolean isMoveTransformer(boolean enemy) {
				return !enemy;
			}
			public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
				if (!entry.getName().equals("Fury Cuttter")) return entry;
				PokemonMove move = entry.getMove();
				int power = move.getPower() * (1 << m_turns);
				if (power > 160) power = 160;
				move.setPower(power);
				return entry;
			}
			public boolean isSingleton() {
				return true;
			}
			public void informDuplicateEffect(Pokemon p) {
			}
		}

		m_moves.add(new MoveListEntry("Fury Cutter", new StatusMove(
				PokemonType.T_BUG, 10, 0.95, 20, new StatusEffect[] {
						new FuryCutterEffect()
				},
				new boolean[] { true },
				new double[] { 1.0 }
		) {
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				if (!super.attemptHit(mech, user, target)) {
					user.removeStatus(FuryCutterEffect.class);
				}
				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Power Trick", new StatusMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 10, new StatusEffect[] {
						new StatusEffect() {                        
							private int m_attack;
							private int m_defence;
							public String getName() {
								return "Power Trick";
							}
							public String getDescription() {
								return " swapped its stats!";
							}
							public int getTier() {
								return -1;
							}
							public boolean tick(Pokemon p) {
								return false;
							}
							public boolean apply(Pokemon p) {
								m_attack = p.getRawStat(Pokemon.S_ATTACK);
								m_defence = p.getRawStat(Pokemon.S_DEFENCE);
								p.setRawStat(Pokemon.S_ATTACK, m_defence);
								p.setRawStat(Pokemon.S_DEFENCE, m_attack);
								return super.apply(p);
							}
							public void unapply(Pokemon p) {
								p.setRawStat(Pokemon.S_ATTACK, m_attack);
								p.setRawStat(Pokemon.S_DEFENCE, m_defence);
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Uproar", new FixedAttackMove(
				PokemonType.T_NORMAL, 50, 1.0, 20, new FixedAttackEffect("Uproar",
						" caused an uproar!", " calmed down."))));

		m_moves.add(new MoveListEntry("Rollout", new FixedAttackMove(
				PokemonType.T_ROCK, 30, 0.9, 20, new RolloutEffect("Rollout"))));

		m_moves.add(new MoveListEntry("Ice Ball", new FixedAttackMove(
				PokemonType.T_ICE, 30, 0.9, 20, new RolloutEffect("Ice Ball"))));

		/** If you had bothered to read the Smogon analysis, you would have learned that
		 *  this is actually nearly a clone of Lunar Dance (except that it does not heal
		 *  PP); it is not at all like Destiny Bond. Fortunately the move is rare
		 *  enough that it has yet to matter.
		 */
		m_moves.add(new MoveListEntry("Healing Wish",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 10) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int party = user.getParty();
				BattleField field = user.getField();
				if (field.getAliveCount(party) == 1) {
					user.getField().showMessage("But it failed!");
					return 0;
				}
				user.faint();
				field.requestAndWaitForSwitch(party);
				target = field.getActivePokemon()[party];
				target.changeHealth(target.getStat(Pokemon.S_HP));
				target.removeStatus(StatusEffect.SPECIAL_EFFECT_LOCK);
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon source, Pokemon target) {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Bug Bite",
				new PokemonMove(PokemonType.T_BUG, 60, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				HoldItem item = target.getItem();
				if (item instanceof Berry) {
					Berry berry = (Berry)item;
					berry.executeEffects(user);
					target.setItem(null);
				}
				return super.use(mech, user, target);
			}
		}
		));

		m_moves.add(new MoveListEntry("Pluck",
				new PokemonMove(PokemonType.T_FLYING, 60, 1.0, 20) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				int power = getPower();
				HoldItem item = target.getItem();
				if (item instanceof Berry) {
					setPower(power * 2);
				}
				int damage = super.use(mech, user, target);
				setPower(power);
				return damage;
			}
		}
		));
		m_moves.add(new MoveListEntry("Lucky Chant",
				new PokemonMove(PokemonType.T_NORMAL, 0, 1.0, 30) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				FieldEffect effect = user.getField().getEffectByType(LuckyChantEffect.class);
				if (effect == null) {
					user.getField().applyEffect(
							new LuckyChantEffect(" is feeling lucky!", "'s luck wore off..."));
				}
				effect = user.getField().getEffectByType(LuckyChantEffect.class);
				LuckyChantEffect eff = (LuckyChantEffect)effect;
				eff.activateParty(user);
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));

		m_moves.add(new MoveListEntry("Heal Block",
				new PokemonMove(PokemonType.T_PSYCHIC, 0, 1.0, 15) {
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				target.addStatus(user, new HealBlockEffect());
				return 0;
			}
			public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
				return true;
			}
		}
		));
		m_moves.add(new MoveListEntry("Focus Energy", new StatusMove(
				PokemonType.T_NORMAL, 0, 1.0, 30, new StatusEffect[] {
						new StatusEffect() {
							public String getName() {
								return "Focus energy";
							}
							public String getDescription() {
								return " tightened its focus!";
							}
							public boolean switchOut(Pokemon p) {
								return true;
							}
						}
				},
				new boolean[] { true },
				new double[] { 1.0 }
		)));

		m_moves.add(new MoveListEntry("Magic Coat", new PokemonMove(
				PokemonType.T_PSYCHIC, 0, 1.0, 15) {
			public int getPriority() {
				return 5;
			}
			public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
				user.getField().showMessage(user.getName() + " created a barrier!");
				target.addStatus(user, new StatusEffect() {
					public boolean isMoveTransformer(boolean enemy) {
						return !enemy;
					} 
					public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
						PokemonMove move = entry.getMove();
						if (!(move instanceof StatusMove)) return entry;
						StatusMove statusMove = (StatusMove)move;
						if (!statusMove.isAttack() || statusMove.isDamaging()) {
							return entry;
						}
						int max = statusMove.getEffects().length;
						for (int i = 0; i < max; ++i) {
							statusMove.setAttacker(i, true);
						}
						return entry;
					}
					public int getTier() {
						return 5;
					}
					public boolean tick(Pokemon p) {
						p.removeStatus(this);
						return true;
					}
				});
				return 0;
			}
		}
		));


	}
	/**
	 * An effect that is applied to a team for 5 turns.
	 */
	public abstract static class PartyEffect extends FieldEffect {
		private int[] m_turns = { 5, 5 };
		private boolean[] m_active = { false, false };
		private String m_endMessage;
		private String m_startMessage;

		public PartyEffect(String start, String end) {
			m_startMessage = start;
			m_endMessage = end;
		}
		public boolean applyToField(BattleField field) {
			return true;
		}
		public int getTier() {
			return 0;
		}
		public boolean tickField(BattleField field) {
			boolean stillActive = false;
			for (int i = 0; i < m_turns.length; ++i) {
				if (!m_active[i]) continue;
				if (--m_turns[i] <= 0) {
					if (m_endMessage != null) {
						field.showMessage(
								field.getActivePokemon()[i].getName() + m_endMessage);
					}
					m_active[i] = false;
				}
				if (m_active[i]) {
					stillActive = true;
				}
			}
			if (!stillActive) {
				field.removeEffect(this);
				return true;
			}
			return false;
		}
		public boolean isSingleton() {
			return true;
		}
		public void activateParty(Pokemon p) {
			int party = p.getParty();
			BattleField field = p.getField();

			if (m_active[party]) {
				informDuplicateEffect(p);
				return;
			}
			m_active[party] = true;
			m_turns[party] = 5;
			if (m_startMessage != null) {
				field.showMessage(
						field.getActivePokemon()[party].getName() + m_startMessage);
			}
		}
		public boolean isActive(int party) {
			if ((party < 0) || (party > m_active.length)) return false;
			return m_active[party];
		}
	}
	/**
	 * The party cannot be hit by critical hits for 5 turns.
	 */
	public static class LuckyChantEffect extends PartyEffect {
		public LuckyChantEffect(String start, String end) {
			super(start, end);
		}
		public String getName() {
			return "Lucky chant";
		}
	}
	/**
	 * Prevents the affected pokemon from using recovery moves for 5 turns.
	 */
	public static class HealBlockEffect extends StatusEffect {
		private int m_turns = 5;
		public String getName() {
			return "Heal block";
		}
		public String getDescription() {
			return " was prevented from healing!";
		}
		public boolean isVetoed(MoveListEntry entry) {
			String name= entry.getName();
			if (name.equals("Rest") || name.equals("Wish")) return true;
			PokemonMove move = entry.getMove();
			if (!(move instanceof StatusMove)) return false;
			StatusMove statusMove = (StatusMove)move;
			StatusEffect[] effects = statusMove.getEffects();
			StatusEffect eff = null;
			for (int i = 0; i < effects.length; ++i) {
				if (effects[i] instanceof PercentEffect) {
					eff = effects[i];
					PercentEffect effect = (PercentEffect)eff;
					if ((effect.getTier() == -1) && (effect.getPercent() > 0)) {
						return true;
					}
				}
			}
			return false;
		}
		public boolean isMoveTransformer(boolean enemy) {
			return enemy;
		}
		public MoveListEntry getEnemyTransformedMove(Pokemon p, MoveListEntry entry) {
			if (isVetoed(entry)) {
				p.getField().showMessage(p.getName() + " can't use " + entry.getName() +
				" because of Heal Block!");
				return null;
			}
			return entry;
		}
		//todo: find actual tier
		public int getTier() {
			return 3;
		}
		public boolean tick(Pokemon p) {
			if (m_turns-- <= 0) {
				p.removeStatus(this);
				p.getField().showMessage(p.getName() + "'s Heal Block wore off!");
				return true;
			}
			return false;
		}
		public boolean vetoesMove(Pokemon p, MoveListEntry entry) {
			return isVetoed(entry);
		}
		public void informDuplicateEffect(Pokemon p) {
			p.getField().showMessage("It failed to affect " + p.getName());
		}
	}

	/**
	 * A move that prevents the user from using other moves until the move misses
	 * or the number of turns is reached.
	 */
	public static class FixedAttackMove extends StatusMove {
		public FixedAttackMove(PokemonType type, int power, double accuracy, int pp, FixedAttackEffect eff) {
			super(type, power, accuracy, pp, new StatusEffect[] { eff },
					new boolean[] { true }, new double[] { 1.0 });
		}
		public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
			return true;
		}
		public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
			if (super.attemptHit(mech, user, target)) {
				return super.use(mech, user, target);
			}
			user.removeStatus(FixedAttackEffect.class);
			return 0;
		}
	}

	/**
	 * A status that forces the user to continue to use the same move.
	 */
	public static class FixedAttackEffect extends StatusEffect {
		protected int m_turns;
		private int m_pp;
		private String m_name;
		private String m_description;
		private String m_message;

		public FixedAttackEffect(String name, String description, String message) {
			m_name = name;
			m_description = description;            
			m_message = message;
		}
		public String getName() {
			return m_name;
		}
		public String getDescription() {
			return m_description;
		}
		public int getTier() {
			return 3;
		}
		private int getMoveIndex(Pokemon p) {
			for (int i = 0; i < 4; ++i) {
				MoveListEntry move = p.getMove(i);
				if (move == null)
					continue;
				if (move.getName().equals(getName())) {
					return i;
				}
			}
			return -1;
		}
		public boolean apply(Pokemon p) {
			m_turns = p.getField().getRandom().nextInt(4) + 2;
			int idx = getMoveIndex(p);
			m_pp = p.getPp(idx);
			return super.apply(p);
		}
		public boolean tick(Pokemon p) {
			int idx = getMoveIndex(p);
			p.setPp(idx, m_pp);
			if (m_turns-- <= 0) {
				if (m_message != null) {
					p.getField().showMessage(p.getName() + m_message);
				}
				p.removeStatus(this);
				return true;
			}
			return false;                            
		}
		public boolean canSwitch(Pokemon p) {
			return false;
		}
		public boolean vetoesMove(Pokemon p, MoveListEntry entry) {
			return !entry.getName().equals(getName());
		}
		public boolean isSingleton() {
			return true;
		}
		public void informDuplicateEffect(Pokemon p) {
		}
		public void executeTurn(Pokemon p, BattleTurn turn) {
			m_pp = p.getPp(turn.getId());
		}
	}

	private static class RolloutEffect extends FixedAttackEffect {
		public RolloutEffect(String name) {
			super(name, null, null);
		}
		public boolean apply(Pokemon p) {
			super.apply(p);
			m_turns = 5;
			return true;
		}
		public boolean isMoveTransformer(boolean enemy) {
			return !enemy;
		}
		public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry move) {
			move.getMove().setPower(move.getMove().getPower() * (1 << (5 - m_turns)));
			return move;
		}
	}

	/**
	 * Ensures that the next move will hit unless the opponenet has an ISE.
	 * The logic is located in AdvanceMechanics.
	 */    
	public static class LockOnEffect extends StatusEffect {
		@SuppressWarnings("unused")
		private int m_turns = 1;
		public String getName() {
			return "Locked-on";
		}
		public String getDescription() {
			return " took aim at the foe!";
		}
		public int getTier() {
			return 0;
		}
		public boolean isMoveTransformer(boolean enemy) {
			return !enemy;
		}
		public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
			if (entry.getMove().isAttack()) {
				p.removeStatus(this);
			}
			return entry;
		}
	}
	/**
	 * A move that attacks for 2-3 turns and then confuses.
	 */
	private static class RampageMove extends PokemonMove {
		public RampageMove(PokemonType type, int power, double accuracy, int pp) {
			super(type, power, accuracy, pp);
		}
		public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
			user.addStatus(user, new StatusEffect() {
				private int m_turns = 0;
				public boolean apply(Pokemon p) {
					m_turns = 2 + p.getField().getMechanics().getRandom().nextInt(2);
					return true;
				}
				public String getName() {
					return getMoveListEntry().getName();
				} 
				public String getDescription() {
					return " went on a rampage!";
				}
				public int getTier() {
					return 1;
				}
				public boolean canSwitch(Pokemon p) {
					return false;
				}
				public boolean vetoesMove(Pokemon p, MoveListEntry move) {
					return !getMoveListEntry().equals(move);
				}
				public boolean tick(Pokemon p) {
					if (--m_turns == 0) {
						p.getField().showMessage(p.getName() + "'s rampage ended.");
						p.removeStatus(this);
						p.addStatus(p, new ConfuseEffect());
					}
					return true;
				}
			});
			return super.use(mech, user, target);
		}
	}

	/**
	 * A move that guards against an enemy attack.
	 */
	private static class ProtectMove extends StatusMove {
		private double m_failure = 0.0;
		public ProtectMove(PokemonType type, int pp, DelegationEffect effect) {
			super(type, 0, 1.0, pp, new StatusEffect[] {
					effect,
					new CounterEffect()
			},
			new boolean[] { true, true },
			new double[] { 1.0, 1.0 }
			);
		}
		public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
			if (mech.getRandom().nextDouble() < m_failure) {
				user.getField().showMessage("But it failed!");
				user.removeStatus(CounterEffect.class);
				return 0;
			}
			return super.use(mech, user, target);
		}
		public void setFailure(double failure) {
			m_failure = failure;
		}
		public int getPriority() {
			return 2;
		}
		public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
			return true;
		}
	}

	/**
	 * Makes pokemon immune to ground attacks for five turns.
	 */
	static class MagnetRiseEffect extends StatusEffect {
		private int m_turns = 5;
		public String getDescription() {
			return " became immune to ground attacks!";
		}
		public String getName() {
			return "Magnet Rise";
		}
		public boolean tick(Pokemon p) {
			if (--m_turns == 0) {
				p.removeStatus(this);
				p.getField().showMessage(p.getName()
						+ " is no longer immune to ground attacks!");
				return true;
			}
			return false;
		}
		public int getTier() {
			// Does not really matter.
			return 1;
		}
		public boolean isEffectivenessTransformer(boolean enemy) {
			return enemy;
		}
		public double getEnemyTransformedEffectiveness(
				PokemonType move, PokemonType target) {
			if (move.equals(PokemonType.T_GROUND)) {
				return 0.0;
			}
			return super.getEnemyTransformedEffectiveness(move, target);
		}
	}

	/**
	 * A move with a high chance of striking critical.
	 */
	static class HighCriticalHitMove extends PokemonMove {
		public HighCriticalHitMove(PokemonType type, int power, double accuracy, int pp) {
			super(type, power, accuracy, pp);
		}
		public boolean hasHighCriticalHitRate() {
			return true;
		}
	}

	/**
	 * A move that always hits unless the opponent is underground or flying.
	 */
	public static class PerfectAccuracyMove extends PokemonMove {
		public PerfectAccuracyMove(PokemonType type, int power, int pp) {
			super(type, power, 0, pp);
		}
		public static boolean isHit(BattleMechanics mech, Pokemon user, Pokemon target) {
			return !target.hasEffect(InvulnerableStateEffect.class);
		}
		public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
			return isHit(mech, user, target);
		}
	}

	private static class JumpKickMove extends PokemonMove {
		private int m_jewelPower, m_power;
		private static class CalculationEffect extends StatusEffect {
			public boolean isEffectivenessTransformer(boolean enemy) {
				return !enemy;
			}
			public double getTransformedEffectiveness(PokemonType move, PokemonType defender) {
				double expected = super.getTransformedEffectiveness(move, defender);
				if (expected == 0.0)
					return 1.0;
				return expected;
			}
		}
		public JumpKickMove(PokemonType type, int power,int jewelPower,
				double accuracy, int pp) {
			super(type, power, accuracy, pp);
			m_jewelPower = jewelPower;
			m_power = power;
		}
		public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
			return true;
		}
		public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
			setPower((mech instanceof JewelMechanics) ? m_jewelPower : m_power);
			boolean ineffective = (getEffectiveness(user, target) == 0.0);
			boolean protect = target.hasEffect(ProtectEffect.class);
			if (ineffective) {
				user.addStatus(user, new CalculationEffect());
			}
			int damage = mech.calculateDamage(this, user, target, protect);
			if (ineffective) {
				user.removeStatus(CalculationEffect.class);
			}
			BattleField field = user.getField();
			if (!ineffective && !protect
					&& super.attemptHit(mech, user, target)) {
				target.changeHealth(-damage);
				return damage;
			} else if (protect) {
				StatusEffect eff = target.getEffect(ProtectEffect.class);
				field.showMessage(target.getName() + eff.getDescription());
			}
			field.showMessage(user.getName() + " kept going and crashed!");
			int recoil = (int)((double)damage / 2.0);
			int max = target.getStat(Pokemon.S_HP) / 2;
			if (recoil > max) {
				recoil = max;
			}
			user.changeHealth(-recoil);
			return damage;
		}
	}

	/**
	 * A move that kills the target in one hit.
	 */
	 public static class OneHitKillMove extends PokemonMove {
		public OneHitKillMove(PokemonType type, int pp) {
			super(type, 0, 0, pp);
		}
		public boolean isAttack() {
			return true;
		}
		public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
			if (target.hasEffect(InvulnerableStateEffect.class)) {
				return false;
			}
			double ratio = ((double)(user.getLevel() - target.getLevel())) / 128.0;
			if (ratio < 0) {
				return false;
			}
			setAccuracy(0.234 + ratio);
			return super.attemptHit(mech, user, target);
		}
		public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
			if (target.hasAbility("Sturdy")) {
				user.getField().showMessage(target.getName() + " held sturdy!");
			} else {
				user.getField().showMessage("It's a OHKO!");
				if (target.hasSubstitute()) {
					target.setSubstitute(1);
					target.changeHealth(-1);
				} else {
					target.faint();
				}
			}
			return 0;
		}
	 }

	 /**
	  * A move with a modified priority.
	  */
	 static class PriorityMove extends PokemonMove {
		 private int m_priority;
		 public PriorityMove(PokemonType type, int power, double accuracy, int pp, int priority) {
			 super(type, power, accuracy, pp);
			 m_priority = priority;
		 }
		 public int getPriority() {
			 return m_priority;
		 }
	 }

	 /**
	  * A move whos power is based on the mass of the opponent.
	  */    
	 private static class MassBasedMove extends PokemonMove {
		 public MassBasedMove(PokemonType type, double accuracy, int pp) {
			 super(type, 0, accuracy, pp);
		 }
		 public int use(BattleMechanics mech, Pokemon user, Pokemon target) {
			 final double mass = PokemonSpecies.getDefaultData().getPokemonByName(target.getSpeciesName()).getWeight();
			 if (mass <= 10.0) {
				 setPower(20);
			 } else if (mass <= 25.0) {
				 setPower(40);
			 } else if (mass <= 100.0) {
				 setPower(80);
			 } else if (mass <= 200.0) {
				 setPower(100);
			 } else {
				 setPower(120);
			 }
			 int damage = super.use(mech, user, target);
			 setPower(0);
			 return damage;
		 }
		 public boolean isAttack() {
			 return true;
		 }
	 }

	 /**
	  * Cuts the power of the opponent's moves of a certain type.
	  */
	 private static class TypeCutMove extends PokemonMove {       
		 private PokemonType m_cut;
		 private String m_name;

		 public TypeCutMove(PokemonType type, int pp, PokemonType cut, String name) {
			 super(type, 0, 1.0, pp);
			 m_cut = cut;
			 m_name = name;
		 }

		 public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
			 return true;
		 }

		 public int use(BattleMechanics mech, Pokemon user, final Pokemon target) {
			 final String name = m_name;
			 BattleField field = user.getField();
			 if (!field.applyEffect(new TypeCutStatus(m_cut, user) {
				 public String getName() {
					 return name;
				 }
			 })) {
				 field.showMessage("But it failed!");
			 }
			 return 0;
		 }
	 }

	 private static class CounterMove extends PokemonMove {       
		 // 1 is special, 2 is physical, 3 is both
		 private int m_special;
		 public boolean isDamaging() {
			 return true;
		 }
		 public boolean isAttack() {
			 return true;
		 }
		 public CounterMove(PokemonType type, double accuracy, int pp, int special) {
			 super(type, 0, accuracy, pp);
			 m_special = special;
		 }
		 public int use(BattleMechanics mech, Pokemon user, final Pokemon target) {
			 if (getEffectiveness(m_type, user, target) == 0.0) {
				 user.getField().showMessage("It doesn't affect " + target.getName() + "...");
				 return 0;
			 }
			 DamageListenerEffect listener = null;
			 List<StatusEffect> effects = user.getNormalStatuses(0);
			 Iterator<StatusEffect> i = effects.iterator();
			 while (i.hasNext()) {
				 StatusEffect eff = (StatusEffect)i.next();
				 if (eff instanceof DamageListenerEffect) {
					 listener = (DamageListenerEffect)eff;
					 break;
				 }
			 }
			 if (listener == null) {
				 user.getField().showMessage("But it failed!");
				 return 0;
			 }
			 int damage = listener.getDamage() * 2;
			 boolean special = listener.isSpecial();
			 boolean good = (m_special == 3 || (m_special == 1 && special) || (m_special == 2 && !special));
			 if (damage <= 0 || !good) {
				 user.getField().showMessage("But it failed!");
				 return 0;
			 }
			 target.changeHealth(-damage);
			 return damage;
		 }
		 public int getPriority() {
			 return -4;
		 }
		 public void beginTurn(BattleTurn[] turn, int index, Pokemon source) {
			 source.addStatus(source, new DamageListenerEffect());
		 }
	 }

	 /**
	  * A move that swaps the StatChangeEffects of certain stats between two Pokemon.
	  */

	 private static class StatChangeSwapMove extends PokemonMove {
		 private int[] m_stats;

		 public StatChangeSwapMove(PokemonType type, int pp, int[] stats) {
			 super(type, 0, 1.0, pp);
			 m_stats = stats;
		 }
		 public boolean attemptHit(BattleMechanics mech, Pokemon user, Pokemon target) {
			 return true;
		 }
		 /**
		  * Removes statuses from the list if they are not StatChangeEffects of the expected variety.
		  */
		 private List<StatusEffect> cleanList(List<StatusEffect> list) {
			 Iterator<StatusEffect> i = list.iterator();
			 while (i.hasNext()) {
				 StatusEffect eff = (StatusEffect)i.next();
				 if (eff == null) continue;
				 if (!(eff instanceof StatChangeEffect)) {
					 i.remove();
					 continue;
				 }
				 StatChangeEffect effect = (StatChangeEffect)eff;
				 int stat = effect.getStat();
				 boolean good = false;
				 for (int j = 0; j < m_stats.length; ++j) {
					 if (stat == m_stats[j]) {
						 good = true;
					 }
				 }
				 if (!good) i.remove();
			 }
			 return list;
		 }
		 /**
		  * Removes a list of effects from a Pokemon.
		  */
		 private void removeStatuses(Pokemon p, List<StatusEffect> effects) {
			 Iterator<StatusEffect> i = effects.iterator();
			 while (i.hasNext()) {
				 StatChangeEffect eff = (StatChangeEffect)i.next();
				 p.removeStatus(eff);
			 }
		 }
		 /**
		  * Applies a list of effects from a Pokemon.
		  */
		 private void addStatuses(Pokemon p, List<StatusEffect> effects) {
			 Iterator<StatusEffect> i = effects.iterator();
			 while (i.hasNext()) {
				 StatChangeEffect eff = (StatChangeEffect)i.next();
				 eff.setDescription(null);
				 p.addStatus(p, eff);
			 }
		 }
		 public int use(BattleMechanics mech, Pokemon user, final Pokemon target) {
			 List<StatusEffect> userStatuses = cleanList(user.getNormalStatuses(0));
			 List<StatusEffect> targetStatuses = cleanList(target.getNormalStatuses(0));
			 removeStatuses(user, userStatuses);
			 removeStatuses(target, targetStatuses);
			 addStatuses(user, targetStatuses);
			 addStatuses(target, userStatuses);
			 user.getField().showMessage("The Pokemon swapped stats!");
			 return 0;
		 }
	 }

	 private static class DamageListenerMove extends PokemonMove {
		 private int m_priority = 0;
		 public DamageListenerMove(PokemonType type, int power, double accuracy, int pp) {
			 super(type, power, accuracy, pp);
		 }
		 public void beginTurn(BattleTurn[] turn, int index, Pokemon source) {
			 source.addStatus(source, new DamageListenerEffect());
		 }
		 public DamageListenerEffect getListener(Pokemon p) {
			 DamageListenerEffect listener = null;
			 List<StatusEffect> effects = p.getNormalStatuses(0);
			 Iterator<StatusEffect> i = effects.iterator();
			 while (i.hasNext()) {
				 StatusEffect eff = (StatusEffect)i.next();
				 if (eff instanceof DamageListenerEffect) {
					 listener = (DamageListenerEffect)eff;
					 break;
				 }
			 }
			 return listener;
		 }
		 public int getPriority() {
			 return m_priority;
		 }
		 public void setPriority(int priority) {
			 m_priority = priority;
		 }
	 }

	 private static class PerishSongEffect extends StatusEffect {
		 private int m_turns = 3;
		 private boolean m_soundImmune = true;
		 public PerishSongEffect(boolean soundImmune) {
			 m_soundImmune = soundImmune;
		 }
		 public String getDescription() {
			 return null;
		 }
		 public boolean apply(Pokemon p) {
			 // No need to reference Cacophony here.
			 return !(m_soundImmune && p.hasAbility("Soundproof"));
		 }
		 public boolean tick(Pokemon p) {
			 p.getField().showMessage(p.getName() + "'s perish count fell to " + m_turns + "!");
			 if (m_turns-- == 0) {
				 p.faint();
			 }
			 return true;
		 }
		 public int getTier() {
			 return 5;
		 }
		 public boolean hitsThroughSubstitute() {
			 return true;
		 }
		 public String getName() {
			 return "Perish count";
		 }
	 }

	 private abstract static class TypeCutStatus extends FieldEffect {

		 private PokemonType m_type;
		 private Pokemon m_user;

		 public TypeCutStatus(PokemonType type, Pokemon user) {
			 m_type = type;
			 m_user = user;
		 }

		 public boolean switchOut(Pokemon p) {
			 if (p == m_user) {
				 p.getField().removeEffect(this);
			 }
			 return super.switchOut(p);
		 }

		 public boolean equals(Object obj) {
			 if (!(obj instanceof TypeCutStatus)) {
				 return false;
			 }
			 TypeCutStatus rhs = (TypeCutStatus)obj;
			 return m_type.equals(rhs.m_type);
		 }

		 public boolean applyToField(BattleField field) {
			 field.showMessage(m_type + " attacks were weakened!");
			 return true;
		 }

		 public String getDescription() {
			 return null;
		 }

		 public boolean tickField(BattleField field) {
			 return false;
		 }

		 public int getTier() {
			 return -1;
		 }

		 public boolean isMoveTransformer(boolean enemy) {
			 return !enemy;
		 }

		 protected MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
			 PokemonMove move = entry.getMove();
			 if (move.getType().equals(m_type)) {
				 move.setPower(move.getPower() / 2);
			 }
			 return entry;
		 }

	 }

	 public static class SubstituteEffect extends StatusEffect {
		 public String getName() {
			 return "Substitute";
		 }
		 public String getDescription() {
			 return " made a substitute!";
		 }
		 public int getTier() {
			 return -1;
		 }
		 public boolean tick(Pokemon p) {
			 return false;
		 }
		 public boolean apply(Pokemon p) {
			 p.getField().refreshActivePokemon();
			 return true;
		 }
		 public void unapply(Pokemon p) {
			 p.getField().refreshActivePokemon();
		 }
		 public boolean switchOut(Pokemon p) {
			 p.setSubstitute(0);
			 return super.switchOut(p);
		 }
	 }

	 private static class InvulnerableStateEffect extends StatusEffect {
		 @SuppressWarnings("unused")
		 private int m_turns = 0;
		 private String[] m_effectiveMoves;

		 /**
		  * Creates an effect that makes a Pokemon invulnerable, except against certain moves.
		  * @param effectiveMoves moves that are doubly effective against a Pokemon in this state
		  */
		 public InvulnerableStateEffect(String[] effectiveMoves) {
			 m_effectiveMoves = effectiveMoves;
		 }
		 public String getName() {
			 return "Invulnerable state";
		 }
		 public String getDescription() {
			 return null;
		 }
		 public boolean isMoveTransformer(boolean enemy) {
			 return enemy;
		 }
		 protected MoveListEntry getEnemyTransformedMove(Pokemon p, MoveListEntry entry) {
			 String moveName = entry.getName();
			 PokemonMove move = entry.getMove();
			 for (int i = 0; i < m_effectiveMoves.length; ++i) {
				 if (moveName.equals(m_effectiveMoves[i])) {
					 move.setPower(move.getPower() * 2);
					 return entry;
				 }
			 }
			 if (!p.hasAbility("No Guard") && !p.getOpponent().hasAbility("No Guard")) {
				 move.setAccuracy(0);
			 }
			 return entry;
		 }
		 public int getTier() {
			 return -1;
		 }
		 public boolean tick(Pokemon p) {
			 return false;
		 }
		 public boolean immobilises(Pokemon poke) {
			 poke.removeStatus(this);
			 return false;
		 }
	 }

	 static class LeechSeedEffect extends StatusEffect {
		 private boolean m_grassImmune = true;
		 public LeechSeedEffect(boolean grassImmune) {
			 m_grassImmune = grassImmune;
		 }
		 public LeechSeedEffect() {

		 }
		 public String getName() {
			 return "Leech seed";
		 }
		 public int getTier() {
			 return 3;
		 }
		 public String getDescription() {
			 return " was seeded!";
		 }
		 public boolean tick(Pokemon p) {
			 final Pokemon opponent = p.getOpponent();
			 if (p.isFainted() || opponent.isFainted()) return false;
			 int damage = p.getStat(Pokemon.S_HP) / 8;
			 if (damage == 0) {
				 damage = 1;
			 }
			 if (!p.hasAbility("Magic Guard")) {
				 p.getField().showMessage(p.getName() + "'s health was sapped by leech seed!");
				 p.changeHealth(-damage, true);

				 if (p.hasAbility("Liquid Ooze")) {
					 p.getField().showMessage(opponent.getName() + " sucked up liquid ooze!");
					 opponent.changeHealth(-damage);
				 } else {
					 p.getField().showMessage(opponent.getName() + " regained health!");
					 opponent.changeHealth(damage);
				 }
			 }
			 return false;
		 }
		 public boolean apply(Pokemon p) {
			 if (m_grassImmune && p.isType(PokemonType.T_GRASS)) {
				 return false;
			 }
			 return true;
		 }
	 }    

	 //tbd - remove this effect if the user switches out
	 static class RestrainingEffect extends StatusEffect {
		 private int m_turns;
		 private String m_description;
		 private String m_name;

		 public RestrainingEffect(String name, String description) {
			 m_name = name;
			 m_description = description;
		 }
		 public String getName() {
			 return m_name;
		 }
		 public String getDescription() {
			 return " was " + m_description + "!";
		 }
		 public int getTier() {
			 return 3;
		 }
		 public boolean tick(Pokemon p) {
			 if (m_turns-- <= 0) {
				 p.getField().showMessage(p.getName() + " was released from " + m_name + "!");
				 p.removeStatus(this);
				 return false;
			 }
			 int maximum = p.getStat(Pokemon.S_HP);
			 int damage = maximum / 16;
			 if (damage == 0) damage = 1;
			 p.getField().showMessage(p.getName() + " is hurt by " + m_name + "!");
			 p.changeHealth(-damage);
			 return true;
		 }
		 public boolean apply(Pokemon p) {
			 if (p.hasEffect(RestrainingEffect.class)) {
				 p.removeStatus(RestrainingEffect.class);
			 }
			 m_turns = p.getField().getRandom().nextInt(4) + 1;
			 p.getOpponent().addStatus(p, new CoEffect(getClass()));
			 return true;
		 }
		 public boolean canSwitch(Pokemon p) {
			 return p.hasItem("Shed Shell");
		 }
	 }

	 private static interface ProtectEffectEvent {
		 public int use(
				 PokemonMove move,
				 BattleMechanics mech,
				 Pokemon source, Pokemon target);
	 }

	 private abstract static class ProtectEffect extends DelegationEffect {
		 public ProtectEffect() {
			 m_event = new ProtectEffectEvent() {
				 public int use(
						 PokemonMove move,
						 BattleMechanics mech,
						 Pokemon source, Pokemon target) {
					 source.getField().showMessage(target.getName()
							 + ProtectEffect.this.getDescription());
					 return 0;
				 }
			 };
		 }
	 }

	 public static class EndureEffect extends DelegationEffect {
		 public String getDescription() {
			 return " became ready to endure!";
		 }
		 public boolean isMoveTransformer(boolean enemy) {
			 return false;
		 }
	 }

	 private abstract static class DelegationEffect extends StatusEffect {
		 protected ProtectEffectEvent m_event;

		 public String getName() {
			 return null;
		 }
		 public int getTier() {
			 return 1;
		 }
		 public boolean isMoveTransformer(boolean enemy) {
			 return enemy;
		 }
		 public boolean tick(Pokemon p) {
			 p.removeStatus(this);
			 return true;
		 }
		 public MoveListEntry getEnemyTransformedMove(Pokemon p, MoveListEntry entry) {
			 final PokemonMove move = entry.getMove();
			 String name = entry.getName();
			 if (name.equals("Feint")
					 || name.equals("Shadow Force")
					 || (move instanceof JumpKickMove)
					 || name.equals("Explosion")
					 || name.equals("Selfdestruct")) return entry;
			 if (!p.hasEffect(ChargeEffect.class) && (move instanceof StatusMove)) {
				 StatusMove smove = (StatusMove)move;
				 StatusEffect[] effects = smove.getEffects();
				 for (int i = 0; i < effects.length; ++i) {
					 if (effects[i] instanceof ChargeEffect) return entry;
				 }
			 }
			 BattleField field = p.getField();
			 if ((field.getMechanics() instanceof JewelMechanics)
					 && (field.getEffectByType(RainEffect.class) != null)
					 && name.equals("Thunder")) {
				 return MoveList.getDefaultData().getMove("Thunder");
			 }
			 if (move.isAttack() || name.equals("Roar") || name.equals("Whirlwind")) {
				 PokemonMove replace =
					 new PokemonMove(PokemonType.T_TYPELESS, 0, move.getAccuracy(), 1) {
					 public int use(BattleMechanics mech, Pokemon source, Pokemon target) {
						 return m_event.use(move, mech, source, target);
					 }
				 };
				 return new MoveListEntry(entry.getName(), replace);
			 }
			 return entry;
		 }
	 }

	 private static class IngrainEffect extends StatusEffect {
		 public String getName() {
			 return "Ingrain";
		 }
		 public int getTier() {
			 return 3;
		 }
		 public String getDescription() {
			 return " planted its roots!";
		 }
		 public boolean tick(Pokemon p) {
			 int absorb = p.getStat(Pokemon.S_HP) / 16;
			 if (absorb == 0) {
				 absorb = 1;
			 }
			 p.getField().showMessage(p.getName() + " absorbed health!");
			 p.changeHealth(absorb);
			 return true;
		 }
		 public boolean canSwitch(Pokemon p) {
			 /** Research from AA confirms that Shed Shell allows you to switch
			  *  out of ingrain.
			  */
			 return p.hasItem("Shed Shell");
		 }
	 }

	 /**
	  * An effect that _effectively_ swaps the speeds of the pokemon in play.
	  */
	 public static class SpeedSwapEffect extends FieldEffect {
		 private int m_turns = 5;
		 public boolean applyToField(BattleField field) {   
			 return true;
		 }
		 public boolean tickField(BattleField field) {
			 if (--m_turns == 0) {
				 field.removeEffect(this);
				 return true;
			 }
			 return false;
		 }
		 public void unapplyToField(BattleField field) {
			 field.showMessage("The twisted dimensions returned to normal!");
		 }
		 public int getTier() {
			 return 1;
		 }
		 public String getDescription() {
			 return null;
		 }
		 public String getName() {
			 return "Trick room";
		 }
	 }

	 /**
	  * A counter used for protect, detect, and endure.
	  */
	 private static class CounterEffect extends StatusEffect {
		 private int m_count = 1;

		 public boolean tick(Pokemon p) {
			 return false;
		 }

		 public String getName() {
			 return null;
		 }

		 public String getDescription() {
			 return null;
		 }

		 public int getTier() {
			 return -1;
		 }

		 public boolean isMoveTransformer(boolean enemy) {
			 return !enemy;
		 }

		 public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
			 PokemonMove move = entry.getMove();

			 if ((move instanceof ProtectMove) && (p.getLastMove() != null)) {
				 ++m_count;
				 ((ProtectMove)move).setFailure(1.0 - 1.0 / (double)(1 << m_count));
			 } else {
				 p.removeStatus(this);
			 }
			 return entry;
		 }

		 public void informDuplicateEffect(Pokemon p) {
			 // Swallow the error.
		 }
	 }

	 public static class TrappingEffect extends StatusEffect {
		 private String m_name;

		 public TrappingEffect(String name) {
			 m_name = name;
		 }
		 public String getName() {
			 return m_name;
		 }
		 public boolean apply(Pokemon p) {
			 p.getOpponent().addStatus(p, new CoEffect(getClass()));
			 return super.apply(p);
		 }
		 public String getDescription() {
			 return " was trapped!";
		 }
		 public int getTier() {
			 return -1;
		 }
		 public boolean tick(Pokemon p) {
			 return false;
		 }
		 public boolean isSingleton() {
			 return false;
		 }
		 public boolean canSwitch(Pokemon p) {
			 return p.hasItem("Shed Shell");
		 }
	 }

	 private static class StockpileEffect extends StatusEffect {
		 private int m_levels = 0;
		 private List<StatusEffect> m_effects = new ArrayList<StatusEffect>();

		 public String getName() {
			 return "Stockpile";
		 }
		 public String getDescription() {
			 return null;
		 }
		 public int getTier() {
			 return -1;
		 }
		 public boolean tick(Pokemon p) {
			 return false;
		 }
		 public boolean incrementLevel(Pokemon p) {
			 if (m_levels < 3) {
				 m_levels++;
				 p.getField().showMessage(p.getName() + " stockpiled " + m_levels + "!");
				 if (p.getField().getMechanics() instanceof JewelMechanics) {
					 m_effects.add(
							 p.addStatus(p, new StatChangeEffect(Pokemon.S_DEFENCE, true)));
					 m_effects.add(
							 p.addStatus(p, new StatChangeEffect(Pokemon.S_SPDEFENCE, true)));
				 }
				 return true;
			 }
			 p.getField().showMessage(p.getName() + " couldn't stockpile any more!");
			 return false;
		 }
		 public int getLevels() {
			 return m_levels;
		 }
		 public boolean isSingleton() {
			 return true;
		 }
		 public boolean isPassable() {
			 return false;
		 }
		 public void informDuplicateEffect(Pokemon p) {
			 //Do nothing
		 }
		 public void unapply(Pokemon p) {
			 Iterator<StatusEffect> i = m_effects.iterator();
			 while (i.hasNext()) {
				 p.removeStatus((StatusEffect)i.next());
			 }
			 super.unapply(p);
		 }
	 }

	 /**
	  * A move that is based upon the number of levels stored in Stockpile
	  */
	 public static class StockpileMove extends PokemonMove {
		 public StockpileMove(PokemonType type, int power, double accuracy, int pp) {
			 super(type, power, accuracy, pp);
		 }
		 public StockpileEffect getStockpileEffect(Pokemon p) {
			 List<StatusEffect> statuses = p.getNormalStatuses(0);
			 Iterator<StatusEffect> i = statuses.iterator();
			 while (i.hasNext()) {
				 StatusEffect eff = (StatusEffect)i.next();
				 if (eff instanceof StockpileEffect) {
					 return (StockpileEffect)eff;
				 }
			 }
			 return null;
		 }
		 public int getLevels(Pokemon p) {
			 StockpileEffect eff = getStockpileEffect(p);
			 if (eff == null) return -1;
			 return eff.getLevels();
		 }
	 }
	 //Removes an effect from the opponent if the user switches out.    
	 public static class CoEffect extends StatusEffect {
		 private Class<?> m_type;
		 public CoEffect(Class<?> type) {
			 m_type = type;
		 }
		 public Class<?> getType() {
			 return m_type;
		 }
		 public String getName() {
			 return null;
		 }
		 public String getDescription() {
			 return null;
		 }
		 public int getTier() {
			 return -1;
		 }
		 public boolean tick(Pokemon p) {
			 return false;
		 }
		 public boolean switchOut(Pokemon p) {
			 p.getOpponent().removeStatus(m_type);
			 return true;
		 }
	 }

	 static class DefenseCurlEffect extends StatusEffect {
		 public String getName() {
			 return "Defense Curl";
		 }
		 public String getDescription() {
			 return null;
		 }
		 public int getTier() {
			 return -1;
		 }
		 public boolean tick(Pokemon p) {
			 return false;
		 }
		 public boolean isMoveTransformer(boolean enemy) {
			 return !enemy;
		 }
		 public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
			 if (entry.getName().equals("Rollout")) {
				 PokemonMove move = entry.getMove();
				 move.setPower(move.getPower() * 2);
			 }
			 return entry;
		 }
		 public boolean isPassable() {
			 return false;
		 }
		 public void informDuplicateEffect(Pokemon p) {
		 }
	 }

	 static class MinimizeEffect extends StatusEffect {
		 public String getName() {
			 return "Minimize";
		 }
		 public String getDescription() {
			 return null;
		 }
		 public int getTier() {
			 return -1;
		 }
		 public boolean tick(Pokemon p) {
			 return false;
		 }
		 public boolean isMoveTransformer(boolean enemy) {
			 return enemy;
		 }
		 public MoveListEntry getEnemyTransformedMove(Pokemon p, MoveListEntry entry) {
			 if (entry.getName().equals("Stomp") || entry.getName().equals("Extrasensory")) {
				 PokemonMove move = entry.getMove();
				 move.setPower(move.getPower() * 2);
			 }
			 return entry;
		 }
		 public boolean isPassable() {
			 return false;
		 }
		 public void informDuplicateEffect(Pokemon p) {
		 }
	 }

	 static class RechargeEffect extends StatusEffect {
		 private int m_turns;

		 public RechargeEffect(int turns) {
			 m_turns = turns;
		 }
		 public String getName() {
			 return "Recharge Effect";
		 }
		 public String getDescription() {
			 return null;
		 }
		 public int getTier() {
			 return 0;
		 }
		 public boolean tick(Pokemon p) {
			 if (m_turns-- <= 0) {
				 p.removeStatus(this);
			 }
			 return false;
		 }
		 public boolean canSwitch(Pokemon p) {
			 return false;
		 }
		 public boolean isMoveTransformer(boolean enemy) {
			 return !enemy;
		 }
		 public MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
			 p.getField().showMessage(p.getName() + " must recharge!");
			 return null;
		 }
	 }

	 /**
	  * Records the amount of damage done to a Pokemon in a turn. Should be applied in
	  * a beginTurn event.
	  */
	 static class DamageListenerEffect extends StatusEffect {
		 private int m_damage = 0;
		 private boolean m_special = true;

		 public String getName() {
			 return null;
		 }
		 public String getDescription() {
			 return null;
		 }
		 public int getTier() {
			 return 1;
		 }
		 public boolean tick(Pokemon p) {
			 p.removeStatus(this);
			 return true;
		 }
		 public boolean isListener() {
			 return true;
		 }
		 public void informDamaged(Pokemon source, Pokemon target, MoveListEntry move, int damage) {
			 m_damage += damage;
			 m_special = move.getMove().isSpecial(source.getField().getMechanics());
		 }
		 public int getDamage() {
			 return m_damage;
		 }
		 public boolean isSpecial() {
			 return m_special;
		 }
	 }

	 public static class SpikesEffect extends FieldEffect {
		 protected int m_layers[] = { 0, 0 };
		 protected int m_maxLayers = 3;
		 protected String m_message = "Spikes were scattered around the foe's team!";

		 public String getName() {
			 return "Spikes";
		 }
		 public String getDescription() {
			 return null;
		 }
		 public int getTier() {
			 return -1;
		 }
		 /**
		  * Returns the instance of a certain class of spikes on the field, if one is present.
		  */
		 public static SpikesEffect getSpikes(BattleField field, Class<?> type) {
			 List<?> effects = field.getEffectsByType(SpikesEffect.class);
			 if (effects.size() == 0) return null;
			 Iterator<?> i = effects.iterator();
			 while (i.hasNext()) {
				 SpikesEffect eff = (SpikesEffect)i.next();
				 if (eff.getClass().equals(type)) {
					 return eff;
				 }
			 }
			 return null;
		 }
		 /**
		  * Returns the number of layers of spikes that are applied to a Pokemon.
		  */
		 public int getLayers(Pokemon p) {
			 int team = p.getParty();
			 return m_layers[team];
		 }
		 /**
		  * Adds a layer of spikes to a Pokemon's team.
		  */
		 public void addSpikes(Pokemon p) {
			 int team = p.getParty();
			 int layers = getLayers(p);
			 if (layers >= m_maxLayers) {
				 p.getField().showMessage("But it failed!");
				 return;
			 }
			 m_layers[team]++;
			 if (m_message != null) {
				 p.getField().showMessage(m_message);
			 }
		 }
		 /**
		  * Removes spikes from a Pokemon's team.
		  */
		 public void removeSpikes(Pokemon p) {
			 int team = p.getParty();
			 m_layers[team] = 0;
		 }
		 public void switchIn(Pokemon p) {
			 BattleField field = p.getField();
			 if ((PokemonMove.getEffectiveness(PokemonType.T_GROUND, null, p) == 0.0) || p.hasAbility("Levitate")
					 || (getLayers(p) <= 0)) {
				 return;
			 }
			 int layers = getLayers(p);
			 int maximum = p.getStat(Pokemon.S_HP);
			 double factor = new double[] { 0.125, 0.1875, 0.25 }[layers - 1];
			 int damage = (int)(((double)maximum) * factor);
			 if (damage < 1) damage = 1;
			 field.showMessage(p.getName() + " was hurt by Spikes!");
			 p.changeHealth(-damage, true);
		 }
		 public boolean applyToField(BattleField field) {
			 field.showMessage("Spikes were scattered everywhere!");
			 return true;
		 }
		 public boolean tickField(BattleField field) {
			 return false;
		 }
	 }

	 public static class ToxicSpikesEffect extends SpikesEffect {
		 public ToxicSpikesEffect() {
			 m_maxLayers = 2;
			 m_message = "Toxic Spikes were scattered around the foe's team!";
		 }
		 public String getName() {
			 return "Toxic spikes";
		 }
		 public void switchIn(Pokemon p) {
			 BattleField field = p.getField();
			 if ((PokemonMove.getEffectiveness(PokemonType.T_GROUND, null, p) == 0.0)
					 || p.isType(PokemonType.T_STEEL)
					 || p.hasAbility("Levitate")
					 || (getLayers(p) <= 0)) {
				 return;
			 }
			 if (p.isType(PokemonType.T_POISON)) {
				 field.showMessage(p.getName() + " absorbed the Toxic Spikes!");
				 removeSpikes(p);
				 return;
			 }
			 int layers = getLayers(p);
			 if (layers == 1) {
				 if (p.addStatus(null, new PoisonEffect()) != null) {
					 field.showMessage(p.getName() + " was poisoned by the Toxic Spikes!");
				 }
			 } else if (layers == 2) {
				 if (p.addStatus(null, new ToxicEffect()) != null) {
					 field.showMessage(p.getName() + " was badly poisoned by the Toxic Spikes!");
				 }
			 }
		 }
	 }

	 public static class StealthRockEffect extends SpikesEffect {
		 public StealthRockEffect() {
			 m_maxLayers = 1;
			 m_message = null;
		 }
		 public String getName() {
			 return "Stealth rock";
		 }
		 public void switchIn(Pokemon p) {
			 if (getLayers(p) <= 0) {
				 return;
			 }
			 double effectiveness = PokemonMove.getEffectiveness(PokemonType.T_ROCK, null, p);
			 double baseDamage = p.getStat(Pokemon.S_HP) / 8.0;
			 int damage = (int)(baseDamage * effectiveness);
			 if (damage <= 1) damage = 1;
			 p.getField().showMessage("Pointed stones dug into " + p.getName() + ".");
			 p.changeHealth(-damage, true);
		 }
		 public boolean applyToField(BattleField field) {
			 field.showMessage("Pointed stones float in the air around your foe's team!");
			 return true;
		 }
	 }

	 public static class AttractEffect extends StatusEffect {
		 public String getName() {
			 return "Attract";
		 }
		 public boolean isPassable() {
			 return false;
		 }
		 public boolean hitsThroughSubstitute() {
			 return true;
		 }
		 public String getDescription() {
			 return " fell in love!";
		 }
		 public int getTier() {
			 return -1;
		 }
		 public boolean tick(Pokemon p) {
			 return false;
		 }
		 public boolean apply(Pokemon p) {           
			 int g1 = p.getGender();
			 int g2 = p.getOpponent().getGender();
			 if ((g1 == g2) ||
					 (g1 == PokemonSpecies.GENDER_NONE) ||
					 (g2 == PokemonSpecies.GENDER_NONE)) {
				 p.getField().showMessage("But it failed!");
				 return false;
			 }
			 if (p.hasAbility("Oblivious")) {
				 p.getField().showMessage(p.getName() + "'s Oblivious prevents attraction!");
				 return false;
			 }
			 p.getOpponent().addStatus(p, new CoEffect(getClass()));
			 return true;
		 }
		 public boolean immobilises(Pokemon p) {
			 p.getField().showMessage(p.getName() + " is in love with foe " + p.getOpponent().getName() + "!");
			 if (p.getField().getRandom().nextBoolean()) {
				 p.getField().showMessage(p.getName() + " is immobilised by love!");
				 return true;
			 }
			 return false;
		 }
	 }

	 /**
	  * Applys an effect after a certain number of turns.
	  */
	 static class DelayedStatusEffect extends FieldEffect {
		 protected int m_party;
		 protected int m_turns;
		 private StatusEffect m_effect;
		 private String m_message;

		 public DelayedStatusEffect(StatusEffect effect, int party, int turns, String message) {
			 m_effect = effect;
			 m_party = party;
			 m_turns = turns;
			 m_message = message;
		 }
		 public boolean isSingleton() {
			 return false;
		 }
		 public String getName() {
			 return null;
		 }
		 public String getDescription() {
			 return null;
		 }
		 public int getTier() {
			 return 1;
		 }
		 public boolean applyToField(BattleField field) {
			 field.showMessage(field.getActivePokemon()[m_party].getName() + m_message);
			 return true;
		 }
		 public boolean tickField(BattleField field) {
			 if (--m_turns == 0) {
				 Pokemon poke = field.getActivePokemon()[m_party];
				 poke.addStatus(poke.getOpponent(), m_effect);
				 field.removeEffect(this);
				 return true;
			 }
			 return false;
		 }
	 }

	 /**
	  * Deals an amount of damage to the active Pokemon a specified number of turns later.
	  */    
	 static class DelayedDamageEffect extends DelayedStatusEffect {
		 private int m_damage;

		 public DelayedDamageEffect(int damage, int party, int turns) {
			 super(null, party, turns, " foresaw an attack!");
			 m_damage = damage;
		 }
		 public boolean tickField(BattleField field) {
			 if (--m_turns == 0) {
				 if (m_damage <= 0) {
					 field.showMessage("But it failed!");
					 field.removeEffect(this);
					 return true;
				 }
				 Pokemon poke = field.getActivePokemon()[m_party];
				 field.showMessage(poke.getName() + " took the attack!");
				 poke.changeHealth(-m_damage);
				 field.removeEffect(this);
				 return true;
			 }
			 return false;
		 }
		 public int getTier() {
			 return 4;
		 }
	 }

	 /**
	  * Cuts a certain stat of an entire team for 5 turns.
	  */
	 static class StatCutEffect extends FieldEffect {
		 private int m_stat;
		 private String m_name;
		 private int m_party;
		 private int m_turns;

		 public StatCutEffect(int stat, int party, String name) {
			 m_stat = stat;
			 m_party = party;
			 m_name = name;
		 }
		 public String getName() {
			 return m_name;
		 }
		 public String getDescription() {
			 return null;
		 }
		 public int getTier() {
			 return 0;
		 }
		 public boolean isSingleton() {
			 return false;
		 }
		 public boolean isMoveTransformer(boolean enemy) {
			 return enemy;
		 }
		 protected MoveListEntry getEnemyTransformedMove(Pokemon p, MoveListEntry entry) {
			 if (p.getParty() != m_party) return entry;
			 PokemonMove move = entry.getMove();
			 boolean special = (m_stat == Pokemon.S_SPDEFENCE);
			 if (move.isSpecial(p.getField().getMechanics()) == special) {
				 move.setPower(move.getPower() / 2);
			 }
			 return entry;
		 }
		 public boolean equals(Object o2) {
			 if (!(o2 instanceof StatCutEffect)) return false;
			 StatCutEffect eff = (StatCutEffect)o2;
			 return ((m_stat == eff.m_stat) && (m_party == eff.m_party));
		 }
		 public boolean applyToField(BattleField field) {
			 Pokemon user = field.getActivePokemon()[m_party];
			 m_turns = user.hasItem("Light Clay") ? 8 : 5;
			 field.showMessage("A barrier was formed!");
			 return true;
		 }
		 public boolean tickField(BattleField field) {
			 if (m_turns-- <= 0) {
				 Pokemon[] pokes = field.getActivePokemon();
				 field.showMessage(pokes[m_party].getName() + "'s " + m_name + " wore off!");
				 field.removeEffect(this);
				 return true;
			 }
			 return false;
		 }

		 public int getParty() {
			 return m_party;
		 }
	 }

	 /**
	  * Cuts a certain stat of an entire team for 5 turns.
	  */
	 static class TailwindEffect extends FieldEffect {
		 private int m_party;
		 private int m_turns = 3;
		 public TailwindEffect(int party) {
			 m_party = party;
		 }
		 public String getName() {
			 return "Tailwind";
		 }
		 public String getDescription() {
			 return null;
		 }
		 public int getTier() {
			 //todo: no idea about tier
			 return 4;
		 }
		 public boolean isSingleton() {
			 return true;
		 }
		 public boolean equals(Object o2) {
			 if (!(o2 instanceof TailwindEffect)) return false;
			 TailwindEffect eff = (TailwindEffect)o2;
			 return m_party == eff.m_party;
		 }
		 public boolean applyToField(BattleField field) {
			 field.showMessage("A tailwind picked up!");
			 return true;
		 }
		 public boolean tickField(BattleField field) {
			 if (m_turns-- <= 0) {
				 field.showMessage("The tailwind died down.");
				 field.removeEffect(this);
				 return true;
			 }
			 return false;
		 }
		 public boolean apply(Pokemon p) {
			 if (p.getParty() == m_party) {
				 p.getMultiplier(Pokemon.S_SPEED).multiplyBy(2.0);
			 }
			 return super.apply(p);
		 }
		 public void unapply(Pokemon p) {
			 if (p.getParty() == m_party) {
				 p.getMultiplier(Pokemon.S_SPEED).divideBy(2.0);
			 }
			 super.unapply(p);
		 }
	 }

	 public static class MistEffect extends PartyEffect {
		 public MistEffect() {
			 super(" was shrouded in Mist!", "'s mist wore off.");
		 }
		 public String getName() {
			 return "Mist";
		 }
	 }

	 public static class SafeguardEffect extends PartyEffect {
		 public SafeguardEffect() {
			 super(" is protected by a veil!", "'s Safeguard wore off.");
		 }
		 public String getName() {
			 return "Safeguard";
		 }
		 public boolean allowsStatus(StatusEffect eff, Pokemon source, Pokemon target) {
			 if (!isActive(target.getParty())) return true;
			 if (source == target) return true;
			 return !((eff instanceof BurnEffect) || (eff instanceof FreezeEffect) || 
					 (eff instanceof ParalysisEffect) || (eff instanceof PoisonEffect) ||
					 (eff instanceof SleepEffect) || (eff instanceof ConfuseEffect));
		 }
	 }

	 public MoveList(boolean initialise) {
		 if (!initialise) {
			 return;
		 }
		 m_moves = new ArrayList<MoveListEntry>();
		 initNonStatusMoves();
		 initStatusMoves();
	 }

	 /**
	  * Get the move list.
	  */
	 public ArrayList<MoveListEntry> getMoveList() {
		 return m_moves;
	 }

	 /**
	  * Get a move by its name.
	  */
	 public MoveListEntry getMove(String name) {
		 if (name == null)
			 return null;
		 Iterator<MoveListEntry> i = m_moves.iterator();
		 while (i.hasNext()) {
			 MoveListEntry move = (MoveListEntry)i.next();
			 if (name.equalsIgnoreCase(move.getName())) {
				 return move;
			 }
		 }
		 return null;
	 }

	 /**
	  * Write data on all moves to an ouput stream.
	  */
	 public void saveMoveList(OutputStream output) throws IOException {
		 ObjectOutputStream stream = new ObjectOutputStream(output);
		 ArrayList<MoveListEntry> moves = getMoveList();
		 stream.writeInt(moves.size());
		 Iterator<MoveListEntry> i = moves.iterator();
		 while (i.hasNext()) {
			 MoveListEntry entry = (MoveListEntry)i.next();
			 stream.writeObject(entry.getName());
			 PokemonMove move = entry.getMove();
			 if (move instanceof StatusMove) {
				 StatusMove statusMove = (StatusMove)move;
				 StatusEffect[] effects = statusMove.getEffects();
				 for (int j = 0; j < effects.length; ++j) {
					 StatusEffect effect = effects[j];
					 if (effect instanceof ChargeEffect) {
						 ChargeEffect charge = (ChargeEffect)effect;
						 MoveListEntry chargeEntry = charge.getMove();
						 if (chargeEntry != null) {
							 PokemonMove chargeMove = chargeEntry.getMove();
							 if (chargeMove != null) {
								 move = chargeMove;
							 }
						 }
					 }
				 }
			 }

			 stream.writeObject(move.getType());
			 stream.writeInt(move.getPower());
			 stream.writeDouble(move.getAccuracy());
			 stream.writeInt(move.getPp());
		 }
		 stream.flush();
	 }

	 /**
	  * Read data on all moves from an input stream.
	  */
	 public void loadMoveList(InputStream input) throws IOException {
		 ObjectInputStream stream = new ObjectInputStream(input);
		 m_moves = new ArrayList<MoveListEntry>();
		 int size = stream.readInt();
		 m_moves.ensureCapacity(size);
		 for (int i = 0; i < size; ++i) {
			 try {
				 String name = (String)stream.readObject();
				 PokemonType type = (PokemonType)stream.readObject();
				 int power = stream.readInt();
				 double accuracy = stream.readDouble();
				 int pp = stream.readInt();
				 m_moves.add(new MoveListEntry(name,
						 new PokemonMove(type, power, accuracy, pp)));
			 } catch (ClassNotFoundException e) {
				 throw new InternalError();
			 }
		 }
	 }

	 /*
	  * Get a list of all move names.
	  */
	 public String[] getMoveNames() {
		 ArrayList<MoveListEntry> moves = m_moves;
		 String[] names = new String[moves.size()];
		 for (int i = 0; i < moves.size(); ++i) {
			 names[i] = ((MoveListEntry)moves.get(i)).getName();
		 }
		 return names;
	 }

}
