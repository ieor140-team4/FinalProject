package essentials;

public class PolarPoint {
	
	public int dist;
	public int angle;
	
	public PolarPoint(int d, int a) {
		dist = d;
		angle = a;
	}
	
	public String toString() {
		return "(" + dist + "," + angle + ")";
	}

}
