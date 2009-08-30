package org.pokenet.server;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.pokenet.server.libs.JGet;

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
	private static JFrame masterFrame = new JFrame("Pokenet: Updating Ursaring");
	private JProgressBar progressBar;
	private JButton startButton;
	private JButton hideButton;
	private JTextArea taskOutput;
	private Task task;
	private Component output;
	private boolean showOutput = true;
	private static String mirror;
	private static int progressSize;
	private static double latestversion;
	private static HashMap<String, String> updates = new HashMap<String, String>();

	class Task extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			int progress = 0;
			//Initialize progress property.
			setProgress(0);
			for (Map.Entry<String, String> entry : updates.entrySet()) {
				try{
					JGet.getFile(mirror+entry.getKey(),entry.getValue());
					progress+=progressSize;
					setProgress(Math.min(progress, 100));
					progressBar.setValue(progress);
					taskOutput.append(String.format(
							"Completed %d%% of task.\n", task.getProgress()));
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
				taskOutput.append(String.format(
						"Completed %d%% of task.\n", task.getProgress()));
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
				pw.println(latestversion+"");
				pw.flush();
				pw.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			int answer = JOptionPane.showConfirmDialog(
					masterFrame,
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
		masterFrame.setVisible(false);
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
		startButton = new JButton("Update");
		startButton.setActionCommand("update");
		startButton.addActionListener(this);

		hideButton = new JButton("Hide Details...");
		hideButton.setActionCommand("hide");
		hideButton.addActionListener(this);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		taskOutput = new JTextArea(5, 20);
		taskOutput.setMargin(new Insets(5,5,5,5));
		taskOutput.setEditable(false);
		output = new JScrollPane(taskOutput);


		JPanel panel = new JPanel();
		panel.add(startButton);
		panel.add(progressBar);

		JPanel panel2 = new JPanel();
		panel2.add(hideButton);

		add(panel, BorderLayout.NORTH);
		add(panel2, BorderLayout.CENTER);
		add(output, BorderLayout.SOUTH);

		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

	}

	/**
	 * Invoked when the user presses the start button.
	 */
	public void actionPerformed(ActionEvent evt) {
		if(evt.getActionCommand().equals("update")){
			startButton.setEnabled(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			//Instances of javax.swing.SwingWorker are not reusuable, so
			//we create new instances as needed.
			task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();
		}else if(evt.getActionCommand().equals("hide")){
			if(!showOutput){
				output.setVisible(true);
				hideButton.setText("Hide Details...");
				masterFrame.setSize(new Dimension(300, 220));
				showOutput=true;
			}else{
				output.setVisible(false);
				hideButton.setText("Show Details...");
				masterFrame.setSize(new Dimension(300, 130));
				showOutput=false;
			}
		}
	}

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
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

		masterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		masterFrame.setSize(new Dimension(282, 242));
		//Create and set up the content pane.
		JComponent newContentPane = new ThinClient();
		newContentPane.setOpaque(true); //content panes must be opaque
		masterFrame.setContentPane(newContentPane);

		//Display the window.
		masterFrame.pack();
		centerTheGUIFrame(masterFrame);
		masterFrame.setVisible(true);

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
					mirror = updateSites.get(new Random().nextInt(updateSites.size()));

					//Check mirror contents
					URL site = new URL(mirror+"updates.txt");
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
								latestversion = Double.parseDouble(inputLine);
							}catch(Exception e){}//Perhaps its badly formatted?


							if(version < latestversion){ //Time to update!
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
										updates.put(inout[0], inout[1]);
										progressSize++;
									}
								}
							}
						}	
					}
					if(version<latestversion){
						answer = JOptionPane.showConfirmDialog(
								frame,
								"There is a Pokenet Update. \nWould you like to Update?\n(You won't be able to play unless you do)\nCurrent version: v"+version+"\nLatest version: v"+latestversion,
								"Pokenet Update System",
								JOptionPane.YES_NO_OPTION);
					}else{
						answer=0;
					}

					if(answer==0){
						if(progressSize!=0){
							progressSize = 100/progressSize;
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
