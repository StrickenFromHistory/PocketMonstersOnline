package org.pokenet.client.backend;

import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.OurPlayer;
import org.pokenet.client.backend.entity.OurPokemon;
import org.pokenet.client.backend.entity.Pokemon;
import org.pokenet.client.ui.BattleWindow;

/**
 * Handles battle events and controls the battle window
 * 
 * @author ZombieBear
 * 
 */
public class BattleManager {
	private OurPlayer m_player;
	private BattleWindow m_battle;
	private OurPokemon[] m_ourPokes;
	private Pokemon[] m_enemyPokes;

	/**
	 * Default Constructor
	 */
	public BattleManager() {
	}

	/**
	 * Retrieves player data
	 */
	private void getPlayerData() {
		m_player = GameClient.getInstance().getOurPlayer();
		m_ourPokes = m_player.getPokemon();
	}

	/**
	 * Retrieves enemy data
	 */
	private void getEnemyData(Pokemon[] info) {
		m_enemyPokes = info;
		// TODO: Draw pokeballs equal to the enemy's pokemon count
	}

	/**
	 * Starts a new BattleWindow and BattleCanvas
	 */
	public void startBattle() {
		m_battle = new BattleWindow("Battle!", true);
		GameClient.getInstance().getDisplay().add(m_battle);
	}

	/**
	 * Retrieves a pokemon's moves and updates the BattleWindow
	 * @param int i
	 */
	public void updateMoves(int i) {
		if (m_ourPokes[i].getMoves()[0] != null) {
			m_battle.move1.setText(m_ourPokes[i].getMoves()[0]);
			m_battle.pp1.setText(m_ourPokes[i].getMovecurPP()[0] + "/"
					+ m_ourPokes[i].getMovemaxPP()[0]);
		} else {
			m_battle.move1.setText("");
			m_battle.pp1.setText("");
		}

		if (m_ourPokes[i].getMoves()[0] != null) {
			m_battle.move2.setText(m_ourPokes[i].getMoves()[1]);
			m_battle.pp2.setText(m_ourPokes[i].getMovecurPP()[1] + "/"
					+ m_ourPokes[i].getMovemaxPP()[1]);
		} else {
			m_battle.move2.setText("");
			m_battle.pp2.setText("");
		}

		if (m_ourPokes[i].getMoves()[0] != null) {
			m_battle.move3.setText(m_ourPokes[i].getMoves()[2]);
			m_battle.pp3.setText(m_ourPokes[i].getMovecurPP()[2] + "/"
					+ m_ourPokes[i].getMovemaxPP()[2]);
		} else {
			m_battle.move3.setText("");
			m_battle.pp3.setText("");
		}
		if (m_ourPokes[i].getMoves()[0] != null) {
			m_battle.move4.setText(m_ourPokes[i].getMoves()[3]);
			m_battle.pp4.setText(m_ourPokes[i].getMovecurPP()[3] + "/"
					+ m_ourPokes[i].getMovemaxPP()[3]);
		} else {
			m_battle.move4.setText("");
			m_battle.pp4.setText("");
		}
	}

	/**
	 * Updates the pokemon pane
	 */
	public void updatePokePane() {
		m_battle.pokeBtn1.setText(m_ourPokes[0].getName());
		m_battle.info1.setText("Lv: " + m_ourPokes[0].getLevel() + " HP:"
				+ m_ourPokes[0].getCurHP() + "/" + m_ourPokes[0].getMaxHP());
		if (m_ourPokes[0].getCurHP() <= 0)
			m_battle.pokeBtn1.setEnabled(false);
		else
			m_battle.pokeBtn1.setEnabled(true);

		try {
			m_battle.pokeBtn2.setText(m_ourPokes[1].getName());
			m_battle.info2
					.setText("Lv: " + m_ourPokes[1].getLevel() + " HP:"
							+ m_ourPokes[1].getCurHP() + "/"
							+ m_ourPokes[1].getMaxHP());
			if (m_ourPokes[1].getCurHP() <= 0)
				m_battle.pokeBtn2.setEnabled(false);
			else
				m_battle.pokeBtn2.setEnabled(true);
		} catch (NullPointerException e) {
			m_battle.pokeBtn2.setEnabled(false);
		}

		try {
			m_battle.pokeBtn3.setText(m_ourPokes[2].getName());
			m_battle.info3
					.setText("Lv: " + m_ourPokes[2].getLevel() + " HP:"
							+ m_ourPokes[2].getCurHP() + "/"
							+ m_ourPokes[2].getMaxHP());
			if (m_ourPokes[2].getCurHP() <= 0)
				m_battle.pokeBtn3.setEnabled(false);
			else
				m_battle.pokeBtn3.setEnabled(true);
		} catch (NullPointerException e) {
			m_battle.pokeBtn3.setEnabled(false);
		}

		try {
			m_battle.pokeBtn4.setText(m_ourPokes[3].getName());
			m_battle.info4
					.setText("Lv: " + m_ourPokes[3].getLevel() + " HP:"
							+ m_ourPokes[3].getCurHP() + "/"
							+ m_ourPokes[3].getMaxHP());
			if (m_ourPokes[3].getCurHP() <= 0)
				m_battle.pokeBtn4.setEnabled(false);
			else
				m_battle.pokeBtn4.setEnabled(true);
		} catch (NullPointerException e) {
			m_battle.pokeBtn4.setEnabled(false);
		}
		try {
			m_battle.pokeBtn5.setText(m_ourPokes[4].getName());
			m_battle.info5
					.setText("Lv: " + m_ourPokes[4].getLevel() + " HP:"
							+ m_ourPokes[4].getCurHP() + "/"
							+ m_ourPokes[4].getMaxHP());
			if (m_ourPokes[4].getCurHP() <= 0)
				m_battle.pokeBtn5.setEnabled(false);
			else
				m_battle.pokeBtn5.setEnabled(true);
		} catch (NullPointerException e) {
			m_battle.pokeBtn5.setEnabled(false);
		}
		try {
			m_battle.pokeBtn6.setText(m_ourPokes[5].getName());
			m_battle.info6
					.setText("Lv: " + m_ourPokes[5].getLevel() + " HP:"
							+ m_ourPokes[5].getCurHP() + "/"
							+ m_ourPokes[5].getMaxHP());
			if (m_ourPokes[5].getCurHP() <= 0)
				m_battle.pokeBtn6.setEnabled(false);
			else
				m_battle.pokeBtn6.setEnabled(true);
		} catch (NullPointerException e) {
			m_battle.pokeBtn6.setEnabled(false);
		}

	}

	/**
	 * Switches a pokemon on the field
	 * 
	 * @param trainerIndex
	 * @param pokeIndex
	 */
	public void switchPoke(int trainerIndex, int pokeIndex, boolean isForced) {
		if (trainerIndex == 0) {
			// TODO: Draw our poke
			updateMoves(pokeIndex);
			m_battle.switchPoke(isForced);
		} else {
			// TODO: Draw enemy poke
		}
	}

	/**
	 * Requests a move from the player
	 */
	public void requestMoves() {
		m_battle.showAttack();
	}
}
