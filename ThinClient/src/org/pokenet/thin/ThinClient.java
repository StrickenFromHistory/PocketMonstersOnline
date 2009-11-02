package org.pokenet.thin;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.pokenet.thin.libs.JGet;

import java.beans.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Dimension;

public class ThinClient extends JPanel implements ActionListener, PropertyChangeListener {

	private static final long serialVersionUID = 2718141354198299420L;
	private static JFrame m_masterFrame = new JFrame("Pokenet: Updating Ursaring");
	private JProgressBar m_progressBar;
	private JButton m_startButton;
	private JButton m_hideButton;
	private JTextArea m_taskOutput;
	private Task m_task;
	private Component m_output;
	private boolean m_showOutput = true;
	private static String m_mirror;
	private static int m_progressSize;
	private static double m_latestversion;
	private static ArrayList<UpgradeActionBean> m_updates = new ArrayList<UpgradeActionBean>();
	private static String m_installpath = "";

	class Task extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			int progress = 0;
			//Initialize progress property.
			setProgress(0);
			for (int i =0;i<m_updates.size();i++) {
				try{
					UpgradeActionBean uab = m_updates.get(i);
					uab.setOutput(uab.getOutput().replace("+",""));
					System.out.println(m_mirror+uab.getInput()+" "+uab.getOutput()+uab.getChecksum());
					if(uab.getChecksum().equals("mkdir"))
						new File(uab.getOutput()).mkdir();
					else{
						JGet.getFile(m_mirror+uab.getInput(),uab.getOutput());
					}		
					progress+=m_progressSize;
					setProgress(Math.min(progress, 100));
					m_progressBar.setValue(progress);
					m_taskOutput.append(String.format(
							"Completed %d%% of task.\n", m_task.getProgress()));
				}catch (IOException e) {
					// Impossible to open or save file
					e.printStackTrace();
					JOptionPane.showMessageDialog(
							m_masterFrame,
							"There seems to be an issue saving files. \nCould you check that one for us and try again?",
							"Pokenet Install System",
							JOptionPane.WARNING_MESSAGE);
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
				m_taskOutput.append(String.format(
						"Completed %d%% of task.\n", m_task.getProgress()));
			}
			/**
			 * Update version.txt to latest. 
			 */

			try {
				File f = new File(m_installpath+"res/.version");
				if(f.exists())
					f.delete();
				PrintWriter pw;
				pw = new PrintWriter(f);
				pw.println(m_latestversion+"");
				pw.flush();
				pw.close();
			} catch (FileNotFoundException e1) {

			}
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
			Process p = Runtime.getRuntime().exec("java -jar Pokenet.jar");
			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(p.getInputStream()));

			// read the output from the command

			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
					m_masterFrame,
					"Ouch! Something happened and we couldn't run the game. \nMaybe it didn't install properly?",
					"Pokenet Install System",
					JOptionPane.WARNING_MESSAGE);
		}
		System.exit(0);
	}

	public ThinClient() {
		super(new BorderLayout());

		//Create the demo's UI.
		if(!m_installpath.equals(""))
			m_startButton = new JButton("Install");
		else
			m_startButton = new JButton("Update");
		m_startButton.setActionCommand("update");
		m_startButton.addActionListener(this);

		m_hideButton = new JButton("Hide Details...");
		m_hideButton.setActionCommand("hide");
		m_hideButton.addActionListener(this);

		m_progressBar = new JProgressBar(0, 100);
		m_progressBar.setValue(0);
		m_progressBar.setStringPainted(true);

		m_taskOutput = new JTextArea(5, 20);
		m_taskOutput.setMargin(new Insets(5,5,5,5));
		m_taskOutput.setEditable(false);
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
				m_masterFrame.setSize(new Dimension(300, 220));
				m_showOutput=true;
			}else{
				m_output.setVisible(false);
				m_hideButton.setText("Show Details...");
				m_masterFrame.setSize(new Dimension(300, 130));
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
			//            taskOutput.append(String.format(
			//                    "Completed %d%% of task.\n", task.getProgress()));
		} 
	}


	/**
	 * Create the GUI and show it. As with all GUI code, this must run
	 * on the event-dispatching thread.
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
				/**
				 *  Connect to Updates
				 */
				try {
					ArrayList<String> updateSites = new ArrayList<String>();
					FileInputStream mirrorstream = new FileInputStream(m_installpath+"res/.mirrors");

					// Get the object of DataInputStream
					DataInputStream mirrorin = new DataInputStream(mirrorstream);
					BufferedReader mirrorbr = new BufferedReader(new InputStreamReader(mirrorin));

					String strLine;
					//Read File Line By Line
					while ((strLine = mirrorbr.readLine()) != null)   {
						updateSites.add(strLine); //Add mirrors to list
					}
					/**
					 *  Check Updates
					 */
					//Pick mirror at random
					m_mirror = updateSites.get(new Random().nextInt(updateSites.size()));
					mirrorin.close();

				} catch (FileNotFoundException e) {
					// File Not Found
					System.out.println("File not found. Using emergency mirror");
					m_mirror = "http://189.160.195.95/pokenet/";
				} catch (IOException e) {
					// Error reading file
					System.out.println("Error reading file. Using emergency mirror");
					m_mirror = "http://189.160.195.95/pokenet/";
				}
				// Check current version
				double version = 0.0;
				try{
					String strLine;
					FileInputStream versionstream = new FileInputStream(m_installpath+"res/.version");
					DataInputStream versionin = new DataInputStream(versionstream);
					BufferedReader versionbr = new BufferedReader(new InputStreamReader(versionin));
					if ((strLine = versionbr.readLine()) != null)   {
						version = Double.parseDouble(strLine); // Get current version
					}
					versionin.close();
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


				try{
					URL site = new URL(m_mirror+"latest");
					String inputLine;
					BufferedReader updateSite = new BufferedReader(new InputStreamReader(site.openStream()));
					while ((inputLine = updateSite.readLine()) != null){
						//Check latest version
						if(inputLine.contains("v")){
							inputLine = inputLine.replace("v","");
							try{
								m_latestversion = Double.parseDouble(inputLine);
							}catch(Exception e){}//Perhaps its badly formatted?

							if(version < m_latestversion){ //Time to update!
								//Check mirror contents

								try {
									site = new URL(m_mirror+"updates.txt");
									updateSite = new BufferedReader(new InputStreamReader(site.openStream()));
									inputLine = "";
									int answer = 3;
									JFrame frame = new JFrame("Pokenet Update System");
									while ((inputLine = updateSite.readLine()) != null){
										//Check latest version
										if(inputLine.contains("--v")){
											inputLine = inputLine.replaceAll("-","");
											inputLine = inputLine.replace("v","");
											try{
												m_latestversion = Double.parseDouble(inputLine);
											}catch(Exception e){}//Perhaps its badly formatted?
											
											if(version < m_latestversion){ //Time to update!
												/**
												 *  Ask user to update
												 */

												System.out.println("Reading list v"+Double.parseDouble(inputLine));
												/**
												 *  Download NEW Content
												 */

												while(((inputLine = updateSite.readLine()) != null ) && !inputLine.equals("-EOF-")){
													System.out.println(inputLine);
													if(!inputLine.startsWith("-")){
														String[] inout = inputLine.split("\\|");
														m_updates.add(new UpgradeActionBean(inout[1].trim().replace(" ","\\ "), m_installpath+inout[2].trim().replace(" ","\\ "), inout[3].trim()));
														m_progressSize++;
													}
												}
											}
										}	
									}
									System.out.println(m_installpath);
									if(m_installpath.equals(""))
										if(version<m_latestversion){
											answer = JOptionPane.showConfirmDialog(
													frame,
													"There is a Pokenet Update. \nWould you like to Update?\n(You won't be able to play unless you do)\nCurrent version: v"+version+"\nLatest version: v"+m_latestversion,
													"Pokenet Update System",
													JOptionPane.YES_NO_OPTION);
										}else{
											answer=0;
										}
									else
										answer=0;

									if(answer==0){
										if(m_progressSize!=0){
											m_progressSize = 100/m_progressSize;
											createAndShowGUI();
										}else{
											LaunchPokenet();
										}
									}else{
										System.exit(0);
									}
									frame= null;
								} catch (MalformedURLException e1) {
									JOptionPane.showMessageDialog(
											m_masterFrame,
											"Ouch! Something happened and we couldn't upgrade. \nTry again later?",
											"Pokenet Install System",
											JOptionPane.WARNING_MESSAGE);
								} catch (IOException e) {
									JOptionPane.showMessageDialog(
											m_masterFrame,
											"Ouch! Something happened and we couldn't upgrade. \nTry again later?",
											"Pokenet Install System",
											JOptionPane.WARNING_MESSAGE);
								}
							}else{
								//Time to play
								LaunchPokenet();
							}
						}
					}
				}catch(Exception e){
					JOptionPane.showMessageDialog(
							m_masterFrame,
							"Ouch! Something happened and we couldn't upgrade. \nTry again later?",
							"Pokenet Install System",
							JOptionPane.WARNING_MESSAGE);
				}



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

	public String getM_installpath() {
		return m_installpath;
	}

	public void setM_installpath(String mInstallpath) {
		m_installpath = mInstallpath;
	}

}
