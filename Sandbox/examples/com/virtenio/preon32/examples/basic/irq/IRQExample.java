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

package com.virtenio.preon32.examples.basic.irq;

import com.virtenio.driver.gpio.GPIO;
import com.virtenio.driver.gpio.NativeGPIO;
import com.virtenio.driver.irq.IRQ;
import com.virtenio.driver.irq.NativeIRQ;
import com.virtenio.vm.event.AsyncEvent;
import com.virtenio.vm.event.AsyncEventHandler;
import com.virtenio.vm.event.AsyncEvents;

/**
 * Testet die Verarbeitung von externen Ereignissen (Interrupts). Als Beispiel
 * können Taster angeschlossen sein, auf die ereignisbasiert reagiert werden
 * soll. Interuptbasiertes Erfassen ist immer einem Polling von Eingängen
 * vorzuziehen, da der Mikrocontroller nicht aktiv auf ein Ereignis warten muss.
 *
 * ACHTUNG: IRQ13 auf PIN GPIO33 wird geteilt mit JTAG_TCK. Da JTAG_TCK ein
 * Ausgang ist kann es zu einem Kurzschluss kommen.
 *
 *
 * <pre>
 * {@literal
 * Schaltungsbeispiel:
 *
 *     VCC                 VCC                 VCC                  VCC
 *      +                   +                   +                    +
 *      |                   |                   |                    |
 *      |                   |                   |                    |
 *      o--------- IRQ0     o--------- IRQ1     o--------- IRQ2      o--------- IRQ3
 *      |                   |                   |                    |
 *      |                   |                   |                    |
 *     .-.                 .-.                 .-.                  .-.
 *     | |                 | |                 | |                  | |
 *     | |  10K            | |  10K            | |  10K             | |  10K
 *     '-'                 '-'                 '-'                  '-'
 *      |                   |                   |                    |
 *      |                   |                   |                    |
 *     ===                 ===                 ===                  ===
 *     GND                 GND                 GND                  GND
 * }
 * </pre>
 */
public class IRQExample {

	public static void main(String[] args) throws Exception {
		System.out.println("TEST IRQ");

		// Get events
		AsyncEvents events = AsyncEvents.getAsyncEvents();

		// print event sources count
		int numEvents = AsyncEvents.getAsyncEvents().getSourceCount();
		System.out.println("Numbers event sources: " + numEvents);

		// print IRQ count
		int numIrq = NativeIRQ.getManager().getInstanceCount();
		System.out.println("Numbers IRQ's: " + numIrq);

		// Create event and event handler
		AsyncEvent event = new AsyncEvent();
		event.addHandler(new AsyncEventHandler() {
			@Override
			public synchronized void handleAsyncEvent(final int eventId) {
				System.out.println("IRQ: " + eventId);
			}
		});

		// open and enable the following IRQs
		int[] irqIds = new int[] { 0, 1, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
		for (int id : irqIds) {
			// get the IRQ
			NativeIRQ irq = NativeIRQ.getInstance(id);

			// open the corresponding GPIO
			NativeGPIO gpio = NativeGPIO.getInstance(irq.getGPIOInstanceId());
			gpio.open(GPIO.MODE_INPUT_PULLUP);

			// we use the IRQ id as event id
			irq.open(IRQ.MODE_RISING_EDGE, id);
			irq.enable();

			// bind the event to the event source
			event.bindTo(id);
		}

		// Start event systemF
		events.start();

		// Now we can do anything
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
