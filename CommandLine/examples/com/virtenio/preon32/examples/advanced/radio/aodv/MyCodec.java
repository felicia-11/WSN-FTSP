package com.virtenio.preon32.examples.advanced.radio.aodv;

import com.virtenio.net.NetAddress;
import com.virtenio.radio.ieee_802_15_4.Frame;
import com.virtenio.route.aodv.AbstractCodec;
import com.virtenio.route.aodv.Message;

/**
 * This class represents the Codec which is used to encode and decode AODV
 * messages from and to IEEE 802.15.4 frames. Most parts are already implemented
 * in {@link AbstractCodec}. We have to implement some missing parts.
 */
public class MyCodec extends AbstractCodec {
	/**
	 * The frame to be used for decoding and encoding.
	 */
	Frame frame;

	/**
	 * Constructs a IEEE 802.15.4 frame codec
	 */
	public MyCodec(Frame frame) {
		super(frame.getBuffer(), frame.getPayloadOffset());
		this.frame = frame;
	}

	/**
	 * Reads our one byte address.
	 */
	@Override
	protected NetAddress readAddress() {
		int addr = buffer[ptr++];
		return new NetAddress(new byte[] { (byte) addr });
	}

	/**
	 * Override this method to update the payload length of the frame after a
	 * message is written.
	 */
	@Override
	public void writeMessage(Message msg) {
		super.writeMessage(msg);
		frame.setPayloadLength(ptr - frame.getPayloadOffset());
	}
}
