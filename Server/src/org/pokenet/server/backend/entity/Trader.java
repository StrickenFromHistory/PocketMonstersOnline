package org.pokenet.server.backend.entity;

import org.pokenet.server.backend.entity.Trader;
import org.pokenet.server.trade.Trade;

/**
 * Provides an interface for all game objects that can be traded with
 * @author felty.wos
 */
public interface Trader {
	public boolean isTrading();
	
	public Trade getTrade();
}
