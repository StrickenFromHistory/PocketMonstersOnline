package org.pokenet.updater;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNEventAdapter;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ThinClient extends JPanel implements ActionListener, PropertyChangeListener {

	private static final long serialVersionUID = 2718141354198299420L;

	public static final String SVN_URL = "pokenet-release.svn.sourceforge.net/svnroot/pokenet-release";
	public static final String FOLDER_NAME = "Pokenet";
	private static JFrame m_masterFrame = new JFrame("Pokenet: Valiant Venonat");
	private JProgressBar m_progressBar;
	private JButton m_startButton;
	private JButton m_hideButton;
	private JTextArea m_taskOutput;
	private Task m_task;
	private Component m_output;
	private boolean m_showOutput = true;
	private static boolean m_isUpdate = false;
	private static float m_progressSize = 0;
	private static String m_installpath = "";

	class Task extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			//Initialize progress property.
			setProgress(0);
			DAVRepositoryFactory.setup();
			ISVNOptions options = SVNWCUtil.createDefaultOptions(true);

			/* 
			 * Creates an instance of SVNClientManager providing authentication 
			 * information (name, password) and an options driver 
			 */ 
			SVNClientManager ourClientManager = SVNClientManager.newInstance(options); 
			SVNUpdateClient updateClient = ourClientManager.getUpdateClient(); 

			/*
			 * Creates the event handler to display information
			 */
			ourClientManager.setEventHandler(
					new SVNEventAdapter(){
						public void handleEvent(SVNEvent event, double progress){
							SVNEventAction action = event.getAction();
							File curFile = event.getFile();

							String path = curFile.getAbsolutePath().substring(m_installpath.length()-1);

							if (action == SVNEventAction.ADD ||
									action == SVNEventAction.UPDATE_ADD){
								//			    					outText.append("Downloading " + event.getFile().getName() + '\n');
								m_taskOutput.append("Downloading " + path + " \n");

								m_taskOutput.setCaretPosition(m_taskOutput.getDocument().getLength());
								System.out.println("Downloading " + curFile.getName());
							} if (action == SVNEventAction.STATUS_COMPLETED ||
									action == SVNEventAction.UPDATE_COMPLETED){
								setProgress(100);
								m_taskOutput.append("Download completed. ");
								m_taskOutput.setCaretPosition(m_taskOutput.getDocument().getLength());
								System.out.println("Download completed. ");

							}
						}
					}
			);	    	


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
			 * A local path where a Working Copy will be ckecked out 
			 */ 
			File destPath = new File(m_installpath); 

			/* 
			 * returns the number of the revision at which the working copy is 
			 */ 
			try {

				m_taskOutput.append("Installing...\nPlease be patient while PokeNet is downloaded...\nThe progress bar is not functional at the moment. Sorry. ");
				System.out.println("Installing...");
				updateClient.doCheckout(url, destPath, pegRevision, 
						revision, SVNDepth.INFINITY, true);
				
			} catch (SVNException e1) {
				// It's probably locked, lets cleanup and resume. 
				try {
					ourClientManager.getWCClient().doCleanup(destPath);
					m_taskOutput.append("Resuming Download...\n");
					System.out.println("Resuming Download...\n");
					updateClient.doUpdate(destPath, revision, SVNDepth.INFINITY, true, true);
				} catch (SVNException e) {
					e.printStackTrace();
				}
			} 
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			if(getProgress()<100){
				setProgress(100);
				m_taskOutput.append("Download complete!");
				m_taskOutput.setCaretPosition(m_taskOutput.getDocument().getLength());
			}

			if(!m_installpath.equals("")){
				m_taskOutput.append("Setting up the install directory...");
				m_taskOutput.setCaretPosition(m_taskOutput.getDocument().getLength());

				String OS = System.getProperty("os.name");
				if(OS.contains("Windows")){
					try {
						File f = new File(System.getenv("APPDATA")+"/.pokenet");
						if(f.exists())
							f.delete();
						PrintWriter pw = new PrintWriter(f);
						pw.println(m_installpath);
						pw.flush();
						pw.close();
					}catch(Exception e){
						e.printStackTrace();
						JOptionPane.showInternalMessageDialog(
								m_masterFrame,
								"Hmm. Game installed, but we couldn't save the location.\nThis means that next time you run, you'll have to select the same installation directory.\nTry running this as admin?",
								"Pokenet Install System",
								JOptionPane.WARNING_MESSAGE);
					}

				}else if(OS.contains("Linux")){
					try {
						File f = new File(System.getenv("HOME")+"/.pokenet");
					if(f.exists())
						f.delete();
					PrintWriter pw = new PrintWriter(f);
					pw.println(m_installpath);
					pw.flush();
					pw.close();
					}catch(Exception e){
						e.printStackTrace();
						JOptionPane.showInternalMessageDialog(
								m_masterFrame,
								"Hmm. Game installed, but we couldn't save the location.\nThis means that next time you run, you'll have to select the same installation directory.\nPerhaps its a SELinux thing?",
								"Pokenet Install System",
								JOptionPane.WARNING_MESSAGE);
					}
				}else if(OS.contains("Mac")){ // Probably?
					try {
						File f = new File(System.getenv("user.home")+"/Library/Preferences/org.pokenet.updaterPrefs"); //Maybe. I don't know. 
					if(f.exists())
						f.delete();
					PrintWriter pw = new PrintWriter(f);
					pw.println(m_installpath);
					pw.flush();
					pw.close();
					}catch(Exception e){
						e.printStackTrace();
						JOptionPane.showInternalMessageDialog(
								m_masterFrame,
								"Hmm. Game installed, but we couldn't save the location.\nThis means that next time you run, you'll have to select the same installation directory.\nIt means we screwed up somewhere :P",
								"Pokenet Install System",
								JOptionPane.WARNING_MESSAGE);
					}
				}else{
					JOptionPane.showInternalMessageDialog(
							m_masterFrame,
							"Hmm. Game installed, but you're using an unsupported (by us, anyways) Operative System and we couldn't save the location.\nThis means that next time you run, you'll have to select the same installation directory.\nLet us know what OS you're running, so we can fix this.",
							"Pokenet Install System",
							JOptionPane.WARNING_MESSAGE);
				}


			}
			/**
			 * Update version.txt to latest. 
			 */

					int answer = JOptionPane.showConfirmDialog(
					m_masterFrame,
					"Your game has finished updating. \nWould you like to play?",
					"Pokenet Update System",
					JOptionPane.YES_NO_OPTION);
			Toolkit.getDefaultToolkit().beep();
			//            startButton.setEnabled(true);
			setCursor(null); //turn off the wait cursor
			if(answer==0){
				/**
				 *  Launch Jar
				 */
				LaunchPokenet();
			}else{
				System.exit(0);
			}
			//            taskOutput.append("Done!\n");
		}
	}

	public static void LaunchPokenet(){
		m_masterFrame.setVisible(false);
		try {
			String s;
			Process p = Runtime.getRuntime().exec("java -Dres.path="+m_installpath+" -Djava.library.path="+m_installpath+"lib/native -jar "+m_installpath+"Pokenet.jar");
			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(p.getErrorStream()));
			// read the output from the command

			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
			while ((s = stdError.readLine()) != null) {
				System.out.println("Error: "+s);
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showInternalMessageDialog(
					m_masterFrame,
					"Ouch! Something happened and we couldn't run the game. \nMaybe it didn't install properly?\nError: "+e.getLocalizedMessage(),
					"Pokenet Install System",
					JOptionPane.WARNING_MESSAGE);
		}
		System.exit(0);
	}

	public ThinClient() {
		super(new BorderLayout());

		//Create the demo's UI.
		if(!m_isUpdate)
			m_startButton = new JButton("Install Now!");
		else
			m_startButton = new JButton("Update Now!");
		m_startButton.setActionCommand("update");
		m_startButton.addActionListener(this);

		m_hideButton = new JButton("Hide Details...");
		m_hideButton.setActionCommand("hide");
		m_hideButton.addActionListener(this);
		m_hideButton.setEnabled(false);

		m_progressBar = new JProgressBar(0, 100);
		m_progressBar.setValue(0);
		m_progressBar.setStringPainted(true);

		m_taskOutput = new JTextArea(10, 40);
		m_taskOutput.setMargin(new Insets(5,5,5,5));
		m_taskOutput.setEditable(false);
		m_taskOutput.setAutoscrolls(true);
		m_output = new JScrollPane(m_taskOutput);


		JPanel panel = new JPanel();
		panel.add(m_startButton);
		panel.add(m_progressBar);

		JPanel panel2 = new JPanel();
		panel2.add(m_hideButton);

		add(panel, BorderLayout.NORTH);
		add(panel2, BorderLayout.CENTER);
		add(m_output, BorderLayout.SOUTH);

		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			SwingUtilities.updateComponentTreeUI(this);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Invoked when the user presses the start button.
	 */
	public void actionPerformed(ActionEvent evt) {
		if(evt.getActionCommand().equals("update")){
			m_startButton.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			//Instances of javax.swing.SwingWorker are not reusuable, so
			//we create new instances as needed.
			m_task = new Task();
			m_task.addPropertyChangeListener(this);
			m_task.execute();
		}else if(evt.getActionCommand().equals("hide")){
			if(!m_showOutput){
				m_output.setVisible(true);
				m_hideButton.setText("Hide Details...");
				m_masterFrame.setSize(new Dimension(400, 220));
				m_showOutput=true;
			}else{
				m_output.setVisible(false);
				m_hideButton.setText("Show Details...");
				m_masterFrame.setSize(new Dimension(400, 130));
				m_showOutput=false;
			}
		}
	}

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			m_progressBar.setValue(progress);
		} 
	}


	/**
	 * Create the GUI and show it. As with all GUI code, this must run
	 * on the event-dispatching thread.
	 * @param mProgressSize 
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		m_masterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		m_masterFrame.setSize(new Dimension(282, 242));
		//Create and set up the content pane.
		JComponent newContentPane = new ThinClient();
		newContentPane.setOpaque(true); //content panes must be opaque
		m_masterFrame.setContentPane(newContentPane);

		//Display the window.
		m_masterFrame.pack();
		centerTheGUIFrame(m_masterFrame);
		m_masterFrame.setVisible(true);

	}

	public static void main(String[] args) {
		runApp();
	}

	public static void runApp(){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				String OS = System.getProperty("os.name");
				String path = "";
				if(OS.contains("Windows")){
					path = System.getenv("APPDATA")+"\\.pokenet";
				}else if(OS.contains("Linux")){
					path =  System.getenv("HOME")+"/.pokenet";
				}else if(OS.contains("Mac")){ // Probably?
						path = System.getenv("user.home")+"/Library/Preferences/org.pokenet.updaterPrefs"; //Maybe. I don't know. 
				}
				if(!path.equals("")){
					BufferedReader br = null;
					try
					{
						br = new BufferedReader(new FileReader(path));
						m_installpath = br.readLine();
					}catch(Exception e){
						int answer = JOptionPane.showConfirmDialog(
								m_masterFrame,
								"Would you like to install this game?",
								"Pokenet Update System",
								JOptionPane.YES_NO_OPTION);
						Toolkit.getDefaultToolkit().beep();
						if(answer==0){
							JFileChooser fc = new JFileChooser();
							fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							int returnVal = fc.showDialog(m_masterFrame, "Install here");
							if (returnVal == JFileChooser.APPROVE_OPTION) {
								File file = fc.getSelectedFile();
								try {
									m_installpath = file.getCanonicalPath()+"/";
									createAndShowGUI();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}else{
								JOptionPane.showMessageDialog(
										m_masterFrame,
										"Thanks for choosing us!",
										"Pokenet Install System",
										JOptionPane.INFORMATION_MESSAGE);
								System.exit(0);
							}
						}else{
							JOptionPane.showMessageDialog(
									m_masterFrame,
									"Thanks for choosing us!",
									"Pokenet Install System",
									JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						}

					}
				}
				/**
				 *  Check Updates
				 */
				m_isUpdate = true;
				createAndShowGUI();
//				DAVRepositoryFactory.setup();
//				ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
//			
//				 /* 
//			     * Creates an instance of SVNClientManager providing authentication 
//			     * information (name, password) and an options driver 
//			     */ 
//				SVNClientManager ourClientManager = SVNClientManager.newInstance(options); 
//			    SVNUpdateClient updateClient = ourClientManager.getUpdateClient(); 
//
//			    /*
//			     * Creates the event handler to display information
//			     */
//			    ourClientManager.setEventHandler(
//			    		new SVNEventAdapter(){
//			    			public void handleEvent(SVNEvent event, double progress){
//			    				SVNEventAction action = event.getAction();
//			    				File curFile = event.getFile();
//			    				String curDir = System.getProperty("user.dir");
//			    				
//			    				String path = curFile.getAbsolutePath().substring(curDir.length());
//			    				
//								if (action == SVNEventAction.ADD ||
//										action == SVNEventAction.UPDATE_ADD){
////				    					outText.append("Downloading " + event.getFile().getName() + '\n');
//										
//										outText.append("Downloading " + path + '\n');
//									
//									outText.setCaretPosition(outText.getDocument().getLength());
//									System.out.println("Downloading " + curFile.getName());
//								} if (action == SVNEventAction.STATUS_COMPLETED ||
//										action == SVNEventAction.UPDATE_COMPLETED){
//									outText.append("Download completed. Launching client!");
//									outText.setCaretPosition(outText.getDocument().getLength());
//									System.out.println("Download completed. Launching client!");
//								}
//			    			}
//			    		}
//			    );	    	
//
//			    
//			    /* 
//			     * sets externals not to be ignored during the checkout 
//			     */ 
//			    updateClient.setIgnoreExternals(false); 
//				
//			    /* 
//			     * A url of a repository to check out 
//			     */ 
//			    SVNURL url = null;
//				try {
//					url = SVNURL.parseURIDecoded("http://" + SVN_URL);
//				} catch (SVNException e2) {
//					e2.printStackTrace();
//				} 
//			    /* 
//			     * A revision to check out 
//			     */ 
//			    SVNRevision revision = SVNRevision.HEAD; 
//
//			    /* 
//			     * A revision for which you're sure that the url you specify is 
//			     * exactly what you need 
//			     */ 
//			    SVNRevision pegRevision = SVNRevision.HEAD; 
//
//			    /* 
//			     * A local path where a Working Copy will be ckecked out 
//			     */ 
//			    File destPath = new File(FOLDER_NAME); 
//
//				ourClientManager.getWCClient().doCleanup(destPath);
//				m_taskOutput.append("Updating...\n");
//				System.out.println("Updating...\n");
//				updateClient.doUpdate(destPath, revision, SVNDepth.INFINITY, true, true);
			}
		});
	}
	/**
	 * This method is used to center the GUI
	 * @param frame - Frame that needs to be centered.
	 */
	public static void centerTheGUIFrame(JFrame frame) {
		int widthWindow = frame.getWidth();
		int heightWindow = frame.getHeight();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int X = (screen.width / 2) - (widthWindow / 2); // Center horizontally.
		int Y = (screen.height / 2) - (heightWindow / 2); // Center vertically.

		frame.setBounds(X, Y, widthWindow, heightWindow);

	}

	public static float getProgressSize() {
		return m_progressSize;
	}

	public static void setProgressSize(float f) {
		m_progressSize = f;
	}

	public String getInstallpath() {
		return m_installpath;
	}

	public void setInstallpath(String mInstallpath) {
		m_installpath = mInstallpath;
	}

}
