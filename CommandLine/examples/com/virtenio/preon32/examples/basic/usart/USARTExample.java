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

package com.virtenio.preon32.examples.basic.usart;

import com.virtenio.driver.usart.NativeUSART;
import com.virtenio.driver.usart.USART;
import com.virtenio.driver.usart.USARTException;
import com.virtenio.driver.usart.USARTParams;
import com.virtenio.io.Console;
import com.virtenio.preon32.examples.common.USARTConstants;

/**
 * Testet die Ansteuerung der Universal Asynchronous Receiver Transmitter (UART)
 * Schnittstelle.
 */
public class USARTExample {
	USART usart;

	public USARTExample() {
		usart = configUSART();
	}

	private USART configUSART() {
		Console console = new Console();

		int instanceID = console.readInt("Geben Sie den USART-Port an (0=STD, 1=EXT1, 2=EXT2)", 0, 2);
		int config = console.readInt("Geben Sie den Baudrate an (\n"
				+ "0=9600, 1=19200, 2=38400, 3=115200, 4=250000)", 0, 4);

		USARTParams params = null;

		switch (config) {
			case 0:
				params = USARTConstants.PARAMS_09600;
				break;
			case 1:
				params = USARTConstants.PARAMS_19200;
				break;
			case 2:
				params = USARTConstants.PARAMS_38400;
				break;
			case 3:
				params = USARTConstants.PARAMS_115200;
				break;
			case 4:
				params = USARTConstants.PARAMS_250000;
				break;
			default:
				params = USARTConstants.PARAMS_115200;
				break;
		}

		NativeUSART usart = NativeUSART.getInstance(instanceID);
		try {
			usart.close();
			usart.open(params);
			return usart;
		} catch (Exception e) {
			return null;
		}
	}

	public void reader() {
		if (usart != null) {
			byte[] buffer = new byte[64];
			for (;;) {
				try {
					int num = usart.read(buffer);
					usart.write(buffer, 0, num);
					usart.flush();
					System.out.print(new String(buffer, 0, num));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void writer() {
		if (usart != null) {
			for (int i = 0; i < 10; i++) {
				try {
					String str = "Hello USART(" + i + ")\n";
					usart.write(str.getBytes());
					usart.flush();
					System.out.print("write: " + str);
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void run() {
		new Thread() {
			public void run() {
				reader();
			}
		}.start();

		writer();

		System.out.flush();
	}

	public static void main(String[] args) throws USARTException {
		new USARTExample().run();
	}
}