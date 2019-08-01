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

package com.virtenio.preon32.examples.advanced.radio.movinglight;

import com.virtenio.driver.device.at86rf231.AT86RF231;
import com.virtenio.driver.led.LED;
import com.virtenio.driver.led.LEDException;
import com.virtenio.driver.led.LEDList;
import com.virtenio.io.Console;
import com.virtenio.misc.PropertyHelper;
import com.virtenio.preon32.examples.common.RadioInit;
import com.virtenio.preon32.shuttle.Shuttle;
import com.virtenio.radio.ieee_802_15_4.Frame;

/**
 * Das Programm verwendet die Funkschnittstelle, um ein Lauflich zwischen zwei
 * drahtlosen Sensorknoten hin und her zu spielen. Dazu muss auf dem 1.
 * drahtlosen Sensorknoten das Programm "lauflicht1" und auf dem anderen
 * drahtlosen Sensorknoten das Programm "lauflicht2" gestartet werden.
 */
public class MovingLightExample {

	/** Variablen zur Konfiguration */
	private int COMMON_CHANNEL = PropertyHelper.getInt("radio.channel", 24);
	private int COMMON_PANID = PropertyHelper.getInt("radio.panid", 0xCAFE);
	private int ADDR_KR_LEFT = PropertyHelper.getInt("addr.left", 0xBABE);
	private int ADDR_KR_RIGHT = PropertyHelper.getInt("addr.right", 0xEBBE);
	private int SPEED = PropertyHelper.getInt("speed", 100);

	/**
	 * Thread schlafen legen.
	 */
	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Unterprogramm zum setzen der LEDs
	 * 
	 * @param speed
	 *            Wartezeit zwischen den Zuständen
	 * @throws LEDException
	 */
	private void initFlash(LEDList ledList, int speed) throws LEDException {
		ledList.off();
		sleep(speed);
		ledList.on();
		sleep(speed);
		ledList.off();
		sleep(speed);
	}

	/**
	 * Methode zur Realisierung eines Lauflichts.
	 * 
	 * @param leds
	 *            Die LEDs, die verwendet werden sollen.
	 * @param speed
	 *            Geschwindigkeit [ms] zwischen den LEDs
	 * 
	 * @throws IOException
	 */
	public void doLight(LED[] leds, int speed) throws Exception {
		for (LED led : leds) {
			led.off();
		}
		for (int i = 0; i < leds.length; i++) {
			leds[i].on();
			sleep(speed);
			leds[i].off();
		}

		sleep(speed);

		for (int i = leds.length - 2; i >= 0; i--) {
			leds[i].on();
			sleep(speed);
			leds[i].off();
		}

		sleep(speed);
	}

	/**
	 * Ein Programme, dass über das Startmenu aufgerufen werden kann
	 */
	public void prog_kr_left() throws Exception {
		final Shuttle shuttle = Shuttle.getInstance();
		final LED[] leds = new LED[] { //
		shuttle.getLED(Shuttle.LED_GREEN), shuttle.getLED(Shuttle.LED_YELLOW),
			shuttle.getLED(Shuttle.LED_RED), shuttle.getLED(Shuttle.LED_AMBER) };

		LEDList ledList = new LEDList(leds);
		ledList.open(); // open all led instance

		final AT86RF231 radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);
		radio.setPANId(COMMON_PANID);
		radio.setShortAddress(ADDR_KR_LEFT);

		initFlash(ledList, SPEED);

		Frame txFrame = new Frame(Frame.TYPE_DATA | Frame.ACK_REQUEST | Frame.ADDR_16
				| Frame.INTRA_PAN);
		txFrame.setSrcAddr(ADDR_KR_LEFT);
		txFrame.setSrcPanId(COMMON_PANID);
		txFrame.setDestAddr(ADDR_KR_RIGHT);
		txFrame.setDestPanId(COMMON_PANID);

		Frame rxFrame = new Frame();

		while (true) {
			doLight(leds, SPEED);

			try {
				radio.setState(AT86RF231.STATE_TX_ARET_ON);
				radio.transmitFrame(txFrame);
				try {
					radio.setState(AT86RF231.STATE_RX_AACK_ON);
					radio.waitForFrame(rxFrame);
				} catch (Exception e) {
					System.out.println("Receive left failed !!!");
				}
			} catch (Exception e) {
				System.out.println("Send left failed !!!");
			}
		}
	}

	/**
	 * Ein Programme, dass über das Startmenü aufgerufen werden kann
	 */
	public void prog_kr_right() throws Exception {
		final Shuttle shuttle = Shuttle.getInstance();

		final LED[] leds = new LED[] {
			shuttle.getLED(Shuttle.LED_AMBER),
			shuttle.getLED(Shuttle.LED_RED),
			shuttle.getLED(Shuttle.LED_YELLOW),
			shuttle.getLED(Shuttle.LED_GREEN)
		};

		LEDList ledList = new LEDList(leds);
		ledList.open(); // open all led instance

		AT86RF231 radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);
		radio.setPANId(COMMON_PANID);
		radio.setShortAddress(ADDR_KR_RIGHT);

		initFlash(ledList, SPEED);

		Frame rxFrame = new Frame();

		Frame txFrame = new Frame(Frame.TYPE_DATA | Frame.ACK_REQUEST | Frame.ADDR_16
				| Frame.INTRA_PAN);
		txFrame.setSrcAddr(ADDR_KR_RIGHT);
		txFrame.setSrcPanId(COMMON_PANID);
		txFrame.setDestAddr(ADDR_KR_LEFT);
		txFrame.setDestPanId(COMMON_PANID);

		while (true) {
			try {
				radio.setState(AT86RF231.STATE_RX_AACK_ON);
				radio.waitForFrame(rxFrame);
			} catch (Exception e) {
				System.out.println("Receive right failed !!!");
			}

			while (true) {
				doLight(leds, SPEED);
				try {
					radio.setState(AT86RF231.STATE_TX_ARET_ON);
					radio.transmitFrame(txFrame);
					break;
				} catch (Exception e) {
					System.out.println("Send right failed !!!");
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Console console = new Console();
		while (true) {
			String line = console.readLine("'left' node or 'right' node?");
			if (line.equalsIgnoreCase("left")) {
				new MovingLightExample().prog_kr_left();
			}
			if (line.equalsIgnoreCase("right")) {
				new MovingLightExample().prog_kr_right();
			}
		}
	}
}
