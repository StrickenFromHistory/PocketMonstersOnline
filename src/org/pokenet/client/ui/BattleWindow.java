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
import org.pokenet.client.ui.base.ProgressBar;

/**
 * Battle window interface
 * @author ZombieBear
 *
 */
public class BattleWindow extends Frame {
	static final long serialVersionUID = -4351471892179339349L;

	private Button cancel;
	private boolean isWild;

	/**
	 * Default constructor
	 * @param title
	 * @param wild
	 */
	public BattleWindow(String title, boolean wild) {
		setTitle(title);
		isWild = wild;
		initComponents();
		this.setCenter();
		this.setSize(259, 381);
	}

	/**
	 * Initializes the interface
	 */
	private void initComponents() {
		this.setBackground(new Color(0, 0, 0, 0));
		Label bg = null;
		try {
			bg = new Label(new Image("pokeglobal/client/res/battle/bg.png"));
		} catch (SlickException e) {
			e.printStackTrace();
		}

		bg.pack();
		bg.setLocation(0, 150);
		getContentPane().add(bg);

		battleBG = new Container();

		attackPane = new Container();
		attackPane.setBackground(new Color(0, 0, 0, 0));
		move1 = BattleButtonFactory.getButton("");
		move2 = BattleButtonFactory.getButton("");
		move3 = BattleButtonFactory.getButton("");

		confirmPane = new Container();
		confirmPane.setBackground(new Color(0, 0, 0, 0));
		shouldReplace = new Label("Are you sure you want to replace this move?");
		shouldReplace.pack();
		yes = BattleButtonFactory.getButton("Replace");
		yes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		yes.pack();
		yes.setLocation(0, 30);
		no = BattleButtonFactory.getButton("Don't Replace");
		no.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		no.pack();
		no.setLocation(0, 83);
		confirmPane.add(shouldReplace);
		confirmPane.add(yes);
		confirmPane.add(no);
		confirmPane.setVisible(false);
		getContentPane().add(confirmPane);

		setResizable(false);

		getContentPane().add(battleBG);
		battleBG.setBounds(0, 1, 257, 105);
		battleBG.setSize(257, 144);
		battleBG.setVisible(true);

		this.getTitleBar().setVisible(false);

		// start attackPane
		attackPane.add(move1);
		move1.setLocation(7, 0);
		move1.pack();
		move1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});
		pp1 = new Label();
		pp1.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp1.setBounds(7, 30, 110, 20);
		attackPane.add(pp1);

		attackPane.add(move2);
		move2.setLocation(130, 0);
		move2.pack();
		move2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});
		pp2 = new Label();
		pp2.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp2.setBounds(130, 30, 110, 20);
		attackPane.add(pp2);

		attackPane.add(move3);
		move3.setLocation(7, 55);
		move3.pack();
		move3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});
		pp3 = new Label();
		pp3.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp3.setBounds(7, 85, 110, 20);
		attackPane.add(pp3);

		{
			move4 = BattleButtonFactory.getButton("");
			attackPane.add(move4);
			move4.setLocation(130, 55);
			move4.pack();
		}
		move4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});
		pp4 = new Label();
		pp4.setHorizontalAlignment(Label.RIGHT_ALIGNMENT);
		pp4.setBounds(130, 85, 110, 20);
		attackPane.add(pp4);

		{
			jBtnRun = BattleButtonFactory.getSmallButton("Run");
			attackPane.add(jBtnRun);

			if (!isWild) {
				jBtnRun.setEnabled(false);
			} else {
				jBtnRun.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {

					}
				});
			}
			jBtnRun.setBounds(97, 132, 60, 47);
		}

		jBtnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});

		{
			jBtnBag = BattleButtonFactory.getSmallButton("Bag");
			attackPane.add(jBtnBag);
			jBtnBag.setLocation(3, 110);
			jBtnBag.pack();
		}

		jBtnBag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});

		{
			jBtnPoke = BattleButtonFactory.getSmallButton("Pokemon");
			attackPane.add(jBtnPoke);
			jBtnPoke.setLocation(168, 110);
			jBtnPoke.pack();
		}

		jBtnPoke.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});

		{
			cancel = BattleButtonFactory.getSmallButton("Cancel");
			attackPane.add(cancel);
			cancel.setLocation(162, 110);
			cancel.pack();
		}

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

			}
		});

		confirmPane.setBounds(0, 154, 257, 181);
		attackPane.setBounds(0, 154, 257, 181);
		getContentPane().add(attackPane);
		// end attackPane

		// start pokesContainer
		{
			pokesContainer = new Container();
			pokesContainer.setBackground(new Color(0, 0, 0, 0));
			getContentPane().add(pokesContainer);
			pokesContainer.setBounds(0, 154, 253, 192);
			{
				pokeBtn1 = BattleButtonFactory.getButton("");
				pokesContainer.add(pokeBtn1);
				pokeBtn1.setBounds(8, 8, 112, 26);
			}

			pokeBtn1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {

				}
			});

			{
				pokeBtn2 = BattleButtonFactory.getButton("");
				pokesContainer.add(pokeBtn2);
				pokeBtn2.setBounds(128, 8, 112, 26);
			}

			pokeBtn2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {

				}
			});

			{
				pokeBtn3 = BattleButtonFactory.getButton("");
				pokesContainer.add(pokeBtn3);
				pokeBtn3.setBounds(8, 57, 112, 26);
			}

			pokeBtn3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {

				}
			});

			{
				pokeBtn4 = BattleButtonFactory.getButton("");
				pokesContainer.add(pokeBtn4);
				pokeBtn4.setBounds(128, 57, 112, 26);
			}

			pokeBtn4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {

				}
			});

			{
				pokeBtn5 = BattleButtonFactory.getButton("");
				pokesContainer.add(pokeBtn5);
				pokeBtn5.setBounds(8, 107, 112, 26);
			}

			pokeBtn5.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {

				}
			});

			{
				pokeBtn6 = BattleButtonFactory.getButton("");
				pokesContainer.add(pokeBtn6);
				pokeBtn6.setBounds(128, 107, 112, 26);
			}

			pokeBtn6.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {

				}
			});

			{
				pokeCancelBtn = BattleButtonFactory.getSmallButton("Cancel");
				pokesContainer.add(pokeCancelBtn);
				pokeCancelBtn.setLocation(128, 140);
				pokeCancelBtn.pack();
			}
			{
				info1 = new Label();
				pokesContainer.add(info1);
				info1.setText("                               ");
				info1.setBounds(8, 37, 107, 14);
			}
			{
				info2 = new Label();
				pokesContainer.add(info2);
				info2.setText("                               ");
				info2.setBounds(128, 37, 107, 14);
			}
			{
				info3 = new Label();
				pokesContainer.add(info3);
				info3.setText("                               ");
				info3.setBounds(8, 88, 107, 14);
			}
			{
				info4 = new Label();
				pokesContainer.add(info4);
				info4.setText("                               ");
				info4.setBounds(128, 88, 107, 14);
			}
			{
				info5 = new Label();
				pokesContainer.add(info5);
				info5.setText("                               ");
				info5.setBounds(8, 139, 107, 14);
			}
			{
				info6 = new Label();
				pokesContainer.add(info6);
				info6.setText("                               ");
				info6.setBounds(128, 139, 107, 14);
			}

			pokeCancelBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {

				}
			});

		}
		{
			endPane = new Container();
			endPane.setBackground(new Color(0, 0, 0, 0));
			getContentPane().add(endPane);
			endPane.setBounds(0, 154, 253, 192);
			{
				close = new Button();
				close.setVisible(true);
				endPane.add(close);
				close.setText("Close");
				close.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {

						setVisible(false);

					}
				});
			}
		}
		endPane.setVisible(false);
		pokesContainer.setVisible(false);
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

	private Container confirmPane;
	private Container battleBG;
	private Button close;
	private Container endPane;
	private Container attackPane;
	private Button move1;
	private Button move2;
	private Label info6;
	private Label info5;
	private Label info3;
	private Label info4;
	private Label info2;
	private Label info1;
	private Label pp1;
	private Label pp2;
	private Label pp3;
	private Label pp4;
	private Button pokeCancelBtn;
	private Button pokeBtn6;
	private Button pokeBtn5;
	private Button pokeBtn4;
	private Button pokeBtn3;
	private Button pokeBtn2;
	private Button pokeBtn1;
	private Container pokesContainer;
	private Button jBtnPoke;
	private Button jBtnBag;
	private Button jBtnRun;
	private Button move3;
	private Button move4;
	private Label shouldReplace;
	private Button yes;
	private Button no;
}
