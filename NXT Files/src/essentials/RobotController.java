package essentials;

import java.io.IOException;
import java.util.ArrayList;

import lejos.geom.Point;
import lejos.nxt.Sound;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.util.Delay;

public class RobotController implements ObstacleListener {
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

	/**
	 * Puts the given message in the robot controller's inbox.
	 * 
	 * For any messages besides STOP, they will be read FIFO. A STOP
	 * message will clear the controller's inbox and its current path, as well
	 * as telling the navigator to stop.
	 * 
	 * @param m the message to be put in the inbox
	 */
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

	/**
	 * Upon execution of this command, the robot controller will begin listening to
	 * its inbox for new messages, executing and removing messages as they come in.
	 */
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

	/**
	 * This method gets the current pose from the navigator and passes a message
	 * to the communicator for it to send to the PC.
	 */
	private void sendPose() {
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
	
	private void sendObstacle(PolarPoint obstacleLocation) {
		float[] array = new float[2];
		Pose pose = navigator.getPoseProvider().getPose();
		Point pt = pose.pointAt(obstacleLocation.dist, obstacleLocation.angle);
		
		array[0] = pt.x;
		array[1] = pt.y;
		
		try {
			comm.send(new Message(MessageType.OBS_UPDATE, array));
		} catch (IOException ioe) {
			System.out.println("Exception thrown updating obstacle.");
		}
	}

	/**
	 * This function recurringly calls the sendPose method every 300 ms, so that
	 * the computer can keep track of what the robot is up to while it's moving.
	 */
	private void sendPoseWhileMoving() {

		while (navigator.isMoving() || navigator.getMoveController().isMoving()) {
			Delay.msDelay(300);
			sendPose();
		}

		sendPose();
	}

	/**
	 * Parses the given message and acts on it accordingly.
	 * 
	 * MOVE: calls the navigator to move to a given x,y
	 * TRAVEL: calls the navigator's differential pilot to travel a given dist
	 * ROTATE: calls the navigator's differential pilot to rotate a given angle
	 * FIX: calls the locator to fix the robot's position and update the stored pose in navigator
	 * SET POSE: updates the locator's and navigator's pose to a given pose for human correction
	 * 
	 * @param m
	 */
	private void execute(Message m) {
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
			navigator.getPoseProvider().setPose(locator._pose);
			
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
			sendPose();
			break;
		default:
			break;
		}
		Sound.playNote(Sound.PIANO, 500, 15);
	} 
	
	public void objectFound(PolarPoint obstacleLocation) {
		sendObstacle(obstacleLocation);		
	}
}
