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

import com.virtenio.driver.device.ADT7410;
import com.virtenio.driver.i2c.I2C;
import com.virtenio.driver.i2c.NativeI2C;

/**
 * Test den Zugriff auf den Temperatursensor ADT7410 von Analog über I2C.
 * <p/>
 * <b> Datenblatt des Temperatursensors: </b> <a href=
 * "http://www.analog.com/static/imported-files/data_sheets/ADT7410.pdf"
 * target="_blank">
 * http://www.analog.com/static/imported-files/data_sheets/ADT7410.pdf</a>
 * (Stand: 29.03.2011)
 */
public class ADT7410Example {
	private NativeI2C i2c;
	private ADT7410 temperatureSensor;

	private void init() throws Exception {
		System.out.println("I2C(Init)");
		i2c = NativeI2C.getInstance(1);
		i2c.open(I2C.DATA_RATE_400);

		System.out.println("ADT7410(Init)");
		temperatureSensor = new ADT7410(i2c, ADT7410.ADDR_0, null, null);
		temperatureSensor.open();
		temperatureSensor.setMode(ADT7410.CONFIG_MODE_CONTINUOUS);

		System.out.println("Done(Init)");
	}

	public void run() throws Exception {
		init();
		while (true) {
			try {
				int raw = temperatureSensor.getTemperatureRaw();
				float celsius = temperatureSensor.getTemperatureCelsius();
				System.out.println("ADT7410: raw=" + raw + "; " + celsius + " [°C]");
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println("ADT7410 error");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new ADT7410Example().run();
	}
}