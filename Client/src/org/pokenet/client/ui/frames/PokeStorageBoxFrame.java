package org.pokenet.client.ui.frames;

import java.io.FileNotFoundException;
import java.io.InputStream;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.ToggleButton;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.FileLoader;
import org.pokenet.client.backend.entity.Pokemon;
import org.pokenet.client.ui.base.ComboBox;
import org.pokenet.client.ui.base.ProgressBar;

/**
 * Storage Box
 * 
 * @author ZombieBear
 * 
 */
public class PokeStorageBoxFrame extends Frame {
	private ToggleButton[] m_buttons = new ToggleButton[30];
	private int[] m_pokeNums = new int[30];
	private int m_buttonChosen = 0;
	private ComboBox m_changeBox;
	private Button m_switchPoke, m_close, m_release;
	private int m_boxNum, m_boxIndex;

	/**
	 * Default constructor
	 * 
	 * @param boxIndex
	 * @param pokes
	 * @throws SlickException
	 */
	public PokeStorageBoxFrame(int[] pokes){
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		m_pokeNums = pokes;
		m_boxIndex = 0;
		m_boxNum = m_boxIndex + 1;

		initGUI();

		setSize(231, 248);
		setLocation(400 - getWidth() / 2, 300 - getHeight() / 2);
		setTitle("Box Number " + String.valueOf(m_boxNum));
		getTitleBar().getCloseButton().setVisible(false);
		setResizable(false);
		setVisible(true);
	}

	/**
	 * Loads pokemon images in buttons
	 */
	public void loadImages() {
		LoadingList.setDeferredLoading(true);
		InputStream f;
		for (int i = 0; i <= 29; i++) {
			m_buttons[i].setImage(null);
			try {
				if(m_pokeNums[i] >= 0) {
					f = FileLoader.loadFile(Pokemon
							.getIconPathByIndex(m_pokeNums[i] + 1)); 
					m_buttons[i].setImage(new Image(f, "boxPoke" + i + " " + Pokemon
							.getIconPathByIndex(m_pokeNums[i] + 1), false));
				}
			} catch (SlickException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		LoadingList.setDeferredLoading(false);
	}

	/**
	 * Toggles the chosen button and untoggles the others
	 * 
	 * @param x
	 */
	public void setChoice(int x) {
		untoggleButtons();
		m_buttons[x].setSelected(true);
		m_switchPoke.setEnabled(true);
		m_release.setEnabled(true);
		m_buttonChosen = x;
		m_boxIndex = x;
	}

	/**
	 * Initializes the interface
	 */
	public void initGUI() {
		int buttonX = 7;
		int buttonY = 5;
		int buttonCount = 0;

		for (int i = 0; i <= 29; i++) {
			m_buttons[i] = new ToggleButton();
			final int j = i;
			m_buttons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					setChoice(j);
				}
			});
			m_buttons[i].setSize(32, 32);
		}

		for (int row = 0; row < 5; row++) {
			for (int column = 0; column < 6; column++) {
				m_buttons[buttonCount].setLocation(buttonX, buttonY);
				buttonX += 37;
				buttonCount += 1;
			}
			buttonX = 7;
			buttonY += 37;
		}

		for (int i = 0; i <= 29; i++) {
			add(m_buttons[i]);
		}

		m_switchPoke = new Button();
		m_close = new Button();
		m_changeBox = new ComboBox();
		m_release = new Button();

		m_switchPoke.setText("Switch");
		m_switchPoke.pack();
		m_switchPoke.setLocation(5, 192);
		m_switchPoke.setEnabled(false);
		m_switchPoke.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
				TeamForBox teamPanel = new TeamForBox(m_boxNum, m_buttonChosen);
				getDisplay().add(teamPanel);
				teamPanel.setLocation(getDisplay().getWidth() / 2
						- teamPanel.getWidth() / 2, getDisplay().getHeight()
						/ 2 - teamPanel.getHeight() / 2);
			}
		});

		m_changeBox.addElement("Box 1");
		m_changeBox.addElement("Box 2");
		m_changeBox.addElement("Box 3");
		m_changeBox.addElement("Box 4");
		m_changeBox.addElement("Box 5");
		m_changeBox.addElement("Box 6");
		m_changeBox.addElement("Box 7");
		m_changeBox.addElement("Box 8");
		m_changeBox.addElement("Box 9");
		
		m_changeBox.setSize(55, 15);
		m_changeBox.setLocation(m_switchPoke.getX() + m_switchPoke.getWidth(),
				197);

		m_release.setText("Release");
		m_release.pack();
		m_release.setLocation(m_changeBox.getX() + m_changeBox.getWidth(), 192);
		m_release.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);

				final Frame confirm = new Frame("Release");
				confirm.getCloseButton().setVisible(false);

				confirm.setResizable(false);
				confirm.setSize(370, 70);
				confirm.setLocationRelativeTo(null);
				Label yousure = new Label(
						"Are you sure you want to release your Pokemon?");
				yousure.pack();
				Button yes = new Button("Release");
				yes.pack();
				yes.setLocation(0, confirm.getHeight()
						- confirm.getTitleBar().getHeight() - yes.getHeight());
				yes.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						confirm.setVisible(false);
						getDisplay().remove(confirm);

						GameClient.getInstance().getPacketGenerator().writeTcpMessage(
								"BR" + m_boxIndex + "," + m_buttonChosen);

						GameClient.getInstance().getPacketGenerator().writeTcpMessage("Bf");
						GameClient.getInstance().getUi().stopUsingBox();
					}
				});
				Button no = new Button("Keep");
				no.pack();
				no.setLocation(yes.getWidth(), confirm.getHeight()
						- confirm.getTitleBar().getHeight() - no.getHeight());
				no.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						confirm.setVisible(false);
						getDisplay().remove(confirm);

						GameClient.getInstance().getPacketGenerator().writeTcpMessage("Bf");
						GameClient.getInstance().getUi().stopUsingBox();

					}
				});
				confirm.getContentPane().add(yousure);
				confirm.getContentPane().add(yes);
				confirm.getContentPane().add(no);

				getDisplay().add(confirm);
			}
		});
		m_release.setEnabled(false);

		m_close.setText("Bye");
		m_close.pack();
		m_close.setLocation(m_release.getX() + m_release.getWidth(), 192);
		m_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
				GameClient.getInstance().getPacketGenerator().writeTcpMessage("Bf");
				GameClient.getInstance().getUi().stopUsingBox();
			}
		});

		add(m_switchPoke);
		add(m_close);
		add(m_changeBox);
		add(m_release);
		loadImages();
	}

	/**
	 * Untoggles all buttons
	 */
	public void untoggleButtons() {
		for (int i = 0; i <= 29; i++) {
			m_buttons[i].setSelected(false);
		}
	}

	/**
	 * Disables all buttons
	 */
	public void disableButtons() {
		for (int i = 0; i <= 29; i++) {
			m_buttons[i].setEnabled(false);
			m_switchPoke.setEnabled(false);
			m_close.setEnabled(false);
			m_changeBox.setEnabled(false);
			m_release.setEnabled(false);
		}
	}

	/**
	 * Enables all buttons
	 */
	public void enableButtons() {
		for (int i = 0; i <= 29; i++) {
			m_buttons[i].setEnabled(true);
			m_switchPoke.setEnabled(true);
			m_close.setEnabled(true);
			m_changeBox.setEnabled(true);
			m_release.setEnabled(true);
		}
	}

	/**
	 * Changes the box
	 * 
	 * @param boxNum
	 */
	public void changeBox(int[] pokes) {
		m_pokeNums = pokes;
		loadImages();
		enableButtons();
	}
	
	@Override
	public void update(GUIContext container, int delta){
		super.update(container, delta);
		if (m_changeBox.getSelectedIndex() != m_boxIndex){
			m_boxIndex = m_changeBox.getSelectedIndex();
			m_boxNum = m_boxIndex + 1;
			disableButtons();
			GameClient.getInstance().getPacketGenerator().writeTcpMessage("Br" + (m_boxIndex));
			setTitle("Box Number " + String.valueOf(m_boxNum));
		}
	}
}

/**
 * Team panel for storage purposes
 * @author ZombieBear
 *
 */
class TeamForBox extends Frame {
	Container[] m_pokes = new Container[6];
	ToggleButton[] m_pokeIcon = new ToggleButton[6];
	Label[] m_pokeName = new Label[6];
	Label[] m_level = new Label[6];
	ProgressBar[] m_hp = new ProgressBar[6];
	Button m_accept = new Button();
	Button m_cancel = new Button();
	private int m_teamIndex = 0, m_boxNumber = 0, m_boxIndex = 0;

	/**
	 * Default Constractor
	 * @param boxNum
	 * @param boxInd
	 */
	public TeamForBox(int boxNum, int boxInd) {
		m_boxNumber = boxNum;
		m_boxIndex = boxInd;
		loadPokes();
		initGUI();
		setVisible(true);
	}

	/**
	 * Initializes the interface
	 */
	public void initGUI() {
		int y = 0;
		for (int i = 0; i < 6; i++) {
			m_pokes[i] = new Container();
			m_pokes[i].setSize(170, 42);
			m_pokes[i].setVisible(true);
			m_pokes[i].setLocation(0, y);

			y += 41;
			getContentPane().add(m_pokes[i]);
			m_pokes[i].setOpaque(true);
			try {
				m_pokes[i].add(m_pokeIcon[i]);
				m_pokeIcon[i].setLocation(2, 3);
				m_pokes[i].add(m_pokeName[i]);
				m_pokeName[i].setLocation(40, 5);
				m_pokes[i].add(m_level[i]);
				m_level[i].setLocation(m_pokeName[i].getX()
						+ m_pokeName[i].getWidth() + 10, 5);
				m_hp[i].setSize(114, 10);
				m_hp[i].setLocation(40, m_pokeName[i].getY()
						+ m_pokeName[i].getHeight() + 5);
				m_pokes[i].add(m_hp[i]);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		m_accept.setSize(80, 30);
		m_accept.setLocation(3, 245);
		m_accept.setText("Accept");
		m_accept.setEnabled(false);
		m_accept.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPokes(m_boxNumber, m_boxIndex, m_teamIndex);
				GameClient.getInstance().getPacketGenerator().writeTcpMessage("Bf");
				GameClient.getInstance().getUi().stopUsingBox();
				setVisible(false);
			}
		});
		add(m_accept);
		m_cancel.setSize(80, 30);
		m_cancel.setLocation(86, 245);
		m_cancel.setText("Cancel");
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				GameClient.getInstance().getPacketGenerator().writeTcpMessage("Bf");
				GameClient.getInstance().getUi().stopUsingBox();
				setVisible(false);
			}
		});
		add(m_cancel);
		getTitleBar().setVisible(false);
		setResizable(false);
		setSize(170, 302);
		setAlwaysOnTop(true);
		setOpaque(true);
	}

	/**
	 * Loads the necessary data
	 */
	public void loadPokes() {
		LoadingList.setDeferredLoading(true);
		for (int i = 0; i < 6; i++) {
			m_pokeIcon[i] = new ToggleButton();
			m_pokeName[i] = new Label();

			m_level[i] = new Label();
			m_hp[i] = new ProgressBar(0, 0);
			m_hp[i].setForeground(Color.green);

			final int j = i;
			m_pokeIcon[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					setChoice(j);
				}
			});

			m_pokeIcon[i].setSize(32, 32);

			m_pokeName[i].pack();

			try {
				if (GameClient.getInstance().getOurPlayer().getPokemon()[i] != null) {
					m_level[i].setText("Lv: "
							+ String.valueOf(GameClient.getInstance().getOurPlayer().getPokemon()[i].getLevel()));
					m_level[i].pack();
					m_pokeName[i].setText(GameClient.getInstance().getOurPlayer().getPokemon()[i].getName());
					m_pokeIcon[i].setImage(GameClient.getInstance().getOurPlayer().getPokemon()[i].getIcon());
					m_hp[i].setMaximum(GameClient.getInstance().getOurPlayer().getPokemon()[i].getMaxHP());
					m_hp[i].setForeground(Color.green);
					m_hp[i].setValue(GameClient.getInstance().getOurPlayer().getPokemon()[i].getCurHP());
					if (GameClient.getInstance().getOurPlayer().getPokemon()[i].getCurHP() > GameClient.getInstance().getOurPlayer().getPokemon()[i].getMaxHP() / 2) {
						m_hp[i].setForeground(Color.green);
					} else if (GameClient.getInstance().getOurPlayer().getPokemon()[i].getCurHP() < GameClient.getInstance().getOurPlayer().getPokemon()[i].getMaxHP() / 2
							&& GameClient.getInstance().getOurPlayer().getPokemon()[i].getCurHP() > GameClient.getInstance().getOurPlayer().getPokemon()[i].getMaxHP() / 3) {
						m_hp[i].setForeground(Color.orange);
					} else if (GameClient.getInstance().getOurPlayer().getPokemon()[i].getCurHP() < GameClient.getInstance().getOurPlayer().getPokemon()[i].getMaxHP() / 3) {
						m_hp[i].setForeground(Color.red);
					}
					m_pokeIcon[i].setImage(GameClient.getInstance().getOurPlayer().getPokemon()[i].getIcon());
					m_pokeIcon[i].setSize(32, 32);
					m_pokeName[i].setText(GameClient.getInstance().getOurPlayer().getPokemon()[i].getName());
					m_pokeName[i].pack();
					m_level[i].setText("Lv: "
							+ String.valueOf(GameClient.getInstance().getOurPlayer().getPokemon()[i].getLevel()));
					m_level[i].pack();
				} else {
					m_hp[i].setVisible(false);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		LoadingList.setDeferredLoading(false);
	}

	/**
	 * Sets the choice
	 * @param x
	 */
	public void setChoice(int x) {
		for (int i = 0; i < 6; i++) {
			m_pokeIcon[i].setSelected(false);
		}
		m_pokeIcon[x].setSelected(true);
		m_accept.setEnabled(true);
		m_teamIndex = x;
	}

	/**
	 * Performs the switch
	 * @param boxNum
	 * @param boxIndex
	 * @param teamIndex
	 */
	public void switchPokes(int boxNum, int boxIndex, int teamIndex) {
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("Bs" + (boxNum - 1) + "," + boxIndex + ","
				+ teamIndex);
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("Bf");
		GameClient.getInstance().getUi().update(false);
	}
}