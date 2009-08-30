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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.awt.Dimension;

public class ThinClient extends JPanel
implements ActionListener, 
PropertyChangeListener {

	/**
	 * 
	 */
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
	private static HashMap<String, String> m_updates = new HashMap<String, String>();

	class Task extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			int progress = 0;
			//Initialize progress property.
			setProgress(0);
			for (Map.Entry<String, String> entry : m_updates.entrySet()) {
				try{
					JGet.getFile(m_mirror+entry.getKey(),entry.getValue());
					progress+=m_progressSize;
					setProgress(Math.min(progress, 100));
					m_progressBar.setValue(progress);
					m_taskOutput.append(String.format(
							"Completed %d%% of task.\n", m_task.getProgress()));
				}catch(FileNotFoundException e){
					// Add "File not found" to the not found array
				} catch (MalformedURLException e) {
					// Bad URL
					e.printStackTrace();
				} catch (IOException e) {
					// Impossible to open or save file
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
				m_taskOutput.append(String.format(
						"Completed %d%% of task.\n", m_task.getProgress()));
			}
			/**
			 * Update version.txt to latest. 
			 */

			try {
				File f = new File("res/.version");
				if(f.exists())
					f.delete();
				PrintWriter pw;
				pw = new PrintWriter(f);
				pw.println(m_latestversion+"");
				pw.flush();
				pw.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
			Process p = Runtime.getRuntime().exec("java -jar Test.jar");
			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(p.getInputStream()));

			// read the output from the command

			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	public ThinClient() {
		super(new BorderLayout());

		//Create the demo's UI.
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
				/**
				 *  Connect to Updates
				 */
				try {
					ArrayList<String> updateSites = new ArrayList<String>();
					FileInputStream mirrorstream = new FileInputStream("res/.mirrors");

					// Get the object of DataInputStream
					DataInputStream mirrorin = new DataInputStream(mirrorstream);
					BufferedReader mirrorbr = new BufferedReader(new InputStreamReader(mirrorin));

					String strLine;
					//Read File Line By Line
					while ((strLine = mirrorbr.readLine()) != null)   {
						updateSites.add(strLine); //Add mirrors to list
					}

					// Check current version
					double version = 0.0;
					try{
						FileInputStream versionstream = new FileInputStream("res/.version");
						DataInputStream versionin = new DataInputStream(versionstream);
						BufferedReader versionbr = new BufferedReader(new InputStreamReader(versionin));
						if ((strLine = versionbr.readLine()) != null)   {
							version = Double.parseDouble(strLine); // Get current version
						}
						versionin.close();
					}catch(Exception e){
						//Use default version. 0.0. 
					}

					/**
					 *  Check Updates
					 */
					//Pick mirror at random
					m_mirror = updateSites.get(new Random().nextInt(updateSites.size()));

					//Check mirror contents
					URL site = new URL(m_mirror+"updates.txt");
					mirrorbr = new BufferedReader(new InputStreamReader(site.openStream()));

					String inputLine;
					int answer = 3;
					JFrame frame = new JFrame("Pokenet Update System");
					JFrame.setDefaultLookAndFeelDecorated(true);
					while ((inputLine = mirrorbr.readLine()) != null){
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

								while(((inputLine = mirrorbr.readLine()) != null ) && !inputLine.equals("-EOF-")){
									System.out.println(inputLine);
									if(!inputLine.startsWith("-")){
										String[] inout = inputLine.split(" ");
										m_updates.put(inout[0], inout[1]);
										m_progressSize++;
									}
								}
							}
						}	
					}
					if(version<m_latestversion){
						answer = JOptionPane.showConfirmDialog(
								frame,
								"There is a Pokenet Update. \nWould you like to Update?\n(You won't be able to play unless you do)\nCurrent version: v"+version+"\nLatest version: v"+m_latestversion,
								"Pokenet Update System",
								JOptionPane.YES_NO_OPTION);
					}else{
						answer=0;
					}

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
					mirrorin.close();

				} catch (FileNotFoundException e) {
					// File Not Found
					System.out.println("File not found"+"\n");
				} catch (IOException e) {
					// Error reading file
					e.printStackTrace();
					System.out.println("Error reading file"+"\n");
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

}
