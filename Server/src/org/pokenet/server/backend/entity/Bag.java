package org.pokenet.server.backend.entity;

import java.util.ArrayList;

/**
 * Represents a player's bag
 * @author shadowkanji
 *
 */
public class Bag {
	private ArrayList<BagItem> m_items;
	private int m_memberId;
	public static int m_bagsize = 30;//30 is the artificial bag size, right?
	
	/**
	 * Default constructor
	 */
	public Bag(int memberid) {
		m_memberId = memberid;
		m_items = new ArrayList<BagItem>();
	}
	
	/**
	 * Returns true if there is space in the bag for that item
	 * @param id
	 * @return
	 */
	public boolean hasSpace(int itemid) {
			if(containsItem(itemid) >= 0 || m_items.size() < m_bagsize)
				return true;
			else
				return false;
	}
	
	/**
	 * Adds an item to the bag. Returns true on success
	 * @param itemNumber
	 * @param quantity
	 */
	public boolean addItem(int itemNumber, int quantity) {
		int bagIndex = containsItem(itemNumber);
		if(bagIndex > -1){
			m_items.get(bagIndex).setQuantity(m_items.get(bagIndex).getQuantity()+quantity);
			return true;
		}else{
			if(m_items.size()<30){
				m_items.add(new BagItem(itemNumber,quantity));
				return true;
			}else{
				return false;
			}
		}
	}
	
	/**
	 * Removes an item. Returns true on success.
	 * @param itemNumber
	 * @param quantity
	 * @return
	 */
	public boolean removeItem(int itemNumber, int quantity) {
		for(int i = 0; i < m_items.size(); i++) {
			if(m_items.get(i).getItemNumber() == itemNumber) {
				if(m_items.get(i).getQuantity() - quantity > 0)
					m_items.get(i).setQuantity(m_items.get(i).getQuantity() - quantity);
				else
					m_items.remove(i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if item is in bag. Returns bagIndex if true, else returns -1. 
	 * @param itemNumber
	 * @param quantity
	 */
	public int containsItem(int itemNumber) {
		int bagIndex = -1;
		for(int i = 0; i < m_items.size(); i++) {
			if(m_items.get(i).getItemNumber() == itemNumber){
				bagIndex = i;
				break;//End for loop. We found what we're looking for. 
			}
		}
		return bagIndex;
	}
	
	/**
	 * Checks if item is in bag. Returns quantity of items.  
	 * @param itemNumber
	 * @param quantity
	 */
	public int getItemQuantity(int itemNumber) {
		int quantity = 0;
		for(int i = 0; i < m_items.size(); i++) {
			if(m_items.get(i).getItemNumber() == itemNumber){
				quantity = m_items.get(i).getQuantity();
				i = m_items.size();//End for loop. We found what we're looking for. 
			}
		}
		return quantity;
	}
	
	/**
	 * Returns all the items in the bag
	 */
	public ArrayList<BagItem> getItems() {
		return m_items;
	}
	
	/**
	 * Sets the member id of this bag
	 * @param id
	 */
	public void setMemberId(int id) {
		m_memberId = id;
	}
	
	/**
	 * Returns this bag's member id
	 * @return
	 */
	public int getMemberId() {
		return m_memberId;
	}
	
	/**
	 * Returns true if the bag contains the item
	 * @param itemId
	 * @return
	 */
	public boolean contains(int itemId) {
		return false;
	}
}
