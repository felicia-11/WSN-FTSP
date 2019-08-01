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

package com.virtenio.preon32.examples.basic.button;

import com.virtenio.driver.button.Button;
import com.virtenio.preon32.shuttle.Shuttle;

/**
 * Testet die Funktion des Benutzerkopfes SW1.
 * <p/>
 * SW1 : Hier kann der Zustand abgefragt werden (Button.getInstance(0))
 * <p/>
 * SW2 : Kann nicht abgefragt werden, führt direkt Reset aus
 * <p/>
 * 
 * <pre>
 * {@literal
 * Schaltungsbeispiel:
 *             VCC
 *              +
 *              |
 *              |
 *              o--------- GPIO
 *              |
 *              |
 *             .-.
 *             | |
 *             | | 10K
 *             '-'
 *              |
 *              |
 *             ===
 *             GND
 * }
 * </pre>
 */
public class ButtonExample {

	public static void main(String[] args) throws Exception {
		System.out.println("Press the button");

		Shuttle shuttle = Shuttle.getInstance();

		Button b0 = shuttle.getButton();
		b0.open();

		while (!b0.isPressed()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		System.out.println("Button pressed");

		b0.close();
		System.out.flush();
	}
}
