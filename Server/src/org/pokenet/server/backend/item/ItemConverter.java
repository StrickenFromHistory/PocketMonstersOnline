package org.pokenet.server.backend.item;

import java.io.File;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.pokenet.server.backend.item.Item.ItemAttribute;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Converts items.txt into an xml
 * @author shadowkanji
 *
 */
public class ItemConverter {
	public static void main(String [] args) {
//		try {
//			ItemDatabase database = new ItemDatabase();
//			String nextLine = "";
//			String next = "";
//			String name = "";
//			int id = 0;
//			Item i;
//			Scanner s = new Scanner(new File("./res/items.txt"));
//			while(s.hasNextLine()) {
//				nextLine = s.nextLine();
//				nextLine = nextLine.trim();
//				StringTokenizer st = new StringTokenizer(nextLine);
//				i = new Item();
//				/*
//				 * Find the id and the name
//				 */
//				while(st.hasMoreTokens()) {
//					next = st.nextToken();
//					try {
//						id = Integer.parseInt(next);
//					} catch (Exception ex) {
//						name = name + next + " ";
//					}
//				}
//				i.setId(id);
//				i.setName(name.trim());
//				
//				/*
//				 * Set the items attributes
//				 */
//				if(id <= 34) {
//					i.addAttribute(ItemAttribute.POKEMON);
//				} else if(id >= 35 && id <= 69) {
//					i.addAttribute(ItemAttribute.BATTLE);
//				} else if(id >= 70 && id <= 90) {
//					i.addAttribute(ItemAttribute.POKEMON);
//				} else if(id >=  91 && id <= 125) {
//					i.addAttribute(ItemAttribute.FIELD);
//				} else if(id >= 126 && id <= 149) {
//					i.addAttribute(ItemAttribute.CRAFT);
//				} else if(id >= 150 && id <= 349) {
//					i.addAttribute(ItemAttribute.POKEMON);
//					if(id >= 300 && id <= 349) {
//						i.addAttribute(ItemAttribute.HOLD);
//					}
//				} else if(id >= 350 && id <= 399) {
//					i.addAttribute(ItemAttribute.BATTLE);
//				} else if(id >= 400 && id <= 474) {
//					i.addAttribute(ItemAttribute.POKEMON);
//				} else if(id >= 475 && id <= 499) {
//					i.addAttribute(ItemAttribute.POKEMON);
//					i.addAttribute(ItemAttribute.HOLD);
//				} else if(id >= 500 && id <= 524) {
//					i.addAttribute(ItemAttribute.POKEMON);
//				} else if(id >= 525 && id <= 549) {
//					i.addAttribute(ItemAttribute.FIELD);
//				} else if(id >= 550 && id <= 575) {
//					i.addAttribute(ItemAttribute.OTHER);
//				} else if(id >= 576 && id <= 749) {
//					i.addAttribute(ItemAttribute.POKEMON);
//					i.addAttribute(ItemAttribute.MOVESLOT);
//				} else if(id >= 750) {
//					i.addAttribute(ItemAttribute.OTHER);
//				}
//				database.addItem(id, i);
//				
//				name = "";
//				id = 0;
//			}
//			File f = new File("items.xml");
//			Serializer serializer = new Persister();
//			serializer.write(database, f);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
