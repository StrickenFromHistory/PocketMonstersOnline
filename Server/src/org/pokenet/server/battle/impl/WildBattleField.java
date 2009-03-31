package org.pokenet.server.battle.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.pokenet.server.GameServer;
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
 * Handles wild battles
 * @author shadowkanji
 *
 */
public class WildBattleField extends BattleField {
	private PlayerChar m_player;
	private Pokemon m_wildPoke;
	BattleTurn[] m_queuedTurns = new BattleTurn[2];
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
		m_player = p;
		applyWeather();
		m_player.getSession().write("bi");
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
			case HAIL:
				this.applyEffect(new HailEffect());
			case SANDSTORM:
				this.applyEffect(new SandstormEffect());
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
		return m_queuedTurns;
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
		m_player.getSession().write("bF" + this.getParty(trainer)[idx].getSpeciesName());
	}

	@Override
	public void informPokemonHealthChanged(Pokemon poke, int change) {
		m_player.getSession().write("bH" + poke.getSpeciesName() + "," + change);
	}

	@Override
	public void informStatusApplied(Pokemon poke, StatusEffect eff) {
		m_player.getSession().write("be" + poke.getSpeciesName() + "," + eff.getName());
	}

	@Override
	public void informStatusRemoved(Pokemon poke, StatusEffect eff) {
		m_player.getSession().write("bE" + poke.getSpeciesName() + "," + eff.getName());
	}

	@Override
	public void informSwitchInPokemon(int trainer, Pokemon poke) {
		if(trainer == 0) {
			m_player.getSession().write("bS" + m_player.getName() + "," + poke.getSpeciesName());
		}
	}

	@Override
	public void informUseMove(Pokemon poke, String name) {
		m_player.getSession().write("bM" + poke.getSpeciesName() + "," + name);
	}

	@Override
	public void informVictory(int winner) {
		if(winner == 0) {
			m_player.getSession().write("b@w");
			//TODO: Exp gain
		} else {
			m_player.getSession().write("b@l");
		}
	}

	/**
	 * Returns true if we are ready to execute the turn
	 */
	@Override
	public boolean isReady() {
		return m_queuedTurns[0] != null && m_queuedTurns[1] != null;
	}
	
	/**
	 * Queues a battle turn
	 */
	@Override
	public void queueMove(int trainer, BattleTurn move)
			throws MoveQueueException {
		if (m_queuedTurns[trainer] == null) {
			if (move.getId() == -1) {
				//No idea what this does but it worked in PG
				if (trainer == 0 && m_queuedTurns[1] != null)
                    executeTurn(m_queuedTurns);	
			} else {
				//Handle faints
				if (this.getActivePokemon()[trainer].isFainted()) {
					 if (!move.isMoveTurn()) {
						 /*
						  * If it is not an actual move, 
						  * it is assumed it was a Pokemon switch.
						  * So switch in Pokemon and request moves
						  */
                         this.switchInPokemon(trainer, move.getId());
                         requestMoves();
					 } else {
						 //Else, request moves from trainer
						 if (trainer == 0) {
                             if (m_participatingPokemon.contains(getActivePokemon()[0]))
                                     m_participatingPokemon.remove(getActivePokemon()[0]);
                             m_player.getSession().write("bs");
						 }
					 }
				} else {
					if (move.isMoveTurn()) {
						//It is a proper move
						//If struggling, struggle!
						if (getActivePokemon()[trainer].mustStruggle())
                            m_queuedTurns[trainer] = BattleTurn.getMoveTurn(-1);
						else {
							//Lets try use that move
							//First check PP, if none, request different move
							if (this.getActivePokemon()[trainer].getPp(move.getId()) <= 0) {
                                if (trainer == 0) {
                                        m_player.getSession().write("bp" +
                                                        this.getActivePokemon()[trainer].getMoveName(
                                                                        move.getId()));
                                        m_player.getSession().write("bm");
                                }
							} else {
								//Else, queue the move
								 m_queuedTurns[trainer] = move;
							}
						}
					} else {
						/* No idea what this part does but it worked in PG */
						if (this.m_pokemon[trainer][move.getId()].isActive()) {
                            m_queuedTurns[trainer] = move;
						} else {
							if(trainer == 0)
								m_player.getSession().write("bm");
						}
					}
				}
			}
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
	 * Requests a new Pokemon (called when a Pokemon faints)
	 */
	@Override
	public void requestAndWaitForSwitch(int party) {
		if(party == 0) {
			m_player.getSession().write("bs");
		}
	}

	/**
	 * Requests moves
	 */
	@Override
	protected void requestMoves() {
		if(this.getActivePokemon()[0].isActive() &&
                this.getActivePokemon()[1].isActive() && !this.isFinished()) {
			m_player.getSession().write("bm");
	        try {
	                int moveID = getMechanics().getRandom().nextInt(4);
	                while (getActivePokemon()[1].getMove(moveID) == null)
	                        moveID = getMechanics().getRandom().nextInt(4);
	                queueMove(1, BattleTurn.getMoveTurn(
	                                moveID));
	        } catch (MoveQueueException x) {
	                x.printStackTrace();
	        }
		}
	}

	/**
	 * Requests a pokemon replacement
	 */
	@Override
	protected void requestPokemonReplacement(int i) {
		if(i == 0) {
			//0 = our player in this case
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
		
	}

	/**
	 * Clears the moves queue
	 */
	@Override
	public void clearQueue() {
		m_queuedTurns[0] = null;
		m_queuedTurns[1] = null;
	}
}
