package org.pokenet.translation.utils;

import java.util.*;
import java.io.*;

/**
 * Recursive file listing under a specified directory.
 *  
 * @author javapractices.com
 * @author Alex Wong
 * @author anonymous user
 */
public final class FileListing {

	
	/**
	 * Return all Files in a Directory
	 * 
	 * @param directory - is the full name of an existing 
	 * directory that can be read.
	 * @throws Exception 
	 */
	public static List<File> getFiles(String directory) throws Exception {
		File startingDirectory= new File(directory);
		List<File> files = FileListing.getFileListing(startingDirectory);
		return files;
	}
	
	/**
	 * Demonstrate use.
	 * 
	 * @param aArgs - <tt>aArgs[0]</tt> is the full name of an existing 
	 * directory that can be read.
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		File startingDirectory= new File("../PokenetClientVenonat/");
		List<File> files = FileListing.getFileListing(startingDirectory);
		ArrayList<String> updates = new ArrayList<String>();
		//print out all file names, in the the order of File.compareTo()
		for(File file : files ){
			String filename = file.getAbsolutePath();
			filename = filename.replace("/home/Nushio/Workspace/PokenetContentServer/../PokenetClientVenonat/","");
			if(file.isFile())
				updates.add("|"+filename+" |+"+filename+" |"+"mkdir");
		}

		File f = new File("updates.txt");
		if(f.exists())
			f.delete();
		try {
			PrintWriter pw = new PrintWriter(f);
			pw.println("--v0.1--");
			pw.println("--------");
			pw.println("-|Initial Installation|-");
			pw.println("--------");
			for (int s = 0; s < updates.size(); s++){
				pw.println(updates.get(s));
			}
			pw.println("-EOF-");
			pw.flush();
			pw.close();

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Recursively walk a directory tree and return a List of all
	 * Files found; the List is sorted using File.compareTo().
	 *
	 * @param aStartingDir is a valid directory, which can be read.
	 */
	static public List<File> getFileListing(
			File aStartingDir
	) throws FileNotFoundException {
		validateDirectory(aStartingDir);
		List<File> result = getFileListingNoSort(aStartingDir);
		Collections.sort(result);
		return result;
	}

	// PRIVATE //
	static private List<File> getFileListingNoSort(
			File aStartingDir
	) throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = aStartingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for(File file : filesDirs) {
			result.add(file); //always add, even if directory
			if ( ! file.isFile() ) {
				//must be a directory
				//recursive call!
				List<File> deeperList = getFileListingNoSort(file);
				result.addAll(deeperList);
			}
		}
		return result;
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 */
	static private void validateDirectory (
			File aDirectory
	) throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
		}
	}
} 