package essentials;

/**
 * This class contains information to be communicated to the robot controller from
 * the data that the communicator receives.
 * 
 * @author nate.kb
 *
 */
public class Message {

	private MessageType type;
	private float[] data;
	
	/**
	 * 
	 * @param mt An enum for various types of message used
	 * @param d An array of floats that represent the data stored in the message
	 */
	public Message(MessageType mt, float[] d) {
		type = mt;
		data = d;
	}
	
	/**
	 * 
	 * @return the enum associated with this message
	 */
	public MessageType getType() {
		return type;
	}
	
	/**
	 * 
	 * @return the array of floats stored within this message
	 */
	public float[] getData() {
		return data;
	}
	
}
