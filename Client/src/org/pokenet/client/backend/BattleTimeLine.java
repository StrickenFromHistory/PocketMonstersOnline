
package org.pokenet.client.backend;

import java.util.ArrayList;
import java.util.List;

import org.pokenet.client.GameClient;
import org.pokenet.client.ui.BattleCanvas;
import org.pokenet.client.ui.frames.BattleSpeechFrame;

/**
 * Handles Battle Events and arranges them for visual purposes.
 * 
 * @author ZombieBear
 */
public class BattleTimeLine {
  private final BattleSpeechFrame m_narrator;
  private BattleCanvas            m_canvas;
  List<String>                    m_translator = new ArrayList<String>();
  // Lines for REGEX needed for l10n
  String                          m_pokeName, m_move, m_trainer, m_foundItem;
  int                             m_newHPValue, m_exp, m_dmg, m_earnings,
      m_level, m_expRemaining;
  private boolean                 m_isBattling;

  /**
   * Default constructor
   */
  public BattleTimeLine() {
    m_translator = Translator.translate("_BATTLE");
    try {
      m_canvas = new BattleCanvas();
    } catch (Exception e) {
      e.printStackTrace();
    }
    m_narrator = new BattleSpeechFrame();
  }

  /**
   * Starts the TimeLine's components
   */
  public void startBattle() {
    m_canvas.startBattle();
    m_isBattling = true;
    GameClient.getInstance().getDisplay().add(m_canvas);
    GameClient.getInstance().getDisplay().add(m_narrator);
    GameClient.getInstance().getUi().nullSpeechFrame();
  }

  /**
   * Informs a pokemon fainted
   * 
   * @param poke
   */
  public void informFaintedPoke(String poke) {
    m_pokeName = poke;
    for (int i = 0; i < GameClient.getInstance().getOurPlayer().getPokemon().length; i++) {
      int counter = 0;
      if (GameClient.getInstance().getOurPlayer().getPokemon()[i] != null &&
    		  GameClient.getInstance().getOurPlayer().getPokemon()[i].getCurHP() <= 0) {
        counter++;
      }
      if (counter < i) {
        BattleManager.getInstance().getBattleWindow().showPokePane(true);
        addSpeech(m_translator.get(0));
        break;
      }
    }
  }

  /**
   * Informs a move was used
   * 
   * @param data
   */
  public void informMoveUsed(String[] data) {
    m_pokeName = data[0];
    m_move = data[1];
    addSpeech(m_translator.get(1));
  }

  /**
   * Informs that a move is requested
   */
  public void informMoveRequested() {
    BattleManager.getInstance().requestMoves();
    addSpeech(m_translator.get(2));
  }

  /**
   * Informs that a pokemon gained experience
   * 
   * @param data
   */
  public void informExperienceGained(String[] data) {
    m_pokeName = data[0];
    m_exp = (int) Double.parseDouble(data[1]);
    m_expRemaining = (int) Double.parseDouble(data[2]);
    addSpeech(m_translator.get(3));
  }

  /**
   * Informs that a pokemon's status was changed
   * 
   * @param data
   */
  public void informStatusChanged(int trainer, String[] data) {
    m_pokeName = data[0];
    m_canvas.setStatus(trainer, data[1]);
    if (data[1].equalsIgnoreCase("poison")) {
      addSpeech(m_translator.get(14));
    } else if (data[1].equalsIgnoreCase("freeze")) {
      addSpeech(m_translator.get(15));
    } else if (data[1].equalsIgnoreCase("burn")) {
      addSpeech(m_translator.get(16));
    } else if (data[1].equalsIgnoreCase("paralysis")) {
      addSpeech(m_translator.get(17));
    } else if (data[1].equalsIgnoreCase("sleep")) {
      addSpeech(m_translator.get(18));
    }
    if (trainer == 1)
      m_canvas.setPokeballImage(BattleManager.getInstance().getCurEnemyIndex(),
        "status");
  }

  /**
   * Informs that a pokemon's status was returned to normal
   * 
   * @param data
   */
  public void informStatusHealed(int trainer, String[] data) {
    m_pokeName = data[0];
    m_canvas.setStatus(trainer, "normal");
    addSpeech(m_translator.get(4));
  }

  /**
   * Informs that a pokemon was switched out.
   * 
   * @param data
   */
  public void informSwitch(String[] data) {
    m_trainer = data[0];
    m_pokeName = data[1];
    BattleManager.getInstance().switchPoke(Integer.parseInt(data[2]),
      Integer.parseInt(data[3]));
    m_canvas.drawOurPoke();
    m_canvas.drawOurInfo();
    m_canvas.drawEnemyPoke();
    m_canvas.drawEnemyInfo();
    addSpeech(m_translator.get(5));
  }

  /**
   * Informs that a pokemon switch is required
   */
  public void informSwitchRequested() {
    BattleManager.getInstance().getBattleWindow().showPokePane(true);
    addSpeech(m_translator.get(6));
  }

  public void informNoPP(String move) {
    m_move = move;
    BattleManager.getInstance().requestMoves();
    addSpeech(m_translator.get(21));
  }

  /**
   * Informs a change in health
   * 
   * @param data
   * @param i
   */
  public void informHealthChanged(String[] data, int i) {
    m_pokeName = data[0];
    m_dmg = Math.abs(Integer.parseInt(data[1]));
    if (i == 0) {
      m_pokeName = BattleManager.getInstance().getCurPoke().getName();
      m_newHPValue = BattleManager.getInstance().getCurPoke().getCurHP()
        + Integer.parseInt(data[1]);
      if (m_newHPValue < 0) {
        m_newHPValue = 0;
      } else if(m_newHPValue > BattleManager.getInstance().getCurPoke().getMaxHP())
      {
    	  m_newHPValue = BattleManager.getInstance().getCurPoke().getMaxHP();
      }
      BattleManager.getInstance().getCurPoke().setCurHP(m_newHPValue);
      m_canvas.updatePlayerHP(BattleManager.getInstance().getCurPoke()
        .getCurHP());
      data[0] = BattleManager.getInstance().getCurPoke().getName();
    } else {
      m_pokeName = BattleManager.getInstance().getCurEnemyPoke().getName();
      m_newHPValue = BattleManager.getInstance().getCurEnemyPoke().getCurHP()
        + Integer.parseInt(data[1]);
      if (m_newHPValue < 0) {
        m_newHPValue = 0;
      } else if(m_newHPValue > BattleManager.getInstance().getCurEnemyPoke().getMaxHP())
      {
    	  m_newHPValue = BattleManager.getInstance().getCurEnemyPoke().getMaxHP();
      }
      BattleManager.getInstance().getCurEnemyPoke().setCurHP(m_newHPValue);
      m_canvas.updateEnemyHP(BattleManager.getInstance().getCurEnemyPoke()
        .getCurHP());
      data[0] = BattleManager.getInstance().getCurEnemyPoke().getName();
    }

    if (i == 1 && m_newHPValue == 0) {
      m_canvas.setPokeballImage(BattleManager.getInstance().getCurEnemyIndex(),
        "fainted");
    }

    if (Integer.parseInt(data[1]) <= 0) {
      addSpeech(m_translator.get(7));
      addSpeech(m_translator.get(8));
    } else {
      addSpeech(m_translator.get(9));
    }
  }

  /**
   * Informs a victory on the player's side
   */
  public void informVictory() {
    m_trainer = GameClient.getInstance().getOurPlayer().getUsername();
    addSpeech(m_translator.get(10));
    BattleManager.getInstance().endBattle();
    m_isBattling = false;
  }

  /**
   * Informs a loss on the player's side
   */
  public void informLoss() {
    m_trainer = GameClient.getInstance().getOurPlayer().getUsername();
    addSpeech(m_translator.get(11));
    BattleManager.getInstance().endBattle();
    m_isBattling = false;
  }

  /**
   * Shows a custom message sent by the server
   * 
   * @param msg
   */
  public void showMessage(String msg) {
    addSpeech(msg);
  }

  /**
   * Informs if a run was successful
   * 
   * @param canRun
   */
  public void informRun(boolean canRun) {
    if (canRun) {
      addSpeech(m_translator.get(12));
      m_narrator.advance();
      BattleManager.getInstance().endBattle();
    } else {
      addSpeech(m_translator.get(13));
      m_narrator.advance();
      informMoveRequested();
    }
  }

  /**
   * Informs the player's earnings
   * 
   * @param money
   */
  public void informMoneyGain(int money) {
    m_earnings = money;
    addSpeech(m_translator.get(19));
  }

  /**
   * Informs the player's that the pokemon dropped an item
   * 
   * @param item
   */
  public void informItemDropped(String item) {
    m_foundItem = item;
    if (BattleManager.getInstance().isWild()) {
      m_pokeName = BattleManager.getInstance().getCurEnemyPoke().getName();
      addSpeech(m_translator.get(22));
    } else
      addSpeech(m_translator.get(23));
  }

  /**
   * Informs the player's earnings
   * 
   * @param money
   */
  public void informLevelUp(String poke, int level) {
    m_pokeName = poke;
    m_level = level;
    addSpeech(m_translator.get(20));
  }

  /**
   * Adds speech to the narrator and waits for it to be read before the next
   * action is taken
   * 
   * @param msg
   */
  public void addSpeech(String msg) {
    String newMsg = parsel10n(msg);
    m_narrator.addSpeech(parsel10n(newMsg));
    while (!m_narrator.getCurrentLine().equalsIgnoreCase(newMsg))
      ;
    while (!m_narrator.getAdvancedLine().equalsIgnoreCase(newMsg))
      ;
  }

  /**
   * Returns the battle speech
   * 
   * @return
   */
  public BattleSpeechFrame getBattleSpeech() {
    return m_narrator;
  }

  /**
   * Returns the battle canvas
   * 
   * @return
   */
  public BattleCanvas getBattleCanvas() {
    return m_canvas;
  }

  /**
   * Stops the timeline
   */
  public void endBattle() {
    m_canvas.stop();
    try {
      GameClient.getInstance().getDisplay().remove(m_canvas);
    } catch (Exception e) {
    }
    ;
    while (GameClient.getInstance().getDisplay().containsChild(m_canvas))
      ;
    try {
      GameClient.getInstance().getDisplay().remove(m_narrator);
    } catch (Exception e) {
    }
    ;
    while (GameClient.getInstance().getDisplay().containsChild(m_narrator))
      ;
  }

  /**
   * Uses regexes to create the appropriate battle messages for battle
   * 
   * @param line
   */
  public String parsel10n(String line) {
    if (line.contains("trainerName")) {
      line = line.replaceAll("trainerName", m_trainer);
    }
    if (line.contains("moveName")) {
      line = line.replaceAll("moveName", m_move);
    }
    if (line.contains("pokeName")) {
      line = line.replace("pokeName", m_pokeName);
    }
    if (line.contains("hpNum")) {
      line = line.replaceAll("hpNum", String.valueOf(m_newHPValue));
    }
    if (line.contains("expNum")) {
      line = line.replaceAll("expNum", String.valueOf(m_exp));
    }
    if (line.contains("damageNum")) {
      line = line.replaceAll("damageNum", String.valueOf(m_dmg));
    }
    if (line.contains("earningsNum")) {
      line = line.replaceAll("earningsNum", String.valueOf(m_earnings));
    }
    if (line.contains("levelNum")) {
      line = line.replaceAll("levelNum", String.valueOf(m_level));
    }
    if (line.contains("rewardItem")) {
      line = line.replaceAll("rewardItem", m_foundItem);
    }
    if (line.contains("expRemaining")) {
      line = line.replaceAll("expRemaining", String.valueOf(m_expRemaining));
    }
    return line;
  }

  public boolean isBattling() {
    return m_isBattling;
  }
}
