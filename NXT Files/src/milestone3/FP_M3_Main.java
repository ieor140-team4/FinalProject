package milestone3;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.Pose;
import lejos.util.Datalogger;
import essentials.ButtonInputter;
import essentials.Locator;
import essentials.Scanner;

public class FP_M3_Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LightSensor ls = new LightSensor(SensorPort.S2);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S3);
		Scanner scanner = new Scanner(Motor.B, ls, us);
		Locator locator = new Locator(scanner);

		Datalogger dl = new Datalogger();

		ButtonInputter bi = new ButtonInputter();

		for (int i = 0; i < 4; i++) {
			LCD.clearDisplay();
			System.out.println("Now go to heading: " + 90*i);
			Pose p = bi.display();
			
			for (int j = 0; j < 8; j++) {
				LCD.clearDisplay();
				locator.setPose(p);
				System.out.println("Run " + (j+1) + " out of 8");
				System.out.println("Pose:\nx=" + locator._pose.getX()
						+ "\ny=" + locator._pose.getY() + "\nH=" + locator._pose.getHeading());
				Button.waitForAnyPress();
				
				
				
				locator.locate();
				Pose newP = locator._pose;
				float x = Math.round(100 * newP.getX()) / 100;
				float y = Math.round(100 * newP.getY()) / 100;
				float heading = Math.round(100 * newP.getHeading()) / 100;

				System.out.println("Pose gotten:\nx=" + x + "\ny=" + y + "\nH=" + heading);
				dl.writeLog(newP.getX(), newP.getY(), newP.getHeading());
				Button.waitForAnyPress();
			}
		}
		
		System.out.println("Press button to start transmitting...");
		Button.waitForAnyPress();
		dl.transmit();


	}

}
