package org.pokenet.client.backend;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.pokenet.client.GameClient;

public class Translator {
	
	@SuppressWarnings("deprecation")
	public static List<String> translate(String filename){
		BufferedReader reader;
		List<String> translated = new ArrayList<String>();
		String f;
		try {
			reader = FileLoader.loadTextFile("/res/language/" + GameClient.getLanguage() + "/UI/" + filename + ".txt");
			while ((f = reader.readLine()) != null) {
				translated.add(f.replaceAll("/n", "\n"));
				if(translated.size()==0){
					FileInputStream fis = new FileInputStream(f);
					BufferedInputStream bis = new BufferedInputStream(fis);
					DataInputStream dis = new DataInputStream(bis);
					while (dis.available() != 0) {
						// this statement reads the line from the file
						translated.add(dis.readLine());
					}
					fis.close();
					bis.close();
					dis.close();
				}
			}
		} catch (Exception e) { //In case of emergencies, load english!
			try{
				reader = FileLoader.loadTextFile("/res/language/english/UI/" + filename + ".txt");
				while((f = reader.readLine()) != null) {
					translated.add(f.replaceAll("/n", "\n"));
				}
			}catch(Exception e2){
				translated.add("/n"); //If there's no english, display default line. 
			}
		}
		return translated;
	}
}
