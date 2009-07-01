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
import org.pokenet.server.network.ProtocolHandler;
import org.pokenet.server.network.message.battle.BattleEndMessage;
import org.pokenet.server.network.message.battle.BattleInitMessage;
import org.pokenet.server.network.message.battle.BattleMessage;
import org.pokenet.server.network.message.battle.BattleMoveMessage;
import org.pokenet.server.network.message.battle.BattleMoveRequest;
import org.pokenet.server.network.message.battle.BattleRewardMessage;
import org.pokenet.server.network.message.battle.EnemyDataMessage;
import org.pokenet.server.network.message.battle.FaintMessage;
import org.pokenet.server.network.message.battle.HealthChangeMessage;
import org.pokenet.server.network.message.battle.NoPPMessage;
import org.pokenet.server.network.message.battle.StatusChangeMessage;
import org.pokenet.server.network.message.battle.SwitchMessage;
import org.pokenet.server.network.message.battle.SwitchRequest;
import org.pokenet.server.network.message.battle.BattleEndMessage.BattleEnd;
import org.pokenet.server.network.message.battle.BattleRewardMessage.BattleRewardType;

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
		ProtocolHandler.writeMessage(p.getSession(), 
				new BattleInitMessage(false, getAliveCount(1)));
		/* Send enemy's Pokemon data */
		sendPokemonData(p);
		/* Set the player's battle id */
		m_player.setBattleId(0);
		/* Send enemy name */
		m_player.getSession().write("bn" + m_npc.getName());
		/* Apply weather and request moves */
		//applyWeather();
		requestMoves();
	}
	
	/**
	 * Sends pokemon data to the client
	 * @param receiver
	 */
	private void sendPokemonData(PlayerChar receiver) {
		for (int i = 0; i < this.getParty(1).length; i++) {
			if (this.getParty(1)[i] != null) {
				ProtocolHandler.writeMessage(receiver.getSession(), 
						new EnemyDataMessage(i, getParty(1)[i]));
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
			ProtocolHandler.writeMessage(m_player.getSession(), 
					new FaintMessage(getParty(trainer)[idx].getSpeciesName()));
	}

	@Override
	public void informPokemonHealthChanged(Pokemon poke, int change) {
		if (m_player != null) {
			if (getActivePokemon()[0] == poke) {
				ProtocolHandler.writeMessage(m_player.getSession(), 
						new HealthChangeMessage(0 , change));
			} else {
				ProtocolHandler.writeMessage(m_player.getSession(), 
						new HealthChangeMessage(1 , change));
			}
		}
	}

	@Override
	public void informStatusApplied(Pokemon poke, StatusEffect eff) {
		if (m_player != null) {
			if (getActivePokemon()[0].compareTo(poke) == 0)
				ProtocolHandler.writeMessage(m_player.getSession(), 
						new StatusChangeMessage(0, 
								poke.getSpeciesName(), 
								eff.getName(), false));
			else
				ProtocolHandler.writeMessage(m_player.getSession(), 
						new StatusChangeMessage(1, 
								poke.getSpeciesName(), 
								eff.getName(), false));
		}
	}

	@Override
	public void informStatusRemoved(Pokemon poke, StatusEffect eff) {
		if (m_player != null) {
			if (getActivePokemon()[0].compareTo(poke) == 0)
				ProtocolHandler.writeMessage(m_player.getSession(), 
						new StatusChangeMessage(0, 
								poke.getSpeciesName(), 
								eff.getName(), true));
			else
				ProtocolHandler.writeMessage(m_player.getSession(), 
						new StatusChangeMessage(1, 
								poke.getSpeciesName(), 
								eff.getName(), true));
		}
	}

	@Override
	public void informSwitchInPokemon(int trainer, Pokemon poke) {
		if(m_player != null) {
			if (trainer == 0) {
				ProtocolHandler.writeMessage(m_player.getSession(), 
						new SwitchMessage(m_player.getName(),
								poke.getSpeciesName(),
								trainer,
								getPokemonPartyIndex(trainer, poke)));
			} else {
				ProtocolHandler.writeMessage(m_player.getSession(), 
						new SwitchMessage(m_npc.getName(),
								poke.getSpeciesName(),
								trainer,
								getPokemonPartyIndex(trainer, poke)));
			}
		}
	}

	@Override
	public void informUseMove(Pokemon poke, String name) {
		if (m_player != null)
			ProtocolHandler.writeMessage(m_player.getSession(), 
					new BattleMoveMessage(poke.getSpeciesName(), name));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void informVictory(int winner) {
		int money = getParty(1)[0].getLevel() * (getMechanics().getRandom().nextInt(4) + 1);
		if (winner == 0) {
			/* Reward the player */

			ProtocolHandler.writeMessage(m_player.getSession(), 
					new BattleRewardMessage(BattleRewardType.MONEY,
					money));
			m_player.setMoney(m_player.getMoney() + money);
			/* End the battle */
			m_player.removeTempStatusEffects();
			ProtocolHandler.writeMessage(m_player.getSession(), 
					new BattleEndMessage(BattleEnd.WON));
			if(m_npc.isGymLeader()) {
				m_player.addBadge(m_npc.getBadge());
			}
		} else {
			if(m_player.getMoney() - money >= 0) {
				m_player.setMoney(m_player.getMoney() - money);
			} else {
				m_player.setMoney(0);
			}
			m_player.updateClientMoney();
			ProtocolHandler.writeMessage(m_player.getSession(), 
					new BattleEndMessage(BattleEnd.LOST));
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
		/* Check if move exists */
		if(move.isMoveTurn() && move.getId() != -1 &&
				getActivePokemon()[trainer].getMove(move.getId()) == null) {
			requestMove(trainer);
			return;
		}
		/* Handle forced switches */
		if(m_isWaiting && m_replace != null && m_replace[trainer]) {
			if(!move.isMoveTurn()) {
				if(getActivePokemon()[trainer].compareTo(this.getParty(trainer)[move.getId()]) != 0) {
					this.switchInPokemon(trainer, move.getId());
					m_replace[trainer] = false;
					m_isWaiting = false;
					return;
				}
			}
			requestPokemonReplacement(trainer);
			return;
		}
		/* Queue the move */
		if(m_turn[trainer] == null) {
			/* Handle Pokemon being unhappy and ignoring you */
			if(trainer == 0 && !getActivePokemon()[0].isFainted()) {
				if(getActivePokemon()[0].getHappiness() <= 40) {
					/* Pokemon is unhappy, they'll do what they feel like */
					showMessage(getActivePokemon()[0].getSpeciesName() + " is unhappy!");
					int moveID = getMechanics().getRandom().nextInt(4);
					while (getActivePokemon()[0].getMove(moveID) == null)
						moveID = getMechanics().getRandom().nextInt(4);
					move = BattleTurn.getMoveTurn(moveID);
				} else if(getActivePokemon()[0].getHappiness() < 70) {
					/* Pokemon is partially unhappy, 50% chance they'll listen to you */
					if(getMechanics().getRandom().nextInt(2) == 1) {
						showMessage(getActivePokemon()[0].getSpeciesName() + " is unhappy!");
						int moveID = getMechanics().getRandom().nextInt(4);
						while (getActivePokemon()[0].getMove(moveID) == null)
							moveID = getMechanics().getRandom().nextInt(4);
						move = BattleTurn.getMoveTurn(moveID);
					}
				}
			}
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
						switchInPokemon(trainer, move.getId());
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
							return;
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
									ProtocolHandler.writeMessage(m_player.getSession(), 
											new NoPPMessage(this.getActivePokemon()[trainer]
												.getMoveName(move.getId())));
									requestMove(0);
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
			m_isWaiting = true;
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
			ProtocolHandler.writeMessage(m_player.getSession(), 
					new BattleMoveRequest());
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
			ProtocolHandler.writeMessage(m_player.getSession(), 
					new SwitchRequest());
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
							this.getParty(1)[index].getHealth() < 1 ||
							this.getParty(1)[index].compareTo(getActivePokemon()[1]) == 0)
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
		if(m_player != null)
			ProtocolHandler.writeMessage(m_player.getSession(), 
				new BattleMessage(message));
	}

}
