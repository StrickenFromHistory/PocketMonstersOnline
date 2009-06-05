package org.pokenet.server.backend.entity;

import java.util.HashMap;
import java.util.Iterator;

import org.pokenet.server.backend.item.ItemDatabase;

/**
 * Handles shops. Stored internally in npcs with a shop attribute.
 * @author shadowkanji
 *
 */
public class Shop implements Runnable {
	private HashMap<String, Integer> m_stock;
	private HashMap<String, Integer> m_prices;
	/*
	 * Delta represents how often the stock should be updated
	 * 
	 * As players buy more stock from this shop,
	 * delta is decreased and restocks occur more quickly
	 * 
	 * If this shop is rarely used, it is rarely restocked
	 */
	private long m_delta;
	private boolean m_isRunning = false;
	
	public Shop() {
		m_stock = new HashMap<String, Integer>();
		m_prices = new HashMap<String, Integer>();
		/*
		 * Generate all the items stock
		 */
	    m_stock.put("POTION", 100);
	    m_stock.put("SUPER POTION", 100);
	    m_stock.put("HYPER POTION", 100);
	    m_stock.put("MAX POTION", 100);
	    m_stock.put("POKEBALL", 100);
	    m_stock.put("GREAT BALL", 100);
	    m_stock.put("ULTRA BALL", 100);
	    m_stock.put("PARALYZ HEAL", 100);
	    m_stock.put("ANTIDOTE", 100);
	    m_stock.put("AWAKENING", 100);
	    m_stock.put("BURN HEAL", 100);
	    m_stock.put("ICE HEAL", 100);
	    m_stock.put("FULL HEAL", 100);
	    m_stock.put("REPEL", 100);
	    m_stock.put("SUPER REPEL", 100);
	    /*
	     * Generate all item prices
	     */
	   m_prices.put("POTION", 300);
	   m_prices.put("SUPER POTION", 700);
	   m_prices.put("HYPER POTION", 1200);
	   m_prices.put("MAX POTION", 2500);
	   m_prices.put("POKEBALL", 200);
	   m_prices.put("GREAT BALL", 2000);
	   m_prices.put("ULTRA BALL", 10000);
	   m_prices.put("PARALYZ HEAL", 200);
	   m_prices.put("ANTIDOTE", 100);
	   m_prices.put("AWAKENING", 250);
	   m_prices.put("BURN HEAL", 250);
	   m_prices.put("ICE HEAL", 250);
	   m_prices.put("FULL HEAL", 600);
	   m_prices.put("REPEL", 350);
	   m_prices.put("SUPER REPEL", 700);
	    /*
	     * Set delta to 20 minutes
	     */
	    m_delta = 1200000;
	}

	/**
	 * Updates stock levels
	 */
	public void run() {
		while(m_isRunning) {
			/*
			 * Loop through all stock updating each quantity by 25
			 */
			Iterator<String> it = m_stock.keySet().iterator();
			String s;
			while(it.hasNext()) {
				s = it.next();
				int q = m_stock.get(s);
				q = q + 25 <= 100 ? q + 25 : 100;
				m_stock.put(s, q);
			}
			/*
			 * Increment delta by 5 minutes so that
			 * less popular shops restock slower
			 */
			m_delta = m_delta + 300000;
			try {
				Thread.sleep(m_delta);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Returns a string of stock data to be sent to the client
	 * @return
	 */
	public String getStockData() {
		String result = "";
		Iterator<String> it = m_stock.keySet().iterator();
		while(it.hasNext()) {
			result = m_stock.get(it.next()) + ",";
		}
		/*
		 * Return the data string without the trailing comma
		 */
		return result.substring(0, result.length() - 1);
	}
	
	/**
	 * Returns the price of an item
	 * @param itemName
	 * @return
	 */
	public int getPriceForItem(int itemid) {	
		return ItemDatabase.getInstance().getItem(itemid).getPrice();
	}
	
	/**
	 * Returns the id of the item bought. -1 if there was no item in stock
	 * @param itemName
	 * @param quantity
	 * @return
	 */
	public boolean buyItem(int itemId, int quantity) {
//		int stock = 0;
//		stock = m_stock.get(itemName.toUpperCase());
//		if(stock - quantity > 0) {
//			m_stock.put(itemName, (stock - quantity));
//			/*
//			 * Decrease delta by 15 seconds to restock the shop sooner
//			 */
//			m_delta = m_delta - 15000 >= 600000 ? m_delta - 15000 : 6000;
//			return true;
//		}
//		return false;
		//TODO: Implement item stocks. 
		return true;
	}
	
	/**
	 * Starts the restocking thread
	 */
	public void start() {
		m_isRunning = true;
		new Thread(this).start();
	}
	
	/**
	 * Stops the restocking thread
	 */
	public void stop() {
		m_isRunning = false;
	}
}
