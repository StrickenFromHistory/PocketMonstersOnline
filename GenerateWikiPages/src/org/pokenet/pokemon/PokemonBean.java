package org.pokenet.pokemon;

public class PokemonBean {
	private int id;
	private String name;
	private String kind;
	private String pokedex;
	private String type1;
	private String type2;
	private String rareness;
//	private ArrayList<Abilities> abilities;
//	private ArrayList<Moves> moves;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getDex() {
		return pokedex;
	}
	public void setDex(String pokedex) {
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
	public String getRareness() {
		return rareness;
	}
	public void setRareness(String rareness) {
		this.rareness = rareness;
	}	
}
