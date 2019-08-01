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

import com.virtenio.driver.device.SHT21;
import com.virtenio.driver.i2c.I2C;
import com.virtenio.driver.i2c.NativeI2C;

/**
 * Test den Zugriff auf den Sensor SHT21 von Sensirion über I2C.
 *
 * <p/>
 * <b> Datenblatt des Sensors: </b> <a href=
 * "http://www.sensirion.com/en/pdf/product_information/Datasheet-humidity-sensor-SHT21.pdf"
 * target="_blank">
 * http://www.sensirion.com/en/pdf/product_information/Datasheet
 * -humidity-sensor-SHT21.pdf</a> (Stand: 29.03.2011)
 */
public class SHT21Example {
	private NativeI2C i2c;
	private SHT21 sht21;

	private void init() throws Exception {
		System.out.println("I2C(Init)");
		i2c = NativeI2C.getInstance(1);
		i2c.open(I2C.DATA_RATE_400);

		System.out.println("SHT21(Init)");
		sht21 = new SHT21(i2c);
		sht21.open();
		sht21.setResolution(SHT21.RESOLUTION_RH12_T14);
		sht21.reset();

		System.out.println("Done(Init)");
	}

	public void run() throws Exception {
		init();

		while (true) {
			try {
				// humidity conversion
				sht21.startRelativeHumidityConversion();
				Thread.sleep(100);
				int rawRH = sht21.getRelativeHumidityRaw();
				float rh = SHT21.convertRawRHToRHw(rawRH);

				// temperature conversion
				sht21.startTemperatureConversion();
				Thread.sleep(100);
				int rawT = sht21.getTemperatureRaw();
				float t = SHT21.convertRawTemperatureToCelsius(rawT);

				System.out.print("SHT21: rawRH=" + rawRH + ", RH=" + rh);
				System.out.println(", rawT=" + rawT + "; T=" + t);

				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println("SHT21 error");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new SHT21Example().run();
	}
}
