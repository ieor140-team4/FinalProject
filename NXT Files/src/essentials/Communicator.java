package essentials;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.Button;
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
		dos.writeInt(m.getType().ordinal());
		for (int i = 0; i < m.getData().length; i++) {
			dos.writeInt(m.getData()[i]);
		}
	}

	/**
	 * Exits stuff.
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
		 * communications will contain three pieces of information in order:
		 * 
		 * 1) a header that indicates the type of message sent. 0 for the robot's
		 * position, 1 for an obstacle's position
		 * 2) the x coordinate of that position
		 * 3) the y coordinate of that position
		 *    
		 */
		public void run()
		{
			System.out.println(" reader started GridControlComm1 ");
			isRunning = true;
			int x = 0;
			int y = 0;
			String message = "";
			Sound.playNote(Sound.PIANO, 500, 50);
			while (isRunning) {
				try {
					int headerNumber = dis.readInt();
					MessageType header = MessageType.values()[headerNumber];
					System.out.println(header.toString());
					Sound.playNote(Sound.PIANO,300,15);
					switch (header){
					case MOVE:
						Sound.playNote(Sound.PIANO, 325, 15);
						int[] array = new int[2];
						for (int i = 0; i < 2; i++) {
							array[i] = dis.readInt();
						}
						Sound.playNote(Sound.PIANO,350,15);
						controller.updateMessage(new Message(header, array));
					case STOP:
						controller.updateMessage(new Message(header, null));
					default:
						Sound.playNote(Sound.PIANO,200,50);
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