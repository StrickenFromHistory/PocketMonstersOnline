package net.pokenet.main;

import java.util.ArrayList;

public class ItemContents {
	private int m_id;
	private String m_name;
	private String m_description;
	private String m_category;
	private String m_shop;
	private String m_price;
	private ArrayList<Attributes> m_attributes;

	public int getM_id() {
		return m_id;
	}
	public String getM_description() {
		return m_description;
	}
	public void setM_description(String mDescription) {
		m_description = mDescription;
	}
	public String getM_category() {
		return m_category;
	}
	public void setM_category(String mCategory) {
		m_category = mCategory;
	}
	public String getM_shop() {
		return m_shop;
	}
	public void setM_shop(String mShop) {
		m_shop = mShop;
	}
	public String getM_price() {
		return m_price;
	}
	public void setM_price(String mPrice) {
		m_price = mPrice;
	}
	public void setM_id(int m_id) {
		this.m_id = m_id;
	}
	public String getM_name() {
		return m_name;
	}
	public void setM_name(String m_name) {
		this.m_name = m_name;
	}
	public ArrayList<Attributes> getM_attributes() {
		return m_attributes;
	}
	public void setM_attributes(ArrayList<Attributes> m_attributes) {
		this.m_attributes = m_attributes;
	}
	
	
}
