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

import java.util.Arrays;

import com.virtenio.driver.realtimeclock.NativeRealtimeClock;
import com.virtenio.driver.realtimeclock.RealtimeClock;

public class RealtimeClockExample {

	public static void main(String[] args) throws Exception {
		System.out.println("TEST RTC");

		NativeRealtimeClock rtc = NativeRealtimeClock.getInstance(0);
		rtc.open();

		short[] fields = new short[RealtimeClock.FIELD_COUNT];

		while (true) {
			rtc.loadValues(fields);
			System.out.println(Arrays.toString(fields));
			Thread.sleep(1000);
		}
	}
}
