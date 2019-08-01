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
package com.virtenio.preon32.examples.basic.watchdog;

import com.virtenio.driver.watchdog.NativeWatchDog;

/**
 * Testet den Hardware-WatchDog des Mikrocontrollers
 */
public class WatchDogExample {

	public static void main(String[] args) throws Exception {
		System.out.println("Watchdog Example");
		NativeWatchDog wd = NativeWatchDog.getInstance(0);
		//
		int wait1000ms = 1000;
		wd.open(1000);
		// ///////////////////////////////////////////////////////////////////////

		System.out.println("Set 500ms");
		wd.open(wait1000ms / 2);
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(350);
			} catch (InterruptedException e) {
			}
			System.out.println("-> reset");
			wd.reset();
		}
		// wd.close();
		System.out.println("Done");

		// ///////////////////////////////////////////////////////////////////////

		System.out.println("Set 2s");
		wd.open(wait1000ms * 2);
		try {
			wd.waitFor();
		} catch (InterruptedException e) {
		}
		System.out.println("This should never be printed out!");
		// wd.close();

		// ///////////////////////////////////////////////////////////////////////
	}
}
