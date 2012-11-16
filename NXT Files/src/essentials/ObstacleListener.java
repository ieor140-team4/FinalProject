package essentials;

/**
 * An interface for something that listens to the obstacle detector.
 * 
 * @author nate.kb
 *
 */
public interface ObstacleListener {

	
	/**
	 * This method will be called when the obstacle detector finds an
	 * obstacle, whether through a touch sensor or an ultrasonic sensor.
	 * 
	 * @param objectLocation the location of the object in polar coordinates
	 */
	public void objectFound(PolarPoint objectLocation);
}
