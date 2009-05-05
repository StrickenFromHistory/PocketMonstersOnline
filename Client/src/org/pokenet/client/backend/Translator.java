package org.pokenet.client.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.pokenet.client.GameClient;

public class Translator {
	
	public static List<String> translate(String filename){
		File f = new File(".");
		List<String> translated = new ArrayList<String>();
		try {
			f = new File(f.getCanonicalPath() + "/res/language/" + GameClient.getLanguage() + "/" + filename + ".txt");
			if(f.exists()) {
				Scanner reader = new Scanner(f);
				while(reader.hasNextLine()) {
					translated.add(reader.nextLine().replaceAll("/n", "\n"));
				}
			}else{ //In case of emergencies, load english!
				try{
					f = new File(".");
					f = new File(f.getCanonicalFile()+ "/res/language/english/" + filename + ".txt");
					Scanner reader = new Scanner(f);
					while(reader.hasNextLine()) {
						translated.add(reader.nextLine().replaceAll("/n", "\n"));
					}
				}catch(Exception e){
					translated.add("/n"); //If there's no english, display default line. 
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translated;
	}
}
