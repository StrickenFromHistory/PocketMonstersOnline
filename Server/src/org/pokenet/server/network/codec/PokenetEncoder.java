package org.pokenet.server.network.codec;

import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Encodes messages to be sent to the client
 * @author shadowkanji
 *
 */
public class PokenetEncoder extends ProtocolEncoderAdapter {

	/**
	 * Encodes a message and sends it to a session
	 */
	public void encode(IoSession arg0, Object arg1, ProtocolEncoderOutput arg2)
			throws Exception {
		
	}

}
