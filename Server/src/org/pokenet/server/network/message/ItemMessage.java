package org.pokenet.server.network.message;

public class ItemMessage extends PokenetMessage {
	public ItemMessage(boolean buy, int itemId, int quantity) {
		if(buy) {
			m_message = "Iu" + itemId + "," + quantity;
		} else {
			m_message = "Ir" + itemId + "," + quantity;
		}
	}
}
