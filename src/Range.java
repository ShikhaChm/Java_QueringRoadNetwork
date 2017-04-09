import java.sql.Timestamp;


public class Range {
	private MovingPoint low;
	private MovingPoint high;
	
	public MovingPoint getLow() {
		return low;
	}

	public void setLow(MovingPoint low) {
		this.low = low;
	}

	public MovingPoint getHigh() {
		return high;
	}

	public void setHigh(MovingPoint high) {
		this.high = high;
	}

	public String toString() {
		return ("["+this.low.toString()+"<->"+this.high.toString()+"]=["+(this.high.getT().getTime()-this.low.getT().getTime())+","+(this.high.getX()-this.low.getX())+","+(this.high.getY()-this.low.getY())+"]");
	}
	public double getSize() {
		double tspan= high.getT().getTime()-low.getT().getTime();
		double xspan= high.getX()-low.getX();
		double yspan= high.getY()-low.getY();
		return Math.sqrt((tspan*tspan)+(xspan*xspan)+(yspan*yspan));
	}
	public double getEnlargment(Range someRange) {
		double enlargement=0;
		if(!contains(someRange)) {
			enlargement = unionRange(someRange).getSize()-getSize();;
		}
		return enlargement;
	}
	public double getEnlargment(MovingPoint mPoint) {
		double enlargement=0;
		if(!contains(mPoint)) {
			enlargement = unionRange(mPoint).getSize()-getSize();;
		}
		return enlargement;
	}
	public void expandRange(MovingPoint mPoint) {
		if(mPoint.getT().getTime()<low.getT().getTime()) low.setT(mPoint.getT());
		if(mPoint.getT().getTime()>high.getT().getTime()) high.setT(mPoint.getT());
		low.setX(Math.min(low.getX(), mPoint.getX()));
		high.setX(Math.max(high.getX(), mPoint.getX()));
		low.setY(Math.min(low.getY(), mPoint.getY()));
		high.setY(Math.max(high.getY(), mPoint.getY()));
	}
	public void expandRange(Range someRange) {
		this.expandRange(someRange.getLow());
		this.expandRange(someRange.getHigh());
	}
	public Range unionRange(Range someRange) {
		Timestamp tmin=low.getT();
		Timestamp tmax=high.getT();
		if(low.getT().getTime()> someRange.getLow().getT().getTime()) tmin=someRange.getLow().getT();
		if(high.getT().getTime()< someRange.getHigh().getT().getTime()) tmax=someRange.getHigh().getT();
		double xmin = Math.min(low.getX(), someRange.getLow().getX());
		double xmax = Math.max(high.getX(), someRange.getHigh().getX());
		double ymin = Math.min(low.getY(), someRange.getLow().getY());
		double ymax = Math.max(high.getY(), someRange.getHigh().getY());
		Range uRange= new Range(new MovingPoint(tmin,xmin,ymin), new MovingPoint(tmax,xmax,ymax));
		return uRange;
	}
	public Range unionRange(MovingPoint mPoint) {
		return unionRange(new Range(mPoint,mPoint));
	}
	public MovingPoint[] getBoundingPoints() {
		//000,001,010,011,100,101,110,111
		MovingPoint[] ret= new MovingPoint[8];
		ret[0]=low;
		ret[1]=new MovingPoint(low.getT(),low.getX(),high.getY());
		ret[2]=new MovingPoint(low.getT(),high.getX(),low.getY());
		ret[3]=new MovingPoint(low.getT(),high.getX(),high.getY());
		ret[4]=new MovingPoint(high.getT(),low.getX(),low.getY());
		ret[5]=new MovingPoint(high.getT(),low.getX(),high.getY());
		ret[6]=new MovingPoint(high.getT(),high.getX(),low.getY());
		ret[7]=high;
		return ret;
	}
	public boolean contains(MovingPoint somePoint) {
		return (somePoint.getT().after(low.getT()) && somePoint.getT().before(high.getT())) && (somePoint.getX()>low.getX()&&somePoint.getX()<high.getX()) && (somePoint.getY()>low.getY()&&somePoint.getY()<high.getY());
	}
	public boolean contains(Range someRange) {
		return (contains(someRange.getLow()) && contains(someRange.getHigh()));
	}	
	public boolean smallerThanThresh(int splitAxes) {
		boolean small=false;
//		System.out.println("Call splitAxes=" + splitAxes + "small="+small);
		if(splitAxes==Constants.SPLITAXES1) {
			small = (Math.abs(this.high.getT().getTime()-this.low.getT().getTime()) < Constants.TMIN);
//			System.out.println("splitAxes=" + splitAxes + "small="+small);
		} else if(splitAxes==Constants.SPLITAXES2) {
			small =( Math.abs(this.high.getX()-this.low.getX()) < Constants.XMIN);
//			System.out.println("splitAxes=" + splitAxes + "small="+small);
		} else if(splitAxes==Constants.SPLITAXES3) {
			small = (Math.abs(this.high.getY()-this.low.getY()) < Constants.YMIN);
//			System.out.println("splitAxes=" + splitAxes + "small="+small);
		}
		return small;
	}
	public boolean smallerThanThresh() {
		return smallerThanThresh(Constants.SPLITAXES1) && smallerThanThresh(Constants.SPLITAXES2) && smallerThanThresh(Constants.SPLITAXES3);
	}
	public boolean intersectsWith(Range someRange) {
		MovingPoint[] boundingPoints=someRange.getBoundingPoints();
		boolean intersects=false;
		for(int i=0;i<8;i++) {
			intersects=intersects || contains(boundingPoints[i]);
		}
		return intersects;
	}

	public Range getRandomSubrange(double tpc,double xpc,double ypc) {
		double tspan= tpc*(high.getT().getTime()-low.getT().getTime())/100;
		double xspan= xpc*(high.getX()-low.getX())/100;
		double yspan= ypc*(high.getY()-low.getY())/100;
		long tlow = low.getT().getTime()+(long)(Math.random()*(high.getT().getTime()-low.getT().getTime() -((long)tspan)));
		long thigh= tlow+((long)tspan);
		double xlow=low.getX()+(Math.random()*(high.getX()-low.getX() -xspan));
		double xhigh=xlow+xspan;
		double ylow=low.getX()+(Math.random()*(high.getY()-low.getY() -yspan));
		double yhigh=ylow+yspan;
		return new Range(new Timestamp(tlow), new Timestamp(thigh), xlow, xhigh, ylow, yhigh);
	}
	public Range(Timestamp tmin, Timestamp tmax, double xmin, double xmax,double ymin, double ymax) {
		this.low=new MovingPoint(tmin,xmin,ymin);
		this.high=new MovingPoint(tmax, xmax, ymax);
	}
	
	public Range(MovingPoint low,MovingPoint high) {
		this.low=low;
		this.high=high;
	}
}
