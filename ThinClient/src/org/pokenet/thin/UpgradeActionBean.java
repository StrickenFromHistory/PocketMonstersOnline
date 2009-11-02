package org.pokenet.thin;

public class UpgradeActionBean {

	private String input;
	private String output;
	private String checksum;
	
	public UpgradeActionBean(String input, String output, String shasum) {
		this.input = input;
		this.output = output;
		checksum = shasum;
	}
	
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	
}

