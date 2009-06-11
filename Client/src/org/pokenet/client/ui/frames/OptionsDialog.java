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
	private HashMap<String, String> m_options;
	private Muffin m_muffin = new FileMuffin();

	private Button m_save;

	private CheckBox m_fullScreen;
	private CheckBox m_muteSound;
	private CheckBox m_disableMaps;

	// private SimpleColorPicker learnColor;

	public OptionsDialog() {
		m_options = GameClient.getOptions();
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		initGUI();
	}

	@Override
	public void setVisible(boolean state) {
		m_options = GameClient.getOptions();
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
			
			m_fullScreen = new CheckBox(translated.get(16));
			m_fullScreen.pack();
			m_fullScreen.setLocation(10, 10);

			m_fullScreen.setSelected(Boolean.parseBoolean(m_options
					.get("fullScreen")));
			getContentPane().add(m_fullScreen);
		}
		{
			m_muteSound = new CheckBox(translated.get(17));
			m_muteSound.pack();
			m_muteSound.setLocation(150, 10);

			m_muteSound.setSelected(Boolean.parseBoolean(m_options
					.get("soundMuted")));
			getContentPane().add(m_muteSound);
		}
		{
			m_disableMaps = new CheckBox(translated.get(48));
			m_disableMaps.pack();
			m_disableMaps.setLocation(10, 45);
			m_disableMaps.setSelected(!Boolean.parseBoolean(m_options.get("surroundingMaps")));
			getContentPane().add(m_disableMaps);
		}
		{
			m_save = new Button(translated.get(18));
			m_save.setSize(50, 25);
			m_save.setLocation(88, 74);
			getContentPane().add(m_save);

			m_save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						List<String> translated = Translator.translate("_GUI");
						/*
						 * options.remove("learnColor");
						 * options.put("learnColor",
						 * learnColor.getColorHexLabel(). getText());
						 */

						m_options.remove("fullScreen");
						m_options.put("fullScreen", Boolean.toString(m_fullScreen
								.isSelected()));

						m_options.remove("soundMuted");
						m_options.put("soundMuted", Boolean.toString(m_muteSound
								.isSelected()));
						
						m_options.remove("surroundingMaps");
						m_options.put("surroundingMaps", Boolean.toString(!m_disableMaps.isSelected()));
						GameClient.setLoadSurroundingMaps(!m_disableMaps.isSelected());
						
						if (m_muteSound.isSelected())
							GameClient.getSoundPlayer().mute(true); 
						else
							GameClient.getSoundPlayer().mute(false);
						
						m_muffin.saveFile(m_options, "options.dat");
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
		setSize(400, 128);
		setResizable(false);
		this.getTitleBar().getCloseButton().setVisible(false);
	}
}
