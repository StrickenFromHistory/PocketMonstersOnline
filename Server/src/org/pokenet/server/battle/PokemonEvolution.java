package org.pokenet.server.battle;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class PokemonEvolution {

    public enum EvolutionTypes {
        Level,
        LevelFemale,
        LevelMale,
        HasMove,
        Item,
		Happiness,
        HappinessDay,
        HappinessNight,
        DayHoldItem,
        TradeItem,
        Trade,
        DefenseGreater,
        AttackGreater,
        AtkDefEqual,
        Silcoon,
        Cascoon,
        Ninjask,
        Shedinja,
        Beauty,
        HasInParty
    }
    @Element
    private EvolutionTypes m_type;
    @Element (required=false)
    private int m_level;
    @Element(required=false)
    private String m_attribute;
    @Element
    private String m_evolveTo;

    public EvolutionTypes getType() {
        return m_type;
    }
    public void setType(EvolutionTypes m_type) {
        this.m_type = m_type;
    }
    public int getLevel() {
        return m_level;
    }
    public void setLevel(int m_level) {
        this.m_level = m_level;
    }
    public String getAttribute() {
        return m_attribute;
    }
    public void setAttribute(String m_attribute) {
        this.m_attribute = m_attribute;
    }
    public void setEvolveTo(String m_evolveTo) {
        this.m_evolveTo = m_evolveTo;
    }
    public String getEvolveTo() {
        return m_evolveTo;
    }
}
