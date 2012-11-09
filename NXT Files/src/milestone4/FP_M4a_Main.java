package milestone4;

import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import essentials.RobotController;

public class FP_M4a_Main {

	public static void main(String[] args) {
		double leftWheelDiameter = 5.42; // 5.57
		double rightWheelDiameter = 5.44; //5.59
		double trackWidth = 13.72; // 13.4



		DifferentialPilot dp = new DifferentialPilot(leftWheelDiameter,
				rightWheelDiameter, trackWidth, Motor.A, Motor.C, false);

		dp.setAcceleration(1500);
		dp.setTravelSpeed(30);
		dp.setRotateSpeed(360);
		
		Navigator navigator = new Navigator(dp);
		
		RobotController controller = new RobotController(navigator);
		controller.go();
	}

}
