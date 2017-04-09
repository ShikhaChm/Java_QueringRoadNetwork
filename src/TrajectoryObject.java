import java.sql.Timestamp;


public class TrajectoryObject extends MovingObject {
	private int trajectoryId;
	private TrajectoryObject previous;
	private TrajectoryObject next;

	public int getTrajectoryId() {
		return trajectoryId;
	}

	public void setTrajectoryId(int trajectoryId) {
		this.trajectoryId = trajectoryId;
	}

	public TrajectoryObject getPrevious() {
		return previous;
	}

	public void setPrevious(TrajectoryObject previous) {
		this.previous = previous;
	}

	public TrajectoryObject getNext() {
		return next;
	}

	public void setNext(TrajectoryObject next) {
		this.next = next;
	}

	public TrajectoryObject(MovingPoint movingPoint, int objectId,int trajectoryId) {
		super(movingPoint, objectId);
		this.trajectoryId=trajectoryId;
		this.previous=null;
		this.next=null;
	}

	public TrajectoryObject(Timestamp t, double x, double y, int objectId, int trajectoryId) {
		super(t, x, y, objectId);
		this.trajectoryId= trajectoryId;
		this.previous=null;
		this.next=null;
	}

}
