package org.pokenet.server.network;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.pokenet.server.GameServer;
import org.pokenet.server.feature.ChatManager;
import org.pokenet.server.network.codec.PokenetCodecFactory;

/**
 * Handles all networking
 * @author shadowkanji
 */
public class NetworkService {
	private TcpProtocolHandler m_tcpProtocolHandler;
	private UdpProtocolHandler m_udpProtocolHandler;
	private LoginManager m_loginManager;
	private LogoutManager m_logoutManager;
	private IoAcceptor m_tcpAcceptor;
	private NioDatagramAcceptor m_udpAcceptor;
	private ChatManager [] m_chatManager;
	
	
	/**
	 * Default constructor
	 */
	public NetworkService() {
		m_logoutManager = new LogoutManager();
		m_loginManager = new LoginManager(m_logoutManager);
		m_tcpProtocolHandler = new TcpProtocolHandler(m_loginManager, m_logoutManager);
		m_udpProtocolHandler = new UdpProtocolHandler();
		m_chatManager = new ChatManager[3];
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
	public TcpProtocolHandler getConnectionManager() {
		return m_tcpProtocolHandler;
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
        
		/*
		 * Bind the TCP port
		 */
		m_tcpAcceptor = new NioSocketAcceptor();
		m_tcpAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new PokenetCodecFactory()));
		m_tcpAcceptor.setHandler(m_tcpProtocolHandler);
		try {
			m_tcpAcceptor.bind(new InetSocketAddress(7002)); 
			System.out.println("INFO: TCP acceptor started.");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		/*
		 * Bind the UDP port
		 */
		m_udpAcceptor = new NioDatagramAcceptor();
		m_udpAcceptor.setHandler(m_udpProtocolHandler);
		DefaultIoFilterChainBuilder chain = m_udpAcceptor.getFilterChain(); 

		chain.addLast("codec", new ProtocolCodecFilter(new PokenetCodecFactory()));
		DatagramSessionConfig dcfg = m_udpAcceptor.getSessionConfig(); 
		dcfg.setReuseAddress(true);
		try {
			m_udpAcceptor.bind(new InetSocketAddress(7005)); 
			System.out.println("INFO: UDP acceptor started.");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		System.out.println("INFO: Network Service started.");
	}
	
	/**
	 * Stop this network service by stopping all threads.
	 */
	public void stop() {
		//Stop all threads (do not use thread.stop() )
		//Unbind network address
		for(int i = 0; i < m_chatManager.length; i++)
             m_chatManager[i].stop();
		m_tcpAcceptor.unbind();
		m_tcpProtocolHandler.logoutAll();
	}
}
