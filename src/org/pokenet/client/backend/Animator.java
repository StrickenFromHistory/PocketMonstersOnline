package org.pokenet.client.backend;

import org.pokenet.client.backend.entity.Player;
import org.pokenet.client.backend.entity.Player.Direction;

public class Animator {
	private ClientMapMatrix m_mapMatrix;

	private static final int ANIMATION_INCREMENT = 4;

	// Sets up calls
	public Animator(ClientMapMatrix maps) {
		m_mapMatrix = maps;
	}

	// Prepares for animation
	public void animate() {
		try {
			ClientMap map = m_mapMatrix.getCurrentMap();
			if(map != null) {
				for(int i = 0; i < m_mapMatrix.getPlayers().size(); i++) {
					animatePlayer(m_mapMatrix.getPlayers().get(i));
				}
			}
		} catch (Exception e) {}
	}

	/**
	 * Animates players moving
	 * @param p
	 */
	private void animatePlayer(Player p) {
		/*
		 * Keep the screen following the player, i.e. move the map also
		 */
		if (p.isOurPlayer()) {
			if (p.getX() > p.getServerX()) {
				m_mapMatrix.getCurrentMap().setXOffset(
						(m_mapMatrix.getCurrentMap().getXOffset() + ANIMATION_INCREMENT),
						true);
			} else if (p.getX() < p.getServerX()) {
				m_mapMatrix.getCurrentMap().setXOffset(
						(m_mapMatrix.getCurrentMap().getXOffset() - ANIMATION_INCREMENT),
						true);
			} else if (p.getY() > p.getServerY()) {
				m_mapMatrix.getCurrentMap().setYOffset(
						(m_mapMatrix.getCurrentMap().getYOffset() + ANIMATION_INCREMENT),
						true);
			} else if (p.getY() < p.getServerY()) {
				m_mapMatrix.getCurrentMap().setYOffset(
						(m_mapMatrix.getCurrentMap().getYOffset() - ANIMATION_INCREMENT),
						true);
			}
		}
		/*
		 * Move the player
		 */
		if (p.getX() > p.getServerX()) {
			if(p.getX() % 32 == 0) {
				p.setDirection(Direction.Left);
				p.m_leftOrRight = !p.m_leftOrRight;
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), true, p.m_leftOrRight, p.getSprite()));
			}
			p.setX(p.getX() - ANIMATION_INCREMENT);
		} else if (p.getX() < p.getServerX()) {
			if(p.getX() % 32 == 0) {
				p.setDirection(Direction.Right);
				p.m_leftOrRight = !p.m_leftOrRight;
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), true, p.m_leftOrRight, p.getSprite()));
			}
			p.setX(p.getX() + ANIMATION_INCREMENT);
		} else if (p.getY() > p.getServerY()) {
			if((p.getY() + 8) % 32 == 0) {
				p.setDirection(Direction.Up);
				p.m_leftOrRight = !p.m_leftOrRight;
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), true, p.m_leftOrRight, p.getSprite()));
			}
			p.setY(p.getY() - ANIMATION_INCREMENT);
		} else if (p.getY() < p.getServerY()) {
			if((p.getY() + 8) % 32 == 0) {
				p.setDirection(Direction.Down);
				p.m_leftOrRight = !p.m_leftOrRight;
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), true, p.m_leftOrRight, p.getSprite()));
			}
			p.setY(p.getY() + ANIMATION_INCREMENT);
		}
		/*
		 * The player is now in sync with the server, stop moving/animating them
		 */
		if (p.getX() == p.getServerX() && p.getY() == p.getServerY() && p.isAnimating()) {
			p.setDirection(p.getDirection());
			p.setAnimating(false);
			p.loadSpriteImage();
		}
	}
}
