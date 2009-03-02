package org.pokenet.client.backend;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.pokenet.client.backend.entity.Player;

/**
 * Represents a map to be rendered on screen
 * @author shadowkanji
 *
 */
public class ClientMap extends TiledMap {
	// map offset modifiers
	private int m_xOffsetModifier;
	private int m_yOffsetModifier;
	private int m_xOffset;
	private int m_yOffset;
	private boolean m_isCurrent = false;
	private boolean m_isRendering;
	private ClientMapMatrix m_mapMatrix;
	private int m_walkableLayer, m_lastRendered;
	
	private Graphics m_graphics;

	/**
	 * Default constructor
	 * @param ref
	 * @param tileSetsLocation
	 * @throws SlickException
	 */
	public ClientMap(String ref, String tileSetsLocation) throws SlickException {
		super(ref, tileSetsLocation);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void renderedLine(int visualY, int mapY, int layer) {
		//m_lastRendered = layer;
		if (m_isCurrent) {
			try {
				m_graphics.resetTransform();
				if (layer == m_walkableLayer) {
					synchronized (m_mapMatrix.getPlayers()) {
						Player p;
						Iterator<Player> it = m_mapMatrix.getPlayers().iterator();
						while(it.hasNext()) {
							p = it.next();
							if (p.getSprite() != 0 && (p.getY() >= mapY * 32 - 39) && (p.getY() <= mapY * 32 + 32)
									&& (p.getCurrentImage() != null)) {
								p.getCurrentImage().draw(m_xOffset + p.getX() - 4, m_yOffset + p.getY());
								m_graphics.drawString(p.getUsername(), m_xOffset + (p.getX()
										- (m_graphics.getFont().getWidth(p.getUsername()) / 2)) + 16, m_yOffset + p.getY()
										- 36);
							}
						}
					}
				}
				m_graphics.scale(2, 2);
			} catch (ConcurrentModificationException e) {}
		}
	}

	/**
	 * Returns true if this map is/should be rendering on screen
	 * @return
	 */
	public boolean isRendering() {
		if (m_isRendering) {
			int drawWidth = getXOffset() + getWidth() * 32;
			int drawHeight = getYOffset() + getHeight() * 32;
			
			if (!(drawWidth < -32 && getXOffset() < -32 ||
					drawWidth > 832 && getXOffset() > 832) &&
					!(drawHeight < -32 && getYOffset() < -32 ||
							drawHeight > 632 && getYOffset() > 632)) {
				return true;
			}
		} return false;
	}
	
	/**
	 * Sets the graphics for this map
	 * @param g
	 */
	public void setGraphics(Graphics g) {
		m_graphics = g;
	}

	/**
	 * Returns the X offset of this map
	 * @return
	 */
	public int getXOffset() {
		return m_xOffset;
	}
	
	/**
	 * Returns the Y offset of this map
	 * @return
	 */
	public int getYOffset() {
		return m_yOffset;
	}
	
	/**
	 * Returns the index of the last rendered layer
	 * @return
	 */
	public int getLastLayerRendered() {
		return m_lastRendered;
	}
	
	/**
	 * Returns the index of the walkable layer
	 * @return
	 */
	public int getWalkableLayer() {
		return m_walkableLayer;
	}
	
	/**
	 * Sets the last layer rendered
	 * @param l
	 */
	public void setLastLayerRendered(int l) {
		m_lastRendered = l;
	}
}
