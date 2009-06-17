package org.pokenet.server.battle.impl;

import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.MoveQueueException;
import org.pokenet.server.battle.mechanics.statuses.StatusEffect;
import org.pokenet.server.battle.mechanics.statuses.field.FieldEffect;
import org.pokenet.server.battle.mechanics.statuses.field.HailEffect;
import org.pokenet.server.battle.mechanics.statuses.field.RainEffect;
import org.pokenet.server.battle.mechanics.statuses.field.SandstormEffect;
import org.pokenet.server.feature.TimeService;

/**
 * A class which handles PvP battles
 * 
 * @author shadowkanji
 * 
 */
public class PvPBattleField extends BattleField {
	private PlayerChar[] m_players;
	private BattleTurn[] m_turn = new BattleTurn[2];

	/**
	 * Constructor
	 * 
	 * @param mech
	 * @param p1
	 * @param p2
	 */
	public PvPBattleField(BattleMechanics mech, PlayerChar p1, PlayerChar p2) {
		super(mech, new Pokemon[][] { p1.getParty(), p2.getParty() });
		/*
		 * Store the players
		 */
		m_players = new PlayerChar[2];
		m_players[0] = p1;
		m_players[1] = p2;
		/*
		 * Set the battlefield for the players
		 */
		p1.setBattleField(this);
		p2.setBattleField(this);
		/*
		 *Set the player to battling 
		 */
		p1.setBattling(true);
		p2.setBattling(true);
		/*
		 * Set battle ids
		 */
		p1.setBattleId(0);
		p2.setBattleId(1);

		/*
		 * Send battle initialisation packets
		 */
		p1.getSession().write("bi0" + p2.getPartyCount());
		p2.getSession().write("bi0" + p1.getPartyCount());
		/* Send the enemy's name to both players*/
		p1.getSession().write("bn" + p2.getName());
		p2.getSession().write("bn" + p1.getName());	
		/* Send pokemon data to both players */
		sendPokemonData(p1, p2);
		sendPokemonData(p2, p1);
		/* Apply weather and request moves */
		applyWeather();
		requestMoves();
	}

	/**
	 * Sends pokemon data for PlayerChar p to receiver
	 * 
	 * @param p
	 * @param receiver
	 */
	private void sendPokemonData(PlayerChar p, PlayerChar receiver) {
		for (int i = 0; i < p.getParty().length; i++) {
			if (p.getParty()[i] != null) {
				receiver.getSession().write(
						"bP" + i + "," + p.getParty()[i].getName() + ","
								+ p.getParty()[i].getLevel() + ","
								+ p.getParty()[i].getGender() + ","
								+ p.getParty()[i].getHealth() + ","
								+ p.getParty()[i].getStat(0) + ","
								+ p.getParty()[i].getSpeciesNumber() + ","
								+ p.getParty()[i].isShiny());
			}
		}
	}

	@Override
	public void applyWeather() {
		if (m_players[0].getMap().isWeatherForced()) {
			switch (m_players[0].getMap().getWeather()) {
			case NORMAL:
				return;
			case RAIN:
				this.applyEffect(new RainEffect());
				return;
			case HAIL:
				this.applyEffect(new HailEffect());
				return;
			case SANDSTORM:
				this.applyEffect(new SandstormEffect());
				return;
			default:
				return;
			}
		} else {
			FieldEffect f = TimeService.getWeatherEffect();
			if (f != null) {
				this.applyEffect(f);
			}
		}
	}

	@Override
	public void clearQueue() {
		m_turn[0] = null;
		m_turn[1] = null;
	}

	@Override
	public BattleTurn[] getQueuedTurns() {
		return m_turn;
	}

	@Override
	public String getTrainerName(int idx) {
		return m_players[idx].getName();
	}

	@Override
	public void informPokemonFainted(int trainer, int idx) {
		m_players[0].getSession().write(
				"bF" + this.getParty(trainer)[idx].getSpeciesName());
		m_players[1].getSession().write(
				"bF" + this.getParty(trainer)[idx].getSpeciesName());
	}

	@Override
	public void informPokemonHealthChanged(Pokemon poke, int change) {
		if (poke != null) {
			if (poke == m_players[0].getParty()[0]) {
				m_players[0].getSession().write("bh0," + change);
				m_players[1].getSession().write("bh1," + change);
			} else {
				m_players[1].getSession().write("bh0," + change);
				m_players[0].getSession().write("bh1," + change);
			}
		}
	}

	@Override
	public void informStatusApplied(Pokemon poke, StatusEffect eff) {
		if (poke != null) {
			if (poke == m_players[0].getParty()[0]) {
				m_players[0].getSession().write(
						"be0" + poke.getSpeciesName() + "," + eff.getName());
				m_players[1].getSession().write(
						"be1" + poke.getSpeciesName() + "," + eff.getName());
			} else {
				m_players[0].getSession().write(
						"be1" + poke.getSpeciesName() + "," + eff.getName());
				m_players[1].getSession().write(
						"be0" + poke.getSpeciesName() + "," + eff.getName());
			}
		}
	}

	@Override
	public void informStatusRemoved(Pokemon poke, StatusEffect eff) {
		if (poke != null) {
			if (poke == m_players[0].getParty()[0]) {
				m_players[0].getSession().write(
						"bE0" + poke.getSpeciesName() + "," + eff.getName());
				m_players[1].getSession().write(
						"bE1" + poke.getSpeciesName() + "," + eff.getName());
			} else {
				m_players[0].getSession().write(
						"bE1" + poke.getSpeciesName() + "," + eff.getName());
				m_players[1].getSession().write(
						"bE0" + poke.getSpeciesName() + "," + eff.getName());
			}
		}
	}

	@Override
	public void informSwitchInPokemon(int trainer, Pokemon poke) {
		if (trainer == 0) {
			m_players[0].getSession().write(
					"bS" + m_players[0].getName() + "," + poke.getSpeciesName()
							+ "," + trainer + "," + getPokemonPartyIndex(poke));
			m_players[1].getSession().write(
					"bS" + m_players[0].getName() + "," + poke.getSpeciesName()
							+ "," + trainer + "," + getPokemonPartyIndex(poke));
		} else {
			m_players[0].getSession().write(
					"bS" + m_players[1].getName() + "," + poke.getSpeciesName()
							+ "," + trainer + "," + getPokemonPartyIndex(poke));
			m_players[1].getSession().write(
					"bS" + m_players[1].getName() + "," + poke.getSpeciesName()
							+ "," + trainer + "," + getPokemonPartyIndex(poke));
		}
	}

	@Override
	public void informUseMove(Pokemon poke, String name) {
		m_players[0].getSession().write(
				"bM" + poke.getSpeciesName() + "," + name);
		m_players[1].getSession().write(
				"bM" + poke.getSpeciesName() + "," + name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void informVictory(int winner) {
		m_players[0].removeTempStatusEffects();
		m_players[1].removeTempStatusEffects();
		if (winner == 0) {
			m_players[0].getSession().write("b@w");
			m_players[1].getSession().write("b@l");
			m_players[1].lostBattle();
		} else {
			m_players[0].getSession().write("b@l");
			m_players[1].getSession().write("b@w");
			m_players[0].lostBattle();
		}
		m_players[0].setBattling(false);
		m_players[1].setBattling(false);
		if (m_dispatch != null) {
			/*
			 * This very bad programming but shoddy does it and forces us to do
			 * it
			 */
			Thread t = m_dispatch;
			m_dispatch = null;
			t.stop();
		}
		dispose();
	}

	@Override
	public void queueMove(int trainer, BattleTurn move)
			throws MoveQueueException {
		// The trainer has no turn queued.
		if (m_turn[trainer] == null) {
			if (move.getId() == -1) {
				if (m_dispatch == null
						&& ((trainer == 0 && m_turn[1] != null) ||
								(trainer == 1 && m_turn[0] != null))) {
					m_dispatch = new Thread(new Runnable() {
						public void run() {
							executeTurn(m_turn);
							m_dispatch = null;
						}
					});
					m_dispatch.start();
					return;
				}
			} else {
				// Handle a fainted pokemon
				if (this.getActivePokemon()[trainer].isFainted()) {
					if (!move.isMoveTurn() && this.getParty(trainer)[move.getId()] != null
							&& this.getParty(trainer)[move.getId()].getHealth() > 0) {
						this.switchInPokemon(trainer, move.getId());
						requestMoves();
						return;
					} else {
						// The player still has pokemon left
						if (getAliveCount(trainer) > 0) {
							requestPokemonReplacement(trainer);
							return;
						} else {
							// the player has no pokemon left. Announce winner
							if (trainer == 0)
								this.informVictory(1);
							else
								this.informVictory(0);
						}
					}
				} else {
					// The turn was used to attack!
					if (move.isMoveTurn()) {
						// Handles Struggle
						if (getActivePokemon()[trainer].mustStruggle())
							m_turn[trainer] = BattleTurn.getMoveTurn(-1);
						else {
							// The move has no more PP. Tell the client!
							if (this.getActivePokemon()[trainer].getPp(move
									.getId()) <= 0) {
								if (trainer == 0) {
									m_players[0]
											.getSession()
											.write("bp"+ this.getActivePokemon()
													[trainer].getMoveName(move.getId()));
								} else {
									m_players[1]
											.getSession()
											.write("bp"+ this.getActivePokemon()
													[trainer].getMoveName(move.getId()));
								}
								return;
							} else {
								// Assign the move to the turn
								m_turn[trainer] = move;
							}
						}
					} else {
						if (this.getActivePokemon()[trainer].isActive() && 
								this.getParty(trainer)[move.getId()] != null &&
								this.getParty(trainer)[move.getId()].getHealth() > 0) {
							m_turn[trainer] = move;
						} else {
							requestMove(trainer);
							return;
						}
					}
				}
			}
		}
		if (m_dispatch != null)
			return;
		// Both turns are ready to be performed 
		if (m_turn[0] != null && m_turn[1] != null) {
			m_dispatch = new Thread(new Runnable() {
				public void run() {
					executeTurn(m_turn);
					for (int i = 0; i < m_participants; ++i) {
						m_turn[i] = null;
					}
					m_dispatch = null;
				}
			});
			m_dispatch.start();
		}
	}

	@Override
	public void refreshActivePokemon() {
		m_players[0].getSession().write(
				"bh0" + this.getActivePokemon()[0].getHealth());
		m_players[0].getSession().write(
				"bh1" + this.getActivePokemon()[1].getHealth());

		m_players[1].getSession().write(
				"bh0" + this.getActivePokemon()[1].getHealth());
		m_players[1].getSession().write(
				"bh1" + this.getActivePokemon()[0].getHealth());
	}

	@Override
	public void requestAndWaitForSwitch(int party) {
		requestPokemonReplacement(party);
		if (!m_replace[party]) {
			return;
		}
		do {
			synchronized (m_dispatch) {
				try {
					m_dispatch.wait(1000);
				} catch (InterruptedException e) {
				}
			}
		} while ((m_replace != null) && m_replace[party]);
	}

	@Override
	protected void requestMove(int trainer) {
		m_players[trainer].getSession().write("bm");
	}

	@Override
	protected void requestMoves() {
		clearQueue();
		if (this.getActivePokemon()[0].isActive()
				&& this.getActivePokemon()[1].isActive()) {
			m_players[0].getSession().write("bm");
			m_players[1].getSession().write("bm");
		}
	}

	@Override
	protected void requestPokemonReplacement(int i) {
		m_players[i].getSession().write("bs");
	}

	@Override
	public void showMessage(String message) {
		m_players[0].getSession().write("b!" + message);
		m_players[1].getSession().write("b!" + message);
	}

}
