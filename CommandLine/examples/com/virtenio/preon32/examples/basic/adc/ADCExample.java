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

package com.virtenio.preon32.examples.basic.adc;

import com.virtenio.driver.adc.ADCChannel;
import com.virtenio.driver.adc.NativeADC;
import com.virtenio.io.Console;
import com.virtenio.preon32.examples.common.Misc;

/**
 * Testet die Funktion des Analog-Digital-Umsetzer (kurz ADC). Als einfacher
 * Test ohne zusätzliche Hardware kann die Instanz[0] und Kanal[0] geöffnet
 * werden. An diesem Kanal ist der Benutzerknopf des Preon32Shuttle verbunden.
 * Wird der Benutzerknopf gedrückt, verändert sich der Pegel am Eingang des
 * ADCs.
 */
public class ADCExample {

	public static void main(String[] args) throws Exception {
		Console console = new Console();
		System.out.println("Start Potineter");
		int num_instance = console.readInt("Geben Sie die ADC-Instance an", 0, 0);
		int num_channel = console.readInt("Geben Sie den ADC-Kanal an", 0, 8);

		NativeADC adc0 = NativeADC.getInstance(num_instance);
		adc0.open();

		ADCChannel channel = adc0.getChannel(num_channel);
		channel.open();

		while (true) {
			int val = channel.getValue();
			double voltage = val * 3.3 / 4096.0;
			//
			int vorKomma = (int) voltage;
			int nachKomma = (int) ((voltage - vorKomma) * 100);
			//
			System.out.println("CH[" + num_channel + "]=" + vorKomma + "." + nachKomma + " [V]");
			//
			Misc.sleep(1000);
		}

		// channel.close();
	}

	public static void run2() throws Exception {
		int[] CHANNELinstance = new int[] { 0, // User-Button
			1, 3, 5, 7, 15, 9, // P2_left
			2, 4, 6, 14, 8 // P2_right
		};
		int num = CHANNELinstance.length;

		double[] adc_value = new double[num];
		ADCChannel[] channels = new ADCChannel[num];

		int inst = 0;
		NativeADC adc = NativeADC.getInstance(inst);
		adc.open();

		for (int i = 0; i < num; i++) {
			int id = CHANNELinstance[i];
			System.out.println("open ADC[" + inst + "]");
			channels[i] = adc.getChannel(id);
			channels[i].open();
			adc_value[i] = 0.0;
		}

		while (true) {
			for (int i = 0; i < num; i++) {
				int status = channels[i].getValue();
				if (status < 512) {
					status = 0;
				} else {
					status = 1;
				}
				// Ausgabe nur bei Änderung
				if (status == 1 & (adc_value[i] == 0)) {
					System.out.println("ADC[" + inst + "]-Channel=" + CHANNELinstance[i]);
					adc_value[i] = 1.0;
				} else if (status == 0 & (adc_value[i] == 1)) {
					System.out.println("ADC[" + inst + "]-Channel=" + CHANNELinstance[i]);
					adc_value[i] = 0.0;
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
