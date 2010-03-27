package org.pokenet.client.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.BattleManager;
import org.pokenet.client.backend.ItemDatabase;
import org.pokenet.client.backend.MoveLearningManager;
import org.pokenet.client.backend.Translator;
import org.pokenet.client.backend.entity.HMObject;
import org.pokenet.client.backend.entity.OurPlayer;
import org.pokenet.client.backend.entity.Player;
import org.pokenet.client.backend.entity.Player.Direction;
import org.pokenet.client.backend.time.WeatherService.Weather;
import org.pokenet.client.ui.frames.SpriteChooserDialog;

/**
 * Handles packets received from the server
 * @author shadowkanji
 * @author ZombieBear
 * @author Nushio
 *
 */
public class TcpProtocolHandler extends IoHandlerAdapter {
	private GameClient m_game;

	/**
	 * Default constructor
	 * @param gameClient
	 */
	public TcpProtocolHandler(GameClient game) {
		m_game = game;
	}

	/**
	 * Called when we lose or close the connection
	 */
	public void sessionClosed(IoSession session) {
		m_game.reset();
		List<String> translated = new ArrayList<String>();
		translated = Translator.translate("_LOGIN");
		GameClient.messageDialog(translated.get(40), GameClient.getInstance().getDisplay());
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
	public void exceptionCaught(IoSession session, Throwable cause) {
		cause.printStackTrace();
	}
	
	
	/**
	 * Processes movement changes
	 * @param player
	 * @param direction
	 */
	private void processMovement(int player, char direction) {
		Player p = m_game.getMapMatrix().getPlayer(player);
		if(p == null)
			return;
		switch(direction) {
		case 'D':
			p.queueMovement(Direction.Down);
			break;
		case 'U':
			p.queueMovement(Direction.Up);
			break;
		case 'L':
			p.queueMovement(Direction.Left);
			break;
		case 'R':
			p.queueMovement(Direction.Right);
			break;
		}
	}

	/**
	 * Once a message is received, this method is called
	 */
	public void messageReceived(IoSession session, Object m) {
		List<String> translated = new ArrayList<String>();
		translated = Translator.translate("_LOGIN");
		Player p;
		String message = (String) m;
		System.out.println("INFO: " + message);
		String [] details;
		switch(message.charAt(0)) {
		case 'R':
			//Server Revision Info
			GameClient.getInstance().getLoginScreen().setServerRevision(message.substring(1));
			break;
		case '!':
			//Server notification
			GameClient.messageDialog(message.substring(1), GameClient.getInstance().getDisplay());
			break;
		case 'q':
			//Server announcement
			GameClient.getInstance().getUi().getChat().addAnnouncement(message.substring(1));
			break;			
		case 'T':
			//Trade
			switch(message.charAt(1)) {
			case 's':
				//Start trade TsTRAINERNAME
				GameClient.getInstance().getUi().startTrade(message.substring(2));
				break;
			case 'o':
				//An offer was made ToPOKEINDEX,MONEYNUM
				String[] offerData = message.substring(2).split(",");
				GameClient.getInstance().getUi().getTrade().getOffer(Integer.parseInt(offerData[0]), 
						Integer.parseInt(offerData[1]));
				break;
			case 'c':
				//The offer was cancelled
				GameClient.getInstance().getUi().getTrade().cancelTheirOffer();
				break;
			case 'i':
				//A pokemon is being added to the trade dialog TiPOKEINDEX,POKEDATA
				String[] data = message.substring(3).split(",");
				GameClient.getInstance().getUi().getTrade().addPoke(Integer.parseInt(String.valueOf(
						message.charAt(2))), data);
				break;
			case 'f':
				//Trading is done
				GameClient.getInstance().getUi().stopTrade();
				break;
			}
			break;
		case 's':
			//Party swapping. Received as s0,5. Pokemons in party at 0 and 5 were swapped around
			m_game.getOurPlayer().swapPokemon(Integer.parseInt(message.substring(1, message.indexOf(','))),
					Integer.parseInt(message.substring(message.indexOf(',') + 1)) );
			break;
		case 'S':
			//Shop
			switch(message.charAt(1)) {
			case 'l': //Shop List
				HashMap<Integer, Integer> stock = new HashMap<Integer, Integer>();
				String[] merchData = message.substring(2).split(",");
				for (int i = 0; i < merchData.length; i++) {
					String[] tempStockData = merchData[i].split(":");
					stock.put(Integer.parseInt(tempStockData[0]), Integer.parseInt(tempStockData[1]));
				}
				GameClient.getInstance().getUi().startShop(stock);
				break;
			case 'n': //N is for No Money
				GameClient.messageDialog("You can't afford this item", GameClient.getInstance().getDisplay());
				break;
			case 'f': //F is for Full Pockets. Can't carry any more
				GameClient.messageDialog("You can't carry any new items", GameClient.getInstance().getDisplay());
				break;
			case 'c': //Cant Carry more of that Type
				GameClient.messageDialog("You can't carry any more "+message.substring(2), 
						GameClient.getInstance().getDisplay());
				break;
			case 'd': //You don't have the item you're trying to sell!
				GameClient.messageDialog("You don't have a "+message.substring(2), 
						GameClient.getInstance().getDisplay());
				break;
			case 'b': //Bought Item
				try {
					GameClient.getInstance().getUi().getNPCSpeech().advance();
					GameClient.getInstance().getUi().getNPCSpeech().advance();
				} catch (Exception e) {}
				GameClient.getInstance().getUi().talkToNPC(
						"You bought a " + ItemDatabase.getInstance().
						getItem(Integer.parseInt(message.substring(2))).getName());
				GameClient.getInstance().getUi().getShop().m_timer.reset();
				GameClient.getInstance().getUi().getShop().m_timer.resume();
				break;
			case 's': //Sold Item
				try {
					GameClient.getInstance().getUi().getNPCSpeech().advance();
					GameClient.getInstance().getUi().getNPCSpeech().advance();
				} catch (Exception e) {}
				GameClient.getInstance().getUi().talkToNPC(
						"You sold a " + ItemDatabase.getInstance().
						getItem(Integer.parseInt(message.substring(2))).getName());
				GameClient.getInstance().getUi().getShop().m_timer.reset();
				GameClient.getInstance().getUi().getShop().m_timer.resume();
				break;	
			case 'S': //Sprite selection
				GameClient.getInstance().getDisplay().add(new SpriteChooserDialog());
				break;
			}

			break;
		case 'B':
			//Box access - receiving a string of pokedex numbers, e.g. B15,23,24,
			int[] pokes = new int[30];
			/*
			 * NOTE: -1 identifies that no pokemon is in a slot
			 */
			if(message.length() > 1) {
				String[] indexes = message.substring(1).split(",");
				for (int i = 0; i < 30; i++){
					if(indexes.length > i)
						if(indexes[i] == null || indexes[i].compareTo("") == 0)
							pokes[i] = -1;
						else
							pokes[i] = (Integer.parseInt(indexes[i]));
					else
						pokes[i] = -1;
				}
			} else {
				for (int i = 0; i < pokes.length; i++){
					pokes[i] = -1;
				}
			}
			if (GameClient.getInstance().getUi().getStorageBox() == null){
				GameClient.getInstance().getUi().useStorageBox(pokes);
			} else {
				GameClient.getInstance().getUi().getStorageBox().changeBox(pokes);
			}
			break;
		case 'b':
			//Battle information
			switch(message.charAt(1)) {
			case 'i':
				//Battle started -> biISWILD,POKEAMOUNT
				BattleManager.getInstance().startBattle(message.charAt(2), 
						Integer.parseInt(message.substring(3)));
				break;
			case 'I':
				//Won an item in battle
				String item = ItemDatabase.getInstance().getItem(Integer.parseInt(message.substring(2))).getName();
				GameClient.getInstance().getOurPlayer().addItem(Integer.parseInt(message.substring(2)), 1);
				BattleManager.getInstance().getTimeLine().informItemDropped(item);
				break;
			case 'p':
				//No PP left for move -> bpMOVENAME
				BattleManager.getInstance().getTimeLine().informNoPP(message.substring(2));
				break;
			case 'P':
				//Receive enemy poke data -> bPINDEX,NAME,LEVEL,GENDER,MAXHP,CURHP,SPRITENUM,ISSHINY
				String[] data = message.substring(2).split(",");
				BattleManager.getInstance().setEnemyPoke(Integer.parseInt(data[0]),
						data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(
								data[4]), Integer.parseInt(data[5]), Integer.parseInt(data[6]), 
								Boolean.valueOf(data[7]));
				break;
			case 'n':
				//Receive the enemy trainer's name
				BattleManager.getInstance().setEnemyName(message.substring(2));
				break;
			case '!':
				//Other battle message not specified by packets below
				BattleManager.getInstance().getTimeLine().showMessage(message.substring(2));
				break;
			case '@':
				//Victory condition
				switch(message.charAt(2)) {
				case 'w':
					//Our player won
					BattleManager.getInstance().getTimeLine().informVictory();
					break;
				case 'l':
					//Our player lost
					BattleManager.getInstance().getTimeLine().informLoss();
					break;
				case 'p':
					//Our player caught the Pokemon
					BattleManager.getInstance().endBattle();
					break;
				}
				break;
			case 'F':
				//A pokemon fainted -> bFPOKEMON
				BattleManager.getInstance().getTimeLine().informFaintedPoke(
						message.substring(2));
				break;
			case 'M':
				//A move was used -> bMPOKEMON,MOVENAME
				BattleManager.getInstance().getTimeLine().informMoveUsed(
						message.substring(2).split(","));
				break;
			case 'm':
				//Move requested
				BattleManager.getInstance().getTimeLine().informMoveRequested();
				break;
			case '.':
				//Exp gain -> b.POKEMON,EXPAMOUNT
				BattleManager.getInstance().getTimeLine().informExperienceGained(
						message.substring(2).split(","));
				break;
			case 'e':
				//A Pokemon received a status effect -> beTRAINER.POKEMON,EFFECT
				BattleManager.getInstance().getTimeLine().informStatusChanged(
						Integer.parseInt(String.valueOf(message.charAt(2))), message.substring(3).split(","));
				break;
			case 'E':
				//A Pokemon had a status effect removed -> bEPOKEMON,EFFECT
				BattleManager.getInstance().getTimeLine().informStatusHealed(
						Integer.parseInt(String.valueOf(message.charAt(2))), message.substring(3).split(","));
				break;
			case 's':
				//Switch in Pokemon requested
				BattleManager.getInstance().getTimeLine().informSwitchRequested();
				break;
			case 'S':
				//A switch occured -> bSTRAINERNAME,NEWPOKEMON
				BattleManager.getInstance().getTimeLine().informSwitch(
						message.substring(2).split(","));
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
					BattleManager.getInstance().getTimeLine().informHealthChanged(
							message.substring(2).split(","), 0);
					break;
				case '1':
					//Enemy pokemon's health
					BattleManager.getInstance().getTimeLine().informHealthChanged(
							message.substring(2).split(","), 1);
					break;
				}
				break;
			case 'r':
				//The player tried to run ->bHSUCCESS
				switch(message.charAt(2)) {
				case '1':
					//Successfully ran away
					BattleManager.getInstance().getTimeLine().informRun(true);
					break;
				case '2':
					//Failed to run away
					BattleManager.getInstance().getTimeLine().informRun(false);
					break;
				}
				break;
			case '$':
				//Receiving earnings
				BattleManager.getInstance().getTimeLine().informMoneyGain(Integer.parseInt(
						message.substring(2)));
				break;
			case 'l':
				//Inform pokemon level up
				final String[] levelData = message.substring(2).split(",");
				BattleManager.getInstance().getTimeLine().informLevelUp(levelData[0],
						Integer.parseInt(levelData[1]));
				break;
			}
			break;
		case 'P':
			//Pokemon information
			switch(message.charAt(1)) {
			case 'S':
				//Stats update
				details = message.substring(3).split(",");
				m_game.getOurPlayer().updatePokemon(Integer.parseInt(String.valueOf(message.charAt(2))), details);
				m_game.getUi().refreshParty();
				break;
			case 'N':
				//A pokemon left the party
				m_game.getOurPlayer().setPokemon(Integer.parseInt(String.valueOf(message.charAt(2))), null);
				m_game.getUi().refreshParty();
				break;
			case 'i':
				//Initialise a pokemon
				details = message.substring(3).split(",");
				m_game.getOurPlayer().setPokemon(Integer.parseInt(message.substring(2, 3)), details);
				break;
			case 'm':
				/*
				 * Move learning by levelling up, received as (for example) Pm0EMBER
				 * The 3rd character is the index of the pokemon in
				 * the player's party and the rest is the move name
				 * NOTE: This packet is for informing the client a move wants to be learned
				 * if the client allows the move learning, the server does not send a reply
				 * to confirm it was learned, as it is ensured it is learned
				 */
				MoveLearningManager.getInstance().queueMoveLearning(Integer.parseInt(String.valueOf(
						message.charAt(2))), message.substring(3));
				break;
			case 'M':
				/* Move learning by TM, this packet is confirmation that the moved was learned */
				m_game.getOurPlayer().getPokemon()[Integer.parseInt(String.valueOf(message.charAt(2)))]
				                                   .setMoves(Integer.parseInt(String.valueOf(message.charAt(3)))
				                                		   , message.substring(4));
				m_game.getUi().update(false);
				break;
			case 'e':
				//EXP gain
				int p1 = Integer.parseInt(String.valueOf(message.charAt(2)));
				int exp = m_game.getOurPlayer().getPokemon()[p1].getExp() + Integer.parseInt(message.substring(3));
				m_game.getOurPlayer().getPokemon()[p1].setExp(exp);
				m_game.getUi().update(false);
				break;
			case 'E':
				/*
				 * A pokemon would like to evolve, received as PE1
				 * 1 is the index of the pokemon in the party
				 * Reply to server with the same packet except
				 * with a 0 at the end if the evolution was cancelled
				 * or a 1 if it was allowed, e.g. PE10
				 */
				GameClient.getInstance().getUi().tryEvolve(Integer.parseInt(String.valueOf(message.charAt(2))));
				break;
			case 'l':
				//Level change
				String[] levelData = message.substring(2).split(",");
				m_game.getOurPlayer().getPokemon()[Integer.parseInt(levelData[0])].setLevel(
						Integer.parseInt(levelData[1]));
				m_game.getUi().update(false);
				break;
			case 'h':
				//HP Change - through item usage
				m_game.getOurPlayer().getPokemon()[Integer.parseInt(String.valueOf(message.charAt(2)))]
				                                   .setCurHP(Integer.parseInt(message.substring(3)));
				m_game.getUi().update(false);
				break;
			case 'p':
				//PP data - Pp POKEINDEX MOVEINDEX CURRENTPP , MAXPP
				details = message.substring(4).split(",");
				int poke = Integer.parseInt(String.valueOf(message.charAt(2)));
				int move = Integer.parseInt(String.valueOf(message.charAt(3)));
				m_game.getOurPlayer().getPokemon()[poke].setMoveCurPP(move, Integer.parseInt(String.valueOf(details[0])));
				m_game.getOurPlayer().getPokemon()[poke].setMoveMaxPP(move, Integer.parseInt(String.valueOf(details[1])));
				break;
			}
			break;
		case 'F':
			//Fishing packets
			switch(message.charAt(1)) {
			case 'F': //You can't use that rod at your current level
				GameClient.messageDialog("You need to have a fishing level of " + 
						message.substring(2) + " to use that rod!", GameClient.getInstance().getDisplay());
				break;
			case 'f': //You can't fish on land!
				GameClient.messageDialog("You can't fish on land!", GameClient.getInstance().getDisplay());
				break;
			case 'U': //It got away! (Too strong for you)
				GameClient.messageDialog("The fish was too strong for you! \n" +
						"It got away!", GameClient.getInstance().getDisplay());
				break;
			case 'u': //Not even a nibble!
				GameClient.messageDialog("Not even a nibble!", GameClient.getInstance().getDisplay());
				break;
			}
			break;
		case 'C':
			//Chat packet
			m_game.getUi().messageReceived(message.substring(1));
			break;
		case 'c':
			//Something changed
			switch (message.charAt(1)){
			case 'h':
				// You need a higher trainer level to use an HM Object
				GameClient.messageDialog("You are not strong enough to do this.\n" +
						"Your trainer level must be " + message.substring(2) + " to do this.",
						GameClient.getInstance().getDisplay());
				break;
			case 'B':
				//Badge change
				switch(message.charAt(2)) {
				case 'i':
					/*
					 * All of the player's badges. Received as 0000001100001110000
					 * 0 = no badge
					 * 1 = badge earned
					 * The string will be 41 chars long
					 * 0 - 7   Kanto Badges
					 * 8 - 15  Johto Badges
					 * 16 - 23 Hoenn Badges
					 * 24 - 31 Sinnoh Badges
					 * 32 - 35 Orange Islands Badges
					 * 36 - 41 Undefined
					 */
					m_game.getOurPlayer().initBadges(message.substring(3));
					GameClient.getInstance().setOurPlayer(m_game.getOurPlayer());
					break;
				case 'a':
					//Add a badge
					m_game.getOurPlayer().addBadge(Integer.valueOf(message.substring(3)));
					break;
				}
				break;

			case 's':
				//You gained a skill level or your skills are being initialized
				switch (message.charAt(2)){
				case 'b':
					//Breeding
					if(m_game.getOurPlayer().getBreedingLevel() != -1 && m_game.getOurPlayer().getBreedingLevel() != Integer.parseInt(message.substring(3)))
						GameClient.getInstance().getUi().getChat().addSystemMessage("*" + "Congratulations! Your breeding level is now " + message.substring(3) + ".");
					m_game.getOurPlayer().setBreedingLevel(Integer.parseInt(message.substring(3)));
					break;
				case 'f':
					//Fishing
					if(m_game.getOurPlayer().getFishingLevel() != -1 && m_game.getOurPlayer().getFishingLevel() != Integer.parseInt(message.substring(3)))
						GameClient.getInstance().getUi().getChat().addSystemMessage("*" + "Congratulations! Your fishing level is now " + message.substring(3) + ".");
					m_game.getOurPlayer().setFishingLevel(Integer.parseInt(message.substring(3)));
					break;
				case 't':
					//Trainer
					if(m_game.getOurPlayer().getTrainerLevel() != -1 && m_game.getOurPlayer().getTrainerLevel() != Integer.parseInt(message.substring(3)))
						GameClient.getInstance().getUi().getChat().addSystemMessage("*" + "Congratulations! Your trainer level is now " + message.substring(3) + ".");
					m_game.getOurPlayer().setTrainerLevel(Integer.parseInt(message.substring(3)));
					break;
				case 'c':
					//Coordinating
					if(m_game.getOurPlayer().getCoordinatingLevel() != -1 && m_game.getOurPlayer().getCoordinatingLevel() != Integer.parseInt(message.substring(3)))
						GameClient.getInstance().getUi().getChat().addSystemMessage("*" + "Congratulations! Your coordinating level is now " + message.substring(3) + ".");
					m_game.getOurPlayer().setCoordinatingLevel(Integer.parseInt(message.substring(3)));
					break;
				}
				break;

			case 'W':
				//Weather Change
				switch(message.charAt(2)) {
				case 'n':
					//Normal
					m_game.getWeatherService().setWeather(Weather.NORMAL);
					break;
				case 'r':
					//Rain
					m_game.getWeatherService().setWeather(Weather.RAIN);
					break;
				case 'h':
					//Hail
					m_game.getWeatherService().setWeather(Weather.HAIL);
					break;
				case 'f':
					//Fog
					m_game.getWeatherService().setWeather(Weather.FOG);
					break;
				case 'S':
					//Sandstorm
					m_game.getWeatherService().setWeather(Weather.SANDSTORM);
					break;
				}
				break;
			case 'M':
				//Money change
				m_game.getOurPlayer().setMoney(Integer.parseInt(message.substring(2)));
				GameClient.getInstance().getUi().update(true);
				break;
			case 'H':
				//Pokes were healed
				for (int i = 0; i < GameClient.getInstance().getOurPlayer().getPokemon().length; i++){
					if (GameClient.getInstance().getOurPlayer().getPokemon()[i] != null){
						GameClient.getInstance().getOurPlayer().getPokemon()[i].setCurHP(
								GameClient.getInstance().getOurPlayer().getPokemon()[i].getMaxHP());
						for (int x = 0; x < 4; x++){
							GameClient.getInstance().getOurPlayer().getPokemon()[i].setMoveCurPP(x, 
									GameClient.getInstance().getOurPlayer().getPokemon()[i].
									getMoveMaxPP()[x]);
						}
					}
				}
				m_game.getUi().update(false);
				break;
			case 'D':
				//Facing down
				p = m_game.getMapMatrix().getPlayer(Integer.parseInt(message.substring(2)));
				if(p != null) {
					p.setDirection(Direction.Down);
					p.loadSpriteImage();
				}
				break;
			case 'L':
				//Facing Left
				p = m_game.getMapMatrix().getPlayer(Integer.parseInt(message.substring(2)));
				if(p != null) {
					p.setDirection(Direction.Left);
					p.loadSpriteImage();
				}
				break;
			case 'R':
				//Facing Right
				p = m_game.getMapMatrix().getPlayer(Integer.parseInt(message.substring(2)));
				if(p != null) {
					p.setDirection(Direction.Right);
					p.loadSpriteImage();
				}
				break;
			case 'U':
				//Facing Up
				p = m_game.getMapMatrix().getPlayer(Integer.parseInt(message.substring(2)));
				if(p != null) {
					p.setDirection(Direction.Up);
					p.loadSpriteImage();
				}
				break;
			case 'S':
				//Sprite change
				details = message.substring(2).split(",");
				p = m_game.getMapMatrix().getPlayer(Integer.parseInt(details[0]));
				if(p != null) {
					p.setSprite(Integer.parseInt(details[1]));
					p.loadSpriteImage();
				}
				break;
			}
			break;
		case 'U':
			//Updating our player co-ordinates (probably went out of sync)
			//Ux,y
			p = m_game.getOurPlayer();
			details = message.substring(1).split(",");
			p.setX(Integer.parseInt(details[0]));
			p.setY(Integer.parseInt(details[1]));
			p.setServerX(p.getX());
			p.setServerY(p.getY());
			/* Reposition screen above player */
			m_game.getMapMatrix().getCurrentMap().setXOffset(400 - p.getX(), false);
			m_game.getMapMatrix().getCurrentMap().setYOffset(300 - p.getY(), false);
			m_game.getMapMatrix().recalibrate();
			break;
		case 'M':
			//Player movements
			//Mdirpid,dirpid
			details = message.substring(1).split(",");
			for(int i = 0; i < details.length; i++) {
				processMovement(Integer.parseInt(details[i].substring(1)), 
						details[i].charAt(0));
			}
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
					try {
						HMObject hm = new HMObject(HMObject.parseHMObject(details[i]));
						i++;
						hm.setId(Integer.parseInt(details[i]));
						i ++;
						hm.setSprite(Integer.parseInt(details[i]));
						i ++;
						hm.setX(Integer.parseInt(details[i]));
						hm.setServerX(Integer.parseInt(details[i]));
						i++;
						hm.setY(Integer.parseInt(details[i]));
						hm.setServerY(Integer.parseInt(details[i]));
						i++;
						hm.setDirection(Direction.Down);
						hm.loadSpriteImage();
						p = hm;
					} catch (Exception e) {
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
					}
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
						GameClient.getInstance().setOurPlayer(pl);
						GameClient.getInstance().getOurPlayer().setAnimating(true);
					} else{
						m_game.getMapMatrix().addPlayer(p);
					}
				}
				break;
			case 'a':
				//Add player
				details = message.substring(2).split(",");
				p = new Player();
				try {
					HMObject hm = new HMObject(HMObject.parseHMObject(details[0]));
					hm.setId(Integer.parseInt(details[1]));
					hm.setSprite(Integer.parseInt(details[2]));
					hm.setX(Integer.parseInt(details[3]));
					hm.setServerX(Integer.parseInt(details[3]));
					hm.setY(Integer.parseInt(details[4]));
					hm.setServerY(Integer.parseInt(details[4]));
					hm.setDirection(Direction.Down);
					hm.loadSpriteImage();
					p = hm;
					p.setId(hm.getId());
				} catch (Exception e) {
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
				}
				m_game.getMapMatrix().addPlayer(p);
				break;
			case 'F':
				//Friends list
				switch(message.charAt(1)) {
				case 'i':
					//Initialise
					GameClient.getInstance().getUi().initFriendsList(message.substring(2).split(","));
					break;
				case 'a':
					//A friend was added
					GameClient.getInstance().getUi().getFriendsList().addFriend(message.substring(2));
					break;
				case 'r':
					//A friend was removed
					GameClient.getInstance().getUi().getFriendsList().removeFriend(message.substring(2));
					break;
				}
				break;
			case 'r':
				//Remove player
				m_game.getMapMatrix().removePlayer(Integer.parseInt(message.substring(2)));
				break;
			case 's':
				//Set the map and weather
				details = message.substring(3).split(",");
				m_game.getMapMatrix().setNewMapPos(message.charAt(2));
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
		case 'p':
			//Change password packets
			switch(message.charAt(1)) {
			case 's':
				//Successful password change
				if(m_game.getPacketGenerator().isUpdatingHashMethod()) {
					m_game.getPacketGenerator().login(m_game.getPacketGenerator().getLastUsername(), m_game.getPacketGenerator().getLastPassword());
				}
				break;
			case 'e':
				//Password change failed
				if(m_game.getPacketGenerator().isUpdatingHashMethod()) {
					// could just copy and paste code from login packet section
					// but then that part of the code is useless
					// opting in favor of the "stupid" way so that login stuff stays in one place
					m_game.getPacketGenerator().login(m_game.getPacketGenerator().getLastUsername(), m_game.getPacketGenerator().getLastPassword());
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
				m_game.getUi().getChat().setVisible(true);
				m_game.getTimeService().setTime(Integer.parseInt(details[1].substring(0, 2)), 
						Integer.parseInt(details[1].substring(2)));
				break;
			case 'u':
				//Unknown problem occurred
				GameClient.messageDialog("An unknown problem occurred!", GameClient.getInstance().getDisplay());
				m_game.getLoadingScreen().setVisible(false);
				m_game.getLoginScreen().enableLogin();
				break;
			case 'e':
				//Error
				// if this is in response to the update hash attempt, then display the error message
				if(m_game.getPacketGenerator().isUpdatingHashMethod()) {
					// end attempt to update their password hash
					m_game.getPacketGenerator().endUpdateHashMethod();
					GameClient.messageDialog(translated.get(21), GameClient.getInstance().getDisplay());
					m_game.getLoadingScreen().setVisible(false);
					m_game.getLoginScreen().enableLogin();
				} else {
					// begin attempt to update password hash
					m_game.getPacketGenerator().updatePasswordHashMethod();
				}
				break;
			case '1':
				//Account server offline
				GameClient.messageDialog(translated.get(22), GameClient.getInstance().getDisplay());
				m_game.getLoadingScreen().setVisible(false);
				m_game.getLoginScreen().showLogin();
				break;
			case '2':
				//Server full
				GameClient.messageDialog("This server is full, please try another", 
						GameClient.getInstance().getDisplay());
				m_game.getLoadingScreen().setVisible(false);
				m_game.getLoginScreen().showLogin();
				break;
			case '3':
				//You are logged in elsewhere
				GameClient.messageDialog("You are still being logged out of another server,\n" +
						"please try again later.", 
						GameClient.getInstance().getDisplay());
				m_game.getLoadingScreen().setVisible(false);
				m_game.getLoginScreen().showLogin();
				break;
			case '4':
				//Banned
				GameClient.messageDialog("You've been banned.", 
						GameClient.getInstance().getDisplay());
				m_game.getLoadingScreen().setVisible(false);
				m_game.getLoginScreen().showLogin();
				break;
			}

			break;
		case 'I': //I is for Items
			switch(message.charAt(1)) {
			case 'u': //Update item totals
				details = message.substring(2).split(",");
				GameClient.getInstance().getOurPlayer().addItem(Integer.parseInt(details[0]),Integer.parseInt(details[1]));
				break;
			case 'r': //Remove item from bag
				details = message.substring(2).split(",");
				m_game.getOurPlayer().removeItem(Integer.parseInt(details[0]), Integer.parseInt(details[1]));
				break;
			case 'i': //Item used
				try {
					GameClient.getInstance().getUi().getNPCSpeech().advance();
					GameClient.getInstance().getUi().getNPCSpeech().advance();
				} catch (Exception e) {}
				details = message.substring(2).split(",");
				GameClient.getInstance().getUi().talkToNPC(details[0]);
				break;
			}
			break;
		case 'r':
			switch(message.charAt(1)) {
			case '!':
				//A notification regarding the request
				switch(message.charAt(2)) {
				case '0':
					//The player logged out
					break;
				case '1':
					//Players must stand beside each other to battle
					GameClient.getInstance().getUi().getChat().addSystemMessage("You must be standing next to and facing the person you want to battle.");
					break;
				case '2':
					//PvP is disabled on this map
					GameClient.getInstance().getUi().getChat().addSystemMessage("You are not allowed to PvP in this map.");
					break;
				case '3':
					//You must be within 3 squares to force this player to battle
					GameClient.getInstance().getUi().getChat().addSystemMessage("You must be within 3 squares of this player to battle.");
					break;
				case '4':
					GameClient.getInstance().getUi().getChat().addSystemMessage("You need to have more than one pokemon and/or you must wait 1 minute before trading again.");
					break;
				}
				break;
			case 't':
				//Trade Request
				GameClient.getInstance().getUi().getReqWindow().addRequest(message.substring(2), "trade");
				break;
			case 'b':
				//Battle Request
				GameClient.getInstance().getUi().getReqWindow().addRequest(message.substring(2), "battle");
				break;
			case 'c':
				//Request canceled
				GameClient.getInstance().getUi().getReqWindow().removeOffer(message.substring(2));
				break;
			case 's':
				//Sucessful registration
				GameClient.messageDialog(translated.get(23), GameClient.getInstance().getDisplay());
				m_game.getLoadingScreen().setVisible(false);
				m_game.getLoginScreen().showLogin();
				break;
			case '1':
				//Account server offline
				GameClient.messageDialog(translated.get(24), GameClient.getInstance().getDisplay());
				m_game.getLoginScreen().getRegistration().enableRegistration();
				m_game.getLoadingScreen().setVisible(false);
				break;
			case '2':
				GameClient.messageDialog(translated.get(25), GameClient.getInstance().getDisplay());
				m_game.getLoginScreen().getRegistration().enableRegistration();
				m_game.getLoadingScreen().setVisible(false);
				break;
			case '3':
				GameClient.messageDialog(translated.get(26), GameClient.getInstance().getDisplay());
				m_game.getLoginScreen().getRegistration().enableRegistration();
				m_game.getLoadingScreen().setVisible(false);
				break;
			case '4':
				GameClient.messageDialog(translated.get(27), GameClient.getInstance().getDisplay());
				m_game.getLoginScreen().getRegistration().enableRegistration();
				m_game.getLoadingScreen().setVisible(false);
				break;
			case '5':
				GameClient.messageDialog(translated.get(41), GameClient.getInstance().getDisplay());
				m_game.getLoginScreen().getRegistration().enableRegistration();
				m_game.getLoadingScreen().setVisible(false);
				break;
			case '6':
				GameClient.messageDialog("Email too long!", GameClient.getInstance().getDisplay());
				m_game.getLoginScreen().getRegistration().enableRegistration();
				m_game.getLoadingScreen().setVisible(false);
				break;
			}
			break;
		}
	}
}
