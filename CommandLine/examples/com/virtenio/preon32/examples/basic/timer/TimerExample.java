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

package com.virtenio.preon32.examples.basic.timer;

import com.virtenio.driver.timer.NativeTimer;
import com.virtenio.driver.timer.Timer;
import com.virtenio.vm.event.AsyncEvent;
import com.virtenio.vm.event.AsyncEventHandler;
import com.virtenio.vm.event.AsyncEvents;

/**
 * Testet die Hardware-Timer und verknüpfte Ereignisverarbeitung
 */
public class TimerExample {

	public static void main(String[] args) throws Exception {
		// ///////////////////////////////////////////////////////////////////////
		AsyncEvents events = AsyncEvents.getAsyncEvents();
		events.start();

		// ///////////////////////////////////////////////////////////////////////
		AsyncEvent event3 = events.getEvent(3);
		event3.addHandler(new AsyncEventHandler() {
			@Override
			public void handleAsyncEvent(int eventId) {
				System.out.println("TIMER 3");
			}
		});

		AsyncEvent event4 = events.getEvent(4);
		event4.addHandler(new AsyncEventHandler() {
			@Override
			public void handleAsyncEvent(int eventId) {
				System.out.println("TIMER 4");
			}
		});

		// ///////////////////////////////////////////////////////////////////////
		Timer timer1 = NativeTimer.getInstance(0);
		timer1.open(Timer.MODE_ONE_SHOT, 3, 250);
		timer1.enable();

		Timer timer2 = NativeTimer.getInstance(1);
		timer2.open(Timer.MODE_CONTINUOUS, 4, 500);
		timer2.enable();

		// ///////////////////////////////////////////////////////////////////////
		System.out.println("Before");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		System.out.println("After");
		timer1.close();
		timer2.close();
		System.out.println("kill passed");
		System.out.flush();
	}
}
