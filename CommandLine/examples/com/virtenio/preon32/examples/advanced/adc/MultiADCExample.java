package com.virtenio.preon32.examples.advanced.adc;/*
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

import com.virtenio.driver.adc.NativeADC;

/**
 * Erfassung von analogen Werten über den internen ADC des Microcontrollers.
 * Anwendung von "Single channel read" und "Multi channel read".
 */
public class MultiADCExample {

	public static void main(String[] args) throws Exception {
		NativeADC adc = NativeADC.getInstance(0);
		adc.open();

		short[] channels = new short[adc.getChannelCount()];
		for (int i = 0; i < channels.length; i++) {
			channels[i] = (short) i;
		}
		adc.openChannels(channels);

		System.out.println("Single channel read");
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < channels.length; j++) {
				int v = adc.getChannel(j).getValue();
				System.out.print("" + v + " ");
			}
			System.out.println();
		}

		System.out.println("Multi channel read");
		short[] buffer = new short[channels.length];
		for (int i = 0; i < 10; i++) {
			adc.getValues(channels, buffer, 0);
			for (int j = 0; j < buffer.length; j++) {
				int v = buffer[j] & 0xFFFF;
				System.out.print("" + v + " ");
			}
			System.out.println();
		}

		adc.closeChannels(channels);
		adc.close();
		
		System.out.flush();
	}
}
