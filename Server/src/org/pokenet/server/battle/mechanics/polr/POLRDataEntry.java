package org.pokenet.server.battle.mechanics.polr;

import java.util.ArrayList;
import java.util.HashMap;

import org.pokenet.server.battle.Pokemon.ExpTypes;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

@Root
public class POLRDataEntry {
	@Element
	String name;
	@Element
	String internalName;
	@Element
	String kind;
	@Element
	String pokedex;
	
	@Element
	String type1;
	@Element(required=false)
	String type2;
	
	@ElementArray
	int[] baseStats = new int[6];
	
	@Element
	int rareness;
	
	@Element
	int baseEXP;
	@Element
	int happiness;
	@Element
	ExpTypes growthRate;
	@Element
	int stepsToHatch;
	
	@Element
	String color;
	@Element(required=false)
	String habitat;
	
	@ElementArray
	int[] effortPoints = new int[6];
	@ElementList
	ArrayList<String> abilities
		= new ArrayList<String>();
	
	@ElementArray
	int[] compatibility = new int[2];
	
	
	
	@Element
	float height;
	@Element
	float weight;
	
	@Element
	int femalePercentage;
	
	@ElementMap
	HashMap<Integer, String> moves =
		new HashMap<Integer, String>();
	@ElementList
	ArrayList<String> starterMoves =
		new ArrayList<String>();
	@ElementList
	ArrayList<String> eggMoves
		= new ArrayList<String>();
	@ElementList
	ArrayList<POLREvolution> evolutions 
		= new ArrayList<POLREvolution>();
	
	@Element
	int battlerPlayerY;
	@Element
	int battlerEnemyY;
	@Element
	int battlerAltitude;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInternalName() {
		return internalName;
	}
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getPokedex() {
		return pokedex;
	}
	public void setPokedex(String pokedex) {
		this.pokedex = pokedex;
	}
	public String getType1() {
		return type1;
	}
	public void setType1(String type1) {
		this.type1 = type1;
	}
	public String getType2() {
		return type2;
	}
	public void setType2(String type2) {
		this.type2 = type2;
	}
	public int[] getBaseStats() {
		return baseStats;
	}
	public void setBaseStats(int[] baseStats) {
		this.baseStats = baseStats;
	}
	public int getRareness() {
		return rareness;
	}
	public void setRareness(int rareness) {
		this.rareness = rareness;
	}
	public int getBaseEXP() {
		return baseEXP;
	}
	public void setBaseEXP(int baseEXP) {
		this.baseEXP = baseEXP;
	}
	public int getHappiness() {
		return happiness;
	}
	public void setHappiness(int happiness) {
		this.happiness = happiness;
	}
	public ExpTypes getGrowthRate() {
		return growthRate;
	}
	public void setGrowthRate(ExpTypes growthRate) {
		this.growthRate = growthRate;
	}
	public int getStepsToHatch() {
		return stepsToHatch;
	}
	public void setStepsToHatch(int stepsToHatch) {
		this.stepsToHatch = stepsToHatch;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getHabitat() {
		return habitat;
	}
	public void setHabitat(String habitat) {
		this.habitat = habitat;
	}
	public int[] getEffortPoints() {
		return effortPoints;
	}
	public void setEffortPoints(int[] effortPoints) {
		this.effortPoints = effortPoints;
	}
	public ArrayList<String> getAbilities() {
		return abilities;
	}
	public int[] getCompatibility() {
		return compatibility;
	}
	public void setCompatibility(int[] compatibility) {
		this.compatibility = compatibility;
	}
	public float getHeight() {
		return height;
	}
	public void setHeight(float height) {
		this.height = height;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public int getFemalePercentage() {
		return femalePercentage;
	}
	public void setGenderRate(String genderRate) {
		if (genderRate.equals("Genderless"))
			femalePercentage = -1;
		else if (genderRate.equals("FemaleOneEighth"))
			;
	}
	public HashMap<Integer, String> getMoves() {
		return moves;
	}
	public void setMoves(HashMap<Integer, String> moves) {
		this.moves = moves;
	}
	public ArrayList<String> getEggMoves() {
		return eggMoves;
	}
	public void setEggMoves(ArrayList<String> eggMoves) {
		this.eggMoves = eggMoves;
	}
	public ArrayList<POLREvolution> getEvolutions() {
		return evolutions;
	}
	public void setEvolutions(ArrayList<POLREvolution> evolutions) {
		this.evolutions = evolutions;
	}
	public int getBattlerPlayerY() {
		return battlerPlayerY;
	}
	public void setBattlerPlayerY(int battlerPlayerY) {
		this.battlerPlayerY = battlerPlayerY;
	}
	public int getBattlerEnemyY() {
		return battlerEnemyY;
	}
	public void setBattlerEnemyY(int battlerEnemyY) {
		this.battlerEnemyY = battlerEnemyY;
	}
	public int getBattlerAltitude() {
		return battlerAltitude;
	}
	public void setBattlerAltitude(int battlerAltitude) {
		this.battlerAltitude = battlerAltitude;
	}
	public void setStarterMoves(ArrayList<String> starterMoves) {
		this.starterMoves = starterMoves;
	}
	public ArrayList<String> getStarterMoves() {
		return starterMoves;
	}
}
