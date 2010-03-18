import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;




public class Pokenet extends JFrame  implements Runnable {
	public static final String SVN_URL = "http://pokenet-release.svn.sourceforge.net/svnroot/pokenet-release";

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
		
		System.out.println("Updating...");

		boolean exists = (new File(".client/")).exists();
		if(exists) {
			/*
			 * first time
			 *  svn co 
			 */
			try {
				Process svn = Runtime.getRuntime().exec("svn co " + SVN_URL, null, new File("./client"));
				StreamReader sr = new StreamReader(svn.getInputStream(), "", outText);
				new Thread(sr).start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
						
		} else {
			/*
			 * other
			 * svn up
			 */
			try {
				Process svn = Runtime.getRuntime().exec("svn up", null, new File("./client"));
				StreamReader sr = new StreamReader(svn.getInputStream(), "");
				new Thread(sr).start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
		}
		
		
		
		System.out.println("Launching...");

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
		Process p = Runtime.getRuntime().exec("java -Dres.path=client/"
				+ " -Djava.library.path=client/lib/native " +
		"-Xmx512m -Xms512m -jar ./client/client.jar");
		StreamReader r1 = new StreamReader(p.getInputStream(), "OUTPUT");
		StreamReader r2 = new StreamReader(p.getErrorStream(), "ERROR");
		new Thread(r1).start();
		new Thread(r2).start();
	}

}
