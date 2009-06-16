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
		// Mute
		if (x.substring(0, 5).equalsIgnoreCase("mute ")) {
			m_ioSession.write("Mm" + x.substring(5));
		}
		// Unmute
		else if (x.substring(0, 7).equalsIgnoreCase("unmute ")) {
			m_ioSession.write("Mu" + x.substring(7));
		}
		// Kick
		else if (x.substring(0, 5).equalsIgnoreCase("kick ")) {
			m_ioSession.write("Mk" + x.substring(5));
		}
		// Change Weather
		else if (x.substring(0, 8).equalsIgnoreCase("weather ")) {
			// Normal
			if (x.substring(8).equalsIgnoreCase("normal") ||
					x.substring(8).equalsIgnoreCase("sunny"))
				m_ioSession.write("Mun");
			// Rain
			else if (x.substring(8).equalsIgnoreCase("rain"))
				m_ioSession.write("Mur");
			// Snow
			else if (x.substring(8).equalsIgnoreCase("snow") ||
					x.substring(8).equalsIgnoreCase("hail") )
				m_ioSession.write("Mus");
			// Fog
			else if (x.substring(8).equalsIgnoreCase("fog"))
				m_ioSession.write("Muf");
			// Fog
			else if (x.substring(8).equalsIgnoreCase("sandstorm"))
				m_ioSession.write("MuS");
		}
		// Stop server
		else if (x.substring(0, 4).equalsIgnoreCase("stop")) {
			m_ioSession.write("Ms");
		}
	}
}
