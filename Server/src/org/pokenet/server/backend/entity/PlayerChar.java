package org.pokenet.server.backend.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.mina.core.session.IoSession;
import org.pokenet.server.GameServer;
import org.pokenet.server.backend.map.ServerMap;
import org.pokenet.server.backend.map.ServerMap.PvPType;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.impl.PvPBattleField;
import org.pokenet.server.battle.impl.WildBattleField;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;
import org.pokenet.server.feature.TimeService;
import org.pokenet.server.network.MySqlManager;
import org.pokenet.server.network.TcpProtocolHandler;
import org.pokenet.server.network.message.ItemMessage;
import org.pokenet.server.network.message.SpriteChangeMessage;
import org.pokenet.server.network.message.shop.ShopBuyMessage;
import org.pokenet.server.network.message.shop.ShopNoItemMessage;
import org.pokenet.server.network.message.shop.ShopNoMoneyMessage;
import org.pokenet.server.network.message.shop.ShopNoSpaceMessage;
import org.pokenet.server.network.message.shop.ShopSellMessage;

/**
 * Represents a player
 * @author shadowkanji
 *
 */
public class PlayerChar extends Char implements Battleable, Tradeable {
	/*
	 * An enum to store request types
	 */
	public enum RequestType { BATTLE, TRADE };
	/*
	 * An enum to store the player's selected language
	 */
	public enum Language { ENGLISH, PORTUGESE, ITALIAN, FRENCH, FINNISH, SPANISH, GERMAN, DUTCH }

	private Language m_language;
	private Bag m_bag;
	private int m_battleId;
	private Pokemon[] m_pokemon;
	private PokemonBox [] m_boxes;
	private boolean m_isBattling = false;
	private boolean m_isShopping = false;
	private boolean m_isTalking = false;
	private boolean m_isBoxing = false;
	private boolean m_isSpriting = false;
	private IoSession m_tcpSession = null;
	private IoSession m_udpSession = null;
	private int m_money;
	private ArrayList<String> m_friends;
	private long m_lastLogin;
	private int m_skillHerbExp = 0;
	private int m_skillCraftExp = 0;
	private int m_skillFishExp = 0;
	private int m_skillTrainingExp = 0;
	private int m_skillCoordExp = 0;
	private int  m_skillBreedExp = 0;
	private int m_oldLevel;
	private BattleField m_battleField = null;
	private int m_healX, m_healY, m_healMapX, m_healMapY;
	private int m_adminLevel = 0;
	private boolean m_isMuted, m_isFishing; 
	private Shop m_currentShop = null;
	private int m_repel = 0;
	private long m_lastTrade = 0;
	/*
	 * Stores movement of other players to be
	 * sent in bulk to client
	 */
	private String [] m_movements = new String [7];
	/*
	 * Kicking timer
	 */
	public long lastPacket = System.currentTimeMillis();
	/*
	 *  Fishing timer
	 */
	public long lastFishingTime = System.currentTimeMillis();
	/*
	 * Trade stuff
	 */
	private Trade m_trade = null;
	private boolean m_isReadyToTrade = false;
	/*
	 * Badges are stored as bytes. 0 = not obtained, 1 = obtained
	 * Stored as following:
	 * 0 - 7   Kanto Badges
	 * 8 - 15  Johto Badges
	 * 16 - 23 Hoenn Badges
	 * 24 - 31 Sinnoh Badges
	 * 32 - 35 Orange Islands
	 * 36 - 41
	 */
	private byte [] m_badges;
	/*
	 * Stores the list of requests the player has sent
	 */
	private HashMap<String, RequestType> m_requests;

	
	/**
	 * Constructor
	 * NOTE: Minimal initialisations should occur here
	 */
	public PlayerChar() {
		m_requests = new HashMap<String, RequestType>();
	}
	
	/**
	 * Queues other player movements to be sent to client in bulk
	 * @param d
	 * @param player
	 */
	public void queueOtherPlayerMovement(Direction d, int player) {
		String s = d.name().toUpperCase().charAt(0) + String.valueOf(player);
		/* Queue the movement */
		for(int i = 0; i < m_movements.length; i++) {
			if(m_movements[i] == null) {
				m_movements[i] = s;
				return;
			}
		}
		/* 
		 * Unsuccessful, queue is full!
		 * Send queue and place at 0
		 */
		String message = "M";
		for(int i = 0; i < m_movements.length; i++) {
			message = message + m_movements[i] + ",";
			m_movements[i] = null;
		}
		message = message.substring(0, message.length() - 1);
		m_tcpSession.write(message);
		m_movements[0] = s;
	}

	/**
	 * Returns this player's ip address
	 * @return
	 */
	public String getIpAddress() {
		if(m_tcpSession != null) {
			String ip = m_tcpSession.getRemoteAddress().toString();
			ip = ip.substring(1);
			ip = ip.substring(0, ip.indexOf(":"));
			return ip;
		} else {
			return "";
		}
	}

	/**
	 * Sets how many steps this Pokemon can repel for
	 * @param steps
	 */
	public void setRepel(int steps) {
		m_repel = steps;
	}

	/**
	 * Returns how many steps this player can repel Pokemon for
	 * @return
	 */
	public int getRepel() {
		return m_repel;
	}


	/**
	 * Releases a pokemon from box
	 * @param box
	 * @param slot
	 */
	public void releasePokemon(int box, int slot) {
		/* If the box doesn't exist, return */
		if(m_boxes[box] == null)
			return;
		/* Check if the pokemon exists */
		if(m_boxes[box].getPokemon(slot) != null) {
			if(m_boxes[box].getPokemon(slot).getDatabaseID() > -1) {
				/* This box exists and the pokemon exists in the database */
				int id = m_boxes[box].getPokemon(slot).getDatabaseID();
				MySqlManager m = new MySqlManager();
				if(m.connect(GameServer.getDatabaseHost(), 
						GameServer.getDatabaseUsername(),
						GameServer.getDatabasePassword())) {
					m.selectDatabase(GameServer.getDatabaseName());
					m.query("DELETE FROM pn_pokemon WHERE id='" + id + "'");
					m.close();
					m_boxes[box].setPokemon(slot, null);
				}
			} else {
				/*
				 * This Pokemon or box has not been saved to the
				 * database yet so just null it.
				 */
				m_boxes[box].setPokemon(slot, null);
			}
		}
	}

	/**
	 * Swaps pokemon between box and party 
	 * @param box
	 * @param boxSlot
	 * @param partySlot
	 */
	public void swapFromBox(int box, int boxSlot, int partySlot) {
		if(box < 0 || box > 8)
			return;
		/* Ensure the box exists */
		if(m_boxes[box] == null) {
			m_boxes[box] = new PokemonBox();
			m_boxes[box].setPokemon(new Pokemon[30]);
		}
		/* Make sure we're not depositing our only Pokemon */
		if(getPartyCount() == 1) {
			if(m_pokemon[partySlot] != null && m_boxes[box].getPokemon(boxSlot) == null)
				return;
		}
		/* Everything is okay, let's get swapping! */
		Pokemon temp = m_pokemon[partySlot];
		m_pokemon[partySlot] = m_boxes[box].getPokemon(boxSlot);
		m_boxes[box].setPokemon(boxSlot, temp);
		if(m_pokemon[partySlot] != null) {
			updateClientParty(partySlot);
		} else {
			m_tcpSession.write("PN" + partySlot);
		}
	}

	/**
	 * Sets if this player is interacting with
	 * a sprite selection npc
	 * @param b
	 */
	public void setSpriting(boolean b) {
		m_isSpriting = b;
	}

	/**
	 * Returns true if this player is
	 * interacting with a sprite selection npc
	 * @return
	 */
	public boolean isSpriting() {
		return m_isSpriting;
	}

	/**
	 * Returns the preferred language of the user
	 * @return
	 */
	public Language getLanguage() {
		return m_language;
	}

	/**
	 * Sets this player's preferred language
	 * @param l
	 */
	public void setLanguage(Language l) {
		m_language = l;
	}

	/**
	 * Returns true if the player is trading
	 * @return
	 */
	public boolean isTrading() {
		return m_trade != null;
	}

	/**
	 * Cancels this player's trade offer
	 */
	public void cancelTradeOffer() {
		m_trade.cancelOffer(this);
	}

	public void cancelTrade() {
		m_trade.endTrade();
	}

	public void finishTrading() {
		m_isTalking = false;
		m_isReadyToTrade = false;
		m_trade = null;
		if(m_tcpSession != null && m_tcpSession.isConnected())
			m_tcpSession.write("Tf");
		ensureHealthyPokemon();
		m_lastTrade = System.currentTimeMillis();
	}

	public void receiveTradeOffer(TradeOffer[] o) {
		m_tcpSession.write("To" + o[0].getId() + "," + o[1].getId());
	}

	public void receiveTradeOfferCancelation() {
		m_tcpSession.write("Tc");
	}

	public void setTradeAccepted(boolean b) {
		m_isReadyToTrade = b;
		if(b)
			m_trade.checkForExecution();
	}

	/**
	 * Returns the trade that the player is involved in
	 * @return
	 */
	public Trade getTrade() {
		return m_trade;
	}

	/**
	 * Sets the trade this player is involved in
	 * @param t
	 */
	public void setTrade(Trade t) {
		m_trade = t;
	}

	/**
	 * Returns true if the player accepted the trade offer
	 * @return
	 */
	public boolean acceptedTradeOffer() {
		return m_isReadyToTrade;
	}

	/**
	 * Returns true if the player is allowed trade
	 * @return
	 */
	public boolean canTrade() {
		return System.currentTimeMillis() - m_lastTrade > 60000 && getPartyCount() >= 2;
	}

	/**
	 * Stores a request the player has sent
	 * @param username
	 * @param r
	 */
	public void addRequest(String username, RequestType r) {
		/* Check if it is a battle request on a pvp enforced map */
		if(r == RequestType.BATTLE) {
			/* 
			 * If the player is on the same map and 
			 * within 3 squares of the player, start the battle
			 */
			if(this.getMap().getPvPType() == PvPType.ENFORCED) {
				PlayerChar otherPlayer = TcpProtocolHandler.getPlayer(username);
				if(otherPlayer != null && this.getMap() == otherPlayer.getMap()) {
					if(otherPlayer.getX() >= this.getX() - 96 || 
							otherPlayer.getX() <= this.getX() + 96 ||
							otherPlayer.getY() >= this.getY() - 96 ||
							otherPlayer.getY() <= this.getY() + 96) {
						/* This is a valid battle, start it */
						ensureHealthyPokemon();
						otherPlayer.ensureHealthyPokemon();
						m_battleField = new PvPBattleField(
								DataService.getBattleMechanics(),this, otherPlayer);
						return;
					} else {
						m_tcpSession.write("r!3");
					}
				}
			}
		}
		/* Else, add the request */
		m_requests.put(username, r);
	}

	/**
	 * Removes a request
	 * @param username
	 */
	public void removeRequest(String username) {
		m_requests.remove(username);
	}

	/**
	 * Called when a player accepts a request sent by this player
	 * @param username
	 */
	public void requestAccepted(String username) {
		PlayerChar otherPlayer = TcpProtocolHandler.getPlayer(username);
		if(otherPlayer != null) {
			if(m_requests.containsKey(username)) {
				switch(m_requests.get(username)) {
				case BATTLE:
					/* First, ensure both players are on the same map */
					if(otherPlayer.getMap() != this.getMap())
						return;
					/* 
					 * Based on the map's pvp type, check this battle is possible
					 * If pvp is enforced, it will be started when the offer is made
					 */
					if(this.getMap().getPvPType() != null) {
						switch(this.getMap().getPvPType()) {
						case DISABLED:
							/* Some maps have pvp disabled */
							otherPlayer.getTcpSession().write("r!2");
							m_tcpSession.write("r!2");
							return;
						case ENABLED:
							/* This is a valid battle, start it */
							ensureHealthyPokemon();
							otherPlayer.ensureHealthyPokemon();
							m_battleField = new PvPBattleField(
									DataService.getBattleMechanics(),this, otherPlayer);
							return;
						}
					} else {
						m_battleField = new PvPBattleField(
								DataService.getBattleMechanics(),this, otherPlayer);
					}
					break;
				case TRADE:
					if(canTrade() && otherPlayer.canTrade()) {
						/* Set the player as talking so they can't move */
						m_isTalking = true;
						/* Create the trade */
						m_trade = new Trade(this, otherPlayer);
						otherPlayer.setTrade(m_trade);
					} else {
						m_tcpSession.write("r!4");
						otherPlayer.getTcpSession().write("r!4");
					}
					break;
				}
			}
		} else {
			m_tcpSession.write("r!0");
		}
	}

	/**
	 * Clears the request list
	 */
	public void clearRequests() {
		if(m_requests.size() > 0) {
			for(String username : m_requests.keySet()) {
				if(TcpProtocolHandler.containsPlayer(username)) {
					TcpProtocolHandler.getPlayer(username).getTcpSession().write("rc" + this.getName());
				}
			}
			m_requests.clear();
		}
	}

	/**
	 * Sets the current shop
	 * @param s
	 */
	public void setShop(Shop s) {
		m_currentShop = s;
	}

	/**
	 * Returns the shop the player is interacting with
	 * @return
	 */
	public Shop getShop() {
		return m_currentShop;
	}

	/**
	 * Creates a new PlayerChar
	 */
	public void createNewPlayer() {
		//Set up all badges.
		m_badges = new byte[42];
		for(int i = 0; i < m_badges.length; i++) {
			m_badges[i] = 0;
		}
		m_isMuted = false;
	}

	/**
	 * Called when a player loses a battle
	 */
	public void lostBattle() {
		/*
		 * Heal the players Pokemon
		 */
		healPokemon();
		/*
		 * Make the Pokemon unhappy
		 */
		for(int i = 0; i < m_pokemon.length; i++) {
			if(m_pokemon[i] != null)
				m_pokemon[i].setHappiness(20);
		}
		/*
		 * Now warp them to the last place they were healed
		 */
		m_x = m_healX;
		m_y = m_healY;
		if(m_tcpSession.isConnected() && !m_tcpSession.isClosing()) {
			this.setMap(GameServer.getServiceManager().getMovementService().getMapMatrix().
					getMapByGamePosition(m_healMapX, m_healMapY), null);
		} else {
			m_mapX = m_healMapX;
			m_mapY = m_healMapY;
		}
		/* Turn back to normal sprite */
		if(isSurfing())
			setSurfing(false);
	}

	/**
	 * Heals the player's pokemon
	 */
	public void healPokemon() {
		for (Pokemon pokemon : getParty()) {
			if (pokemon != null) {
				pokemon.calculateStats(true);
				pokemon.reinitialise();
				pokemon.setIsFainted(false);
				for(int i = 0; i < pokemon.getMoves().length; i++) {
					if(pokemon.getMoves()[i] != null) {
						PokemonMove move = pokemon.getMoves()[i].getMove();
						pokemon.setPp(i, move.getPp() * (5 + pokemon.getPpUpCount(i)) / 5);
						pokemon.setMaxPP(i, move.getPp() * (5 + pokemon.getPpUpCount(i)) / 5);
					}
				}
			}
		}
		m_tcpSession.write("cH");
	}

	/**
	 * Removes temporary status effects such as StatChangeEffects
	 */
	public void removeTempStatusEffects() {
		for(Pokemon pokemon: getParty()) {
			if(pokemon != null) {
				pokemon.removeStatusEffects(false);
			}
		}
	}

	/**
	 * Returns true if this player is accessing their box
	 * @return
	 */
	public boolean isBoxing() {
		return m_isBoxing;
	}

	/**
	 * Sets if this player has box access at the moment
	 * @param b
	 */
	public void setBoxing(boolean b) {
		m_isBoxing = b;
	}

	/**
	 * Returns true if this player is muted
	 * @return
	 */
	public boolean isMuted() {
		return m_isMuted;
	}

	/**
	 * Sets if this player is muted
	 * @param b
	 */
	public void setMuted(boolean b) {
		m_isMuted = b;
	}

	/**
	 * If the player's first Pokemon in party has 0 HP, 
	 * it puts the first Pokemon in their party with more
	 * than 0 HP at the front
	 */
	public void ensureHealthyPokemon() {
		if(m_pokemon[0] == null || m_pokemon[0].getHealth() == 0) {
			for(int i = 1; i < 6; i++) {
				if(m_pokemon[i] != null && m_pokemon[i].getHealth() > 0) {
					swapPokemon(0, i);
					return;
				}
			}
		}
	}

	/**
	 * Swaps two Pokemon in a player's party
	 * @param a
	 * @param b
	 */
	public void swapPokemon(int a, int b) {
		if(a >= 0 && a < 6 && b >= 0 && b < 6) {
			Pokemon temp = m_pokemon[a];
			m_pokemon[a] = m_pokemon[b];
			m_pokemon[b] = temp;
			m_tcpSession.write("s" + a + "," + b);
		}
	}

	/**
	 * Returns true if this player is talking to an npc
	 * @return
	 */
	public boolean isTalking() {
		return m_isTalking;
	}

	/**
	 * Sets if this player is talking to an npc
	 * @param b
	 */
	public void setTalking(boolean b) {
		m_isTalking = b;
	}

	/**
	 * Adds a friend to the friend list
	 * @param username
	 */
	public void addFriend(String username) {
		if(m_friends == null)
			m_friends = new ArrayList<String>();
		if(m_friends.size() < 10) {
			m_friends.add(username);
			m_tcpSession.write("Fa" + username);
		}
	}

	/**
	 * Removes a friend from the friends list
	 * @param username
	 */
	public void removeFriend(String username) {
		if(m_friends == null) {
			m_friends = new ArrayList<String>();
			return;
		}
		for(int i = 0; i < m_friends.size(); i++) {
			if(m_friends.get(i).equalsIgnoreCase(username)) {
				m_friends.remove(i);
				m_tcpSession.write("Fr" + username);
				return;
			}
		}
	}

	/**
	 * Returns the battlefield this player is on.
	 */
	public BattleField getBattleField() {
		return m_battleField;
	}

	/**
	 * Returns the battle id of this player on the battlefield
	 */
	public int getBattleId() {
		return m_battleId;
	}

	/**
	 * Returns this player's opponent
	 */
	public Battleable getOpponent() {
		//DO WE REALLY NEED THIS?
		return null;
	}

	/**
	 * Returns the amount of Pokemon in this player's party
	 * @return
	 */
	public int getPartyCount() {
		int r = 0;
		for(int i = 0; i < m_pokemon.length; i++) {
			if(m_pokemon[i] != null)
				r++;
		}
		return r;
	}

	/**
	 * Returns the highest level pokemon in the player's party
	 * @return
	 */
	public int getHighestLevel() {
		int h = 0;
		for(int i = 0; i < m_pokemon.length; i++) {
			if(m_pokemon[i] != null && h < m_pokemon[i].getLevel())
				h = m_pokemon[i].getLevel();
		}
		return h;
	}

	/**
	 * Returns the Pokemon party of this player
	 */
	public Pokemon[] getParty() {
		return m_pokemon;
	}

	/**
	 * Returns true if this player is battling
	 */
	public boolean isBattling() {
		return m_isBattling;
	}

	/**
	 * Sets if this player is battling
	 * @param b
	 */
	public void setBattling(boolean b) {
		m_isBattling = b;
		if(!m_isBattling) {
			/*
			 * If the player has finished battling
			 * kill their battlefield
			 */
			m_battleField = null;
		}
	}

	/**
	 * Sets this player's battle id on a battlefield
	 */
	public void setBattleId(int battleID) {
		m_battleId = battleID;
	}

	/**
	 * Set the pokemon party of this player
	 */
	public void setParty(Pokemon[] team) {
		m_pokemon = team;
	}

	/**
	 * Sets the TCP session for this player (their connection to the server)
	 * @param session
	 */
	public void setTcpSession(IoSession session) {
		m_tcpSession = session;
	}

	/**
	 * Returns the TCP session (connection to server) for this player
	 * @return
	 */
	public IoSession getTcpSession() {
		return m_tcpSession;
	}

	/**
	 * Returns the UDP session for this player
	 * @return
	 */
	public IoSession getUdpSession() {
		return m_udpSession;
	}

	/**
	 * Fishes for a pokemon.
	 */
	public void fish(int rod) {
		if(this.lastFishingTime + 1000 < System.currentTimeMillis()) {
			if(this.getMap().caughtFish(this, this.getFacing(), rod)) {
				Pokemon p = this.getMap().getWildPokemon(this);
				//If we have both the required level to fish this thing up and the rod to do it
				if(this.getFishingLevel() >= DataService.getFishDatabase().getFish(p.getSpeciesName()).getReqLevel() && rod >= DataService.getFishDatabase().getFish(p.getSpeciesName()).getReqRod()) {
					this.addFishingExp(DataService.getFishDatabase().getFish(p.getSpeciesName()).getExperience());
					this.ensureHealthyPokemon();
					m_battleField = new WildBattleField(DataService.getBattleMechanics(),this,p);
				}
				//If you either have too low a fishing level or too weak a rod
				else {
					m_tcpSession.write("FU"); // Notify client you pulled up a fish too strong for you
					this.addFishingExp(10); //Conciliatory exp for "hooking" something even if it got away
				}
			} else {
				if (this.getMap().facingWater(this, getFacing()));
				m_tcpSession.write("Fu"); //"Not even a nibble!" message
			}
			this.setFishing(false);
		}
	}

	/**
	 * Overrides char's move method.
	 * Adds a check for wild battles and clears battle/trade request lists
	 */
	@Override
	public boolean move(Direction d) {
		if(!m_isBattling && !m_isTalking && !m_isShopping && !m_isBoxing) {
			if(super.move(d)) {
				//If the player moved
				if(this.getMap() != null) {
					if(m_repel > 0)
						m_repel--;
					if(m_repel <= 0 && this.getMap().isWildBattle(m_x, m_y, this)) {
						m_tcpSession.write("U" + getX() + "," + getY());
						this.ensureHealthyPokemon();
						m_battleField = new WildBattleField(
								DataService.getBattleMechanics(),
								this,
								this.getMap().getWildPokemon(this));
						m_movementQueue.clear();
					} else {
						if(m_map.isNpcBattle(this))
							m_movementQueue.clear();
					}
					/* If it wasn't a battle see should we increase happiness */
					if(this.getX() % 32 == 0 || (this.getY() + 8) % 32 == 0) {
						for(int i = 0; i < m_pokemon.length; i++) {
							/* 
							 * Pokemon only have their happiness 
							 * increased by walking if it is below 70
							 */
							if(m_pokemon[i] != null && m_pokemon[i].getHappiness() < 70)
								m_pokemon[i].setHappiness(m_pokemon[i].getHappiness() + 1);
						}
					}
				}
				return true;
			}
		} else {
			if(getPriority() > 0) {
				//Someone has been trying to move in-battle! RESYNC
				m_movementQueue.clear();
				m_tcpSession.write("U" + getX() + "," + getY());
			}
		}
		return false;
	}

	/**
	 * Sets how much money this player has
	 * @param money
	 */
	public void setMoney(int money) {
		m_money = money;
	}

	/**
	 * Returns how much money this player has
	 * @return
	 */
	public int getMoney() {
		return m_money;
	}
	/**
	 * Returns true if this char is fishing
	 * @return
	 */
	public boolean isFishing() {
		return m_isFishing;
	}

	/**
	 * Sets if this char is fishing or not and sends the sprite change information to everyone
	 * @param b
	 */
	public void setFishing(boolean b) {
		m_isFishing = b;
		if(b == true)
			this.lastFishingTime = System.currentTimeMillis();
		if(m_map != null) {
			//Tell clients to update this char to reflect whether player is fishing or not.
		}
	}

	/**
	 * Initializes the client's skill levels
	 */
	public void initializeClientSkills(){
		m_tcpSession.write("cst" + getTrainingLevel());
		m_tcpSession.write("csb" + getBreedingLevel());
		m_tcpSession.write("csf" + getFishingLevel());
		m_tcpSession.write("csc" + getCoordinatingLevel());
	}

	/**
	 * Sets the herbalism skill's exp points
	 * @param exp
	 */
	public void setHerbalismExp(int exp) {
		m_skillHerbExp = exp;
	}

	/**	
	 * Add something to the herbalism skill exp points
	 * @param exp
	 */
	public void addHerbalismExp(int exp) {
		m_oldLevel = getHerbalismLevel();
		m_skillHerbExp = m_skillHerbExp + exp;
		if(getHerbalismLevel() > m_oldLevel && getHerbalismLevel()<= 100) {
			m_tcpSession.write("csh" + getHerbalismLevel());
		}
	}

	/**
	 * Returns the herbalism skill exp points
	 * @return
	 */
	public int getHerbalismExp() {
		return m_skillHerbExp;
	}

	/**
	 * Returns the herbalism skill level
	 * @return
	 */
	public int getHerbalismLevel() {
		int level = (int) Math.pow(m_skillHerbExp, (0.3333));
		if(level <= 100)
			return level;
		else
			return 100;
	}
	/**
	 * Sets the crafting skill exp points
	 * @param exp
	 */
	public void setCraftingExp(int exp) {
		m_skillCraftExp = exp;
	}

	/**	
	 * Add something to the crafting skill exp points
	 * @param exp
	 */
	public void addCraftingExp(int exp) {
		m_oldLevel = getCraftingLevel();
		m_skillCraftExp = m_skillCraftExp + exp;
		if(getCraftingLevel() > m_oldLevel) {
			m_tcpSession.write("csC" + getCraftingLevel());
		}
	}

	/**
	 * Returns the crafting skill exp points
	 * @return
	 */
	public int getCraftingExp() {
		return m_skillCraftExp;
	}

	/**
	 * Returns the crafting skill level
	 * @return
	 */
	public int getCraftingLevel() {
		if(((int)Math.pow(m_skillCraftExp, (0.3333))) <= 100)
			return (int)Math.pow(m_skillCraftExp, (0.3333));
		else
			return 100;
	}
	/**
	 * Sets the fishing skill exp points 
	 * @param exp
	 */
	public void setFishingExp(int exp) {
		m_skillFishExp = exp;
	}

	/**	
	 * Add something to the fishing skill exp points
	 * @param exp
	 */
	public void addFishingExp(int exp) {
		m_oldLevel = getFishingLevel();
		m_skillFishExp = m_skillFishExp + exp;
		if(getFishingLevel() > m_oldLevel) {
			m_tcpSession.write("csf" + getFishingLevel());
		}
	}

	/**
	 * Returns the fishing skill exp points
	 * @return
	 */
	public int getFishingExp() {
		return m_skillFishExp;
	}
	/**
	 * Returns the fishing skill level
	 * @return
	 */
	public int getFishingLevel() {
		int level = ((int)Math.pow(m_skillFishExp, (0.3333)));
		if(level <= 100)
			return level;
		else
			return 100;
	}

	/**	
	 * Set the training skill exp points
	 * @param exp
	 */
	public void setTrainingExp(int exp) {
		m_skillTrainingExp = exp;
	}

	/**	
	 * Add something to the training skill exp points
	 * @param exp
	 */
	public void addTrainingExp(int exp) {
		m_oldLevel = getTrainingLevel();
		m_skillTrainingExp = m_skillTrainingExp + exp;
		if(getTrainingLevel() > m_oldLevel) {
			m_tcpSession.write("cst" + getTrainingLevel());
		}
	}

	/**
	 * Return the training skill exp points
	 * @return
	 */
	public int getTrainingExp() {
		return m_skillTrainingExp;
	}

	/**
	 * Returns the training skill level
	 * @return
	 */
	public int getTrainingLevel() {
		int level = ((int)Math.pow((m_skillTrainingExp/1.25), (0.3333)));
		if (level <= 100)
			return level;
		else
			return 100;
	}

	/**
	 * Sets the co-ordinating skill exp points
	 * @param exp
	 */
	public void setCoordinatingExp(int exp) {
		m_skillCoordExp = exp;
	}

	/**	
	 * Add something to the coordinating skill exp points
	 * @param exp
	 */
	public void addCoordinatingExp(int exp) {
		m_oldLevel = getCoordinatingLevel();
		m_skillCoordExp = m_skillCoordExp + exp;
		if(getCoordinatingLevel() > m_oldLevel) {
			m_tcpSession.write("csc" + getCoordinatingLevel());
		}
	}

	/**
	 * Returns the co-ordinating skill exp points
	 * @return
	 */
	public int getCoordinatingExp() {
		return m_skillCoordExp;
	}

	/**
	 * Returns the co-ordinating skill level
	 * @return
	 */
	public int getCoordinatingLevel() {
		int level = ((int)Math.pow(m_skillCoordExp, (0.3333)));
		if(level <= 100)
			return level;
		else
			return 100;

	}	


	/**
	 * Sets the breeding skill exp points
	 * @param exp
	 */
	public void setBreedingExp(int exp) {
		m_skillBreedExp = exp;
	}

	/**	
	 * Add something to the breeding skill exp points
	 * @param exp
	 */
	public void addBreedingExp(int exp) {
		m_oldLevel = getBreedingLevel();
		m_skillBreedExp = m_skillBreedExp + exp;
		if(getBreedingLevel() > m_oldLevel) {
			m_tcpSession.write("csb" + getBreedingLevel());
		}
	}

	/**
	 * Returns the breeding skill exp
	 * @return
	 */ 
	public int getBreedingExp() {
		return m_skillBreedExp;
	}

	/**
	 * Returns the breeding skill level
	 * @return
	 */
	public int getBreedingLevel() {
		int level = (int)Math.pow((m_skillBreedExp/1.25), (0.3333));
		if(level <= 100)
			return level;
		else
			return 100;
	}	

	/**
	 * Sets this player's boxes
	 * @param boxes
	 */
	public void setBoxes(PokemonBox [] boxes) {
		m_boxes = boxes;
	}

	/**
	 * Returns this player's boxes
	 * @return
	 */
	public PokemonBox[] getBoxes() {
		return m_boxes;
	}

	/**
	 * Stores a caught Pokemon in the player's party or box
	 * @param p
	 */
	public void catchPokemon(Pokemon p) {
		Date d = new Date();
		String date = new SimpleDateFormat ("yyyy-MM-dd:HH-mm-ss").format (d);
		p.setDateCaught(date);
		p.setOriginalTrainer(this.getName());
		p.setDatabaseID(-1);
		addPokemon(p);
		addTrainingExp(1000/p.getRareness());
	}

	/**
	 * Adds a pokemon to this player's party or box
	 * @param p
	 */
	public void addPokemon(Pokemon p) {
		/* See if there is space in the player's party */
		for(int i = 0; i < 6; i++) {
			if(m_pokemon[i] == null) {
				m_pokemon[i] = p;
				updateClientParty(i);
				return;
			}
		}
		/* Else, find space in a box */
		for(int i = 0; i < m_boxes.length; i++) {
			if(m_boxes[i] != null) {
				/* Find space in an existing box */
				for(int j = 0; j < m_boxes[i].getPokemon().length; j++) {
					if(m_boxes[i].getPokemon(j) == null) {
						m_boxes[i].setPokemon(j, p) ;
						return;
					}
				}
			} else {
				/* We need a new box */
				m_boxes[i] = new PokemonBox();
				m_boxes[i].setPokemon(new Pokemon[30]);
				m_boxes[i].setPokemon(0, p);
				break;
			}
		}
	}

	/**
	 * Sets the last login time (used for connection downtimes)
	 * @param t
	 */
	public void setLastLoginTime(long t) {
		m_lastLogin = t;
	}

	/**
	 * Returns the last login time
	 * @return
	 */
	public long getLastLoginTime() {
		return m_lastLogin;
	}

	/**
	 * Returns the player's bag
	 * @return
	 */
	public Bag getBag() {
		return m_bag;
	}

	/**
	 * Sets the player's bag
	 * @param b
	 */
	public void setBag(Bag b) {
		m_bag = b;
	}

	/**
	 * Sets the map for this player
	 */
	@Override
	public void setMap(ServerMap map, Direction dir) {
		char direction = 'n';
		if (dir != null) {
			switch (dir) {
			case Up:
				direction = 'u';
				break;
			case Down:
				direction = 'd';
				break;
			case Left:
				direction = 'l';
				break;
			case Right:
				direction = 'r';
				break;
			}
		}
		super.setMap(map, dir);
		//Clear the requests list
		clearRequests();
		//Send the map switch packet to the client
		m_tcpSession.write("ms" + direction + map.getX() + "," + map.getY() + "," + (map.isWeatherForced() ? map.getWeatherId() : TimeService.getWeatherId()));
		Char c;
		String packet = "mi";
		//Send all player information to the client
		for(PlayerChar p : map.getPlayers().values()) {
			c = p;
			packet = packet + c.getName() + "," + 
			c.getId() + "," + c.getSprite() + "," + c.getX() + "," + c.getY() + "," + 
			(c.getFacing() == Direction.Down ? "D" : 
				c.getFacing() == Direction.Up ? "U" :
					c.getFacing() == Direction.Left ? "L" :
			"R") + ",";
		}
		//Send all npc information to the client
		for(int i = 0; i < map.getNpcs().size(); i++) {
			c = map.getNpcs().get(i);
			if(!c.getName().equalsIgnoreCase("NULL")) {
				/* Send no name to indicate NPC */
				packet = packet + "!NPC!," + 
				c.getId() + "," + c.getSprite() + "," + c.getX() + "," + c.getY() + "," + 
				(c.getFacing() == Direction.Down ? "D" : 
					c.getFacing() == Direction.Up ? "U" :
						c.getFacing() == Direction.Left ? "L" :
				"R") + ",";
			}
		}
		/*
		 * Only send the packet if there were players on the map
		 */
		if(packet.length() > 2)
			m_tcpSession.write(packet);
	}

	/**
	 * Disposes of this player char
	 */
	public void dispose() {
		super.dispose();
		m_pokemon = null;
		m_boxes = null;
		m_friends = null;
		m_bag = null;
		m_currentShop = null;
		m_battleField = null;
	}

	/**
	 * Forces the player to be logged out
	 */
	public void forceLogout() {
		if(m_tcpSession.isConnected()) {
			m_tcpSession.close(true);
		} else {
			GameServer.getServiceManager().getNetworkService().getLogoutManager().queuePlayer(this);
		}
	}

	/**
	 * Sets if this player is interacting with a shop npc
	 * @param b
	 */
	public void setShopping(boolean b) {
		m_isShopping = b;
		if(!b) {
			m_currentShop = null;
		}
	}

	/**
	 * Returns true if this player is shopping
	 * @return
	 */
	public boolean isShopping() {
		return m_isShopping;
	}

	/**
	 * Returns true if the player has the badge
	 * @param badge
	 * @return
	 */
	public boolean hasBadge(int badge) {
		return m_badges[badge] == 1;
	}

	/**
	 * Sets the badges this player has
	 * @param badges
	 */
	public void setBadges(byte [] badges) {
		m_badges = badges;
	}

	/**
	 * Adds a badge to the player's badge collection
	 * @param num
	 */
	public void addBadge(int num) {
		if(num >= 0 && num < m_badges.length) {
			m_badges[num] = 1;
			m_tcpSession.write("cBa" + num);
		}
	}

	/**
	 * Generates the player's badges from a string
	 * @param badges
	 */
	public void generateBadges(String badges) {
		m_badges = new byte[42];
		if(badges == null || badges.equalsIgnoreCase("")) {
			for(int i = 0; i < 42; i++)
				m_badges[i] = 0;
		} else {
			for(int i = 0; i < 42; i++) {
				if(badges.charAt(i) == '1')
					m_badges[i] = 1;
				else
					m_badges[i] = 0;
			}
		}
	}

	/**
	 * Returns the badges of this player
	 * @return
	 */
	public byte[] getBadges() {
		return m_badges;
	}

	/**
	 * Sets the admin level for this player
	 * @param adminLevel
	 */
	public void setAdminLevel(int adminLevel) {
		m_adminLevel = adminLevel;
	}

	/**
	 * Returns the admin level of this player
	 * @return
	 */
	public int getAdminLevel() {
		return m_adminLevel;
	}

	/**
	 * Sets the location this player was last healed at
	 * @param x
	 * @param y
	 * @param mapX
	 * @param mapY
	 */
	public void setLastHeal(int x, int y, int mapX, int mapY) {
		m_healX = x;
		m_healY = y;
		m_healMapX = mapX;
		m_healMapY = mapY;
	}

	/**
	 * Returns the x co-ordinate of this player's last heal point
	 * @return
	 */
	public int getHealX() {
		return m_healX;
	}

	/**
	 * Returns the y co-ordinate of this player's last heal point
	 * @return
	 */
	public int getHealY() {
		return m_healY;
	}

	/**
	 * Returns the map x of this player's last heal point
	 * @return
	 */
	public int getHealMapX() {
		return m_healMapX;
	}

	/**
	 * Returns the map y of this player's last heal point
	 * @return
	 */
	public int getHealMapY() {
		return m_healMapY;
	}

	/**
	 * Returns true if this player can surf
	 * @return
	 */
	public boolean canSurf() {
		return (getTrainingLevel() >= 25);
	}

	/**
	 * Returns how many badges this player has
	 * @return
	 */
	public int getBadgeCount() {
		int result = 0;
		for(int i = 0; i < m_badges.length; i++) {
			if(m_badges[i] == 1)
				result++;
		}
		return result;
	}

	/**
	 * This player talks to the npc in front of them
	 */
	public void talkToNpc() {
		if(m_map != null)
			this.getMap().talkToNpc(this);
	}

	/**
	 * Sends box information to client
	 * @param i - Box number
	 */
	public void sendBoxInfo(int j) {
		/* If box is non-existant, create it and send small packet */
		if(m_boxes[j] == null) {
			m_boxes[j] = new PokemonBox();
			m_boxes[j].setPokemon(new Pokemon[30]);
			m_tcpSession.write("B");
		}
		/* Else send all pokes in box */
		String packet = "";
		for(int i = 0; i < m_boxes[j].getPokemon().length; i++) {
			if(m_boxes[j].getPokemon(i) != null)
				packet = packet + m_boxes[j].getPokemon(i).getSpeciesNumber() + ",";
			else
				packet = packet + ",";
		}
		m_tcpSession.write("B" + packet);
	}

	/**
	 * Allows the player to buy an item
	 * @param id
	 * @param q
	 */
	public void buyItem(int id, int q) {
		/* If the player isn't shopping, ignore this */
		if(m_currentShop == null)
			return;
		if(m_bag.hasSpace(id)) {
			/* First, check if the player can afford this */
			if(m_money - (q * m_currentShop.getPriceForItem(id)) >= 0) {
				/* Finally, if the item is in stock, buy it */
				if(m_currentShop.buyItem(id, q)) {
					m_money = m_money - (q * m_currentShop.getPriceForItem(id));
					m_bag.addItem(id, q);
					this.updateClientMoney();
					//Let player know he bought the item
					TcpProtocolHandler.writeMessage(m_tcpSession, 
							new ShopBuyMessage(GameServer.getServiceManager().getItemDatabase().getItem(id).getId()));
					//Update player inventory
					TcpProtocolHandler.writeMessage(m_tcpSession, new ItemMessage(true, 
							GameServer.getServiceManager().getItemDatabase().getItem(id).getId(), 1));
				}
			}else{
				//Return You have no money, fool!
				TcpProtocolHandler.writeMessage(m_tcpSession, new ShopNoMoneyMessage());
			}
		}else{
			//Send You cant carry any more items!
			TcpProtocolHandler.writeMessage(m_tcpSession, new ShopNoSpaceMessage());
		}
	}
	/**
	 * Allows the player to sell an item
	 * @param id
	 * @param q
	 */
	public void sellItem(int id, int q) {
		/* If the player isn't shopping, ignore this */
		if(m_currentShop == null)
			return;
		if(m_bag.containsItem(id) > -1) { //Guy does have the item he's selling. 
			m_money = m_money + m_currentShop.sellItem(id, q);
			m_bag.removeItem(id, q);
			//Tell the client to remove the item from the player's inventory
			TcpProtocolHandler.writeMessage(m_tcpSession, new ItemMessage(false, 
					GameServer.getServiceManager().getItemDatabase().getItem(id).getId(), q));
			//Update the client's money
			this.updateClientMoney();
			//Let player know he sold the item.
			TcpProtocolHandler.writeMessage(m_tcpSession, 
					new ShopSellMessage(GameServer.getServiceManager().getItemDatabase().getItem(id).getId()));
		} else {
			//Return You don't have that item, fool!
			TcpProtocolHandler.writeMessage(m_tcpSession, new ShopNoItemMessage(GameServer.getServiceManager().getItemDatabase()
					.getItem(id).getName()));
		}
	}

	/**
	 * Updates the player's money clientside
	 */
	public void updateClientMoney() {
		m_tcpSession.write("cM" + m_money);
	}

	/**
	 * Sends all badges to client
	 */
	public void updateClientBadges() {
		String data = "";
		for(int i = 0; i < m_badges.length; i++) {
			data = data + m_badges[i];
		}
		m_tcpSession.write("cBi" + data);
	}

	/**
	 * Sends all party information to the client
	 */
	public void updateClientParty() {
		for(int i = 0; i < this.getParty().length; i++) {
			updateClientParty(i);
		}
	}

	/**
	 * Sends all bag information to the client
	 */
	public void updateClientBag() {
		for(int i = 0; i < this.getBag().getItems().size(); i++) {
			updateClientBag(i);
		}
	}

	/**
	 * Updates the client with their sprite
	 */
	public void updateClientSprite() {
		TcpProtocolHandler.writeMessage(m_tcpSession, new SpriteChangeMessage(m_id, m_sprite));
	}

	/**
	 * Sets the battlefield for this player
	 */
	public void setBattleField(BattleField b) {
		if(m_battleField == null)
			m_battleField = b;
	}

	/**
	 * Returns the index of the pokemon in the player's party
	 * @param p
	 * @return
	 */
	public int getPokemonIndex(Pokemon p) {
		for(int i = 0; i < m_pokemon.length; i++) {
			if(m_pokemon[i] != null) {
				if(p.compareTo(m_pokemon[i]) == 0)
					return i;
			}
		}
		return -1;
	}

	/**
	 * Updates the client for a specific Pokemon
	 * @param index
	 */
	public void updateClientParty(int i) {
		if(this.getParty()[i] != null) {
			m_tcpSession.write("Pi" + i + PokemonSpecies.getDefaultData().getPokemonByName(this.getParty()[i].getSpeciesName()).getSpeciesNumber() + "," +
					this.getParty()[i].getName() + "," +
					this.getParty()[i].getHealth() + "," +
					this.getParty()[i].getGender() + "," +
					(this.getParty()[i].isShiny() ? 1 : 0) + "," +
					this.getParty()[i].getStat(0) + "," +
					this.getParty()[i].getStat(1) + "," +
					this.getParty()[i].getStat(2) + "," +
					this.getParty()[i].getStat(3) + "," +
					this.getParty()[i].getStat(4) + "," +
					this.getParty()[i].getStat(5) + "," +
					this.getParty()[i].getTypes()[0] + "," +
					(this.getParty()[i].getTypes().length > 1 &&
							this.getParty()[i].getTypes()[1] != null ? this.getParty()[i].getTypes()[1] + "," : ",") +
							this.getParty()[i].getExp() + "," +
							this.getParty()[i].getLevel() + "," +
							this.getParty()[i].getAbilityName() + "," +
							this.getParty()[i].getNature().getName() + "," +
							(this.getParty()[i].getMoves()[0] != null ? this.getParty()[i].getMoveName(0) : "") + "," +
							(this.getParty()[i].getMoves()[1] != null ? this.getParty()[i].getMoveName(1) : "") + "," +
							(this.getParty()[i].getMoves()[2] != null ? this.getParty()[i].getMoveName(2) : "") + "," +
							(this.getParty()[i].getMoves()[3] != null ? this.getParty()[i].getMoveName(3) : "")
			);
			/* Update move pp */
			for(int j = 0; j < 4; j++) {
				updateClientPP(i, j);
			}
		}
	}

	/**
	 * Updates stats for a Pokemon
	 * @param i
	 */
	public void updateClientPokemonStats(int i) {
		if(m_pokemon[i] != null) {
			m_tcpSession.write("PS" + i + m_pokemon[i].getHealth() + "," +
					m_pokemon[i].getStat(0) + "," +
					m_pokemon[i].getStat(1) + "," +
					m_pokemon[i].getStat(2) + "," +
					m_pokemon[i].getStat(3) + "," +
					m_pokemon[i].getStat(4) + "," +
					m_pokemon[i].getStat(5));

		}
	}

	/**
	 * Updates the pp of a move
	 * @param poke
	 * @param move
	 */
	public void updateClientPP(int poke, int move) {
		if(this.getParty()[poke] != null && this.getParty()[poke].getMove(move) != null)
			m_tcpSession.write("Pp" + String.valueOf(poke) + String.valueOf(move) 
					+ this.getParty()[poke].getPp(move) + "," + this.getParty()[poke].getMaxPp(move));
	}

	/**
	 * Updates the client for a specific Item
	 * @param index
	 */
	public void updateClientBag(int i) {
		if(this.getBag().getItems().get(i) != null) {
			TcpProtocolHandler.writeMessage(m_tcpSession, new ItemMessage(true, 
					getBag().getItems().get(i).getItemNumber(), 
					getBag().getItems().get(i).getQuantity()));
		}
	}
}
