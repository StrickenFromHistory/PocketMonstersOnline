package org.pokenet.server.backend;

import org.pokenet.server.GameServer;
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
 * Processes an item using a thread
 * @author shadowkanji
 *
 */
public class ItemProcessor implements Runnable {
	/* An enum which handles Pokeball types */
	public enum PokeBall { POKEBALL, GREATBALL, ULTRABALL, MASTERBALL };
	private PlayerChar m_player;
	private String [] m_details;
	
	/**
	 * Constructor
	 * @param p
	 * @param details
	 */
	public ItemProcessor(PlayerChar p, String [] details) {
		m_player = p;
		m_details = details;
	}
	
	/**
	 * Executes the item usage
	 */
	public void run() {
		String [] data = new String[m_details.length - 1];
		for(int i = 1; i < m_details.length; i++)
			data[i - 1] = m_details[i];
		if(useItem(m_player, Integer.parseInt(m_details[0]), data)) {
			m_player.getBag().removeItem(Integer.parseInt(m_details[0]), 1);
			m_player.getSession().write("Ir" + m_details[0] + "," + 1);
		}
	}
	
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
		try {
			/* Check if the item is a repel or escape rope */
			if(i.getName().equalsIgnoreCase("REPEL")) {
				p.setRepel(100);
				return true;
			} else if(i.getName().equalsIgnoreCase("SUPER REPEL")) {
				p.setRepel(200);
				return true;
			} else if(i.getName().equalsIgnoreCase("MAX REPEL")) {
				p.setRepel(250);
				return true;
			} else if(i.getName().equalsIgnoreCase("ESCAPE ROPE")) {
				/* Warp the player to their last heal point */
				p.setX(p.getHealX());
				p.setY(p.getHealY());
				p.setMap(GameServer.getServiceManager().getMovementService().getMapMatrix().
						getMapByGamePosition(p.getHealMapX(), p.getHealMapY()));
				return true;
			}
			/* Else, determine what do to with the item */
			if(i.getAttributes().contains(ItemAttribute.MOVESLOT)) {
				/* TMs & HMs */
				try {
					/* Can't use a TM/HM during battle */
					if(p.isBattling())
						return false;
					/* Player is not in battle, learn the move */
					poke = p.getParty()[Integer.parseInt(data[0])];
					if(poke == null)
						return false;
					String moveName = i.getName().substring(5);
					/* Ensure the Pokemon can learn this move */
					if(DataService.getMoveSetData().getMoveSet(poke.getSpeciesNumber()).canLearn(moveName)) {
						poke.getMovesLearning().add(moveName);
						m_player.getSession().write("Pm" + data[0] + moveName);
						return true;
					}
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
					if(poke == null)
						return false;
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
					return true;
				} else if(i.getCategory().equalsIgnoreCase("EVOLUTION")) {
					/* Evolution items can't be used in battle */
					if(p.isBattling())
						return false;
					/* Get the pokemon's evolution data */
					poke = p.getParty()[Integer.parseInt(data[0])];
					/* Ensure poke exists */
					if(poke == null)
						return false;
					POLRDataEntry pokeData = DataService.getPOLRDatabase()
					.getPokemonData(
							DataService.getSpeciesDatabase()
									.getPokemonByName(poke.getSpeciesName()));
					for(int j = 0; j < pokeData.getEvolutions().size(); j++) {
						POLREvolution evolution = pokeData.getEvolutions().get(j);
						/*
						 * Check if this pokemon evolves by item
						 */
						if(evolution.getType() == EvoTypes.Item) {
							/*
							 * Check if the item is an evolution stone
							 * If so, evolve the Pokemon
							 */
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
				} else if(i.getCategory().equalsIgnoreCase("FOOD")) {
					poke = p.getParty()[Integer.parseInt(data[0])];
					if(poke == null)
						return false;
					if(i.getName().equalsIgnoreCase("LEPPA BERRY")) {
						int ppSlot = Integer.parseInt(data[1]);
						if(poke.getPp(ppSlot) + 10 <= poke.getMaxPp(ppSlot)) {
							poke.setPp(ppSlot, poke.getPp(ppSlot) + 10);
						} else {
							poke.setPp(ppSlot, poke.getMaxPp(ppSlot));
						}
						if(p.isBattling()) {
							try {
								p.getBattleField().queueMove(p.getBattleId(), BattleTurn.getMoveTurn(-1));
							} catch (Exception e) {}
						}
						return true;
					} else if(i.getName().equalsIgnoreCase("ORAN BERRY")) {
						poke.changeHealth(10);
						if(!p.isBattling())
							p.getSession().write("Ph" + data[0] + poke.getHealth());
						else
							try {
								p.getBattleField().queueMove(p.getBattleId(), BattleTurn.getMoveTurn(-1));
							} catch (Exception e) {}
					} else if(i.getName().equalsIgnoreCase("PERSIM BERRY")) {
						//TODO: Remove confusion
					} else if(i.getName().equalsIgnoreCase("LUM BERRY")) {
						poke.removeStatusEffects(true);
						return true;
					} else if(i.getName().equalsIgnoreCase("SITRUS BERRY")) {
						poke.changeHealth(30);
						if(!p.isBattling())
							p.getSession().write("Ph" + data[0] + poke.getHealth());
						else
							try {
								p.getBattleField().queueMove(p.getBattleId(), BattleTurn.getMoveTurn(-1));
							} catch (Exception e) {}
						return true;
					} else if(i.getName().equalsIgnoreCase("FIGY BERRY") || 
							i.getName().equalsIgnoreCase("WIKI BERRY") ||
							i.getName().equalsIgnoreCase("MAGO BERRY") ||
							i.getName().equalsIgnoreCase("AGUAV BERRY") ||
							i.getName().equalsIgnoreCase("IAPAPA BERRY")) {
						poke.changeHealth(poke.getRawStat(0) / 8);
						if(!p.isBattling())
							p.getSession().write("Ph" + data[0] + poke.getHealth());
						else
							try {
								p.getBattleField().queueMove(p.getBattleId(), BattleTurn.getMoveTurn(-1));
							} catch (Exception e) {}
						return true;
					}
				}
			} else if(i.getAttributes().contains(ItemAttribute.BATTLE)) {
				/* Pokeballs */
				if(i.getName().equalsIgnoreCase("POKE BALL")) {
					if(p.getBattleField() instanceof WildBattleField) {
						WildBattleField w = (WildBattleField) p.getBattleField();
						if(!w.throwPokeball(PokeBall.POKEBALL))
							w.queueMove(0, BattleTurn.getMoveTurn(-1));
						return true;
					}
				} else if(i.getName().equalsIgnoreCase("GREAT BALL")) {
					if(p.getBattleField() instanceof WildBattleField) {
						WildBattleField w = (WildBattleField) p.getBattleField();
						if(!w.throwPokeball(PokeBall.GREATBALL))
							w.queueMove(0, BattleTurn.getMoveTurn(-1));
						return true;
					}
				} else if(i.getName().equalsIgnoreCase("ULTRA BALL")) {
					if(p.getBattleField() instanceof WildBattleField) {
						WildBattleField w = (WildBattleField) p.getBattleField();
						if(!w.throwPokeball(PokeBall.ULTRABALL))
							w.queueMove(0, BattleTurn.getMoveTurn(-1));
						return true;
					}
				} else if(i.getName().equalsIgnoreCase("MASTER BALL")) {
					if(p.getBattleField() instanceof WildBattleField) {
						WildBattleField w = (WildBattleField) p.getBattleField();
						if(!w.throwPokeball(PokeBall.MASTERBALL))
							w.queueMove(0, BattleTurn.getMoveTurn(-1));
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
