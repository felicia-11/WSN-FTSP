package com.virtenio.preon32.examples.advanced.radio.sendreceive;

import com.virtenio.driver.device.at86rf231.AT86RF231;
import com.virtenio.driver.device.at86rf231.AT86RF231RadioDriver;
import com.virtenio.misc.PropertyHelper;
import com.virtenio.preon32.node.Node;
import com.virtenio.preon32.shuttle.Shuttle;
import com.virtenio.radio.ieee_802_15_4.Frame;
import com.virtenio.radio.ieee_802_15_4.FrameIO;
import com.virtenio.radio.ieee_802_15_4.RadioDriver;
import com.virtenio.radio.ieee_802_15_4.RadioDriverFrameIO;

/**
 * Demonstrates radio transmit and receive using FrameIO classes which eases
 * eases transmit and receive and which is hardware independent.
 */
public class FrameIOExample {
	// The local address
	private int localAddr = PropertyHelper.getInt("local.addr", 0);

	// the remote address, address of the receiver
	private int remoteAddr = PropertyHelper.getInt("remote.addr", 1);

	// the panID to use
	private int panID = PropertyHelper.getInt("radio.panid", 0xCAFE);

	/**
	 * Method to run the example.
	 */
	public void run() {
		System.out.println("local.addr: " + localAddr);
		System.out.println("remote.addr: " + remoteAddr);

		try {
			// open and configure the AT86RF231 transceiver
			AT86RF231 t = Node.getInstance().getTransceiver();
			t.open();
			t.setAddressFilter(panID, localAddr, localAddr, false);

			// Create a FrameIO instance based on the AT86RF231. After the
			// FrameIO is created transmit and receive is hardware independent
			// if you only use the FrameIO instance.
			final RadioDriver radioDriver = new AT86RF231RadioDriver(t);
			final FrameIO fio = new RadioDriverFrameIO(radioDriver);

			// start transmitter
			startTransmitter(fio);

			// start receiver
			runReceiver(fio);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create and start a receiver thread which uses the given FrameIO instance.
	 */
	private void runReceiver(final FrameIO fio) {
		// create empty frame used by the receiver
		Frame frame = new Frame();
		for (;;) {
			try {
				// receive a frame
				fio.receive(frame);

				// print the frame
				System.out.println(frame);
			} catch (Exception e) {
				System.out.println("Error receiving frame");
			}
		}
	}

	/**
	 * Create and start a transmitter thread which uses the given FrameIO
	 * instance.
	 */
	private void startTransmitter(final FrameIO fio) {
		new Thread() {
			@Override
			public void run() {
				// Create a testframe which is used by the transmitter
				int frameControl = Frame.TYPE_DATA | Frame.ACK_REQUEST | Frame.DST_ADDR_16
						| Frame.INTRA_PAN | Frame.SRC_ADDR_16;

				final Frame testFrame = new Frame(frameControl);
				testFrame.setSequenceNumber(0);
				testFrame.setDestPanId(panID);
				testFrame.setDestAddr(remoteAddr);
				testFrame.setSrcAddr(localAddr);
				testFrame.setPayload(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 });

				System.out.println("Press button to send data");
				for (;;) {
					try {
						// wait until button is pressed
						if (!Shuttle.getInstance().getButton(true).isPressed()) {
							Thread.sleep(100);
							continue;
						}

						// transmit a frame
						fio.transmit(testFrame);

						// increase sequence number
						testFrame.setSequenceNumber(testFrame.getSequenceNumber() + 1);

						Thread.sleep(50);
					} catch (Exception e) {
						System.out.println("Error transmitting frame " + testFrame.getSequenceNumber());
					}
				}
			}
		}.start();
	}

	/**
	 * Main method to execute as an application.
	 */
	public static void main(String[] args) {
		new FrameIOExample().run();
	}
}
