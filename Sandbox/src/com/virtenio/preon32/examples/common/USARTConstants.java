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

package com.virtenio.preon32.examples.common;

import com.virtenio.driver.usart.USART;
import com.virtenio.driver.usart.USARTParams;

/** Beispiele einer Konfiguration der USART Schnittstelle. */
public class USARTConstants {

	public final static int PORT_EXT = 0;
	public final static int PORT_CP2103 = 1;

	/** Definition für die Konfiguration mit 9600 Baud */
	public final static USARTParams PARAMS_09600 = new USARTParams(9600, USART.DATA_BITS_8,
			USART.STOP_BITS_1, USART.PARITY_NONE);

	/** Definition für die Konfiguration mit 19200 Baud */

	public final static USARTParams PARAMS_19200 = new USARTParams(19200, USART.DATA_BITS_8,
			USART.STOP_BITS_1, USART.PARITY_NONE);

	/** Definition für die Konfiguration mit 38400 Baud */

	public final static USARTParams PARAMS_38400 = new USARTParams(38400, USART.DATA_BITS_8,
			USART.STOP_BITS_1, USART.PARITY_NONE);

	/** Definition für die Konfiguration mit 115200 Baud */

	public final static USARTParams PARAMS_115200 = new USARTParams(115200, USART.DATA_BITS_8,
			USART.STOP_BITS_1, USART.PARITY_NONE);

	/** Definition für die Konfiguration mit 250000 Baud */

	public final static USARTParams PARAMS_250000 = new USARTParams(250000, USART.DATA_BITS_8,
			USART.STOP_BITS_1, USART.PARITY_NONE);

}
