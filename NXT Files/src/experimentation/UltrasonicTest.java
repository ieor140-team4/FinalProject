package experimentation;

import java.util.ArrayList;
import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Datalogger;

public class UltrasonicTest {

	// Instance variables
	private UltrasonicSensor us;
	private NXTRegulatedMotor motor;
	private Datalogger dl;

	public void scan() {
		motor.rotateTo(-90, false);
		dl.writeLog(us.getDistance());
		motor.rotateTo(90, false);
		dl.writeLog(us.getDistance());

	}

	public UltrasonicTest() {
		us = new UltrasonicSensor(SensorPort.S3);
		motor = Motor.B;
		dl = new Datalogger();
	}

	public void rotateTo(int angle) {
		motor.rotateTo(angle);
	}

	public static void main(String[] args) {
		UltrasonicTest usr = new UltrasonicTest();
		for (int i = 0; i < 12; i++) {
			Button.waitForAnyPress();
			usr.scan();
		}

		Button.waitForAnyPress();

		usr.dl.transmit();
	}

}
