package org.pokenet.server.battle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import org.pokenet.server.backend.item.DropData;
import org.pokenet.server.battle.Pokemon.ExpTypes;
import org.pokenet.server.battle.mechanics.PokemonType;
import org.pokenet.server.battle.mechanics.StatException;
import org.pokenet.server.battle.mechanics.moves.MoveSet;
import org.pokenet.server.battle.mechanics.moves.MoveSetData;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

public class PokemonSpecies {
	
	private static PokemonSpeciesData m_default = new PokemonSpeciesData();
    @Element
    protected int m_species;
    @Element
    protected String m_name;
    
    /**
     * Gender constants.
     */
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;
    public static final int GENDER_BOTH = GENDER_MALE | GENDER_FEMALE;
    public static final int GENDER_NONE = 0;
    
    @ElementArray
    transient protected int[] m_base;
 
	@ElementArray
    transient protected PokemonType[] m_type;
    @Element
    transient protected int m_genders; // Possible genders.
	@Element
	protected String m_internalName;
	@Element
	protected String m_kind;
	@Element
	protected String m_pokedex;
	
	@Element
	protected String m_type1;
	@Element(required=false)
	protected String m_type2;
	
	@ElementArray
	protected int[] m_baseStats = new int[6];
	
	@Element
	protected int m_rareness;
	
	@Element
	protected int m_baseEXP;
	@Element
	protected int m_happiness;
	@Element
	protected ExpTypes m_growthRate;
	@Element
	protected int m_stepsToHatch;
	
	@Element
	protected String m_color;
	@Element(required=false)
	protected String m_habitat;
	
	@ElementArray
	protected int[] m_effortPoints = new int[6];
	@ElementList
	protected String [] m_abilities;
	
	@ElementArray
	protected int[] m_compatibility = new int[2];
	
	@Element
	protected float m_height;
	@Element
	protected float m_weight;
	
	@Element
	protected int m_femalePercentage;
	
	@ElementMap
	protected Map<Integer, String> m_levelMoves;
	@ElementArray
	protected String [] m_starterMoves;
	@ElementArray
	protected String [] m_eggMoves;
	@ElementArray
	protected PokemonEvolution [] m_evolutions;
	@ElementArray
	protected String [] m_tmMoves;
	@ElementArray
	protected DropData [] m_drops;
	
	public DropData [] getDropData() {
		return m_drops;
	}
	
	public void setDropData(DropData [] d) {
		m_drops = d;
	}
	
	/**
	 * Returns a random item dropped by the Pokemon, -1 if no item was dropped
	 * @return
	 */
	public int getRandomItem() {
		if(m_drops == null) {
			System.err.println("INFO: Drop data null for " + m_name);
			return -1;
		}
		if(DataService.getBattleMechanics().getRandom().nextInt(99) < 30) {
            int r = 100;
            ArrayList<Integer> m_result = new ArrayList<Integer>();
            for(int i = 0; i < m_drops.length; i++) {
                    r = DataService.getBattleMechanics().getRandom().nextInt(100) + 1;
                    if(m_drops[i] != null && r < m_drops[i].getProbability())
                            m_result.add(m_drops[i].getItemNumber());
            }
            return m_result.size() > 0 ? 
                            m_result.get(DataService.getBattleMechanics()
                                            .getRandom().nextInt(m_result.size())) : -1;
		}
		return - 1;
	}
	
	  /**
     * Returns the pokedex number
     * @return
     */
    public int getSpeciesNumber() {
    	return m_species;
    }
    
    /**
     * Return the possible genders for this species.
     */
    public int getPossibleGenders() {
        return m_genders;
    }
    
    /**
     * Set the possible genders for this species.
     */
    public void setPossibleGenders(int genders) {
        m_genders = genders;
    }
    
    /**
     * Return the default species data.
     */
    public static PokemonSpeciesData getDefaultData() {
        return m_default;
    }
    
    /**
     * Set the default species data.
     */
    public static void setDefaultData(PokemonSpeciesData data) {
        m_default = data;
    }
    
    /**
     * Get a "balanced" level for this species using this formula:
     *     level = 113 - 0.074 * [base stat total]
     * This formula places the pokemon's level within the interval [60, 100]
     * based on base stats.
     */
    public int getBalancedLevel() {
        int total = 0;
        for (int i = 0; i < m_base.length; ++i) {
            total += m_base[i];
        }
        int level = (int)Math.round(113.0 - 0.074 * ((double)total));
        if (level < 0) {
            level = 0;
        } else if (level > 100) {
            level = 100;
        }
        return level;
    }
    
    /**
     * Return whether a pokemon can have a particular ability.
     */
    public boolean canUseAbility(PokemonSpeciesData data, String ability) {
        return data.canUseAbility(m_name, ability);
    }
    
    /**
     * Return a TreeSet of possible abilities.
     */
    public String[] getPossibleAbilities(PokemonSpeciesData data) {
        return data.getPossibleAbilities(m_name);
    }
    
    /**
     * Construct a new pokemon species with arbitrary stats.
     */
    public PokemonSpecies(int species, String name, int[] base, int gender) {
        m_species = species;
        m_name = name;
        m_base = base;
        m_genders = gender;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    
    /**
     * Read a PokemonSpecies from a stream, backed by an arbitrary
     * PokemonSpeciesData object.
     */
    public synchronized static Object readObject(PokemonSpeciesData data, ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        PokemonSpeciesData old = m_default;
        m_default = data;
        Object o = in.readObject();
        m_default = old;
        return o;
    }
    
    /**
     * This methods prevents pokemon with arbitrary base stats from being
     * loaded. Pokemon are unserialised only by id and their stats are loaded
     * from that id.
     *
     * This method creatively throws an IOException if the species id does not
     * correspond to a valid pokemon species.
     *
     * This method works from the default species data. To use this with
     * arbitrary species data, use the <code>readFromStream</code> method.
     */
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        PokemonSpecies species;
        try {
            species = m_default.getSpecies(m_species);
        } catch (PokemonException e) {
            throw new IOException();
        }
        m_name = species.m_name;
        m_base = species.m_base;
        m_type = species.m_type;
        m_genders = species.m_genders;
    }
    
    /**
     * Creates a new instance of PokemonSpecies
     */
    public PokemonSpecies(PokemonSpeciesData data, int i) throws PokemonException {
        this(data.getSpecies(i));
    }
    
    /**
     * Allows for construction from another PokemonSpecies.
     */
    public PokemonSpecies(PokemonSpecies i) {
        m_species = i.m_species;
        m_name = i.m_name;
        m_base = i.m_base;
        m_type = i.m_type;
        m_genders = i.m_genders;
    }
    
    /** Constructor used for serialization */
    public PokemonSpecies() {}
    
    public PokemonType[] getTypes() {
        return m_type;
    }
    
    public int getBase(int i) throws StatException {
        if ((i < 0) || (i > 5)) throw new StatException();
        return m_base[i];
    }
    
    /**
     * Get the MoveSet associated with this species.
     */
    public MoveSet getMoveSet(MoveSetData data) {
        return data.getMoveSet(m_species);
    }
    
    /**
     * Return whether this species can learn a particular move.
     */
    public boolean canLearn(PokemonSpeciesData data, String move) {
        return data.canLearn(this, move);
    }
	
	public int getSpecies() {
		return m_species;
	}
	public void setSpecies(int mSpecies) {
		m_species = mSpecies;
	}
	public String getName() {
		return m_name;
	}
	public void setName(String mName) {
		m_name = mName;
	}
	public int[] getBase() {
		return m_base;
	}
	public void setBase(int[] mBase) {
		m_base = mBase;
	}
	public PokemonType[] getType() {
		return m_type;
	}
	public void setType(PokemonType[] mType) {
		m_type = mType;
	}
	public int getGenders() {
		return m_genders;
	}
	public void setGenders(int mGenders) {
		m_genders = mGenders;
	}
	public String getInternalName() {
		return m_internalName;
	}
	public void setInternalName(String mInternalName) {
		m_internalName = mInternalName;
	}
	public String getKind() {
		return m_kind;
	}
	public void setKind(String mKind) {
		m_kind = mKind;
	}
	public String getPokedexInfo() {
		return m_pokedex;
	}
	public void setPokedexInfo(String mPokedex) {
		m_pokedex = mPokedex;
	}
	public String getType1() {
		return m_type1;
	}
	public void setType1(String mType1) {
		m_type1 = mType1;
	}
	public String getType2() {
		return m_type2;
	}
	public void setType2(String mType2) {
		m_type2 = mType2;
	}
	public int[] getBaseStats() {
		return m_baseStats;
	}
	public void setBaseStats(int[] mBaseStats) {
		m_baseStats = mBaseStats;
	}
	public int getRareness() {
		return m_rareness;
	}
	public void setRareness(int mRareness) {
		m_rareness = mRareness;
	}
	public int getBaseEXP() {
		return m_baseEXP;
	}
	public void setBaseEXP(int mBaseEXP) {
		m_baseEXP = mBaseEXP;
	}
	public int getHappiness() {
		return m_happiness;
	}
	public void setHappiness(int mHappiness) {
		m_happiness = mHappiness;
	}
	public ExpTypes getGrowthRate() {
		return m_growthRate;
	}
	public void setGrowthRate(ExpTypes mGrowthRate) {
		m_growthRate = mGrowthRate;
	}
	public int getStepsToHatch() {
		return m_stepsToHatch;
	}
	public void setStepsToHatch(int mStepsToHatch) {
		m_stepsToHatch = mStepsToHatch;
	}
	public String getColor() {
		return m_color;
	}
	public void setColor(String mColor) {
		m_color = mColor;
	}
	public String getHabitat() {
		return m_habitat;
	}
	public void setHabitat(String mHabitat) {
		m_habitat = mHabitat;
	}
	public int[] getEffortPoints() {
		return m_effortPoints;
	}
	public void setEffortPoints(int[] mEffortPoints) {
		m_effortPoints = mEffortPoints;
	}
	public String [] getAbilities() {
		return m_abilities;
	}
	public void setAbilities(String [] mAbilities) {
		m_abilities = mAbilities;
	}
	public int[] getCompatibility() {
		return m_compatibility;
	}
	public void setCompatibility(int[] mCompatibility) {
		m_compatibility = mCompatibility;
	}
	public float getHeight() {
		return m_height;
	}
	public void setHeight(float mHeight) {
		m_height = mHeight;
	}
	public float getWeight() {
		return m_weight;
	}
	public void setWeight(float mWeight) {
		m_weight = mWeight;
	}
	public int getFemalePercentage() {
		return m_femalePercentage;
	}
	public void setFemalePercentage(int mFemalePercentage) {
		m_femalePercentage = mFemalePercentage;
	}
	public Map<Integer, String> getLevelMoves() {
		return m_levelMoves;
	}
	public void setLevelMoves(HashMap<Integer, String> mMoves) {
		m_levelMoves = mMoves;
	}
	
	public void setStarterMoves(String [] m) {
		m_starterMoves = m;
	}
	
	public String [] getStarterMoves() {
		return m_starterMoves;
	}

	public String [] getEggMoves() {
		return m_eggMoves;
	}
	public void setEggMoves(String [] mEggMoves) {
		m_eggMoves = mEggMoves;
	}
	public PokemonEvolution [] getEvolutions() {
		return m_evolutions;
	}
	public void setEvolutions(PokemonEvolution [] mEvolutions) {
		m_evolutions = mEvolutions;
	}
	public String [] getTMMoves() {
		return m_tmMoves;
	}
	public void setTMMoves(String [] mPossibleMoves) {
		m_tmMoves = mPossibleMoves;
	}
}
