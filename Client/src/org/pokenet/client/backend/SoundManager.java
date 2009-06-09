package org.pokenet.client.backend;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

/**
 * Handles music throughout the game
 * @author ZombieBear
 *
 */
public class SoundManager extends Thread{
	private HashMap<String, Music> m_files;
	private HashMap<String, String> m_fileList;
	protected String m_trackName;
	private boolean m_muted = false, m_tracksLoaded = false, m_trackChanged = true, m_isRunning = false;

	private final String m_audioPath = "res/music/";

	/**
	 * Default Constructor
	 */
	public SoundManager() {
		m_files = new HashMap<String, Music>();
		loadFileList();
	}
	
	/**
	 * Loads the file list
	 */
	private void loadFileList() {
		try {
			Scanner reader = new Scanner(new File(m_audioPath+"index.txt"));
			m_fileList = new HashMap<String, String>();

			String f = null;
			while (reader.hasNext()) {
				f = reader.nextLine();
				if (f.charAt(1) != '*'){
					String[] addFile = f.split(":", 2);
					m_fileList.put(addFile[0], addFile[1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to load music");
		}
	}
	
	/**
	 * Loads the files
	 */
	private void loadFiles() {
		for (String key : m_fileList.keySet()){
			try {
				m_files.put(key, new Music(m_audioPath + m_fileList.get(key)));
				System.out.println("Loaded " + m_fileList.get(key));
			} catch (Exception e) {e.printStackTrace();}
		}
		m_tracksLoaded = true;
	}
	
	/**
	 * Called by m_thread.start(). Loops through all players calling PlayerChar.move() if the player requested to be moved.
	 */
	@Override
	public void run() {
		while (m_isRunning){
			while (!m_tracksLoaded) {
				loadFiles();
			}
			if (m_trackChanged){
				try{
					m_trackChanged = false;
					System.out.println("Playing: " + m_fileList.get(m_trackName));
					m_files.get(m_trackName).loop();
				} catch (Exception e){
					System.err.println("Failed to load " +  m_fileList.get(m_trackName));
					m_trackChanged = false;
				}
			}
		}
	}
	
	/**
	 * Sets the track to play
	 * @param key
	 */
	public void setTrack(String key){
		if (key != m_trackName){
			m_trackName = key;
			m_trackChanged = true;
		}
	}
	
	/**
	 * Starts the thread
	 */
	public void start(){
		m_isRunning = true;
		super.start();
	}
	
	/**
	 * Mutes or unmutes the music
	 * @param mute
	 */
	public void mute(boolean mute){
		m_muted = false;
		try {
			if (mute){
				for (String key : m_files.keySet()){
					m_files.get(key).setVolume(0);
				}
			} else {
				for (String key : m_files.keySet()){
					m_files.get(key).setVolume(1);
				}
			}
		} catch (Exception e) {}
	}
}
