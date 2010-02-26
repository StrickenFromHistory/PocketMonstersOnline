package org.pokenet.client.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.newdawn.slick.muffin.FileMuffin;
import org.newdawn.slick.muffin.Muffin;

/**
 * This is class is to build a bridge between certain information
 * and the users hard drive. Inspired by the plist system on Mac
 * OS X (the whole ~/Library/Preferences/ thing)
 * 
 *  the prefs will be stored here:
 *  for Mac OS X:
 *  	~/Library/Preferences/org.pokedev.pokePrefs
 *  for Windows:
 *  	<username>\My Documents\Game Preferences\org.pokedev.pokePrefs
 *  for Linux:
 *  	~/Game Preferences/org.pokedev.pokePrefs
 *  
 * @author lprestonsegoiii
 *
 */
public class PreferencesManager {
	private static PreferencesManager m_instance = null;
	private HashMap<String, Object> m_prefs = new HashMap<String, Object>();
	private static final String FILE_NAME = "org.pokedev.pokePrefs";
	private static final String MAC_NAME = "Mac OS X";
	private static final String WIN_NAME = "Windows";
	
	public final String USER_KEY_NAME = "userName";
	public final String PASS_KEY_NAME = "password";
	public final String PRIVATE_SERVERS_KEY_NAME = "recentPrivateServers";
	
	// booleans
	public final String SOUND_MUTED_KEY_NAME = "soundMuted";
	public final String DISABLE_MAPS_KEY_NAME = "disableMaps";
	public final String DISABLE_WEATHER_KEY_NAME = "disableWeather";
	public final String FULLSCREEN_KEY_NAME = "fullscreen";
	public final String REMEMBER_ME_KEY_NAME = "saveLogin";

	
	private String m_prefsPath = getPathForOS();
	
	
	private Muffin m_muffin = new FileMuffin();
	
	/**
	 * Constructor
	 */
	private PreferencesManager(){
		// initialize
//		readInPreferencesFromFile();
		readInPreferencesFromFile();
	}
	
	/**
	 * Guarantees that there is only one PreferencesManager
	 * @return
	 */
	public static PreferencesManager getPreferencesManager(){
		if (null == m_instance){
			m_instance = new PreferencesManager();
		}
		
		return m_instance;
	}
	
	private String getPathForOS(){
		Properties prop = System.getProperties();
		String osString = prop.getProperty( "os.name" );
		if (osString.equals(MAC_NAME)){
			return System.getProperty("user.home") + "/Library/Preferences/";
		}else if (osString.equals(WIN_NAME)){
			return "";
		}else {
			// same directory as the application
			return "";
		}
	}
	
//	options = new FileMuffin().loadFile("options.dat");
	@SuppressWarnings("unchecked")
	private void readInPreferencesFromFile() {
		try {
			FileInputStream fin = new FileInputStream(this.m_prefsPath + FILE_NAME);
		    System.out.println("Opening: " + this.m_prefsPath + FILE_NAME);
		    
			ObjectInputStream ois = new ObjectInputStream(fin);
		    this.m_prefs = (HashMap<String, Object>) ois.readObject();
		    ois.close();
		}catch (FileNotFoundException fnfe) {
			System.out.println("Creating Preferences File at: " + this.m_prefsPath + FILE_NAME);
			
			File f = new File(this.m_prefsPath + FILE_NAME);
			
		    if(!f.exists()){
				 
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
		      }
		    
		    writeDefaults();
		   
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void load() {
//		readInPreferencesFromFile();
		try {
			m_prefs = m_muffin.loadFile(FILE_NAME);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void save() {
//		savePreferences();
		try {
			m_muffin.saveFile(m_prefs, FILE_NAME);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeDefaults() {
		
		m_prefs = new HashMap<String, Object>();
		System.out.println("Writing defaults");
		//initialize saved things
	    setObjectForKey(false, SOUND_MUTED_KEY_NAME);
	    setObjectForKey(false, DISABLE_MAPS_KEY_NAME);
	    setObjectForKey(false, DISABLE_WEATHER_KEY_NAME);
	    setObjectForKey(false, FULLSCREEN_KEY_NAME);
	    setObjectForKey(false, REMEMBER_ME_KEY_NAME);
	}
	
	/**
	 * serialize the preferences to a file.
	 * location is based on the OS
	 */
	public void savePreferences() {
		
		try {
		      FileOutputStream fout = new FileOutputStream(this.m_prefsPath + FILE_NAME);
		      System.out.println("Saving: " + this.m_prefsPath + FILE_NAME);
		      ObjectOutputStream oos = new ObjectOutputStream(fout);
		      oos.writeObject(m_prefs);
		      oos.close();
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
	}
	
	public Object getObjectForKey(String key) {
		return m_prefs.get(key);
	}
	
	public String getStringForKey(String key) {
		return (String)m_prefs.get(key);
	}
	
	public int getIntForKey(String key) {
		return ((Integer)m_prefs.get(key)).intValue();
	}
	
	public boolean getBoolForKey(String key) {
		return ((Boolean)m_prefs.get(key)).booleanValue();
	}

	public void setObjectForKey(Object value, String key) {
		this.m_prefs.put(key, value);
		savePreferences();
		save();
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getStringArrayListForKey(String key) {
		return (ArrayList<String>)this.m_prefs.get(key);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Integer> getIntegerArrayListForKey(String key) {
		return (ArrayList<Integer>)this.m_prefs.get(key);
	}

	public String[] getStringArrayForKey(String key) {
		return (String[])this.m_prefs.get(key);
	}

	public void reload() {
		this.m_prefs = null;
		readInPreferencesFromFile();
	}
	
}
