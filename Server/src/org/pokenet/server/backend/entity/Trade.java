package org.pokenet.server.backend.entity;

import java.util.HashMap;
import java.util.Iterator;

import org.pokenet.server.backend.entity.TradeObject.TradeType;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.mechanics.polr.POLRDataEntry;
import org.pokenet.server.battle.mechanics.polr.POLREvolution;
import org.pokenet.server.battle.mechanics.polr.POLREvolution.EvoTypes;

/**
 * A trade between two players
 * @author shadowkanji
 *
 */
public class Trade {
	/* Stores the offers */
	private HashMap<PlayerChar, TradeObject[]> m_offers;
	public boolean m_isExecuting = false;
	
	/**
	 * Constructor
	 * @param player1
	 * @param player2
	 */
	public Trade(PlayerChar player1, PlayerChar player2) {
		m_offers = new HashMap<PlayerChar, TradeObject[]>();
		m_offers.put(player1, null);
		m_offers.put(player2, null);
		/* Tell the clients to open the window */
		player1.getSession().write("Ts" + player2.getName());
		player2.getSession().write("Ts" + player1.getName());
		
		/*
		 * Send poke data to both clients
		 */
		for(int i = 0; i < player1.getParty().length; i++) {
			if(player1.getParty()[i] != null) {
				player2.getSession().write("Ti" + i + PokemonSpecies.getDefaultData().getPokemonByName(player1.getParty()[i].getSpeciesName()) + "," +
						player1.getParty()[i].getName() + "," +
						player1.getParty()[i].getHealth() + "," +
						player1.getParty()[i].getGender() + "," +
						(player1.getParty()[i].isShiny() ? 1 : 0) + "," +
						player1.getParty()[i].getStat(0) + "," +
						player1.getParty()[i].getStat(1) + "," +
						player1.getParty()[i].getStat(2) + "," +
						player1.getParty()[i].getStat(3) + "," +
						player1.getParty()[i].getStat(4) + "," +
						player1.getParty()[i].getStat(5) + "," +
						player1.getParty()[i].getTypes()[0] + "," +
						(player1.getParty()[i].getTypes().length > 1 &&
								player1.getParty()[i].getTypes()[1] != null ? player1.getParty()[i].getTypes()[1] + "," : ",") +
								player1.getParty()[i].getExp() + "," +
								player1.getParty()[i].getLevel() + "," +
								player1.getParty()[i].getAbilityName() + "," +
								player1.getParty()[i].getNature().getName() + "," +
						(player1.getParty()[i].getMoves()[0] != null ? player1.getParty()[i].getMoves()[0].getName() : "") + "," +
						(player1.getParty()[i].getMoves()[1] != null ? player1.getParty()[i].getMoves()[1].getName() : "") + "," +
						(player1.getParty()[i].getMoves()[2] != null ? player1.getParty()[i].getMoves()[2].getName() : "") + "," +
						(player1.getParty()[i].getMoves()[3] != null ? player1.getParty()[i].getMoves()[3].getName() : ""));
			}
		}
		for(int i = 0; i < player2.getParty().length; i++) {
			if(player2.getParty()[i] != null) {
				player1.getSession().write("Ti" + i + PokemonSpecies.getDefaultData().getPokemonByName(player2.getParty()[i].getSpeciesName()) + "," +
						player2.getParty()[i].getName() + "," +
						player2.getParty()[i].getHealth() + "," +
						player2.getParty()[i].getGender() + "," +
						(player2.getParty()[i].isShiny() ? 1 : 0) + "," +
						player2.getParty()[i].getStat(0) + "," +
						player2.getParty()[i].getStat(1) + "," +
						player2.getParty()[i].getStat(2) + "," +
						player2.getParty()[i].getStat(3) + "," +
						player2.getParty()[i].getStat(4) + "," +
						player2.getParty()[i].getStat(5) + "," +
						player2.getParty()[i].getTypes()[0] + "," +
						(player2.getParty()[i].getTypes().length > 1 &&
								player2.getParty()[i].getTypes()[1] != null ? player2.getParty()[i].getTypes()[1] + "," : ",") +
								player2.getParty()[i].getExp() + "," +
								player2.getParty()[i].getLevel() + "," +
								player2.getParty()[i].getAbilityName() + "," +
								player2.getParty()[i].getNature().getName() + "," +
						(player2.getParty()[i].getMoves()[0] != null ? player2.getParty()[i].getMoves()[0].getName() : "") + "," +
						(player2.getParty()[i].getMoves()[1] != null ? player2.getParty()[i].getMoves()[1].getName() : "") + "," +
						(player2.getParty()[i].getMoves()[2] != null ? player2.getParty()[i].getMoves()[2].getName() : "") + "," +
						(player2.getParty()[i].getMoves()[3] != null ? player2.getParty()[i].getMoves()[3].getName() : ""));
			}
		}
	}
	
	/**
	 * Sets the offer from a player
	 * @param p
	 * @param o
	 */
	public void setOffer(PlayerChar p, int poke, int money) {
		if(p.getMoney() >= money) {
			TradeObject [] o = new TradeObject[2];
			o[0] = new TradeObject();
			o[0].setId(poke);
			o[0].setType(TradeType.POKEMON);
			
			o[1] = new TradeObject();
			o[1].setQuantity(money);
			o[1].setType(TradeType.MONEY);
			
			m_offers.put(p, o);
			/* Send the offer to the other player */
			sendOfferInformation(p, poke, money);
		}
	}
	
	/**
	 * Cancels an offer from a player
	 * @param p
	 */
	public void cancelOffer(PlayerChar p) {
		Iterator<PlayerChar> it = m_offers.keySet().iterator();
		PlayerChar otherPlayer = null;
		/* Find the other player */
		while(it.hasNext()) {
			PlayerChar temp = it.next();
			if(temp != p) {
				otherPlayer = temp;
			}
		}
		/* Check the other player hasn't accepted a previous offer */
		if(!otherPlayer.acceptedTradeOffer()) {
			m_offers.put(p, null);
			otherPlayer.getSession().write("Tc");
		}
	}
	
	/**
	 * Sends offer information from one player to another
	 * @param p
	 * @param poke
	 * @param money
	 */
	private void sendOfferInformation(PlayerChar p, int poke, int money) {
		Iterator<PlayerChar> i = m_offers.keySet().iterator();
		while(i.hasNext()) {
			PlayerChar player = i.next();
			if(player.getId() != p.getId()) {
				/* This is player we want to send data to */
				player.getSession().write("To" + poke + "," + money);
				return;
			}
		}
	}
	
	/**
	 * Checks if both player's agree to trade
	 */
	public void checkForExecution() {
		Iterator<PlayerChar> i = m_offers.keySet().iterator();
		if(i.next().acceptedTradeOffer() && i.next().acceptedTradeOffer()) {
			executeTrade();
		}
	}
	
	/**
	 * Executes the trade
	 */
	private void executeTrade() {
		/* Ensure two threads can't cause execute the trade */
		if(!m_isExecuting) {
			m_isExecuting = true;
			Pokemon [] temp = new Pokemon[2];
			
			Iterator<PlayerChar> it = m_offers.keySet().iterator();
			PlayerChar player1 = it.next();
			PlayerChar player2 = it.next();
			TradeObject [] o1 = m_offers.get(player1);
			TradeObject [] o2 = m_offers.get(player2);
			
			/* Ensure each player has made an offer */
			if(o1 == null || o2 == null)
				return;
			
			/* Keep checking no player has left the trade */
			if(player1 != null && player2 != null) {
				/* Handle player 1's offers */
				for(int j = 0; j < o1.length; j++) {
					switch(o1[j].getType()) {
					case POKEMON:
						/* 
						 * An id greater than 5 or less an 0 is sent 
						 * if no pokemon is being traded 
						 */
						if(o1[j].getId() >= 0 && o1[j].getId() <= 5) {
							/* Store the Pokemon temporarily */
							temp[0] = player1.getParty()[o1[j].getId()];
							player1.getParty()[o1[j].getId()] = null;
						}
						break;
					case MONEY:
						/* Ensure there was money offered */
						if(o1[j].getQuantity() > 0) {
							player1.setMoney(player1.getMoney() - o1[j].getQuantity());
							player2.setMoney(player2.getMoney() + o1[j].getQuantity());
						}
						break;
					case ITEM:
						break;
					}
				}
				
				/* Handle player 2's offers */
				for(int j = 0; j < o2.length; j++) {
					switch(o2[j].getType()) {
					case POKEMON:
						/* 
						 * An id greater than 5 or less an 0 is sent 
						 * if no pokemon is being traded 
						 */
						if(o2[j].getId() >= 0 && o2[j].getId() <= 5) {
							/* Store the Pokemon temporarily */
							temp[1] = player2.getParty()[o2[j].getId()];
							player2.getParty()[o2[j].getId()] = null;
						}
						break;
					case MONEY:
						/* Ensure there was money offered */
						if(o2[j].getQuantity() > 0) {
							player2.setMoney(player2.getMoney() - o2[j].getQuantity());
							player1.setMoney(player1.getMoney() + o2[j].getQuantity());
						}
						break;
					case ITEM:
						break;
					}
				}
				
				/* Execute the Pokemon swap */
				if(temp[1] != null)
					player1.addPokemon(temp[1]);
				if(temp[0] != null)
					player2.addPokemon(temp[0]);
				
				/* Evolution checks */
				for(int i = 0; i < 2; i++) {
					if(temp[i] != null) {
						/*
						 * Find if the pokemon is in the player's party
						 * If not, we can't evolve it
						 */
						int index = -1;
						if(i == 0 && player2.getPokemonIndex(temp[i]) > -1) {
							index = player2.getPokemonIndex(temp[i]);
						} else if(i == 1 && player1.getPokemonIndex(temp[i]) > -1) {
							index = player2.getPokemonIndex(temp[i]);
						} else {
							continue;
						}
						if(index > -1) {
							/*
							 * See if this Pokemon evolves by trade
							 */
							POLRDataEntry pokeData = DataService.getPOLRDatabase()
							.getPokemonData(
									DataService.getSpeciesDatabase()
											.getPokemonByName(temp[i].getSpeciesName()));
							POLREvolution evolution = null;
							for(int j = 0; j < pokeData.getEvolutions().size(); j++) {
								/*
								 * If it evolves by trade, tell the client
								 */
								evolution = pokeData.getEvolutions().get(j);
								if(evolution != null && evolution.getType() == EvoTypes.Trade) {
									temp[i].setEvolution(evolution);
									if(i == 0)
										player2.getSession().write("PE" + index);
									else
										player1.getSession().write("PE" + index);
									break;
								}
							}
						}
					}
				}
				
				/* Update the money */
				player1.updateClientMoney();
				player2.updateClientMoney();
				/* End the trade */
				m_isExecuting = false;
				endTrade();
			}
		}
	}
	
	/**
	 * Returns true if the trade was ended
	 */
	public boolean endTrade() {
		if(!m_isExecuting) {
			Iterator<PlayerChar> i = m_offers.keySet().iterator();
			while(i.hasNext()) {
				i.next().endTrading();
			}
			m_offers.clear();
			m_offers = null;
			return true;
		}
		return false;
	}
}
