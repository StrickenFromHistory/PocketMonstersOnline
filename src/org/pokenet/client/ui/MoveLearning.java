package org.pokenet.client.ui;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mdes.slick.sui.Button;
import mdes.slick.sui.Container;
import mdes.slick.sui.Frame;
import mdes.slick.sui.Label;
import mdes.slick.sui.event.ActionEvent;
import mdes.slick.sui.event.ActionListener;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.BattleManager;
import org.pokenet.client.backend.FileLoader;
import org.pokenet.client.backend.MoveLearningManager;
import org.pokenet.client.ui.base.BattleButtonFactory;
import org.pokenet.client.ui.base.ConfirmationDialog;

/**
 * Handles move learning and evolution
 * 
 * @author ZombieBear
 * 
 */
public class MoveLearning extends Frame {
	private Button move1, move2, move3, move4;
	private Label pp1, pp2, pp3, pp4;
	private Button m_cancel;
	private Label m_bg;
	private Container m_movePane;
	private String m_move;
	private int m_pokeIndex;
	public List<Button> m_moveButtons = new ArrayList<Button>();
	public List<Label> m_pp = new ArrayList<Label>();
	private ConfirmationDialog m_replace;
	private MoveLearnCanvas m_canvas;

	// Image Loading tools
	String m_path = "res/battle/";
	InputStream f;
	
	/**
	 * Default Constructor
	 * 
	 * @param pokeIndex
	 * @param move
	 * @param isMoveLearning
	 */
	public MoveLearning() {
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		m_path = respath+m_path;
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		m_canvas = new MoveLearnCanvas();
		getContentPane().add(m_canvas);
		setSize(259, 369);
		initGUI();
		setCenter();
	}

	/**
	 * Starts the GUI
	 * 
	 * @param isMoveLearning
	 */
	public void initGUI() {
		// TRUE = Move Learning
		// FALSE = Evolution
		m_bg = new Label();
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		try {
			f = FileLoader.loadFile(respath+"res/ui/bg.png");
			m_bg = new Label(new Image(f, respath+"res/ui", false));
		} catch (SlickException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		m_bg.setSize(256, 203);
		m_bg.setLocation(0, 142);
		getContentPane().add(m_bg);

		m_movePane = new Container();
		m_movePane.setBackground(new Color(0, 0, 0, 0));
		m_movePane.setBounds(2, 140, 257, 201);

		move1 = BattleButtonFactory.getButton("");
		move2 = BattleButtonFactory.getButton("");
		move3 = BattleButtonFactory.getButton("");
		move4 = BattleButtonFactory.getButton("");

		setResizable(false);
		getTitleBar().setVisible(false);

		// start attackPane
		m_movePane.add(move1);
		move1.setLocation(7, 10);
		move1.setSize(116, 51);
		move1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				replaceMove(0);
			}
		});
		pp1 = new Label();
		pp1.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp1.setBounds(7, 40, 110, 20);
		m_movePane.add(pp1);

		m_movePane.add(move2);
		move2.setLocation(130, 10);
		move2.setSize(116, 51);
		move2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				replaceMove(1);
			}
		});
		pp2 = new Label();
		pp2.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp2.setBounds(130, 40, 110, 20);
		m_movePane.add(pp2);

		m_movePane.add(move3);
		move3.setLocation(7, 65);
		move3.setSize(116, 51);
		move3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				replaceMove(2);
			}
		});
		pp3 = new Label();
		pp3.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp3.setBounds(7, 95, 110, 20);
		m_movePane.add(pp3);

		m_movePane.add(move4);
		move4.setLocation(130, 65);
		move4.setSize(116, 51);
		move4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				replaceMove(3);
			}
		});
		pp4 = new Label();
		pp4.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp4.setBounds(130, 95, 110, 20);
		m_movePane.add(pp4);

		m_moveButtons.add(move1);
		m_moveButtons.add(move2);
		m_moveButtons.add(move3);
		m_moveButtons.add(move4);

		m_pp.add(pp1);
		m_pp.add(pp2);
		m_pp.add(pp3);
		m_pp.add(pp4);

		m_cancel = new Button("Cancel");
		m_cancel.setBounds(3, 122, 246, 77);
		m_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				MoveLearningManager.getInstance().removeMoveLearning();
				GameClient.getInstance().getPacketGenerator().writeTcpMessage(
						"PM" + m_pokeIndex + m_move);
			}
		});
		m_movePane.add(m_cancel);
		
		getContentPane().add(m_movePane);
	}

	/**
	 * Handles move replacement
	 * 
	 * @param i
	 */
	private void replaceMove(int i) {
		final int j = i;
		if (!GameClient.getInstance().getDisplay().containsChild(m_replace)) {
			if (m_moveButtons.get(i).getText().equals("")) {
				GameClient.getInstance().getOurPlayer().getPokemon()[m_pokeIndex].setMoves(j, m_move);
				if (BattleManager.getInstance().getBattleWindow().isVisible())
					BattleManager.getInstance().updateMoves();
				GameClient.getInstance().getPacketGenerator().writeTcpMessage(
						"Pm" + m_pokeIndex + i + m_move);
				MoveLearningManager.getInstance().removeMoveLearning();
			} else {
				setAlwaysOnTop(false);
				m_replace = new ConfirmationDialog(
						"Are you sure you want to forget "
						+ m_moveButtons.get(i).getText() + " to learn "
						+ m_move + "?");
				m_replace.setAlwaysOnTop(true);
				ActionListener yes = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						GameClient.getInstance().getOurPlayer().getPokemon()[m_pokeIndex].setMoves(j, m_move);
						BattleManager.getInstance().updateMoves();
						GameClient.getInstance().getPacketGenerator().writeTcpMessage(
								"Pm" + m_pokeIndex + j + m_move);
						GameClient.getInstance().getDisplay().remove(m_replace);
						m_replace = null;
						MoveLearningManager.getInstance().removeMoveLearning();
					}
				};
				ActionListener no = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						GameClient.getInstance().getDisplay().remove(m_replace);
						m_replace = null;
						setAlwaysOnTop(true);
					}
				};
				m_replace.addYesListener(yes);
				m_replace.addNoListener(no);
			}
		}
	}

	public void learnMove(int pokeIndex, String move) {
		setAlwaysOnTop(true);
		m_pokeIndex = pokeIndex;
		
		GameClient.getInstance().getUi().talkToNPC(GameClient.getInstance().getOurPlayer()
				.getPokemon()[pokeIndex].getName() + " wants to learn " + move);

		m_move = move;

		move1.setText(GameClient.getInstance().getOurPlayer().getPokemon()[m_pokeIndex].getMoves()[0]);
		move2.setText(GameClient.getInstance().getOurPlayer().getPokemon()[m_pokeIndex].getMoves()[1]);
		move3.setText(GameClient.getInstance().getOurPlayer().getPokemon()[m_pokeIndex].getMoves()[2]);
		move4.setText(GameClient.getInstance().getOurPlayer().getPokemon()[m_pokeIndex].getMoves()[3]);

		for (int i = 0; i < 4; i++) {
			if (m_moveButtons.get(i).getText().equals("")) {
				m_pp.get(i).setVisible(false);
			} else {
				m_pp.get(i).setText(GameClient.getInstance().getOurPlayer().getPokemon()[pokeIndex].
						getMoveCurPP()[i] + "/" + GameClient.getInstance().getOurPlayer().getPokemon()
						[pokeIndex].getMoveMaxPP()[i]);
				m_pp.get(i).setVisible(true);
			}
		}

		m_movePane.setVisible(true);
		m_canvas.draw(pokeIndex);
	}

	/**
	 * Centers the frame
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - 130;
		int y = (height / 2) - 238;
		this.setLocation(x, y);
	}
}

/**
 * Canvas for Move Learning screen
 * 
 * @author ZombieBear
 * 
 */
class MoveLearnCanvas extends Container {
	Label bg = new Label();
	Label poke = new Label();

	public MoveLearnCanvas() {
		setSize(257, 144);
		setVisible(true);
		bg.setBackground(Color.black);
		bg.setOpaque(true);
		// Background?
		/*LoadingList.setDeferredLoading(true);
		try {
			bg = new Label(new Image("res/ui/DP_darkgrass.png"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
		LoadingList.setDeferredLoading(false);*/
		bg.setBounds(0, 0, 256, 144);
		this.add(bg);
		setY(1);
	}

	public void draw(int pokeIndex) {
		poke = new Label(GameClient.getInstance().getOurPlayer().getPokemon()[pokeIndex].getSprite());
		poke.setSize(80, 80);
		poke.setLocation(getWidth() / 2 - 40, getHeight() / 2 - 40);
		this.add(poke);
	}
}