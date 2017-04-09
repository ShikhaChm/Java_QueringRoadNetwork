import java.sql.Timestamp;


public class MovingPoint {
	private double x;
	private double y;
	private Timestamp t;
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public Timestamp getT() {
		return t;
	}
	public void setT(Timestamp t) {
		this.t = t;
	}
	public String toString() {
		return ("("+this.t+","+this.x+","+this.y+")");
	}
	public MovingPoint(Timestamp t,double x,double y) {
		this.x= x;
		this.y= y;
		this.t= t;
	}
	public MovingPoint(MovingPoint mPoint) {
		this.t= mPoint.getT();
		this.x= mPoint.getX();
		this.y= mPoint.getY();
	}

}
