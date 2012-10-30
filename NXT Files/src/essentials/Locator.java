package essentials;

import lejos.geom.Point;
import lejos.robotics.navigation.Pose;



/**
 *Starting point navigation lab - for testing the fix method
 *R. Glassey 10/08
 **/
class Locator

{ 




	/**
	 *sets beaconBearing array based on current position.
	 *In your robot, , you will use the scanner to get this data
	 */

	public float[] scanBeacons()
	{
		float bearings[] = {0,0};

		System.out.println("Pose"+_pose.getX()+" "+_pose.getY()+" "+_pose.getHeading());
		//** for testing only.  You will need something much different
		for (int i = 0; i < 2; i++) {
			bearings[i] = _pose.angleTo(beacon[i])-_pose.getHeading();// relative bearing;
			System.out.println(beacon[i]);
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


	float hallWidth = 244f; // cm   - check with scanner.
	float beaconY = 244f;   // hallWidth -10;  // verify
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


}
