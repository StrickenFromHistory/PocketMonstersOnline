package org.pokenet.client.backend;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.pokenet.client.GameClient;

public class Translator {
	
	public static List<String> translate(String filename){
		File f = new File(".");
		List<String> translated = new ArrayList<String>();
		try {
			f = new File(f.getCanonicalPath() + "/res/language/" + GameClient.getLanguage() + "/UI/" + filename + ".txt");
			if(f.exists()) {
				Scanner reader = new Scanner(f);
				while(reader.hasNextLine()) {
					translated.add(reader.nextLine().replaceAll("/n", "\n"));
				}
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
			}else{ //In case of emergencies, load english!
				try{
					f = new File(".");
					f = new File(f.getCanonicalFile()+ "/res/language/english/UI/" + filename + ".txt");
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
