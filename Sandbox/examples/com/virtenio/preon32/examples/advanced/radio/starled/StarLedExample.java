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

package com.virtenio.preon32.examples.advanced.radio.starled;

import com.virtenio.driver.button.Button;
import com.virtenio.driver.device.at86rf231.AT86RF231;
import com.virtenio.driver.led.LED;
import com.virtenio.misc.PropertyHelper;
import com.virtenio.preon32.examples.common.RadioInit;
import com.virtenio.preon32.shuttle.Shuttle;
import com.virtenio.radio.ieee_802_15_4.Frame;

/**
 * Diese Anwendung baut ein Funknetz mit der Topologie ein Sterns auf. Zu einem
 * zentralen Netzwerkknoten (prog_sun) werden Nachrichten von weiteren
 * Netzwerkknoten (satellite) geschickt. In der Anwendung ist vorgesehen, dass
 * bis zu drei Satilliten vorhanden sind. Jeder Satellite als auch die Sun haben
 * eine eindeutige Kennung. Werden Funknachtrichten von einem Satelliten bei der
 * Sun empfange, dann leuchtet eine LED {grün, rot oder orange}. Die Farbe der
 * leuchtenden LED wird durch die Funkkennung bestimmt.
 */
public class StarLedExample {

	/* intern Variables */
	boolean isRunning = true;
	private int COMMON_CHANNEL = PropertyHelper.getInt("channel", 24);
	private int COMMON_PANID = PropertyHelper.getInt("panID", 0xCAFE);
	private int ADDR_MY = PropertyHelper.getInt("addr_my", 0xBABE);
	private int ADDR_SUN = PropertyHelper.getInt("addr_sun", 0xEBBE);
	private int SPEED = PropertyHelper.getInt("speed", 100);
	private int LED_ID = PropertyHelper.getInt("led_id", Shuttle.LED_GREEN);

	/** Ein Programme, dass über das Startmenu aufgerufen werden kann */
	public void prog_sun() throws Exception {
		System.out.println("Important: Properties must be set correctly!");

		Shuttle shuttle = Shuttle.getInstance();

		AT86RF231 radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);
		radio.setPANId(COMMON_PANID);
		radio.setShortAddress(ADDR_MY);
		radio.setState(AT86RF231.STATE_RX_AACK_ON);

		LED green = shuttle.getLED(Shuttle.LED_GREEN, true);
		LED yellow = shuttle.getLED(Shuttle.LED_YELLOW, true);
		LED red = shuttle.getLED(Shuttle.LED_RED, true);
		LED orange = shuttle.getLED(Shuttle.LED_ORANGE, true);

		Frame frameIn = new Frame();
		while (isRunning) {
			try {
				radio.waitForFrame(frameIn);
				int ledID = frameIn.getPayloadAt(0);
				System.out.println("LED: " + ledID);
				try {
					switch (ledID) {
						case Shuttle.LED_GREEN:
							green.on();
							Thread.sleep(10);
							green.off();
							break;
						case Shuttle.LED_YELLOW:
							yellow.on();
							Thread.sleep(10);
							yellow.off();
							break;
						case Shuttle.LED_RED:
							red.on();
							Thread.sleep(10);
							red.off();
							break;
						case Shuttle.LED_ORANGE:
							orange.on();
							Thread.sleep(10);
							orange.off();
							break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				System.out.println("Exception: waitForFrame()");
			}
		}
	}

	/** Ein Programme, dass über das Startmenu aufgerufen werden kann */
	public void prog_satellite() throws Exception {

		Shuttle shuttle = Shuttle.getInstance();

		AT86RF231 radio = RadioInit.initRadio();
		radio.setChannel(COMMON_CHANNEL);
		radio.setPANId(COMMON_PANID);
		radio.setShortAddress(ADDR_MY);
		radio.setState(AT86RF231.STATE_TX_ARET_ON);

		LED green = shuttle.getLED(Shuttle.LED_GREEN, true);
		LED red = shuttle.getLED(Shuttle.LED_RED, true);
		Button button = shuttle.getButton(true);

		System.out.println("LED ID: " + LED_ID);

		while (isRunning) {
			if (!button.isPressed()) {
				Frame frame = RadioInit.prepareFrame(ADDR_MY, COMMON_PANID, ADDR_SUN);
				frame.setPayload(new byte[10]);
				if (LED_ID >= 0 && LED_ID <= 3) {
					frame.setPayloadAt(0, LED_ID);
				} else {
					frame.setPayloadAt(0, 0);
				}
				try {
					try {
						radio.transmitFrame(frame);
						green.on();
						Thread.sleep(10);
						green.off();
					} catch (Exception e) {
						red.on();
						Thread.sleep(10);
						red.off();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(SPEED);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String progName = System.getProperty("prog_name");
		if (progName.equals("StarLEDsatellite")) {
			System.out.println("StarLEDsatellite");
			new StarLedExample().prog_satellite();
		}
		if (progName.equals("StarLEDsun")) {
			System.out.println("StarLEDsun");
			new StarLedExample().prog_sun();
		}
	}
}
