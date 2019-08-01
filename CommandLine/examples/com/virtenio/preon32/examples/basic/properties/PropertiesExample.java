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

package com.virtenio.preon32.examples.basic.properties;

/**
 * Testprogramm zum Lesen und Schreiben von Systemproperties. Die
 * Systemproperties können zum einen über das NodeTool editiert werden. Es
 * besteht darüber hinaus auch die Möglichkeit einer Manipulation aus dem Java
 * Programm selbst.
 */
public class PropertiesExample {
	private final String key0 = "key0";
	private final String key1 = "key1";
	private final String key2 = "key2";
	private final String key3 = "keyNULL";

	public void write() {
		setProperty(key0, "Schlüssel_0");
		setProperty(key1, "Schlüssel_1");
		setProperty(key2, "Schlüssel_2");
		setProperty(key3, null);
	}

	public void read() {
		System.out.println(key0 + ":" + System.getProperty(key0));
		System.out.println(key1 + ":" + System.getProperty(key1));
		System.out.println(key2 + ":" + System.getProperty(key2));
		System.out.println(key3 + ":" + System.getProperty(key3));
	}

	private void setProperty(String key, String value) {
		System.out.println(key + " -> " + value);
		System.setProperty(key, value);
	}

	public static void main(String[] args) {
		PropertiesExample example = new PropertiesExample();
		example.read();
		example.write();
		example.read();
		System.out.flush();
	}
}
