package org.pokenet.client.backend;

import org.pokenet.client.GameClient;
import org.pokenet.client.network.PacketGenerator;

/**
 * Parses chat text for commands
 * @author ZombieBear
 *
 */
public class ModerationManager {
	private static PacketGenerator m_ioSession = GameClient.getInstance().getPacketGenerator();
	
	public static void parseLine(String x){
		// Announcement
		if (x.length() >= 9 && x.substring(0, 9).equalsIgnoreCase("announce ")) {
			m_ioSession.writeTcpMessage("Ma" + x.substring(9));
		}
		// Mute
		else if (x.length() >= 5 && x.substring(0, 5).equalsIgnoreCase("mute ")) {
			m_ioSession.writeTcpMessage("Mm" + x.substring(5));
		}
		// Unmute
		else if (x.length() >= 7 && x.substring(0, 7).equalsIgnoreCase("unmute ")) {
			m_ioSession.writeTcpMessage("Mu" + x.substring(7));
		}
		// Kick
		else if (x.length() >= 5 && x.substring(0, 5).equalsIgnoreCase("kick ")) {
			m_ioSession.writeTcpMessage("Mk" + x.substring(5));
		}
		// Ban
		else if (x.length() >= 4 && x.substring(0, 4).equalsIgnoreCase("ban ")) {
			m_ioSession.writeTcpMessage("Mb" + x.substring(4));
		}
		// Unban
		else if (x.length() >= 6 && x.substring(0, 6).equalsIgnoreCase("unban ")) {
			m_ioSession.writeTcpMessage("MB" + x.substring(6));
		}
		// Jump to [player]
		else if (x.length() >= 7 && x.substring(0, 7).equalsIgnoreCase("jumpto ")) {
			m_ioSession.writeTcpMessage("MW" + x.substring(7));
		}
		// Player count
		else if (x.length() >= 11 && x.substring(0, 11).equalsIgnoreCase("playercount")) {
			m_ioSession.writeTcpMessage("Mc");
		}
		// Change Weather
		else if (x.length() >= 8 && x.substring(0, 8).equalsIgnoreCase("weather ")) {
			// Normal
			if (x.substring(8).equalsIgnoreCase("normal") ||
					x.substring(8).equalsIgnoreCase("sunny"))
				m_ioSession.writeTcpMessage("Mun");
			// Rain
			else if (x.substring(8).equalsIgnoreCase("rain"))
				m_ioSession.writeTcpMessage("Mur");
			// Snow
			else if (x.substring(8).equalsIgnoreCase("snow") ||
					x.substring(8).equalsIgnoreCase("hail") )
				m_ioSession.writeTcpMessage("Mus");
			// Fog
			else if (x.substring(8).equalsIgnoreCase("fog"))
				m_ioSession.writeTcpMessage("Muf");
			// Sandstorm
			else if (x.substring(8).equalsIgnoreCase("sandstorm"))
				m_ioSession.writeTcpMessage("MuS");
			// Random
			else if (x.substring(8).equalsIgnoreCase("random"))
				m_ioSession.writeTcpMessage("MuR");
		}
		// Stop server
		else if (x.length() >= 4 && x.substring(0, 4).equalsIgnoreCase("stop")) {
			m_ioSession.writeTcpMessage("Ms");
		}
	}
}
