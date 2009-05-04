package org.pokenet.client.network;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.OurPlayer;
import org.pokenet.client.backend.entity.Player;
import org.pokenet.client.backend.entity.Player.Direction;
import org.pokenet.client.backend.time.WeatherService.Weather;
import org.pokenet.client.ui.frames.ShopDialog;

/**
 * Handles packets received from the server
 * @author shadowkanji
 *
 */
public class ConnectionManager extends IoHandlerAdapter {
	private GameClient m_game;

	/**
	 * Default constructor
	 * @param gameClient
	 */
	public ConnectionManager(GameClient game) {
		m_game = game;
	}
	
	/**
	 * Called when we lose or close the connection
	 */
	public void sessionClosed(IoSession session) {
		m_game.reset();
		GameClient.messageDialog("You have been disconnected\n" +
				"from the game server.", GameClient.getInstance().getDisplay());
	}
	
	/**
	 * Called when connected is made to the server
	 */
	public void sessionOpened(IoSession session) {
		System.out.println("Connected to game server.");
	}
	
	/**
	 * Catches networking exceptions
	 * @param session
	 */
	public void exceptionCaught(IoSession session, Throwable cause) {}

	/**
	 * Once a message is received, this method is called
	 */
	public void messageReceived(IoSession session, Object m) {
		Player p;
		String message = (String) m;
		System.out.println("INFO: " + message);
		String [] details;
		switch(message.charAt(0)) {
		case '!':
			//Server notification
			break;
		case 'S':
			//Shop
			List<Integer> merch = new ArrayList<Integer>();
			String items = "-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1";
			String[] merchData = items.split(",");
			
			for (int i = 1; i < merchData.length; i++) {
				merch.add(Integer.parseInt(merchData[i]));
			}
			GameClient.getInstance().getDisplay().add(new ShopDialog(merch, GameClient.getInstance().getPacketGenerator()));

			break;
		case 'B':
			//Box access - receiving a string of pokedex numbers, e.g. B15,23,24,
			break;
		case 'b':
			//Battle information
			switch(message.charAt(1)) {
			case 'i':
				//Battle started -> biISWILD,POKEDEXNUMBER
				GameClient.getInstance().getUi().getBattleManager().startBattle();
				break;
			case 'p':
				//No PP left for move -> bpMOVENAME
				break;
			case '!':
				//Other battle message not specified by packets below
				GameClient.getInstance().getUi().getBattleManager().getTimeLine().showMessage(message.substring(2));
				break;
			case '@':
				//Victory condition
				switch(message.charAt(2)) {
				case 'w':
					//Our player won
					GameClient.getInstance().getUi().getBattleManager().getTimeLine().informVictory();
					break;
				case 'l':
					//Our player lost
					GameClient.getInstance().getUi().getBattleManager().getTimeLine().informLoss();
					break;
				}
				break;
			case 'F':
				//A pokemon fainted -> bFPOKEMON
				GameClient.getInstance().getUi().getBattleManager().getTimeLine().informFaintedPoke(message.substring(2));
				break;
			case 'M':
				//A move was used -> bMPOKEMON,MOVENAME
				GameClient.getInstance().getUi().getBattleManager().getTimeLine().informMoveUsed(message.substring(2).split(","));
				break;
			case 'm':
				//Move requested
				GameClient.getInstance().getUi().getBattleManager().getTimeLine().informMoveRequested();
				break;
			case '.':
				//Exp gain -> b.POKEMON,EXPAMOUNT
				GameClient.getInstance().getUi().getBattleManager().getTimeLine().informExperienceGained(message.substring(2).split(","));
				break;
			case 'e':
				//A Pokemon received a status effect -> bePOKEMON,EFFECT
				GameClient.getInstance().getUi().getBattleManager().getTimeLine().informStatusChanged(message.substring(2).split(","));
				break;
			case 'E':
				//A Pokemon had a status effect removed -> bEPOKEMON,EFFECT
				GameClient.getInstance().getUi().getBattleManager().getTimeLine().informStatusHealed(message.substring(2).split(",")[0]);
				break;
			case 's':
				//Switch in Pokemon requested
				GameClient.getInstance().getUi().getBattleManager().getBattleWindow().showPokePane(true);
				break;
			case 'S':
				//A switch occured -> bSTRAINERNAME,NEWPOKEMON
				GameClient.getInstance().getUi().getBattleManager().getTimeLine().informSwitch(message.substring(2).split(","));
				break;
			case 'h':
				/*
				 * Receiving health info (health value, not health lost)
				 * (don't ask why there's two methods for this, shoddy needs em for some reason)
				 * NOTE: 0 is always our player
				 */
				switch(message.charAt(2)) {
				case '0':
					//Our pokemon's health
					GameClient.getInstance().getUi().getBattleManager().getTimeLine().informHealthChanged(message.substring(2).split(","), 0);
					break;
				case '1':
					//Enemy pokemon's health
					GameClient.getInstance().getUi().getBattleManager().getTimeLine().informHealthChanged(message.substring(2).split(","), 1);
					break;
				}
				break;
			case 'r':
				//The player tried to run ->bHSUCCESS
				switch(message.charAt(2)) {
				case '1':
					//Successfully ran away
					GameClient.getInstance().getUi().getBattleManager().getTimeLine().informRun(true);
					break;
				case '2':
					//Failed to run away
					GameClient.getInstance().getUi().getBattleManager().getTimeLine().informRun(false);
					break;
				}
				break;
			}
			break;
		case 'P':
			//Pokemon information
			switch(message.charAt(1)) {
			case 'i':
				//Initialise a pokemon
				details = message.substring(3).split(",");
				m_game.getOurPlayer().setPokemon(Integer.parseInt(message.substring(2, 3)), details);
				break;
			}
			break;
		case 'C':
			//Chat packet
			m_game.getUi().messageReceived(message.substring(1));
			break;
		case 'c':
			//Something changed
			p = m_game.getMapMatrix().getPlayer(Integer.parseInt(message.substring(2)));
			if(p != null) {
				switch(message.charAt(1)) {
				//Directional changes
				case 'D':
					p.setDirection(Direction.Down);
					break;
				case 'L':
					p.setDirection(Direction.Left);
					break;
				case 'R':
					p.setDirection(Direction.Right);
					break;
				case 'U':
					p.setDirection(Direction.Up);
					break;
				case 'S':
					//Sprite change
					p.setSprite(Integer.parseInt(message.substring(2)));
					break;
				case 'H':
					//Player's pokemon were healed
					for (int i = 0; i < GameClient.getInstance().getOurPlayer().getPokemon().length; i++){
						GameClient.getInstance().getOurPlayer().getPokemon()[i].setCurHP(
								GameClient.getInstance().getOurPlayer().getPokemon()[i].getMaxHP());
					}
					break;
				default:
					break;
				}
				p.loadSpriteImage();
			}
			break;
		case 'U':
			//Player moving up
			p = m_game.getMapMatrix().getPlayer(Integer.parseInt(message.substring(1)));
			if(p != null)
				p.moveUp();
			break;
		case 'D':
			//Player moving down
			p = m_game.getMapMatrix().getPlayer(Integer.parseInt(message.substring(1)));
			if(p != null)
				p.moveDown();
			break;
		case 'L':
			//Player moving left
			p = m_game.getMapMatrix().getPlayer(Integer.parseInt(message.substring(1)));
			if(p != null)
				p.moveLeft();
			break;
		case 'R':
			//Player moving right
			p = m_game.getMapMatrix().getPlayer(Integer.parseInt(message.substring(1)));
			if(p != null)
				p.moveRight();
			break;
		case 'm':
			//Map Information
			switch(message.charAt(1)) {
			case 'i':
				//Initialise players
				m_game.getMapMatrix().getPlayers().clear();
				m_game.getMapMatrix().getPlayers().trimToSize();
				details = message.substring(2).split(",");
				/*
				 * Parse all the information. This packet contains details for all players on this map
				 */
				for(int i = 0; i < details.length - 1; i++) {
					p = new Player();
					p.setUsername(details[i]);
					i++;
					p.setId(Integer.parseInt(details[i]));
					i++;
					p.setSprite(Integer.parseInt(details[i]));
					i++;
					p.setX(Integer.parseInt(details[i]));
					p.setServerX(Integer.parseInt(details[i]));
					i++;
					p.setY(Integer.parseInt(details[i]));
					p.setServerY(Integer.parseInt(details[i]));
					i++;
					switch(details[i].charAt(0)) {
					case 'D':
						p.setDirection(Direction.Down);
						break;
					case 'L':
						p.setDirection(Direction.Left);
						break;
					case 'R':
						p.setDirection(Direction.Right);
						break;
					case 'U':
						p.setDirection(Direction.Up);
						break;
					default:
						p.setDirection(Direction.Down);
						break;
					}
					p.loadSpriteImage();
					if(p.getId() == m_game.getPlayerId()) {
						/*
						 * This dude is our player! Store this information
						 */
						p.setOurPlayer(true);
						OurPlayer pl;
						if(m_game.getOurPlayer() == null) {
							pl = new OurPlayer();
						} else {
							pl = new OurPlayer(m_game.getOurPlayer());
						}
						pl.set(p);
						m_game.setOurPlayer(pl);
						m_game.getMapMatrix().addPlayer(pl);
						m_game.getInstance().getOurPlayer().setAnimating(true);
					} else
						m_game.getMapMatrix().addPlayer(p);
				}
				break;
			case 'a':
				//Add player
				details = message.substring(2).split(",");
				p = new Player();
				p.setUsername(details[0]);
				p.setId(Integer.parseInt(details[1]));
				p.setSprite(Integer.parseInt(details[2]));
				p.setX(Integer.parseInt(details[3]));
				p.setY(Integer.parseInt(details[4]));
				p.setServerX(Integer.parseInt(details[3]));
				p.setServerY(Integer.parseInt(details[4]));
				switch(details[5].charAt(0)) {
				case 'D':
					p.setDirection(Direction.Down);
					break;
				case 'L':
					p.setDirection(Direction.Left);
					break;
				case 'R':
					p.setDirection(Direction.Right);
					break;
				case 'U':
					p.setDirection(Direction.Up);
					break;
				default:
					p.setDirection(Direction.Down);
					break;
				}
				m_game.getMapMatrix().addPlayer(p);
				break;
			case 'F':
				//Friends list
				switch(message.charAt(1)) {
				case 'i':
					//Initialise
					break;
				case 'a':
					//A friend was added
					break;
				case 'r':
					//A friend was removed
					break;
				}
				break;
			case 'r':
				//Remove player
				m_game.getMapMatrix().removePlayer(Integer.parseInt(message.substring(2)));
				break;
			case 's':
				//Set the map and weather
				details = message.substring(2).split(",");
				m_game.setMap(Integer.parseInt(details[0]), Integer.parseInt(details[1]));
				switch(Integer.parseInt(details[2])) {
				case 0:
					m_game.getWeatherService().setWeather(Weather.NORMAL);
					break;
				case 1:
					m_game.getWeatherService().setWeather(Weather.RAIN);
					break;
				case 2:
					m_game.getWeatherService().setWeather(Weather.HAIL);
					break;
				case 3:
					m_game.getWeatherService().setWeather(Weather.SANDSTORM);
					break;
				case 4:
					m_game.getWeatherService().setWeather(Weather.FOG);
					break;
				default:
					m_game.getWeatherService().setWeather(Weather.NORMAL);
					break;
				}
				break;
			}
			break;
		case 'l':
			//Login Information
			switch(message.charAt(1)) {
			case 's':
				//Sucessful login
				details = message.substring(2).split(",");
				m_game.getLoginScreen().setVisible(false);
				m_game.getLoadingScreen().setVisible(false);
				m_game.setPlayerId(Integer.parseInt(details[0]));
				m_game.getUi().setVisible(true);
				m_game.getUi().getLocalChat().setVisible(true);
				m_game.getTimeService().setTime(Integer.parseInt(details[1].substring(0, 2)), 
						Integer.parseInt(details[1].substring(2)));
				break;
			case 'e':
				//Error
				GameClient.messageDialog("User or password are incorrect" +
				"Verify you typed user and password correctly.", GameClient.getInstance().getDisplay());
				
				m_game.getLoadingScreen().setVisible(false);
				m_game.getLoginScreen().enableLogin();
				break;
			case '1':
				//Account server offline
				GameClient.messageDialog("The account server is currently offline.\n" +
						"Please try again later.", GameClient.getInstance().getDisplay());
				m_game.getLoadingScreen().setVisible(false);
				m_game.getLoginScreen().showLogin();
				break;
			}
		
			break;
		case 'r':
			switch(message.charAt(1)) {
			case 's':
				//Sucessful registration
				GameClient.messageDialog("Successful registration. You may now login on any server.", GameClient.getInstance().getDisplay());
				m_game.getLoadingScreen().setVisible(false);
				m_game.getLoginScreen().showLogin();
				break;
			case '1':
				//Account server offline
				GameClient.messageDialog("The account server is currently offline.\n" +
						"Please try again later.", GameClient.getInstance().getDisplay());
				m_game.getLoginScreen().getRegistration().enableRegistration();
				m_game.getLoadingScreen().setVisible(false);
				break;
			case '2':
				GameClient.messageDialog("Username already taken.", GameClient.getInstance().getDisplay());
				m_game.getLoginScreen().getRegistration().enableRegistration();
				m_game.getLoadingScreen().setVisible(false);
				break;
			case '3':
				GameClient.messageDialog("Unkown error occurred. Please try again later.", GameClient.getInstance().getDisplay());
				m_game.getLoginScreen().getRegistration().enableRegistration();
				m_game.getLoadingScreen().setVisible(false);
				break;
			case '4':
				GameClient.messageDialog("Invalid data.", GameClient.getInstance().getDisplay());
				m_game.getLoginScreen().getRegistration().enableRegistration();
				m_game.getLoadingScreen().setVisible(false);
				break;
			}
			break;
		}
	}
}
