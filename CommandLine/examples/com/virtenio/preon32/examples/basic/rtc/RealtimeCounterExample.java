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

package com.virtenio.preon32.examples.basic.rtc;

import com.virtenio.driver.realtimecounter.NativeRealtimeCounter;

public class RealtimeCounterExample {

	public static void main(String[] args) throws Exception {
		System.out.println("TEST RTC");

		NativeRealtimeCounter rtc = NativeRealtimeCounter.getInstance(0);
		rtc.open();

		System.out.println("Read 5 values");
		for (int i = 0; i < 5; i++) {
			System.out.println("Date: " + rtc.getValue());
			Thread.sleep(1000);
		}

		long value = 100000;
		System.out.println("Write Value: " + value);
		rtc.setValue(value);

		System.out.println("Read 5 Values");
		for (int i = 0; i < 5; i++) {
			System.out.println("Date: " + rtc.getValue());
			Thread.sleep(1000);
		}

		rtc.close();
	}
}
