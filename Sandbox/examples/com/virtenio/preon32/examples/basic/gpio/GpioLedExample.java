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
import com.virtenio.io.Console;
import com.virtenio.preon32.examples.common.Misc;
import com.virtenio.preon32.shuttle.Shuttle13;

/**
 * Testet die Funktion von General Purpose Input/Output (GPIO) an denen LED
 * angeschlossen sind.
 * <p/>
 * 
 * <pre>
 * {@literal
 * Schaltungsbeispiel:
 *              VCC                VCC              VCC
 *               +                  +                +
 *               |                  |                |
 *               |                  |                |
 *               V -> LED           V -> LED         V -> LED
 *               -                  -                -
 *               |                  |                |
 *               |                  |                |
 *              .-.                .-.              .-.
 *              | |  1K            | |  1K          | |  1K
 *              | |                | |              | |
 *              '-'                '-'              '-'
 *               |                  |                |
 *               |                  |                |
 *               |                  |                |
 *               |                  |                |
 *               o                  o                o
 *              GPIO2              GPIO5           GPIO6
 * }
 * </pre>
 */
public class GpioLedExample {
	private int speed = 1000;

	public void run() throws Exception {
		final GPIO green = NativeGPIO.getInstance(Shuttle13.LED_GREEN_GPIO_ID);
		green.open(GPIO.MODE_OUTPUT);

		final GPIO yellow = NativeGPIO.getInstance(Shuttle13.LED_YELLOW_GPIO_ID);
		yellow.open(GPIO.MODE_OUTPUT);

		final GPIO red = NativeGPIO.getInstance(Shuttle13.LED_RED_GPIO_ID);
		red.open(GPIO.MODE_OUTPUT);

		final GPIO amber = NativeGPIO.getInstance(Shuttle13.LED_AMBER_GPIO_ID);
		amber.open(GPIO.MODE_OUTPUT);

		Thread t0 = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						green.set(0);
						Misc.sleep(speed);
						green.set(1);
						yellow.set(0);
						Misc.sleep(speed);
						yellow.set(1);
						red.set(0);
						Misc.sleep(speed);
						red.set(1);
						amber.set(0);
						Misc.sleep(speed);
						amber.set(1);
						//
						red.set(0);
						Misc.sleep(speed);
						red.set(1);
						yellow.set(0);
						Misc.sleep(speed);
						yellow.set(1);
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
			String line = console.readLine("Setze Speed [ms] (default=1000)");
			try {
				speed = Integer.parseInt(line);
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new GpioLedExample().run();
	}
}
