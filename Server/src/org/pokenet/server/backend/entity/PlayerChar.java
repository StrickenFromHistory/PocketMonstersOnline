package org.pokenet.server.backend.entity;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.mina.common.IoSession;
import org.pokenet.server.GameServer;
import org.pokenet.server.backend.ServerMap;
import org.pokenet.server.backend.item.ItemDatabase;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.impl.WildBattleField;
import org.pokenet.server.feature.TimeService;

/**
 * Represents a player
 * @author shadowkanji
 *
 */
public class PlayerChar extends Char implements Battleable {
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
                    		pokemon.setPp(i, pokemon.getMaxPp(i));
                    	}
                    }
            }
		}
		m_session.write("cH");
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
				//TODO: Clear requests list
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
	 * Catches a Pokemon
	 * @param p
	 */
	public void catchPokemon(Pokemon p) {
		Date d = new Date();
		String date = new SimpleDateFormat ("yyyy-MM-dd:HH-mm-ss").format (d);
		p.setDateCaught(date);
		p.setOriginalTrainer(this.getName());
		//TODO: Add the pokemon to the party/box
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
			if(m_money - (q * m_currentShop.getPriceForItem(ItemDatabase.getInstance().getItem(id).getName())) >= 0) {
				/* Then, check if the player has right amount of badges to buy the item */
				switch(id) {
				case 0:
					//TODO: Check badge based on items that require x amount of badges to buy
					break;
				}
				/* Finally, if the item is in stock, buy it */
				if(m_currentShop.buyItem(ItemDatabase.getInstance().getItem(id).getName(), q)) {
					m_money = m_money - (q * m_currentShop.getPriceForItem(ItemDatabase.getInstance().getItem(id).getName()));
					m_bag.addItem(id, q);
					this.updateClientMoney();
				}
			}
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

	/**
	 * Sets the battlefield for this player
	 */
	public void setBattleField(BattleField b) {
		if(m_battleField == null)
			m_battleField = b;
	}
}
