package org.pokenet.server.network;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.pokenet.server.GameServer;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.backend.entity.Positionable.Direction;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.impl.WildBattleField;
import org.pokenet.server.feature.TimeService.Weather;

/**
 * Handles packets received from the player
 * @author shadowkanji
 *
 */
public class ConnectionManager extends IoHandlerAdapter {
	private static HashMap<String, PlayerChar> m_players;
	private LoginManager m_loginManager;
	private LogoutManager m_logoutManager;
	private RegistrationManager m_regManager;
	
	/**
	 * Constructor
	 * @param login
	 * @param logout
	 */
	public ConnectionManager(LoginManager login, LogoutManager logout) {
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
			//TODO: If player is battling, end the battle with them losing 
			GameServer.getServiceManager().getNetworkService().getLogoutManager().queuePlayer(p);
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
		System.out.println(message);
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
			switch(message.charAt(0)) {
			case 'P':
				//Pokemon interaction
				int pokemonIndex = 0;
				switch(message.charAt(1)) {
				case 'm':
					//Player is allowing move to be learned
					pokemonIndex = Integer.parseInt(String.valueOf(message.charAt(2)));
					int moveIndex = Integer.parseInt(String.valueOf(message.charAt(3)));
					String move = message.substring(4);
					if(p.getParty()[pokemonIndex] != null) {
						if(p.getParty()[pokemonIndex].getMovesLearning().contains(move)) {
							p.getParty()[pokemonIndex].learnMove(moveIndex, move);
						}
					}
					break;
				case 'e':
					//Player is allowing evolution
					pokemonIndex = Integer.parseInt(String.valueOf(message.charAt(2)));
					if(p.getParty()[pokemonIndex] != null) {
						p.getParty()[pokemonIndex].evolutionResponse(message.charAt(3) == '1', p);
					}
					break;
				}
			case 's':
				//Party swapping
				p.swapPokemon(Integer.parseInt(message.substring(1, message.indexOf(','))), 
						Integer.parseInt(message.substring(message.indexOf(',') + 1)));
				break;
			case 'S':
				//Shop interaction
				if(p.isShopping()) {
					switch(message.charAt(1)) {
					case 'b':
						//Buy items. Sent as SbITEMID,QUANTITY
						int id = Integer.parseInt(message.substring(2, message.indexOf(',')));
						int q = Integer.parseInt(message.substring(message.indexOf(',') + 1));
						p.buyItem(id, q);
						break;
					case 'f':
						//Finished shopping
						p.setShopping(false);
						break;
					}
				}
				break;
			case 'r':
				//A request was sent
				switch(message.charAt(1)) {
				case 'b':
					//Battle Request rbUSERNAME
					break;
				case 't':
					//Battle Request rtUSERNAME
					break;
				case 'a':
					//Request accepted raUSERNAME
					break;
				case 'c':
					//Request canceled rcUSERNAME
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
						break;
					case 's':
						//Swap pokemon between box and party - sent as BsBOXNUM,BOXSLOT,PARTYSLOT, e.g.Bs0,1,0
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
				if(p.getAdminLevel() > 0) {
					try {
						switch(message.charAt(1)) {
						case 'm':
							//Mute player
							m_players.get(message.substring(2)).setMuted(true);
							break;
						case 'u':
							//Unmute player
							m_players.get(message.substring(2)).setMuted(false);
							break;
						case 'w':
							//Change weather on current map
							switch(message.charAt(2)) {
							case 'n':
								//Normal
								p.getMap().setWeather(Weather.NORMAL);
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
					case 'i':
						//Item
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
				switch(message.charAt(1)) {
				case 'a':
					//Add a friend
					p.addFriend(message.substring(2));
					break;
				case 'r':
					//Remove a friend
					p.removeFriend(message.substring(2));
					break;
				}
				break;
			case 'T':
				//Trade packets
				switch (message.charAt(1)){
				case 'o':
					//Make an offer ToPOKENUM,MONEYAMOUNT
					break;
				case 't':
					//Ready to perform the trade
					break;
				case 'c':
					//Cancel the offer
					break;
				case 'C':
					//Cancel the trade
					break;
				}
				break;
			case 'C':
				//Chat/Interact
				switch(message.charAt(1)) {
				case 'l':
					//Local chat
					String mes = message.substring(2);
					if(mes.equalsIgnoreCase("/playercount"))
						p.getSession().write("Cl" + m_players.size() + " players online");
					else
						GameServer.getServiceManager().getNetworkService().getChatManager().
								queueLocalChatMessage("<" + p.getName() + "> " + mes, p.getMapX(), p.getMapY());
					break;
				case 'p':
					//Private chat
					details = message.substring(2).split(",");
					GameServer.getServiceManager().getNetworkService().getChatManager().
						queuePrivateMessage(details[1], m_players.get(details[0]).getSession(), p.getName());
					break;
				case 't':
					//Start talking
					if(!p.isTalking())
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
			//TODO: If player is battling, end the battle with them losing 
			if(p != null) {
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
}
