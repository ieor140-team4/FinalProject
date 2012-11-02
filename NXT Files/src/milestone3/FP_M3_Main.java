package milestone3;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
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

		double leftWheelDiameter = 5.42; // 5.57
		double rightWheelDiameter = 5.44; //5.59
		double trackWidth = 13.72; // 13.4
		DifferentialPilot dp = new DifferentialPilot(leftWheelDiameter,
				rightWheelDiameter, trackWidth, Motor.A, Motor.C, false);
		dp.setAcceleration(1500);
		dp.setTravelSpeed(30);
		dp.setRotateSpeed(360);

		Datalogger dl = new Datalogger();
		Pose p = new Pose();
		p.setLocation(240, 185);
		
		Button.waitForAnyPress();

		for (int i = 0; i < 4; i++) {
			p.setHeading((float) 90 * i);
			locator.setPose(p);
			
			for (int j = 0; j < 8; j++) {
				locator.locate();
				dl.writeLog(locator._pose.getX(), locator._pose.getY(), locator._pose.getHeading());
			}
			
			dp.rotate(90);
		}
		
		LCD.clearDisplay();
		System.out.println("Press button to start transmitting...");
		Button.waitForAnyPress();
		dl.transmit();


	}

}
