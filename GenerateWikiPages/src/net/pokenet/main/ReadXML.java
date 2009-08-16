/**
 * @author Juan M. Rodriguez
 * @date Mon May 25, 2009
 * @version 0.1
 */
package net.pokenet.main;

import java.io.File;
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

public class ReadXML {

	public static void main(String[] args) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("input-items.xml"));
			doc.getDocumentElement().normalize();
			
			NodeList itemsNodeList = doc.getElementsByTagName("itemDatabase");
			for (int s = 0; s < itemsNodeList.getLength(); s++) {
				Node itemNode = itemsNodeList.item(s);
				if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
					Element itemDBElement = (Element) itemNode;
					NodeList m_itemsNodeList = itemDBElement.getElementsByTagName("m_items");
					for (int i = 0; i < m_itemsNodeList.getLength(); i++) {
						NodeList entryNodeList = ((Element)m_itemsNodeList.item(i)).getElementsByTagName("entry");
						HashMap<Integer,ItemBean> hasshy = new HashMap<Integer,ItemBean>();
						for (int j = 0; j < entryNodeList.getLength(); j++) {
							ItemBean ibean = new ItemBean();
							
							//Set Integer
							NodeList integerNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("integer");
							ibean.setBigID(Integer.parseInt(integerNodeList.item(0).getChildNodes().item(0).getNodeValue()));
							//Start Parsing some Items!
							NodeList itemNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("item");
							Element readItemElement = (Element) itemNodeList.item(0);
							
							ItemContents icontents = new ItemContents();
							//m_id
							NodeList m_idList = readItemElement.getElementsByTagName("m_id");
//							System.out.println(m_idList.item(0).getChildNodes().item(0).getNodeValue());
							icontents.setM_id(Integer.parseInt(m_idList.item(0).getChildNodes().item(0).getNodeValue()));
							
							//m_name
							NodeList m_nameList = readItemElement.getElementsByTagName("m_name");
//							System.out.println(m_nameList.item(0).getChildNodes().item(0).getNodeValue());
							icontents.setM_name(m_nameList.item(0).getChildNodes().item(0).getNodeValue());
							
							//m_description
							NodeList m_descList = readItemElement.getElementsByTagName("m_description");
//							System.out.println(m_descList.item(0).getChildNodes().item(0).getNodeValue());
							icontents.setM_description(m_descList.item(0).getChildNodes().item(0).getNodeValue());
							
							//m_category
							NodeList m_catList = readItemElement.getElementsByTagName("m_category");
//							System.out.println(m_catList.item(0).getChildNodes().item(0).getNodeValue());
							icontents.setM_category(m_catList.item(0).getChildNodes().item(0).getNodeValue());
							
							//m_shop
							NodeList m_shopList = readItemElement.getElementsByTagName("m_shop");
//							System.out.println(m_shopList.item(0).getChildNodes().item(0).getNodeValue());
							icontents.setM_shop(m_shopList.item(0).getChildNodes().item(0).getNodeValue());
							
							//m_price
							NodeList m_priceList = readItemElement.getElementsByTagName("m_price");
//							System.out.println(m_priceList.item(0).getChildNodes().item(0).getNodeValue());
							icontents.setM_price(m_priceList.item(0).getChildNodes().item(0).getNodeValue());
							
							//m_attributes
							NodeList m_attributes = readItemElement.getElementsByTagName("m_attributes");
							Element attributesElement = (Element) m_attributes.item(0);
							NodeList m_attributesList = attributesElement.getElementsByTagName("itemAttribute");
							ArrayList<Attributes> attrList = new ArrayList<Attributes>();
							for (int l = 0; l < m_attributesList.getLength(); l++) {
								Attributes attr = new Attributes();
//								System.out.println(m_attributesList.item(l).getChildNodes().item(0).getNodeValue());
								attr.setItemAttribute(m_attributesList.item(l).getChildNodes().item(0).getNodeValue());
								
								attrList.add(attr);
							}
							icontents.setM_attributes(attrList);
							ibean.setItem(icontents);
							hasshy.put(ibean.getBigID(),ibean);
						}
						
						
						File f = new File("output-items.wiki");
						if(f.exists())
							f.delete();
						PrintWriter pw = new PrintWriter(f);
						pw.println(" = Items List = ");
						for (int shop = 0; shop < 20; shop++){
							pw.println("");
							pw.println(" === Shop "+shop+" === ");
							pw.println("{{{");
							for(int w = 1; w < 9999;w++){
								try{
									ItemBean ibean = hasshy.get(w);
									ItemContents item = ibean.getItem();
									if(item.getM_shop().equals(shop+""))
										pw.println(item.getM_id()+": "+item.getM_name().replaceAll("'", "`'`") + " - $"+item.getM_price());
								}catch(Exception e){} //Not an item. 
							}
							pw.println("}}} ");
						}
						pw.flush();
						pw.close();
						
						
						//I don't remember what I did this for, but it was important and no longer needed. 
//						pw.println("<itemDatabase>");
//						pw.println("<m_items>");
//						for(int w=1;w<751;w++){
//							try{
//								ItemBean ibean = hasshy.get(w);
//								System.out.println(ibean.getBigID());
//								pw.println("<entry>");
//								pw.println("<integer>"+ibean.getBigID()+"</integer>");
//								pw.println("<item>");
//								pw.println("<m_name>"+ibean.getItem().getM_name()+"</m_name>");
//								pw.println("<m_id>"+ibean.getItem().getM_id()+"</m_id>");
//								System.out.println(ibean.getItem().getM_name());
//								pw.println("<m_attributes>");
//								for(int z = 0; z<ibean.getItem().getM_attributes().size();z++){
//									System.out.println(ibean.getItem().getM_attributes().get(z).getItemAttribute());
//									pw.println("<itemAttribute>"+ibean.getItem().getM_attributes().get(z).getItemAttribute()+"</itemAttribute>");
//								}
//								pw.println("</m_attributes>");
//								pw.println("</item>");
//								pw.println("</entry>");
//							}catch(Exception e){}
//						}
//						pw.println("</m_items>");
//						pw.println("</itemDatabase>");
//						pw.flush();
//						pw.close();
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
	}
}
