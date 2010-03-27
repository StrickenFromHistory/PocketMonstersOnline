package org.pokenet.client.backend;

import java.io.BufferedReader;
import java.util.HashMap;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioImpl;
import org.newdawn.slick.openal.AudioLoader;

/**
 * Handles music throughout the game
 * @author ZombieBear
 *
 */
public class SoundManager extends Thread{
	private HashMap<String, AudioImpl> m_files;
	private HashMap<String, String> m_fileList, m_locations;
	protected String m_trackName;
	private boolean m_tracksLoaded = false, m_trackChanged = true, m_isRunning = false;
	private boolean m_mute = false;

	private String m_audioPath = "res/music/";

	/**
	 * Default Constructor
	 */
	public SoundManager() {
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		m_audioPath = respath+m_audioPath;
		m_files = new HashMap<String, AudioImpl>();
		loadFileList();
		loadLocations();
	}
	
	/**
	 * Loads the file list
	 */
	private void loadFileList() {
		try {
			BufferedReader stream = FileLoader.loadTextFile(m_audioPath + "index.txt");
			m_fileList = new HashMap<String, String>();

			String f;
			while ((f = stream.readLine()) != null) {
				String[] addFile = f.split(":", 2);
				try{
					if (f.charAt(1) != '*'){
						m_fileList.put(addFile[0], addFile[1]);
					}
				} catch (Exception e) {System.err.println("Failed to add file: " + addFile[1]);}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to load music");
		}
	}

	/**
	 * Loads the locations and their respective keys
	 */
	private void loadLocations() {
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		try {
			BufferedReader stream = FileLoader.loadTextFile(respath+"res/language/english/_MUSICKEYS.txt");
			m_locations = new HashMap<String, String>();

			String f;
			while ((f = stream.readLine()) != null) {
				String[] addFile = f.split(":", 2);
				try{
					m_locations.put(addFile[0], addFile[1]);
				} catch (Exception e) {e.printStackTrace();}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Loads the files
	 */
	private void loadFiles() {
		Audio a;
		for (String key : m_fileList.keySet()){
			try {
				a = AudioLoader.getAudio("OGG", FileLoader.loadFile(m_audioPath + m_fileList.get(key)));
				/* For some reason it reads intro and gym wrong so do this to fix it */
				if(key.endsWith("introandgym"))
					key = "introandgym";
				m_files.put(key, (AudioImpl) a);
			} catch (Exception e) {e.printStackTrace();}
		}
		m_tracksLoaded = true;
	}
	
	/**
	 * Called by m_thread.start().
	 */
	@Override
	public void run() {
		while (m_isRunning){
			if(!m_mute) {
				while (!m_tracksLoaded) {
					loadFiles();
				}
			}
			if (m_trackChanged){
				try{
					m_trackChanged = false;
					if(!m_mute && m_trackName != null) {
						System.out.println("Playing: " + m_trackName);
						//LoadingList.setDeferredLoading(true);
						m_files.get(m_trackName).playAsMusic(1, 20, true);
						//LoadingList.setDeferredLoading(false);
					} else if (m_mute && m_trackName != null) {
						m_files.clear();
					}
				} catch (Exception e){
					e.printStackTrace();
					System.err.println("Failed to load " +  m_trackName);
					m_trackChanged = false;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Sets the track to play
	 * @param key
	 */
	public void setTrack(String key){
		if (key != m_trackName && key != null){
			m_trackName = key;
			m_trackChanged = true;
		}
	}
	
	/**
	 * Sets the track according to the player's location
	 * @param key
	 */
	public void setTrackByLocation(String track){
		if (track != null) {
			String key = track;
			System.out.println(key);
			//if (key.substring(0, 5).equalsIgnoreCase("Route"))
			if (key.contains("Route"))
				key = "Route";
			if (m_locations.get(key) != m_trackName && m_locations.get(key) != null){
				m_trackName = m_locations.get(key);
				m_trackChanged = true;
			}
		}
	}
	
	/**
	 * Starts the thread
	 */
	public void start(){
		if(!m_mute) {
			m_isRunning = true;
			super.start();
		}
	}
	
	/**
	 * Mutes or unmutes the music
	 * @param mute
	 */
	public void mute(boolean mute){
		m_mute = mute;
	}
}