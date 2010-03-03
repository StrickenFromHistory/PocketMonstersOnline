package org.pokenet.client.backend;

import org.lwjgl.util.Timer;
import org.pokenet.client.backend.entity.Player;
import org.pokenet.client.backend.entity.Player.Direction;

public class Animator {
	private ClientMapMatrix m_mapMatrix;


	private Timer m_timer;

	private static final int ANIMATION_INCREMENT = 4;

	// Sets up calls
	public Animator(ClientMapMatrix maps) {
		m_mapMatrix = maps;
		m_timer = new Timer();
	}

	// Prepares for animation
	public void animate() {
		try {
			while (m_timer.getTime() <= 0.025)
				Timer.tick();
			ClientMap map = m_mapMatrix.getCurrentMap();
			if (map != null) {
				for(int i = 0; i < m_mapMatrix.getPlayers().size(); i++) {
						animatePlayer(m_mapMatrix.getPlayers().get(i));
				}
			}
			m_timer.reset();
		} catch (Exception e) {
			m_timer.reset();
		}
	}

	/**
	 * Animates players moving
	 * @param p
	 */
	private void animatePlayer(Player p) {
		/*
		 * Check if we need to move the player
		 */
		if(p.requiresMovement()) {
			switch(p.getNextMovement()) {
			case Down:
				if(p.getDirection() == Direction.Down)
					p.setServerY(p.getY() + 32);
				else {
					p.setDirection(Direction.Down);
					p.loadSpriteImage();
				}
				break;
			case Left:
				if(p.getDirection() == Direction.Left)
					p.setServerX(p.getX() - 32);
				else {
					p.setDirection(Direction.Left);
					p.loadSpriteImage();
				}
				break;
			case Right:
				if(p.getDirection() == Direction.Right)
					p.setServerX(p.getX() + 32);
				else {
					p.setDirection(Direction.Right);
					p.loadSpriteImage();
				}
				break;
			case Up:
				if(p.getDirection() == Direction.Up)
					p.setServerY(p.getY() - 32);
				else {
					p.setDirection(Direction.Up);
					p.loadSpriteImage();
				}
				break;
			}
		}
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
		 * Move the player on screen
		 */
		if (p.getX() > p.getServerX()) {
			// Choose the sprite according to the player's position
			if(p.getX() % 32 == 0) {
				p.setDirection(Direction.Left);
				p.m_leftOrRight = !p.m_leftOrRight;
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), true, p.m_leftOrRight, p.getSprite()));
			}
			
			p.setX(p.getX() - ANIMATION_INCREMENT);
			if(p.getX() > p.getServerX() && p.getX() % 32 == 0) {
				/* If the player is still behind the server, make sure the stopped animation is shown */
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), false, p.m_leftOrRight, p.getSprite()));
			}
		} else if (p.getX() < p.getServerX()) {
			if(p.getX() % 32 == 0) {
				p.setDirection(Direction.Right);
				p.m_leftOrRight = !p.m_leftOrRight;
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), true, p.m_leftOrRight, p.getSprite()));
			}
			p.setX(p.getX() + ANIMATION_INCREMENT);
			if(p.getX() < p.getServerX() && p.getX() % 32 == 0) {
				/* If the player is still behind the server, make sure the stopped animation is shown */
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), false, p.m_leftOrRight, p.getSprite()));
			}
		} else if (p.getY() > p.getServerY()) {
			if((p.getY() + 8) % 32 == 0) {
				p.setDirection(Direction.Up);
				p.m_leftOrRight = !p.m_leftOrRight;
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), true, p.m_leftOrRight, p.getSprite()));
			}
			p.setY(p.getY() - ANIMATION_INCREMENT);
			if(p.getY() > p.getServerY() && (p.getY() + 8) % 32 == 0) {
				/* If the player is still behind the server, make sure the stopped animation is shown */
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), false, p.m_leftOrRight, p.getSprite()));
			}
		} else if (p.getY() < p.getServerY()) {
			if((p.getY() + 8) % 32 == 0) {
				p.setDirection(Direction.Down);
				p.m_leftOrRight = !p.m_leftOrRight;
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), true, p.m_leftOrRight, p.getSprite()));
			}
			p.setY(p.getY() + ANIMATION_INCREMENT);
			if(p.getY() < p.getServerY() && (p.getY() + 8) % 32 == 0) {
				/* If the player is still behind the server, make sure the stopped animation is shown */
				p.setCurrentImage(Player.getSpriteFactory().getSprite(p.getDirection(), false, p.m_leftOrRight, p.getSprite()));
			}
		}
		/*
		 * The player is now in sync with the server, stop moving/animating them
		 */
		if (p.getX() == p.getServerX() && p.getY() == p.getServerY()) {
			p.setDirection(p.getDirection());
			p.setAnimating(false);
			p.loadSpriteImage();
		}
	}
}
