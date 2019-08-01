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
 * Ein Vertrieb oder eine Ver�ffentlichung in jeglicher Form ist nicht ohne Zustimmung von Virtenio erlaubt.
 * F�r andere Formen der Lizenz nehmen Sie bitte Kontakt mit info@virtenio.de auf.
 */

import com.virtenio.driver.adc.ADC;
import com.virtenio.driver.adc.ADCChannelList;
import com.virtenio.driver.adc.ADCSamplerListener;
import com.virtenio.driver.adc.NativeADC;
import com.virtenio.driver.adc.TimerADCSampler;
import com.virtenio.driver.adc.TriggeredADCSampler;
import com.virtenio.driver.timer.NativeTimer;
import com.virtenio.driver.timer.PeriodicTimer;

/**
 * Zeitgesteuerte Erfassung von analogen Werte �ber einen ADC
 */
public class TimedADCExample {

	public static void main(String[] args) throws Exception {
		// ADC-Instanz aus dem Mikrocontroller
		ADC adc = NativeADC.getInstance(0);
		adc.open();

		// Kennung der ADC-Kan�le
		short[] channels = { 0, 1, 2, 3, 4, 5 };
		ADCChannelList channelList = adc.getChannelList(channels);

		// Speicherplatz f�r Messwerte
		short[] buffer = new short[1024];

		// Ereignis-Listener f�r Buffer-F�llstand
		ADCSamplerListener listener = new ADCSamplerListener() {
			@Override
			public void adcSamplerNotify(int event) {
				if (event == ADCSamplerListener.HALF_FULL) {
					System.out.println("HALF FULL");
				} else {
					System.out.println("FULL");
				}
			}
		};
		channelList.openChannels();

		// zeitgesteuerte Datenerfassung
		TriggeredADCSampler triggerSampler = new TriggeredADCSampler(channelList, buffer, listener);
		int millis = 50;
		PeriodicTimer timer = new PeriodicTimer(NativeTimer.getInstance(0), 0, millis);
		TimerADCSampler timerSampler = new TimerADCSampler(triggerSampler, timer);
		timerSampler.start();

		// Datenerfassung l�uft nebenl�ufig, so dass das Hauptprogramm andere
		// Funktionen durchf�hren kann
		while (true) {
			// monitor F�llstand
			System.out.println(triggerSampler.getOffset());
			Thread.sleep(500);
		}
	}
}
