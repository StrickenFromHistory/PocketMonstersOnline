/**
 * @author Juan M. Rodriguez
 * @date Mon May 25, 2009
 * @version 0.1
 */
package org.pokenet.maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.pokenet.items.ItemBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ReadMaps {
	
	HashMap<String,MapBean> mapList = new HashMap<String,MapBean>();
	
	public static void main(String[] args){
		new ReadMaps().getMapsList();
	}
	
	public ReadMaps() {
	}

	public void getMapsList() {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("maps/0.0.tmx"));
			doc.getDocumentElement().normalize();

			NodeList mapNodeList = doc.getElementsByTagName("map");
			Node properties = mapNodeList.item(0);
			
			if (properties.getNodeType() == Node.ELEMENT_NODE) {
				Element propiertiesDBElement = (Element) properties;
				NodeList propertiesNodeList = propiertiesDBElement.getElementsByTagName("properties");
				for(int i=0;i< propertiesNodeList.getLength();i++){
					NodeList propertyNodeList = ((Element)propertiesNodeList.item(i)).getElementsByTagName("property");
					
					for(int j = 0;j<propertyNodeList.getLength();j++){
						Element readItemElement = (Element) propertyNodeList.item(j);
						String name = readItemElement.getAttribute("name");
						String value = readItemElement.getAttribute("value");
						if(!value.equals("")){
							if(name.equals("dayPokemonLevels")){
								MapBean map = new MapBean(0,0);
								map.setLevels(value);
								map.setPokeName(((Element) propertyNodeList.item(j)).getAttribute("value"));
								mapList.put(map.getPokeName(),map);
							} else if(name.equals("nightPokemonLevels")){
								MapBean map = new MapBean(0,0);
								map.setLevels(value);
								map.setPokeName(((Element) propertyNodeList.item(j)).getAttribute("value"));
								mapList.put(map.getPokeName(),map);
							} else if(name.equals("waterPokemonLevels")){
								MapBean map = new MapBean(0,0);
								map.setLevels(value);
								map.setPokeName(((Element) propertyNodeList.item(j)).getAttribute("value"));
								mapList.put(map.getPokeName(),map);
							} else if(name.equals("fishPokemonLevels")){
								MapBean map = new MapBean(0,0);
								map.setLevels(value);
								map.setPokeName(((Element) propertyNodeList.item(j)).getAttribute("value"));
								mapList.put(map.getPokeName(),map);
							}
							System.out.println(name + " "+value);
						}
					}
				}
			}
//				Node itemNode = itemsNodeList.item(s);
//				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
//					Element itemDBElement = (Element) itemNode;
//					NodeList m_itemsNodeList = itemDBElement.getElementsByTagName("items");
//					for (int i = 0; i < m_itemsNodeList.getLength(); i++) {
//						NodeList itemNodeList = ((Element)m_itemsNodeList.item(i)).getElementsByTagName("item");
//						for (int j = 0; j < itemNodeList.getLength(); j++) {
//							ItemBean ibean = new ItemBean();
//
//							//Start Parsing some Items!
//							Element readItemElement = (Element) itemNodeList.item(j);
//							//m_id
//							NodeList m_idList = readItemElement.getElementsByTagName("id");
////							System.out.println(m_idList.item(0).getChildNodes().item(0).getNodeValue());
//							ibean.setId(Integer.parseInt(m_idList.item(0).getChildNodes().item(0).getNodeValue()));
//
//							//m_name
//							NodeList m_nameList = readItemElement.getElementsByTagName("name");
////							System.out.println(m_nameList.item(0).getChildNodes().item(0).getNodeValue());
//							ibean.setName(m_nameList.item(0).getChildNodes().item(0).getNodeValue());
//
//							//m_description
//							NodeList m_descList = readItemElement.getElementsByTagName("description");
////							System.out.println(m_descList.item(0).getChildNodes().item(0).getNodeValue());
//							ibean.setDescription(m_descList.item(0).getChildNodes().item(0).getNodeValue());
//
//							//m_category
//							NodeList m_catList = readItemElement.getElementsByTagName("category");
//							//							System.out.println(m_catList.item(0).getChildNodes().item(0).getNodeValue());
//							ibean.setCategory(m_catList.item(0).getChildNodes().item(0).getNodeValue());
//
//							//m_shop
//							NodeList m_shopList = readItemElement.getElementsByTagName("shop");
//							//							System.out.println(m_shopList.item(0).getChildNodes().item(0).getNodeValue());
//							ibean.setShop(m_shopList.item(0).getChildNodes().item(0).getNodeValue());
//
//							//m_price
//							NodeList m_priceList = readItemElement.getElementsByTagName("price");
//							//							System.out.println(m_priceList.item(0).getChildNodes().item(0).getNodeValue());
//							ibean.setPrice(m_priceList.item(0).getChildNodes().item(0).getNodeValue());
//
//							//m_attributes
//							NodeList m_attributes = readItemElement.getElementsByTagName("attributes");
//							Element attributesElement = (Element) m_attributes.item(0);
//							NodeList m_attributesList = attributesElement.getElementsByTagName("itemAttribute");
//							for (int l = 0; l < m_attributesList.getLength(); l++) {
////								attr.setItemAttribute(m_attributesList.item(l).getChildNodes().item(0).getNodeValue());
//
//							}
//						}
//					}
//				}
//			}
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
	}
}
