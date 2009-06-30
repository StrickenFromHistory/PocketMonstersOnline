package org.pokenet.server.network;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.pokenet.server.GameServer;
import org.pokenet.server.backend.ItemProcessor;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.backend.entity.PlayerChar.RequestType;
import org.pokenet.server.backend.entity.Positionable.Direction;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.impl.PvPBattleField;
import org.pokenet.server.battle.impl.WildBattleField;
import org.pokenet.server.feature.TimeService.Weather;
import org.pokenet.server.network.message.ItemMessage;
import org.pokenet.server.network.message.PokenetMessage;
import org.pokenet.server.network.message.RequestMessage;

/**
 * Handles packets received from the player
 * @author shadowkanji
 *
 */
public class ProtocolHandler extends IoHandlerAdapter {
	private static HashMap<String, PlayerChar> m_players;
	private LoginManager m_loginManager;
	private LogoutManager m_logoutManager;
	private RegistrationManager m_regManager;
	
	/**
	 * Constructor
	 * @param login
	 * @param logout
	 */
	public ProtocolHandler(LoginManager login, LogoutManager logout) {
		m_loginManager = login;
		m_logoutManager = logout;
		m_regManager = new RegistrationManager();
		m_regManager.start();
	}
	
	static {
		m_players = new HashMap<String, PlayerChar>();
	}
	
	/**
	 * Handles any exceptions involving a player's session
	 */
	public void exceptionCaught(IoSession session, Throwable t)
	throws Exception {
		/*
		 * Attempt to disconnect and logout the player (save their data)
		 */
		try {
			PlayerChar p = (PlayerChar) session.getAttribute("player");
			if (p.isBattling())
				p.lostBattle();
			GameServer.getServiceManager().getNetworkService().getLogoutManager().queuePlayer(p);
			GameServer.getServiceManager().getMovementService().removePlayer(p.getName());
			m_players.remove(p);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		t.printStackTrace();
	}
	
	/**
	    * Once the server receives a packet from the client, this method is run.
	    * @param IoSession session - A client session
	    * @param Object msg - The packet received from the client
		*/
	public void messageReceived(IoSession session, Object msg) throws Exception {
		String message = (String) msg;
		String [] details;
		//System.out.println(message);
		if(session.getAttribute("player") == null) {
			/*
			 * The player hasn't been logged in, only allow login and registration packets
			 */
			switch(message.charAt(0)) {
			case 'l':
				//Login packet
				details = message.substring(1).split(",");
				m_loginManager.queuePlayer(session, details[0], details[1]);
				break;
			case 'r':
				//Registration packet
				m_regManager.queueRegistration(session, message.substring(1));
				break;
			}
		} else {
			/*
			 * Player is logged in, allow interaction with their player object
			 */
			PlayerChar p = (PlayerChar) session.getAttribute("player");
			p.lastPacket = System.currentTimeMillis();
			switch(message.charAt(0)) {
			case 'P':
				//Pokemon interaction
				int pokemonIndex = 0;
				String move;
				switch(message.charAt(1)) {
				case 'm':
					//Player is allowing move to be learned
					pokemonIndex = Integer.parseInt(String.valueOf(message.charAt(2)));
					int moveIndex = Integer.parseInt(String.valueOf(message.charAt(3)));
					move = message.substring(4);
					if(move != null && !move.equalsIgnoreCase("") &&
							p.getParty()[pokemonIndex] != null) {
						if(p.getParty()[pokemonIndex].getMovesLearning().contains(move)) {
							p.getParty()[pokemonIndex].learnMove(moveIndex, move);
							p.updateClientPP(pokemonIndex, moveIndex);
						}
					}
					break;
				case 'M':
					//Player is not allowing the move to be learned
					pokemonIndex = Integer.parseInt(String.valueOf(message.charAt(2)));
					move = message.substring(3);
					if(p.getParty()[pokemonIndex] != null) {
						if(p.getParty()[pokemonIndex].getMovesLearning().contains(move)) {
							p.getParty()[pokemonIndex].getMovesLearning().remove(move);
						}
					}
					break;
				case 'e':
					//Evolution response
					pokemonIndex = Integer.parseInt(String.valueOf(message.charAt(3)));
					if(p.getParty()[pokemonIndex] != null) {
						switch(message.charAt(2)) {
						case '0':
							//Cancel evolution
							p.getParty()[pokemonIndex].evolutionResponse(false, p);
							break;
						case '1':
							//Allow evolution
							p.getParty()[pokemonIndex].evolutionResponse(true, p);
							break;
						}
					}
					break;
				}
				break;
			case 's':
				//Party swapping
				p.swapPokemon(Integer.parseInt(message.substring(1, message.indexOf(','))), 
						Integer.parseInt(message.substring(message.indexOf(',') + 1)));
				break;
			case 'S':
				//Shop interaction
				if(p.isShopping()) {
					int item = -1;
					switch(message.charAt(1)) {
					case 'b':
						//Buy items. Sent as SbITEMID,QUANTITY
						item = Integer.parseInt(message.substring(2, message.indexOf(',')));
						//int q = Integer.parseInt(message.substring(message.indexOf(',') + 1));
						p.buyItem(item, 1);
						break;
					case 's':
						//Sell items. Sent as SsITEMID,QUANTITY
						item = Integer.parseInt(message.substring(2, message.indexOf(',')));
						//int q = Integer.parseInt(message.substring(message.indexOf(',') + 1));
						p.sellItem(item, 1);
						break;
					case 'f':
						//Finished shopping
						p.setShopping(false);
						break;
					}
				} else if(p.isSpriting()) {
					//Sprite changing
					int sprite = Integer.parseInt(message.substring(1));
					/* Ensure the user buys a visible sprite */
					if(sprite > 0 && !GameServer.getServiceManager().
							getSpriteList().getUnbuyableSprites().contains(sprite)) {
						if(p.getMoney() >= 500) {
							p.setMoney(p.getMoney() - 500);
							p.updateClientMoney();
							p.setSprite(sprite);
							p.setSpriting(false);
						}
					}
				}
				break;
			case 'r':
				String player = message.substring(2);
				//A request was sent
				switch(message.charAt(1)) {
				case 'b':
					//Battle Request rbUSERNAME
					if(m_players.containsKey(player)) {
						ProtocolHandler.writeMessage(m_players.get(player).getSession(), 
								new RequestMessage(RequestType.BATTLE, p.getName()));
						p.addRequest(player, RequestType.BATTLE);
					}
					break;
				case 't':
					//Trade Request rtUSERNAME
					if(m_players.containsKey(player)) {
						ProtocolHandler.writeMessage(m_players.get(player).getSession(), 
								new RequestMessage(RequestType.TRADE, p.getName()));
						p.addRequest(player, RequestType.TRADE);
					}
					break;
				case 'a':
					//Request accepted raUSERNAME
					if(m_players.containsKey(player)) {
						m_players.get(player).requestAccepted(p.getName());
					}
					break;
				case 'c':
					//Request declined rcUSERNAME
					if(m_players.containsKey(player)) {
						m_players.get(player).removeRequest(p.getName());
					}
					break;
				}
				break;
			case 'B':
				//Box interaction
				if(p.isBoxing()) {
					switch(message.charAt(1)) {
					case 'r':
						//Requesting info for box number - e.g. Br0
						int boxNum = Integer.parseInt("" + message.charAt(2));
						if(boxNum >= 0 && boxNum < 9)
							p.sendBoxInfo(boxNum);
						break;
					case 'R':
						//Releasing a pokemon from storage - sent as BRBOXNUM,BOXSLOT
						details = message.substring(2).split(",");
						p.releasePokemon(Integer.parseInt(details[0]), Integer.parseInt(details[1]));
						break;
					case 's':
						//Swap pokemon between box and party - sent as BsBOXNUM,BOXSLOT,PARTYSLOT, e.g.Bs0,1,0
						details = message.substring(2).split(",");
						p.swapFromBox(Integer.parseInt(details[0]), 
								Integer.parseInt(details[1]), Integer.parseInt(details[2]));
						break;
					case 'f':
						//Finished with box interfaction
						p.setBoxing(false);
						break;
					}
				}
				break;
			case 'M':
				//Moderation
				if(message.charAt(1) == 'c') {
					p.getSession().write("Cl" + m_players.size() + " players online");
				} else if(p.getAdminLevel() > 0) {
					PlayerChar o;
					try {
						switch(message.charAt(1)) {
						case 'b':
							//Ban player
							if(m_players.containsKey(message.substring(2))) {
								o = m_players.get(message.substring(2));
								MySqlManager m = new MySqlManager();
								if(m.connect(GameServer.getDatabaseHost(), 
										GameServer.getDatabaseUsername(), 
										GameServer.getDatabasePassword())) {
									m.selectDatabase(GameServer.getDatabaseName());
									m.query("INSERT INTO pn_bans (ip) VALUE ('" + 
											o.getIpAddress()
											+ "')");
									m.close();
								}
							}
							break;
						case 'B':
							//Unban ip
							MySqlManager m = new MySqlManager();
							if(m.connect(GameServer.getDatabaseHost(), 
									GameServer.getDatabaseUsername(), 
									GameServer.getDatabasePassword())) {
								m.selectDatabase(GameServer.getDatabaseName());
								m.query("DELETE FROM pn_bans WHERE ip='" + 
										message.substring(2)
										+ "'");
								m.close();
							}
							break;
						case 'W':
							//Warp to player
							if(m_players.containsKey(message.substring(2))) {
								o = m_players.get(message.substring(2));
								p.setX(o.getX());
								p.setY(o.getY());
								p.setMap(o.getMap());
							}
							break;
						case 'm':
							//Mute player
							if(m_players.containsKey(message.substring(2))) {
								o = m_players.get(message.substring(2));
								o.setMuted(true);
								o.getSession().write("!You have been muted.");
							}
							break;
						case 'u':
							//Unmute player
							if(m_players.containsKey(message.substring(2))) {
								o = m_players.get(message.substring(2));
								o.setMuted(false);
								o.getSession().write("!You have been unmuted.");
							}
							break;
						case 'k':
							if(m_players.containsKey(message.substring(2))) {
								o = m_players.get(message.substring(2));
								o.getSession().write("!You have been kicked from the server.");
								o.getSession().close();
							}
							break;
						case 'w':
							//Change weather on current map
							switch(message.charAt(2)) {
							case 'n':
								//Normal
								p.getMap().setWeather(Weather.NORMAL);
								break;
							case 's':
								//Snow/Hail
								p.getMap().setWeather(Weather.HAIL);
								break;
							case 'r':
								//Rain
								p.getMap().setWeather(Weather.RAIN);
								break;
							case 'f':
								//Fog
								p.getMap().setWeather(Weather.FOG);
								break;
							case 'S':
								//Fog
								p.getMap().setWeather(Weather.SANDSTORM);
								break;
							}
						case 's':
							if(p.getAdminLevel() == 2) {
								GameServer.getServiceManager().stop();
								return;
							}
							break;
						}
					} catch (Exception e) {}
				}
				break;
			case 'b':
				//Battle information
				if(p.isBattling()) {
					BattleTurn turn;
					switch(message.charAt(1)) {
					case 'm':
						//Move selected (bmINDEXOFMOVE)
						turn = BattleTurn.getMoveTurn(Integer.parseInt(message.substring(2)));
						p.getBattleField().queueMove(p.getBattleId(), turn);
						break;
					case 's':
						//Pokemon switch (bsPARTYINDEX)
						int pIndex = Integer.parseInt(message.substring(2));
						if(p.getParty()[pIndex] != null) {
							if(!p.getParty()[pIndex].isFainted()) {
								turn = BattleTurn.getSwitchTurn(pIndex);
								p.getBattleField().queueMove(p.getBattleId(), turn);
							}
						}
						break;
					case 'r':
						//Run
						if(p.getBattleField() instanceof WildBattleField) {
							((WildBattleField) p.getBattleField()).run();
						}
						break;
					}
				}
				break;
			case 'U':
				//Move up
				if(!p.isBattling() && !p.isShopping())
					p.setNextMovement(Direction.Up);
				break;
			case 'D':
				//Move down
				if(!p.isBattling() && !p.isShopping())
					p.setNextMovement(Direction.Down);
				break;
			case 'L':
				//Move left
				if(!p.isBattling() && !p.isShopping())
					p.setNextMovement(Direction.Left);
				break;
			case 'R':
				//Move right
				if(!p.isBattling() && !p.isShopping())
					p.setNextMovement(Direction.Right);
				break;
			case 'F':
				//Friend list
				String friend = message.substring(2);
				switch(message.charAt(1)) {
				case 'a':
					//Add a friend
					if(m_players.containsKey(friend));
						p.addFriend(message.substring(2));
					break;
				case 'r':
					//Remove a friend
					p.removeFriend(message.substring(2));
					break;
				}
				break;
			case 'I':
				//Use an item, applies inside and outside of battle
				details = message.substring(1).split(",");
				new Thread(new ItemProcessor(p, details)).start();
				break;
			case 'i':
				//Drop item
				if(p.getBag().removeItem(Integer.parseInt(message.substring(1)), 1)) {
					ProtocolHandler.writeMessage(p.getSession(), new ItemMessage(false, 
							Integer.parseInt(message.substring(1)), 1));
				}
				break;
			case 'T':
				//Trade packets
				if(p.isTrading()) {
					switch (message.charAt(1)){
					case 'o':
						//Make an offer ToPOKENUM,MONEYAMOUNT
						details = message.substring(2).split(",");
						p.getTrade().setOffer(p, Integer.parseInt(String.valueOf(details[0])) , 
								Integer.parseInt(String.valueOf(details[1])));
						break;
					case 't':
						//Ready to perform the trade
						p.setTradeOfferAccepted(true);
						break;
					case 'c':
						//Cancel the offer
						p.cancelTradeOffer();
						break;
					case 'C':
						//Cancel the trade
						p.getTrade().endTrade();
						break;
					}
				}
				break;
			case 'C':
				//Chat/Interact
				switch(message.charAt(1)) {
				case 'l':
					//Local chat
					String mes = message.substring(2);
					if(!p.isMuted())
						GameServer.getServiceManager().getNetworkService().getChatManager().
						queueLocalChatMessage("<" + p.getName() + "> " + mes, p.getMapX(), p.getMapY(), p.getLanguage());
					break;
				case 'p':
					//Private chat
					details = message.substring(2).split(",");
					if(m_players.containsKey(details[0])) {
						GameServer.getServiceManager().getNetworkService().getChatManager().
						queuePrivateMessage(details[1], m_players.get(details[0]).getSession(), p.getName());
					}
					break;
				case 't':
					//Start talking
					if(!p.isTalking() && !p.isBattling())
						p.talkToNpc();
					break;
				case 'f':
					//Finish talking
					if(p.isTalking())
						p.setTalking(false);
					break;
				}
				break;
			}
		}
	}
	
	/**
	 * When a user disconnects voluntarily, this method is called
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		/*
		 * Attempt to save the player's data
		 */
		try {
			PlayerChar p = (PlayerChar) session.getAttribute("player");
			if(p != null) {
				if(p.isBattling()) {
					/* If in PvP battle, the player loses */
					if(p.getBattleField() instanceof PvPBattleField) {
						((PvPBattleField) p.getBattleField()).disconnect(p.getBattleId());
					}
					p.setBattleField(null);
					p.lostBattle();
				}
				/* If trading, end the trade */
				if(p.isTrading()) {
					p.getTrade().endTrade();
				}
				GameServer.getServiceManager().getNetworkService().getLogoutManager().queuePlayer(p);
				GameServer.getServiceManager().getMovementService().removePlayer(p.getName());
				m_players.remove(p);
				session.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Logs out all players and stops login/logout/registration managers
	 */
	public void logoutAll() {
		m_regManager.stop();
		m_loginManager.stop();
		/*
		 * Queue all players to be saved
		 */
		Iterator<PlayerChar> it = m_players.values().iterator();
		PlayerChar p;
		while(it.hasNext()) {
			p = it.next();
			m_logoutManager.queuePlayer(p);
		}
		/*
		 * Since the method is called during a server shutdown, wait for all players to be logged out
		 */
		while(m_logoutManager.getPlayerAmount() > 0);
		m_logoutManager.stop();
	}
	
	/**
	 * Returns the list of players
	 * @return
	 */
	public static HashMap<String, PlayerChar> getPlayers() {
		return m_players;
	}
	
	/**
	 * Returns how many players are logged in
	 * @return
	 */
	public static int getPlayerCount() {
		return m_players.keySet().size();
	}
	
	/**
	 * Writes the message to the session
	 * @param session
	 * @param m
	 */
	public static void writeMessage(IoSession session, PokenetMessage m) {
		session.write(m.getMessage());
	}
}
