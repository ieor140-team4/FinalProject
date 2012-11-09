package essentials;

import java.io.IOException;
import java.util.ArrayList;

import lejos.nxt.Sound;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;

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
	
	public void sendPose() {
		Pose pose = navigator.getPoseProvider().getPose();
		float[] array = new float[3];
		array[0] = pose.getX();
		array[1] = pose.getY();
		array[2] = pose.getHeading();
		try {
			comm.send(new Message(MessageType.POS_UPDATE, array));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Exception thrown");
		}
	}
	
	public void execute(Message m) {
		Sound.playNote(Sound.PIANO, 450, 15);
		switch(m.getType()) {
		case STOP:
			navigator.stop();
			break;
		case MOVE:
			navigator.goTo(m.getData()[0], m.getData()[1], m.getData()[2]);
			break;
		case FIX_POS:
			locator.locate();
			break;
		case ROTATE:
			navigator.rotateTo(m.getData()[0]);
			break;
		case TRAVEL:
			double angle = navigator.getPoseProvider().getPose().getHeading();
			float dist = m.getData()[0];
			navigator.goTo((dist * (float) Math.cos(angle)), (dist * (float) Math.sin(angle)));
			break;
		default:
			break;
		}
		
		Sound.playNote(Sound.PIANO, 500, 15);
	} 
}
