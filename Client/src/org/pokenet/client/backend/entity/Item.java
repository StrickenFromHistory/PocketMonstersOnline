package org.pokenet.client.backend.entity;

import java.util.ArrayList;
import java.util.List;

public class Item {
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
    public Item(int number,
    		int quantity) {
            m_number = number;
            m_quantity = quantity;
            m_name = Item.getItemName(number);
            m_picname = Item.getPicName(number);
    }

	
	public Item(String name,
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

	public static List<Item> generatePokeballs(){
		List<Item> m_items = new ArrayList<Item>();
		m_items.add(new Item("Pokeball", "pokeball", "Catches Pokemon", 200,100));
		m_items.add(new Item("Greatball", "greatball", "Catches Pokemon", 600,100));
		m_items.add(new Item("Ultraball", "ultraball", "Catches Pokemon", 1000,100));
		return m_items;
	}
	public static List<Item> generateFieldItems(){
		List<Item> m_items = new ArrayList<Item>();
		m_items.add(new Item("Repel", "repel", "Scares Pokemon", 500,100));
		m_items.add(new Item("Super Repel", "superrepel", "Scares Pokemon longer", 500,100));
		m_items.add(new Item("Max Repel", "maxrepel", "Scares Pokemon longer faster better stronger", 500,100));
		return m_items;
	}
	public static List<Item> generatePotions(){
		List<Item> m_items = new ArrayList<Item>();
		m_items.add(new Item("Potion", "potion", "Heals 20 HP", 200,100));
		m_items.add(new Item("Super Potion", "superpotion", "Heals 50 HP", 400,100));
		m_items.add(new Item("Hyper Potion", "hyperpotion", "Heals 200 HP", 800,100));
		m_items.add(new Item("Max Potion", "maxpotion", "Heals ALL HP", 1200,100));
		return m_items;
	}
	public static List<Item> generateStatusHeals(){
		List<Item> m_items = new ArrayList<Item>();
		m_items.add(new Item("Antidote", "antidote", "Cures Poison", 200,100));
		m_items.add(new Item("Parlyz Heal", "parlyzheal", "Cures Parlyz", 200,100));
		m_items.add(new Item("Burn Heal", "burnheal", "Cures Burn", 200,100));
		m_items.add(new Item("Ice Heal", "iceheal", "Cures Frost", 200,100));
		m_items.add(new Item("Awakening", "awakening", "Cures Sleep", 200,100));
		m_items.add(new Item("Full Heal", "fullheal", "Cures anything", 500,100));
		return m_items;
	}
	
	/**
     * Returns the name of the item based on its item number
     * @param number
     * @return
     */
    public static String getItemName(int number) {
    	//We should load item DB from XML, but for now, this will do. 
    	String name = "";
    	switch(number){
    	case 1:
    		name = "Potion";
    		break;
    	case 2:
    		name = "Super Potion";
    		break;
    	case 3:
    		name = "Hyper Potion";
    		break;
    	case 4:
    		name = "Max Potion";
    		break;
    	case 5:
    		name = "Full Restore";
    		break;
    	default:
    		name="";
    		break;
    	}
    	return name;   
    }
    
    /**
     * Returns the picname of the item based on its item number
     * @param number
     * @return
     */
    public static String getPicName(int number) {
    	//We should load item DB from XML, but for now, this will do. 
    	String name = "";
    	switch(number){
    	case 1:
    		name = "potion";
    		break;
    	case 2:
    		name = "superpotion";
    		break;
    	case 3:
    		name = "hyperpotion";
    		break;
    	case 4:
    		name = "maxpotion";
    		break;
    	case 5:
    		name = "fullheal";
    		break;
    	default:
    		name="";
    		break;
    	}
    	return name;   
    }

}