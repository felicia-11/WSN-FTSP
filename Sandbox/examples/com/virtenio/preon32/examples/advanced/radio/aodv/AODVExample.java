package com.virtenio.preon32.examples.advanced.radio.aodv;

import java.util.Random;

import com.virtenio.driver.device.at86rf231.AT86RF231;
import com.virtenio.driver.device.at86rf231.AT86RF231RadioDriver;
import com.virtenio.misc.PropertyHelper;
import com.virtenio.net.NetAddress;
import com.virtenio.preon32.node.Node;
import com.virtenio.radio.ieee_802_15_4.FrameIO;
import com.virtenio.radio.ieee_802_15_4.PANAddress;
import com.virtenio.radio.ieee_802_15_4.RadioDriver;
import com.virtenio.radio.ieee_802_15_4.RadioDriverFrameIO;
import com.virtenio.route.aodv.DataMessage;

/**
 * Example class to demonstrate AODV routing over IEEE 802.15.4. Uses X data
 * generator nodes and one target node. All nodes must be configured via
 * properties.
 * <p>
 * Properties of Node 1 (data generator):
 * 
 * <pre>
 * local.addr=1
 * target.addr=255
 * </pre>
 * 
 * Properties of Node 2 (data generator):
 * 
 * <pre>
 * local.addr=2
 * target.addr=255
 * </pre>
 * 
 * Properties of Node X (data generator):
 * 
 * <pre>
 * local.addr=X
 * target.addr=255
 * </pre>
 * 
 * Properties of target Node:
 * 
 * <pre>
 * local.addr = 255
 * </pre>
 * 
 * The data generator nodes generates data and send it to the target node. The
 * nodes can be covered to reduce TX range. Data can be send directly to the
 * target or via additional hops.
 */
public class AODVExample {
	/**
	 * The AODV test method
	 */
	public static void run() throws Exception {
		// get local and target address from properties
		final int localAddr = PropertyHelper.getInt("local.addr", 0);
		final int targetAddr = PropertyHelper.getInt("target.addr", -1);

		// construct the local MAC address which is an IEEE 802.1.5.4 PAN
		// address.
		final PANAddress macAddr = new PANAddress(0xCAFE, localAddr + 100);

		// Construct a node address which is like an IP address, We use a one
		// byte address. Other address can be used too.
		final NetAddress nodeAddr = new NetAddress(new byte[] { (byte) localAddr });

		// get AT86RF231 instance, open and configure it. We set a low transmit
		// power so we can simulate a network inside our rooms.
		final AT86RF231 transceiver = Node.getInstance().getTransceiver();
		transceiver.open();
		transceiver.setTransmitPower(AT86RF231.TRANSMIT_POWER_MIN);
		transceiver.setAddressFilter(macAddr.getPanId(), (int) macAddr.getAddress().getValue(),
				macAddr.getAddress().getValue(), false);

		// construct a FrameIO to be hardware independent
		final RadioDriver radioDriver = new AT86RF231RadioDriver(transceiver);
		final FrameIO fio = new RadioDriverFrameIO(radioDriver);

		// construct a node based on addresses and the FrameIO
		MyNode node = new MyNode(macAddr, nodeAddr, fio);

		// run the node within an extra thread
		new Thread(node).start();

		// generate data if we are a data generator node
		if (targetAddr != -1) {
			final Random random = new Random(System.nanoTime());

			// route message to this target address. Using one byte addressing.
			NetAddress destAddr = new NetAddress(new byte[] { (byte) targetAddr });
			while (true) {
				try {
					// generate a data message and send it
					long freeMemory = Runtime.getRuntime().freeMemory();
					String msgStr = "FreeMem: " + freeMemory;
					DataMessage msg = new DataMessage(nodeAddr, destAddr, msgStr.getBytes());
					node.send(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Thread.sleep(5000 + random.nextInt(1000));
			}
		}
	}

	/**
	 * The main method to run the example.
	 */
	public static void main(String[] args) throws Exception {
		AODVExample.run();
	}
}
