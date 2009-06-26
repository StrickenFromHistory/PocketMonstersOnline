package org.pokenet.server.backend;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import org.pokenet.server.backend.entity.NonPlayerChar;
import org.pokenet.server.backend.entity.Positionable.Direction;
import org.pokenet.server.backend.map.ServerMap;
import org.pokenet.server.backend.map.WarpTile;

/**
 * Handles NPC and WarpTile Loading
 * @author shadowkanji
 *
 */
public class DataLoader implements Runnable {
	private File m_file;
	private ServerMap m_map;
	
	/**
	 * Constructor
	 * @param f
	 */
	public DataLoader(File f, ServerMap m) {
		m_file = f;
		m_map = m;
		new Thread(this).start();
	}

	/**
	 * Called by starting the thread
	 */
	public void run() {
		try {
			Scanner reader = new Scanner(m_file);
			NonPlayerChar npc = new NonPlayerChar();
			WarpTile warp = new WarpTile();
			String line;
			String [] details;
			String direction = "Down";
			while(reader.hasNextLine()) {
				line = reader.nextLine();
				if(line.equalsIgnoreCase("[npc]")) {
					npc = new NonPlayerChar();
					npc.setName(reader.nextLine());
					direction = reader.nextLine();
					if(direction.equalsIgnoreCase("UP")) {
						npc.setFacing(Direction.Up);
					} else if(direction.equalsIgnoreCase("LEFT")) {
						npc.setFacing(Direction.Left);
					} else if(direction.equalsIgnoreCase("RIGHT")) {
						npc.setFacing(Direction.Right);
					} else {
						npc.setFacing(Direction.Down);
					}
					npc.setSprite(Integer.parseInt(reader.nextLine()));
					npc.setX((Integer.parseInt(reader.nextLine())) * 32);
					npc.setY(((Integer.parseInt(reader.nextLine())) * 32) - 8);
					//Load possible Pokemons
					line = reader.nextLine();
					if(!line.equalsIgnoreCase("NULL")) {
						details = line.split(",");
						HashMap<String, Integer> pokes = new HashMap<String, Integer>();
						for(int i = 0; i < details.length; i = i + 2) {
							pokes.put(details[i], Integer.parseInt(details[i + 1]));
						}
						npc.setPossiblePokemon(pokes);
					}
					//Set minimum party level
					npc.setPartySize(Integer.parseInt(reader.nextLine()));
					npc.setBadge(Integer.parseInt(reader.nextLine()));
					//Add all speech, if any
					line = reader.nextLine();
					if(!line.equalsIgnoreCase("NULL")) {
						details = line.split(",");
						for(int i = 0; i < details.length; i++) {
							npc.addSpeech(Integer.parseInt(details[i]));
						}
					}
					npc.setHealer(Boolean.parseBoolean(reader.nextLine().toLowerCase()));
					npc.setBox(Boolean.parseBoolean(reader.nextLine().toLowerCase()));
					npc.setShopKeeper(Boolean.parseBoolean(reader.nextLine().toLowerCase()));
				} else if(line.equalsIgnoreCase("[/npc]")) {
					m_map.addChar(npc);
				} else if(line.equalsIgnoreCase("[warp]")) {
					warp = new WarpTile();
					warp.setX(Integer.parseInt(reader.nextLine()));
					warp.setY(Integer.parseInt(reader.nextLine()));
					warp.setWarpX(Integer.parseInt(reader.nextLine()) * 32);
					warp.setWarpY((Integer.parseInt(reader.nextLine()) * 32) - 8);
					warp.setWarpMapX(Integer.parseInt(reader.nextLine()));
					warp.setWarpMapY(Integer.parseInt(reader.nextLine()));
					warp.setBadgeRequirement(Integer.parseInt(reader.nextLine()));
				} else if(line.equalsIgnoreCase("[/warp]")) {
					m_map.addWarp(warp);
				}
			}
		} catch (Exception e) {
			System.err.println("Error in " + m_map.getX() + "." + m_map.getY() + ".txt - Invalid NPC or WarpTile");
		}
	}
}
