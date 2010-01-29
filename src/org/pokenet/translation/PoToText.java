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
				if(file.isFile()&&!file.getAbsolutePath().contains(".svn")){
					String lang = "";
					if(file.getAbsolutePath().contains("res/po/en-US"))
						lang = "english";
					else if (file.getAbsolutePath().contains("res/po/es"))
						lang = "spanish";
					else if (file.getAbsolutePath().contains("res/po/pt"))
						lang = "portuguese";
					else if (file.getAbsolutePath().contains("res/po/fr"))
						lang = "french";
					else if (file.getAbsolutePath().contains("res/po/it"))
						lang = "italian";
					else if (file.getAbsolutePath().contains("res/po/nl"))
						lang = "dutch";
					else if (file.getAbsolutePath().contains("res/po/fi"))
						lang = "finnish";
					else if (file.getAbsolutePath().contains("res/po/de"))
						lang = "german";
					Translation trans = new Translation(lang);
					BufferedReader input =  new BufferedReader(new FileReader(file));
					try {
						String line = null; //not declared within while loop
						while (( line = input.readLine()) != null){
							if(!line.startsWith("#"))
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
					File folder = new File("res/txt/"+translations.get(i).getLanguage());
					if(!folder.exists())
						folder.mkdir();
					folder = new File("res/txt/"+translations.get(i).getLanguage()+"/UI");
					if(!folder.exists())
						folder.mkdir();
					folder = new File("res/txt/"+translations.get(i).getLanguage()+"/NPC");
					if(!folder.exists())
						folder.mkdir();
					File f;
					if(filename.contains("_MAPNAMES")||filename.contains("_MUSICKEYS"))
						f = new File("res/txt/"+translations.get(i).getLanguage()+"/"+filename.replace(".po",".txt"));
					else if(filename.contains("_BATTLE")||filename.contains("_GUI")||filename.contains("_LOGIN")||filename.contains("_MAP"))
						f = new File("res/txt/"+translations.get(i).getLanguage()+"/UI/"+filename.replace(".po",".txt"));
					else
						f = new File("res/txt/"+translations.get(i).getLanguage()+"/NPC/"+filename.replace(".po",".txt"));
					if(f.exists())
						f.delete();
					PrintWriter pw = new PrintWriter(f);
					for(int j=0;j<translations.get(i).getLines().size();j++){
						try{
							if(translations.get(i).getLines().get(j).startsWith("msgstr \""))
								pw.println(""+translations.get(i).getLines().get(j).replace("msgstr \"","").replace("\"",""));
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