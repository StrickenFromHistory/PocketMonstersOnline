package org.pokenet.translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.pokenet.translation.utils.FileListing;

public class PoToText {

	public static void main(String[] args){
		try {
			List<File> files = FileListing.getFiles("res/po/");
			HashMap<String,ArrayList<Translation>> fileGroup = new HashMap<String,ArrayList<Translation>>();
			for(File file : files ){
				if(file.isFile()){
					String lang = "";
					if(file.getAbsolutePath().contains("res/po/en"))
						lang = "en-US";
					else if (file.getAbsolutePath().contains("res/txt/es"))
						lang = "es";
					else if (file.getAbsolutePath().contains("res/txt/pt"))
						lang = "pt";
					else if (file.getAbsolutePath().contains("res/txt/fr"))
						lang = "fr";
					else if (file.getAbsolutePath().contains("res/txt/it"))
						lang = "it";
					else if (file.getAbsolutePath().contains("res/txt/nl"))
						lang = "nl";
					else if (file.getAbsolutePath().contains("res/txt/fi"))
						lang = "fi";
					else if (file.getAbsolutePath().contains("res/txt/de"))
						lang = "de";
					Translation trans = new Translation(lang);
					BufferedReader input =  new BufferedReader(new FileReader(file));
					try {
						String line = null; //not declared within while loop
						/*
						 * readLine is a bit quirky :
						 * it returns the content of a line MINUS the newline.
						 * it returns null only for the END of the stream.
						 * it returns an empty String if two newlines appear in a row.
						 */
						while (( line = input.readLine()) != null){
							trans.addLine(line);
						}
					}finally {
						input.close();
					}
					ArrayList<Translation> allTrans = new ArrayList<Translation>();
					try{
						allTrans = fileGroup.get(file.getName());
						if(allTrans!=null)
							allTrans.add(trans);
						else{
							allTrans = new ArrayList<Translation>();
							allTrans.add(trans);
						}
					}catch(Exception e){
						allTrans.add(trans);
					}//Nothing translated so far.
					fileGroup.put(file.getName(), allTrans);
				}
			}
			Collection<String> collection = fileGroup.keySet();
			Iterator<String> itr = collection.iterator(); 
			while(itr.hasNext()){
				String filename = itr.next();
				System.out.println("File: "+filename);
				ArrayList<Translation> translations = fileGroup.get(filename);
				for(int i = 0;i<translations.size();i++){
					File folder = new File("res/po/"+translations.get(i).getLanguage());
					if(!folder.exists())
						folder.mkdir();
					folder = new File("res/po/"+translations.get(i).getLanguage()+"/UI");
					if(!folder.exists())
						folder.mkdir();
					folder = new File("res/po/"+translations.get(i).getLanguage()+"/NPC");
					if(!folder.exists())
						folder.mkdir();
					File f;
					if(filename.contains("_BATTLE")||filename.contains("_GUI")||filename.contains("_LOGIN")||filename.contains("_MAP"))
						f = new File("res/po/"+translations.get(i).getLanguage()+"/UI/"+filename.replace(".txt",".po"));
					else
						f = new File("res/po/"+translations.get(i).getLanguage()+"/NPC/"+filename.replace(".txt",".po"));
					if(f.exists())
						f.delete();
					PrintWriter pw = new PrintWriter(f);
					pw.println("# Pokenet "+filename);
					pw.println("# Copyright (C) 2010 PokeDev Developer Group, Inc");
					pw.println("# Nushio <nushio@pokedev.org>, 2010.");
					pw.println("# ");
					for(int j=0;j<translations.get(i).getLines().size();j++){
						try{
							pw.println("msgid \""+translations.get(0).getLines().get(j)+"\"");
							pw.println("msgstr \""+translations.get(i).getLines().get(j)+"\"");
							pw.println("");
						}catch(Exception e){}
					}
					pw.flush();
					pw.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}