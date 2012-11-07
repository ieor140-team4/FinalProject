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
		switch (m.getType()) {
		case MOVE:
			navigator.goTo(m.getData()[0], m.getData()[1]);
		case STOP:
			navigator.stop();
		}
	}
}
