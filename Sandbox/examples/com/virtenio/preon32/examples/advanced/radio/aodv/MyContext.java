package com.virtenio.preon32.examples.advanced.radio.aodv;

import com.virtenio.net.NetAddress;
import com.virtenio.radio.ieee_802_15_4.Frame;
import com.virtenio.radio.ieee_802_15_4.PANAddress;
import com.virtenio.route.aodv.Config;
import com.virtenio.route.aodv.Context;
import com.virtenio.route.aodv.DataMessage;
import com.virtenio.route.aodv.Message;

/**
 * Class representing the AODV context which is needed by the AODV
 * implementation.
 */
public class MyContext implements Context {
	private MyNode node;

	/**
	 * Constructs a new context based on a node.
	 */
	public MyContext(MyNode node) {
		this.node = node;
	}

	/**
	 * Returns the local address of the node.
	 */
	@Override
	public NetAddress getLocalAddr() {
		return node.nodeAddr;
	}

	/**
	 * Returns the configuration of the node.
	 */
	@Override
	public Config getConfig() {
		return node.config;
	}

	/**
	 * This method is called if a data frame has reached his target. The AODV
	 * manager invokes this method so that we can handle it.
	 */
	@Override
	public void handleMessage(Message msg) {
		System.out.println("HANDLE: " + msg);
		if (msg instanceof DataMessage) {
			try {
				DataMessage dm = (DataMessage) msg;
				String dataStr = new String(dm.getBuffer(), dm.getOffset(), dm.getLength());
				System.out.println("![0x" + dm.getSrcAddr() + ";" + dataStr.substring(9) + "]");
			} catch (Exception e) {
			}
		}
	}

	/**
	 * This method is called if a message could not be send to a target. The
	 * AODV manager invokes this method so that we can handle it.
	 */
	@Override
	public void routeError(Message msg) {
		System.out.println("ROUTE ERROR: " + msg);
	}

	/**
	 * This method is responsible for transmitting message to neighbors. If a
	 * message is to broadcast <code>nextHopAddr</code> is <code>null</code>.
	 * <p>
	 * This implementation encodes a message to a IEEE 802.15.4 frame and
	 * transmits the frame.
	 */
	@Override
	public void sendMessage(Message msg, NetAddress nextHopAddr) {
		System.out.println("TX: " + msg.toString() + " -> " + nextHopAddr + " " + System.currentTimeMillis());

		// build the frame
		int fc = Frame.TYPE_DATA | Frame.DST_ADDR_16 | Frame.INTRA_PAN | Frame.SRC_ADDR_16;
		if (nextHopAddr != null) {
			fc |= Frame.ACK_REQUEST;
		}
		final Frame frame = new Frame(fc);
		frame.setSequenceNumber(0);

		if (nextHopAddr == null) {
			frame.setDestPanId(node.macAddr.getPanId());
			frame.setDestAddr(0xFFFF);
		} else {
			PANAddress destAddr = (PANAddress) nextHopAddr;
			frame.setDestPanId(destAddr.getPanId());
			frame.setDestAddr(destAddr.getAddress().getValue());
		}
		frame.setSrcAddr(node.macAddr.getAddress().getValue());

		// encode the message to the frame
		MyCodec codec = new MyCodec(frame);
		codec.writeMessage(msg);

		// transmit the frame. The message is added to a queue and
		// transmitted in an extra thread. Look at ThreadedFrameIO for
		// more information.
		node.thFrameIO.transmit(frame);
	}
}
