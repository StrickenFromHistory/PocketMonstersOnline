package org.pokenet.content;

import java.security.MessageDigest;
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
			if(!filename.contains("src"))
				if(!filename.contains(".settings")||!filename.contains(".classpath")||!filename.contains(".directory")||!filename.contains("MANIFEST.MF")||!filename.contains(".svn")||!filename.contains("bin/"))
					if(file.isFile())
						updates.add("|"+filename+" |+"+filename+" |"+getMD5Checksum(file.getAbsolutePath()));
					else
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

	public static byte[] createChecksum(String filename) throws
	Exception
	{
		InputStream fis =  new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("SHA1");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	// see this How-to for a faster way to convert 
	// a byte array to a HEX string 
	public static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";
		for (int i=0; i < b.length; i++) {
			result +=
				Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
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