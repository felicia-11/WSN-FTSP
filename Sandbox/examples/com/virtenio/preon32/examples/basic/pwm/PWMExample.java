package com.virtenio.preon32.examples.basic.pwm;

import com.virtenio.driver.pwm.NativePWM;
import com.virtenio.driver.pwm.PWM;

/**
 * Demonstrates the usage of PWM instance. Outputs PWM signals on each PWM
 * channel.
 */
public class PWMExample {

	/**
	 * Method to run the example
	 */
	public static void main(String[] args) throws Exception {
		// get PWM instance
		int numPwms = NativePWM.getManager().getInstanceCount();
		PWM[] pwms = new PWM[numPwms];
		for (int i = 0; i < numPwms; i++) {
			pwms[i] = NativePWM.getInstance(i);
		}
		for (PWM pwm : pwms) {
			System.out.println("Channel Count:" + pwm.getChannelCount());
		}

		// test frequencies in Hz
		int[] frequencies = { 100, 400, 2000, 5000, 10000, 20000, 40000, 100000 };

		// open PWMs with each test frequency
		for (int f : frequencies) {
			// open the PWMs and print resolution
			System.out.println("Open PWMs: " + f);
			for (PWM pwm : pwms) {
				pwm.open(f);
				pwm.enable();
				System.out.println("Resolution: " + pwm.getResolution());
			}

			// open each channel of each PWM and set duty cycle
			int steps = 6;
			int step = 0xFFFF / steps;
			for (int i = 0; i <= steps; i++) {
				System.out.println("Open channels: " + (step * i));
				for (PWM pwm : pwms) {
					for (int j = 0; j < pwm.getChannelCount(); j++) {
						pwm.getChannel(j).open(step * i);
						pwm.getChannel(j).enable();
					}
				}
				Thread.sleep(5000);
			}

			// close channels and PWM
			System.out.println("Close");
			for (PWM pwm : pwms) {
				for (int j = 0; j < pwm.getChannelCount(); j++) {
					pwm.getChannel(j).close();
				}
				pwm.close();
			}
		}
		
		System.out.flush();
	}
}
