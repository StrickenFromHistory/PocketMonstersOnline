
package org.pokenet.server.backend;

import java.util.Random;

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
import org.pokenet.server.battle.mechanics.statuses.BurnEffect;
import org.pokenet.server.battle.mechanics.statuses.ConfuseEffect;
import org.pokenet.server.battle.mechanics.statuses.FreezeEffect;
import org.pokenet.server.battle.mechanics.statuses.ParalysisEffect;
import org.pokenet.server.battle.mechanics.statuses.PoisonEffect;
import org.pokenet.server.battle.mechanics.statuses.SleepEffect;


/**
 * Processes an item using a thread
 * 
 * @author shadowkanji
 */
public class ItemProcessor implements Runnable {
	/* An enum which handles Pokeball types */
	public enum PokeBall {
		POKEBALL, GREATBALL, ULTRABALL, MASTERBALL
	};

	private final PlayerChar m_player;
	private final String[]   m_details;

	/**
	 * Constructor
	 * 
	 * @param p
	 * @param details
	 */
	public ItemProcessor(PlayerChar p, String[] details) {
		m_player = p;
		m_details = details;
	}

	/**
	 * Executes the item usage
	 */
	public void run() {
		String[] data = new String[m_details.length - 1];
		for (int i = 1; i < m_details.length; i++)
			data[i - 1] = m_details[i];
		if (useItem(m_player, Integer.parseInt(m_details[0]), data) &&
				!ItemDatabase.getInstance().getItem(Integer.parseInt(m_details[0])).getName().contains("Rod")) {
			m_player.getBag().removeItem(Integer.parseInt(m_details[0]), 1);
			m_player.getTcpSession().write("Ir" + m_details[0] + "," + 1);
		}
	}

	/**
	 * Uses an item in the player's bag. Returns true if it was used.
	 * 
	 * @param p
	 * @param itemId
	 * @param data
	 *          - extra data received from client
	 * @return
	 */
	public boolean useItem(PlayerChar p, int itemId, String[] data) {
		/* Check that the bag contains the item */
		if (p.getBag().containsItem(itemId) < 0) return false;
		/* We have the item, so let us use it */
		Item i = ItemDatabase.getInstance().getItem(itemId);
		/* Pokemon object we might need */
		Pokemon poke = null;
		try {
			/* Check if the item is a rod */
			if (i.getName().equalsIgnoreCase("OLD ROD")) {
				if(!p.isBattling() && !p.isFishing()) {
					p.fish(0);
					return true;
				}
			} else if(i.getName().equalsIgnoreCase("GOOD ROD")) {
				if(!p.isBattling() && !p.isFishing()) {
					if(p.getFishingLevel() >= 15) {
						p.fish(15);
					} else {
						// Notify client that you need a fishing level of 15 or higher for this rod
						p.getTcpSession().write("FF15");
					}
					return true;
				}
			} else if(i.getName().equalsIgnoreCase("GREAT ROD")) {
				if(!p.isBattling() && !p.isFishing()) {
					if(p.getFishingLevel() >= 50) {
						p.fish(35);
					} else {
						// Notify client that you need a fishing level of 50 or higher for this rod
						p.getTcpSession().write("FF50");
					}
					return true;
				}
			} else if(i.getName().equalsIgnoreCase("ULTRA ROD")) {
				if(!p.isBattling() && !p.isFishing()) {
					if(p.getFishingLevel() >= 70) {
						p.fish(50);
					} else {
						// Notify client that you need a fishing level of 70 or higher for this rod
						p.getTcpSession().write("FF70");
					}
					return true;
				}
			}
			/* Check if the item is a repel or escape rope */
			else if (i.getName().equalsIgnoreCase("REPEL")) {
				p.setRepel(100);
				return true;
			} else if (i.getName().equalsIgnoreCase("SUPER REPEL")) {
				p.setRepel(200);
				return true;
			} else if (i.getName().equalsIgnoreCase("MAX REPEL")) {
				p.setRepel(250);
				return true;
			} else if (i.getName().equalsIgnoreCase("ESCAPE ROPE")) {
				if (p.isBattling()) return false;
				/* Warp the player to their last heal point */
				p.setX(p.getHealX());
				p.setY(p.getHealY());
				p.setMap(GameServer.getServiceManager().getMovementService()
						.getMapMatrix()
						.getMapByGamePosition(p.getHealMapX(), p.getHealMapY()), null);
				return true;
			}
			/* Else, determine what do to with the item */
			if (i.getAttributes().contains(ItemAttribute.MOVESLOT)) {
				/* TMs & HMs */
				try {
					/* Can't use a TM/HM during battle */
					if (p.isBattling()) return false;
					/* Player is not in battle, learn the move */
					poke = p.getParty()[Integer.parseInt(data[0])];
					if (poke == null) return false;
					String moveName = i.getName().substring(5);
					/* Ensure the Pokemon can learn this move */
					if (DataService.getMoveSetData().getMoveSet(poke.getSpeciesNumber())
							.canLearn(moveName)) {
						poke.getMovesLearning().add(moveName);
						m_player.getTcpSession().write("Pm" + data[0] + moveName);
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			} else if (i.getAttributes().contains(ItemAttribute.POKEMON)) {
				/* Status healers, hold items, etc. */
				if (i.getCategory().equalsIgnoreCase("POTIONS")) {
					/*
					 * Potions
					 */
					int hpBoost = 0;
					poke = p.getParty()[Integer.parseInt(data[0])];
					if (poke == null) return false;
					if (i.getName().equalsIgnoreCase("POTION")) {
						hpBoost = 20;
						poke.changeHealth(hpBoost);
					} else if (i.getName().equalsIgnoreCase("SUPER POTION")) {
						hpBoost = 50;
						poke.changeHealth(hpBoost);
					} else if (i.getName().equalsIgnoreCase("HYPER POTION")) {
						hpBoost = 200;
						poke.changeHealth(hpBoost);
					} else if (i.getName().equalsIgnoreCase("MAX POTION")) {
						poke.changeHealth(poke.getRawStat(Pokemon.S_HP));
					} else if (i.getName().equalsIgnoreCase("FULL RESTORE")) {
						// restore full HP
						poke.changeHealth(poke.getRawStat(Pokemon.S_HP));
						// remove status effects
						poke.removeStatusEffects(true);
					} else {
						return false;
					}
					if (!p.isBattling()) {
						/* Update the client */
						p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
					} else {
						/* Player is in battle, take a hit from enemy */
						p.getBattleField().forceExecuteTurn();
					}
					return true;
				} else if (i.getCategory().equalsIgnoreCase("EVOLUTION")) {
					/* Evolution items can't be used in battle */
					if (p.isBattling()) return false;
					/* Get the pokemon's evolution data */
					poke = p.getParty()[Integer.parseInt(data[0])];
					/* Ensure poke exists */
					if (poke == null) return false;
					POLRDataEntry pokeData = DataService.getPOLRDatabase()
					.getPokemonData(
							DataService.getSpeciesDatabase().getPokemonByName(
									poke.getSpeciesName()));
					for (int j = 0; j < pokeData.getEvolutions().size(); j++) {
						POLREvolution evolution = pokeData.getEvolutions().get(j);
						/*
						 * Check if this pokemon evolves by item
						 */
						if (evolution.getType() == EvoTypes.Item) {
							/*
							 * Check if the item is an evolution stone If so, evolve the
							 * Pokemon
							 */
							if (i.getName().equalsIgnoreCase("FIRE STONE")
									&& evolution.getAttribute().equalsIgnoreCase("FIRESTONE")) {
								poke.setEvolution(evolution);
								poke.evolutionResponse(true, p);
								return true;
							} else if (i.getName().equalsIgnoreCase("WATER STONE")
									&& evolution.getAttribute().equalsIgnoreCase("WATERSTONE")) {
								poke.setEvolution(evolution);
								poke.evolutionResponse(true, p);
								return true;
							} else if (i.getName().equalsIgnoreCase("THUNDERSTONE")
									&& evolution.getAttribute().equalsIgnoreCase("THUNDERSTONE")) {
								poke.setEvolution(evolution);
								poke.evolutionResponse(true, p);
								return true;
							} else if (i.getName().equalsIgnoreCase("LEAF STONE")
									&& evolution.getAttribute().equalsIgnoreCase("LEAFSTONE")) {
								poke.setEvolution(evolution);
								poke.evolutionResponse(true, p);
								return true;
							} else if (i.getName().equalsIgnoreCase("MOON STONE")
									&& evolution.getAttribute().equalsIgnoreCase("MOONSTONE")) {
								poke.setEvolution(evolution);
								poke.evolutionResponse(true, p);
								return true;
							} else if (i.getName().equalsIgnoreCase("SUN STONE")
									&& evolution.getAttribute().equalsIgnoreCase("SUNSTONE")) {
								poke.setEvolution(evolution);
								poke.evolutionResponse(true, p);
								return true;
							} else if (i.getName().equalsIgnoreCase("SHINY STONE")
									&& evolution.getAttribute().equalsIgnoreCase("SHINYSTONE")) {
								poke.setEvolution(evolution);
								poke.evolutionResponse(true, p);
								return true;
							} else if (i.getName().equalsIgnoreCase("DUSK STONE")
									&& evolution.getAttribute().equalsIgnoreCase("DUSKSTONE")) {
								poke.setEvolution(evolution);
								poke.evolutionResponse(true, p);
								return true;
							} else if (i.getName().equalsIgnoreCase("DAWN STONE")
									&& evolution.getAttribute().equalsIgnoreCase("DAWNSTONE")) {
								poke.setEvolution(evolution);
								poke.evolutionResponse(true, p);
								return true;
							} else if (i.getName().equalsIgnoreCase("OVAL STONE")
									&& evolution.getAttribute().equalsIgnoreCase("OVALSTONE")) {
								poke.setEvolution(evolution);
								poke.evolutionResponse(true, p);
								return true;
							}
						}
					}
				} else if (i.getCategory().equalsIgnoreCase("MEDICINE")) {
					poke = p.getParty()[Integer.parseInt(data[0])];
					if (poke == null) return false;
					if (i.getName().equalsIgnoreCase("ANTIDOTE")) {
						poke.removeStatus(PoisonEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("PARALYZ HEAL")) {
						poke.removeStatus(ParalysisEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("AWAKENING")) {
						poke.removeStatus(SleepEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("BURN HEAL")) {
						poke.removeStatus(BurnEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("ICE HEAL")) {
						poke.removeStatus(FreezeEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("FULL HEAL")) {
						poke.removeStatusEffects(true);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("LAVA COOKIE")) {
						// just like a FULL HEAL
						poke.removeStatusEffects(true);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("OLD GATEAU")) {
						// just like a FULL HEAL
						poke.removeStatusEffects(true);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					}
				} else if (i.getCategory().equalsIgnoreCase("FOOD")) {
					poke = p.getParty()[Integer.parseInt(data[0])];
					Random rand = new Random();
					if (poke == null) return false;
					if (i.getName().equalsIgnoreCase("CHERI BERRY")) {
						poke.removeStatus(ParalysisEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("CHESTO BERRY")) {
						poke.removeStatus(SleepEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("PECHA BERRY")) {
						poke.removeStatus(PoisonEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("RAWST BERRY")) {
						poke.removeStatus(BurnEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("ASPEAR BERRY")) {
						poke.removeStatus(FreezeEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("LEPPA BERRY")) {
						int ppSlot = Integer.parseInt(data[1]);
						if (poke.getPp(ppSlot) + 10 <= poke.getMaxPp(ppSlot)) {
							poke.setPp(ppSlot, poke.getPp(ppSlot) + 10);
						} else {
							poke.setPp(ppSlot, poke.getMaxPp(ppSlot));
						}
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("ORAN BERRY")) {
						poke.changeHealth(10);
						if (!p.isBattling()) p.getTcpSession().write(
								"Ph" + data[0] + poke.getHealth());
						else {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("PERSIM BERRY")) {
						poke.removeStatus(ConfuseEffect.class);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("LUM BERRY")) {
						poke.removeStatusEffects(true);
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}
						return true;
					} else if (i.getName().equalsIgnoreCase("SITRUS BERRY")) {
						poke.changeHealth(30);
						if (!p.isBattling()) p.getTcpSession().write(
								"Ph" + data[0] + poke.getHealth());
						else
							p.getBattleField().forceExecuteTurn();
						return true;
					} else if (i.getName().equalsIgnoreCase("FIGY BERRY")
							|| i.getName().equalsIgnoreCase("WIKI BERRY")
							|| i.getName().equalsIgnoreCase("MAGO BERRY")
							|| i.getName().equalsIgnoreCase("AGUAV BERRY")
							|| i.getName().equalsIgnoreCase("IAPAPA BERRY")) {
						poke.changeHealth(poke.getRawStat(Pokemon.S_HP) / 8);
						if (!p.isBattling()) p.getTcpSession().write(
								"Ph" + data[0] + poke.getHealth());
						else
							p.getBattleField().forceExecuteTurn();
						return true;
					} else if (i.getId() == 800) { //Voltorb Lollipop
						String message = poke.getName()+" ate the Voltorb Lollipop/nThe Lollipop restored 50 HP to " +poke.getName()+"!";
						poke.changeHealth(50);
						int random = rand.nextInt(10);
						if(random <3){
							poke.addStatus(new ParalysisEffect());
							message+="/n"+poke.getName()+" was Paralyzed from the Lollipop!";
						}
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}else{
							p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
							p.getTcpSession().write("Ii" + message);
						}
						return true;
					} else if (i.getId() == 801) { //Sweet Chills
						String message = poke.getName()+" ate the Sweet Chill/nThe Sweet Chill restored " +poke.getName()+"'s moves!";
						for(int ppSlot=0;ppSlot<4;ppSlot++){
							if (poke.getPp(ppSlot) + 5 <= poke.getMaxPp(ppSlot)) {
								poke.setPp(ppSlot, poke.getPp(ppSlot) + 5);
							} else {
								poke.setPp(ppSlot, poke.getMaxPp(ppSlot));
							}
						}
						int random = rand.nextInt(10);
						if(random <3){
							try{
							poke.addStatus(new FreezeEffect());
							message+="/n"+poke.getName()+" was frozen solid from the cold candy!";
							}catch(Exception e){}//Already under a status effect. 
						}
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}else
							p.getTcpSession().write("Ii" + message);
						return true;
					}else if (i.getId() == 802) { //Cinnamon Candy
						String message = poke.getName()+" ate the Cinnamon Candy./nThe Cinnamon Candy restored " +poke.getName()+"'s status to normal!";
						poke.removeStatusEffects(true);
						int random = rand.nextInt(10);
						if(random <3){
							poke.addStatus(new BurnEffect());
							message+="/n"+poke.getName()+" was burned from the candy!";
						}
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}else{
							p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
							p.getTcpSession().write("Ii"+message);
						}
						return true;
					} else if (i.getId() == 803) { //Candy Corn
						String message = poke.getName()+" ate the Candy Corn./n" +poke.getName()+" is happier!";
						int happiness = poke.getHappiness()+15;
						if(happiness<=300)
							poke.setHappiness(happiness);
						else
							poke.setHappiness(300);
						int random = rand.nextInt(10);
						if(random <3){
							poke.addStatus(new PoisonEffect());
							message+="/n"+poke.getName()+" got Poisoned from the rotten candy!";
						}
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}else
							p.getTcpSession().write("Ii"+message);
						return true;
					} else if (i.getId() == 804) { //Poke'Choc
						String message = poke.getName()+" ate the Poke'Choc Bar!/n" +poke.getName()+" is happier!";
						int happiness = poke.getHappiness()+10;
						if(happiness<=300)
							poke.setHappiness(happiness);
						else
							poke.setHappiness(300);
						int random = rand.nextInt(10);
						if(random <=3){
							poke.changeHealth(30);
							message+="/n"+poke.getName()+" recovered 30HP.";
						}
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}else
							p.getTcpSession().write("Ii"+message);
						return true;
					} else if (i.getId() == 805) { //Sour Candy
						String message = poke.getName()+" ate the Gummilax./n" +poke.getName()+" is happier!";
						int happiness = poke.getHappiness()+rand.nextInt(30);
						if(happiness<=300)
							poke.setHappiness(happiness);
						else
							poke.setHappiness(300);
						int random = rand.nextInt(10);
						if(random <3){
							poke.addStatus(new ParalysisEffect());
							message+="/nThe gummi was too sweet for "+poke.getName()+"./n"+poke.getName()+" fell asleep!";
						}
						if (p.isBattling()) {
							p.getBattleField().forceExecuteTurn();
						}else
							p.getTcpSession().write("Ii"+message);
						return true;
					} else if (i.getId() == 806) { //Funball
						String message = poke.getName()+" ate the Gengum.";
						int randHealth = rand.nextInt(100);
						randHealth-=20;
						if(poke.getHealth()+randHealth<0)
							poke.setHealth(1);
						else
							poke.changeHealth(randHealth);
						if(randHealth>0)
							message+="/n"+poke.getName()+" healed "+randHealth+"HP";
						else
							message+="/n"+poke.getName()+" lost "+-randHealth+"HP";
						if (p.isBattling()) {
							p.getBattleField().queueMove(0,BattleTurn.getMoveTurn(-1));
						}else{
							p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
							p.getTcpSession().write("Ii"+message);
						}
						return true;
					}
				}
			} else if (i.getAttributes().contains(ItemAttribute.BATTLE)) {
				/* Pokeballs */
				if (i.getName().equalsIgnoreCase("POKE BALL")) {
					if (p.getBattleField() instanceof WildBattleField) {
						WildBattleField w = (WildBattleField) p.getBattleField();
						if (!w.throwPokeball(PokeBall.POKEBALL))
							w.queueMove(0, BattleTurn.getMoveTurn(-1));
						return true;
					}
				} else if (i.getName().equalsIgnoreCase("GREAT BALL")) {
					if (p.getBattleField() instanceof WildBattleField) {
						WildBattleField w = (WildBattleField) p.getBattleField();
						if (!w.throwPokeball(PokeBall.GREATBALL))
							w.queueMove(0, BattleTurn.getMoveTurn(-1));
						return true;
					}
				} else if (i.getName().equalsIgnoreCase("ULTRA BALL")) {
					if (p.getBattleField() instanceof WildBattleField) {
						WildBattleField w = (WildBattleField) p.getBattleField();
						if (!w.throwPokeball(PokeBall.ULTRABALL))
							w.queueMove(0, BattleTurn.getMoveTurn(-1));
						return true;
					}
				} else if (i.getName().equalsIgnoreCase("MASTER BALL")) {
					if (p.getBattleField() instanceof WildBattleField) {
						WildBattleField w = (WildBattleField) p.getBattleField();
						if (!w.throwPokeball(PokeBall.MASTERBALL))
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
