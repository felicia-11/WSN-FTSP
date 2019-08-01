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

package com.virtenio.preon32.examples.advanced.serialflash;

import java.io.InputStream;
import java.io.OutputStream;

import com.virtenio.driver.flash.Flash;
import com.virtenio.driver.flash.FlashSectorEraseStrategy;
import com.virtenio.preon32.node.Node;
import com.virtenio.vm.Time;

/**
 * Example of how to use the flash memory chip on the node.
 */
public class SerialFlashExample {

	/**
	 * Runs the example
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("TEST FLASH");

		/*
		 * Get the flash
		 */
		Flash flash = Node.getInstance().getFlash();
		flash.open();

		/*
		 * Erase whole chip
		 */
		System.out.println("Erase chip");
		long start = Time.millis();
		flash.eraseChip();
		flash.waitWhileBusy();
		long end = Time.millis();
		System.out.println("Erased. Time: " + (end - start));

		/*
		 * Write hello world
		 */
		System.out.println("Hello World Write Test");
		flash.eraseSector(0);
		byte[] b1 = "Hello world!".getBytes();
		flash.write(0, b1);
		b1 = b1.clone();
		flash.read(0, b1);
		if (!new String(b1).equals("Hello world!")) {
			System.out.println("Hello World Error: ");
			for (int i = 0; i < b1.length; i++) {
				int c = (b1[i] & 0xFF);
				System.out.print((char) c);
				System.out.print("(" + c + ")");
			}
			System.out.println();
		}

		/*
		 * Write single bytes
		 */
		System.out.println("Single Value Write Test");
		flash.write(0xFF, 'X');
		flash.write(0x1FF, 'Y');
		flash.write(0x2FF, 'Z');
		if (flash.read(0xFF) != 'X' || flash.read(0x1FF) != 'Y' || flash.read(0x2FF) != 'Z') {
			System.out.println("Single value write error");
		}

		/*
		 * Erase some blocks
		 */
		System.out.println("Block Erase Test");
		int eraseSize = flash.getBlockSize();
		for (int i = 0; i < 5; i++) {
			System.out.println("Erase Block: " + (i * eraseSize));
			flash.eraseSector(i * eraseSize);
			for (int j = 0; j < eraseSize; j++) {
				if (flash.read(i * eraseSize + j) != 0xFF) {
					System.out.println("Flash erase block error");
					System.out.print('[');
					for (int k = 0; k < eraseSize; k++) {
						int v = flash.read(i * eraseSize + k);
						if (v != 0xFF) {
							System.out.println((i * eraseSize + k) + ":" + (char) v + " " + v + " "
									+ Integer.toHexString(v));
						}
					}
					System.out.println(']');
					break;
				}
			}
		}

		/*
		 * Write blocks using an erase strategy
		 */
		System.out.println("Write Test");
		byte[] buffer = new byte[200];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) i;
		}

		OutputStream out = flash.getOutputStream(0, new FlashSectorEraseStrategy());
		InputStream in = flash.getInputStream(0);

		for (int i = 0; i < 25; i++) {
			System.out.println("Buf: " + i);
			out.write(buffer);
			byte[] check = new byte[buffer.length];
			in.read(check);
			for (int j = 0; j < check.length; j++) {
				if (check[j] != buffer[j]) {
					System.out.println("Write Test Error");
					System.out.print('[');
					for (int k = 0; k < check.length; k++) {
						System.out.print(Integer.toString(0xFF & check[j]));
						System.out.print(' ');
					}
					System.out.println(']');
					break;
				}
			}
		}

		/*
		 * Measure flash speed
		 */
		System.out.println("Measure Speed");
		int loops = 400;
		int[] bufferSizes = { 150, 200, 256, 350, 512, 1024 };

		System.out.println("Write With No Erase (Block Size : KB/s");
		for (int bufferSize : bufferSizes) {
			flash.eraseChip();
			flash.waitWhileBusy();
			buffer = new byte[bufferSize];
			out = flash.getOutputStream(0);
			start = Time.millis();
			for (int i = 0; i < loops; i++) {
				out.write(buffer, 0, buffer.length);
			}
			end = Time.millis();
			System.out.println(bufferSize + " : " + ((loops * bufferSize) / (end - start)));
		}

		System.out.println("Write With Erase (Block Size : KB/s)");
		for (int bufferSize : bufferSizes) {
			flash.eraseChip();
			flash.waitWhileBusy();
			buffer = new byte[bufferSize];
			out = flash.getOutputStream(0, new FlashSectorEraseStrategy());
			start = Time.millis();
			for (int i = 0; i < loops; i++) {
				out.write(buffer, 0, buffer.length);
			}
			end = Time.millis();
			System.out.println(bufferSize + " : " + ((loops * bufferSize) / (end - start)));
		}

		System.out.println("Read (Block Size : KB/s)");
		for (int bufferSize : bufferSizes) {
			buffer = new byte[bufferSize];
			in = flash.getInputStream(0);
			start = Time.millis();
			for (int i = 0; i < loops; i++) {
				in.read(buffer, 0, buffer.length);
			}
			end = Time.millis();
			System.out.println(bufferSize + " : " + ((loops * bufferSize) / (end - start)));
		}

		flash.close();
	}
}
