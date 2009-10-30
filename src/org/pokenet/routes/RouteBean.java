package org.pokenet.routes;

public class RouteBean {
	private int x;
	private int y;
	private String mapname;
	
	public RouteBean(int x2, int y2, String string) {
		x = x2;
		y = y2;
		mapname = string;
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
	public String getMapname() {
		return mapname;
	}
	public void setMapname(String mapname) {
		this.mapname = mapname;
	}
	
}
