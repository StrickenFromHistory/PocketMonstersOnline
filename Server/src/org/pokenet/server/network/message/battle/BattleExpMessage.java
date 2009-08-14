
package org.pokenet.server.network.message.battle;

import org.pokenet.server.network.message.PokenetMessage;

/**
 * Exp gain during battle
 * 
 * @author shadowkanji
 */
public class BattleExpMessage extends PokenetMessage {
  /**
   * Constructor
   * 
   * @param pokeName
   * @param exp
   */
  public BattleExpMessage(String pokeName, double exp, double rem) {
    m_message = "b." + pokeName + "," + exp + "," + rem;
  }
}
