package org.pokenet.server.backend.entity;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.mina.common.IoSession;
import org.pokenet.server.GameServer;
import org.pokenet.server.backend.ServerMap;
import org.pokenet.server.backend.item.ItemDatabase;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.impl.WildBattleField;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;
import org.pokenet.server.feature.TimeService;
import org.pokenet.server.network.ConnectionManager;

/**
 * Represents a player
 * @author shadowkanji
 *
 */
public class PlayerChar extends Char implements Battleable {
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
	private IoSession m_session = null;
	private int m_money;
	private ResultSet m_databasePokemon;
	private ArrayList<String> m_friends;
	private long m_lastLogin;
	private double m_npcMultiplier;
	private int m_skillHerb = 0;
	private int m_skillCraft = 0;
	private int m_skillFish = 0;
	private int m_skillTraining = 0;
	private int m_skillCoord = 0;
	private int  m_skillBreed = 0;
	private BattleField m_battleField = null;
	private int m_healX, m_healY, m_healMapX, m_healMapY;
	private int m_adminLevel = 0;
	private boolean m_isMuted;
	private Shop m_currentShop = null;
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
	 * Sets if this player accepted the trade offer
	 * @param b
	 * @return
	 */
	public void setTradeOfferAccepted(boolean b) {
		m_isReadyToTrade = b;
		if(b)
			m_trade.checkForExecution();
	}
	
	/**
	 * Stops this player trading
	 */
	public void endTrading() {
		m_isTalking = false;
		m_isReadyToTrade = false;
		m_trade = null;
		if(m_session != null && m_session.isConnected())
			m_session.write("Tf");
	}
	
	/**
	 * Constructor
	 * NOTE: Minimal initialisations should occur here
	 */
	public PlayerChar() {
		m_requests = new HashMap<String, RequestType>();
	}
	
	/**
	 * Stores a request the player has sent
	 * @param username
	 * @param r
	 */
	public void addRequest(String username, RequestType r) {
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
		if(m_requests.containsKey(username)) {
			switch(m_requests.get(username)) {
			case BATTLE:
				//TODO: Start PvP battle
				break;
			case TRADE:
				/* Set the player as talking so they can't move */
				m_isTalking = true;
				/* Create the trade */
				PlayerChar otherPlayer = ConnectionManager.getPlayers().get(username);
				m_trade = new Trade(this, otherPlayer);
				otherPlayer.setTrade(m_trade);
				break;
			}
		}
	}
	
	/**
	 * Clears the request list
	 */
	public void clearRequests() {
		if(m_requests.size() > 0) {
			for(String username : m_requests.keySet()) {
				if(ConnectionManager.getPlayers().containsKey(username)) {
					ConnectionManager.getPlayers().get(username).getSession().write("rc" + this.getName());
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
		 * Now warp them to the last place they were healed
		 */
		m_x = m_healX;
		m_y = m_healY;
		this.setMap(GameServer.getServiceManager().getMovementService().getMapMatrix().
				getMapByGamePosition(m_healMapX, m_healMapY));
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
		m_session.write("cH");
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
		if(m_pokemon[0].getHealth() == 0) {
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
			if(m_pokemon[a] != null && m_pokemon[b] != null) {
				Pokemon temp = m_pokemon[a];
				m_pokemon[a] = m_pokemon[b];
				m_pokemon[b] = temp;
				m_session.write("s" + a + "," + b);
			}
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
			m_session.write("Fa" + username);
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
				m_session.write("Fr" + username);
				return;
			}
		}
	}
	
	/**
	 * Sets the badges this player has
	 * @param badges
	 */
	public void setBadges(byte [] badges) {
		m_badges = badges;
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
	 * Sets the session for this player (their connection to the server)
	 * @param session
	 */
	public void setSession(IoSession session) {
		m_session = session;
	}
	
	/**
	 * Returns the session (connection to server) for this player
	 * @return
	 */
	public IoSession getSession() {
		return m_session;
	}
	
	/**
	 * Forces the player to move in the direction they are facing.
	 * Returns true if they were moved
	 */
	public boolean forceMove() {
		this.setNextMovement(getFacing());
		return super.move();
	}
	
	/**
	 * Overrides char's move method.
	 * Adds a check for wild battles and clears battle/trade request lists
	 */
	@Override
	public boolean move() {
		if(!m_isBattling && !m_isTalking && !m_isShopping && !m_isBoxing) {
			if(super.move()) {
				//If the player moved
				if(this.getMap() != null) {
					if(this.getMap().isWildBattle(m_x, m_y, this)) {
						this.ensureHealthyPokemon();
						m_battleField = new WildBattleField(
								DataService.getBattleMechanics(),
								this,
								this.getMap().getWildPokemon(this));
					} else {
						m_map.isNpcBattle(this);
					}
				}
				return true;
			}
		} else {
			//Ignore any movement request if the player can't move
			this.setNextMovement(null);
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
	 * Sets the npc multiplier. Used for exp gain for NPC battles.
	 * @param m
	 */
	public void setNpcMultiplier(double m) {
		m_npcMultiplier = m;
	}
	
	/**
	 * Returns the npc multiplier
	 * @return
	 */
	public double getNpcMultiplier() {
		return m_npcMultiplier;
	}
	
	/**
	 * Sets the herbalism skill's exp points
	 * @param exp
	 */
	public void setHerbalismExp(int exp) {
		m_skillHerb = exp;
	}
	
	/**
	 * Returns the herbalism skill exp points
	 * @return
	 */
	public int getHerbalismExp() {
		return m_skillHerb;
	}
	
	/**
	 * Sets the crafting skill exp points
	 * @param exp
	 */
	public void setCraftingExp(int exp) {
		m_skillCraft = exp;
	}
	
	/**
	 * Returns the crafting skill exp points
	 * @return
	 */
	public int getCraftingExp() {
		return m_skillCraft;
	}
	
	/**
	 * Sets the fishing skill exp points 
	 * @param exp
	 */
	public void setFishingExp(int exp) {
		m_skillFish = exp;
	}
	
	/**
	 * Returns the fishing skill exp points
	 * @return
	 */
	public int getFishingExp() {
		return m_skillFish;
	}
	
	/**
	 * Set the training skill exp points
	 * @param exp
	 */
	public void setTrainingExp(int exp) {
		m_skillTraining = exp;
	}
	
	/**
	 * Return the training skill exp points
	 * @return
	 */
	public int getTrainingExp() {
		return m_skillTraining;
	}
	
	/**
	 * Sets the co-ordinating skill exp points
	 * @param exp
	 */
	public void setCoordinatingExp(int exp) {
		m_skillCoord = exp;
	}
	
	/**
	 * Returns the co-ordinating skill exp points
	 * @return
	 */
	public int getCoordinatingExp() {
		return m_skillCoord;
	}
	
	/**
	 * Sets the breeding skill exp points
	 * @param exp
	 */
	public void setBreedingExp(int exp) {
		m_skillBreed = exp;
	}
	
	/**
	 * Returns the breeding skill exp
	 * @return
	 */
	public int getBreedingExp() {
		return m_skillBreed;
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
	 * Stores the id of this player's party and boxes in the database
	 * @param r
	 */
	public void setDatabasePokemon(ResultSet r) {
		m_databasePokemon = r;
	}
	
	/**
	 * Returns the result set of the party and box ids in the database
	 * @return
	 */
	public ResultSet getDatabasePokemon() {
		return m_databasePokemon;
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
				for(int j = 0; j < m_boxes[i].getPokemon().length; j++)
					if(m_boxes[i].getPokemon(j) == null) {
						m_boxes[i].setPokemon(j, p) ;
						return;
					}
			} else {
				/* We need a new box */
				m_boxes[i] = new PokemonBox();
				m_boxes[i].setDatabaseId(-1);
				m_boxes[i].setPokemon(new Pokemon[20]);
				m_boxes[i].setPokemon(0, p);
				return;
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
	public void setMap(ServerMap map) {
		super.setMap(map);
		//Clear the requests list
		clearRequests();
		//Send the map switch packet to the client
		m_session.write("ms" + map.getX() + "," + map.getY() + "," + (map.isWeatherForced() ? map.getWeatherId() : TimeService.getWeatherId()));
		Char c;
		String packet = "mi";
		//Send all player information to the client
		for(int i = 0; i < map.getPlayers().size(); i++) {
			c = map.getPlayers().get(i);
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
				packet = packet + c.getName() + "," + 
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
			m_session.write(packet);
	}
	
	/**
	 * Disposes of this player char
	 */
	public void dispose() {
		super.dispose();
		m_pokemon = null;
		m_boxes = null;
		m_databasePokemon = null;
		m_friends = null;
		m_bag = null;
		m_currentShop = null;
		m_battleField = null;
	}
	
	/**
	 * Forces the player to be logged out
	 */
	public void forceLogout() {
		m_session.close();
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
		return m_skillTraining >= 4;
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
		if(m_boxes[j] != null) {
			String packet = "";
			for(int i = 0; i < m_boxes[j].getPokemon().length; i++) {
				if(m_boxes[j].getPokemon(i) != null)
					packet = packet + m_boxes[j].getPokemon(i).getSpeciesNumber() + ",";
			}
			if(!packet.equalsIgnoreCase(""))
				m_session.write("B" + packet);
		}
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
				/* Then, check if the player has right amount of badges to buy the item */
//				switch(id) {
//				case 0:
//					//TODO: Check badge based on items that require x amount of badges to buy
//					break;
//				}
				/* Finally, if the item is in stock, buy it */
				if(m_currentShop.buyItem(id, q)) {
					m_money = m_money - (q * m_currentShop.getPriceForItem(id));
					m_bag.addItem(id, q);
					this.updateClientMoney();
					//Let player know he bought potion.
					m_session.write("Sb" + ItemDatabase.getInstance().getItem(id).getName());
				}
			}else{
				//Return You have no money, fool!
				m_session.write("Sn");
			}
		}else{
			//Send You cant carry any more items!
			m_session.write("Sf");
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
		if(m_bag.checkItem(id)>0) { //Guy does have the item he's selling. 
			m_money = m_money + (q * m_currentShop.getPriceForItem(id));
			m_bag.removeItem(id, q);
			this.updateClientMoney();
			//Let player know he bought potion. 
		}
	}
	/**
	 * Updates the player's money clientside
	 */
	public void updateClientMoney() {
		m_session.write("cM" + m_money);
	}
	
	/**
	 * Sends all badges to client
	 */
	public void updateClientBadges() {
		String data = "";
		for(int i = 0; i < m_badges.length; i++) {
			data = data + m_badges[i];
		}
		m_session.write("cB" + data);
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
				if(p.getDatabaseID() != -1 && p.getDatabaseID() == m_pokemon[i].getDatabaseID())
					return i;
				else if(p.getDateCaught() != null && m_pokemon[i].getDateCaught() != null) {
					if(p.getDateCaught().equalsIgnoreCase(m_pokemon[i].getDateCaught()))
						return i;
				} else if(p == m_pokemon[i])
					return i;
			}
		}
		return 0;
	}

	/**
	 * Updates the client for a specific Pokemon
	 * @param index
	 */
	public void updateClientParty(int i) {
		if(this.getParty()[i] != null) {
			m_session.write("Pi" + i + PokemonSpecies.getDefaultData().getPokemonByName(this.getParty()[i].getSpeciesName()) + "," +
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
					(this.getParty()[i].getMoves()[0] != null ? this.getParty()[i].getMoves()[0].getName() : "") + "," +
					(this.getParty()[i].getMoves()[1] != null ? this.getParty()[i].getMoves()[1].getName() : "") + "," +
					(this.getParty()[i].getMoves()[2] != null ? this.getParty()[i].getMoves()[2].getName() : "") + "," +
					(this.getParty()[i].getMoves()[3] != null ? this.getParty()[i].getMoves()[3].getName() : "")
			);
		}
	}
}
