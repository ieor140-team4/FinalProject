package essentials;

import java.util.ArrayList;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.robotics.navigation.Navigator;

public class RobotController {
	private Navigator navigator;
	private Communicator comm;
	private ArrayList<Message> inbox;
	private Locator locator;
	
	public RobotController(Navigator n) {
		System.out.println("Connecting...");
		comm = new Communicator();
		comm.setController(this);
		navigator = n;
		inbox = new ArrayList<Message>();
	}
	
	public void updateMessage(Message m) {
		System.out.println("Updating messages...");
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
				System.out.println("Running message!");
				try {
					execute(inbox.get(0));
					inbox.remove(0);
				} catch (NullPointerException npe) {
					System.out.println("You are correct.");
				}
			}
		}
	}
	
	public void execute(Message m) {
		Sound.playNote(Sound.PIANO, 450, 15);
		switch(m.getType()) {
		case STOP:
			navigator.stop();
			break;
		case MOVE:
			navigator.goTo(m.getData()[0], m.getData()[1]);
			break;
		case MOVE_HEADING:
			navigator.goTo(m.getData()[0], m.getData()[1], m.getData()[2]);
			break;
		case FIX_POS:
			locator.locate();
			break;
		default:
			break;
		}
		
		Sound.playNote(Sound.PIANO, 500, 15);
	} 
}
