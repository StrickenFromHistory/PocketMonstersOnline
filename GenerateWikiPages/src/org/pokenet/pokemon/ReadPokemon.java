/**
 * @author Juan M. Rodriguez
 * @date Mon May 25, 2009
 * @version 0.1
 */
package org.pokenet.pokemon;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ReadPokemon {
	HashMap<Integer,PokemonBean> pokeList;
	
	public ReadPokemon() {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("polrdb.xml"));
			doc.getDocumentElement().normalize();
			
			NodeList polrdbNodeList = doc.getElementsByTagName("POLRDatabase");
			for (int s = 0; s < polrdbNodeList.getLength(); s++) {
				Node pokeNode = polrdbNodeList.item(s);
				if (pokeNode.getNodeType() == Node.ELEMENT_NODE) {
					Element pokeDBElement = (Element) pokeNode;
					NodeList m_pokemonDataNodeList = pokeDBElement.getElementsByTagName("m_pokemonData");
					for (int i = 0; i < m_pokemonDataNodeList.getLength(); i++) {
						NodeList entryNodeList = ((Element)m_pokemonDataNodeList.item(i)).getElementsByTagName("POLRDataEntry");
						pokeList = new HashMap<Integer,PokemonBean>();
						for (int j = 0; j < entryNodeList.getLength(); j++) {
							PokemonBean ibean = new PokemonBean();

							// Set Integer							
							ibean.setId(j);

							// Set Name
							NodeList nameNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("name");
							ibean.setName(nameNodeList.item(0).getChildNodes().item(0).getNodeValue());
							
							// Set Kind
							NodeList kindNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("kind");
							ibean.setKind(kindNodeList.item(0).getChildNodes().item(0).getNodeValue());
							
							// Set Kind
							NodeList dexNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("pokedex");
							ibean.setDex(dexNodeList.item(0).getChildNodes().item(0).getNodeValue());

							// Set Type 1
							NodeList type1NodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("type1");
							ibean.setType1(type1NodeList.item(0).getChildNodes().item(0).getNodeValue());
							
							// Set Type 2
							NodeList type2NodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("type2");
							try{
								ibean.setType2(type2NodeList.item(0).getChildNodes().item(0).getNodeValue());
							}catch(Exception e){
								ibean.setType2("null");
							}
							
							// Set Rareness
							NodeList rarenessNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("rareness");
							ibean.setRareness(rarenessNodeList.item(0).getChildNodes().item(0).getNodeValue());
//							//m_attributes
//							NodeList m_attributes = readItemElement.getElementsByTagName("m_attributes");
//							Element attributesElement = (Element) m_attributes.item(0);
//							NodeList m_attributesList = attributesElement.getElementsByTagName("itemAttribute");
//							ArrayList<Attributes> attrList = new ArrayList<Attributes>();
//							for (int l = 0; l < m_attributesList.getLength(); l++) {
//								Attributes attr = new Attributes();
//								attr.setItemAttribute(m_attributesList.item(l).getChildNodes().item(0).getNodeValue());
//								
//								attrList.add(attr);
//							}
//							icontents.setM_attributes(attrList);
							pokeList.put(j,ibean);
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
	}

	public HashMap<Integer, PokemonBean> getPokeList() {
		return pokeList;
	}

	public void setPokeList(HashMap<Integer, PokemonBean> pokeList) {
		this.pokeList = pokeList;
	}
}
