package org.pokenet.client.ui.frames;

import org.newdawn.slick.gui.GUIContext;

public class BattleSpeechFrame extends SpeechFrame {
    //private BattleWindow m_battle;
   
    public void addSpeech(String speech) {
            if (stringToPrint != null && (stringToPrint.equals("Awaiting your move.") || stringToPrint.equals("Awaiting players' moves."))
                            && speechQueue.peek() == null)
                    triangulate();
            speechQueue.add(speech);
            if (stringToPrint == null || stringToPrint.equals(""))
                    advance();
    }
    public BattleSpeechFrame(/*BattleWindow battleWindow*/) {
            super("");
            //m_battle = battleWindow;
    }
    @Override
    public void advancing(String toPrint) {
            /*if (toPrint.equals("Awaiting your move.") || toPrint.equals("Awaiting players' moves."))
                    m_battle.setUIToMove();
            else if (toPrint.contains(" wants to learn ")) {
                    int indexOfWants = toPrint.indexOf(" wants to learn ");
                    String afterWants = toPrint.substring(indexOfWants
                                    + " wants to learn ".length());
                    String moveName = afterWants.substring(0, afterWants.indexOf("."));
                    m_battle.setUIToLearn(moveName);
            }
            else if (toPrint.contains("Awaiting opponent's Pokemon switch.")) {
                    m_battle.disableMoves();
            }
            else if(toPrint.contains("has disconnected.")) {
                    m_battle.disableMoves();
            }*/
    }
    @Override
    public boolean canAdvance() {
            if (speechQueue.peek() == null &&
                            stringToPrint != null && (stringToPrint.equals("Awaiting your move.") || stringToPrint.equals("Awaiting players' moves.") || stringToPrint.equals("Awaiting opponent's Pokemon switch."))) {
                    return false;
            } //else if (m_battle != null && m_battle.isLearningMove()) return false;
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
            /*if ((speechQueue.peek() == null &&
                            !printed.equals("Awaiting your move.") &&
                            !printed.equals("Awaiting players' moves.") &&
                            !printed.equals("Awaiting opponent's Pokemon switch.") &&
                            !printed.startsWith("Sorry, the move ")  &&
                            !m_battle.isMoving() &&
                            !m_battle.isLearningMove() &&  
                            (!m_battle.enemyHasMorePokes()) ||
                             printed.contains(" won the battle.") ||
                             printed.equals("Got away safely!") ||
                             printed.endsWith(" was caught successfully!"))) {
                    m_battle.setBattleSpeech(null);
                    m_battle.setVisible(false);
                    triangle = null;
                    setVisible(false);
                    GlobalGame.getSoundPlayer().stopChannel("battleMusic");
            GlobalGame.getSoundPlayer().playChannel("route30", "routeMusic", true);
            }
*/    }
}

