/**
 * @author Juan M. Rodriguez
 * @date Mon May 25, 2009
 * @version 0.1
 */
package org.pokenet.moves;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ReadMoves {
	HashMap<String,MovesBean> movesList;

	public ReadMoves() {
		
	}
	
	public ReadMoves(HashMap<String,MovesBean> movesList) {
		this.movesList = movesList;
	}
	
	/**
	 * Usage: new ReadMoves().getMovesList();
	 * @return HashMap<String, MovesBean> movesList 
	 */
	public HashMap<String, MovesBean> getMovesList() {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File("movedex.xml"));
			doc.getDocumentElement().normalize();

			NodeList polrdbNodeList = doc.getElementsByTagName("movesDatabase");
			for (int s = 0; s < polrdbNodeList.getLength(); s++) {
				Node pokeNode = polrdbNodeList.item(s);
				if (pokeNode.getNodeType() == Node.ELEMENT_NODE) {
					for (int i = 0; i < polrdbNodeList.getLength(); i++) {
						NodeList entryNodeList = ((Element)polrdbNodeList.item(i)).getElementsByTagName("entry");
						movesList = new HashMap<String,MovesBean>();
						for (int j = 0; j < entryNodeList.getLength(); j++) {
							MovesBean ibean = new MovesBean();

							// Set Integer							
							ibean.setId(j);

							// Set Name
							NodeList nameNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("name");
							ibean.setName(nameNodeList.item(0).getChildNodes().item(0).getNodeValue());

							// Set Description
							NodeList descNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("description");
							ibean.setDescription(descNodeList.item(0).getChildNodes().item(0).getNodeValue());
							
							// Set Type
							NodeList typeNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("type");
							ibean.setType(typeNodeList.item(0).getChildNodes().item(0).getNodeValue());

							// Set Category
							NodeList catNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("category");
							ibean.setCategory(catNodeList.item(0).getChildNodes().item(0).getNodeValue());

							// Set Contest
							NodeList contNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("contest");
							ibean.setContest(contNodeList.item(0).getChildNodes().item(0).getNodeValue());

							// Set PP
							NodeList ppNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("pp");
							ibean.setPp(ppNodeList.item(0).getChildNodes().item(0).getNodeValue());

							// Set Power
							NodeList powNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("power");
							ibean.setPower(powNodeList.item(0).getChildNodes().item(0).getNodeValue());

							// Set Accuracy
							NodeList accuNodeList = ((Element)entryNodeList.item(j)).getElementsByTagName("accuracy");
							ibean.setAccuracy(accuNodeList.item(0).getChildNodes().item(0).getNodeValue());

							movesList.put(ibean.getName().replace(" ","").toLowerCase(),ibean);
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
		return movesList;
	}

	public void setMovesList(HashMap<String, MovesBean> pokeList) {
		this.movesList = pokeList;
	}
	
	/**
	 * Usage: new ReadMoves(HashMap<String, MovesBean> movesList ).getMove(moveName);
	 * It can fetch moves regardless of Spaces or Case Usage. 
	 * @return MovesBean move
	 */
	public MovesBean getMoves(String moveName) {
		MovesBean move = new MovesBean();
		try{
			move = movesList.get(moveName.replaceAll(" ","").toLowerCase());
			if(move.getName().replaceAll(" ","").toLowerCase().equals(moveName.replaceAll(" ","").toLowerCase())){
				return move;
			}
		}catch(Exception e){
		} //Not a bug. 
		return move;
	}
	
	/**
	 * Generates the Movedex.xml. Requires changing the Key on the Hashmap from movename to id. 
	 */
	public void generateMoveXML(){
		try{
			File f = new File("movedex.xml");
			if(f.exists())
				f.delete();
			PrintWriter pw = new PrintWriter(f);
			pw.println("<movesDatabase>");
			for(int j = 0;j<movesList.size();j++){
				pw.println("	<entry>");
				pw.println("		<id>"+movesList.get(j).getId()+"</id>");
				pw.println("		<name>"+movesList.get(j).getName()+"</name>");
				pw.println("		<description>"+movesList.get(j).getDescription()+"</description>");
				pw.println("		<type>"+movesList.get(j).getType()+"</type>");
				pw.println("		<category>"+movesList.get(j).getCategory()+"</category>");
				pw.println("		<contest>"+movesList.get(j).getContest()+"</contest>");
				pw.println("		<pp>"+movesList.get(j).getPp()+"</pp>");
				pw.println("		<power>"+movesList.get(j).getPower()+"</power>");
				pw.println("		<accuracy>"+movesList.get(j).getAccuracy()+"</accuracy>");
				pw.println("	</entry>");
			}
			pw.println("</movesDatabase>");
			pw.flush();
			pw.close();
		}catch(Exception e){}
	}
}
