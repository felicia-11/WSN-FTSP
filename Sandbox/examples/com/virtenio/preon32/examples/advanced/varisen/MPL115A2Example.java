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

package com.virtenio.preon32.examples.advanced.varisen;

import com.virtenio.driver.device.MPL115A2;
import com.virtenio.driver.gpio.GPIO;
import com.virtenio.driver.gpio.NativeGPIO;
import com.virtenio.driver.i2c.I2C;
import com.virtenio.driver.i2c.NativeI2C;

/**
 * Test den Zugriff auf den Sensor MPL115A2 von Freescale über I2C.
 * 
 * <p/>
 * <b> Datenblatt des Sensors: </b> <a href=
 * "http://cache.freescale.com/files/sensors/doc/data_sheet/MPL115A2.pdf"
 * target="_blank">
 * http://cache.freescale.com/files/sensors/doc/data_sheet/MPL115A2.pdf</a>
 * (Stand: 24.08.2011)
 */
public class MPL115A2Example {
	private NativeI2C i2c;
	private MPL115A2 pressureSensor;

	private void init() throws Exception {
		System.out.println("I2C(Init)");
		i2c = NativeI2C.getInstance(1);
		i2c.open(I2C.DATA_RATE_400);

		System.out.println("GPIO(Init)");
		GPIO resetPin = NativeGPIO.getInstance(24);
		GPIO shutDownPin = NativeGPIO.getInstance(12);

		System.out.println("MPL115A2(Init)");
		pressureSensor = new MPL115A2(i2c, resetPin, shutDownPin);
		pressureSensor.open();
		pressureSensor.setReset(false);
		pressureSensor.setShutdown(false);

		System.out.println("Done(Init)");
	}

	public void run() throws Exception {
		init();

		while (true) {
			try {
				pressureSensor.startBothConversion();
				Thread.sleep(MPL115A2.BOTH_CONVERSION_TIME);
				int pressurePr = pressureSensor.getPressureRaw();
				int tempRaw = pressureSensor.getTemperatureRaw();
				float pressure = pressureSensor.compensate(pressurePr, tempRaw);
				System.out.println("MPL115A2 P=" + pressure);
				Thread.sleep(1000 - MPL115A2.BOTH_CONVERSION_TIME);

				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println("MPL115A2 error");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new MPL115A2Example().run();
	}
}
