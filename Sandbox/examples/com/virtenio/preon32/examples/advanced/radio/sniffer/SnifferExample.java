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

package com.virtenio.preon32.examples.advanced.radio.sniffer;

import com.virtenio.driver.device.at86rf231.AT86RF231;
import com.virtenio.driver.led.LED;
import com.virtenio.io.Console;
import com.virtenio.preon32.examples.common.Misc;
import com.virtenio.preon32.examples.common.RadioInit;
import com.virtenio.preon32.examples.common.utils.StringAddon;
import com.virtenio.preon32.shuttle.Shuttle;
import com.virtenio.radio.ieee_802_15_4.Frame;

/**
 * Umsetzung eines einfachen Sniffers zum Abhören von Funknachrichten.
 */
public class SnifferExample {

	private int COMMON_CHANNEL = 20;
	private int COMMON_PANID = 0xCAFE;
	private int ADDR_SEND = 0xAFFE;
	private int ADDR_RECV = 0xBABE;
	private long SLEEP_TIME = 1000;

	/**
	 * Konfiguriert die Parameter des Sender oder Empfängers
	 * 
	 * @param isSender
	 *            true = es handelt sich um den Sender, false = es handelt sich
	 *            um den Empfänger
	 */
	private void inputValues(boolean isSender) {
		Console console = new Console();
		COMMON_CHANNEL = console.readInt("Funkkanal", 11, 26);
		if (isSender) {
			COMMON_PANID = console.readInt("PANId", 0, 0xFFFF);
			ADDR_SEND = console.readInt("Sender-Adresse", 0, 0xFFFF);
			ADDR_RECV = console.readInt("Empfänger-Adresse", 0, 0xFFFF);
			SLEEP_TIME = console.readInt("Wartezeit zwischen Nachrichten [ms]", 100, 10000);
		}
	}

	/**
	 * The frame transmitter
	 */
	public void prog_sender() throws Exception {
		System.out.println("***************************************");
		inputValues(true);
		System.out.println("---");

		final Shuttle shuttle = Shuttle.getInstance();

		AT86RF231 radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);
		radio.setPANId(COMMON_PANID);
		radio.setShortAddress(ADDR_SEND);

		LED green = shuttle.getLED(Shuttle.LED_GREEN);
		green.open();

		LED red = shuttle.getLED(Shuttle.LED_RED);
		red.open();

		Console console = new Console();
		while (true) {
			String mesg = console.readLine("Geben Sie eine Text-Nachricht ein");
			int num = console.readInt("Wie oft soll die Nachricht gesendet werden: ", 1,
					Integer.MAX_VALUE);

			for (int i = 0; i < num; i++) {
				boolean isOK = false;
				while (!isOK) {
					try {
						// String msg = i + "-" + mesg;
						// ///////////////////////////////////////////////////////////////////////
						//
						Frame frame = new Frame(Frame.TYPE_DATA | Frame.ACK_REQUEST
								| Frame.DST_ADDR_16 | Frame.INTRA_PAN | Frame.SRC_ADDR_16);
						frame.setSrcAddr(ADDR_SEND);
						frame.setSrcPanId(COMMON_PANID);
						frame.setDestAddr(ADDR_RECV);
						frame.setDestPanId(COMMON_PANID);
						radio.setState(AT86RF231.STATE_TX_ARET_ON);
						frame.setPayload(mesg.getBytes());
						radio.transmitFrame(frame);
						// ///////////////////////////////////////////////////////////////////////
						//
						System.out.println(i + ". ACK");
						Misc.LedBlinker(green, 100, false);
					} catch (Exception e) {
						System.out.println(i + ". NACK");
						Misc.LedBlinker(red, 100, false);
					}
					isOK = true;
				}
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
				}
			}

		}
		// radio.close();
	}

	/**
	 * The sniffer
	 */
	public void prog_sniffer() throws Exception {
		System.out.println("***************************************");

		inputValues(false);
		System.out.println("---");

		final Shuttle shuttle = Shuttle.getInstance();

		final AT86RF231 radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);

		final LED green = shuttle.getLED(Shuttle.LED_GREEN, true);
		final LED red = shuttle.getLED(Shuttle.LED_RED, true);

		Thread reader = new Thread() {
			@Override
			public void run() {
				while (true) {
					Frame f = null;
					try {
						f = new Frame();
						radio.setState(AT86RF231.STATE_RX_ON);
						radio.waitForFrame(f);
						Misc.LedBlinker(green, 100, false);

						String str = StringAddon.ByteArrayToString(f.getPayload());
						System.out.println("(FROM=" + f.getSrcAddr() + ";PANID=" + f.getSrcPanId()
								+ ";" + "TO=" + f.getDestAddr() + ";PANID=" + f.getDestPanId()
								+ "): " + str);
					} catch (Exception e) {
						Misc.LedBlinker(red, 100, false);
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
			String line = console.readLine("'sender' or 'sniffer'?");
			if (line.equalsIgnoreCase("sender")) {
				new SnifferExample().prog_sender();
			}
			if (line.equalsIgnoreCase("sniffer")) {
				new SnifferExample().prog_sniffer();
			}
		}
	}
}
