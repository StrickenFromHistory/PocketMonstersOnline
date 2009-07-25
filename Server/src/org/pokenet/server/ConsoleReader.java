package org.pokenet.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Allows easier input via a text file or console
 * @author J.M. Morris
 *
 */
public class ConsoleReader {

	private BufferedReader br; // the input stream

	static String buffer = "";
	static int p = 1; // buffer[p..] contains next input

	/**
	 * Constructor
	 * @param f - the name of the file
	 */
	public ConsoleReader(String f) {
		try { 
			br = new BufferedReader(new FileReader(f));
		} 
		catch (FileNotFoundException e) {e.printStackTrace();}
	}

	/**
	 * Constructor
	 */
	public ConsoleReader() { // default: keyboard input
		br = new BufferedReader(new InputStreamReader(System.in));
	}       

	/**
	 * Returns the next token of in the input stream
	 * @return
	 * @throws IOException
	 */
	String getToken() throws IOException {
		while (buffer != null && (p>= buffer.length() || 
				Character.isWhitespace(buffer.charAt(p)))) {
			if (p>= buffer.length()) {
				buffer = br.readLine();
				p = 0;
			}
			else p++;
		}
		if (buffer == null) throw new IOException("ConsoleReader: Unexpected end of file");
		int t = p;
		p++;
		while(p < buffer.length() &&
				!(Character.isWhitespace(buffer.charAt(p))))
			p++;
		p++;
		return(buffer.substring(t,p-1));
	}

	/**
	 * Read the next integer
	 * @return
	 */
	public int readInt() {
		try {   
			return Integer.parseInt(getToken());
		} 
		catch (Exception e) {
			System.err.println("ConsoleReader: IO Exception in readInt");
			return 0;
		}
	}   

	/**
	 * Read the next boolean
	 * NOTE: Any other string other than "true" is treated as false
	 * @return
	 */
	public boolean readBoolean() {
		try {   
			return new Boolean(getToken()).booleanValue();
		} 
		catch (Exception e) {
			System.err.println("ConsoleReader: IO Exception in readBoolean");
			return false;
		}
	}

	/**
	 * Read the next double
	 * @return
	 */
	public double readDouble() {
		try {
			return new Double(getToken()).doubleValue();
		} 
		catch (Exception ioe) {
			System.err.println("ConsoleReader: IO Exception in readDouble");
			return 0.0;
		}
	}

	/**
	 * Reads the next Token
	 * @return
	 */
	public String readToken() {
		// Consume and return a token. Trailing delimiter consumed.
		// A token is a maximal sequence of non-whitespace characters.
		// null returned on end of file
		try {
			while (buffer != null && (p>= buffer.length() || 
					Character.isWhitespace(buffer.charAt(p)))) {
				if (p>= buffer.length()) {
					buffer = br.readLine();
					p = 0;
				}
				else p++;
			}
			if (buffer == null) return null;
			int t = p;
			p++;
			while(p < buffer.length() &&
					!(Character.isWhitespace(buffer.charAt(p))))
				p++;
			p++;
			return(buffer.substring(t,p-1));
		} 
		catch (IOException ioe) {
			System.err.println("ConsoleReader: IO Exception in readToken");
			return "";
		} 
	}

	/**
	 * Reads the next character
	 * @return
	 */
	public char readChar() {
		//Consume and return a character (which may be an end-of-line).
		try { 
			if (buffer != null && p>buffer.length()) {
				buffer = br.readLine();
				p = 0;
			}
			if (buffer == null) 
				throw new IOException("ConsoleReader: Unexpected end of file in readChar"); 
			if (p == buffer.length()) { // supply end-of-line
				p++; 
				return('\n'); 
			}       
			else {
				p++;
				return buffer.charAt(p-1); 
			} 
		} 
		catch (IOException ioe) {
			System.err.println("ConsoleReader: IO Exception in readChar");
			return (char)0;
		}
	}

	/**
	 * Allows you to see what the next character is
	 * @return
	 */
	public char peekChar() {
		// The next available character if any (which may be an end-of-line). The
		// character is not consumed. If buffer is empty return null character.
		if (buffer == null || p>buffer.length()) return('\000'); 
		else if (p == buffer.length()) return('\n'); 
		else return buffer.charAt(p); 
	}

	/**
	 * Reads the next string
	 * @return
	 */
	public String readString() {
		// Consume and return the remainder of current line (end-of-line discarded).
		// null returned on end of file
		try {
			if (buffer!= null && p>buffer.length()) {
				buffer = br.readLine();
				p = 0;
			}   
			if (buffer == null) return null; 
			int t = p;  p = buffer.length() + 1;
			return buffer.substring(t);
		} 
		catch (IOException ioe) {
			System.err.println("ConsoleReader: IO Exception in readString");
			return "";
		} 
	}

	/**
	 * Returns the amount of characters available
	 * @return
	 */
	public int available() {
		if (buffer == null) return 0;
		else return (buffer.length()+1-p);
	}

	/**
	 * Returns true if there are more tokens
	 * @return
	 */
	public boolean hasMoreTokens() {
		// Are there more tokens on the current line?
		if (buffer == null) return false;
		int q = p; 
		while (q<buffer.length() && Character.isWhitespace(buffer.charAt(q))) q++;
		return (q<buffer.length());
	}

	/**
	 * Skip the rest of this line
	 */
	public void skipLine() {
		// Skip any remaining input on this line.
		if (buffer != null) p = buffer.length() + 1;
	}

	/**
	 * Skips whitespace
	 */
	public void skipWhitespace() {
		// Consumes input until a non-whitespace character is entered (which
		// is not consumed).
		try {
			while (buffer != null && (p>= buffer.length() || 
					Character.isWhitespace(buffer.charAt(p)))) {
				if (p>= buffer.length()) {
					buffer = br.readLine(); 
					p = 0;
				}
				else p++;
			}
		} 
		catch (IOException ioe) {
			System.err.println("ConsoleReader: IO Exception in skipWhitespace");
		}
	}   

	/**
	 * Returns true if the buffer is at the end of the file or input
	 * @return
	 */
	public boolean endOfFile() { //Does the file contain more characters
		// Not intended for use with keyboard.
		// This version just provides alternative spelling
		if (available()>0) return false;
		try { 
			buffer = br.readLine(); 
		} 
		catch (IOException ioe) {
			System.err.println("ConsoleReader: IO Exception in EndOfFile");
		}
		p = 0;
		return (buffer == null);
	}

	/**
	 * Closes the file
	 */
	public void close() { // close file
		try {
			br.close();
		} 
		catch(IOException e) { 
			e.printStackTrace();
		}
	}    
}