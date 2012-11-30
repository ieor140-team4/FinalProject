package essentials;

import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;

/**
 * Detects touches made on the touch sensors located on the front of the robot.
 * 
 * @author nate.kb
 *
 */
public class TouchDetector {

	private TouchSensor lb;
	private TouchSensor rb;
	private ObstacleListener listener;
	
	/** Creates a touch detector object using touch sensors located in the given
	 * sensor ports.
	 * @param leftPort The sensor port corresponding to the left bumper.
	 * @param rightPort The sensor port corresponding to the right bumper.
	 */
	public TouchDetector(SensorPort leftPort, SensorPort rightPort) {
		lb = new TouchSensor(leftPort);
		rb = new TouchSensor(rightPort);
		leftPort.addSensorPortListener(new TouchDetectorListener(true));
		rightPort.addSensorPortListener(new TouchDetectorListener(false));
	}
	

	/**
	 * Tells the touch detector who to call when there's something bad and it don't look good.
	 * @param l The obstacle listener that will listen to the Touch Detector.
	 */
	public void setObstacleListener(ObstacleListener l) {
		listener = l;
	}
	
	/**
	 * Listens to the sensor ports corresponding to the touch sensors and updates
	 * the obstacle listener when there is a problem.
	 * 
	 * @author nate.kb
	 *
	 */
	private class TouchDetectorListener implements SensorPortListener {

		private boolean isLeft;

		/**
		 * 
		 * @param left True if it's the left touch sensor, false if it's the right.
		 */
		public TouchDetectorListener(boolean left) {
			isLeft = left;
		}

		/**
		 * When the touch sensor becomes pressed, it passes the information
		 * to the obstacleListener if there is one attached.
		 * 
		 */
		public void stateChanged(SensorPort port, int aOldValue, int aNewValue) {
			PolarPoint obstacleLocation;
			
			if ((aNewValue < 190) && (aOldValue > 190)) {
				System.out.println("Alert! " + aOldValue + " " + aNewValue);
				Sound.playNote(Sound.PIANO, 400, 50);

				if (listener != null) {
					if (isLeft) {
						obstacleLocation = new PolarPoint((int) Math.round(7 * 2.54), -25);
					} else {
						obstacleLocation = new PolarPoint((int) Math.round(7 * 2.54), 25);
					}
					listener.objectFound(obstacleLocation);
				}


			} else if ((aNewValue > 1000) && (aOldValue > 0)) {
				System.out.println("Released!" + aOldValue + " " + aNewValue);
				Sound.playNote(Sound.PIANO, 550, 50);
			}
		}

	}
}
