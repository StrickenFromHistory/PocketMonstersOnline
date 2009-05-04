package org.pokenet.client.ui.frames;

import org.newdawn.slick.gui.GUIContext;

public class BattleSpeechFrame extends SpeechFrame {
	private String advancedLine;
   
    public void addSpeech(String speech) {
            if (stringToPrint != null && (stringToPrint.equals("Awaiting your move.") || stringToPrint.equals("Awaiting players' moves."))
                            && speechQueue.peek() == null)
                    triangulate();
            speechQueue.add(speech);
            if (stringToPrint == null || stringToPrint.equals(""))
                    advance();
    }
    public BattleSpeechFrame() {
            super("");
    }
    @Override
    public void advancing(String toPrint) {
    }

    @Override
    public boolean canAdvance() {
            if (speechQueue.peek() == null &&
                            stringToPrint != null && (stringToPrint.equals("Awaiting your move.") || stringToPrint.equals("Awaiting players' moves.") || stringToPrint.equals("Awaiting opponent's Pokemon switch."))) {
                    return false;
            } 
            else return true;
    }

    @Override
    public void update(GUIContext ctx, int delta) {
            super.update(ctx, delta);
            if (speechDisplay.getText().equals("")
                            && speechQueue.peek() != null &&
                            (speechQueue.peek().equals("Awaiting your move.") || speechQueue.peek().equals("Awaiting players' moves.") || speechQueue.peek().equals("Awaiting opponent's Pokemon switch.")))
                    advance();
    }

    @Override
    public void advancedPast(String printed) {
    	advancedLine = printed;
    }
    
    public String getAdvancedLine(){
    	return advancedLine;
    }
    
    public String getCurrentLine(){
    	return stringToPrint;
    }
}

