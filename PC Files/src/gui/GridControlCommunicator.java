package gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;
import essentials.MessageType;

/**
 * Starter version - does everything except read and write juseful data
 * @author Roger
 */
public class GridControlCommunicator
{

	public GridControlCommunicator(GNC control)
	{
		this.control = control; //callback path
		System.out.println("GridControlCom1 built");
	}


	/**
	 * establish a bluetooth connection to the robot ; needs the robot name 
	 * @param name
	 */

	public void connect(String name)
	{
		try {
			connector.close();
		} catch (Exception e) {
			System.out.println(e );
		}


		System.out.println(" conecting to "+name);

		if( connector.connectTo(name, "", NXTCommFactory.BLUETOOTH) ) {
			control.setMessage("Connected to "+name);
			System.out.println(" connected !");
			dataIn = new DataInputStream(connector.getInputStream());
			dataOut = new DataOutputStream( connector.getOutputStream());

			if (dataIn == null) {
				System.out.println(" no Data  ");
			} else if  (!reader.isRunning) {
				reader.start();
			}

		} else {
			System.out.println(" no connection ");
		}
	}

	/**
	 * Sends an x and a y to the robot to indicate what point it should travel to.
	 * 
	 * @param x the x coordinate to travel to
	 * @param y the y coordinate to travel to
	 */
	public void sendDestination(float x, float y, float heading)
	{
		System.out.println(" Communicator sending: MOVE TO " +x+ ", "+y + " with heading " + heading);
		try{
			dataOut.writeInt(MessageType.MOVE.ordinal());
			System.out.println(MessageType.MOVE.ordinal());
			dataOut.writeFloat(x);
			dataOut.writeFloat(y);
			dataOut.writeFloat(heading);
			dataOut.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void sendStop() {
		System.out.println(" Communicator sending: STOP");
		try {
			dataOut.writeInt(MessageType.STOP.ordinal());
			dataOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendRotate(float angle) {
		System.out.println(" Communicator sending: ROTATE");
		try {
			dataOut.writeInt(MessageType.ROTATE.ordinal());
			dataOut.writeFloat(angle);
			dataOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendTravel(float dist) {
		System.out.println(" Communicator sending: TRAVEL");
		try {
			dataOut.writeInt(MessageType.TRAVEL.ordinal());
			dataOut.writeFloat(dist);
			dataOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendFix() {
		System.out.println(" Communicator sending: FIX");
		try {
			dataOut.writeInt(MessageType.FIX_POS.ordinal());
			dataOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
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
			float x = 0;
			float y = 0;
			float heading = 0;
			String message = "";
			while (isRunning) {
				try {
					int index = dataIn.readInt();
					System.out.println(index);
					MessageType header = MessageType.values()[index];
					System.out.println("Message received - " + header.toString());

					switch (header) {
					case POS_UPDATE:
						x = dataIn.readFloat();
						y = dataIn.readFloat();
						heading = dataIn.readFloat();
						System.out.println("Robot position: " + x + "," + y + "," + heading);
						message = x + "," + y + "," + heading;
						//control.drawRobotPath(x, y);
						break;

					case OBS_UPDATE:
						x = dataIn.readFloat();
						y = dataIn.readFloat();
						message = "Received obstacle pos: " + x + "," + y;
						//control.drawObstacle(x, y);
						break;
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
	 * call back reference; calls  setMessage, dreawRobotPositin, drasObstacle;
	 */
	GNC control;
	/**
	 * default bluetooth address. used by reader
	 */
	//   String address = "";
	/**
	 * connects to NXT using bluetooth.  Provides data input stream and data output stream
	 */
	private NXTConnector connector = new NXTConnector();
	/**
	 * used by reader
	 */
	private DataInputStream dataIn;
	/**
	 * used by send()
	 */
	private DataOutputStream dataOut;
	/**
	 * inner class extends Thread; listens  for incoming data from the NXT
	 */
	private Reader reader = new Reader();

}
