package org.pokenet.client.ui.frames;

import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Player;
import org.pokenet.client.backend.entity.Player.Direction;

/**
 * A frame that shows trainer information and stats
 * 
 * @author ZombieBear
 * @author TriMethylXanthine_OverDose (TMX:OD/adpoliak)
 * 
 */
public class PlayerInfoDialog extends Frame {
	final boolean ALL_REGIONS = false; // set this to TRUE/refactor as regions are completed
	private Label[] m_kanto;
	private Label[] m_johto;
	private Label[] m_hoenn;
	private Label[] m_sinnoh;
	private Label[] m_orange;
	private Label[] m_extras;

	private Label m_kantoLbl;
	private Label m_johtoLbl;
	private Label m_hoennLbl;
	private Label m_sinnohLbl;
	private Label m_orangeLbl;
	private Label m_extrasLbl;

	private Label m_playerImage;

	private Label m_trainerEXP;
	private Label m_breedingEXP;
	private Label m_fishingEXP;
	private Label m_coordinatingEXP;

	private float maxLblWidth;

	/**
	 * Default constructor
	 */
	public PlayerInfoDialog() {
		m_kanto = new Label[8];
		m_johto = new Label[8];
		if (ALL_REGIONS)
			m_hoenn = new Label[8];
		if (ALL_REGIONS)
			m_sinnoh = new Label[8];
		if (ALL_REGIONS)
			m_orange = new Label[4];
		if (ALL_REGIONS)
			m_extras = new Label[6];

		initGUI();
	}

	/**
	 * Initializes the interface
	 */
	@SuppressWarnings("all")
	private void initGUI() {
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		setTitle("Player Information");
		// Player Image
		m_playerImage = new Label(Player.getSpriteFactory().getSprite(
				Direction.Down, false, false,
				GameClient.getInstance().getOurPlayer().getSprite()));
		m_playerImage.setSize(52, 52);
		m_playerImage.setLocation(2, 2);
		getContentPane().add(m_playerImage);

		// Trainer data labels
		m_trainerEXP = new Label("Trainer Lv:          "
				+ GameClient.getInstance().getOurPlayer().getTrainerLevel());
		m_breedingEXP = new Label("Breeding Lv:       "
				+ GameClient.getInstance().getOurPlayer().getBreedingLevel());
		m_fishingEXP = new Label("Fishing Lv:          "
				+ GameClient.getInstance().getOurPlayer().getFishingLevel());
		m_coordinatingEXP = new Label("Corrdinating Lv:  "
				+ GameClient.getInstance().getOurPlayer()
						.getCoordinatingLevel());

		m_trainerEXP.pack();
		m_breedingEXP.pack();
		m_fishingEXP.pack();
		m_coordinatingEXP.pack();

		m_trainerEXP.setForeground(Color.white);
		m_breedingEXP.setForeground(Color.white);
		m_fishingEXP.setForeground(Color.white);
		m_coordinatingEXP.setForeground(Color.white);
		
		m_trainerEXP.setLocation(m_playerImage.getWidth() + 2, 2);
		m_breedingEXP.setLocation(m_playerImage.getWidth() + 2, 2
				+ m_trainerEXP.getY() + m_trainerEXP.getHeight());
		m_fishingEXP.setLocation(m_playerImage.getWidth() + 2, 2
				+ m_breedingEXP.getY() + m_breedingEXP.getHeight());
		m_coordinatingEXP.setLocation(m_playerImage.getWidth() + 2, 2
				+ m_fishingEXP.getY() + m_fishingEXP.getHeight());

		getContentPane().add(m_trainerEXP);
		getContentPane().add(m_breedingEXP);
		getContentPane().add(m_fishingEXP);
		getContentPane().add(m_coordinatingEXP);

		// Start the badge labels
		m_kantoLbl = new Label("Kanto:");
		m_johtoLbl = new Label("Johto:");
		if (ALL_REGIONS)
			m_hoennLbl = new Label("Hoenn:");
		if (ALL_REGIONS)
			m_sinnohLbl = new Label("Sinnoh:");
		if (ALL_REGIONS)
			m_orangeLbl = new Label("Orange Islands:");
		if (ALL_REGIONS)
			m_extrasLbl = new Label("Others:");

		// Pack the badge labels
		m_kantoLbl.pack();
		m_johtoLbl.pack();
		
		m_kantoLbl.setForeground(Color.white);
		m_johtoLbl.setForeground(Color.white);

		if (ALL_REGIONS)
			m_hoennLbl.pack();
		if (ALL_REGIONS)
			m_sinnohLbl.pack();
		if (ALL_REGIONS)
			m_orangeLbl.pack();
		if (ALL_REGIONS)
			m_extrasLbl.pack();

		// Badge Label Placement
		m_kantoLbl.setY(m_coordinatingEXP.getY()
				+ m_coordinatingEXP.getHeight() + 4);
		m_johtoLbl.setY(m_kantoLbl.getY() + m_kantoLbl.getHeight() + 2);
		if (ALL_REGIONS)
			m_hoennLbl.setY(m_johtoLbl.getY() + m_johtoLbl.getHeight() + 2);
		if (ALL_REGIONS)
			m_sinnohLbl.setY(m_hoennLbl.getY() + m_hoennLbl.getHeight() + 2);
		if (ALL_REGIONS)
			m_orangeLbl.setY(m_sinnohLbl.getY() + m_sinnohLbl.getHeight() + 2);
		if (ALL_REGIONS)
			m_extrasLbl.setY(m_orangeLbl.getY() + m_orangeLbl.getHeight() + 2);

		// Add Labels to Content Pane
		getContentPane().add(m_kantoLbl);
		getContentPane().add(m_johtoLbl);
		if (ALL_REGIONS)
			getContentPane().add(m_hoennLbl);
		if (ALL_REGIONS)
			getContentPane().add(m_sinnohLbl);
		if (ALL_REGIONS)
			getContentPane().add(m_orangeLbl);
		if (ALL_REGIONS)
			getContentPane().add(m_extrasLbl);

		// DEAD CODE WARNING CAN BE IGNORED, THIS FILE WILL NEED TO BE
		// REFACTORED ANYWAYZ ~TMXOD/apoliak
		maxLblWidth = ((ALL_REGIONS) ? m_orangeLbl : m_kantoLbl).getWidth();

		loadImages();
		/*
		 * 6 rows, 20 pixels each + title bar height
		 */
		if (ALL_REGIONS) {
			setSize(160 + 2 + maxLblWidth, m_extras[0].getY()
					+ m_extras[0].getHeight() + getTitleBar().getHeight() + 2);
		} else {
			setSize(160 + 2 + maxLblWidth, m_johto[0].getY()
					+ m_johto[0].getHeight() + getTitleBar().getHeight() + 2);
		}
		
		setBackground(new Color(0, 0, 0, 85));
		setResizable(false);
		showBadges();
	}

	/**
	 * Loads the status icons
	 */
	public void loadImages() {
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		String m_path = respath+"res/badges/";
		// Kanto Badges
		for (int i = 0; i < 8; i++) {
			try {
				// KANTO
				m_kanto[i] = new Label(new Image(m_path
						+ "kanto" + (i + 1) + ".png", false));
				m_kanto[i].setSize(18, 18);
				m_kanto[i].setX(maxLblWidth + (20 * i));
				m_kanto[i].setY(m_coordinatingEXP.getY()
						+ m_coordinatingEXP.getHeight() + 2);
				getContentPane().add(m_kanto[i]);

				// JOHTO
				m_johto[i] = new Label(new Image(m_path
						+ "johto" + (i + 1) + ".png", false));
				m_johto[i].setSize(18, 18);
				m_johto[i].setX(2 + maxLblWidth + (20 * i));
				m_johto[i].setY(2 + m_kanto[i].getY() + m_kanto[i].getHeight());
				getContentPane().add(m_johto[i]);

				// HOENN
				if (ALL_REGIONS) {
					m_hoenn[i] = new Label(new Image(m_path + "hoenn" + (i + 1) + ".png", false));
					m_hoenn[i].setSize(18, 18);
					m_hoenn[i].setX(2 + maxLblWidth + (20 * i));
					m_hoenn[i].setY(2 + m_johto[i].getY()
							+ m_johto[i].getHeight());
					getContentPane().add(m_hoenn[i]);
				}
				// SINNOH
				if (ALL_REGIONS) {
					m_sinnoh[i] = new Label(new Image(m_path + "sinnoh" + (i + 1) + ".png", false));
					m_sinnoh[i].setSize(18, 18);
					m_sinnoh[i].setX(2 + maxLblWidth + (20 * i));
					m_sinnoh[i].setY(2 + m_hoenn[i].getY()
							+ m_hoenn[i].getHeight());
					getContentPane().add(m_sinnoh[i]);
				}
				// ORANGE ISLANDS
				if (ALL_REGIONS) {
					if (i < 4) {
						m_orange[i] = new Label(new Image(m_path + "orange" + (i + 1) + ".png", false));
						m_orange[i].setSize(18, 18);
						m_orange[i].setX(2 + maxLblWidth + (20 * i));
						m_orange[i].setY(2 + m_sinnoh[i].getY()
								+ m_sinnoh[i].getHeight());
						getContentPane().add(m_orange[i]);
					}
				}
				// Extra badges ???
				if (ALL_REGIONS) {
					if (i < 6) {
						m_extras[i] = new Label(new Image(m_path + "extra" + (i + 1) + ".png", false));
						m_extras[i].setSize(18, 18);
						m_extras[i].setX(2 + maxLblWidth + (20 * i));
						m_extras[i].setY(2 + m_orange[i].getY()
								+ m_orange[i].getHeight());
						getContentPane().add(m_extras[i]);
					}
				}
			} catch (SlickException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Shows badges (darkens ones the player does not have)
	 * @throws NullPointerException
	 */
	public void showBadges() throws NullPointerException {
		int[] badges = GameClient.getInstance().getOurPlayer().getBadges();
		try {
			for (int i = 0; i < badges.length; i++) {
				if (i < 8) {
					// Kanto
					if (badges[i] == 0)
						m_kanto[i].setImageFilter(Color.black);
				} else if (i < 16) {
					// Johto
					if (badges[i] == 0)
						m_johto[i - 8].setImageFilter(Color.black);
				} else if (i < 24) {
					// Hoenn
					if (ALL_REGIONS) {
						if (badges[i] == 0)
							m_hoenn[i - 16].setImageFilter(Color.black);
					}
				} else if (i < 32) {
					// Sinnoh
					if (ALL_REGIONS) {
						if (badges[i] == 0)
							m_sinnoh[i - 24].setImageFilter(Color.black);
					}
				} else if (i < 36) {
					// Orange Islands
					if (ALL_REGIONS) {
						if (badges[i] == 0)
							m_orange[i - 32].setImageFilter(Color.black);
					}
				} else if (i < 42) {
					// Extras
					if (ALL_REGIONS) {
						if (badges[i] == 0)
							m_extras[i - 36].setImageFilter(Color.black);
					}
				} else {
					throw new NullPointerException("Bad Badge Number");
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.err.println("See http://xkcd.com/371/ for details");
		}
	}
	
	public void updateDialog() {
		m_trainerEXP.setText("Trainer Lv:          "
				+ GameClient.getInstance().getOurPlayer().getTrainerLevel());
		m_breedingEXP.setText("Breeding Lv:       "
				+ GameClient.getInstance().getOurPlayer().getBreedingLevel());
		m_fishingEXP.setText("Fishing Lv:          "
				+ GameClient.getInstance().getOurPlayer().getFishingLevel());
		m_coordinatingEXP.setText("Corrdinating Lv:  "
				+ GameClient.getInstance().getOurPlayer()
						.getCoordinatingLevel());
		showBadges();
	}
}
