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

package com.virtenio.preon32.examples.basic.led;

import com.virtenio.driver.led.LED;
import com.virtenio.io.Console;
import com.virtenio.preon32.examples.common.Misc;
import com.virtenio.preon32.shuttle.Shuttle;

import java.io.IOException;

/**
 * Test die Ansteuerung einer Light Emitting Diode (LED), die an einem General
 * Purpose Input/Output (GPIO) Pin angeschlossen ist. Die Ansteuerung erfolgt
 * hier aus einem eigenen Thread heraus.
 */
public class ThreadLEDExample {
	private long speed = 250;
	private LED red, green, yellow, amber;

	private void init() throws Exception {
		final Shuttle shuttle = Shuttle.getInstance();

		green = shuttle.getLED(Shuttle.LED_GREEN);
		green.open();

		yellow = shuttle.getLED(Shuttle.LED_YELLOW);
		yellow.open();

		red = shuttle.getLED(Shuttle.LED_RED);
		red.open();

		amber = shuttle.getLED(Shuttle.LED_AMBER);
		amber.open();
	}

	public void run1() throws Exception {
		init();

		Thread t0 = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						amber.on();
						Misc.sleep(speed / 8);
						amber.off();
						Misc.sleep(speed / 8);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		t0.start();

		Thread t1 = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						red.on();
						Misc.sleep(speed / 4);
						red.off();
						Misc.sleep(speed / 4);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		t1.start();

		Thread t2 = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						green.on();
						Misc.sleep(speed / 2);
						green.off();
						Misc.sleep(speed / 2);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		t2.start();

		Thread t3 = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						yellow.on();
						Misc.sleep(speed);
						yellow.off();
						Misc.sleep(speed);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		t3.start();

		Console console = new Console();
		while (true) {
			System.out.println();
			String line = console.readLine("Setze Speed [ms] (default=1000)");
			try {
				speed = Integer.parseInt(line);
			} catch (Exception e) {
			}
		}
	}

	public void run2() throws Exception {
		init();

		Thread t0 = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						doLight(green, yellow, red, amber, speed);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		t0.start();

		Console console = new Console();
		while (true) {
			System.out.println();
			String line = console.readLine("Setze Speed [ms] (default=1000): ");
			try {
				speed = Integer.parseInt(line);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Methode zur Realisierung eines generischen Lauflichtes. Die LEDs werden
	 * in Hin- und Rückrichtung.
	 * 
	 * @param l0
	 *            1. LED
	 * @param l1
	 *            2. LED
	 * @param l2
	 *            3. LED
	 * @param l3
	 *            4. LED
	 * @param speed
	 *            Geschwindigkeit [ms] zwischn den LEDs
	 * 
	 * @throws IOException
	 */
	public static void doLight(LED l0, LED l1, LED l2, LED l3, long speed) throws Exception {
		l0.off();
		l1.off();
		l2.off();
		l3.off();
		//
		l0.on();
		Misc.sleep(speed);
		l0.off();
		l1.on();
		Misc.sleep(speed);
		l1.off();
		l2.on();
		Misc.sleep(speed);
		l2.off();
		l3.on();
		Misc.sleep(speed);
		l3.off();
		//
		l2.on();
		Misc.sleep(speed);
		l2.off();
		l1.on();
		Misc.sleep(speed);
		l1.off();
		l0.on();
		Misc.sleep(speed);
		l0.off();
	}

	public static void main(String[] args) throws Exception {
		new ThreadLEDExample().run1();
	}
}
