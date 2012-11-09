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
	private JTextField statusField;
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

		JPanel statusPanel = new JPanel();
		topPanel.add(statusPanel);

		JLabel lblX = new JLabel("X:");
		statusPanel.add(lblX);

		xField = new JTextField();
		statusPanel.add(xField);
		xField.setColumns(5);

		JLabel lblNewLabel = new JLabel("Y:");
		statusPanel.add(lblNewLabel);

		yField = new JTextField();
		statusPanel.add(yField);
		yField.setColumns(5);

		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonActionListener());
		statusPanel.add(sendButton);

		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new StopButtonActionListener());
		statusPanel.add(stopButton);

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

	private class SendButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("Send button pressed.");
			sendXY();
		}
	}

	private class StopButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("Stop button pressed.");
			sendStop();
		}
	}

	public void sendXY() {
		int x = 0;
		int y = 0;

		try {
			x = Integer.parseInt(xField.getText());
			System.out.println(" get x " + x);
		} catch (Exception e) {
			setMessage("Problem with X field");
			return;
		}

		try {
			y = Integer.parseInt(yField.getText());
			System.out.println(" get y " + y);
		} catch (Exception e) {
			setMessage("Problem  with Y field");
			return;
		}

		communicator.sendMove(x, y);
		repaint();
	}

	public void sendStop() {
		communicator.sendStop();
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