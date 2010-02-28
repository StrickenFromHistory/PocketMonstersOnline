package org.pokenet.client.ui;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.newdawn.slick.loading.LoadingList;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.BattleManager;
import org.pokenet.client.backend.FileLoader;
import org.pokenet.client.ui.base.BattleButtonFactory;
import org.pokenet.client.ui.frames.BattleBag;

/**
 * Battle window interface
 * 
 * @author ZombieBear
 * 
 */
public class BattleWindow extends Frame {
	static final long serialVersionUID = -4351471892179339349L;

	public Container endPane;
	public Container attackPane;
	public Container pokesContainer;
	public Button move1;
	public Button move2;
	public Button move3;
	public Button move4;
	public Label pp1;
	public Label pp2;
	public Label pp3;
	public Label pp4;
	public Button pokeCancelBtn;
	public Button pokeBtn1;
	public Button pokeBtn2;
	public Button pokeBtn3;
	public Button pokeBtn4;
	public Button pokeBtn5;
	public Button pokeBtn6;
	public Button btnPoke;
	public Button btnBag;
	public Button btnRun;
	public Button cancel;
	public Button close;
	public BattleBag m_bag;
	private boolean isWild;
	public List<Button> m_moveButtons = new ArrayList<Button>();
	public List<Label> m_ppLabels = new ArrayList<Label>();
	public List<Button> m_pokeButtons = new ArrayList<Button>();
	public List<Label> m_pokeInfo = new ArrayList<Label>();
	public List<Label> m_pokeStatus = new ArrayList<Label>();
	public HashMap<String, Image> m_statusIcons = new HashMap<String, Image>();
	
	// Image Loading tools
	private String m_path = "res/battle/";
	InputStream f;
	
	private Label m_bg = new Label();

	
	/**
	 * Default constructor
	 * 
	 * @param title
	 * @param wild
	 */
	public BattleWindow(String title) {
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		m_path=respath+m_path;
		getContentPane().setX(getContentPane().getX() - 1);
		getContentPane().setY(getContentPane().getY() + 1);
		setTitle(title);
		loadStatusIcons();
		initComponents();
		setCenter();
		setSize(259, 369);
	}

	/**
	 * Loads the status icons
	 */
	public void loadStatusIcons(){
		LoadingList.setDeferredLoading(true);
		try{
			m_statusIcons.put("Poison", new Image(m_path + "PSN" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Sleep", new Image(m_path + "SLP" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Freze", new Image(m_path + "FRZ" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Burn", new Image(m_path + "BRN" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();} try{
			m_statusIcons.put("Paralysis", new Image(m_path + "PAR" + ".png", false));
		} catch (SlickException e) {e.printStackTrace();}
		LoadingList.setDeferredLoading(false);
	}
	
	/**
	 * Disables moves
	 */
	public void disableMoves() {
		attackPane.setVisible(false);
		move1.setEnabled(false);
		move2.setEnabled(false);
		move3.setEnabled(false);
		move4.setEnabled(false);

		pp1.setEnabled(false);
		pp2.setEnabled(false);
		pp3.setEnabled(false);
		pp4.setEnabled(false);

		btnPoke.setEnabled(false);
		btnBag.setEnabled(false);
		btnRun.setEnabled(false);

		cancel.setVisible(false);
	}

	/**
	 * Enables moves
	 */
	public void enableMoves() {
		attackPane.setVisible(true);
		btnPoke.setEnabled(true);
		btnBag.setEnabled(true);
		if (!isWild) {
			btnRun.setEnabled(false);
		} else {
			btnRun.setEnabled(true);
		}

		pokeCancelBtn.setEnabled(true);
		if (!move1.getText().equals("")) {
			move1.setEnabled(true);
			pp1.setEnabled(true);
		}
		if (!move2.getText().equals("")) {
			move2.setEnabled(true);
			pp2.setEnabled(true);
		}
		if (!move3.getText().equals("")) {
			move3.setEnabled(true);
			pp3.setEnabled(true);
		}
		if (!move4.getText().equals("")) {
			move4.setEnabled(true);
			pp4.setEnabled(true);
		}
		cancel.setVisible(false);
	}

	/**
	 * Initializes the interface
	 */
	private void initComponents() {
		LoadingList.setDeferredLoading(true);
		this.setBackground(new Color(0, 0, 0, 0));
		String respath = System.getProperty("res.path");
		if(respath==null)
			respath="";
		try {
			f = FileLoader.loadFile(respath+"res/ui/bg.png");
			m_bg = new Label(new Image(f, respath+"res/ui/bg.png", false));
		} catch (SlickException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		m_bg.setSize(256, 203);
		m_bg.setLocation(0, 142);
		getContentPane().add(m_bg);

		attackPane = new Container();
		attackPane.setBackground(new Color(0, 0, 0, 0));

		move1 = BattleButtonFactory.getButton("");
		move2 = BattleButtonFactory.getButton("");
		move3 = BattleButtonFactory.getButton("");
		move4 = BattleButtonFactory.getButton("");

		setResizable(false);

		this.getTitleBar().setVisible(false);

		// start attackPane
		attackPane.add(move1);
		move1.setLocation(7, 10);
		move1.setSize(116, 51);
		move1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				useMove(0);
			}
		});
		pp1 = new Label();
		pp1.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp1.setBounds(0, move1.getHeight() - 20, move1.getWidth() - 5, 20);
		move1.add(pp1);

		attackPane.add(move2);
		move2.setLocation(130, 10);
		move2.setSize(116, 51);
		move2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				useMove(1);
			}
		});
		pp2 = new Label();
		pp2.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp2.setBounds(0, move2.getHeight() - 20, move2.getWidth() - 5, 20);
		move2.add(pp2);

		attackPane.add(move3);
		move3.setLocation(7, 65);
		move3.setSize(116, 51);
		move3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				useMove(2);
			}
		});
		pp3 = new Label();
		pp3.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp3.setBounds(0, move3.getHeight() - 20, move3.getWidth() - 5, 20);
		move3.add(pp3);

		attackPane.add(move4);
		move4.setLocation(130, 65);
		move4.setSize(116, 51);
		move4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				useMove(3);
			}
		});
		pp4 = new Label();
		pp4.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp4.setBounds(0, move4.getHeight() - 20, move4.getWidth() - 5, 20);
		move4.add(pp4);

		pp1.setFont(GameClient.getFontSmall());
		pp2.setFont(GameClient.getFontSmall());
		pp3.setFont(GameClient.getFontSmall());
		pp4.setFont(GameClient.getFontSmall());
		
		pp1.setForeground(Color.white);
		pp2.setForeground(Color.white);
		pp3.setForeground(Color.white);
		pp4.setForeground(Color.white);
		
		m_moveButtons.add(move1);
		m_moveButtons.add(move2);
		m_moveButtons.add(move3);
		m_moveButtons.add(move4);

		m_ppLabels.add(pp1);
		m_ppLabels.add(pp2);
		m_ppLabels.add(pp3);
		m_ppLabels.add(pp4);

		btnRun = BattleButtonFactory.getSmallButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				run();
			}
		});
		attackPane.add(btnRun);

		btnRun.setBounds(97, 148, 60, 47);

		btnBag = BattleButtonFactory.getSmallButton("Bag");
		attackPane.add(btnBag);
		btnBag.setLocation(3, 122);
		btnBag.setSize(82, 48);

		btnBag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				showBag();
			}
		});

		btnPoke = BattleButtonFactory.getSmallButton("Pokemon");
		attackPane.add(btnPoke);
		btnPoke.setLocation(168, 122);
		btnPoke.setSize(82, 48);

		btnPoke.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				showPokePane(false);
			}
		});

		cancel = BattleButtonFactory.getSmallButton("Cancel");
		attackPane.add(cancel);
		cancel.setVisible(false);
		cancel.setLocation(162, 110);
		cancel.setSize(82, 48);

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});

		attackPane.setBounds(2, 140, 257, 201);
		getContentPane().add(attackPane);
		// end attackPane

		// start pokesContainer
		pokesContainer = new Container();
		pokesContainer.setBackground(new Color(0, 0, 0, 0));
		pokesContainer.setBounds(2, 140, 257, 201);

		pokeBtn1 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn1);
		pokeBtn1.setBounds(8, 8, 116, 51);

		pokeBtn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(0);
			}
		});

		pokeBtn2 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn2);
		pokeBtn2.setBounds(128, 8, 116, 51);

		pokeBtn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(1);
			}
		});

		pokeBtn3 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn3);
		pokeBtn3.setBounds(8, 59, 116, 51);

		pokeBtn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(2);
			}
		});

		pokeBtn4 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn4);
		pokeBtn4.setBounds(128, 59, 116, 51);

		pokeBtn4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(3);
			}
		});

		pokeBtn5 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn5);
		pokeBtn5.setBounds(8, 110, 116, 51);

		pokeBtn5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(4);
			}
		});

		pokeBtn6 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn6);
		pokeBtn6.setBounds(128, 110, 116, 51);

		pokeBtn6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(5);
			}
		});

		m_pokeButtons.add(pokeBtn1);
		m_pokeButtons.add(pokeBtn2);
		m_pokeButtons.add(pokeBtn3);
		m_pokeButtons.add(pokeBtn4);
		m_pokeButtons.add(pokeBtn5);
		m_pokeButtons.add(pokeBtn6);

		for (int i = 0; i < 6; i++){
			Label status = new Label();
			status.setSize(30, 12);
			status.setGlassPane(true);
			m_pokeButtons.get(i).add(status);
			status.setLocation(6, 5);
			
			Label info = new Label();
			m_pokeButtons.get(i).add(info);
			info.setText("                               ");
			info.setLocation(3, 34);
			info.setSize(107, 14);
			info.setForeground(Color.white);
			info.setGlassPane(true);
			info.setFont(GameClient.getFontSmall());
			m_pokeInfo.add(info);
		}

		pokeCancelBtn = BattleButtonFactory.getSmallButton("Cancel");
		pokesContainer.add(pokeCancelBtn);
		pokeCancelBtn.setLocation(162, 161);
		pokeCancelBtn.setSize(82, 48);
		
		pokeCancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				showAttack();
			}
		});
		pokesContainer.setVisible(false);
		getContentPane().add(pokesContainer);
		// End pokesContainer

		endPane = new Container();
		endPane.setBackground(new Color(0, 0, 0, 0));
		getContentPane().add(endPane);
		endPane.setBounds(0, 154, 253, 192);

		close = new Button();
		close.setVisible(true);
		endPane.add(close);
		close.setText("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			}
		});

		endPane.setVisible(false);
		LoadingList.setDeferredLoading(false);
	}

	/**
	 * Sends the run packer
	 */
	private void run() {
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("br");
	}

	/**
	 * Centers the battle window
	 */
	public void setCenter() {
		int height = (int) GameClient.getInstance().getDisplay().getHeight();
		int width = (int) GameClient.getInstance().getDisplay().getWidth();
		int x = (width / 2) - 130;
		int y = (height / 2) - 238;
		this.setBounds(x, y, 259, 475);
	}

	/**
	 * Sets whether the battle is a wild pokemon
	 * @param isWild
	 */
	public void setWild(boolean isWild) {
		this.isWild = isWild;
		btnRun.setEnabled(isWild);
	}

	/**
	 * Shows the attack Pane
	 */
	public void showAttack() {
		pokesContainer.setVisible(false);
		// bagPane.setVisible(false);
		attackPane.setVisible(true);
		endPane.setVisible(false);
	}

	/**
	 * Shows the Bag Pane
	 */
	public void showBag() {
		attackPane.setVisible(false);
		endPane.setVisible(false);
		pokesContainer.setVisible(false);
		m_bag = new BattleBag();
		m_bag.setAlwaysOnTop(true);
		m_bag.setZIndex(10000000); // Ridiculous z-index ensures that the bag is on top.
		GameClient.getInstance().getDisplay().add(m_bag);
	}

	/**
	 * Shows the pokemon Pane
	 */
	public void showPokePane(boolean isForced) {
		BattleManager.getInstance().updatePokePane();
		attackPane.setVisible(false);
		// bagPane.setVisible(false);
		pokesContainer.setVisible(true);
		endPane.setVisible(false);
		if (isForced)
			pokeCancelBtn.setEnabled(false);
		else
			pokeCancelBtn.setEnabled(true);
	}

	/**
	 * Sends the pokemon switch packet
	 * 
	 * @param i
	 */
	private void switchPoke(int i) {
		attackPane.setVisible(false);
		pokesContainer.setVisible(false);
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("bs" + i);
	}

	/**
	 * Sends a move packet
	 * 
	 * @param i
	 */
	private void useMove(int i) {
		disableMoves();
		if (BattleManager.getInstance().getCurPoke().getMoveCurPP()[i] != 0) {
			BattleManager.getInstance().getCurPoke().setMoveCurPP(i, 
					BattleManager.getInstance().getCurPoke().getMoveCurPP()[i] - 1);
			BattleManager.getInstance().updateMoves();
		}
		GameClient.getInstance().getPacketGenerator().writeTcpMessage("bm" + i);
		// BattleManager.getInstance().getTimeLine().getBattleSpeech().advance();
	}
}
