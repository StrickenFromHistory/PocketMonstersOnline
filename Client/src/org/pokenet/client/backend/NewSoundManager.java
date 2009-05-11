package org.pokenet.client.backend;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

public class NewSoundManager implements Runnable{
	private HashMap<String, String> m_fileList;
	private String m_trackName;
	private boolean m_isRunning = false, m_muted = false, m_trackChanged = false;
	private Thread m_thread;
	private Music m_music;
	
	private final String m_audioPath = "res/music/";

	/**
	 * Default Constructor
	 */
	public NewSoundManager() {
		loadFileList();
		m_thread = new Thread();
	}
	
	/**
	 * Loads the files
	 */
	private void loadFileList() {
		try {
			Scanner reader = new Scanner(new File(m_audioPath+"index.txt"));
			m_fileList = new HashMap<String, String>();

			String f = null;
			while (reader.hasNext()) {
				f = reader.nextLine();
				String[] addFile = f.split(":", 2);
				m_fileList.put(addFile[0], addFile[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to load music");
		}
		System.out.println(m_fileList.keySet().toString());
		System.out.println(m_fileList.values().toString());
		System.out.println(m_fileList.toString());
		System.out.println(m_fileList.get("opening"));
	}
	
	/**
	 * Called by m_thread.start(). Loops through all players calling PlayerChar.move() if the player requested to be moved.
	 */
	public void run() {
		while(m_isRunning) {
			try {
				if (m_trackChanged){
					System.out.println(m_fileList.keySet().toString());
					m_music = new Music(m_audioPath + m_fileList.get(m_trackName), true);
					if (m_muted)
						m_music.setVolume(0);
					else
						m_music.setVolume(1);
				}
				play();
				m_trackChanged = false;
			} catch (SlickException e){
				System.out.println("FAIL");
				m_music = null;
				m_trackChanged = false;
			}
		}
	}
	
	/**
	 * Plays the track
	 */
	public void play(){
		try {
			m_music.play();
			m_music.loop();
		} catch (NullPointerException e){}
	}
	
	/**
	 * Starts the movement thread
	 */
	public void start() {
		m_isRunning = true;
		m_thread.start();
	}
	
	/**
	 * Sets the track to play
	 * @param key
	 */
	public void setTrack(String key){
		m_trackName = key;
		m_trackChanged = true;
	}
	
	/**
	 * Mutes or unmutes the music
	 * @param mute
	 */
	public void mute(boolean mute){
		m_muted = true;
		try {
			m_music.setVolume(0);
		} catch (NullPointerException e) {}
	}
	
	/**
	 * Stops the movement thread
	 */
	public void stop() {
		m_isRunning = false;
	}
}
