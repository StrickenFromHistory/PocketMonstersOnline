package org.pokenet.client.backend.entity;

import java.util.ArrayList;
import java.util.List;

import org.pokenet.client.backend.ItemDatabase;

public class PlayerItem {
	private int m_number;
	private String m_name;
	private String m_picname;
	private String m_description;
	private int m_quantity;
	private int m_cost;
	private int m_available;
	
	/**
     * Default constructor
     * @param number
     * @param quantity
     */
    public PlayerItem(int number,
    		int quantity) {
            m_number = number;
            m_quantity = quantity;
    }

	
	public PlayerItem(String name,
			String picname,
			String description,
			int cost,
			int available) {
		super();
		this.m_name = name;
		this.m_picname = picname;
		this.m_description = description;
		this.m_cost = cost;
		this.m_available = available;
	}
	
	public String getName() {
		return m_name;
	}
	public void setName(String name) {
		this.m_name = name;
	}
	public String getPicname() {
		return m_picname;
	}
	public void setPicname(String picname) {
		this.m_picname = picname;
	}
	public String getDescription() {
		return m_description;
	}
	public void setDescription(String description) {
		this.m_description = description;
	}
	public int getCost() {
		return m_cost;
	}
	public void setCost(int cost) {
		this.m_cost = cost;
	}
	public int getAvailable() {
		return m_available;
	}
	public void setAvailable(int available) {
		this.m_available = available;
	}
	public int getNumber() {
		return m_number;
	}
	public void setNumber(int m_number) {
		this.m_number = m_number;
	}
	public int getQuantity() {
		return m_quantity;
	}
	public void setQuantity(int m_quantity) {
		this.m_quantity = m_quantity;
	}

	public static List<PlayerItem> generatePokeballs(){
		List<PlayerItem> m_items = new ArrayList<PlayerItem>();
		m_items.add(new PlayerItem("Pokeball", "pokeball", "Catches Pokemon", 200,100));
		m_items.add(new PlayerItem("Greatball", "greatball", "Catches Pokemon", 600,100));
		m_items.add(new PlayerItem("Ultraball", "ultraball", "Catches Pokemon", 1000,100));
		return m_items;
	}
	public static List<PlayerItem> generateFieldItems(){
		List<PlayerItem> m_items = new ArrayList<PlayerItem>();
		m_items.add(new PlayerItem("Repel", "repel", "Scares Pokemon", 500,100));
		m_items.add(new PlayerItem("Super Repel", "superrepel", "Scares Pokemon longer", 500,100));
		m_items.add(new PlayerItem("Max Repel", "maxrepel", "Scares Pokemon longer faster better stronger", 500,100));
		return m_items;
	}
	public static List<PlayerItem> generatePotions(){
		List<PlayerItem> m_items = new ArrayList<PlayerItem>();
		m_items.add(new PlayerItem("Potion", "potion", "Heals 20 HP", 200,100));
		m_items.add(new PlayerItem("Super Potion", "superpotion", "Heals 50 HP", 400,100));
		m_items.add(new PlayerItem("Hyper Potion", "hyperpotion", "Heals 200 HP", 800,100));
		m_items.add(new PlayerItem("Max Potion", "maxpotion", "Heals ALL HP", 1200,100));
		return m_items;
	}
	public static List<PlayerItem> generateStatusHeals(){
		List<PlayerItem> m_items = new ArrayList<PlayerItem>();
		m_items.add(new PlayerItem("Antidote", "antidote", "Cures Poison", 200,100));
		m_items.add(new PlayerItem("Parlyz Heal", "parlyzheal", "Cures Parlyz", 200,100));
		m_items.add(new PlayerItem("Burn Heal", "burnheal", "Cures Burn", 200,100));
		m_items.add(new PlayerItem("Ice Heal", "iceheal", "Cures Frost", 200,100));
		m_items.add(new PlayerItem("Awakening", "awakening", "Cures Sleep", 200,100));
		m_items.add(new PlayerItem("Full Heal", "fullheal", "Cures anything", 500,100));
		return m_items;
	}
	
	/**
     * Returns the item based on its item number
     * @param number
     * @return
     */
    public static PlayerItem getItem(int number) {
    	//We should load item DB from XML, but for now, this will do. 
    	PlayerItem item = ItemDatabase.getInstance().getItem(number);
    	return item;  
    }
}