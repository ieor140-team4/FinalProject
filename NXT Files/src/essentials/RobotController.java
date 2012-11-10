package essentials;

import java.io.IOException;
import java.util.ArrayList;

import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;

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

	public void execute(Message m) {
		Sound.playNote(Sound.PIANO, 450, 15);
		switch(m.getType()) {
		case STOP:
			navigator.stop();
			sendPose();
			break;
		case MOVE:
			Pose oldPose = navigator.getPoseProvider().getPose();
			Pose newPose;
			navigator.goTo(m.getData()[0], m.getData()[1], m.getData()[2]);
			while(navigator.isMoving()) {
				newPose = navigator.getPoseProvider().getPose();

				if (newPose.distanceTo(oldPose.getLocation()) > 10) {
					oldPose.setLocation(newPose.getLocation());
					oldPose.setHeading(newPose.getHeading());
					sendPose();
				}
			}
			sendPose();
			break;
		case FIX_POS:
			locator.locate();
			sendPose();
			break;
		case ROTATE:
			((DifferentialPilot) navigator.getMoveController()).rotate(m.getData()[0]);

			/*Pose pose = navigator.getPoseProvider().getPose();
			navigator.goTo(pose.getX(), pose.getY(), (pose.getHeading() + m.getData()[0]));*/

			sendPose();
			break;
		case TRAVEL:

			navigator.getMoveController().travel(m.getData()[0]);

			/* double angle = Math.toRadians(navigator.getPoseProvider().getPose().getHeading());
			float dist = m.getData()[0];
			navigator.goTo((dist * (float) Math.cos(angle)), (dist * (float) Math.sin(angle))); */
			Pose oldPose2 = navigator.getPoseProvider().getPose();
			Pose newPose2;
			while(navigator.isMoving()) {
				newPose2 = navigator.getPoseProvider().getPose();

				if (newPose2.distanceTo(oldPose2.getLocation()) > 10) {
					oldPose2.setLocation(newPose2.getLocation());
					oldPose2.setHeading(newPose2.getHeading());
					sendPose();
				}
			}
			sendPose();
			break;
		default:
			break;
		}
		Sound.playNote(Sound.PIANO, 500, 15);
	} 
}
