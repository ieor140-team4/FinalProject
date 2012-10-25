package milestone1;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class FP_M1_Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		double leftWheelDiameter = 5.42; // 5.57
		double rightWheelDiameter = 5.44; //5.59
		double trackWidth = 13.72; // 13.4



		DifferentialPilot dp = new DifferentialPilot(leftWheelDiameter,
				rightWheelDiameter, trackWidth, Motor.A, Motor.C, false);
		
		dp.setAcceleration(1500);
		dp.setTravelSpeed(30);
		dp.setRotateSpeed(360);

		Button.waitForAnyPress();

		dp.travel(488); //travel 488 cm = 4.88 m = 16 tiles

		Button.waitForAnyPress();

		int numRotations = 4;
		for (int i = 0; i < numRotations; i++) {
			dp.rotate(90);
		}
		
		Button.waitForAnyPress();
		
		for (int i = 0; i < numRotations; i++) {
			dp.rotate(-90);
		}
		
		Button.waitForAnyPress();
		
		int sides = 4;
		for (int i = 0; i < sides; i++) {
			dp.travel(120);
			dp.rotate(90);
		}


	}

}
