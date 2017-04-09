import java.sql.Timestamp;

public class MovingObject extends MovingPoint {
	private int objectId;

	public int getObjectId() {
		return objectId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}


	public MovingObject(MovingPoint movingPoint,int objectId) {
		super(movingPoint.getT(), movingPoint.getX(), movingPoint.getY());
		this.objectId= objectId;
	}

	public MovingObject(Timestamp t,double x,double y,int objectId) {
		super(t, x, y);
		this.objectId= objectId;
	}

}
