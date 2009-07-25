package org.pokenet.client.ui.frames;

import org.pokenet.client.GameClient;

/**
 * NPC speech pop-up
 * @author ZombieBear
 *
 */
public class NPCSpeechFrame extends SpeechFrame {
	/**
	 * Default Constructor
	 * @param text
	 */
    public NPCSpeechFrame(String text) {
            super(text);
    }
    
    /**
	 * Modified constructor, sets time to auto-skip to the next line. 
	 * @param text
	 * @param seconds
	 */
    public NPCSpeechFrame(String text,int seconds) {
        super(text,seconds);
    }
    /**
     * Sends a packet when finished displaying text
     */
    public void advancedPast(String advancedMe) {
            if (speechQueue.peek() == null) {
                    triangle = null;
                    setVisible(false);
                    GameClient.getInstance().getUi().nullSpeechFrame();
                    GameClient.getInstance().getPacketGenerator().writeTcpMessage("Cf");
            }
    }
}
