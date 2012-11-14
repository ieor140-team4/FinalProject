package gui;


/**
 * interface for generic GridNavigationController
 * @author glassey
 *
 */
public interface GNC
{
	public void setMessage(String s);
	public void drawRobotPath(int x, int y, int heading);
	public void drawObstacle(int x,int y);
	
}
