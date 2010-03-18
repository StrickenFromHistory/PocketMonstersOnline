import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;




public class Pokenet extends JFrame  implements Runnable {
	public static final String SVN_URL = "http://pokenet-release.svn.sourceforge.net/svnroot/pokenet-release";
	public static final String FOLDER_NAME = "pokenet-release";
	
	JTextArea outText;
	
	public Pokenet() {
		super("PokeNet Game Launcher and Updater");
		this.setSize(500, 300);
		this.setResizable(false);
		
		/* Center the updater */
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((d.getWidth() / 2) - this.getWidth() / 2);
		int y = (int) ((d.getHeight() / 2) - this.getHeight() / 2);
		this.setLocation(x, y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		outText = new JTextArea();
		outText.setEditable(false);
		this.add(outText);
		outText.append("Console Information...\n");
		this.setVisible(true);
		
		new Thread(this).start();

	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pokenet c = new Pokenet();
	}

	@Override
	public void run() {
		/*
		 * TODO:
		 * make sure they have SVN...
		 * if not, install it
		 */
		Process svn;
		Thread t = null;
		String command;

		boolean exists = (new File(FOLDER_NAME)).exists();
		if(!exists) {
			this.outText.append("Installing...\n Please be patient while PokeNet is downloaded...\n");

			command = "svn co " + SVN_URL;
		} else {
			this.outText.append("Updating...\n");
			
			command = "svn up";
		}
		
		try {
			svn = Runtime.getRuntime().exec(command);
			StreamReader sr = new StreamReader(svn.getInputStream(), "", outText);
			t = new Thread(sr);
			t.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while (t.isAlive()) {
			// TODO: progress
			
		}
		
		this.outText.append("Launching...\n");

		/* Launch the game */
		try {
			this.setVisible(false);
			runPokenet();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occured while running the game.");
			System.exit(0);
		}
		
	}
	
	public void runPokenet() throws Exception {
		Process p = Runtime.getRuntime().exec("java -Dres.path="+FOLDER_NAME+"/"
				+ " -Djava.library.path=client/lib/native " +
		"-Xmx512m -Xms512m -jar ./"+FOLDER_NAME+"/client.jar");
		StreamReader r1 = new StreamReader(p.getInputStream(), "OUTPUT");
		StreamReader r2 = new StreamReader(p.getErrorStream(), "ERROR");
		new Thread(r1).start();
		new Thread(r2).start();
	}

}
