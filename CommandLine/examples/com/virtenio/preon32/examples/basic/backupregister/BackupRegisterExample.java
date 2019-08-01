package com.virtenio.preon32.examples.basic.backupregister;

import com.virtenio.driver.ram.NativeRAM;
import com.virtenio.driver.ram.RAM;
import com.virtenio.driver.watchdog.NativeWatchDog;
import com.virtenio.driver.watchdog.WatchDog;

/**
 * Backup registers are a small amount of registers which can hold data. Backup
 * registers can be read and written. Normally backup registers keep their
 * values beyond a reset or reboot.
 * <p>
 * The backup registers of the Preon32 are provided as a RAM like instance. You
 * can use this instance to access the registers.
 * <p>
 * Programmers can use these registers to store some values, go into deep sleep
 * which results into a reboot and than restore these values. <br>
 * <p>
 * The size of a backup register is system dependent. Look at your hardware
 * documentation for register size. Alternatively you can query the size by
 * using the method {@link RAM#getSize()}.
 */
public class BackupRegisterExample {

	public static void main(String[] args) throws Exception {
		/*
		 * The backup registers are provided as a RAM like instance. The
		 * instance identifier is 0.
		 */
		RAM ram = NativeRAM.getInstance(0);
		ram.open();

		// Read and print the size
		int size = ram.getSize();
		System.out.println("Size: " + size);

		// Print current values in registers
		System.out.println("Current data:");
		for (int i = 0; i < size; i++) {
			System.out.println(i + ":" + ram.read(i));
		}

		// Set new values
		System.out.println("Set data...");
		for (int i = 0; i < size; i++) {
			ram.write(i, i);
		}

		// Read and print values
		System.out.println("Read data:");
		for (int i = 0; i < size; i++) {
			System.out.println(i + ":" + ram.read(i));
		}

		// Set multiple values at once
		System.out.println("Set multiple data...");
		byte[] data = { 0, 101, 103, 105, 107, 109, 0 };
		ram.write(5, data, 1, 5);

		// Read and print multiple values
		System.out.println("Read data:");
		byte[] buffer = new byte[5];
		ram.read(5, buffer, 0, 5);
		for (int i = 0; i < buffer.length; i++) {
			System.out.println(buffer[i] & 0xFF);
		}

		// You can wait for the watchdog to reboot the system. Register values
		// keep their data during reset.
		System.out.println("Waiting for Watchdog to reboot...");
		WatchDog wd = NativeWatchDog.getInstance(0);
		wd.open(5000);
		wd.waitFor();
	}

}
