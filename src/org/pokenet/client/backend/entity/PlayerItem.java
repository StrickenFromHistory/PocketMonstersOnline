package org.pokenet.client.backend.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Image;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.backend.FileLoader;
import org.pokenet.client.backend.ItemDatabase;

public class PlayerItem {
	private int m_number;
	private Item m_item;
	private int m_quantity;
	private Image m_bagImage;
	
	/**
     * Default constructor
     * @param number
     * @param quantity
     */
    public PlayerItem(int number,
    		int quantity) {
            m_number = number;
            m_quantity = quantity;
            m_item = getItem(m_number);
            try {
            	InputStream f;
            	LoadingList.setDeferredLoading(true);
            	if (m_item.getCategory().equalsIgnoreCase("TM")){
            		f = FileLoader.loadFile("res/items/48/TM.png");
            	} else {
            		f = FileLoader.loadFile("res/items/48/" + m_item.getId() + ".png");
            	}
            	m_bagImage = new Image(f, "res/items/48/" + m_item.getId() + ".png", false);
            	LoadingList.setDeferredLoading(false);
            	
            } catch (Exception e){
            	try {
            	InputStream f;
            	LoadingList.setDeferredLoading(true);
            	f = FileLoader.loadFile("res/items/48/0.png");
            	m_bagImage = new Image(f, "res/items/48/0.png", false);
            	LoadingList.setDeferredLoading(false);
            	} catch (Exception e2){
            		e2.printStackTrace();
            	}
            }
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
	public Item getItem() {
		return m_item;
	}
	public void setItem(Item item) {
		this.m_item = item;
	}
	public Image getBagImage(){
		return m_bagImage;
	}
	
	public static List<Item> generatePokeballs(){
		List<Item> m_items = new ArrayList<Item>();
		m_items = ItemDatabase.getCategoryItems("Pokeball");
		return m_items;
	}
	public static List<Item> generateFieldItems(){
		List<Item> m_items = new ArrayList<Item>();
		m_items = ItemDatabase.getCategoryItems("Field");
		return m_items;
	}
	public static List<Item> generatePotions(){
		List<Item> m_items = new ArrayList<Item>();
		m_items = ItemDatabase.getCategoryItems("Potions");
		return m_items;
	}
	public static List<Item> generateStatusHeals(){
		List<Item> m_items = new ArrayList<Item>();
		m_items = ItemDatabase.getCategoryItems("Medicine");
		return m_items;
	}
	
	/**
     * Returns the item based on its item number
     * @param number
     * @return
     */
    public static Item getItem(int number) {
    	Item item = ItemDatabase.getInstance().getItem(number);
    	return item;  
    }
}