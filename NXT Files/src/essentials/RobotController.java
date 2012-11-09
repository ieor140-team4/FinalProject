package essentials;

import java.util.ArrayList;

import lejos.robotics.navigation.Navigator;

public class RobotController {
	private Navigator navigator;
	private Communicator comm;
	private ArrayList<Message> inbox;
	
	public RobotController(Navigator n) {
		System.out.println("Connecting...");
		comm = new Communicator();
		navigator = n;
	}
	
	public void updateMessage(Message m) {
		if(m.getType() == MessageType.STOP) {
			inbox.clear();
			inbox.add(m);
		} else {
			inbox.add(m);
		}
	}
	
	public void go() {
		while(true) {
			while(!inbox.isEmpty()){
				execute(inbox.get(0));
				inbox.remove(0);
			}
		}
	}
	
	public void execute(Message m) {
		switch(m.getType()) {
		case MOVE:
			navigator.goTo(m.getData()[0], m.getData()[1]);
		case STOP:
			navigator.stop();
		}
	}
}
