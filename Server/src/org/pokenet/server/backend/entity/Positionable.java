/*
 Pokemon Global. A Pokemon MMO based on the series of games made by Nintendo.
 Copyright � 2007-2008 Pokemon Global Team

 This file is part of Pokemon Global.

 Pokemon Global is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Pokemon Global is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Pokemon Global.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.pokenet.server.backend.entity;

import org.pokenet.server.backend.map.ServerMap;
import org.simpleframework.xml.Root;

/**
 * Provides an interface for game objects that can be placed on a map
 * @author shadowkanji
 *
 */
@Root
public interface Positionable {
	public enum Direction { Up, Down, Left, Right }
	public int getX();
	public int getY();
	public void setX(int x);
	public void setY(int y);
	public void setId(int id);
	public int getId();

	public String getName();
	public void setName(String name);
	public int getSprite();

	public boolean move(Direction d);
	public Direction getNextMovement();
	public int getPriority();
	public void queueMovement(Direction d);
	public Direction getFacing();
	public ServerMap getMap();

	public void setSprite(int sprite);

	public int getMapX();
	public int getMapY();
	
	public void setMap(ServerMap map, Direction dir);
	public void setVisible(boolean visible);
	
	public boolean isVisible();
}
