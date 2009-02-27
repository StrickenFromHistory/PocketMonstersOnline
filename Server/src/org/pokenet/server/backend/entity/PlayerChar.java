package org.pokenet.server.backend.entity;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.mina.common.IoSession;
import org.pokenet.server.GameServer;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.Pokemon;

/**
 * Represents a player
 * @author shadowkanji
 *
 */
public class PlayerChar extends Char implements Battleable {
	private Bag m_bag;
	private Pokemon[] m_pokemon;
	private PokemonBox [] m_boxes;
	private boolean m_isBattling = false;
	private IoSession m_session;
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
	 * Creates a new PlayerChar
	 */
	public void createNewPlayer() {
		//Set up all badges.
		m_badges = new byte[42];
		for(int i = 0; i < m_badges.length; i++) {
			m_badges[i] = 0;
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
		return GameServer.getServiceManager().getBattleFieldForPlayer(this);
	}

	/**
	 * Returns the battle id of this player on the battlefield
	 */
	public int getBattleID() {
		//NOTE: I HAVE NO IDEA WHAT THIS DOES? IS IT THE TRAINER INDEX ON THE BATTLEFIELD?
		return 0;
	}

	/**
	 * Returns this player's opponent
	 */
	public Battleable getOpponent() {
		//DO WE REALLY NEED THIS?
		return null;
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
	 * Sets this player's battle id on a battlefield
	 */
	public void setBattleID(int battleID) {
	
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
	 * Sends an error code to the client. Must be between -128 to 127.
	 * See the wiki page of error codes @ http://pokenetonline.googlecode.com
	 * @param error
	 */
	public void sendErrorCode(String error) {
		
	}
	
	/**
	 * Overrides char's move method.
	 * Adds a check for wild battles and clears battle/trade request lists
	 */
	public void move() {
		super.move();
		if(this.getMap().isWildBattle())
			GameServer.getServiceManager().getBattleService().startWildBattle(this, this.getMap().getWildPokemon(this));
		//TODO: Clear requests list
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
		p.setOriginalNo(this.getId());
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
}
