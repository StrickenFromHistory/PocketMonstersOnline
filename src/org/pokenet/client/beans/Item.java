package org.pokenet.client.beans;

import java.util.ArrayList;
import java.util.List;

public class Item {
	private String name;
	private String picname;
	private String description;
	private int cost;
	private int available;
	
	public Item() {
		super();
	}
	
	public Item(String name, String picname, String description, int cost, int available) {
		super();
		this.name = name;
		this.picname = picname;
		this.description = description;
		this.cost = cost;
		this.available = available;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPicname() {
		return picname;
	}
	public void setPicname(String picname) {
		this.picname = picname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getAvailable() {
		return available;
	}
	public void setAvailable(int available) {
		this.available = available;
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
}