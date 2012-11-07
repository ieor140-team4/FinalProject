package essentials;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.pc.comm.NXTCommFactory;

public class Communicator {
	private BTConnection btc;
	private DataInputStream dis;
	private DataOutputStream dos;

	/**
	 * Creates a BTCommunicator object and connects it to the computer,
	 * then sets up the data streams and such.
	 */
	public Communicator() {
		connect();
	}
	/**
	 * Establishes a bluetooth connection with the computer.
	 */
	public void connect() {
		String waiting = "Waiting for connection...";
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
			while (isRunning) {
				try {
					MessageType header = MessageType.values()[dataIn.readInt()];
					
					switch (header) {
					case POS_UPDATE:
						x = dataIn.readInt();
						y = dataIn.readInt();
						message = "Received robot pos: " + x + "," + y;
						control.drawRobotPath(x, y);
						
					case OBS_UPDATE:
						x = dataIn.readInt();
						y = dataIn.readInt();
						message = "Received obstacle pos: " + x + "," + y;
						control.drawObstacle(x, y);
					}

				} catch (IOException e) {
					System.out.println("Read Exception in GridControlComm");
					count++;
				}
				control.setMessage(message);
			}
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
	public void send(int header, int x, int y) throws IOException {  
		// send point at or obstacles found
		dos.writeInt(header);
		dos.writeInt(x);
		dos.writeInt(y);
		//dos.write
		dos.flush(); 
	}

	/** 
	 * Obtains a point from the computer
	 * 
	 * @return a point object corresponding to the x,y sent by the computer
	 * @throws IOException
	 */
	public MessageType receive() throws IOException {
		return MessageType.values()[dis.readInt()];
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
}