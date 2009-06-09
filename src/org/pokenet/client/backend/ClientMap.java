package org.pokenet.client.backend;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import org.pokenet.client.backend.entity.Player;
import org.pokenet.client.backend.entity.Player.Direction;

/**
 * Represents a map to be rendered on screen
 * @author shadowkanji
 * @author ZombieBear
 *
 */
public class ClientMap extends TiledMap {
	// map offset modifiers
	private int m_xOffsetModifier;
	private int m_yOffsetModifier;
	private int m_xOffset;
	private int m_yOffset;
	private int m_mapX;
	private int m_mapY;
	private boolean m_isCurrent = false;
	private ClientMapMatrix m_mapMatrix;
	private int m_walkableLayer, m_lastRendered;
	private String m_name;
	
	private Graphics m_graphics;

	/**
	 * Default constructor
	 * @param ref
	 * @param tileSetsLocation
	 * @throws SlickException
	 */
	public ClientMap(String ref, String tileSetsLocation) throws SlickException {
		super(ref, tileSetsLocation);
		m_xOffsetModifier = Integer.parseInt(getMapProperty("xOffsetModifier",
		"0").trim());
		m_yOffsetModifier = Integer.parseInt(getMapProperty("yOffsetModifier",
		"0").trim());
		m_xOffset = m_xOffsetModifier;
		m_yOffset = m_yOffsetModifier;
		if (getLayerIndex("WalkBehind") == -1){
			m_walkableLayer = getLayerCount() - 1;
		} else {
			m_walkableLayer = getLayerCount() - 2;
		}
		m_lastRendered = 0;
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
							if(p != null && p.getSprite() != 0 && (p.getY() >= mapY * 32 - 39) && (p.getY() <= mapY * 32 + 32)
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
		int drawWidth = getXOffset() + getWidth() * 32;
		int drawHeight = getYOffset() + getHeight() * 32;
		
		if (!(drawWidth < -32 && getXOffset() < -32 ||
				drawWidth > 832 && getXOffset() > 832) &&
				!(drawHeight < -32 && getYOffset() < -32 ||
						drawHeight > 632 && getYOffset() > 632)) {
			return true;
		}
		return false;
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
	
	/**
	 * Set to true if this is at 1,1 in the map matrix
	 * @param b
	 */
	public void setCurrent(boolean b) {
		m_isCurrent = b;
	}
	
	/**
	 * Returns true if this is 1,1 in the map matrix
	 * @return
	 */
	public boolean isCurrent() {
		return m_isCurrent;
	}
	
	/**
	 * Sets the map matrix
	 * @param m
	 */
	public void setMapMatrix(ClientMapMatrix m) {
		m_mapMatrix = m;
	}
	
	/**
	 * Sets the map x
	 * @param x
	 */
	public void setMapX(int x) {
		m_mapX = x;
	}
	
	/**
	 * Sets the map y
	 * @param y
	 */
	public void setMapY(int y) {
		m_mapY = y;
	}
	
	/**
	 * Returns the x offset modifier
	 * @return
	 */
	public int getXOffsetModifier() {
		return m_xOffsetModifier;
	}
	
	/**
	 * Returns the y offset modifier
	 * @return
	 */
	public int getYOffsetModifier() {
		return m_yOffsetModifier;
	}
	
	/**
	 * Returns true if the player is colliding with an object
	 * @param p
	 * @param d
	 * @return
	 */
	public boolean isColliding(Player p, Direction d) {
		return false;
	}
	
	/**
	 * Sets the y offset and recalibrates surrounding maps if calibrate is true
	 * @param offset
	 * @param calibrate
	 */
	public void setYOffset(int offset, boolean calibrate) {
		m_yOffset = offset;
		
		if(calibrate) {
			ClientMap map = m_mapMatrix.getMap(m_mapX - 1, m_mapY);
			if (map != null)
				map.setYOffset(offset - getYOffsetModifier()
						+ map.getYOffsetModifier(), false);

			map = m_mapMatrix.getMap(m_mapX + 1, m_mapY);
			if (map != null)
				map.setYOffset(offset - getYOffsetModifier()
						+ map.getYOffsetModifier(), false);

			map = m_mapMatrix.getMap(m_mapX, m_mapY - 1);
			if (map != null)
				map.setYOffset(offset - map.getHeight() * 32, false);

			map = m_mapMatrix.getMap(m_mapX, m_mapY + 1);
			if (map != null) {
				map.setYOffset(offset + getHeight() * 32, false);
			}
			
			map = m_mapMatrix.getMap(m_mapX + 1, m_mapY - 1);
			if (map != null)
				map.setYOffset(offset - map.getHeight() * 32
						+ map.getYOffsetModifier(), false);
			
			map = m_mapMatrix.getMap(m_mapX - 1, m_mapY - 1);
			if (map != null)
				map.setYOffset(offset - map.getHeight() * 32, false);
			
			map = m_mapMatrix.getMap(m_mapX + 1, m_mapY + 1);
			if (map != null)
				map.setYOffset(offset + getHeight() * 32, false);
			
			map = m_mapMatrix.getMap(m_mapX - 1, m_mapY + 1);
			if (map != null)
				map.setYOffset(offset + getHeight() * 32, false);
		}
	}
	
	/**
	 * Sets the x offset and recalibrates surrounding maps if calibrate is set to true
	 * @param offset
	 * @param calibrate
	 */
	public void setXOffset(int offset, boolean calibrate) {
		m_xOffset = offset;

		if(calibrate) {
			ClientMap map = m_mapMatrix.getMap(m_mapX - 1, m_mapY);
			if (map != null)
				map.setXOffset(offset - map.getWidth() * 32 - m_xOffsetModifier
						+ map.getXOffsetModifier(), false);

			map = m_mapMatrix.getMap(m_mapX + 1, m_mapY);
			if (map != null)
				map.setXOffset(offset + getWidth() * 32 - getXOffsetModifier()
						+ map.getXOffsetModifier(), false);

			map = m_mapMatrix.getMap(m_mapX, m_mapY - 1);
			if (map != null)
				map.setXOffset(offset - getXOffsetModifier()
						+ map.getXOffsetModifier(), false);

			map = m_mapMatrix.getMap(m_mapX, m_mapY + 1);
			if (map != null)
				map.setXOffset(offset - getXOffsetModifier()
						+ map.getXOffsetModifier(), false);
			
			map = m_mapMatrix.getMap(m_mapX - 1, m_mapY - 1);
			if (map != null)
				map.setXOffset(offset - map.getWidth() * 32 - getXOffsetModifier()
						+ map.getXOffsetModifier(), false);
			
			map = m_mapMatrix.getMap(m_mapX - 1, m_mapY + 1);
			if (map != null)
				map.setXOffset(offset - map.getWidth() * 32 - getXOffsetModifier()
						+ map.getXOffsetModifier(), false);
			
			map = m_mapMatrix.getMap(m_mapX + 1, m_mapY + 1);
			if (map != null)
				map.setXOffset(offset + getWidth() * 32 - getXOffsetModifier()
						+ map.getXOffsetModifier(), false);
			
			map = m_mapMatrix.getMap(m_mapX + 1, m_mapY - 1);
			if (map != null)
				map.setXOffset(offset + getWidth() * 32 - getXOffsetModifier()
						+ map.getXOffsetModifier(), false);
		}
	}

	/**
	 * Returns the map's name
	 * @return the map's name
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Sets the map's name
	 * @param name
	 */
	public void setName(String name) {
		m_name = name;
	}
}
