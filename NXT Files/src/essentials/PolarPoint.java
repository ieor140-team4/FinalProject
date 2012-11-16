package essentials;

public class PolarPoint {
	
	public float dist;
	public float angle;
	
	public PolarPoint(int d, int a) {
		dist = d;
		angle = a;
	}
	
	public String toString() {
		return "(" + dist + "," + angle + ")";
	}

}
