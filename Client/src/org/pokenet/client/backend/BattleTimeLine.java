package org.pokenet.client.backend;

import org.pokenet.client.GameClient;
import org.pokenet.client.ui.BattleCanvas;
import org.pokenet.client.ui.frames.BattleSpeechFrame;

/**
 * Handles Battle Events and arranges them for visual purposes.
 * @author ZombieBear
 *
 */
public class BattleTimeLine {
	private BattleSpeechFrame m_narrator;
	private BattleCanvas m_canvas;
	
	/**
	 * Default constructor
	 */
	public BattleTimeLine(){
		initCanvas();
	}
	
	public void initCanvas(){
		m_canvas = new BattleCanvas();
		GameClient.getInstance().getDisplay().add(m_canvas);
	}
	

}
