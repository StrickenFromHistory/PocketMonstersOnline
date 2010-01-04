package org.pokenet.thin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.pokenet.thin.libs.CheckSums;

/**
 * Generates the update file
 * @author Sienide
 *
 */
public class UpdateCreator {
	public static HashMap<String, String> m_files = new HashMap<String, String>();
	public static String UPDATEURL = "http://pokeglobal.sourceforge.net/pokenet/";
	
	public static void main(String [] args) {
		/* Create the revision number */
		int revision = 1;
		try {
			URL u = new URL(UPDATEURL + "updates.txt");
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							u.openStream()));
			revision = Integer.parseInt(in.readLine());
			revision++;
			in.close();
		} catch (Exception e) {
			revision = 1;
		}
		CheckSums s;
		try {
			/* Create checksum for client.jar */
			s = new CheckSums();
			System.out.println("Generating checksums");
			m_files.put("./client.jar", s.getSHA1Checksum("client.jar"));
			/* Create checksum for native libs */
			File f = new File("./lib/native/");
			String [] dir = f.list();
			for(int i = 0; i < dir.length; i++) {
				f = new File("./lib/native/" + dir[i]);
				if(f.isFile()) {
					m_files.put(f.getPath(), s.getSHA1Checksum(f.getPath()));
				}
			}
			/* Create checksum for res */
			f = new File("./res/");
			dir = f.list();
			for(int i = 0; i < dir.length; i++) {
				f = new File("./res/" + dir[i]);
				if(f.isFile()) {
					m_files.put(f.getPath(), s.getSHA1Checksum(f.getPath()));
				} else if(f.isDirectory()) {
					scanDirectory(f.getPath());
				}
			}
			/* Create updates.txt */
			System.out.println("Generating updates.txt");
			PrintWriter p = new PrintWriter(new File("updates.txt"));
			p.println(revision);
			Iterator<String> it = m_files.keySet().iterator();
			while(it.hasNext()) {
				String file = it.next();
				String checksum = m_files.get(file);
				file = file.substring(2).replace('\\', '/');
				p.println(checksum + " " + file);
			}
			p.flush();
			p.close();
			System.out.println("DONE!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Allows recursive scanning of directories
	 * @param directory
	 * @throws Exception
	 */
	private static void scanDirectory(String directory) throws Exception {
		if(directory.contains(".svn"))
			return;
		File f = new File(directory);
		String [] list = f.list();
		CheckSums s = new CheckSums();
		for(int i = 0; i < list.length; i++) {
			f = new File(directory + "/" + list[i]);
			if(f.isFile()) {
				m_files.put(f.getPath(), s.getSHA1Checksum(f.getPath()));
			} else if(f.isDirectory()) {
				scanDirectory(f.getPath());
			}
		}
	}
}
