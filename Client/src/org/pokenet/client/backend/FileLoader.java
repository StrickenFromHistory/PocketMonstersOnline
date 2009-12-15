package org.pokenet.client.backend;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A simple file loader to make our lives easier
 * @author ZombieBear
 *
 */
public class FileLoader {
	
	/**
	 *  Loads a file as an InputStream
	 * @param path
	 * @return an InputStream of a file
	 * @throws FileNotFoundException 
	 */
	public static InputStream loadFile(String path) throws FileNotFoundException {
		return new FileInputStream(path);
	}
	
	/**
	 * Loads a text file and gets it ready for parsing
	 * @param path
	 * @return a BufferedReader for a text file
	 * @throws FileNotFoundException 
	 */
	public static BufferedReader loadTextFile(String path) throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(loadFile(path)));
	}
}
