package org.pokenet.server.battle.impl;

import org.pokenet.server.backend.entity.NonPlayerChar;
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
 * The battlefield for NPC battles
 * @author shadowkanji
 *
 */
public class NpcBattleField extends BattleField {
	private PlayerChar m_player;
	private NonPlayerChar m_npc;
	private BattleTurn[] m_turn = new BattleTurn[2];

	/**
	 * Constructor
	 * @param mech
	 * @param p
	 * @param n
	 */
	public NpcBattleField(BattleMechanics mech, PlayerChar p, NonPlayerChar n) {
		super(mech, new Pokemon[][] { p.getParty(), n.getParty(p) });
		/* Store the player and npc */
		m_player = p;
		m_npc = n;

		/* Start the battle */
		m_player.getSession().write("bi0" + m_pokemon[1].length);

		/* Set the player's battle id */
		m_player.setBattleId(0);
		/* Send enemy name and pokemon data */
		m_player.getSession().write("bn" + m_npc.getName());
		sendPokemonData(p);
		/* Apply weather and request moves */
		applyWeather();
		requestMoves();
	}
	
	/**
	 * Sends pokemon data to the client
	 * @param receiver
	 */
	private void sendPokemonData(PlayerChar receiver) {
		for (int i = 0; i < this.getParty(1).length; i++) {
			if (this.getParty(1)[i] != null) {
				receiver.getSession().write(
						"bP" + i + "," + this.getParty(1)[i].getName() + ","
								+ this.getParty(1)[i].getLevel() + ","
								+ this.getParty(1)[i].getGender() + ","
								+ this.getParty(1)[i].getHealth() + ","
								+ this.getParty(1)[i].getStat(0) + ","
								+ this.getParty(1)[i].getSpeciesNumber() + ","
								+ this.getParty(1)[i].isShiny());
			}
		}
	}

	@Override
	public void applyWeather() {
		if (m_player.getMap().isWeatherForced()) {
			switch (m_player.getMap().getWeather()) {
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
		if(idx == 0)
			return m_player.getName();
		else
			return m_npc.getName();
	}

	@Override
	public void informPokemonFainted(int trainer, int idx) {
		if (m_player != null)
			m_player.getSession().write(
					"bF" + this.getParty(trainer)[idx].getSpeciesName());
	}

	@Override
	public void informPokemonHealthChanged(Pokemon poke, int change) {
		if (m_player != null) {
			if (getActivePokemon()[0] == poke) {
				m_player.getSession().write("bh0," + change);
			} else {
				m_player.getSession().write("bh1," + change);
			}
		}
	}

	@Override
	public void informStatusApplied(Pokemon poke, StatusEffect eff) {
		if (m_player != null) {
			if (poke == getActivePokemon()[0])
				m_player.getSession().write(
						"be0" + poke.getSpeciesName() + "," + eff.getName());
			else
				m_player.getSession().write(
						"be1" + poke.getSpeciesName() + "," + eff.getName());
		}
	}

	@Override
	public void informStatusRemoved(Pokemon poke, StatusEffect eff) {
		if (m_player != null) {
			if (poke == getActivePokemon()[0])
				m_player.getSession().write(
						"bE0" + poke.getSpeciesName() + "," + eff.getName());
			else
				m_player.getSession().write(
						"bE1" + poke.getSpeciesName() + "," + eff.getName());
		}
	}

	@Override
	public void informSwitchInPokemon(int trainer, Pokemon poke) {
		if(m_player != null) {
			if (trainer == 0) {
				m_player.getSession().write(
						"bS" + m_player.getName() + "," + poke.getSpeciesName()
								+ "," + trainer + "," + getPokemonPartyIndex(poke));
			} else {
				m_player.getSession().write(
						"bS" + m_npc.getName() + "," + poke.getSpeciesName()
								+ "," + trainer + "," + getPokemonPartyIndex(poke));
			}
		}
	}

	@Override
	public void informUseMove(Pokemon poke, String name) {
		if (m_player != null)
			m_player.getSession().write(
					"bM" + poke.getSpeciesName() + "," + name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void informVictory(int winner) {
		if (winner == 0) {
			m_player.removeTempStatusEffects();
			m_player.getSession().write("b@w");
		} else {
			m_player.getSession().write("b@l");
			m_player.lostBattle();
		}
		m_player.setBattling(false);
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
							// The move has no more PP
							if (this.getActivePokemon()[trainer].getPp(move
									.getId()) <= 0) {
								if (trainer == 0) {
									m_player.getSession()
											.write("bp"+ this.getActivePokemon()
													[trainer].getMoveName(move.getId()));
								} else {
									/* Get another move from the npc */
									requestMove(1);
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
		/* Ensures the npc selected a move */
		if(trainer == 0 && m_turn[0] != null && m_turn[1] == null) {
			requestMove(1);
			return;
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
		m_player.getSession().write(
				"bh0" + this.getActivePokemon()[0].getHealth());
		m_player.getSession().write(
				"bh1" + this.getActivePokemon()[1].getHealth());
	}

	@Override
	public void requestAndWaitForSwitch(int party) {
		requestPokemonReplacement(party);
		if (party == 0) {
			/* Request a switch from the player */
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
	}

	@Override
	protected void requestMove(int trainer) {
		if(trainer == 0) {
			/* Request move from player */
			m_player.getSession().write("bm");
		} else {
			/* Request move from npc */
			try {
				if(getActivePokemon()[1].hasTypeWeakness(getActivePokemon()[0])
						&& this.getAliveCount(1) >= 2) {
					/* The npc should switch out a different Pokemon */
					/* 50:50 chance they will switch */
					if(this.getMechanics().getRandom().nextInt(2) == 0) {
						requestPokemonReplacement(1);
						return;
					}
				}
				/* If they did not switch, select a move */
				int moveID = getMechanics().getRandom().nextInt(4);
				while (getActivePokemon()[1].getMove(moveID) == null)
					moveID = getMechanics().getRandom().nextInt(4);
				queueMove(1, BattleTurn.getMoveTurn(moveID));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void requestMoves() {
		clearQueue();
		requestMove(1);
		requestMove(0);
	}

	@Override
	protected void requestPokemonReplacement(int i) {
		if(i == 0) {
			/* Request Pokemon replacement from player */
			m_player.getSession().write("bs");
		} else {
			/* Request Pokemon replacement from npc */
			try {
				Thread t = Thread.currentThread();
				/*
				 * Ensure we're not queueing a switch inside the dispatch thread,
				 * this will cause an infinite loop.
				 * So if it is the dispatch thread, we'll respond in a new thread
				 */
				if(t == m_dispatch) {
					Thread nt = new Thread(new Runnable() {
						public void run() {
							try {
								/* Sleep for a moment to allow dispatch thread to finish */
								Thread.sleep(1500);
								/* Carry out the switch */
								requestPokemonReplacement(1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					nt.start();
				} else {
					/*
					 * This is outside the dispatch thread,
					 * safe to respond like this
					 */
					int index = 0;
					while(this.getParty(1)[index] == null ||
							this.getParty(1)[index].getHealth() < 1)
						index = getMechanics().getRandom().nextInt(6);
					this.queueMove(1, BattleTurn.getSwitchTurn(index));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void showMessage(String message) {
		m_player.getSession().write("b!" + message);
	}

}
