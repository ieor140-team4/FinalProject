package essentials;

public class Message {

	private MessageType type;
	private int[] data;
	
	public Message(MessageType mt, int[] d) {
		type = mt;
		data = d;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public int[] getData() {
		return data;
	}
	
}
