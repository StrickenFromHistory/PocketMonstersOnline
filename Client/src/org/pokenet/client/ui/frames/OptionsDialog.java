package org.pokenet.client.ui.frames;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.CheckBox;
import mdes.slick.sui.Frame;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.muffin.FileMuffin;
import org.newdawn.slick.muffin.Muffin;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.Translator;

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
		List<String> translated = Translator.translate("_GUI");
		setBackground(new Color(0, 0, 0, 70));
		{
			
			fullScreen = new CheckBox(translated.get(16));
			fullScreen.pack();
			fullScreen.setLocation(10, 10);

			fullScreen.setSelected(Boolean.parseBoolean(options
					.get("fullScreen")));
			getContentPane().add(fullScreen);
		}
		{
			muteSound = new CheckBox(translated.get(17));
			muteSound.pack();
			muteSound.setLocation(150, 10);

			muteSound.setSelected(Boolean.parseBoolean(options
					.get("soundMuted")));
			getContentPane().add(muteSound);
		}
		{
			save = new Button(translated.get(18));
			save.setSize(50, 25);
			save.setLocation(150, 45);
			getContentPane().add(save);

			save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						List<String> translated = Translator.translate("_GUI");
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
						
						if (muteSound.isSelected())
							GameClient.getSoundPlayer().muteAll(); 
						else
							GameClient.getSoundPlayer().unmuteAll();
						
						muffin.saveFile(options, "options.dat");
						GameClient
								.messageDialog(
										translated.get(19),
										getDisplay());
						GameClient.reloadOptions();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		setTitle(translated.get(15));
		setSize(400, 100);
		setResizable(false);
		this.getTitleBar().getCloseButton().setVisible(false);
	}
}
