package com.virtenio.preon32.examples.advanced.radio.aodv;

import com.virtenio.io.ErrorListener;
import com.virtenio.net.NetAddress;
import com.virtenio.radio.ieee_802_15_4.Frame;
import com.virtenio.radio.ieee_802_15_4.FrameIO;
import com.virtenio.radio.ieee_802_15_4.PANAddress;
import com.virtenio.radio.ieee_802_15_4.ThreadedFrameIO;
import com.virtenio.route.aodv.Config;
import com.virtenio.route.aodv.Manager;
import com.virtenio.route.aodv.Message;

/**
 * This class represents a node which contains everything to use AODV routing.
 */
public class MyNode implements Runnable {
	/**
	 * The local MAC address which is a {@link PANAddress}
	 */
	PANAddress macAddr;

	/**
	 * Our node address.
	 */
	NetAddress nodeAddr;

	/**
	 * The instance of the AODV routing manager
	 */
	Manager routeManager;

	/**
	 * Instance of AODV routing configuration. Configuration values can be fine
	 * tuned.
	 */
	Config config = new Config();

	/**
	 * The {@link FrameIO} instance which is used to transport frames.
	 */
	ThreadedFrameIO thFrameIO;

	/**
	 * Constructs a new Node.
	 * 
	 * @param macAddr
	 *            The local MAC address to use.
	 * @param nodeAddr
	 *            The address of the node which is like an IP address.
	 * @param frameIO
	 *            The frame transport instance in form of a {@link FrameIO}.
	 */
	public MyNode(PANAddress macAddr, NetAddress nodeAddr, FrameIO frameIO) {
		this.macAddr = macAddr;
		this.nodeAddr = nodeAddr;
		this.routeManager = new Manager(new MyContext(this));
		this.thFrameIO = new ThreadedFrameIO(frameIO);
		this.thFrameIO.setErrorListener(new ErrorListener<Frame>() {
			@Override
			public void transmitError(Frame frame, Exception exception) {
				routeManager.routeError(new PANAddress(frame.getSrcPanId(), frame.getSrcAddr()));
			}

			@Override
			public void receiveError(Exception exception) {
				exception.printStackTrace();
			}
		});
	}

	/**
	 * This method transmits a message using the AODV routing manager.
	 */
	public void send(Message msg) {
		routeManager.send(msg);
	}

	/**
	 * The run method implementation.
	 * 
	 * @see Runnable
	 */
	@Override
	public void run() {
		thFrameIO.start();

		// construct a thread to periodically invoke the timeout method of
		// the AODV manager.
		new Thread() {
			public void run() {
				while (true) {
					routeManager.timeout();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();

		// run in a loop and receive messages
		for (;;) {
			try {
				// receive a message
				Frame frame = new Frame();
				thFrameIO.receive(frame);

				// decode a message
				MyCodec codec = new MyCodec(frame);
				Message msg = codec.readMessage();

				// set the address of the last hop. Address is stored inside
				// the frame.
				msg.setLastHopAddr(new PANAddress(frame.getSrcPanId(), frame.getSrcAddr()));

				System.out.println("RX: " + msg.toString() + " " + System.currentTimeMillis() + " " + frame);

				// give the message into the route manager.
				routeManager.received(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Node " + nodeAddr.toString();
	}
}
