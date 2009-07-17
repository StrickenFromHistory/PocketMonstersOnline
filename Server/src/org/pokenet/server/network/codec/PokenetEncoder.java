package org.pokenet.server.network.codec;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineEncoder;

/**
 * Encodes messages to be sent to the client
 * @author shadowkanji
 *
 */
public class PokenetEncoder extends ProtocolEncoderAdapter {
	private static final String ENCODER = TextLineEncoder.class.getName() + ".encoder";
	private final Charset m_charset;
	private final LineDelimiter m_delimiter;
	
	/**
	 * Default constructor
	 */
	public PokenetEncoder() {
		m_charset = Charset.forName("US-ASCII");
		m_delimiter = LineDelimiter.UNIX;
	}

	/**
	 * Encodes a message and sends it to a session
	 */
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
			throws Exception {
		CharsetEncoder encoder = (CharsetEncoder) session.getAttribute(ENCODER);
		if (encoder == null) {
			encoder = m_charset.newEncoder();
			session.setAttribute(ENCODER, encoder);
		}
		String value = message.toString();
		ByteBuffer buf = ByteBuffer.allocate(value.length()).setAutoExpand(true);
		buf.putString(value, encoder);
		if (buf.position() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Line length: " + buf.position());
		}
		buf.putString(m_delimiter.getValue(), encoder);
		buf.flip();
		out.write(buf);
	}

}
