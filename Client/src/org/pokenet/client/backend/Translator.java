package org.pokenet.client.backend;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.pokenet.client.GameClient;

public class Translator {
	private static Translator m_instance;
	
	/**
	 * Returns a list of translated text
	 * @param filename
	 * @return
	 */
	public List<String> translateText(String filename) {
		List<String> translated = new ArrayList<String>();
		try {
			//First, add English to translated. 
			try{
				InputStream in = getClass().getResourceAsStream("/res/language/english/UI/" + filename + ".txt");
				BufferedReader f = new BufferedReader(new InputStreamReader(in));
				Scanner reader = new Scanner(f);
				while(reader.hasNextLine()) {
					translated.add(reader.nextLine().replaceAll("/n", "\n"));
				}
			}catch(Exception e){
				translated.add("/n"); //If there's no english, display default line. 
			}
			//Now, overwrite English with the translation
			String path = "/res/language/" + GameClient.getLanguage() + "/UI/" + filename + ".txt";
			InputStream in = getClass().getResourceAsStream(path);
			int index = 0;
			if(in != null) {
				BufferedReader f = new BufferedReader(new InputStreamReader(in));
				Scanner reader = new Scanner(f);
				while(reader.hasNextLine()) {
					translated.remove(index);
					translated.add(index,reader.nextLine().replaceAll("/n", "\n"));
					index++;
				}
				/*if(translated.size()==0){
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
				}*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translated;
	}
	
	/**
	 * Returns the instance of translator
	 * @return
	 */
	public static Translator getInstance() {
		if(m_instance == null)
			m_instance = new Translator();
		return m_instance;
	}
	
	public static List<String> translate(String filename){
		return Translator.getInstance().translateText(filename);
	}
}
