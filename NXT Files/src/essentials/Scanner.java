package essentials;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.NXTRegulatedMotor;

public class Scanner {

	private LightSensor sensor;
	private NXTRegulatedMotor motor;
	private int[] bearings;
	
	public Scanner(NXTRegulatedMotor m, LightSensor ls) {
		motor = m;
		sensor = ls;
		sensor.setFloodlight(false);
		bearings = new int[2];
		m.setSpeed(200);
	}
	
	
	/**
	 * Scans for beacons between a specified start angle and end angle, and then
	 * backwards again. It looks only at light values above 42, which seems to be
	 * a good threshold to use, and stores the peaks found in the scanner's bearings
	 * instance variable.
	 * 
	 * @param startAngle angle to start the scan at, in degrees from forward
	 * @param endAngle   angle to end 1 scan at, in degrees from forward.
	 */
	public void lightScan(int startAngle, int endAngle) {
		int threshold = 42;
		
		motor.rotateTo(startAngle);
		
		int[] ccwBearings = new int[2];
		int[] cwBearings = new int[2];
		
		int oldAngle = motor.getTachoCount();
		int highestLightValue = 0;
		int ccwIndex = 0;
		int cwIndex = 0;
		boolean ccwAssigned = false;
		boolean cwAssigned = false;
		
		motor.rotateTo(endAngle, true);
		
		while (motor.isMoving()) {
			int newAngle = motor.getTachoCount();
			
			if (newAngle != oldAngle) {
				oldAngle = newAngle;
				int lv = sensor.getLightValue();
				
				if ((lv > threshold) && (lv > highestLightValue)) {
					highestLightValue = lv;
					ccwBearings[ccwIndex] = newAngle;
					ccwAssigned = true;
				} else if ((lv < threshold) && (ccwAssigned) && (ccwIndex == 0)) {
					System.out.println("First peak found. Moving on.");
					ccwIndex++;
					highestLightValue = 0;
				}
			}
		}
		
		highestLightValue = 0;
		motor.rotateTo(startAngle, true);
		
		while (motor.isMoving()) {

			int newAngle = motor.getTachoCount();
			
			if (newAngle != oldAngle) {
				oldAngle = newAngle;
				int lv = sensor.getLightValue();
				
				if ((lv > threshold) && (lv > highestLightValue)) {
					highestLightValue = lv;
					cwBearings[cwIndex] = newAngle;
					cwAssigned = true;
				} else if ((lv < threshold) && (cwAssigned) && (cwIndex == 0)) {
					System.out.println("First peak passed. Moving on.");
					cwIndex++;
					highestLightValue = 0;
				}
				
			}
		}
		
		calculateBearingsFromLightValues(ccwBearings, cwBearings);
	}
	
	/**
	 * Takes both sets of bearings found from the clockwise scan and the counterclockwise
	 * scans, then combines them to create a set of true bearings to the beacons.
	 * 
	 * @param ccwBearings the bearings gotten from the counterclockwise scan
	 * @param cwBearings  the bearings gotten from the clockwise scan
	 */
	public void calculateBearingsFromLightValues(int[] ccwBearings, int[] cwBearings) {
		/*
		if (ccwBearings[1] == 0) {
			
			if (Math.abs(ccwBearings[0] - cwBearings[0]) <= 15) {
				ccwBearings[1] = ccwBearings[0];
				ccwBearings[0] = 0;
			}
			
		} else if (cwBearings[1] == 0) {
			
			if (Math.abs(cwBearings[0] - ccwBearings[0]) <= 15) {
				cwBearings[1] = cwBearings[0];
				cwBearings[0] = 0;
			}
			
		}
		*/
		
		for (int i = 0; i < ccwBearings.length; i++) {
			/*
			if (ccwBearings[i] == 0) {
				ccwBearings[i] = cwBearings[1 - i];
			} else if (cwBearings[i] == 0) {
				cwBearings[i] = ccwBearings[1 - i];
			}
			*/
			
			bearings[i] = (ccwBearings[i] + cwBearings[1 - i]) / 2;
		}
	}
	
	/**
	 * 
	 * @return the relative bearings to the light beacons stored in scanner
	 */
	public int[] getBearings() {
		return bearings;
	}
	
}
