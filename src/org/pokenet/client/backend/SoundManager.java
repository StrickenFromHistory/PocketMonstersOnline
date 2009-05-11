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
	private HashMap<String, String> m_fileList;
	private String m_trackName;
	private boolean m_muted, m_trackChanged = true;
	private Music m_music;
	
	private final String m_audioPath = "res/music/";

	/**
	 * Default Constructor
	 */
	public SoundManager() {
		loadFileList();
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
	}
	
	/**
	 * Called by m_thread.start(). Loops through all players calling PlayerChar.move() if the player requested to be moved.
	 */
	@Override
	public void run() {
		System.out.println("Running!");
		try {
			if (m_trackChanged){
				System.out.println("Changed track to: " + m_fileList.get(m_trackName));
				m_music = new Music(m_audioPath + m_fileList.get(m_trackName), true);
				if (m_muted)
					m_music.setVolume(0);
				else
					m_music.setVolume(1);
				play();
			}
			m_trackChanged = false;
		} catch (SlickException e){
			System.err.println("Failed to load " +  m_fileList.get(m_trackName));
			m_music = null;
			m_trackChanged = false;
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
	 * Mutes or unmutes the music
	 * @param mute
	 */
	public void mute(boolean mute){
		m_muted = mute;
		if (m_muted){
			try {
				m_music.setVolume(0);
			} catch (NullPointerException e) {
				System.err.println("Failed to mute the curent track");
			}
		}
	}
}
