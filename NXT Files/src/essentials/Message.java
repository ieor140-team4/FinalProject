package essentials;

public class Message {

	private MessageType type;
	private float[] data;
	
	public Message(MessageType mt, float[] d) {
		type = mt;
		data = d;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public float[] getData() {
		return data;
	}
	
}
