
package org.pokenet.server.battle.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.pokenet.server.backend.ItemProcessor.PokeBall;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.PokemonEvolution;
import org.pokenet.server.battle.PokemonSpecies;
import org.pokenet.server.battle.PokemonEvolution.EvolutionTypes;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.MoveQueueException;
import org.pokenet.server.battle.mechanics.statuses.StatusEffect;
import org.pokenet.server.battle.mechanics.statuses.field.FieldEffect;
import org.pokenet.server.battle.mechanics.statuses.field.HailEffect;
import org.pokenet.server.battle.mechanics.statuses.field.RainEffect;
import org.pokenet.server.battle.mechanics.statuses.field.SandstormEffect;
import org.pokenet.server.feature.TimeService;
import org.pokenet.server.network.TcpProtocolHandler;
import org.pokenet.server.network.message.battle.BattleEndMessage;
import org.pokenet.server.network.message.battle.BattleExpMessage;
import org.pokenet.server.network.message.battle.BattleInitMessage;
import org.pokenet.server.network.message.battle.BattleLevelChangeMessage;
import org.pokenet.server.network.message.battle.BattleMessage;
import org.pokenet.server.network.message.battle.BattleMoveMessage;
import org.pokenet.server.network.message.battle.BattleMoveRequest;
import org.pokenet.server.network.message.battle.BattleRewardMessage;
import org.pokenet.server.network.message.battle.EnemyDataMessage;
import org.pokenet.server.network.message.battle.FaintMessage;
import org.pokenet.server.network.message.battle.HealthChangeMessage;
import org.pokenet.server.network.message.battle.NoPPMessage;
import org.pokenet.server.network.message.battle.RunMessage;
import org.pokenet.server.network.message.battle.StatusChangeMessage;
import org.pokenet.server.network.message.battle.SwitchMessage;
import org.pokenet.server.network.message.battle.SwitchRequest;
import org.pokenet.server.network.message.battle.BattleEndMessage.BattleEnd;
import org.pokenet.server.network.message.battle.BattleRewardMessage.BattleRewardType;

/**
 * A battlefield for wild Pokemon battles
 * @author shadowkanji
 */
public class WildBattleField extends BattleField {
	private final PlayerChar   m_player;
	private Pokemon            m_wildPoke;
	private final BattleTurn[] m_turn                 = new BattleTurn[2];
	private int                m_runCount;
	Set<Pokemon>               m_participatingPokemon = new LinkedHashSet<Pokemon>();
	private boolean            m_finished             = false;

	/**
	 * Constructor
	 * 
	 * @param m
	 * @param p
	 * @param wild
	 */
	public WildBattleField(BattleMechanics m, PlayerChar p, Pokemon wild) {
		super(m, new Pokemon[][] { p.getParty(), new Pokemon[] { wild } });
		/* Send information to client */
		p.setBattling(true);
		p.setBattleId(0);
		TcpProtocolHandler.writeMessage(p.getTcpSession(), new BattleInitMessage(
				true, 1));
		TcpProtocolHandler.writeMessage(p.getTcpSession(), new EnemyDataMessage(0,
				wild));

		/* Store variables */
		m_player = p;
		m_wildPoke = wild;
		m_participatingPokemon.add(p.getParty()[0]);

		/* Call methods */
		// applyWeather();
		requestMoves();
	}

	/**
	 * Applies weather effect based on world/map weather
	 */
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
	public BattleTurn[] getQueuedTurns() {
		return m_turn;
	}

	@Override
	public String getTrainerName(int idx) {
		if (idx == 0) {
			return m_player.getName();
		} else {
			return m_wildPoke.getSpeciesName();
		}
	}

	@Override
	public void informPokemonFainted(int trainer, int idx) {
		/*
		 * If the pokemon is the player's make sure it don't get exp
		 */
		if (trainer == 0 && m_participatingPokemon.contains(getParty(trainer)[idx])) {
			m_participatingPokemon.remove(getParty(trainer)[idx]);
		}
		if (m_player != null)
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new FaintMessage(getParty(trainer)[idx].getSpeciesName()));
	}

	@Override
	public void informPokemonHealthChanged(Pokemon poke, int change) {
		if (m_player != null) {
			if (getActivePokemon()[0] == poke) {
				TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
						new HealthChangeMessage(0, change));
			} else if (getActivePokemon()[1] == poke) {
				TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
						new HealthChangeMessage(1, change));
			} else {
				int index = getPokemonPartyIndex(0, poke);
				if (index > -1) {
					m_player.getTcpSession().write(
							"Ph" + String.valueOf(index) + poke.getHealth());
				}
			}
		}
	}

	@Override
	public void informStatusApplied(Pokemon poke, StatusEffect eff) {
		if (m_finished) return;
		if (m_player != null) {
			if (poke != m_wildPoke) TcpProtocolHandler.writeMessage(m_player
					.getTcpSession(), new StatusChangeMessage(0, poke.getSpeciesName(), eff
							.getName(), false));
			else if (poke == m_wildPoke)
				TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
						new StatusChangeMessage(1, poke.getSpeciesName(), eff.getName(),
								false));
		}
	}

	@Override
	public void informStatusRemoved(Pokemon poke, StatusEffect eff) {
		if (m_finished) return;
		if (m_player != null) {
			if (poke != m_wildPoke && !poke.isFainted()) TcpProtocolHandler.writeMessage(m_player
					.getTcpSession(), new StatusChangeMessage(0, poke.getSpeciesName(), eff
							.getName(), true));
			else if (poke == m_wildPoke && !poke.isFainted())
				TcpProtocolHandler
				.writeMessage(m_player.getTcpSession(), new StatusChangeMessage(1,
						poke.getSpeciesName(), eff.getName(), true));
		}
	}

	@Override
	public void informSwitchInPokemon(int trainer, Pokemon poke) {
		if (trainer == 0 && m_player != null) {
			if (!m_participatingPokemon.contains(poke))
				m_participatingPokemon.add(poke);
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new SwitchMessage(m_player.getName(), poke.getSpeciesName(), trainer,
							getPokemonPartyIndex(trainer, poke)));
		}
	}

	@Override
	public void informUseMove(Pokemon poke, String name) {
		if (m_player != null)
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new BattleMoveMessage(poke.getSpeciesName(), name));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void informVictory(int winner) {
		m_finished = true;
		if (winner == 0) {
			calculateExp();
			m_player.removeTempStatusEffects();
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new BattleEndMessage(BattleEnd.WON));
		} else {
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new BattleEndMessage(BattleEnd.LOST));
			m_player.lostBattle();
		}
		m_player.setBattling(false);
		dispose();
		m_wildPoke = null;
		if (m_dispatch != null) {
			/*
			 * This very bad programming but shoddy does it and forces us to do it
			 */
			Thread t = m_dispatch;
			m_dispatch = null;
			t.stop();
		}
	}

	/**
	 * Queues a battle turn
	 */
	@Override
	public void queueMove(int trainer, BattleTurn move) throws MoveQueueException {
		/* Checks the move exists */
		if (move.isMoveTurn() && move.getId() != -1
				&& getActivePokemon()[trainer].getMove(move.getId()) == null) {
			requestMove(trainer);
			return;
		}
		/* Handle forced switches */
		if (m_isWaiting && m_replace != null && m_replace[trainer]) {
			if (!move.isMoveTurn()) {
				if (getActivePokemon()[trainer].compareTo(this.getParty(trainer)[move
				                                                                 .getId()]) != 0) {
					this.switchInPokemon(trainer, move.getId());
					m_replace[trainer] = false;
					m_isWaiting = false;
					return;
				}
			}
			requestPokemonReplacement(trainer);
			return;
		}
		/* Ensure they haven't queued a move already */
		if (m_turn[trainer] == null) {
			/* Handle Pokemon being unhappy and ignoring you */
			if (trainer == 0 && !getActivePokemon()[0].isFainted()) {
				if (getActivePokemon()[0].getHappiness() <= 40) {
					/* Pokemon is unhappy, they'll do what they feel like */
					showMessage(getActivePokemon()[0].getSpeciesName() + " is unhappy!");
					int moveID = getMechanics().getRandom().nextInt(4);
					while (getActivePokemon()[0].getMove(moveID) == null)
						moveID = getMechanics().getRandom().nextInt(4);
					move = BattleTurn.getMoveTurn(moveID);
				} else if (getActivePokemon()[0].getHappiness() < 70) {
					/* Pokemon is partially unhappy, 50% chance they'll listen to you */
					if (getMechanics().getRandom().nextInt(2) == 1) {
						showMessage(getActivePokemon()[0].getSpeciesName() + " is unhappy!");
						int moveID = getMechanics().getRandom().nextInt(4);
						while (getActivePokemon()[0].getMove(moveID) == null)
							moveID = getMechanics().getRandom().nextInt(4);
						move = BattleTurn.getMoveTurn(moveID);
					}
				}
			}
			if (move.getId() == -1) {
				if (m_dispatch == null && (trainer == 0 && m_turn[1] != null)) {
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
				if (this.getActivePokemon()[trainer].isFainted()) {
					if (!move.isMoveTurn()
							&& this.getParty(trainer)[move.getId()] != null
							&& this.getParty(trainer)[move.getId()].getHealth() > 0) {
						switchInPokemon(trainer, move.getId());
						requestMoves();
						if (!m_participatingPokemon.contains(getActivePokemon()[0]))
							m_participatingPokemon.add(getActivePokemon()[0]);
						return;
					} else {
						if (trainer == 0 && getAliveCount(0) > 0) {
							if (getAliveCount(0) > 0) {
								if (m_participatingPokemon.contains(getActivePokemon()[0]))
									m_participatingPokemon.remove(getActivePokemon()[0]);
								requestPokemonReplacement(0);
								return;
							} else {
								/* Player lost the battle */
								this.informVictory(1);
								return;
							}
						}
					}
				} else {
					if (move.isMoveTurn()) {
						if (getActivePokemon()[trainer].mustStruggle()) m_turn[trainer] = BattleTurn
						.getMoveTurn(-1);
						else {
							if (this.getActivePokemon()[trainer].getPp(move.getId()) <= 0) {
								if (trainer == 0) {
									TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
											new NoPPMessage(this.getActivePokemon()[trainer]
											                                        .getMoveName(move.getId())));
									requestMove(0);
								} else {
									requestMove(1);
								}
								return;
							} else {
								m_turn[trainer] = move;
							}
						}
					} else {
						if (this.getActivePokemon()[trainer].isActive()
								&& this.getParty(trainer)[move.getId()] != null
								&& this.getParty(trainer)[move.getId()].getHealth() > 0) {
							m_turn[trainer] = move;
						} else {
							if (trainer == 0) {
								requestMove(0);
							}
							return;
						}
					}
				}
			}
		}
		if (trainer == 0 && m_turn[1] == null) {
			requestMove(1);
			return;
		}
		if (m_dispatch != null) return;
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

	/**
	 * Refreshes Pokemon on battlefield
	 */
	@Override
	public void refreshActivePokemon() {
		m_player.getTcpSession().write(
				"bh0" + this.getActivePokemon()[0].getHealth());
		m_player.getTcpSession().write(
				"bh1" + this.getActivePokemon()[1].getHealth());
	}

	/**
	 * Requests a new Pokemon (called by moves that force poke switches)
	 */
	@Override
	public void requestAndWaitForSwitch(int party) {
		if (party == 0) {
			requestPokemonReplacement(party);
			if (!m_replace[party]) { return; }
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

	/**
	 * Generates a wild Pokemon move
	 */
	protected void getWildPokemonMove() {
		if (getActivePokemon()[1] == null) return;
		try {
			int moveID = getMechanics().getRandom().nextInt(4);
			while (getActivePokemon()[1].getMove(moveID) == null) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				moveID = getMechanics().getRandom().nextInt(4);
				/*
				 * Stop infinite loops when player disconnects
				 */
				if (m_player.getTcpSession() == null
						|| m_player.getTcpSession().isClosing()
						|| !m_player.getTcpSession().isConnected()) break;
			}
			queueMove(1, BattleTurn.getMoveTurn(moveID));
		} catch (MoveQueueException x) {
			x.printStackTrace();
		}
	}

	/**
	 * Requests moves
	 */
	@Override
	protected void requestMoves() {
		clearQueue();
		if (this.getActivePokemon()[0].isActive()
				&& this.getActivePokemon()[1].isActive()) {
			getWildPokemonMove();
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new BattleMoveRequest());
		}
	}

	/**
	 * Requests a pokemon replacement
	 */
	@Override
	protected void requestPokemonReplacement(int i) {
		if (i == 0) {
			/*
			 * 0 = our player in this case
			 */
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new SwitchRequest());
		}
	}

	@Override
	public void showMessage(String message) {
		if (m_finished) return;
		if (m_player != null)
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new BattleMessage(message));
	}

	/**
	 * Returns true if the player can run from the battle
	 * 
	 * @return
	 */
	private boolean canRun() {
		// Formula from http://bulbapedia.bulbagarden.net/wiki/Escape
		float A = getActivePokemon()[0].getStat(Pokemon.S_SPEED);
		float B = getActivePokemon()[1].getStat(Pokemon.S_SPEED);
		int C = ++m_runCount;

		float F = (((A * 32) / (B / 4)) + 30) * C;

		if (F > 255) return true;

		if (getMechanics().getRandom().nextInt(255) <= F) return true;

		return false;
	}

	/**
	 * Attempts to run from this battle
	 */
	public void run() {
		if (canRun()) {
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(), new RunMessage(
					true));
			m_player.setBattling(false);
			this.dispose();
		} else {
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(), new RunMessage(
					false));
			if (m_turn[1] == null) this.getWildPokemonMove();
			try {
				this.queueMove(0, BattleTurn.getMoveTurn(-1));
			} catch (MoveQueueException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Throws a Pokeball. Returns true if pokemon was caught
	 * 
	 * @param p
	 * @return
	 */
	public boolean throwPokeball(PokeBall p) {
		/* Ensure user doesn't throw a Pokeball while battling */
		while (m_dispatch != null) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
		}
		switch (p) {
		case POKEBALL:
			showMessage(m_player.getName() + " threw a Pokeball!");
			if (getMechanics().isCaught(
					m_wildPoke,
					m_wildPoke.getRareness(), 1.0, 1)) {
				m_wildPoke.calculateStats(false);
				m_player.catchPokemon(m_wildPoke);
				showMessage("You successfuly caught " + m_wildPoke.getSpeciesName());
				TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
						new BattleEndMessage(BattleEnd.POKEBALL));
				m_player.setBattling(false);
				dispose();
				return true;
			} else
				showMessage("...but it failed!");
			break;
		case GREATBALL:
			showMessage(m_player.getName() + " threw a Great Ball!");
			if (getMechanics().isCaught(
					m_wildPoke,
					m_wildPoke.getRareness(), 1.5, 1)) {
				m_wildPoke.calculateStats(false);
				m_player.catchPokemon(m_wildPoke);
				showMessage("You successfuly caught " + m_wildPoke.getSpeciesName());
				TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
						new BattleEndMessage(BattleEnd.POKEBALL));
				m_player.setBattling(false);
				dispose();
				return true;
			} else
				showMessage("...but it failed!");
			break;
		case ULTRABALL:
			showMessage(m_player.getName() + " threw an Ultra Ball!");
			if (getMechanics().isCaught(
					m_wildPoke,
					m_wildPoke.getRareness(), 2.0, 1)) {
				m_wildPoke.calculateStats(false);
				m_player.catchPokemon(m_wildPoke);
				showMessage("You successfuly caught " + m_wildPoke.getSpeciesName());
				TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
						new BattleEndMessage(BattleEnd.POKEBALL));
				m_player.setBattling(false);
				dispose();
				return true;
			} else
				showMessage("...but it failed!");
			break;
		case MASTERBALL:
			showMessage(m_player.getName() + " threw a Master Ball!");
			if (getMechanics().isCaught(
					m_wildPoke,
					m_wildPoke.getRareness(), 255.0, 1)) {
				m_wildPoke.calculateStats(false);
				m_player.catchPokemon(m_wildPoke);
				showMessage("You successfuly caught " + m_wildPoke.getSpeciesName());
				TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
						new BattleEndMessage(BattleEnd.POKEBALL));
				m_player.setBattling(false);
				dispose();
				return true;
			} else
				showMessage("...but it failed!");
			break;
		}
		return false;
	}

	/**
	 * Clears the moves queue
	 */
	@Override
	public void clearQueue() {
		m_turn[0] = null;
		m_turn[1] = null;
	}

	/**
	 * Requests a move from a specific player
	 */
	@Override
	protected void requestMove(int trainer) {
		if (trainer == 0) {
			/*
			 * If its the player, send a move request packet
			 */
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new BattleMoveRequest());
		} else {
			/*
			 * If its the wild Pokemon, just get the moves
			 */
			getWildPokemonMove();
		}
	}

	/**
	 * Calculates exp gained for Pokemon at the end of battles
	 */
	private void calculateExp() {
		/*
		 * First calculate earnings
		 */
		int item = PokemonSpecies.getDefaultData().
			getPokemonByName(m_wildPoke.getSpeciesName()).getRandomItem();
		if (item > -1) {
			m_player.getBag().addItem(item, 1);
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new BattleRewardMessage(BattleRewardType.ITEM, item));
		} else {
			int money = 5;
			m_player.setMoney(m_player.getMoney() + money);
			m_player.updateClientMoney();
			TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
					new BattleRewardMessage(BattleRewardType.MONEY, money));
		}

		if (m_participatingPokemon.size() > 0) {
			double exp = (DataService.getBattleMechanics().calculateExpGain(
					m_wildPoke, m_participatingPokemon.size()));
			if (exp == 0) exp = 1;

			/*
			 * Secondly, calculate EVs and exp
			 */
			int[] evs = m_wildPoke.getEffortPoints();

			/*
			 * Finally, add the EVs and exp to the participating Pokemon
			 */
			for (Pokemon p : m_participatingPokemon) {
				int index = m_player.getPokemonIndex(p);

				/* Add the evs */
				/* Ensure EVs don't go over limit, before or during addition */
				int evTotal = p.getEvTotal();
				if (evTotal < 510) {
					for (int i = 0; i < evs.length; i++) {
						/* Ensure we don't hit the EV limit */
						if (evTotal + evs[i] < 510) {
							if (p.getEv(i) < 255) {
								if (p.getEv(i) + evs[i] < 255) {
									/* Add the EV */
									evTotal += evs[i];
									p.setEv(i, p.getEv(i) + evs[i]);
								} else {
									/* Cap the EV at 255 */
									evTotal += (255 - p.getEv(i));
									p.setEv(i, 255);
								}
							}
						} else {
							/*
							 * We're going to hit the EV total limit Only add what's allowed
							 */
							evs[i] = 510 - evTotal;
							if (p.getEv(i) + evs[i] < 255) p.setEv(i, p.getEv(i) + evs[i]);
							else
								p.setEv(i, 255);
							i = evs.length;
						}
					}
				}

				/* Gain exp/level up and update client */
				p.setExp(p.getExp() + exp);
				//Calculate how much exp is left to next level
				int expTillLvl = (int)(DataService.getBattleMechanics().getExpForLevel(p, (p.getLevel() + 1)) - p.getExp());
				//Make sure that value isn't negative.
				if (expTillLvl < 0){
					expTillLvl = 0;
				}
				TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
						new BattleExpMessage(p.getSpeciesName(), exp, expTillLvl));

				String expGain = exp + "";
				expGain = expGain.substring(0, expGain.indexOf('.'));
				m_player.getTcpSession().write("Pe" + index + expGain);

				double levelExp = DataService.getBattleMechanics().getExpForLevel(p,
						p.getLevel() + 1)
						- p.getExp();
				if (levelExp <= 0) {
					PokemonSpecies pokeData = PokemonSpecies.getDefaultData().getPokemonByName(
							p.getSpeciesName());
					boolean evolve = false;
					/* Handle evolution */
					for (int i = 0; i < pokeData.getEvolutions().length; i++) {
						PokemonEvolution evolution = pokeData.getEvolutions()[i];
						if (evolution.getType() == EvolutionTypes.Level) {
							if (evolution.getLevel() <= p.getLevel() + 1) {
								p.setEvolution(evolution);
								m_player.getTcpSession().write("PE" + index);
								evolve = true;
								i = pokeData.getEvolutions().length;
							}
						} else if (evolution.getType() == EvolutionTypes.HappinessDay) {
							if (p.getHappiness() > 220 && !TimeService.isNight()) {
								p.setEvolution(evolution);
								m_player.getTcpSession().write("PE" + index);
								evolve = true;
								i = pokeData.getEvolutions().length;
							}
						} else if (evolution.getType() == EvolutionTypes.HappinessNight) {
							if (p.getHappiness() > 220 && TimeService.isNight()) {
								p.setEvolution(evolution);
								m_player.getTcpSession().write("PE" + index);
								evolve = true;
								i = pokeData.getEvolutions().length;
							}
						} else if (evolution.getType() == EvolutionTypes.Happiness) {
							if (p.getHappiness() > 220) {
								p.setEvolution(evolution);
								m_player.getTcpSession().write("PE" + index);
								evolve = true;
								i = pokeData.getEvolutions().length;
							}
						}
					}
					/* If the Pokemon is evolving, don't move learn just yet */
					if (evolve) continue;

					/* This Pokemon just levelled up! */
					p.setHappiness(p.getHappiness() + 2);
					p.calculateStats(false);

					int level = DataService.getBattleMechanics().calculateLevel(p);
					m_player.addTrainingExp(level * 5);
					int oldLevel = p.getLevel();
					String move = "";

					/* Move learning */
					p.getMovesLearning().clear();
					for (int i = oldLevel + 1; i <= level; i++) {
						if (pokeData.getLevelMoves().get(i) != null) {
							move = pokeData.getLevelMoves().get(i);
							p.getMovesLearning().add(move);
							m_player.getTcpSession().write("Pm" + index + move);
						}
					}

					/* Save the level and update the client */
					p.setLevel(level);
					m_player.getTcpSession().write("Pl" + index + "," + level);
					TcpProtocolHandler.writeMessage(m_player.getTcpSession(),
							new BattleLevelChangeMessage(p.getSpeciesName(), level));
					m_player.updateClientPokemonStats(index);
				}
			}
		}
	}

	@Override
	public void forceExecuteTurn() {
		if (m_turn[0] == null) {
			m_turn[0] = BattleTurn.getMoveTurn(-1);
		}
		if (m_turn[1] == null) {
			m_turn[1] = BattleTurn.getMoveTurn(-1);
		}
		executeTurn(m_turn);
	}
}
