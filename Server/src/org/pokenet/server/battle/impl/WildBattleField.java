package org.pokenet.server.battle.impl;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.DataService;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.MoveQueueException;
import org.pokenet.server.battle.mechanics.polr.POLRDataEntry;
import org.pokenet.server.battle.mechanics.polr.POLREvolution;
import org.pokenet.server.battle.mechanics.statuses.StatusEffect;
import org.pokenet.server.battle.mechanics.statuses.field.FieldEffect;
import org.pokenet.server.battle.mechanics.statuses.field.HailEffect;
import org.pokenet.server.battle.mechanics.statuses.field.RainEffect;
import org.pokenet.server.battle.mechanics.statuses.field.SandstormEffect;
import org.pokenet.server.feature.TimeService;

/**
 * Handles wild battles
 * @author shadowkanji
 *
 */
public class WildBattleField extends BattleField {
	private PlayerChar m_player;
	private Pokemon m_wildPoke;
	BattleTurn[] m_turn = new BattleTurn[2];
	private int m_runCount;
	Set<Pokemon> m_participatingPokemon = new LinkedHashSet<Pokemon>();
	
	/**
	 * Constructor
	 * @param m
	 * @param p
	 * @param wild
	 */
	public WildBattleField(BattleMechanics m, PlayerChar p, Pokemon wild) {
		super(m, new Pokemon[][] { p.getParty(), new Pokemon[] { wild }});
		/* Send information to client */
		p.setBattling(true);
		p.setBattleId(0);
		p.getSession().write("bi1");
		p.getSession().write("bP0," + wild.getName() + "," + wild.getLevel() + "," + wild.getGender() + "," 
				+ wild.getHealth() + "," + wild.getHealth() + ","  + wild.getSpeciesNumber() + "," +
				wild.isShiny());
		
		/* Store variables */
		m_player = p;
		m_wildPoke = wild;
		m_participatingPokemon.add(p.getParty()[0]);
		
		/* Call methods */
		applyWeather();
		requestMoves();
	}

	/**
	 * Applies weather effect based on world/map weather
	 */
	@Override
	public void applyWeather() {
		if(m_player.getMap().isWeatherForced()) {
			switch(m_player.getMap().getWeather()) {
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
			if(f != null) {
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
		if(idx == 0) {
			return m_player.getName();
		} else {
			return m_wildPoke.getSpeciesName();
		}
	}

	@Override
	public void informPokemonFainted(int trainer, int idx) {
		if(m_player != null)
			m_player.getSession().write("bF" + this.getParty(trainer)[idx].getSpeciesName());
	}

	@Override
	public void informPokemonHealthChanged(Pokemon poke, int change) {
		if(m_player != null) {
			if(getActivePokemon()[0] == poke) {
				m_player.getSession().write("bh0," + change);
			} else {
				m_player.getSession().write("bh1," + change);
			}
		}
	}

	@Override
	public void informStatusApplied(Pokemon poke, StatusEffect eff) {
		if(m_player != null){
			if (poke != m_wildPoke)
				m_player.getSession().write("be0" + poke.getSpeciesName() + "," + eff.getName());
			else
				m_player.getSession().write("be1" + poke.getSpeciesName() + "," + eff.getName());
		}
	}

	@Override
	public void informStatusRemoved(Pokemon poke, StatusEffect eff) {
		if(m_player != null){
			if (poke != m_wildPoke)
				m_player.getSession().write("bE0" + poke.getSpeciesName() + "," + eff.getName());
			else
				m_player.getSession().write("bE1" + poke.getSpeciesName() + "," + eff.getName());
		}
	}

	@Override
	public void informSwitchInPokemon(int trainer, Pokemon poke) {
		if(trainer == 0 && m_player != null) {
			m_player.getSession().write("bS" + m_player.getName() + "," + poke.getSpeciesName());
		}
	}

	@Override
	public void informUseMove(Pokemon poke, String name) {
		if(m_player != null)
			m_player.getSession().write("bM" + poke.getSpeciesName() + "," + name);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void informVictory(int winner) {
		if(winner == 0) {
			calculateExp();
			m_player.getSession().write("b@w");
		} else {
			m_player.getSession().write("b@l");
			m_player.lostBattle();
		}
		m_player.setBattling(false);
		if (m_dispatch != null) {
			/*
			 * This very bad programming but shoddy does it
			 * and forces us to do it
			 */
            Thread t = m_dispatch;
            m_dispatch = null;
            t.stop();
		}
        dispose();
	}
	
	/**
	 * Queues a battle turn
	 */
	@Override
	public void queueMove(int trainer, BattleTurn move)
			throws MoveQueueException {
		if (m_turn[trainer] == null) {
			System.out.println("Queueing move for trainer No." + trainer);
            if (move.getId() == -1) {
            	if(m_dispatch == null && (trainer == 0 && m_turn[1] != null)) {
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
                            if (!move.isMoveTurn()) {
                                    this.switchInPokemon(trainer, move.getId());
                                    requestMoves();
                                    if (!m_participatingPokemon.contains(getActivePokemon()[0]))
                                        m_participatingPokemon.add(getActivePokemon()[0]);
                                    return;
                            } else {
                                    if (trainer == 0 && getAliveCount(0) > 0) {
                                    	if(getAliveCount(0) > 0) {
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
                                    if (getActivePokemon()[trainer].mustStruggle())
                                            m_turn[trainer] = BattleTurn.getMoveTurn(-1);
                                    else {
                                            if (this.getActivePokemon()[trainer].getPp(move.getId()) <= 0) {
                                                    if (trainer == 0) {
                                                    	showMessage("Sorry, the move " +
                                                                            this.getActivePokemon()[trainer].getMoveName(
                                                                                            move.getId()) + " has no PP left. " +
                                                            "Select a different move.");
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
                                    if (this.m_pokemon[trainer][move.getId()].isActive()) {
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
		if(trainer == 0 && m_turn[1] == null) {
			requestMove(1);
			return;
		}
		if(m_dispatch != null)
			return;
		if(m_turn[0] != null && m_turn[1] != null) {
			System.out.println("Executing Turn");
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
		m_player.getSession().write("bh0" + this.getActivePokemon()[0].getHealth());
		m_player.getSession().write("bh1" + this.getActivePokemon()[1].getHealth());
	}

	/**
	 * Requests a new Pokemon (called by moves that force poke switches)
	 */
	@Override
	public void requestAndWaitForSwitch(int party) {
		if(party == 0) {
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
	}
	
	/**
	 * Generates a wild Pokemon move
	 */
	protected void getWildPokemonMove() {
		try {
            int moveID = getMechanics().getRandom().nextInt(4);
            while (getActivePokemon()[1].getMove(moveID) == null)
                    moveID = getMechanics().getRandom().nextInt(4);
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
		if(this.getActivePokemon()[0].isActive() &&
                this.getActivePokemon()[1].isActive()) {
			getWildPokemonMove();
			m_player.getSession().write("bm");
		}
	}

	/**
	 * Requests a pokemon replacement
	 */
	@Override
	protected void requestPokemonReplacement(int i) {
		if(i == 0) {
			/*
			 * 0 = our player in this case
			 */
			m_player.getSession().write("bs");
		}
	}

	@Override
	public void showMessage(String message) {
		m_player.getSession().write("b!" + message);
	}

	/**
	 * Returns true if the player can run from the battle
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
		if(canRun()) {
			m_player.getSession().write("br1");
			m_player.setBattling(false);
			this.dispose();
		} else {
			m_player.getSession().write("br2");
			if(m_turn[1] == null)
				this.getWildPokemonMove();
			try {
				this.queueMove(0, BattleTurn.getMoveTurn(-1));
			} catch (MoveQueueException e) {
				e.printStackTrace();
			}
		}
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
		if(trainer == 0) {
			/*
			 * If its the player, send a move request packet
			 */
			m_player.getSession().write("bm");
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
		m_player.setMoney(m_player.getMoney() + 5);
		showMessage("You earned 5 PD!");
		
		/*
		 * Secondly, calculate EVs and exp
		 */
		POLRDataEntry poke = DataService.getPOLRDatabase().
			getPokemonData(DataService.getSpeciesDatabase().getPokemonByName(m_wildPoke.getSpeciesName()));
		int [] evs = poke.getEffortPoints();
		
		double exp = (DataService.getBattleMechanics().calculateExpGain
			(m_wildPoke, m_participatingPokemon.size(), false)) / 2;
		if(exp == 0)
			exp = 1;
		/*
		 * Finally, add the EVs and exp to the participating Pokemon
		 */
		Iterator<Pokemon> it = m_participatingPokemon.iterator();
		Pokemon p = null;
		while(it.hasNext()) {
			p = it.next();
			int index = m_player.getPokemonIndex(p);
			
			/* Add the evs */
			/* Ensure EVs don't go over limit, before or during addition */
			int evTotal = p.getEvTotal();
			if(evTotal < 510) {
				for(int i = 0; i < evs.length; i++) {
					/* Ensure we don't hit the EV limit */
					if(evTotal + evs[i] < 510) {
						if(p.getEv(i) < 255) {
							if(p.getEv(i) + evs[i] < 255) {
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
						 * We're going to hit the EV total limit
						 * Only add what's allowed
						 */
						evs[i] = 510 - evTotal;
						if(p.getEv(i) + evs[i] < 255)
							p.setEv(i, p.getEv(i) + evs[i]);
						else
							p.setEv(i, 255);
						break;
					}
				}
			}
			
			/* Gain exp/level up and update client */
			p.setExp(p.getExp() + exp);
			m_player.getSession().write("b." + p.getSpeciesName() + "," + exp);
			String expGain = exp + "";
			expGain = expGain.substring(0, expGain.indexOf('.'));
			m_player.getSession().write("Pe" + index + expGain);
			
			double levelExp = DataService.getBattleMechanics().getExpForLevel(p, p.getLevel() + 1) - p.getExp();
			if(levelExp <= 0) {
				POLRDataEntry pokeData = DataService.getPOLRDatabase().getPokemonData(
						DataService.getSpeciesDatabase().getPokemonByName(p.getSpeciesName()));
				
				/* Handle evolution */
				for(int i = 0; i < pokeData.getEvolutions().size(); i++) {
					POLREvolution evolution = pokeData.getEvolutions().get(i);
					switch(evolution.getType()) {
					case Level:
						if(evolution.getLevel() == p.getLevel() + 1) {
							p.setEvolution(evolution);
							m_player.getSession().write("Pe" + index);
							return;
						}
						break;
					case HappinessDay:
						if(p.getHappiness() > 220 && !TimeService.isNight()) {
							p.setEvolution(evolution);
							m_player.getSession().write("Pe" + index);
							return;
						}
						break;
					case HappinessNight:
						if(p.getHappiness() > 220 && TimeService.isNight()) {
							p.setEvolution(evolution);
							m_player.getSession().write("Pe" + index);
							return;
						}
						break;
					case Happiness:
						if(p.getHappiness() > 220) {
							p.setEvolution(evolution);
							m_player.getSession().write("Pe" + index);
							return;
						}
						break;
					case Beauty:
						break;
					}
				}
				
				/* This Pokemon just, levelled up! */
				p.setHappiness(p.getHappiness() + 2);
				p.calculateStats(false);
				

				int level = DataService.getBattleMechanics().calculateLevel(p);
				int oldLevel = p.getLevel();
				String move = "";
				/* Move learning */
				p.getMovesLearning().clear();
				for(int i = oldLevel; i < level; i++) {
					if(pokeData.getMoves().get(i) != null) {
						move = pokeData.getMoves().get(i);
						p.getMovesLearning().add(move);
						m_player.getSession().write("Pm" + index + move);
					}
				}
				/* Save the level and update the client */
				p.setLevel(level);
				m_player.getSession().write("Pl" + index + "," + level);
				showMessage(p.getSpeciesName() + " reached level " + level + "!");
			}
		}
	}
}
