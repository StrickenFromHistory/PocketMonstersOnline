package org.pokenet.server.backend.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.pokenet.server.GameServer;
import org.pokenet.server.backend.entity.TradeOffer.TradeType;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.network.MySqlManager;

/**
 * A trade between two players
 * @author shadowkanji
 *
 */
public class Trade implements Runnable{
	/* Stores the offers */
	private HashMap<Tradeable, TradeOffer[]> m_offers;
	public boolean m_isExecuting = false;
	private ArrayList<String> m_queries = new ArrayList<String>();
	
	/**
	 * Constructor
	 * @param player1
	 * @param player2
	 */
	public Trade(Tradeable player1, Tradeable player2) {
		m_offers = new HashMap<Tradeable, TradeOffer[]>();
		m_offers.put(player1, null);
		m_offers.put(player2, null);
		/* Block players of same IP address from trading */
//		if(player1.getIpAddress().equalsIgnoreCase(player2.getIpAddress())) {
//			if(player1 instanceof PlayerChar) {
//				PlayerChar p = (PlayerChar) player1;
//				p.getTcpSession().write("!Trading cannot be done with that player");
//			}
//			endTrade();
//			return;
//		}
		if(player1 instanceof PlayerChar) {
			/* Tell the client to open the trade window */
			PlayerChar p = (PlayerChar) player1;
			Char c = (Char) player2;
			p.getTcpSession().write("Ts" + c.getName());
			/*
			 * Send the pokemon data of player 2 to player 1
			 */
			for(int i = 0; i < player2.getParty().length; i++) {
				if(p.getParty()[i] != null) {
					p.getTcpSession().write("Ti" + i + PokemonSpecies.getDefaultData().getPokemonByName(player2.getParty()[i].getSpeciesName()).getSpeciesNumber() + "," +
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
							(player2.getParty()[i].getMoves()[0] != null ? player1.getParty()[i].getMoves()[0].getName() : "") + "," +
							(player2.getParty()[i].getMoves()[1] != null ? player1.getParty()[i].getMoves()[1].getName() : "") + "," +
							(player2.getParty()[i].getMoves()[2] != null ? player1.getParty()[i].getMoves()[2].getName() : "") + "," +
							(player2.getParty()[i].getMoves()[3] != null ? player1.getParty()[i].getMoves()[3].getName() : ""));
				}
			}
		}
		if(player2 instanceof PlayerChar) {
			/* If player 2 is a PlayerChar, tell client to open trade window */
			PlayerChar p = (PlayerChar) player2;
			Char c = (Char) player1;
			p.getTcpSession().write("Ts" + c.getName());
			/*
			 * Send the Pokemon data of player 1 to player 2
			 */
			for(int i = 0; i < player1.getParty().length; i++) {
				if(player1.getParty()[i] != null) {
					p.getTcpSession().write("Ti" + i + PokemonSpecies.getDefaultData().getPokemonByName(player1.getParty()[i].getSpeciesName()).getSpeciesNumber() + "," +
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
		}
	}
	
	/**
	 * Sets the offer from a player
	 * @param p
	 * @param o
	 */
	public void setOffer(Tradeable t, int poke, int money) {
		if(t instanceof PlayerChar) {
			PlayerChar p = (PlayerChar) t;
			if(p.getMoney() < money)
				return;
		}
		TradeOffer [] o = new TradeOffer[2];
		o[0] = new TradeOffer();
		o[0].setId(poke);
		o[0].setType(TradeType.POKEMON);
		o[0].setInformation(t.getParty()[poke].getSpeciesName());
		if(poke > -1 && poke < 6) {
			if(!DataService.canTrade(t.getParty()[poke].getSpeciesName())) {
				endTrade();
				return;
			}
		}
		
		o[1] = new TradeOffer();
		o[1].setQuantity(money);
		o[1].setType(TradeType.MONEY);
		
		m_offers.put(t, o);
		/* Send the offer to the other player */
		sendOfferInformation(t, o);
	}
	
	/**
	 * Cancels an offer from a player
	 * @param p
	 */
	public void cancelOffer(Tradeable t) {
		Iterator<Tradeable> it = m_offers.keySet().iterator();
		Tradeable otherPlayer = null;
		/* Find the other player */
		while(it.hasNext()) {
			Tradeable temp = it.next();
			if(temp != t) {
				otherPlayer = temp;
			}
		}
		/* Check the other player hasn't accepted a previous offer */
		if(!otherPlayer.acceptedTradeOffer()) {
			m_offers.put(t, null);
			otherPlayer.receiveTradeOfferCancelation();
		}
	}
	
	/**
	 * Sends offer information from one player to another
	 * @param p
	 * @param poke
	 * @param money
	 */
	private void sendOfferInformation(Tradeable t, TradeOffer [] o) {
		Iterator<Tradeable> i = m_offers.keySet().iterator();
		while(i.hasNext()) {
			Tradeable temp = i.next();
			if(temp.getName().compareTo(t.getName()) != 0) {
				temp.receiveTradeOffer(o);
			}
		}
	}
	
	/**
	 * Checks if both player's agree to trade
	 */
	public void checkForExecution() {
		Iterator<Tradeable> i = m_offers.keySet().iterator();
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
			
			Iterator<Tradeable> it = m_offers.keySet().iterator();
			Tradeable player1 = it.next();
			Tradeable player2 = it.next();
			TradeOffer [] o1 = m_offers.get(player1);
			TradeOffer [] o2 = m_offers.get(player2);
			
			/* Ensure each player has made an offer */
			if(o1 == null || o2 == null)
				return;
			
			/* Keep checking no player has left the trade */
			if(player1 != null && player2 != null) {
				
				/* Store a timestamp of the transaction */
				Date date = new Date();
				String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
				
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
							if(player1 instanceof PlayerChar && player2 instanceof PlayerChar) {
								player1.getParty()[o1[j].getId()] = null;
								m_queries.add("INSERT into pn_history (member,actiontaken,withwho,timestamp,tradedarticle) VALUES ('"+((PlayerChar) player1).getId()+"','1','"+((PlayerChar)player2).getId()+"','"+timestamp+"','"+temp[0].getDatabaseID()+"')");
							}
							
						}
						break;
					case MONEY:
						/* Ensure there was money offered */
						if(o1[j].getQuantity() > 0) {
							player1.setMoney(player1.getMoney() - o1[j].getQuantity());
							player2.setMoney(player2.getMoney() + o1[j].getQuantity());
							if(player1 instanceof PlayerChar && player2 instanceof PlayerChar) {
								m_queries.add("INSERT into pn_history (member,actiontaken,withwho,timestamp,tradedarticle) VALUES ('"+((PlayerChar) player1).getId()+"','0','"+((PlayerChar)player2).getId()+"','"+timestamp+"','"+o1[j].getQuantity()+"')");
							}
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
							if(player1 instanceof PlayerChar && player2 instanceof PlayerChar) {
								player2.getParty()[o1[j].getId()] = null;
								m_queries.add("INSERT into pn_history (member,actiontaken,withwho,timestamp,tradedarticle) VALUES ('"+((PlayerChar) player2).getId()+"','1','"+((PlayerChar)player1).getId()+"','"+timestamp+"','"+temp[1].getDatabaseID()+"')");
							}
						}
						break;
					case MONEY:
						/* Ensure there was money offered */
						if(o2[j].getQuantity() > 0) {
							player2.setMoney(player2.getMoney() - o2[j].getQuantity());
							player1.setMoney(player1.getMoney() + o2[j].getQuantity());
							if(player1 instanceof PlayerChar && player2 instanceof PlayerChar) {
								m_queries.add("INSERT into pn_history (member,actiontaken,withwho,timestamp,tradedarticle) VALUES ('"+((PlayerChar) player2).getId()+"','0','"+((PlayerChar)player1).getId()+"','"+timestamp+"','"+o2[j].getQuantity()+"')");
							}
						}
						break;
					case ITEM:
						break;
					}
				}
				
				/* Execute the Pokemon swap */
				if(temp[1] != null) {
					if(player1 instanceof PlayerChar) {
						PlayerChar p = (PlayerChar) player1;
						p.addPokemon(temp[1]);
					}
				}
				if(temp[0] != null) {
					if(player2 instanceof PlayerChar) {
						PlayerChar p = (PlayerChar) player2;
						p.addPokemon(temp[0]);
					}
				}
				
				/* TODO: Evolution checks */
				
				/* Update the money */
				if(player1 instanceof PlayerChar) {
					PlayerChar p = (PlayerChar) player1;
					p.updateClientMoney();
				}
				if(player2 instanceof PlayerChar) {
					PlayerChar p = (PlayerChar) player2;
					p.updateClientMoney();
				}
				
				/* Store transactions on DB */
				new Thread(this).start();
				
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
			Iterator<Tradeable> i = m_offers.keySet().iterator();
			while(i.hasNext()) {
				Tradeable t = i.next();
				if(t != null)
					t.finishTrading();
			}
			m_offers.clear();
			m_offers = null;
			return true;
		}
		return false;
	}

	public void run() {
		/*Record Trade on History Table*/
		MySqlManager m_database = new MySqlManager();
		if(m_database.connect(GameServer.getDatabaseHost(), GameServer.getDatabaseUsername(), GameServer.getDatabasePassword())){
			m_database.selectDatabase(GameServer.getDatabaseName());
			while(!m_queries.isEmpty()){
				m_database.query(m_queries.get(0));
				m_queries.remove(0);
			}
		}
		m_database.close();
	}
}
