package org.pokenet.server.backend;

import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.backend.item.Item;
import org.pokenet.server.backend.item.ItemDatabase;
import org.pokenet.server.backend.item.Item.ItemAttribute;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.impl.WildBattleField;
import org.pokenet.server.battle.mechanics.polr.POLRDataEntry;
import org.pokenet.server.battle.mechanics.polr.POLREvolution;
import org.pokenet.server.battle.mechanics.polr.POLREvolution.EvoTypes;

/**
 * Processes an item
 * @author shadowkanji
 *
 */
public class ItemProcessor {
	/* An enum which handles Pokeball types */
	public enum PokeBall { POKEBALL, GREATBALL, ULTRABALL, MASTERBALL };
	/**
	 * Uses an item in the player's bag. Returns true if it was used.
	 * @param p
	 * @param itemId
	 * @param data    - extra data received from client
	 * @return
	 */
	public boolean useItem(PlayerChar p, int itemId, String [] data) {
		/* Check that the bag contains the item */
		if(p.getBag().containsItem(itemId) < 0)
			return false;
		/* We have the item, so let us use it */
		Item i = ItemDatabase.getInstance().getItem(itemId);
		/* Pokemon object we might need */
		Pokemon poke = null;
		/* Determine what do to with the item */
		if(i.getAttributes().contains(ItemAttribute.MOVESLOT)) {
			/* TMs & HMs */
			try {
				if(p.isBattling())
					return false;
				poke = p.getParty()[Integer.parseInt(data[0])];
				poke.learnMove(Integer.parseInt(data[1]), i.getName().substring(5));
				p.getSession().write("PM" + data[0] + data[1] + i.getName().substring(5));
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else if(i.getAttributes().contains(ItemAttribute.POKEMON)) {
			/* Status healers, hold items, etc. */
			if(i.getCategory().equalsIgnoreCase("POTIONS")) {
				/*
				 * Potions
				 */
				int hpBoost = 0;
				poke = p.getParty()[Integer.parseInt(data[0])];
				if(i.getName().equalsIgnoreCase("POTION")) {
					hpBoost = 20;
					poke.changeHealth(hpBoost);
				} else if(i.getName().equalsIgnoreCase("SUPER POTION")) {
					hpBoost = 50;
					poke.changeHealth(hpBoost);
				} else if(i.getName().equalsIgnoreCase("HYPER POTION")) {
					hpBoost = 200;
					poke.changeHealth(hpBoost);
				} else if(i.getName().equalsIgnoreCase("MAX POTION")) {
					poke.changeHealth(poke.getRawStat(0));
				} else {
					return false;
				}
				if(!p.isBattling()) {
					/* Update the client */
					p.getSession().write("Ph" + data[0] + poke.getHealth());
				} else {
					/* Player is in battle, take a hit from enemy */
					try {
						p.getBattleField().queueMove(p.getBattleId(), BattleTurn.getMoveTurn(-1));
					} catch (Exception e) {}
				}
			} else if(i.getCategory().equalsIgnoreCase("EVOLUTION")) {
				poke = p.getParty()[Integer.parseInt(data[0])];
				POLRDataEntry pokeData = DataService.getPOLRDatabase()
				.getPokemonData(
						DataService.getSpeciesDatabase()
								.getPokemonByName(poke.getSpeciesName()));
				for(int j = 0; j < pokeData.getEvolutions().size(); j++) {
					POLREvolution evolution = pokeData.getEvolutions().get(j);
					if(evolution.getType() == EvoTypes.Item) {
						if(i.getName().equalsIgnoreCase("FIRE STONE")
								|| evolution.getAttribute().equalsIgnoreCase("FIRESTONE")) {
							poke.setEvolution(evolution);
							poke.evolutionResponse(true, p);
							return true;
						} else if(i.getName().equalsIgnoreCase("WATER STONE")
								|| evolution.getAttribute().equalsIgnoreCase("WATERSTONE")) {
							poke.setEvolution(evolution);
							poke.evolutionResponse(true, p);
							return true;
						} else if(i.getName().equalsIgnoreCase("THUNDERSTONE")
								|| evolution.getAttribute().equalsIgnoreCase("THUNDERSTONE")) {
							poke.setEvolution(evolution);
							poke.evolutionResponse(true, p);
							return true;
						} else if(i.getName().equalsIgnoreCase("LEAF STONE")
								|| evolution.getAttribute().equalsIgnoreCase("LEAFSTONE")) {
							poke.setEvolution(evolution);
							poke.evolutionResponse(true, p);
							return true;
						} else if(i.getName().equalsIgnoreCase("MOON STONE")
								|| evolution.getAttribute().equalsIgnoreCase("MOONSTONE")) {
							poke.setEvolution(evolution);
							poke.evolutionResponse(true, p);
							return true;
						} else if(i.getName().equalsIgnoreCase("SUN STONE")
								|| evolution.getAttribute().equalsIgnoreCase("SUNSTONE")) {
							poke.setEvolution(evolution);
							poke.evolutionResponse(true, p);
							return true;
						} else if(i.getName().equalsIgnoreCase("SHINY STONE")
								|| evolution.getAttribute().equalsIgnoreCase("SHINYSTONE")) {
							poke.setEvolution(evolution);
							poke.evolutionResponse(true, p);
							return true;
						} else if(i.getName().equalsIgnoreCase("DUSK STONE")
								|| evolution.getAttribute().equalsIgnoreCase("DUSKSTONE")) {
							poke.setEvolution(evolution);
							poke.evolutionResponse(true, p);
							return true;
						} else if(i.getName().equalsIgnoreCase("DAWN STONE")
								|| evolution.getAttribute().equalsIgnoreCase("DAWNSTONE")) {
							poke.setEvolution(evolution);
							poke.evolutionResponse(true, p);
							return true;
						} else if(i.getName().equalsIgnoreCase("OVAL STONE")
								|| evolution.getAttribute().equalsIgnoreCase("OVALSTONE")) {
							poke.setEvolution(evolution);
							poke.evolutionResponse(true, p);
							return true;
						}
					}
				}
			}
		} else if(i.getAttributes().contains(ItemAttribute.BATTLE)) {
			/* Pokeballs */
			if(i.getName().equalsIgnoreCase("POKE BALL")) {
				if(p.getBattleField() instanceof WildBattleField) {
					WildBattleField w = (WildBattleField) p.getBattleField();
					w.throwPokeball(PokeBall.POKEBALL);
					return true;
				}
			} else if(i.getName().equalsIgnoreCase("GREAT BALL")) {
				if(p.getBattleField() instanceof WildBattleField) {
					WildBattleField w = (WildBattleField) p.getBattleField();
					w.throwPokeball(PokeBall.GREATBALL);
					return true;
				}
			} else if(i.getName().equalsIgnoreCase("ULTRA BALL")) {
				if(p.getBattleField() instanceof WildBattleField) {
					WildBattleField w = (WildBattleField) p.getBattleField();
					w.throwPokeball(PokeBall.ULTRABALL);
					return true;
				}
			} else if(i.getName().equalsIgnoreCase("MASTER BALL")) {
				if(p.getBattleField() instanceof WildBattleField) {
					WildBattleField w = (WildBattleField) p.getBattleField();
					w.throwPokeball(PokeBall.MASTERBALL);
					return true;
				}
			}
		}
		return false;
	}
}
