package essentials;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;



/**
 *Starting point navigation lab - for testing the fix method
 *R. Glassey 10/08
 **/
public class Locator

{ 

	public Locator(Scanner s) {
		scanner = s;
	}
	
	public void setPose(Pose p) {
		_pose.setLocation(p.getX(),p.getY());
		_pose.setHeading(p.getHeading());
	}
	
	public void locate() {
		
		float x = _pose.getX();
		float y = _pose.getY();
		float head = _pose.getHeading();
		float angleToZeroWall = _pose.relativeBearing(new Point(x, 0));
		float angleToYWall = _pose.relativeBearing(new Point(x, hallWidth));
		
		int distanceToWall = 255;
		float[] bearings = {0f, 0f};
		
		//Compare pose.getY() to hall width, rotate to & scan closer wall
		if (y < (hallWidth / 2)) {
			distanceToWall = scanner.getDistanceToWall(angleToZeroWall);
			bearings = scanBeacons();
		} else {
			distanceToWall = (int) hallWidth - scanner.getDistanceToWall(angleToYWall);
			float[] tempBearings = scanBeacons();
			for (int i = 0; i < 2; i++) {
				bearings[i] = tempBearings[1 - i];
			}
		}
		
		System.out.println("Dist to Wall: " + distanceToWall);
		System.out.println("Bearings: (" + bearings[0] + "," + bearings[1] + ")");
		
		//Then call scanBeacons() to scan for the beacons
		
		//Then fix position.
		fixPosition(bearings, (float) distanceToWall);
	}

	/**
	 *sets beaconBearing array based on current position.
	 *In your robot, , you will use the scanner to get this data
	 */

	public float[] scanBeacons() {
		float x = _pose.getX();
		float y = _pose.getY();
		float head = _pose.getHeading();
		float angleToZeroWall = _pose.relativeBearing(new Point(x, 0));
		float angleToYWall = _pose.relativeBearing(new Point(x, hallWidth));
		
		if (x >= 0) {
			if (y < hallWidth/2) {
				scanner.lightScan((int) angleToZeroWall, (int) angleToZeroWall - 180);
			} else {
				scanner.lightScan((int) angleToYWall, (int) angleToYWall + 180);
			}
		} else {
			if (y < hallWidth/2) {
				scanner.lightScan((int) angleToZeroWall, (int) angleToZeroWall + 180);
			} else {
				scanner.lightScan((int) angleToYWall, (int) angleToYWall - 180);
			}
		}
		
		int[] intBearings = scanner.getBearings();
		
		float[] bearings = {0f, 0f};
		for (int i = 0; i < 2; i++) {
			bearings[i] = (float) intBearings[i];
		}
		
		return bearings;
	}


	/**
	 *calculates position from beacon coordinates and beacon bearing
	 * and echo distance<br>
	 *returns a new Pose
	 */

	public Pose fixPosition(float[] bearings, float echoDistance)
	{
		System.out.println("FIX");

		float y = echoDistance;
		
		float y0 = y;
		float y1 = beaconY - y;
		
		double c = Math.toRadians(normalize(bearings[0] - bearings[1]));

		float x = 0;
		
		if (Math.abs( Math.abs(c) - 180) <= 2) {
			x = (float) (beaconY * Math.tan((Math.PI / 2) - (c/2)) / 2);
		} else if (c > 0) {
			x = (float) (0.5 * ( ((y0 + y1) / Math.tan(c)) +
					Math.sqrt( Math.pow(((y0 + y1) / Math.tan(c)), 2) + (4*y0*y1)) ));
		} else if (c <= 0) {
			x = (float) (0.5 * ( ((y0 + y1) / Math.tan(c)) -
					Math.sqrt( Math.pow(((y0 + y1) / Math.tan(c)), 2) + (4*y0*y1)) ));
		}
		
		_pose.setLocation(x,y);
		float heading = normalize(_pose.angleTo(beacon[0]) - bearings[0]);
		_pose.setHeading(heading);
		
		return _pose;
	}

	/**
	 *returns angle between -180 and 180 degrees
	 */	
	private float normalize(float angle){
		while(angle<-180)angle+=360;
		while(angle>180)angle-=360;	
		return angle;
	}

	//----------Fields-------------------------------------- ------------------------
	//	Scanner scanner;


	float hallWidth = 237f; // cm   - check with scanner.
	float beaconY = 237f;   // hallWidth -10;  // verify
	/**
	 * beacon coordinates as Point objects;
	 */
	Point[] beacon = {new Point(0,0), new Point(0,beaconY)};
	/**
	 * current position robot; set by fixPosition()
	 */
	public Pose _pose = new Pose();
	/**
	 * set by scanForBeacons used by fixPosition()
	 */
	public float echoDistance;
	public float[] _beaconBearing = new float[2];

	private Scanner scanner;
	
}
