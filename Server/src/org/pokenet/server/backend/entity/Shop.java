package org.pokenet.server.backend.entity;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Handles shops. Stored internally in npcs with a shop attribute.
 * @author shadowkanji
 *
 */
public class Shop implements Runnable {
	private HashMap<String, Integer> m_stock;
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
		/*
		 * Generate all the items
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
	 * Returns true if there was enough stock to buy the item
	 * @param itemName
	 * @return
	 */
	public boolean buyItem(String itemName, int quantity) {
		int stock = 0;
		stock = m_stock.get(itemName);
		if(stock - quantity > 0) {
			m_stock.put(itemName, (stock - quantity));
			/*
			 * Decrease delta by 15 seconds to restock the shop sooner
			 */
			m_delta = m_delta - 15000 >= 600000 ? m_delta - 15000 : 6000;
			return true;
		}
		return false;
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
