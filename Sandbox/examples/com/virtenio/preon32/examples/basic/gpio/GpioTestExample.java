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

package com.virtenio.preon32.examples.basic.gpio;

import com.virtenio.driver.gpio.GPIO;
import com.virtenio.driver.gpio.NativeGPIO;

/**
 * Testet alle GPIO, die auf dem Preon32 Shuttle verfügbar sind.
 * 
 * Der Test kann so erfolgen, dass mit einer Masseleitung die einzelnen GPIO
 * gesetzt werden können. Entsprechende Pull-Widerstände werden gesetzt.
 * 
 * ACHTUNG: Einige GPIOs sind mit dem Hardware geteilt, so dass die Funktion
 * eingeschränkt ist. Es ist zu beachten das es zu Kurzschlüssen kommen kann,
 * wenn zwei Ausgänge verbunden werden.
 */
public class GpioTestExample {

	public static void main(String[] args) throws Exception {
		int[] gpios = new int[] { //
		37, 35, 33, 25, 23, 21, 19, // P1_left
			36, 34, 32, 24, 22, 20, 18, // P1_right
			6, 8, 10, 12, 14, 16, // P2_left
			1, 0, 7, 9, 11, 13, 15 // P2_right
		};
		int num = gpios.length;

		int[] gpio_status = new int[num];
		NativeGPIO[] gpio = new NativeGPIO[num];

		// Setze die GPIO mit einem Pullup
		for (int i = 0; i < num; i++) {
			gpio[i] = NativeGPIO.getInstance(gpios[i]);
			gpio[i].open(GPIO.MODE_INPUT_PULLUP);
			gpio_status[i] = 0;
		}

		while (true) {
			for (int i = 0; i < num; i++) {
				boolean status = gpio[i].get() != 0;

				// Nur bei einer Änderung erfolgt eine Ausgabe
				if (status & (gpio_status[i] == 0)) {
					System.out.println("Set GPIO=" + gpios[i]);
					gpio_status[i] = 1;
				} else if (!status & (gpio_status[i] == 1)) {
					System.out.println("Clear GPIO=" + gpios[i]);
					gpio_status[i] = 0;
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
