import java.io.*;
import java.util.*;

public class BP3DTree extends Tree {

	public void printTree(){
		root.printNode("");
	}
	public void printTree(String filename){
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
		    root.printNode("",writer);
		} catch (IOException ex) {
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	public void printTreeStats() {
		System.out.println("Num Internal Nodes="+root.countInternalNodes());
		System.out.println("Num Leaf Nodes="+root.countLeafNodes());
		Stack<Integer> sizeStack= root.leafNodeSizes(new Stack<Integer>());
		Stack<Integer> depthStack= root.getDepth(0,(new Stack<Integer>()));
		double sum=0;
		double sumDepth=0;
		int min=sizeStack.get(0); 
		int max=sizeStack.get(0);
		int minDepth=depthStack.get(0);
		int maxDepth=depthStack.get(0);
		System.out.println("sizeStack.size()="+sizeStack.size()+" depthStack.size()="+depthStack.size());
		for(int i=0;i<sizeStack.size();i++) {
			int size=sizeStack.get(i).intValue();
			int depth=depthStack.get(i).intValue();
			sum=sum+size;		
			sumDepth=sumDepth+depth;
			min=Math.min(min, size);
			max=Math.max(max, size);
			minDepth=Math.min(minDepth, depth);
			maxDepth=Math.max(maxDepth, depth);
		}
		System.out.println("Leaf Avg count ="+(sum/sizeStack.size())+" min="+min+" max="+max);
		System.out.println("Leaf Avg depth ="+(sumDepth/depthStack.size())+" minDepth="+minDepth+" maxDepth="+maxDepth);
	}
	public BP3DTree(List<TrajectoryObject> trajectoryObjectList) {
		long startTime = System.nanoTime();
		root = new BP3dNode(trajectoryObjectList,Constants.SPLITAXES3);
		long endTime = System.nanoTime();
		System.out.println("BP3DTree Creation Time: "+(endTime-startTime)/1000);
	}
	public List<MovingObject> getObjectsInRange(Range someRange) {
		List<MovingObject> retList= this.root.getMovingObjectsInRange(someRange);
		return retList;
	}
	@Override
	public void insertPoint(double t, double x, double y) {
		// TODO Auto-generated method stub

	}

	@Override
	public TreeNode[] rangeQuery(Range range) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void createTreeFromDataFile(String fileName) {
		
    }

}
