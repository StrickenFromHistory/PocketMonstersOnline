/**
 * @author Juan M. Rodriguez
 * @date Mon May 25, 2009
 * @version 0.1
 */
package org.pokenet.items;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ReadItems {
	HashMap<Integer,ItemBean> itemsList;

	public ReadItems() {
	}
	public ReadItems(HashMap<Integer,ItemBean> itemsList) {
		this.itemsList = itemsList;
	}

	public HashMap<Integer, ItemBean> getItemsList() {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("items.xml"));
			doc.getDocumentElement().normalize();

			NodeList itemsNodeList = doc.getElementsByTagName("itemDatabase");
			for (int s = 0; s < itemsNodeList.getLength(); s++) {
				Node itemNode = itemsNodeList.item(s);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					Element itemDBElement = (Element) itemNode;
					NodeList m_itemsNodeList = itemDBElement.getElementsByTagName("items");
					for (int i = 0; i < m_itemsNodeList.getLength(); i++) {
						itemsList = new HashMap<Integer,ItemBean>();
						NodeList itemNodeList = ((Element)m_itemsNodeList.item(i)).getElementsByTagName("item");
						for (int j = 0; j < itemNodeList.getLength(); j++) {
							ItemBean ibean = new ItemBean();

							//Start Parsing some Items!
							Element readItemElement = (Element) itemNodeList.item(j);
							//m_id
							NodeList m_idList = readItemElement.getElementsByTagName("id");
//							System.out.println(m_idList.item(0).getChildNodes().item(0).getNodeValue());
							ibean.setId(Integer.parseInt(m_idList.item(0).getChildNodes().item(0).getNodeValue()));

							//m_name
							NodeList m_nameList = readItemElement.getElementsByTagName("name");
//							System.out.println(m_nameList.item(0).getChildNodes().item(0).getNodeValue());
							ibean.setName(m_nameList.item(0).getChildNodes().item(0).getNodeValue());

							//m_description
							NodeList m_descList = readItemElement.getElementsByTagName("description");
//							System.out.println(m_descList.item(0).getChildNodes().item(0).getNodeValue());
							ibean.setDescription(m_descList.item(0).getChildNodes().item(0).getNodeValue());

							//m_category
							NodeList m_catList = readItemElement.getElementsByTagName("category");
							//							System.out.println(m_catList.item(0).getChildNodes().item(0).getNodeValue());
							ibean.setCategory(m_catList.item(0).getChildNodes().item(0).getNodeValue());

							//m_shop
							NodeList m_shopList = readItemElement.getElementsByTagName("shop");
							//							System.out.println(m_shopList.item(0).getChildNodes().item(0).getNodeValue());
							ibean.setShop(m_shopList.item(0).getChildNodes().item(0).getNodeValue());

							//m_price
							NodeList m_priceList = readItemElement.getElementsByTagName("price");
							//							System.out.println(m_priceList.item(0).getChildNodes().item(0).getNodeValue());
							ibean.setPrice(m_priceList.item(0).getChildNodes().item(0).getNodeValue());

							//m_attributes
							NodeList m_attributes = readItemElement.getElementsByTagName("attributes");
							Element attributesElement = (Element) m_attributes.item(0);
							NodeList m_attributesList = attributesElement.getElementsByTagName("itemAttribute");
							ArrayList<Attributes> attrList = new ArrayList<Attributes>();
							for (int l = 0; l < m_attributesList.getLength(); l++) {
								Attributes attr = new Attributes();
								attr.setItemAttribute(m_attributesList.item(l).getChildNodes().item(0).getNodeValue());

								attrList.add(attr);
							}
							ibean.setAttributes(attrList);
							itemsList.put(ibean.getId(),ibean);
						}
					}
				}
			}
		} catch (SAXParseException err) {
			System.out.println("** Error de Parseo, linea "
					+ err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());
		} catch (SAXException e) {
			Exception x = e.getException();
			(x == null ? (Exception) e : x).printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return itemsList;
	}

	public void setItemsList(HashMap<Integer, ItemBean> itemsList) {
		this.itemsList = itemsList;
	}

	public ItemBean getItem(int Id) {
		ItemBean itembean = new ItemBean();
		for(int w = 1; w < 900;w++){
			try{
				ItemBean ibean = itemsList.get(w);
				if(ibean.getId()==Id){
					itembean = ibean;
					w=900;
				}
			}catch(Exception e){} //Not an item. 
		}
		return itembean;
	}
	
	public ItemBean getItem(String name) {
		ItemBean itembean = new ItemBean();
		for(int w = 1; w < 900;w++){
			try{
				ItemBean ibean = itemsList.get(w);
				if(ibean.getName().replaceAll(" ","").toLowerCase()==name.replaceAll(" ","").toLowerCase()){
					itembean = ibean;
					w=900;
				}
			}catch(Exception e){} //Not an item. 
		}
		return itembean;
	}
	
	public void generateShopWiki(){
		File f = new File("output-items.wiki");
		if(f.exists())
			f.delete();
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.println(" = Items List = ");
			for (int shop = 0; shop < 20; shop++){
				pw.println("");
				pw.println(" === Shop "+shop+" === ");
				pw.println("{{{");
				for(int w = 1; w < 9999;w++){
					try{
						ItemBean item = itemsList.get(w);
						if(item.getShop().equals(shop+""))
							pw.println(item.getId()+": "+item.getName().replaceAll("'", "`'`") + " - $"+item.getPrice());
					}catch(Exception e){} //Not an item. 
				}
				pw.println("}}} ");
			}
			pw.flush();
			pw.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
