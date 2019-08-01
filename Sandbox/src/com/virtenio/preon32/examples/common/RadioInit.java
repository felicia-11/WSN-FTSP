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

import com.virtenio.driver.device.at86rf231.AT86RF231;
import com.virtenio.misc.PropertyHelper;
import com.virtenio.preon32.node.Node;
import com.virtenio.radio.ieee_802_15_4.Frame;

/** Beispiele einer Initialisierung der Funkverbindung. */
public class RadioInit {

	/**
	 * <pre>
	 *  {@literal
	 *      Value        TX Output Power [dBm]
	 *      0x0                 3.0
	 *      0x1                 2.8
	 *      0x2                 2.3
	 *      0x3                 1.8
	 *      0x4                 1.3
	 *      0x5                 0.7
	 *      0x6                 0.0
	 *      0x7                 -1
	 *      0x8                 -2
	 *      0x9                 -3
	 *      0xA                 -4
	 *      0xB                 -5
	 *      0xC                 -7
	 *      0xD                 -9
	 *      0xE                 -12
	 *      0xF                 -17
	 * }
	 * </pre>
	 */
	private final int TRANSMIT_POWER = PropertyHelper.getInt("radio.transmit_power", 0, 0, 15);

	/**
	 * A CCA measurement is used to detect a clear channel. Four modes are
	 * specified by IEEE 802.15.4 - 2006:
	 * <p/>
	 * mode 1: Energy above threshold. CCA shall report a busy medium upon
	 * detecting any energy above the ED threshold. mode 2: Carrier sense only.
	 * CCA shall report a busy medium only upon the detection of a signal with
	 * the modulation and spreading characteristics of an IEEE 802.15.4
	 * compliant signal. The signal strength may be above or below the ED
	 * threshold. mode 0,3: Carrier sense with energy above threshold. CCA shall
	 * report a busy medium using a logical combination of
	 * <li>Detection of a signal with the modulation and spreading
	 * characteristics of this standard and</li>
	 * <li>Energy above the ED threshold.</li> Where the logical operator may be
	 * configured as either OR (mode 0) or AND (mode 3).
	 */
	private final int CCA_MODE = PropertyHelper.getInt("radio.cca_mode", 3, 0, 3);
	private final int CCA_THREHOLD = PropertyHelper.getInt("radio.cca_threshold", 7, 0, 15);

	/**
	 * Number frame retries
	 */
	private final int FRAME_RETIRES = PropertyHelper.getInt("radio.frame_retries", 1, 0, 15);

	/**
	 * CSMA related values
	 */
	private final int CSMA_RETIRES = PropertyHelper.getInt("radio.csma_retries", 1, 0, 7);
	private final int CSMA_SEED = PropertyHelper.getInt("radio.csma_seed", 511, 0, 1023);

	/**
	 * Backoff exponent
	 */
	private final int MIN_BACKOFF_EXPONENT = PropertyHelper.getInt("radio.min_backoff_exponent", 3,
			0, 15);
	private final int MAX_BACKOFF_EXPONENT = PropertyHelper.getInt("radio.max_backoff_exponent", 5,
			0, 15);

	/**
	 * Address related parts
	 */
	private final int PAN_ID = PropertyHelper.getInt("radio.pan_id", 0xCAFE, 0, 0xFFFF);
	private final int SHORT_ADDR = PropertyHelper.getInt("radio.short_address", 0, 0, 0xFFFF);
	private final long LONG_ADDR = PropertyHelper.getLong("radio.long_address", 0);
	private final boolean I_AM_COORD = PropertyHelper.getBool("radio.i_am_cooord", false);

	/**
	 * Radio channel
	 */
	private final int CHANNEL = PropertyHelper.getInt("radio.channel", AT86RF231.CHANNEL_MIN,
			AT86RF231.CHANNEL_MIN, AT86RF231.CHANNEL_MAX);

	/**
	 * 
	 * @param channel
	 * @param panID
	 * @param addr
	 * @return
	 */
	public static AT86RF231 initRadio() throws Exception {
		RadioInit ri = new RadioInit();
		AT86RF231 radio = Node.getInstance().getTransceiver();
		radio.open();
		radio.reset();
		radio.setState(AT86RF231.STATE_TRX_OFF);
		radio.setRxSaveMode(true);
		radio.setMaxFrameRetries(ri.FRAME_RETIRES);
		radio.setMaxCSMARetries(ri.CSMA_RETIRES);
		radio.setCSMASeed(ri.CSMA_SEED);
		radio.setBackoffExponent(ri.MIN_BACKOFF_EXPONENT, ri.MAX_BACKOFF_EXPONENT);
		radio.setChannel(ri.CHANNEL);
		radio.setTransmitPower(ri.TRANSMIT_POWER);
		radio.setCCAMode(ri.CCA_MODE, ri.CCA_THREHOLD);
		radio.setAddressFilter(ri.PAN_ID, ri.SHORT_ADDR, ri.LONG_ADDR, ri.I_AM_COORD);
		radio.setPendingData(false);
		radio.setState(AT86RF231.STATE_RX_AACK_ON);
		return radio;
	}

	/**
	 * @param addrSRC
	 *            Addresse des Senders
	 * @param panID
	 *            Gruppenaddresse von Sender und Empfänger
	 * @param addrDST
	 *            Addresse des Empfänger
	 * 
	 * @return Gibt ein vorkonfiguriertes Frame zurück
	 */

	public static Frame prepareFrame(int addrSRC, int panID, int addrDST) {
		Frame frame = new Frame(Frame.TYPE_DATA | Frame.ACK_REQUEST | Frame.DST_ADDR_16
				| Frame.INTRA_PAN | Frame.SRC_ADDR_16);
		frame.setSrcAddr(addrSRC);
		frame.setSrcPanId(panID);
		frame.setDestAddr(addrDST);
		frame.setDestPanId(panID);
		return frame;
	}

}
