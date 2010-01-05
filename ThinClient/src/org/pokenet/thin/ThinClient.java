package org.pokenet.thin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import org.pokenet.thin.libs.CheckSums;
import org.pokenet.thin.libs.JGet;

/**
 * ThinClient
 * @author Sienide
 *
 */
public class ThinClient extends JFrame implements Runnable {
	private ImageIcon m_logo;
	private JProgressBar m_progress;
	private JLabel m_update;
	/* The root directory of update location 
	 * NOTE: Must start with http:// and end with a /
	 */
	public static String UPDATEURL = "http://pokeglobal.sourceforge.net/pokenet/";
	public static String LOGOURL = "http://trainerdex.org/bg.png";

	/**
	 * Constructor
	 */
	public ThinClient() {
		super("PokeNet Updater");
		this.setSize(396, 160);
		this.setResizable(false);
		/* Center the updater */
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((d.getWidth() / 2) - this.getWidth() / 2);
		int y = (int) ((d.getHeight() / 2) - this.getHeight() / 2);
		this.setLocation(x, y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/* Add PokeNet Image */
		try {
			m_logo = new ImageIcon(new URL(LOGOURL));
			this.add(new JLabel(m_logo), BorderLayout.CENTER);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		/* Create progress bar */
		m_progress = new JProgressBar();
		m_progress.setValue(0);
		/* Create bottom panel */
		m_update = new JLabel("Updating: ");
		JPanel l = new JPanel();
		l.add(m_update);
		l.add(m_progress);
		this.add(l, BorderLayout.SOUTH);
		this.setVisible(true);
		/* Start downloading updates */
		new Thread(this).start();
	}

	public static void main(String [] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		ThinClient c = new ThinClient();
	}

	@Override
	public void run() {
		int ourRev = 0;
		int currentRev = 1;
		/* Get the current revision, if any */
		try {
			Scanner revCheck = new Scanner(new File("./PokeNet/rev.txt"));
			ourRev = revCheck.nextInt();
			revCheck.close();
		} catch (Exception e) {
			ourRev = 0;
		}
		/* Hashmap of <files, checksums> */
		HashMap<String, String> files = new HashMap<String, String>();
		/* Download updates if possible */
		try {
			URL u = new URL(UPDATEURL + "updates.txt");
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							u.openStream()));
			/* Check current revision */
			currentRev = Integer.parseInt(in.readLine());
			if(ourRev < currentRev) {
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					String checksum = inputLine.substring(0, inputLine.indexOf(' '));
					String file = inputLine.substring(inputLine.indexOf(' ') + 1);
					files.put(file, checksum);
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			/* Update server not available, run game */
			try {
				this.setVisible(false);
				storeRevision(ourRev);
				runPokenet();
				System.exit(0);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "An error occured while running the game.");
				System.exit(0);
			}
			return;
		}
		int total = files.keySet().size();
		int value = 0;
		m_update.setText("Updating (" + value + "/" + total + "): ");
		/* We got the list of checksums, let's see if we need to update */
		Iterator<String> it = files.keySet().iterator();
		CheckSums s;
		String folder = "./";
		try {
			folder = new File("./PokeNet/").getCanonicalPath();
			folder = folder + "\\";
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(it.hasNext()) {
			String file = it.next();
			/* First check if we have the file */
			File f = new File(folder + file);
			if(f.exists()) {
				/* It exists, does it need updating? */
				s = new CheckSums();
				String current = "";
				String online = files.get(file);
				try {
					current = s.getSHA1Checksum(f.getPath());
					if(current.compareTo(online) != 0) {
						/* We need to update */
						f.delete();
						f = new File(folder + file);
						f.createNewFile();
						JGet.getFile(UPDATEURL + file, f.getPath());
					}
				} catch (Exception e) {
					/* Error! Redownload file */
					try {
						f.delete();
						f = new File(folder + file);
						f.createNewFile();
						JGet.getFile(UPDATEURL + file, f.getPath());
					} catch (Exception ex) {
						System.err.println(f.getPath());
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, "Could not download update.");
						break;
					}
				}
			} else {
				/* Check if directory exists */
				try {
					if(f.getPath().contains("\\")) {
						File dir = new File(f.getPath().substring(0, f.getPath().lastIndexOf('\\')));
						if(!dir.exists())
							dir.mkdirs();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				/* We don't have it, download it */
				try {
					f.createNewFile();
					JGet.getFile(UPDATEURL + file, f.getPath());
				} catch (Exception e) {
					System.err.println(f.getPath());
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Could not download update.");
					break;
				}
			}
			value++;
			m_progress.setValue(m_progress.getValue() + 1);
			if(m_progress.getValue() == m_progress.getMaximum())
				m_progress.setValue(0);
			m_update.setText("Updating (" + value + "/" + total + "): ");
		}
		/* Launch the game */
		try {
			this.setVisible(false);
			storeRevision(currentRev);
			runPokenet();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occured while running the game.");
			System.exit(0);
		}
	}
	
	public void storeRevision(int rev) {
		/* Store our revision */
		try {
			PrintWriter p = new PrintWriter(new File("./PokeNet/rev.txt"));
			p.println(rev);
			p.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void runPokenet() throws Exception {
		Process p = Runtime.getRuntime().exec("java -Dres.path=PokeNet/ -Djava.library.path=PokeNet/lib/native " +
		"-Xmx512m -Xms512m -jar ./PokeNet/client.jar");
		BufferedReader stdInput = new BufferedReader(new 
				InputStreamReader(p.getInputStream()));
		BufferedReader stdError = new BufferedReader(new 
				InputStreamReader(p.getErrorStream()));
		String line;
		PrintWriter pw = new PrintWriter(new File("./PokeNet/errors.txt"));
		while(true) {
			while ((line = stdInput.readLine()) != null) {
				System.out.println(line);
			}
			while ((line = stdError.readLine()) != null) {
				pw.println(line);
				pw.flush();
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {}
			try {
				if(p.exitValue() >= 0)
					break;
			} catch (Exception e) {}
		}
		pw.close();
	}
}
