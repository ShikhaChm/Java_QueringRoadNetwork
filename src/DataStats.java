import java.sql.Timestamp;
import java.util.*;


public class DataStats {
	private Range dataRange;
	private int splitAxes;
	private double medianT,medianX,medianY;
	private List<TrajectoryObject> split1;
	private List<TrajectoryObject> split2;

	public List<TrajectoryObject> getSplit1() {
		return split1;
	}

	public List<TrajectoryObject> getSplit2() {
		return split2;
	}
	
	public int getSplitAxes() {
		return splitAxes;
	}

	public Range getDataRange() {
		return dataRange;
	}
	
	public double getMedianT() {
		return medianT;
	}
	
	public double getMedianX() {
		return medianX;
	}
	
	public double getMedianY() {
		return medianY;
	}
	public double getMedian(int splitAxes) {
		switch(splitAxes) {
		case Constants.SPLITAXES1 :
			return getMedianT();
		case Constants.SPLITAXES2 :
			return getMedianX();
		case Constants.SPLITAXES3 :
			return getMedianY();
		}
		return getMedianT();
	}
	public DataStats(List<TrajectoryObject> trajectoryObjects,int splitAxes) {
//		System.out.println("DataStats: Called with : "+trajectoryObjects.size()+"  splitAxes="+splitAxes);
		long tmin,tmax;
		double xmin, xmax;
		double ymin, ymax;
		this.splitAxes=splitAxes%3;
		int len= trajectoryObjects.size();
		int mid = trajectoryObjects.size()/2;
		if(len==0) {
			java.util.Date date= new Date();
			Timestamp ts=new Timestamp(date.getTime());
			Timestamp tsLow=Helpers.getDateFromTime(ts);
			Timestamp tsHigh=new Timestamp(tsLow.getTime());
			this.dataRange=new Range(tsLow,tsHigh,0.0,0.0,0.0,0.0);
		} else if (len>=1) {
			Collections.sort(trajectoryObjects, new Comparator<TrajectoryObject>(){ public int compare(TrajectoryObject o1, TrajectoryObject o2){ if(o1.getT() == o2.getT()) return 0; return o1.getT().getTime() < o2.getT().getTime() ? -1 : 1; } });
			tmin=trajectoryObjects.get(0).getT().getTime(); 
			tmax=trajectoryObjects.get(trajectoryObjects.size()-1).getT().getTime();
			if(splitAxes==Constants.SPLITAXES1) {
//				System.out.println("Length="+len+"  Mid="+mid);
				split1= trajectoryObjects.subList(0, mid);
//				System.out.println("Split 1 Size="+split1.size());
				split2= trajectoryObjects.subList(mid, len);
//				System.out.println("Split 2 Size="+split2.size());
			}
			medianT=trajectoryObjects.get(mid).getT().getTime();
			Collections.sort(trajectoryObjects, new Comparator<TrajectoryObject>(){ public int compare(TrajectoryObject o1, TrajectoryObject o2){ if(o1.getX() == o2.getX()) return 0; return o1.getX() < o2.getX() ? -1 : 1; } });
			xmin=trajectoryObjects.get(0).getX(); 
			xmax=trajectoryObjects.get(trajectoryObjects.size()-1).getX();
			if(splitAxes==Constants.SPLITAXES2) {
//				System.out.println("Length="+len+"  Mid="+mid);
				split1= trajectoryObjects.subList(0, mid);
//				System.out.println("Split 1 Size="+split1.size());
				split2= trajectoryObjects.subList(mid, len);
//				System.out.println("Split 2 Size="+split2.size());
			}
			medianX=trajectoryObjects.get(mid).getX();
			Collections.sort(trajectoryObjects, new Comparator<TrajectoryObject>(){ public int compare(TrajectoryObject o1, TrajectoryObject o2){ if(o1.getY() == o2.getY()) return 0; return o1.getY() < o2.getY() ? -1 : 1; } });
			ymin=trajectoryObjects.get(0).getY(); 
			ymax=trajectoryObjects.get(trajectoryObjects.size()-1).getY(); 
			medianY=trajectoryObjects.get(mid).getY();
			if(splitAxes==Constants.SPLITAXES3) {
//				System.out.println("Length="+len+"  Mid="+mid);
				split1= trajectoryObjects.subList(0, mid);
//				System.out.println("Split 1 Size="+split1.size());
				split2= trajectoryObjects.subList(mid, len);
//				System.out.println("Split 2 Size="+split2.size());
			}
			this.dataRange=new Range(new Timestamp(tmin-TimeZone.getDefault().getOffset(tmin)),new Timestamp(tmax-TimeZone.getDefault().getOffset(tmax)),xmin,xmax,ymin,ymax);
		}
	}


}
