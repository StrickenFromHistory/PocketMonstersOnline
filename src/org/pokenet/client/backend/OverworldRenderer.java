/**
 * 
 */
package org.pokenet.client.backend;

import java.io.InputStream;
import java.util.Iterator;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.pokenet.client.GameClient;
import org.pokenet.client.backend.entity.Player;

/**
 * Renders elements on the overall world
 * @author ZombieBear
 *
 */
public class OverworldRenderer{
	private Image m_grassOverlay;
	private ClientMapMatrix m_mapMatrix = GameClient.getInstance().getMapMatrix();
	
	/**
	 * Constructor
	 * @param g
	 */
	public OverworldRenderer(Graphics g) {
		try{
			InputStream f = getClass().getResourceAsStream("/res/ui/grass_overlay.png");
			m_grassOverlay = new Image(f, "/res/ui/grass_overlay.png", false);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public void render(Graphics m_graphics) {
		synchronized (m_mapMatrix.getPlayers()) {
			Player p;
			Iterator<Player> it = m_mapMatrix.getPlayers().iterator();
			while(it.hasNext()) {
				p = it.next();
				ClientMap m_curMap = m_mapMatrix.getCurrentMap();
				int m_xOffset = m_curMap.getXOffset();
				int m_yOffset = m_curMap.getYOffset();
				if(p != null && p.getSprite() != 0 && (p.getCurrentImage() != null)) {
					if (m_curMap.shouldReflect(p)){
						// If there's a reflection, flip the player's image, set his alpha so its more translucent, and then draw it.
						Image m_reflection = p.getCurrentImage().getFlippedCopy(false, true);
						m_reflection.setAlpha((float) 0.05);
						if (p.getSprite() != -1)
							m_reflection.draw(m_xOffset + p.getX() - 4, m_yOffset + p.getY() + 32);
						else{
							m_reflection.draw(m_xOffset + p.getX() - 4, m_yOffset + p.getY() + 8);
						}
					}
					if (m_curMap.wasOnGrass(p) && m_curMap.isOnGrass(p)){
						switch (p.getDirection()){
						case Up:
							m_grassOverlay.draw(m_xOffset + p.getServerX(), m_yOffset + p.getServerY() + 32 + 8);
							break;
						case Left:
							m_grassOverlay.copy().draw(m_xOffset + p.getServerX() + 32, m_yOffset + p.getServerY() + 8);
							break;
						case Right:
							m_grassOverlay.copy().draw(m_xOffset + p.getServerX() - 32, m_yOffset + p.getServerY() + 8);
							break;
						}
					}
					if (m_curMap.isOnGrass(p) && p.getY() <= p.getServerY()){
						m_grassOverlay.draw(m_xOffset + p.getServerX(), m_yOffset + p.getServerY() + 9);
					}
					m_graphics.drawString(p.getUsername(), m_xOffset + (p.getX()
							- (m_graphics.getFont().getWidth(p.getUsername()) / 2)) + 16, m_yOffset + p.getY()
							- 36);
				}
			}
		}
	}
}
