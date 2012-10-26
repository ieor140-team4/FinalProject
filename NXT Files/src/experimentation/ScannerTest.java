package experimentation;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.util.Datalogger;
import essentials.Scanner;

public class ScannerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//Create a scanner and a Datalogger.
		Scanner scanner = new Scanner(Motor.B, new LightSensor(SensorPort.S2));
		Datalogger dl = new Datalogger();
		
		//Scan 8 times at the far point.
		for (int i = 0; i < 8; i++) {
			Button.waitForAnyPress();
			scanner.lightScan(-180, 180);
			int[] bearings = scanner.getBearings();
			dl.writeLog(bearings[0], bearings[1]);
			System.out.println("(" + bearings[0] + ", " + bearings[1] + ")");
		}
		
		System.out.println("Waiting to transmit point 1...");
		Button.waitForAnyPress();
		dl.transmit();
		
		//Scan 8 times at the close point.
		for (int i = 0; i < 8; i++) {
			Button.waitForAnyPress();
			scanner.lightScan(-180, 180);
			int[] bearings = scanner.getBearings();
			dl.writeLog(bearings[0], bearings[1]);
			System.out.println("(" + bearings[0] + ", " + bearings[1] + ")");
		}
		

		System.out.println("Waiting to transmit point 2...");
		Button.waitForAnyPress();
		dl.transmit();
		
	}

}
