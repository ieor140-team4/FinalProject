package essentials;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class Communicator {
	private BTConnection btc;
	private DataInputStream dis;
	private DataOutputStream dos;
	private RobotController controller;
	private Reader reader;

	/**
	 * Creates a BTCommunicator object and connects it to the computer,
	 * then sets up the data streams and such.
	 */
	public Communicator() {
		reader = new Reader();
		connect();
	}

	public void setController(RobotController rc) {
		controller = rc;
	}

	/**
	 * Establishes a bluetooth connection with the computer.
	 */
	public void connect() {
		String waiting = "Waiting for \nconnection...";
		String connected = "Connected!";

		LCD.drawString(waiting,0,0);
		LCD.refresh();

		btc = Bluetooth.waitForConnection(); 

		LCD.clear();
		LCD.drawString(connected,0,0);
		LCD.refresh();	

		OutputStream os = btc.openOutputStream();
		dos = new DataOutputStream(os);

		InputStream is = btc.openInputStream();
		dis = new DataInputStream(is);

		System.out.println("Data stream opened.");

		if (dis == null) {
			System.out.println(" no Data  ");
		} else if  (!reader.isRunning) {
			reader.start();
		}
	}


	/**
	 * Sends a message to the computer specified by header, x, and y
	 * 
	 * @param header 0 if location info, 1 if obstacle info
	 * @param x        the x coordinate of the information
	 * @param y        the y coordinate of the information
	 * @throws IOException
	 */
	public void send(Message m) throws IOException {
		System.out.println(m.getType().ordinal());
		dos.writeInt(m.getType().ordinal());
		if (m.getData() != null) {
			for (int i = 0; i < m.getData().length; i++) {
				System.out.println(m.getData()[i]);
				dos.writeFloat(m.getData()[i]);
			}
		}
		dos.flush();
		Sound.playNote(Sound.PIANO, 444, 10);
		System.out.println("Send.");
	}

	/**
	 * Closes all the data streams.
	 * 
	 * @throws IOException
	 */
	public void exit() throws IOException {
		dis.close();
		dos.close();
		btc.close();
	}

	/**
	 * reads the  data input stream, and calls DrawRobotPath() and DrawObstacle()
	 * uses OffScreenDrawing,  dataIn
	 * @author Roger Glassey
	 */
	class Reader extends Thread
	{

		int count = 0;
		boolean isRunning = false;

		/**
		 * Runs the reader and takes in readings that the robot sends. The robot's
		 * communications contain two parts: a MessageType that indicates what the
		 * message means the robot to do, and an array of floats that represent the
		 * data included in that message.
		 * 
		 * The size of the float array depends on the message type, so this method
		 * must parse the message type to construct a message object with an accurate
		 * amount of data.
		 *    
		 */
		public void run() {
			System.out.println(" reader started GridControlComm1 ");
			isRunning = true;
			Sound.playNote(Sound.PIANO, 500, 50);
			while (isRunning) {
				try {
					int headerNumber = dis.readInt();
					MessageType header = MessageType.values()[headerNumber];
					System.out.println(header.toString());
					Sound.playNote(Sound.PIANO,300,15);
					switch (header){
					case ROTATE:
						float[] rotate = new float[1];
						rotate[0] = dis.readFloat();
						System.out.println(rotate[0]);
						controller.updateMessage(new Message(header, rotate));
						break;
					case TRAVEL:
						float[] travel = new float[1];
						travel[0] = dis.readFloat();
						System.out.println(travel[0]);
						controller.updateMessage(new Message(header, travel));
						break;
					case MOVE:
						float[] move = new float[3];
						for (int i = 0; i < 2; i++) {
							move[i] = dis.readFloat();
						}
						System.out.println(move[0] + "," + move[1]);
						controller.updateMessage(new Message(header, move));
						break;
					case FIX_POS:
						dis.readFloat();
						dis.readFloat();
						dis.readFloat();
						dis.readBoolean();
						controller.updateMessage(new Message(header, null));
						break;
					case STOP:
						controller.updateMessage(new Message(header, null));
						break;
					case PING:
						send(new Message(MessageType.PONG, null));
						break;
					case SET_POSE:
						float[] newPose = new float[3];
						for (int i = 0; i < 3; i++) {
							newPose[i] = dis.readFloat();
						}
						dis.readBoolean();
						controller.updateMessage(new Message(header, newPose));
						break;
					default:
						System.out.println("Unknown?");
						break;
					}

				} catch (IOException e) {
					System.out.println("Read Exception in GridControlComm");
					count++;
				}
			}
		}
	}
}