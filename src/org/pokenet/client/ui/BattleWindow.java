package org.pokenet.client.ui;

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
import org.pokenet.client.ui.base.BattleButtonFactory;

/**
 * Battle window interface
 * 
 * @author ZombieBear
 * 
 */
public class BattleWindow extends Frame {
	static final long serialVersionUID = -4351471892179339349L;

	public Container confirmPane;
	public Container endPane;
	public Container attackPane;
	public Container pokesContainer;
	public Button move1;
	public Button move2;
	public Button move3;
	public Button move4;
	public Label info1;
	public Label info2;
	public Label info3;
	public Label info4;
	public Label info5;
	public Label info6;
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
	public Button jBtnPoke;
	public Button jBtnBag;
	public Button jBtnRun;
	public Label shouldReplace;
	public Button yes;
	public Button no;
	public Button cancel;
	public Button close;
	private boolean isWild;

	/**
	 * Default constructor
	 * 
	 * @param title
	 * @param wild
	 */
	public BattleWindow(String title, boolean wild) {
		setTitle(title);
		isWild = wild;
		initComponents();
		setCenter();
		setSize(259, 369);
	}

	/**
	 * Initializes the interface
	 */
	private void initComponents() {
		this.setBackground(new Color(0, 0, 0, 0));
		Label bg = null;
		try {
			bg = new Label(new Image("/res/ui/bg.png"));
			bg.setSize(256,203);
		} catch (SlickException e) {
			e.printStackTrace();
		}

		bg.setLocation(0, 142);
		getContentPane().add(bg);

		attackPane = new Container();
		attackPane.setBackground(new Color(0, 0, 0, 0));

		move1 = BattleButtonFactory.getButton("");
		move2 = BattleButtonFactory.getButton("");
		move3 = BattleButtonFactory.getButton("");
		move4 = BattleButtonFactory.getButton("");

		confirmPane = new Container();
		confirmPane.setBackground(new Color(0, 0, 0, 0));
		shouldReplace = new Label("Are you sure you want to replace this move?");
		shouldReplace.pack();
		yes = BattleButtonFactory.getButton("Replace");
		yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: Add code
			}
		});
		yes.setSize(82, 48);
		yes.setLocation(0, 30);
		no = BattleButtonFactory.getButton("Don't Replace");
		no.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: Add code
			}
		});
		no.setSize(82, 48);
		no.setLocation(0, 83);
		confirmPane.add(shouldReplace);
		confirmPane.add(yes);
		confirmPane.add(no);
		confirmPane.setVisible(false);
		getContentPane().add(confirmPane);

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
		pp1.setBounds(7, 40, 110, 20);
		attackPane.add(pp1);

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
		pp2.setBounds(130, 40, 110, 20);
		attackPane.add(pp2);

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
		pp3.setBounds(7, 95, 110, 20);
		attackPane.add(pp3);

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
		pp4.setBounds(130, 95, 110, 20);
		attackPane.add(pp4);

		jBtnRun = BattleButtonFactory.getSmallButton("Run");
		attackPane.add(jBtnRun);

		if (!isWild) {
			jBtnRun.setEnabled(false);
		} else {
			jBtnRun.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					run();
				}
			});
		}
		jBtnRun.setBounds(97, 148, 60, 47);

		jBtnBag = BattleButtonFactory.getSmallButton("Bag");
		attackPane.add(jBtnBag);
		jBtnBag.setLocation(3, 122);
		jBtnBag.setSize(82, 48);

		jBtnBag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				//TODO: Show bag
			}
		});

		jBtnPoke = BattleButtonFactory.getSmallButton("Pokemon");
		attackPane.add(jBtnPoke);
		jBtnPoke.setLocation(168, 122);
		jBtnPoke.setSize(82, 48);

		jBtnPoke.addActionListener(new ActionListener() {
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
		
		confirmPane.setBounds(2, 140, 257, 201);
		attackPane.setBounds(2, 140, 257, 201);
		getContentPane().add(attackPane);
		// end attackPane

		// start pokesContainer
		pokesContainer = new Container();
		pokesContainer.setBackground(new Color(0, 0, 0, 0));
		pokesContainer.setBounds(2, 140, 257, 201);

		pokeBtn1 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn1);
		pokeBtn1.setBounds(8, 8, 112, 26);

		pokeBtn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(0);
			}
		});

		pokeBtn2 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn2);
		pokeBtn2.setBounds(128, 8, 112, 26);

		pokeBtn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(1);
			}
		});

		pokeBtn3 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn3);
		pokeBtn3.setBounds(8, 57, 112, 26);

		pokeBtn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(2);
			}
		});

		pokeBtn4 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn4);
		pokeBtn4.setBounds(128, 57, 112, 26);

		pokeBtn4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(3);
			}
		});

		pokeBtn5 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn5);
		pokeBtn5.setBounds(8, 107, 112, 26);

		pokeBtn5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(4);
			}
		});

		pokeBtn6 = BattleButtonFactory.getButton(" ");
		pokesContainer.add(pokeBtn6);
		pokeBtn6.setBounds(128, 107, 112, 26);

		pokeBtn6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				switchPoke(5);
			}
		});

		pokeCancelBtn = BattleButtonFactory.getSmallButton("Cancel");
		pokesContainer.add(pokeCancelBtn);
		pokeCancelBtn.setLocation(128, 140);
		pokeCancelBtn.setSize(82, 48);

		info1 = new Label();
		pokesContainer.add(info1);
		info1.setText("                               ");
		info1.setBounds(8, 37, 107, 14);

		info2 = new Label();
		pokesContainer.add(info2);
		info2.setText("                               ");
		info2.setBounds(128, 37, 107, 14);

		info3 = new Label();
		pokesContainer.add(info3);
		info3.setText("                               ");
		info3.setBounds(8, 88, 107, 14);

		info4 = new Label();
		pokesContainer.add(info4);
		info4.setText("                               ");
		info4.setBounds(128, 88, 107, 14);

		info5 = new Label();
		pokesContainer.add(info5);
		info5.setText("                               ");
		info5.setBounds(8, 139, 107, 14);

		info6 = new Label();
		pokesContainer.add(info6);
		info6.setText("                               ");
		info6.setBounds(128, 139, 107, 14);

		pokeCancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				showAttack();
			}
		});
		pokesContainer.setVisible(false);
		getContentPane().add(pokesContainer);
		//End pokesContainer
		
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
	 * Disables moves
	 */
	public void disableMoves() {
		move1.setEnabled(false);
		move2.setEnabled(false);
		move3.setEnabled(false);
		move4.setEnabled(false);

		pp1.setEnabled(false);
		pp2.setEnabled(false);
		pp3.setEnabled(false);
		pp4.setEnabled(false);

		jBtnPoke.setEnabled(false);
		jBtnBag.setEnabled(false);
		jBtnRun.setEnabled(false);

		//cancel.setVisible(false);
	}

	/**
	 * Enables moves
	 */
	public void enableMoves() {
		jBtnPoke.setEnabled(true);
		jBtnBag.setEnabled(true);
		if (!isWild) {
			jBtnRun.setEnabled(false);
		} else {
			jBtnRun.setEnabled(true);
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
		// bagPane.setVisible(true);
	}

	/**
	 * Shows the pokemon Pane
	 */
	public void showPokePane(boolean isForced) {
		GameClient.getInstance().getUi().getBattleManager().updatePokePane();
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
	 * Sets the move learning interface
	 */
	public void setUIToLearn(String moveName) {
		showAttack();

		move1.setEnabled(true);
		move2.setEnabled(true);
		move3.setEnabled(true);
		move4.setEnabled(true);
		cancel.setVisible(true);
		cancel.setEnabled(true);
	}
	
	/**
	 * Sends a move packet
	 * @param i
	 */
	private void useMove(int i){
        disableMoves();
        GameClient.getInstance().getPacketGenerator().write("bm" + i);
	}
	
	/**
	 * Sends the run packer
	 */
	private void run(){
        GameClient.getInstance().getPacketGenerator().write("br");
	}
	
	/**
	 * Sends the pokemon switch packet
	 * @param i
	 */
	private void switchPoke(int i){
        GameClient.getInstance().getPacketGenerator().write("bs" + i);
	}
}
