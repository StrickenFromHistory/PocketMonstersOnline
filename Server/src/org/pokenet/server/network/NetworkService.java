package org.pokenet.server.network;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;
import org.pokenet.server.network.codec.PokenetCodecFactory;
import org.simpleframework.xml.core.Persister;

/**
 * Handles all networking
 * @author shadowkanji
 */
public class NetworkService {
	private ConnectionManager m_connectionManager;
	private LoginManager m_loginManager;
	private LogoutManager m_logoutManager;
	private IoAcceptor m_acceptor;
	private ChatManager m_chatManager;
	
	/**
	 * Default constructor
	 */
	public NetworkService() {
		m_connectionManager = new ConnectionManager();
		m_logoutManager = new LogoutManager();
		m_loginManager = new LoginManager(m_logoutManager);
		m_chatManager = new ChatManager();
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
	 * Returns the chat manager
	 * @return
	 */
	public ChatManager getChatManager() {
		return m_chatManager;
	}
	
	/**
	 * Returns the connection manager (packet handler)
	 * @return
	 */
	public ConnectionManager getConnectionManager() {
		return m_connectionManager;
	}
	
	/**
	 * Start this network service by starting all threads.
	 */
	public void start() {
		m_logoutManager.start();
		m_loginManager.start();
		m_chatManager.start();
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
			m_acceptor.bind(new InetSocketAddress(3128), m_connectionManager, cfg);
			System.out.println("INFO: Networking Service started");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Stop this network service by stopping all threads.
	 */
	public void stop() {
		//Stop all threads (do not use thread.stop() )
		//Unbind network address
		m_acceptor.unbindAll();
		m_connectionManager.logoutAll();
	}
}
