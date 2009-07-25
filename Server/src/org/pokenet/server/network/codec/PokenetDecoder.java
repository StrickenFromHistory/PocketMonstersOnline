package org.pokenet.server.network.codec;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;

/**
 * Decodes messages received from a client
 * @author shadowkanji
 *
 */
public class PokenetDecoder extends TextLineDecoder {
	
	/**
	 * Default constructor
	 */
	public PokenetDecoder() {
		super(Charset.forName("US-ASCII"), LineDelimiter.AUTO);
	}
	
	/**
	 * Decodes a message
	 */
	public void decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		super.decode(session, in, out);
	}
}
