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

import com.virtenio.driver.device.BH1710FVC;
import com.virtenio.driver.gpio.NativeGPIO;
import com.virtenio.driver.i2c.I2C;
import com.virtenio.driver.i2c.NativeI2C;

/**
 * Testet den Zugriff auf einen Lichtsensors (BH1715FVC) über den
 * Inter-Integrated Circuit (I2C) Bus. Der Lichtsensor ist optionales Zubehöhr
 * und nicht auf der Grundplatine enthalten. Der Lichtsensor muss an die Pins
 * des Verbindungssteckers für den I2C Bus verbunden werden. Auch ist eine
 * Spannungsversorgung des Sensor mit +3.3VDC vorzusehen.
 */
public class BH1715fVCExample {
	private NativeGPIO dvi;
	private NativeI2C i2c;
	private BH1710FVC lightsensor;

	private void init() throws Exception {
		System.out.println("GPIO(Init)");
		dvi = NativeGPIO.getInstance(10);

		System.out.println("I2C(Init)");
		i2c = NativeI2C.getInstance(1);
		i2c.open(I2C.DATA_RATE_400);

		System.out.println("BH1710FVC(Init)");
		lightsensor = new BH1710FVC(i2c, BH1710FVC.ADDR_L, dvi);
		lightsensor.open();
		lightsensor.enable(true);
		lightsensor.writeCmd(BH1710FVC.CMD_CONT_HRES);
		System.out.println("Done(Init)");
	}

	public void run() throws Exception {
		init();

		while (true) {
			try {
				int raw = lightsensor.getValueLx();
				int lx = BH1710FVC.convertRawToLx(raw);
				System.out.println("BH1710FVC: raw=" + raw + "; " + lx + " [lx]");
				Thread.sleep(500);
			} catch (Exception e) {
				System.out.println("BH1710FVC error");
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new BH1715fVCExample().run();
	}
}
