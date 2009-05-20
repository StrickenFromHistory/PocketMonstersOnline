package org.pokenet.server.battle.impl;

import org.pokenet.server.backend.entity.NonPlayerChar;
import org.pokenet.server.backend.entity.PlayerChar;
import org.pokenet.server.battle.BattleField;
import org.pokenet.server.battle.BattleTurn;
import org.pokenet.server.battle.Pokemon;
import org.pokenet.server.battle.mechanics.BattleMechanics;
import org.pokenet.server.battle.mechanics.MoveQueueException;
import org.pokenet.server.battle.mechanics.statuses.StatusEffect;

/**
 * The battlefield for NPC battles
 * @author shadowkanji
 *
 */
public class NpcBattleField extends BattleField {
	private PlayerChar m_player;
	private NonPlayerChar m_npc;

	/**
	 * Constructor
	 * @param mech
	 * @param p
	 * @param n
	 */
	public NpcBattleField(BattleMechanics mech, PlayerChar p, NonPlayerChar n) {
		super(mech, new Pokemon[][] { p.getParty(), n.getParty(p) });
		m_player = p;
		m_npc = n;
		applyWeather();
		requestMoves();
	}

	@Override
	public void applyWeather() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearQueue() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BattleTurn[] getQueuedTurns() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTrainerName(int idx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void informPokemonFainted(int trainer, int idx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void informPokemonHealthChanged(Pokemon poke, int change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void informStatusApplied(Pokemon poke, StatusEffect eff) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void informStatusRemoved(Pokemon poke, StatusEffect eff) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void informSwitchInPokemon(int trainer, Pokemon poke) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void informUseMove(Pokemon poke, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void informVictory(int winner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void queueMove(int trainer, BattleTurn move)
			throws MoveQueueException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshActivePokemon() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestAndWaitForSwitch(int party) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void requestMove(int trainer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void requestMoves() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void requestPokemonReplacement(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showMessage(String message) {
		// TODO Auto-generated method stub
		
	}

}
