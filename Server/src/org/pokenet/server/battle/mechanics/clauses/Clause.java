package org.pokenet.server.battle.mechanics.clauses;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.ModData;
import org.pokenet.server.battle.mechanics.moves.MoveListEntry;
import org.pokenet.server.battle.mechanics.moves.PokemonMove;
import org.pokenet.server.battle.mechanics.moves.StatusMove;
import org.pokenet.server.battle.mechanics.moves.MoveList.OneHitKillMove;
import org.pokenet.server.battle.mechanics.statuses.FreezeEffect;
import org.pokenet.server.battle.mechanics.statuses.SleepEffect;
import org.pokenet.server.battle.mechanics.statuses.StatChangeEffect;
import org.pokenet.server.battle.mechanics.statuses.StatusEffect;
import org.pokenet.server.battle.mechanics.statuses.field.FieldEffect;
import org.pokenet.server.battle.mechanics.statuses.items.HoldItem;

/**
 * A rule applied to a battle.
 * 
 * @author Colin
 */
public abstract class Clause extends FieldEffect {
    
    @SuppressWarnings("unchecked")
	public static class ClauseChoice implements Serializable, Comparable {
        private static final long serialVersionUID = 1L;
        private String m_name, m_description;
        private boolean m_default, m_disablesSelection;
        private transient Clause m_clause;
        public ClauseChoice(Clause c) {
            m_name = c.getClauseName();
            m_description = c.getClauseDescription();
            m_default = c.isEnabledByDefault();
            m_disablesSelection = c.disablesTeamSelection();
            m_clause = c;
        }
        public Clause getClause() {
            return m_clause;
        }
        public int compareTo(Object o2) {
            ClauseChoice c2 = (ClauseChoice)o2;
            return m_name.compareToIgnoreCase(c2.m_name);
        }
        public String getName() {
            return m_name;
        }
        public String getDescription() {
            return m_description;
        }
        public boolean isEnabledByDefault() {
            return m_default;
        }
        public boolean disablesTeamSelection() {
            return m_disablesSelection;
        }
    }
    
    private String m_name;
    private static final Map<String, Clause> m_clauses = new HashMap<String, Clause>();
    private static ClauseChoice[] m_clauseChoices;
    
    static {
        
        new EffectClause("Sleep Clause", SleepEffect.class) {
            public String getClauseDescription() {
                return "Only one pokemon on each team may be afflicted "
                        + "with enemy-induced sleep at a given time. "
                        + "Subsequent enemy attempts to inflict sleep fail.";
            }
            
            public boolean isEnabledByDefault() {
                return true;
            }
        };
        
        new EffectClause("Freeze Clause", FreezeEffect.class) {
            public String getClauseDescription() {
                return "Only one pokemon on each team may be afflicted "
                        + "with freeze at a given time. "
                        + "Subsequent uses of moves with a chance to freeze "
                        + "will not freeze.";
            }
        };
        
        new PendanticDamageClause();
        
        new Clause("Item Clause") {
            public String getClauseDescription() {
                return "No two pokemon on a team may begin the battle "
                        + "holding the same item.";
            }
            public boolean isTeamValid(BattleField field, Pokemon[] team, int idx) {
                Set<String> set = new HashSet<String>();
                for (int i = 0; i < team.length; ++i) {
                    if (team[i] == null)
                        continue;
                    HoldItem item = team[i].getItem();
                    if (item != null) {
                        if (!set.add(item.getName())) {
                            return false;
                        }
                    }
                }
                return true;
            }
        };

        new Clause("Species Clause") {
            public String getClauseDescription() {
                return "No two pokemon on a team may have the same species.";
            }
            public boolean isTeamValid(BattleField field, Pokemon[] team, int idx) {
                Set<String> set = new HashSet<String>();
                for (int i = 0; i < team.length; ++i) {
                    if (team[i] == null)
                        continue;
                    if (!set.add(team[i].getSpeciesName())) {
                        return false;
                    }
                }
                return true;
            }
            public boolean isEnabledByDefault() {
                return true;
            }
        };
        
        new Clause("Random Battle") {
            public String getClauseDescription() {
                return "Instead of loading teams, both players use randomly "
                        + "generated parties.";
            }
            public boolean isTeamValid(BattleField field, Pokemon[] team, int idx) {
                for (int i = 0; i < team.length; ++i) {
                    if (team[i] != null) {
                        team[i].setItem(null);
                        team[i].setAbility(null, true);
                    }
                    team[i] = Pokemon.getRandomPokemon(
                            ModData.getDefaultData(), field.getMechanics());
                }
                field.attachField(idx);
                return true;
            }
            public boolean disablesTeamSelection() {
                return true;
            }
            public int getTier() {
                return -2;
            }
        };
        
        new Clause("OHKO Clause") {
            public String getClauseDescription() {
                return "Moves that kill in one hit (e.g. Fissure) fail.";
            }
            public boolean isMoveTransformer(boolean enemy) {
                return !enemy;
            }
            protected MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
                if (entry.getMove() instanceof OneHitKillMove) {
                    BattleField field = p.getField();
                    field.informUseMove(p, entry.getName());
                    field.showMessage("But it failed!");
                    return null;
                }
                return entry;
            }
        };
        
        new Clause("Level Balance") {
            public String getClauseDescription() {
                return "Multiplies 0.074 by a pokemon's base stat total "
                        + "and subtracts the result from 113; the pokemon's "
                        + "level is set to the integer nearest this value. "
                        + "This gives each pokemon a level in the closed "
                        + "interval [60, 100] based on its base stats.";
            }
            public boolean isTeamValid(BattleField field, Pokemon[] team, int idx) {
                for (int i = 0; i < team.length; ++i) {
                    Pokemon p = team[i];
                    if (p != null) {
                        p.setLevel(p.getBalancedLevel());
                        p.calculateStats(true);
                    }
                }
                return true;
            }
        };
        
        new Clause("Evasion Clause") {
            public String getClauseDescription() {
                return "Moves that specifically raise evasion (e.g. Double "
                        + "Team) fail.";
            }
            public boolean isMoveTransformer(boolean enemy) {
                return !enemy;
            }
            protected MoveListEntry getTransformedMove(Pokemon p, MoveListEntry entry) {
                PokemonMove move = entry.getMove();
                if (move instanceof StatusMove) {
                    StatusMove statusMove = (StatusMove)move;
                    StatusEffect[] effects = statusMove.getEffects();
                    boolean failure = false;
                    for (int i = 0; i < effects.length; ++i) {
                        if (!statusMove.getAttacker(i))
                            continue;
                        StatusEffect eff = effects[i];
                        if (eff instanceof StatChangeEffect) {
                            StatChangeEffect stat = (StatChangeEffect)eff;
                            if ((stat.getStat() == Pokemon.S_EVASION)
                                    && stat.isRaise()) {
                                failure = true;
                                break;
                            }
                        }
                    }
                    
                    if (failure) {
                        BattleField field = p.getField();
                        field.informUseMove(p, entry.getName());
                        field.showMessage("But it failed!");
                        return null;
                    }
                }
                return entry;
            }
        };
        
        initialiseClauseChoices();
        
    }
    
    @SuppressWarnings("unchecked")
	private static void initialiseClauseChoices() {
        m_clauseChoices = new ClauseChoice[m_clauses.size()];
        int i = 0;
        Iterator<Clause> j = m_clauses.values().iterator();
        while (j.hasNext()) {
            m_clauseChoices[i++] = new ClauseChoice((Clause)j.next());
        }
        Collections.sort(Arrays.asList(m_clauseChoices));
    }
    
    public static class PendanticDamageClause extends Clause {
        public PendanticDamageClause() {
            super("Strict Damage Clause");
        }
        public String getClauseDescription() {
            return "By default, Shoddy Battle does not limit the display of "
                    + "damage done to a pokemon to its remaining health. "
                    + "Enabling this clause causes Shoddy Battle never to "
                    + "show more damage being done than the target has "
                    + "remaining health.";
        }
    }
    
    public static Clause getInstance(String name) {
        return (Clause)m_clauses.get(name);
    }
    
    public static ClauseChoice[] getClauses() {
        return m_clauseChoices;
    }
    
    public Clause(String name) {
        m_clauses.put(name, this);
        m_name = name;
    }
    
    public String getClauseName() {
        return m_name;
    }
    
    public boolean disablesTeamSelection() {
        return false;
    }
    
    public boolean isEnabledByDefault() {
        return false;
    }
    
    public int getTier() {
        return -1;
    }
    
    public boolean isTeamValid(BattleField field, Pokemon[] team, int idx) {
        return true;
    }
    
    public abstract String getClauseDescription();
    
    public boolean applyToField(BattleField field) {
        return true;
    }
    
    public boolean tickField(BattleField field) {
        return false;
    }

}
