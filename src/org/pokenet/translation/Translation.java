package org.pokenet.translation;

import java.util.ArrayList;
import java.util.List;


public class Translation {
	private String language;
	private List<String> lines = new ArrayList<String>();
	
	public Translation(String lang){
		language = lang;
	}

	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public List<String> getLines() {
		return lines;
	}
	public void setLines(List<String> lines) {
		this.lines = lines;
	}
	public void addLine(String line) {
		this.lines.add(line);
	}

}
