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

/** Testet die Funktion des Benutzer-Buttons auf dem Preon32Shuttle. */

public class GpioButtonExample {

	public static void main(String[] args) throws Exception {
		System.out.println("Press the button");
		GPIO button = NativeGPIO.getInstance(5);
		button.open(GPIO.MODE_INPUT);
		boolean isPressed = false;
		while (true) {
			if (button.get() != 0 & !isPressed) {
				System.out.println("Button pressed !");
				isPressed = true;
			} else if (button.get() == 0 & isPressed) {
				System.out.println("Button released !");
				isPressed = false;
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
