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

package com.virtenio.preon32.examples.basic.sleep;

import com.virtenio.driver.cpu.CPUException;
import com.virtenio.preon32.cpu.CPUConstants;
import com.virtenio.preon32.cpu.CPUHelper;

/**
 * Testprogramm, das den Microcontroller in den Schlafzustand versetzt. Im
 * Schlafzustand ist der Energiebedarf deutlich geringer und somit verlängert
 * sich die Betriebsdauer eines autarken System, dass aus Batterien mit
 * begrenzter Kapazität versorgt wird.
 */
public class SleepExample {

	public static void main(String[] args) throws CPUException {
		System.out.println("TEST SLEEP");
		System.out.println("Enter Powersave: 5s");
		System.out.flush();

		CPUHelper.setPowerState(CPUConstants.V_POWER_STATE_STANDBY, 5000);

		System.out.println("Powsersave left");
		System.out.flush();
	}
}
