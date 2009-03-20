package org.pokenet.client.ui.frames;

import java.io.IOException;
import java.util.HashMap;

import mdes.slick.sui.Button;
import mdes.slick.sui.CheckBox;
import mdes.slick.sui.Frame;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.muffin.FileMuffin;
import org.newdawn.slick.muffin.Muffin;
import org.pokenet.client.GameClient;

public class OptionsDialog extends Frame {
	private HashMap<String, String> options;
	private Muffin muffin = new FileMuffin();

	private Button save;

	private CheckBox fullScreen;
	private CheckBox muteSound;

	// private SimpleColorPicker learnColor;

	public OptionsDialog() {
		options = GameClient.getOptions();

		initGUI();
	}

	@Override
	public void setVisible(boolean state) {
		options = GameClient.getOptions();
		super.setVisible(state);
	}

	public void initGUI() {
		/*
		 * { learnColor = new SimpleColorPicker(); try {
		 * learnColor.setSelectedColor(Color.decode(options.get("learnColor")));
		 * } catch (RuntimeException e) { e.printStackTrace(); }
		 * learnColor.setLocation(10, 70);
		 * 
		 * getContentPane().add(learnColor); }
		 */
		setBackground(new Color(0, 0, 0, 70));
		{
			fullScreen = new CheckBox("Full screen view");
			fullScreen.pack();
			fullScreen.setLocation(10, 10);

			fullScreen.setSelected(Boolean.parseBoolean(options
					.get("fullScreen")));
			getContentPane().add(fullScreen);
		}
		{
			muteSound = new CheckBox("Mute");
			muteSound.pack();
			muteSound.setLocation(150, 10);

			muteSound.setSelected(Boolean.parseBoolean(options
					.get("soundMuted")));
			getContentPane().add(muteSound);
		}
		{
			save = new Button("Save");
			save.setSize(50, 25);
			save.setLocation(150, 45);
			getContentPane().add(save);

			save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						/*
						 * options.remove("learnColor");
						 * options.put("learnColor",
						 * learnColor.getColorHexLabel(). getText());
						 */

						options.remove("fullScreen");
						options.put("fullScreen", Boolean.toString(fullScreen
								.isSelected()));

						options.remove("soundMuted");
						options.put("soundMuted", Boolean.toString(muteSound
								.isSelected()));
						/*
						 * if (muteSound.isSelected())
						 * GameClient.getSoundPlayer().muteAll(); else
						 * GameClient.getSoundPlayer().unmuteAll();
						 */
						muffin.saveFile(options, "options.dat");
						GameClient
								.messageDialog(
										"Changes will not take effect until you restart.",
										getDisplay());
						GameClient.reloadOptions();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		setTitle("Options");
		setSize(400, 100);
		setResizable(false);
		this.getTitleBar().getCloseButton().setVisible(false);
	}
}
