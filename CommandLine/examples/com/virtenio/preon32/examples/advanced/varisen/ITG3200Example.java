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

import java.util.Arrays;

import com.virtenio.driver.device.ITG3200;
import com.virtenio.driver.gpio.GPIO;
import com.virtenio.driver.gpio.NativeGPIO;
import com.virtenio.driver.i2c.I2C;
import com.virtenio.driver.i2c.NativeI2C;

/**
 * Example application for the Triple-Axis Gyro sensor ITG3200.
 */
public class ITG3200Example {
	private NativeI2C i2c;
	private ITG3200 gyroSensor;
	private GPIO gyroFrameSyncPin;
	private GPIO gyroIrqPin;

	private void init() throws Exception {
		System.out.println("ITG3200(Init)");

		// open I2C instance where the sensor is connected to
		i2c = NativeI2C.getInstance(1);
		i2c.open(I2C.DATA_RATE_400);

		// get gyro pins, not used in this example
		gyroFrameSyncPin = NativeGPIO.getInstance(1);
		gyroIrqPin = NativeGPIO.getInstance(0);

		// create gyro sensor instance
		gyroSensor = new ITG3200(i2c, ITG3200.ADDR_L);
		gyroSensor.open();

		System.out.println("Done(Init)");
	}

	public void run() throws Exception {
		init();

		short[] gyroValues = new short[3];
		int gyroTRaw = 0;
		float gyroT = 0;

		while (true) {
			try {
				gyroSensor.getGyroRaw(gyroValues, 0);
				gyroTRaw = gyroSensor.getTemperatureRaw();
				gyroT = ITG3200.convertRawTemperatureToCelsius(gyroTRaw);

				System.out.print("ITG3200: " + Arrays.toString(gyroValues));
				System.out.println(", rawT=" + gyroTRaw + ", T=" + gyroT);

				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println("ITG3200 error");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new ITG3200Example().run();
	}
}
