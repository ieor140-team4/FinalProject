package gui;

import java.awt.*;
import javax.swing.JTextField;
import java.awt.event.*;

/*
 * OffScreenGrid.java ; manages drawing of grid and robot path on an Image 
 * which is displayed when  repaint() is called. 
 * Mouse listener used    
 * updated 10/13   2011
 * @author  Roger
 */
public class OffScreenGrid extends javax.swing.JPanel
{

	/** Creates new form OffScreenGrid */
	public OffScreenGrid()
	{
		initComponents();
		setBackground(Color.white);
		System.out.println(" OffScreen Drawing constructor ");
		//		makeImage();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (offScreenImage == null)
		{
			makeImage();
		}
		g.drawImage(offScreenImage, 0, 0, this);  //Writes the Image to the screen
	}

	/**
	 * Create the offScreenImage, 
	 */
	public void makeImage()
	{
		System.out.println("OffScreenGrid  makeImage() called");
		imageWidth = getSize().width;// size from the panel
		imageHeight = getSize().height;
		yOrigin = imageHeight - 50;
		robotPrevX = xpixel(0);
		robotPrevY = ypixel(0);
		offScreenImage = createImage(imageWidth, imageHeight);// the container can make an image
		try {Thread.sleep(500);}
		catch(Exception e){};
		System.out.print("Off Screen Grid  create image ----- " );
		System.out.println( offScreenImage == null);
		if(offScreenImage == null)
		{
			//			System.out.println("Null image" );
			offScreenImage =  createImage(imageWidth, imageHeight);
		}
		osGraphics = (Graphics2D) offScreenImage.getGraphics();
		osGraphics.setColor(getBackground());
		osGraphics.fillRect(0, 0, imageWidth, imageHeight);// erase everything
		drawGrid();
	}

	/**
	 *draws the grid with labels; draw robot at 0,0
	 */
	public void drawGrid()
	{
		if(offScreenImage == null)makeImage();
		int xmin = -240;
		int xmax = 240;
		int xSpacing = 30;
		int ymax = 240;
		int ySpacing = 30;
		osGraphics.setColor(Color.green); // Set the line color
		for (int y = 0; y <= ymax; y += ySpacing)
		{
			osGraphics.drawLine(xpixel(xmin), ypixel(y), xpixel(xmax), ypixel(y));//horizontal lines
		}
		for (int x = xmin; x <= xmax; x += xSpacing)
		{
			osGraphics.drawLine(xpixel(x), ypixel(0), xpixel(x), ypixel(ymax));// vertical lines
		}
		osGraphics.setColor(Color.black); //set number color 	
		for (int y = 0; y <= ymax; y += ySpacing) // number the  y axis
		{
			osGraphics.drawString(y + "", xpixel(-0.5f), ypixel(y));
		}
		for (int x = 0; x <= xmax; x +=  xSpacing) // number the x axis
		{
			osGraphics.drawString(x + "", xpixel(x), ypixel(-0.5f));
		}
		drawRobotPath(0, 0, 0);

	}

	/**
	 *clear the screen and draw a new grid
	 */
	public void clear()
	{
		System.out.println(" clear called ");
		osGraphics.setColor(getBackground());
		osGraphics.fillRect(0, 0, imageWidth, imageHeight);// clear the image
		drawGrid();
		repaint();
	}

	/**
	 *Obstacles shown as magenta dot
	 */
	public void drawObstacle(int x, int y)
	{
		x = xpixel(x); // coordinates of intersection
		y = ypixel(y);
		block = true;
		osGraphics.setColor(Color.magenta);
		osGraphics.fillOval(x - 3, y - 3, 6, 6);//bounding rectangle is 10 x 10
		repaint();
	}

	public void drawDest(int x, int y)
	{
		x = xpixel(x); // coordinates of intersection
		y = ypixel(y);
		osGraphics.setColor(Color.blue);
		osGraphics.fillOval(x - 3, y - 3, 6, 6);//bounding rectangle is 10 x 10
		repaint();
	}

	/**
	 *blue line connects current robot position to last position if adjacent to current position
	 */
	public void drawRobotPath(int xx, int yy, int heading) {
		
		int x = xpixel(xx); // coordinates of intersection
		int y = ypixel(yy);
		osGraphics.setColor(Color.blue);
		drawPose(x, y, heading, Color.BLUE);
		osGraphics.drawLine(robotPrevX, robotPrevY, x, y);
		robotPrevX = x;
		robotPrevY = y;
		repaint();
	}
	

	/**
	 * clear the old robot position, arg pixels
	 */
	private void clearSpot(int x, int y, Color c)
	{
		System.out.println("clear spot ");
		if(osGraphics == null)System.out.println("null osGraphics");
		osGraphics.setColor(Color.white);
		osGraphics.fillOval(x - 3, y - 3, 6, 6);
		osGraphics.setColor(c);
		/*
		osGraphics.drawLine(x - 5, y, x + 5, y);
		osGraphics.drawLine(x, y - 5, x, y + 5);
		*/
	}


	private void drawPose(int x, int y, int heading, Color c) {
		osGraphics.setColor(Color.WHITE);
		osGraphics.fillPolygon(poseTriangle);

		poseTriangle = new Polygon();

		int newX;
		int newY;
		int radius;

		for (int i = 0; i < 3; i++) {

			if (i == 0) {
				radius = 10;
			} else {
				radius = 6;
			}
			newX = x + (int) (radius * Math.cos(Math.toRadians(heading + (120 * i))));
			newY = y - (int) (radius * Math.sin(Math.toRadians(heading + (120 * i))));


			poseTriangle.addPoint(newX, newY);
			System.out.println("Point " + i + ": (" + newX + "," + newY + ")");
		}

		osGraphics.setColor(c);
		osGraphics.fillPolygon(poseTriangle);
	}

	public int abs(int a)
	{
		return (a < 0 ? (-a) : (a));
	}

	/**
	 *convert grid coordinates to pixels
	 */
	private int xpixel(float x)
	{
		return xOrigin + (int) (x * gridSpacing);
	}

	private int gridX(int xpix)
	{
		float x = (xpix - xOrigin)/(1.0f*gridSpacing);
		return Math.round(x);
	}
	private int ypixel(float y)
	{
		return yOrigin - (int) (y * gridSpacing);
	}
	private int gridY(int ypix)
	{
		float y = (yOrigin - ypix)/(1.0f*gridSpacing);
		return Math.round(y);
	}
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				formMouseClicked(evt);
			}
		});
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);

	}// </editor-fold>//GEN-END:initComponents

	private void clearBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBActionPerformed

		clear();
	}//GEN-LAST:event_clearBActionPerformed

	/**
	 * Translates a click on the screen to a selection of destination in the text fields.
	 * 
	 * @param evt
	 */
	private void formMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseClicked
	{
		clearSpot(xpixel(destXo), ypixel(destYo), Color.green);

		destXo = gridX(evt.getX());
		destYo = gridY(evt.getY());

		textX.setText(destXo + "");
		textY.setText(destYo + "");
		drawDest(destXo, destYo);
	}//GEN-LAST:event_formMouseClicked

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton clearB;
	// End of variables declaration//GEN-END:variables
	/**
	 *The robot path is drawn and updated on this object. <br>
	 *created by makeImage which is called by paint(); guarantees image always exists before used; 
	 */
	Image offScreenImage;
	/**
	 *width of the dawing area;set by makeImage,used by clearImage
	 */
	int imageWidth;
	/**
	 *height of the dawing are; set by  makeImage,used by clearImage
	 */
	int imageHeight;
	/** 
	 *the graphics context of the image; set by makeImage, used by all methods that draw on the image
	 */
	private Graphics2D osGraphics;
	/**
	 * y origin in pixels
	 */
	public int yOrigin;
	/**
	 * line spacing in  pixels
	 */
	public final int gridSpacing = 2;
	/**
	 * origin in pixels from corner of drawing area
	 */
	public final int xOrigin = 600;
	/**
	 *robot position ; used by checkContinuity, drawRobotPath
	 */
	private int robotPrevX = xpixel(0);
	/**
	 * robot position; used by checkContinuity, drawRobotPath
	 */
	private int robotPrevY = ypixel(0);
	private int destXo = xpixel(0);
	private int destYo = ypixel(0);
	/**
	 * node status - true if blocked; set by drawObstacle, used by drawRobotPath
	 */
	private boolean block = false;
	private Polygon poseTriangle = new Polygon();
	public JTextField textX;
	public JTextField textY;
}
