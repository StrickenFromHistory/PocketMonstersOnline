package org.pokenet.client.ui.frames;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;
import mdes.slick.sui.event.MouseAdapter;
import mdes.slick.sui.event.MouseEvent;
import mdes.slick.sui.skin.simple.SimpleArrowButton;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.OurPokemon;
import org.pokenet.client.network.PacketGenerator;
import org.pokenet.client.ui.base.ProgressBar;

/**
 * Party information frame
 * @author ZombieBear
 *
 */
public class PartyInfoDialog extends Frame {
	Container[] m_container = new Container[6];
	Label[] m_pokeBall = new Label[6];
	Label[] m_pokeIcon = new Label[6];
	Label[] m_pokeName = new Label[6];
	Label[] m_level = new Label[6];
	ProgressBar[] m_hp = new ProgressBar[6];
	Button[] m_switchUp = new Button[6];
	Button[] m_switchDown = new Button[6];

	OurPokemon[] m_pokes;

	/**
	 * Default constructor
	 * 
	 * @param ourPokes
	 * @param out
	 */
	public PartyInfoDialog(OurPokemon[] ourPokes) {
		m_pokes = ourPokes;
		loadImages(ourPokes);
		initGUI();
	}

	/**
	 * Initializes interface
	 */
	public void initGUI() {
		int y = 0;
		this.getTitleBar().getCloseButton().setVisible(false);
		this.setFont(GameClient.getFontSmall());
		this.setBackground(new Color(0, 0, 0, 85));
		this.setForeground(new Color(255, 255, 255));
		for (int i = 0; i < 6; i++) {
			final int j = i;
			m_container[i] = new Container();
			m_container[i].setSize(170, 42);
			m_container[i].setVisible(true);
			m_container[i].setLocation(0, y+10);
			m_container[i].setBackground(new Color(0, 0, 0, 0));
			y += 41;
			getContentPane().add(m_container[i]);
			m_container[i].setOpaque(true);
			try {
				m_container[i].add(m_pokeBall[i]);
				m_pokeBall[i].setLocation(4, 4);
				m_pokeName[i].setFont(GameClient.getFontSmall());
				m_pokeName[i].setForeground(new Color(255, 255, 255));
				m_pokeName[i].addMouseListener(new MouseAdapter() {

					@Override
					public void mouseReleased(MouseEvent e) {
						super.mouseReleased(e);
						PokemonInfoDialog info = new PokemonInfoDialog(m_pokes[j]);
						info.setAlwaysOnTop(true);
						info.setLocationRelativeTo(null);
						getDisplay().add(info);
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						super.mouseEntered(e);
						m_pokeName[j].setForeground(new Color(255, 215, 0));
					}

					@Override
					public void mouseExited(MouseEvent e) {
						super.mouseExited(e);
						m_pokeName[j].setForeground(new Color(255, 255, 255));
					}

				});
				m_container[i].add(m_pokeIcon[i]);
				m_pokeIcon[i].setLocation(2, 3);
				m_container[i].add(m_pokeName[i]);
				m_pokeName[i].setLocation(42, 5);
				m_container[i].add(m_level[i]);
				m_level[i].setFont(GameClient.getFontSmall());
				m_level[i].setForeground(new Color(255, 255, 255));
				m_level[i].setLocation(m_pokeName[i].getX()
						+ m_pokeName[i].getWidth() + 10, m_pokeName[i].getY());
				m_container[i].add(m_hp[i]);
				m_hp[i].setSize(114, 10);
				m_hp[i].setLocation(40, m_pokeName[i].getY()
						+ m_pokeName[i].getHeight() + 5);
				if (i != 0) {
					m_switchUp[i] = new SimpleArrowButton(
							SimpleArrowButton.FACE_UP);
					m_switchUp[i].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							GameClient.getInstance().getPacketGenerator().write("I" + String.valueOf(j)
									+ String.valueOf(j - 1));
						}
					});
					m_switchUp[i].setHeight(16);
					m_switchUp[i].setWidth(16);
					m_container[i].add(m_switchUp[i]);
				}
				if (i != 5) {
					m_switchDown[i] = new SimpleArrowButton(
							SimpleArrowButton.FACE_DOWN);
					m_switchDown[i].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							GameClient.getInstance().getPacketGenerator().write("I" + String.valueOf(j)
									+ String.valueOf(j + 1));
						}
					});

					m_switchDown[i].setHeight(16);
					m_switchDown[i].setWidth(16);
					m_switchDown[i].setX(24);
					m_container[i].add(m_switchDown[i]);

				}
			} catch (NullPointerException e) {
				//e.printStackTrace();
			}
		}
		
		this.getTitleBar().setGlassPane(true);
		this.setResizable(false);
		this.setSize(170, 288);
		this.setLocationRelativeTo(null);
		this.setBorderRendered(false);
		this.setTitle("Pokemon Team");
	}

	/**
	 * Loads necessary images
	 * @param pokes
	 */
	public void loadImages(OurPokemon[] pokes) {
		LoadingList.setDeferredLoading(true);
		for (int i = 0; i < 6; i++) {
			m_pokeIcon[i] = new Label();
			m_pokeBall[i] = new Label();
			m_pokeName[i] = new Label();

			m_level[i] = new Label();
			m_hp[i] = new ProgressBar(0, 0);
			m_hp[i].setForeground(Color.green);

			m_pokeIcon[i].setSize(32, 32);

			m_pokeName[i].pack();

			try {
				m_pokeBall[i].setImage(new Image("/res/ui/Pokeball.gif"));
				m_pokeBall[i].setSize(30, 30);
			} catch (SlickException e) {
				System.out.println("Couldn't load pokeball");
			}
			try {
				if (pokes[i] != null) {
					m_level[i].setText("Lv: "
							+ String.valueOf(pokes[i].getLevel()));
					m_level[i].pack();
					m_pokeName[i].setText(pokes[i].getName());
					m_pokeIcon[i].setImage(pokes[i].getIcon());
					pokes[i].setIcon();
					m_hp[i].setMaximum(pokes[i].getMaxHP());
					m_hp[i].setForeground(Color.green);
					m_hp[i].setValue(pokes[i].getCurHP());
					if (pokes[i].getCurHP() > pokes[i].getMaxHP() / 2) {
						m_hp[i].setForeground(Color.green);
					} else if (pokes[i].getCurHP() < pokes[i].getMaxHP() / 2
							&& pokes[i].getCurHP() > pokes[i].getMaxHP() / 3) {
						m_hp[i].setForeground(Color.orange);
					} else if (pokes[i].getCurHP() < pokes[i].getMaxHP() / 3) {
						m_hp[i].setForeground(Color.red);
					}
					pokes[i].setIcon();
					m_pokeIcon[i].setImage(pokes[i].getIcon());
					m_pokeIcon[i].setSize(32, 32);
					m_pokeName[i].setText(pokes[i].getName());
					m_pokeName[i].pack();
					m_level[i].setText("Lv: "
							+ String.valueOf(pokes[i].getLevel()));
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
	 * Updates info
	 * 
	 * @param pokes
	 */
	public void update(OurPokemon[] pokes) {
		m_pokes = pokes;
		LoadingList.setDeferredLoading(true);
		for (int i = 0; i < 6; i++) {
			try {
				if (pokes[i] != null) {
					m_hp[i].setMaximum(pokes[i].getMaxHP());
					m_hp[i].setValue(pokes[i].getCurHP());
					if (pokes[i].getCurHP() > pokes[i].getMaxHP() / 2) {
						m_hp[i].setForeground(Color.green);
					} else if (pokes[i].getCurHP() < pokes[i].getMaxHP() / 2
							&& pokes[i].getCurHP() > pokes[i].getMaxHP() / 3) {
						m_hp[i].setForeground(Color.orange);
					} else if (pokes[i].getCurHP() < pokes[i].getMaxHP() / 3) {
						m_hp[i].setForeground(Color.red);
					}
					pokes[i].setIcon();
					m_pokeIcon[i].setImage(pokes[i].getIcon());
					m_pokeName[i].setText(pokes[i].getName());
					m_pokeName[i].pack();
					m_level[i].setText("Lv: "
							+ String.valueOf(pokes[i].getLevel()));
					m_level[i].pack();
					m_level[i].setLocation(m_pokeName[i].getX()
							+ m_pokeName[i].getWidth() + 10, 5);

					m_pokeBall[i].setLocation(4, 4);
					m_pokeIcon[i].setLocation(2, 3);
					m_pokeName[i].setLocation(40, 5);
					m_hp[i].setLocation(40, m_pokeName[i].getY()
							+ m_pokeName[i].getHeight() + 5);
					m_hp[i].setVisible(true);
					if (i != 0)
						m_switchUp[i].setVisible(true);
					if (i != 5)
						m_switchDown[i].setVisible(true);
				} else {
					if (i != 0)
						m_switchUp[i].setVisible(false);
					if (i != 5)
						m_switchDown[i].setVisible(false);
					m_hp[i].setVisible(false);
					m_level[i].setText("");
					m_level[i].pack();
					m_pokeIcon[i].setImage(null);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		LoadingList.setDeferredLoading(false);
	}

	/**
	 * Sets sprite number
	 * 
	 * @param x
	 * @return
	 */
	public int setSpriteNumber(int x) {
		int i = 0;
		if (x <= 385) {
			i = x + 1;
		} else if (x <= 388) {
			i = 386;
		} else if (x <= 414) {
			i = x - 2;
		} else if (x <= 416) {
			i = 413;
		} else {
			i = x - 4;
		}
		return i;
	}
}
