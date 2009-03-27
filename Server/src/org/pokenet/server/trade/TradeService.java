package org.pokenet.server.trade;

import java.util.ArrayList;

import org.pokenet.server.backend.entity.NonPlayerChar;
import org.pokenet.server.backend.entity.PlayerChar;

/**
 * Handles trades
 * @author felty.wos
 */
public class TradeService implements Runnable {	
	private ArrayList<Trade> m_trades;
	private Thread m_thread;
	private boolean m_isRunning;
	
	/**
	 * Default constructor
	 */
	public TradeService() {
		m_trades = new ArrayList<Trade>();
		m_thread = new Thread(this);
	}
	
	/**
	 * Returns the index of the trade the player is part of
	 * Returns -1 if they are not trading
	 * @param p
	 * @return
	 */
	public int containsPlayer(PlayerChar p) {
		for(int i = 0; i < m_trades.size(); i++) {
			if(m_trades.get(i).isParticipating(p))
				return i;
		}
		return -1;
	}
	
	/**
	 * Returns a trade based on the index of it in the arraylist of trades
	 * @param index
	 * @return
	 */
	public Trade getTrades(int index) {
		return m_trades.get(index);
	}
	
	/**
	 * Returns the processing load of this thread (how many trades exist in it)
	 * @return
	 */
	public int getProcessingLoad() {
		return m_trades.size();
	}
	
	/**
	 * Starts a trade with player1 and player2
	 * @param player1
	 * @param player2
	 */
	public void startPlayerTrade(PlayerChar player1, PlayerChar player2) {
		
	}
	
	/**
	 * Called by m_thread.start(). Loops through all trades and calls Trade.executeTrade()
	 * if both participants have confirmed the trade
	 */
	public void run() {
		while(m_isRunning) {
			synchronized(m_trades) {
				for(int i = 0; i < m_trades.size(); i++) {
					if(m_trades.get(i).isReady()) {
						m_trades.get(i).startTrade();
					}
					else if(m_trades.get(i).isOffered()) {
						m_trades.get(i).sendOffers();
					}
					else if(m_trades.get(i).isConfirmed()) {
						m_trades.get(i).executeTrade();
					}
				}
			}
			try {
				Thread.sleep(350);
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Starts this battle service
	 */
	public void start() {
		m_isRunning = true;
		m_thread.start();
	}
	
	/**
	 * Stops this battle service
	 */
	public void stop() {
		m_isRunning = false;
	}

}