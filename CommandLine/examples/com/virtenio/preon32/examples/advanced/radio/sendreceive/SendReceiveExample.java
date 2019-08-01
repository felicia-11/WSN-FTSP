/*
 * Copyright (c) 2011., Virtenio GmbH
 * All rights reserved.
 *
 * Commercial software license.
 * Only for test and evaluation purposes.
 * Use in commercial products prohibited.
 * No distribution without permission by Virtenio.
 * Ask Virtenio for other type of license at info@virtenio.de
 *
 * Kommerzielle Softwarelizenz.
 * Nur zum Test und Evaluierung zu verwenden.
 * Der Einsatz in kommerziellen Produkten ist verboten.
 * Ein Vertrieb oder eine Veröffentlichung in jeglicher Form ist nicht ohne Zustimmung von Virtenio erlaubt.
 * Für andere Formen der Lizenz nehmen Sie bitte Kontakt mit info@virtenio.de auf.
 */

package com.virtenio.preon32.examples.advanced.radio.sendreceive;

import com.virtenio.driver.button.Button;
import com.virtenio.driver.device.at86rf231.AT86RF231;
import com.virtenio.driver.led.LED;
import com.virtenio.io.Console;
import com.virtenio.preon32.examples.common.Misc;
import com.virtenio.preon32.examples.common.RadioInit;
import com.virtenio.preon32.shuttle.Shuttle;
import com.virtenio.radio.ieee_802_15_4.Frame;

/**
 * Einfaches Beispiel der Funkübertragung mit Senden und Empfangen.
 */
public class SendReceiveExample {

	/** Interne Variablen */
	private int COMMON_CHANNEL = 24;
	private int COMMON_PANID = 0xCAFE;
	private int ADDR_SEND = 0xAFFE;
	private int ADDR_RESV = 0xBABE;

	public void prog_sender() throws Exception {
		final Shuttle shuttle = Shuttle.getInstance();

		AT86RF231 radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);
		radio.setPANId(COMMON_PANID);
		radio.setShortAddress(ADDR_SEND);

		LED green = shuttle.getLED(Shuttle.LED_GREEN);
		green.open();

		LED red = shuttle.getLED(Shuttle.LED_GREEN);
		red.open();

		Console console = new Console();

		while (true) {
			String msg = console.readLine("Please enter text message");
			for (int i = 0; i < 9; i++) {
				boolean isOK = false;
				while (!isOK) {
					try {
						String message = i + "-" + msg;
						// ///////////////////////////////////////////////////////////////////////
						Frame frame = new Frame(Frame.TYPE_DATA | Frame.ACK_REQUEST
								| Frame.DST_ADDR_16 | Frame.INTRA_PAN | Frame.SRC_ADDR_16);
						frame.setSrcAddr(ADDR_SEND);
						frame.setSrcPanId(COMMON_PANID);
						frame.setDestAddr(ADDR_RESV);
						frame.setDestPanId(COMMON_PANID);
						radio.setState(AT86RF231.STATE_TX_ARET_ON);
						frame.setPayload(message.getBytes());
						radio.transmitFrame(frame);
						// ///////////////////////////////////////////////////////////////////////
						Misc.LedBlinker(green, 100, false);
						System.out.println("(" + i + ") SEND: " + msg);
						isOK = true;
					} catch (Exception e) {
						Misc.LedBlinker(red, 100, false);
						System.out.println("(" + i + ") ERROR: no receiver");
					}
				}
			}

		}
		// radio.close();
	}

	public void prog_sender_button() throws Exception {
		final Shuttle shuttle = Shuttle.getInstance();

		AT86RF231 radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);
		radio.setPANId(COMMON_PANID);
		radio.setShortAddress(ADDR_SEND);

		LED green = shuttle.getLED(Shuttle.LED_GREEN);
		green.open();

		LED red = shuttle.getLED(Shuttle.LED_GREEN);
		red.open();

		Console console = new Console();

		String mesg = console.readLine("Please enter text message");
		Button b0 = shuttle.getButton();
		b0.open();

		while (true) {
			try {
				Thread.sleep(100);
				if (b0.isPressed()) {
					try {
						// ///////////////////////////////////////////////////////////////////////
						//
						Frame frame = new Frame(Frame.TYPE_DATA | Frame.ACK_REQUEST
								| Frame.DST_ADDR_16 | Frame.INTRA_PAN | Frame.SRC_ADDR_16);
						frame.setSrcAddr(ADDR_SEND);
						frame.setSrcPanId(COMMON_PANID);
						frame.setDestAddr(ADDR_RESV);
						frame.setDestPanId(COMMON_PANID);
						radio.setState(AT86RF231.STATE_TX_ARET_ON);
						frame.setPayload(mesg.getBytes());
						radio.transmitFrame(frame);
						// ///////////////////////////////////////////////////////////////////////
						//
						Misc.LedBlinker(green, 100, false);
					} catch (Exception e) {
						Misc.LedBlinker(red, 100, false);
					}
					Thread.sleep(150);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** Ein Programme, dass über das Startmenu aufgerufen werden kann */

	public void prog_receiver() throws Exception {
		System.out.println("Text_Receiver");

		final Shuttle shuttle = Shuttle.getInstance();

		final AT86RF231 radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);
		radio.setPANId(COMMON_PANID);
		radio.setShortAddress(ADDR_RESV);

		final LED orange = shuttle.getLED(Shuttle.LED_ORANGE);
		orange.open();

		Thread reader = new Thread() {
			@Override
			public void run() {
				while (true) {
					Frame f = null;
					try {
						f = new Frame();
						radio.setState(AT86RF231.STATE_RX_AACK_ON);
						radio.waitForFrame(f);
						Misc.LedBlinker(orange, 100, false);
					} catch (Exception e) {
					}

					if (f != null) {
						byte[] dg = f.getPayload();
						String str = new String(dg, 0, dg.length);
						String hex_addr = Integer.toHexString((int) f.getSrcAddr());
						System.out.println("FROM(" + hex_addr + "): " + str);
					}
				}
			}
		};
		reader.start();

		while (true) {
			Misc.sleep(1000);
		}
	}

	public static void main(String[] args) throws Exception {
		Console console = new Console();
		while (true) {
			String line = console.readLine("'sender' or 'receiver'?");
			if (line.equalsIgnoreCase("sender")) {
				new SendReceiveExample().prog_sender();
			}
			if (line.equalsIgnoreCase("receiver")) {
				new SendReceiveExample().prog_receiver();
			}
		}
	}
}
