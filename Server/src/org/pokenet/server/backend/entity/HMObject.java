
package org.pokenet.server.backend.entity;

public class HMObject extends Char {
  public enum objectType {
    ROCK_SMASH, CUT, DE_FOG, FLY, SURF, STRENGTH, ROCK_CLIMB, WATERFALL
  };

  private objectType m_HMType;

  public objectType getType() {
    return m_HMType;
  }

  public void setType(objectType oT) {
    m_HMType = oT;
  }
}
