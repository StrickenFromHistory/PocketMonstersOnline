package org.pokenet.server.backend.item;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.pokenet.server.backend.item.Item.ItemAttribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The item database
 * @author shadowkanji
 * @author Nushio
 * @author ZombieBear
 */
public class ItemDatabase {
	private static HashMap<Integer, Item> m_items;
	
	public void initialise() {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("res/itemdex.xml"));
			doc.getDocumentElement().normalize();

			NodeList itemsNodeList = doc.getElementsByTagName("itemDatabase");
			for (int s = 0; s < itemsNodeList.getLength(); s++) {
				Node itemNode = itemsNodeList.item(s);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					Element itemDBElement = (Element) itemNode;
					NodeList m_itemsNodeList = itemDBElement.getElementsByTagName("items");
					for (int i = 0; i < m_itemsNodeList.getLength(); i++) {
						m_items = new HashMap<Integer,Item>();
						NodeList itemNodeList = ((Element)m_itemsNodeList.item(i)).getElementsByTagName("item");
						for (int j = 0; j < itemNodeList.getLength(); j++) {
							Item item = new Item();

							//Start Parsing some Items!
							Element readItemElement = (Element) itemNodeList.item(j);
							//m_id
							NodeList m_idList = readItemElement.getElementsByTagName("id");
//							System.out.println(m_idList.item(0).getChildNodes().item(0).getNodeValue());
							item.setId(Integer.parseInt(m_idList.item(0).getChildNodes().item(0).getNodeValue()));

							//m_name
							NodeList m_nameList = readItemElement.getElementsByTagName("name");
//							System.out.println(m_nameList.item(0).getChildNodes().item(0).getNodeValue());
							item.setName(m_nameList.item(0).getChildNodes().item(0).getNodeValue());

							//m_description
							NodeList m_descList = readItemElement.getElementsByTagName("description");
//							System.out.println(m_descList.item(0).getChildNodes().item(0).getNodeValue());
							item.setDescription(m_descList.item(0).getChildNodes().item(0).getNodeValue());

							//m_category
							NodeList m_catList = readItemElement.getElementsByTagName("category");
							//	System.out.println(m_catList.item(0).getChildNodes().item(0).getNodeValue());
							item.setCategory(m_catList.item(0).getChildNodes().item(0).getNodeValue());

							//m_shop
							NodeList m_shopList = readItemElement.getElementsByTagName("shop");
							//	System.out.println(m_shopList.item(0).getChildNodes().item(0).getNodeValue());
							item.setShop(Integer.parseInt(m_shopList.item(0).getChildNodes().item(0).getNodeValue()));

							//m_price
							NodeList m_priceList = readItemElement.getElementsByTagName("price");
							//	System.out.println(m_priceList.item(0).getChildNodes().item(0).getNodeValue());
							item.setPrice(Integer.parseInt(m_priceList.item(0).getChildNodes().item(0).getNodeValue()));

							//m_attributes
							NodeList m_attributes = readItemElement.getElementsByTagName("attributes");
							Element attributesElement = (Element) m_attributes.item(0);
							NodeList m_attributesList = attributesElement.getElementsByTagName("itemAttribute");
							for (int l = 0; l < m_attributesList.getLength(); l++) {
								// Doing a Lame Switch. 
								// Possible Values: POKEMON, MOVESLOT, BATTLE, FIELD, CRAFT, HOLD, OTHER
								if(m_attributesList.item(l).getChildNodes().item(0).getNodeValue().equals("POKEMON"))
									item.addAttribute(ItemAttribute.POKEMON);
								else if(m_attributesList.item(l).getChildNodes().item(0).getNodeValue().equals("MOVESLOT"))
									item.addAttribute(ItemAttribute.MOVESLOT);
								else if(m_attributesList.item(l).getChildNodes().item(0).getNodeValue().equals("BATTLE"))
									item.addAttribute(ItemAttribute.BATTLE);
								else if(m_attributesList.item(l).getChildNodes().item(0).getNodeValue().equals("FIELD"))
									item.addAttribute(ItemAttribute.FIELD);
								else if(m_attributesList.item(l).getChildNodes().item(0).getNodeValue().equals("CRAFT"))
									item.addAttribute(ItemAttribute.CRAFT);
								else if(m_attributesList.item(l).getChildNodes().item(0).getNodeValue().equals("HOLD"))
									item.addAttribute(ItemAttribute.HOLD);
								else if(m_attributesList.item(l).getChildNodes().item(0).getNodeValue().equals("OTHER"))
									item.addAttribute(ItemAttribute.OTHER);
							}
							m_items.put(item.getId(),item);
						}
					}
				}
			}
		} catch (SAXParseException err) {
			System.out.println("** Parsing error, line "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());
		} catch (SAXException e) {
			Exception x = e.getException();
			(x == null ? (Exception) e : x).printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

		
	public HashMap<Integer, Item> getItemsList() {
		return m_items;
	}
	
	/**
	 * Adds an item to the database
	 * @param id
	 * @param i
	 */
	public void addItem(int id, Item i) {
		if(m_items == null)
			m_items = new HashMap<Integer, Item>();
		m_items.put(id, i);
	}
	
	/**
	 * Returns an item based on its id
	 * @param id
	 * @return
	 */
	public Item getItem(int id) {
		return m_items.get(id);
	}
	
	/**
	 * Returns an item based on its name
	 * @param name
	 * @return
	 */
	public Item getItem(String name) {
		Iterator<Item> it = m_items.values().iterator();
		Item i;
		while(it.hasNext()) {
			i = it.next();
			if(i.getName().equalsIgnoreCase(name))
				return i;
		}
		return null;
	}
		
	
	/**
	 * Returns the instance of items in the database
	 * @return the instance of items in the database
	 */
	public static List<Item> getCategoryItems(String category) {
		List<Item> itemList = new ArrayList<Item>();
		for(int i=0;i<=m_items.size();i++){
			try{
				Item item = m_items.get(i);
				if(item.getCategory().equals(category))
					itemList.add(item);
			}catch(Exception e){}
		}
		return itemList;
	}

	/**
	 * Returns the ids of the items that should be added to the shop
	 * @param type 
	 * @return the ids of the items that should be added to the shop
	 */
	public List<Integer> getShopItems(int type){
		List<Integer> shopItems = new ArrayList<Integer>();
		for (int i : m_items.keySet()){
			if (m_items.get(i).getShop() > 0 && 
					m_items.get(i).getShop() == type)
				shopItems.add(i);
		}
		return shopItems;
	}
}
