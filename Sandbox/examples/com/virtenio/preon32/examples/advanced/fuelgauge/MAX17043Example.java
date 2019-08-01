/*
 * Copyright (c) 2011, Virtenio GmbH
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
 * Für andere Formen der Lizenz nehmen Sie bitte Kontakt mit info@virtenio.de
 */

package com.virtenio.preon32.examples.advanced.fuelgauge;

import com.virtenio.driver.device.MAX17043;
import com.virtenio.driver.gpio.NativeGPIO;
import com.virtenio.driver.i2c.I2C;
import com.virtenio.driver.i2c.I2CException;
import com.virtenio.driver.i2c.NativeI2C;
import com.virtenio.driver.irq.NativeIRQ;
import com.virtenio.vm.event.AsyncEvents;

/** Example for evaluating the fuel gauge with driver for MAX17043 */
@SuppressWarnings("unused")
public class MAX17043Example {

	public static void main(String[] args) throws Exception {
		// IC is connected to i2c instance(0): GPIO1 & GPIO0
		NativeI2C i2c = NativeI2C.getInstance(0);
		try {
			i2c.open(I2C.DATA_RATE_100); // open instance

			MAX17043 max17043 = new MAX17043(i2c);

			// perform initial reset
			max17043.performPowerOnReset();
			Thread.sleep(500);

			// perform "first guess" for battery voltage to state of charge
			// (SOC)
			max17043.performQuickStart();
			Thread.sleep(500);

			while (true) {
				Thread.sleep(1000);
				// read voltage and state of charge every 1000ms
				System.out.println("Voltage = " + max17043.getCellVoltage() + " [V] : "
						+ max17043.getStateOfCharge() + " %");
			}
		} finally {
			try {
				i2c.close();
			} catch (I2CException e) {
				e.printStackTrace();
			}
			System.out.flush();
		}
	}
}
