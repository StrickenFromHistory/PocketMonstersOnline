package org.pokenet.server.network.codec;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Decodes messages received from a client
 * @author shadowkanji
 *
 */
public class PokenetDecoder extends ProtocolDecoderAdapter {

	/**
	 * Decodes a message
	 */
	public void decode(IoSession session, ByteBuffer buffer,
			ProtocolDecoderOutput out) throws Exception {
		
	}

}
