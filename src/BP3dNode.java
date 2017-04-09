import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.io.*;


public class BP3dNode extends TreeNode {
	private Range dataRange;
	private int splitAxes;
	private double splitValue;
	private BP3dNode left;
	private BP3dNode right;
	private List<TrajectoryObject> trajectoryObjects;
	
	public int getSplitAxes() {
		return splitAxes;
	}
	public double getSplitValue() {
		return splitValue;
	}
	public BP3dNode getLeft() {
		return left;
	}
	public BP3dNode getRight() {
		return right;
	}
	public List<TrajectoryObject> getTrajectoryObjects() {
		return trajectoryObjects;
	}
	public Range getDataRange() {
		return dataRange;
	}
	public void setDataRange(Range dataRange) {
		this.dataRange = dataRange;
	}
	public boolean isLeaf() {
		return !(this.trajectoryObjects==null);
	}
	public boolean intersectsWith(Range someRange) {
		return this.dataRange.intersectsWith(someRange);
	}
	@Override
	public int countInternalNodes() {
		int cnt=0;
		if(!isLeaf()) {
			cnt=1+this.left.countInternalNodes()+this.right.countInternalNodes();
		}
		return cnt;
	}
	@Override
	public int countLeafNodes() {
		int cnt=1;
		if(!isLeaf()) {
			cnt=this.left.countLeafNodes()+this.right.countLeafNodes();
		}
		return cnt;
	}
	@Override
	public Stack<Integer> getDepth(int depth,Stack<Integer> depthStack) {
		if(isLeaf()) depthStack.add(1+depth);
		else {
			this.left.getDepth(1+depth,depthStack);
			this.right.getDepth(1+depth,depthStack);
		}
		return depthStack;
	}
	@Override
	public Stack<Integer> leafNodeSizes(Stack<Integer> sizeStack) {
		if(isLeaf()) sizeStack.add(this.trajectoryObjects.size());
		else {
			this.left.leafNodeSizes(sizeStack);
			this.right.leafNodeSizes(sizeStack);
		}
		return sizeStack;
	}
	@Override
	public List<MovingObject> getMovingObjects() {
		if(isLeaf()) {
			return (ArrayList<MovingObject>)(ArrayList<? extends MovingObject>)trajectoryObjects;
		} else {
			List<MovingObject> movingObjects=new ArrayList<MovingObject>();
			if(left !=null) movingObjects.addAll(left.getMovingObjects());
			if(right !=null) movingObjects.addAll(right.getMovingObjects());
			return movingObjects;
		}
	}
	@Override
	public List<MovingObject> getMovingObjectsInRange(Range someRange) {
		List<MovingObject> intersectionList = new ArrayList<MovingObject>();
		if(isLeaf()) {
			Iterator<TrajectoryObject> it = trajectoryObjects.iterator();
//			if(intersectsWith(someRange)) {
				while(it.hasNext())
				{
					TrajectoryObject tPoint = (TrajectoryObject) it.next();
					if(someRange.contains(tPoint)) {
						intersectionList.add(tPoint);
					}
				}
//			}
			return intersectionList;
		} else {
			if((left !=null) && (left.getDataRange().intersectsWith(someRange))) intersectionList.addAll(left.getMovingObjectsInRange(someRange));
			if((right !=null) && (right.getDataRange().intersectsWith(someRange))) intersectionList.addAll(right.getMovingObjectsInRange(someRange));
		}
		return intersectionList;
	}
	@Override
	public void printNode(String prefix) {
		if(isLeaf()){
			System.out.println(prefix+"SplitAxes:"+this.splitAxes+"  SplitValue="+this.splitValue+"   Count="+this.trajectoryObjects.size());
		} else {
			System.out.println(prefix+"SplitAxes:"+this.splitAxes+"  SplitValue="+this.splitValue);
			this.left.printNode(" "+prefix);
			this.right.printNode(" "+prefix);
		}
	}
	@Override
	public void printNode(String prefix,Writer writer) {
		if(isLeaf()){
			try {
				writer.write(prefix+"SplitAxes:"+this.splitAxes+"  SplitValue="+this.splitValue+"   Count="+this.trajectoryObjects.size()+" Range:"+this.dataRange.toString()+ System.getProperty( "line.separator" ));
			} catch (IOException e) {
				System.out.println("Node File Write Error");
				e.printStackTrace();
			}
		} else {
			try {
				writer.write(prefix+"SplitAxes:"+this.splitAxes+"  SplitValue="+this.splitValue +" Range:"+this.dataRange.toString()+ System.getProperty( "line.separator" ));
			} catch (IOException e) {
				System.out.println("Node File Write Error");
				e.printStackTrace();
			}
			this.left.printNode(" "+prefix,writer);
			this.right.printNode(" "+prefix,writer);
		}
	}
	
	public BP3dNode(List<TrajectoryObject> trajectoryObjectList,int splitAxes) {
//		System.out.println("Called With : "+trajectoryObjectList.size()+"  splitAxes="+splitAxes); 
		this.splitAxes= Helpers.nextSplitAxes(splitAxes);
		if(trajectoryObjectList.size()==0) { 
			java.util.Date date= new Date();
			Timestamp ts=new Timestamp(date.getTime());
			Timestamp tsLow=Helpers.getDateFromTime(ts);
			Timestamp tsHigh=new Timestamp(tsLow.getTime());
			this.dataRange=new Range(tsLow,tsHigh,0.0,0.0,0.0,0.0);
			if(splitAxes==Constants.SPLITAXES1) this.splitValue=tsLow.getTime();
			else this.splitValue=0;
			this.left=null;
			this.right=null;
			this.trajectoryObjects=trajectoryObjectList;
			
		} else if (trajectoryObjectList.size()==1) {
			TrajectoryObject trajObj= trajectoryObjectList.get(0);
			Timestamp tsLow=new Timestamp(trajObj.getT().getTime());
			Timestamp tsHigh=new Timestamp(tsLow.getTime());
			this.dataRange=new Range(tsLow,tsHigh,trajObj.getX(),trajObj.getX(),trajObj.getY(),trajObj.getY());
			if(splitAxes==Constants.SPLITAXES1) this.splitValue=tsLow.getTime();
			else if(splitAxes==Constants.SPLITAXES2) this.splitValue=trajObj.getX();
			else if(splitAxes==Constants.SPLITAXES3) this.splitValue=trajObj.getY();
			this.left=null;
			this.right=null;
			this.trajectoryObjects=trajectoryObjectList;
		} else if (trajectoryObjectList.size()>1) {
			DataStats stats=new DataStats(trajectoryObjectList,this.splitAxes);
			this.dataRange=stats.getDataRange();
			if(this.dataRange.smallerThanThresh()) {
				this.splitValue=stats.getMedian(this.splitAxes); //non-consequential as it is leaf and will not be split further
				this.left=null;
				this.right=null;
				this.trajectoryObjects=trajectoryObjectList;
			} else {
				//non leaf node
//				System.out.println("Case 2 : split axes:"+this.splitAxes);
				if(this.dataRange.smallerThanThresh(this.splitAxes)){
//					System.out.println("Case 21");
					this.splitAxes=Helpers.nextSplitAxes(this.splitAxes);
					if(this.dataRange.smallerThanThresh(this.splitAxes)) {
//						System.out.println("Case 22");
						this.splitAxes=Helpers.nextSplitAxes(this.splitAxes);	
					}
					stats=new DataStats(trajectoryObjectList,this.splitAxes);
					this.dataRange=stats.getDataRange();
				}
//				System.out.println("Case 22 : split axes:"+this.splitAxes);
				
				this.splitValue=stats.getMedian(this.splitAxes);
				this.left=new BP3dNode(stats.getSplit1(),this.splitAxes);
				this.right=new BP3dNode(stats.getSplit2(),this.splitAxes);
				this.trajectoryObjects=null;
			}
		}
	}

}
