package org.pokenet.chat.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * Starts up chat server
 * @author shadowkanji
 *
 */
public class ChatServer {
	public static void main(String [] args) {
		NioSocketAcceptor m_tcpAcceptor = new NioSocketAcceptor();
		m_tcpAcceptor.getFilterChain().addLast("codec", 
				new ProtocolCodecFilter(new TextLineCodecFactory(
				Charset.forName("US-ASCII"))));
		m_tcpAcceptor.setHandler(new ChatProtocolHandler());
		try {
			m_tcpAcceptor.bind(new InetSocketAddress(7001)); 
			System.out.println("INFO: Chat server started.");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}
