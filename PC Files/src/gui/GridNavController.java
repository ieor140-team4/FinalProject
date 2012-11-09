package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.Rectangle;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


// starter for GridNavController

public class GridNavController extends JFrame implements GNC
{

	private JPanel contentPane;
	private JTextField nameField;
	private JTextField xField;
	private JTextField yField;
	private JTextField headingField;
	private JTextField statusField;
	private JTextField distField;
	private JTextField angleField;
	/**
	 * provides communications services: sends and recieves NXT data
	 */
	private GridControlCommunicator communicator = new GridControlCommunicator(this);
	private OffScreenGrid oSGrid = new OffScreenGrid();
	// Add mouse listeners to OffScreenGrid0  

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					GridNavController frame = new GridNavController();
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GridNavController()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 629);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel topPanel = new JPanel();
		topPanel.setBounds(new Rectangle(0, 0, 200, 50));
		contentPane.add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new GridLayout(3, 1, 0, 0));

		JPanel connectPanel = new JPanel();
		topPanel.add(connectPanel);

		JLabel lblName = new JLabel("Name");
		connectPanel.add(lblName);

		nameField = new JTextField();
		connectPanel.add(nameField);
		nameField.setColumns(10);

		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new BtnConnectActionListener());
		connectPanel.add(btnConnect);

		//Create a panel with information about position: x, y, heading
		JPanel positionStatusPanel = new JPanel();
		topPanel.add(positionStatusPanel, BorderLayout.NORTH);

		JLabel lblX = new JLabel("X:");
		positionStatusPanel.add(lblX);

		xField = new JTextField();
		positionStatusPanel.add(xField);
		xField.setColumns(5);

		JLabel lblNewLabel = new JLabel("Y:");
		positionStatusPanel.add(lblNewLabel);

		yField = new JTextField();
		positionStatusPanel.add(yField);
		yField.setColumns(5);
		
		JLabel lblHeading = new JLabel("Heading:");
		positionStatusPanel.add(lblHeading);
		
		headingField = new JTextField();
		positionStatusPanel.add(headingField);
		headingField.setColumns(5);
		
		//Create a panel with information about distances, for travelling and rotating.
		JPanel distStatusPanel = new JPanel();
		topPanel.add(distStatusPanel, BorderLayout.CENTER);
		
		JLabel lblDist = new JLabel("Distance:");
		distStatusPanel.add(lblDist);
		
		distField = new JTextField();
		distStatusPanel.add(distField);
		distField.setColumns(5);
		
		JLabel lblAngle = new JLabel("Angle:");
		distStatusPanel.add(lblAngle);
		
		angleField = new JTextField();
		distStatusPanel.add(angleField);
		angleField.setColumns(5);

		//Create a panel with buttons on it for transmitting the info to the robot
		JPanel buttonPanel = new JPanel();
		topPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		JButton sendButton = new JButton("Go To X,Y,H");
		sendButton.addActionListener(new MoveButtonActionListener());
		buttonPanel.add(sendButton);
		
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new StopButtonActionListener());
		buttonPanel.add(stopButton);
		
		JButton travelButton = new JButton("Travel");
		travelButton.addActionListener(new TravelButtonActionListener());
		buttonPanel.add(travelButton);
		
		JButton rotateButton = new JButton("Rotate");
		rotateButton.addActionListener(new RotateButtonActionListener());
		buttonPanel.add(rotateButton);
		
		JButton fixButton = new JButton("Fix");
		fixButton.addActionListener(new FixButtonActionListener());
		buttonPanel.add(fixButton);

		JPanel panel_2 = new JPanel();
		topPanel.add(panel_2);

		JLabel lblStatus = new JLabel("Status");
		panel_2.add(lblStatus);

		statusField = new JTextField();
		panel_2.add(statusField);
		statusField.setColumns(20);


		contentPane.add(oSGrid, BorderLayout.CENTER);

		oSGrid.textX = this.xField;
		oSGrid.textY = this.yField;


	}

	private class BtnConnectActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			String name = nameField.getText();
			communicator.connect(name);
			System.out.println("Connect to "+name);
		}
	}

	private class MoveButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("Send button pressed.");
			sendMove();
		}
	}

	private class StopButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("Stop button pressed.");
			sendStop();
		}
	}
	
	private class TravelButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("Travel button pressed.");
			sendTravel();
		}
	}
	
	private class RotateButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("Rotate button pressed.");
			sendRotate();
		}
	}
	
	private class FixButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("Fix button pressed.");
			sendFix();
		}
	}
	
	

	public void sendMove() {
		float x = 0;
		float y = 0;
		float heading = 0;

		try {
			x = Float.parseFloat(xField.getText());
			System.out.println(" get x " + x);
		} catch (Exception e) {
			setMessage("Problem with X field");
			return;
		}

		try {
			y = Float.parseFloat(yField.getText());
			System.out.println(" get y " + y);
		} catch (Exception e) {
			setMessage("Problem  with Y field");
			return;
		}
		
		try {
			heading = Float.parseFloat(headingField.getText());
			System.out.println(" get heading " + heading);
		} catch (Exception e) {
			setMessage("Problem with heading field");
			return;
		}

		communicator.sendDestination(x, y, heading);
		repaint();
	}

	public void sendStop() {
		communicator.sendStop();
		repaint();
	}
	
	public void sendFix() {
		communicator.sendFix();
		repaint();
	}
	
	public void sendTravel() {
		float dist = 0;
		
		try {
			dist = Float.parseFloat(distField.getText());
			System.out.println(" get dist " + dist);
		} catch (Exception e) {
			setMessage("Problem with travel field");
			return;
		}
		
		communicator.sendTravel(dist);
		repaint();
	}
	
	public void sendRotate() {
		float angle = 0;
		
		try {
			angle = Float.parseFloat(angleField.getText());
			System.out.println(" get angle " + angle);
		} catch (Exception e) {
			setMessage("Problem with Angle Field");
			return;
		}
		
		communicator.sendRotate(angle);
		repaint();
	}

	public void setMessage(String message) {
		statusField.setText(message);
	}

	public void drawRobotPath(int x, int y) {
		oSGrid.drawRobotPath(x, y);
	}

	public void drawObstacle(int x, int y) {
		oSGrid.drawObstacle(x, y);
	}
}