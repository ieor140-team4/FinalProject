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
				Sound.playNote(Sound.PIANO, 400, 15);
				System.out.println("Here...");
				Button.waitForAnyPress();
				
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
		case MOVE:
			navigator.goTo(m.getData()[0], m.getData()[1]);
		case MOVE_HEADING:
			navigator.goTo(m.getData()[0], m.getData()[1], m.getData()[2]);
		case FIX_POS:
			locator.locate();
		default:
			break;
		}
		
		Sound.playNote(Sound.PIANO, 500, 15);
	} 
}
