package org.pokenet.maps;

public class MapBean {
	private int x;
	private int y;
	private String pokeName;
	private String levels;
	private String chances;
	private String where;
	
	
	public MapBean(int x2, int y2) {
		x = x2;
		y = y2;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public String getPokeName() {
		return pokeName;
	}
	public void setPokeName(String pokeName) {
		this.pokeName = pokeName;
	}
	public String getLevels() {
		return levels;
	}
	public void setLevels(String levels) {
		this.levels = levels;
	}
	public String getChances() {
		return chances;
	}
	public void setChances(String chances) {
		this.chances = chances;
	}
	public String getWhere() {
		return where;
	}
	public void setWhere(String where) {
		this.where = where;
	}
	
}
