package org.pokenet.items;

import java.util.ArrayList;

public class ItemContents {
	private int m_id;
	private String m_name;
	private String m_description;
	private String m_category;
	private String m_shop;
	private String m_price;
	private ArrayList<Attributes> m_attributes;

	public int getId() {
		return m_id;
	}
	public String getDescription() {
		return m_description;
	}
	public void setDescription(String mDescription) {
		m_description = mDescription;
	}
	public String getCategory() {
		return m_category;
	}
	public void setCategory(String mCategory) {
		m_category = mCategory;
	}
	public String getShop() {
		return m_shop;
	}
	public void setShop(String mShop) {
		m_shop = mShop;
	}
	public String getPrice() {
		return m_price;
	}
	public void setPrice(String mPrice) {
		m_price = mPrice;
	}
	public void setId(int m_id) {
		this.m_id = m_id;
	}
	public String getName() {
		return m_name;
	}
	public void setName(String m_name) {
		this.m_name = m_name;
	}
	public ArrayList<Attributes> getAttributes() {
		return m_attributes;
	}
	public void setAttributes(ArrayList<Attributes> m_attributes) {
		this.m_attributes = m_attributes;
	}
	
	
}
