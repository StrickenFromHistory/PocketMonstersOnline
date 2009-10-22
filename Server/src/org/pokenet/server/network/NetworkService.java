package org.pokenet.server.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;
import org.pokenet.server.GameServer;
import org.pokenet.server.feature.ChatManager;
import org.pokenet.server.network.codec.PokenetCodecFactory;

/**
 * Handles all networking
 * @author shadowkanji
 */
public class NetworkService {
	private ProtocolHandler m_connectionManager;
	private LoginManager m_loginManager;
	private LogoutManager m_logoutManager;
	private IoAcceptor m_acceptor;
	private ChatManager [] m_chatManager;
	
	/**
	 * Default constructor
	 */
	public NetworkService() {
		m_logoutManager = new LogoutManager();
		m_loginManager = new LoginManager(m_logoutManager);
		m_chatManager = new ChatManager[3];
		m_connectionManager = new ProtocolHandler(m_loginManager, m_logoutManager);
	}
	
	/**
	 * Returns the login manager
	 * @return
	 */
	public LoginManager getLoginManager() {
		return m_loginManager;
	}
	
	/**
	 * Returns the logout manager
	 * @return
	 */
	public LogoutManager getLogoutManager() {
		return m_logoutManager;
	}
	
	/**
	 * Returns the chat manager with the least amount of processing to be done
	 * @return
	 */
	public ChatManager getChatManager() {
		int smallest = 0;
		for(int i = 1; i < m_chatManager.length; i++) {
			if(m_chatManager[i].getProcessingLoad() < m_chatManager[smallest].getProcessingLoad())
				smallest = i;
		}
		return m_chatManager[smallest];
	}
	
	/**
	 * Returns the connection manager (packet handler)
	 * @return
	 */
	public ProtocolHandler getConnectionManager() {
		return m_connectionManager;
	}
	
	/**
	 * Start this network service by starting all threads.
	 */
	public void start() {
		//Load MySQL JDBC Driver
        try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * Ensure anyone still marked as logged in on this server
		 * is unmarked
		 */
		MySqlManager m = new MySqlManager();
		if(m.connect(GameServer.getDatabaseHost(), 
				GameServer.getDatabaseUsername(),
				GameServer.getDatabasePassword())) {
			m.selectDatabase(GameServer.getDatabaseName());
			m.query("UPDATE pn_members SET lastLoginServer='null' WHERE lastLoginServer='"
					+ GameServer.getServerName() + "'");
			m.close();
		}
		m = null;
		/*
		 * Start the login/logout managers
		 */
		m_logoutManager.start();
		m_loginManager.start();
		/*
		 * Start the chat managers
		 */
		for(int i = 0; i < m_chatManager.length; i++) {
			m_chatManager[i] = new ChatManager();
			m_chatManager[i].start();
		}
		//Bind network address and start connection manager
		ByteBuffer.setUseDirectBuffers(false);
		ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

		m_acceptor = new SocketAcceptor(5, Executors
				.newCachedThreadPool());

		SocketAcceptorConfig cfg = new SocketAcceptorConfig();
		((SocketSessionConfig) cfg.getSessionConfig()).setTcpNoDelay(true);
		cfg.getSessionConfig().setReuseAddress(true);
		cfg.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new PokenetCodecFactory()));
		cfg.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors
				.newCachedThreadPool()));
		
			try {
				m_acceptor.bind(new InetSocketAddress(7001), m_connectionManager, cfg);
				System.out.println("INFO: Networking Service started");
			} catch (IOException e) {
				if(!e.getMessage().contains("peer"));//Ignore connection reset by peer messages. They're useless. 
					e.printStackTrace();
			}
	}
	
	/**
	 * Stop this network service by stopping all threads.
	 */
	public void stop() {
		//Stop all threads (do not use thread.stop() )
		//Unbind network address
		for(int i = 0; i < m_chatManager.length; i++)
			m_chatManager[i].stop();
		m_acceptor.unbindAll();
		m_connectionManager.logoutAll();
	}
}
