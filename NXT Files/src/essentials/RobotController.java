package essentials;

import lejos.robotics.navigation.Navigator;

public class RobotController {
	private Navigator navigator;
	private Communicator comm;
	
	public RobotController(Navigator n) {
		comm = new Communicator();
		navigator = n;
	}
	
	public void updateMessage(Message m) {
		
	}
}
