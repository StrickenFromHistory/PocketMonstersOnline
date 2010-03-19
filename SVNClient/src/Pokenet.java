import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.tmatesoft.svn.cli.SVNCommandUtil;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;



public class Pokenet extends JFrame  implements Runnable {
	public static final String SVN_URL = "pokenet-release.svn.sourceforge.net/svnroot/pokenet-release";
	public static final String FOLDER_NAME = "pokenet-release";

	JTextArea outText;
	JScrollPane scrollPane;
	
	public Pokenet() {
		super("PokeNet Game Launcher and Updater");
		
		
		/* Center the updater */
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((d.getWidth() / 2) - this.getWidth() / 2);
		int y = (int) ((d.getHeight() / 2) - this.getHeight() / 2);
		this.setLocation(x, y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setSize(740, 450);
		this.setResizable(false);
		
		outText = new JTextArea();
		outText.setEditable(false);
		outText.append("Console Information:\n");
		
		scrollPane = new JScrollPane(outText);

		
		ImageIcon m_logo;
		JLabel l = null;
		System.out.println("5...");

		try {
			m_logo = new ImageIcon(new URL("http://pokedev.org/header.png"));
			l = new JLabel(m_logo);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.out.println("4...");

		
		l.setBounds(0,0,740,190);
		outText.setBounds(0, 190, 740, 440-190);
		scrollPane.setBounds(0,190,740,450-190);
		System.out.println("3...");

//		Insets insets = this.getInsets();
//        Dimension size = l.getPreferredSize();
//        l.setBounds(insets.left,insets.top,
//                     size.width, size.height);
//        size = outText.getPreferredSize();
//        outText.setBounds(insets.left, 190 + insets.top,
//                     size.width, size.height - 190);
//		System.out.println("2...");

        this.add(l);
		this.add(scrollPane);
		System.out.println("Starting...");
		this.setVisible(true);
		
//		FSRepositoryFactory.setup(); // for local access (file protocol). 
//		SVNRepositoryFactoryImpl.setup(); // for svn(+ssh) protocol 
		
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
//		String command;
		
			ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		
			 /* 
		     * Creates an instance of SVNClientManager providing authentication 
		     * information (name, password) and an options driver 
		     */ 
			SVNClientManager ourClientManager = SVNClientManager.newInstance(options); 
		    SVNUpdateClient updateClient = ourClientManager.getUpdateClient(); 

		    /* 
		     * sets externals not to be ignored during the checkout 
		     */ 
		    updateClient.setIgnoreExternals(false); 
			
		    /* 
		     * A url of a repository to check out 
		     */ 
		    SVNURL url = null;
			try {
				url = SVNURL.parseURIDecoded("http://" + SVN_URL);
			} catch (SVNException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} 
		    /* 
		     * A revision to check out 
		     */ 
		    SVNRevision revision = SVNRevision.HEAD; 

		    /* 
		     * A revision for which you're sure that the url you specify is 
		     * exactly what you need 
		     */ 
		    SVNRevision pegRevision = SVNRevision.HEAD; 

		    /* 
		     * Set whether a checkout is recursive or not 
		     */ 
		    boolean isRecursive = true; 

		    /* 
		     * A local path where a Working Copy will be ckecked out 
		     */ 
		    File destPath = new File(FOLDER_NAME); 

		    /* 
		     * returns the number of the revision at which the working copy is 
		     */ 
		    try {
				
				boolean exists = destPath.exists();
				if(!exists) {
					this.outText.append("Installing...\n Please be patient while PokeNet is downloaded...\n");
					System.out.println("Installing...");
					updateClient.doCheckout(url, destPath, pegRevision, 
                            revision, isRecursive);

				} else {
					this.outText.append("Updating...\n");
					System.out.println("Updating...\n");
					updateClient.doUpdate(destPath, revision, true);
				}
		    } catch (SVNException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 

	        
	        
			
		
		
		  try { 

		                
		    } catch (Exception e) { 
		        e.toString(); 
		    }

		
//		try {
//			svn = Runtime.getRuntime().exec(command);
//			StreamReader sr = new StreamReader(svn.getInputStream(), "", outText);
//			t = new Thread(sr);
//			t.start();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		
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
		String curDir = System.getProperty("user.dir");

		Process p = Runtime.getRuntime().exec("java -Dres.path="+ curDir + FOLDER_NAME+"/"
				+ " -Djava.library.path="+FOLDER_NAME+"/lib/native " +
		"-Xmx512m -Xms512m -jar ./"+ curDir + FOLDER_NAME+"/Pokenet.jar");
		StreamReader r1 = new StreamReader(p.getInputStream(), "OUTPUT");
		StreamReader r2 = new StreamReader(p.getErrorStream(), "ERROR");
		new Thread(r1).start();
		new Thread(r2).start();
	}
 
}
