package essentials;

import java.io.IOException;
import java.util.ArrayList;

import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

public class RobotController {
	private Navigator navigator;
	private Communicator comm;
	private ArrayList<Message> inbox;
	private Locator locator;

	public RobotController(Navigator n, Locator l) {
		System.out.println("Connecting...");
		comm = new Communicator();
		comm.setController(this);
		navigator = n;
		locator = l;
		inbox = new ArrayList<Message>();
	}

	public void updateMessage(Message m) {
		System.out.println("Updating messages...");
		if(m.getType() == MessageType.STOP) {
			inbox.clear();
			navigator.stop();
			navigator.clearPath();
		} else {
			inbox.add(m);
		}
	}

	public void go() {
		Message currentMessage;

		while(true) {
			while(!inbox.isEmpty()){
				System.out.println("Running message!");
				currentMessage = inbox.get(0);
				inbox.remove(0);
				execute(currentMessage);
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

	public void sendPoseWhileMoving() {

		while (navigator.isMoving() || navigator.getMoveController().isMoving()) {
			Delay.msDelay(300);
			sendPose();
		}

		sendPose();
	}

	public void execute(Message m) {
		Sound.playNote(Sound.PIANO, 450, 15);
		switch(m.getType()) {
		case STOP: //never called
			navigator.stop();
			sendPose();
			break;
		case MOVE:
			Pose startPose = navigator.getPoseProvider().getPose();
			navigator.goTo(m.getData()[0], m.getData()[1]);

			sendPoseWhileMoving();

			break;
		case FIX_POS:
			locator.setPose(navigator.getPoseProvider().getPose());
			locator.locate();
			if (locator._pose.distanceTo(navigator.getPoseProvider().getPose().getLocation()) <= 30) {
				navigator.getPoseProvider().setPose(locator._pose);
			}
			sendPose();
			break;
		case ROTATE:
			((DifferentialPilot) navigator.getMoveController()).rotate(m.getData()[0]);

			/*Pose pose = navigator.getPoseProvider().getPose();
			navigator.goTo(pose.getX(), pose.getY(), (pose.getHeading() + m.getData()[0]));*/

			sendPose();
			break;
		case TRAVEL:

			Pose startingPose = navigator.getPoseProvider().getPose();
			navigator.getMoveController().travel(m.getData()[0], true);

			sendPoseWhileMoving();

			break;
		case SET_POSE:
			locator._pose.setLocation(m.getData()[0], m.getData()[1]);
			locator._pose.setHeading(m.getData()[2]);
			
			navigator.getPoseProvider().setPose(locator._pose);
			break;
		default:
			break;
		}
		Sound.playNote(Sound.PIANO, 500, 15);
	} 
}
