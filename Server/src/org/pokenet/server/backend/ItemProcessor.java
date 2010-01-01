
package org.pokenet.server.backend;

import java.util.Random;

import org.pokenet.server.GameServer;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.backend.item.Item;
import org.pokenet.server.backend.item.Item.ItemAttribute;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.PokemonEvolution;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.PokemonEvolution.EvolutionTypes;
import org.pokenet.server.battle.impl.WildBattleField;
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
				!GameServer.getServiceManager().getItemDatabase().getItem(Integer.parseInt(m_details[0])).getName().contains("Rod")) {
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
		Item i = GameServer.getServiceManager().getItemDatabase().getItem(itemId);
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
					String message = "";
					if (poke == null) return false;
					if(i.getId() == 1) { //Potion
                        hpBoost = 20;
                        poke.changeHealth(hpBoost);
                        message = "You used Potion on "+poke.getName()+"/nThe Potion restored 20 HP";
	                } else if(i.getId()==2) {//Super Potion
	                        hpBoost = 50;
	                        poke.changeHealth(hpBoost);
	                        message = "You used Super Potion on "+poke.getName()+"/nThe Super Potion restored 50 HP";
	                } else if(i.getId()==3) { //Hyper Potion
	                        hpBoost = 200;
	                        poke.changeHealth(hpBoost);
	                        message = "You used Hyper Potion on "+poke.getName()+"/nThe Hyper Potion restored 200 HP";
	                } else if(i.getId()==4) {//Max Potion
	                        poke.changeHealth(poke.getRawStat(0));
	                        message = "You used Max Potion on "+poke.getName()+"/nThe Max Potion restored All HP";
	                } else {
	                        return false;
	                }
					if (!p.isBattling()) {
						/* Update the client */
						p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
						p.getTcpSession().write("Ii" + message);
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
					PokemonSpecies pokeData = PokemonSpecies.getDefaultData().getPokemonByName(
							poke.getSpeciesName());
					for (int j = 0; j < pokeData.getEvolutions().length; j++) {
						PokemonEvolution evolution = pokeData.getEvolutions()[j];
						/*
						 * Check if this pokemon evolves by item
						 */
						if (evolution.getType() == EvolutionTypes.Item) {
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
					if(i.getId() == 16) { //Antidote
            			String message = "You used Antidote on "+poke.getName()+"/nThe Antidote restored "+poke.getName()+" status to normal";
            			poke.removeStatus(PoisonEffect.class);
            			if(p.isBattling())
            				p.getBattleField().forceExecuteTurn();
            			else
            				p.getTcpSession().write("Ii" + message);
            			return true;
                    } else if(i.getId() == 17) { //Parlyz Heal
                    	String message = "You used Parlyz Heal on "+poke.getName()+"/nThe Parlyz Heal restored "+poke.getName()+" status to normal";
                    	poke.removeStatus(ParalysisEffect.class);
                    	if(p.isBattling())
                    		p.getBattleField().forceExecuteTurn();
                    	else
                    		p.getTcpSession().write("Ii" + message);
                    	return true;
                    } else if(i.getId() == 18) { //Awakening
                    	String message = "You used Awakening on "+poke.getName()+"/nThe Awakening restored "+poke.getName()+" status to normal";
                    	poke.removeStatus(SleepEffect.class);
                    	if(p.isBattling())
                    		p.getBattleField().forceExecuteTurn();
                    	else
                    		p.getTcpSession().write("Ii" + message);
                    	return true;
                    } else if(i.getId() == 19) { //Burn Heal
                    	String message = "You used Burn Heal on "+poke.getName()+"/nThe Burn Heal restored "+poke.getName()+" status to normal";
                    	poke.removeStatus(BurnEffect.class);
                    	if(p.isBattling())
                    		p.getBattleField().forceExecuteTurn();
                    	else
                    		p.getTcpSession().write("Ii" + message);
                    	return true;
                    } else if(i.getId() == 20) { //Ice Heal
                    	String message = "You used Ice Heal on "+poke.getName()+"/nThe Ice Heal restored "+poke.getName()+" status to normal";
                    	poke.removeStatus(FreezeEffect.class);
                    	if(p.isBattling())
                    		p.getBattleField().forceExecuteTurn();
                    	else
                    		p.getTcpSession().write("Ii" + message);
                    	return true;
                    } else if(i.getId() == 21) { //Full Heal
                    	String message = "You used Full Heal on "+poke.getName()+"/nThe Full Heal restored "+poke.getName()+" status to normal";
                    	poke.removeStatusEffects(true);
                    	if(p.isBattling())
                    		p.getBattleField().forceExecuteTurn();
                    	else
                    		p.getTcpSession().write("Ii" + message);
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
					if(i.getId() == 200) { //Cheri Berry
                    	String message = poke.getName()+" ate the Cheri Berry/nThe Cheri Berry restored "+poke.getName()+" status to normal";
                    	poke.removeStatus(ParalysisEffect.class);
                        if(p.isBattling())
                        	p.getBattleField().forceExecuteTurn();
                        else
                        	p.getTcpSession().write("Ii" + message);
                        return true;
                    } else if(i.getId() == 201) { //Chesto Berry
                    	String message = poke.getName()+" ate the Chesto Berry/nThe Chesto Berry restored "+poke.getName()+" status to normal";
                    	poke.removeStatus(SleepEffect.class);
                        if(p.isBattling())
                        	p.getBattleField().forceExecuteTurn();
                        else
                        	p.getTcpSession().write("Ii" + message);
                        return true;
                    } else if(i.getId() == 202) { //Pecha Berry
                    	String message = poke.getName()+" ate the Pecha Berry/nThe Pecha Berry restored "+poke.getName()+" status to normal";
                        poke.removeStatus(PoisonEffect.class);
                        if(p.isBattling())
                        	p.getBattleField().forceExecuteTurn();
                        else
                        	p.getTcpSession().write("Ii" + message);
                        return true;
                    } else if(i.getId() == 203) { //Rawst Berry
                    	String message = poke.getName()+" ate the Rawst Berry/nThe Rawst Berry restored "+poke.getName()+" status to normal";
                    	poke.removeStatus(BurnEffect.class);
                    	if(p.isBattling())
                        	p.getBattleField().forceExecuteTurn();
                        else
                        	p.getTcpSession().write("Ii" + message);
                        return true;
                    } else if(i.getId() == 204) { //Aspear Berry
                    	String message = poke.getName()+" ate the Aspear Berry/nThe Aspear Berry restored "+poke.getName()+" status to normal";
                        poke.removeStatus(FreezeEffect.class);
                        if(p.isBattling())
                        	p.getBattleField().forceExecuteTurn();
                        else
                        	p.getTcpSession().write("Ii" + message);
                        return true;
                    } else if(i.getId() == 205) { //Leppa Berry
                    	String message = "Leppa Berry had no effect"; // Move selection not completed, temp message TODO. Add support for this
                        int ppSlot = Integer.parseInt(data[1]);
                        if(poke.getPp(ppSlot) + 10 <= poke.getMaxPp(ppSlot))
                        	poke.setPp(ppSlot, poke.getPp(ppSlot) + 10);
                        else
                        	poke.setPp(ppSlot, poke.getMaxPp(ppSlot));
                        if(p.isBattling())
                        	p.getBattleField().forceExecuteTurn();
                        else
                        	p.getTcpSession().write("Ii" + message);
                        return true;
                    } else if(i.getId() == 206) { //Oran Berry
                    	String message = poke.getName()+" ate the Oran Berry/nThe Oran Berry restored 10HP";
                    	poke.changeHealth(10);
                        if(!p.isBattling()) {
                        	p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
                        	p.getTcpSession().write("Ii" + message);
                        }
                        else
                        	p.getBattleField().forceExecuteTurn();
                        return true;
                    } else if(i.getId() == 207) { //Persim Berry
                    	String message = poke.getName()+" ate the Persim Berry/nThe Persim Berry restored "+poke.getName()+" status to normal";
                    	poke.removeStatus(ConfuseEffect.class);
                        if(p.isBattling())
                        	p.getBattleField().forceExecuteTurn();
                        else
                        	p.getTcpSession().write("Ii" + message);
                        return true;
                    } else if(i.getId() == 208) { //Lum Berry
                    	String message = poke.getName()+" ate the Lum Berry/nThe Lum Berry restored "+poke.getName()+" status to normal";
                        poke.removeStatusEffects(true);
                        if(p.isBattling())
                        	p.getBattleField().forceExecuteTurn();
                        else
                           	p.getTcpSession().write("Ii" + message);
                        return true;
                    } else if(i.getId() == 209) { //Sitrus Berry
                    	String message = poke.getName()+" ate the Sitrus Berry/nThe Sitrus Berry restored 30HP";
                        poke.changeHealth(30);
                        if(!p.isBattling()) {
                          	p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
                        	p.getTcpSession().write("Ii" + message);
                        }
                        else
                        	p.getBattleField().forceExecuteTurn();
                        return true;
                    } else if(i.getId() == 210) { //Figy Berry
                    	String message = poke.getName()+" ate the Figy Berry/nThe Figy Berry restored" +poke.getRawStat(0) / 8+" HP to " +poke.getName()+"!";
                        poke.changeHealth(poke.getRawStat(0) / 8);
                        if(!p.isBattling()) {
                          	p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
                           	p.getTcpSession().write("Ii" + message);
                        }
                        else
                           	p.getBattleField().forceExecuteTurn();
                        return true;
                    } else if(i.getId() == 214) { //Wiki Berry
                    	String message = poke.getName()+" ate the Wiki Berry/nThe Wiki Berry restored" +poke.getRawStat(0) / 8+" HP to " +poke.getName()+"!";
                        poke.changeHealth(poke.getRawStat(0) / 8);
                        if(!p.isBattling()) {
                          	p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
                           	p.getTcpSession().write("Ii" + message);
                        }
                        else
                           	p.getBattleField().forceExecuteTurn();
                        return true;
                    } else if(i.getId() == 212) { //Mago Berry
                    	String message = poke.getName()+" ate the Mago Berry/nThe Mago Berry restored" +poke.getRawStat(0) / 8+" HP to " +poke.getName()+"!";
                        poke.changeHealth(poke.getRawStat(0) / 8);
                        if(!p.isBattling()) {
                          	p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
                           	p.getTcpSession().write("Ii" + message);
                        }
                        else
                           	p.getBattleField().forceExecuteTurn();
                        return true;
                    } else if(i.getId() == 213) { //Aguav Berry
                    	String message = poke.getName()+" ate the Aguav Berry/nThe Aguav Berry restored" +poke.getRawStat(0) / 8+" HP to " +poke.getName()+"!";
                        poke.changeHealth(poke.getRawStat(0) / 8);
                        if(!p.isBattling()) {
                          	p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
                           	p.getTcpSession().write("Ii" + message);
                        }
                        else
                           	p.getBattleField().forceExecuteTurn();
                        return true;
                    } else if(i.getId() == 214) { //Iapapa Berry
                    	String message = poke.getName()+" ate the Iapapa Berry/nThe Iapapa Berry restored" +poke.getRawStat(0) / 8+" HP to " +poke.getName()+"!";
                        poke.changeHealth(poke.getRawStat(0) / 8);
                        if(!p.isBattling()) {
                          	p.getTcpSession().write("Ph" + data[0] + poke.getHealth());
                           	p.getTcpSession().write("Ii" + message);
                        }
                        else
                           	p.getBattleField().forceExecuteTurn();
                        return true;
                    }else if (i.getId() == 800) { //Voltorb Lollipop
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
					} else if (i.getId() == 805) { //Gummilax
						String message = poke.getName()+" ate the Gummilax./n" +poke.getName()+" is happier!";
						int happiness = poke.getHappiness()+rand.nextInt(30);
						if(happiness<=255)
							poke.setHappiness(happiness);
						else
							poke.setHappiness(255);
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
					} else if (i.getId() == 806) { //Gengum
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
