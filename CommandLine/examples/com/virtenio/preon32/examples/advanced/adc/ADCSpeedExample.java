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

import com.virtenio.driver.adc.*;
import com.virtenio.vm.Time;

/**
 * Erfassung von analogen Werten über den internen ADC des Mikrocontrollers.
 * Anwendung von "Single channel read" und "Multi channel read".
 */
public class ADCSpeedExample {

	/**
	 * Executes the ADC example
	 */
	public static void main(String[] args) throws Exception {
		// get ADC instance
		NativeADC adc = NativeADC.getInstance(0);
		adc.open();

		// single channel speed test
		int ch = 0;
		adc.openChannel(ch);

		int[] sampleTimes = { ADC.SAMPLE_TIME_FAST, ADC.SAMPLE_TIME_NORMAL, ADC.SAMPLE_TIME_SLOWEST };
		for (int v : sampleTimes) {
			System.out.println("Measure speed of single channel read: sampleTime=" + v);
			adc.setSampleTime(ch, v);
			int num = 1000;
			for (int k = 10; k > 0; --k) {
				long start = Time.millis();
				for (int i = 0; i < num; i++) {
					adc.getValue(ch);
				}
				long end = Time.millis();
				System.out.println("values/s: " + (1000 * num / (end - start)));
			}
		}

		adc.closeChannel(ch);

		// init and open channels
		System.out.println("Measure speed of multi channel read");
		short[] channels = new short[] { 2, 3, 4 };
		adc.openChannels(channels);
		ADCChannelList channelList = adc.getChannelList(channels);
		channelList.setSampleTime(ADC.SAMPLE_TIME_FASTEST);

		short[] buffer = new short[channels.length];
		int num = 1000;
		for (int k = 10; k > 0; --k) {
			long start = Time.millis();
			for (int i = 0; i < num; i++) {
				channelList.getValues(buffer, 0);
			}
			long end = Time.millis();
			System.out.println("values/s: " + (1000 * num * channelList.size() / (end - start)));
		}

		adc.closeChannels(channels);
		adc.close();

		System.out.flush();
	}
}
