import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class RTreeNode extends TreeNode {
	private Range dataRange;
	private Stack<RTreeNode> childNodeStack;
	private TrajectoryObject leaf;
	
	public Range getDataRange() {
		return dataRange;
	}
	public void setDataRange(Range dataRange) {
		this.dataRange = dataRange;
	}
	public Stack<RTreeNode> getChildNodeStack() {
		return childNodeStack;
	}
	public void setChildNodeStack(Stack<RTreeNode> childNodeStack) {
		this.childNodeStack = childNodeStack;
	}
	public TrajectoryObject getLeaf() {
		return leaf;
	}
	public void setLeaf(TrajectoryObject leaf) {
		this.leaf = leaf;
	}
	
	public RTreeNode() {
		java.util.Date date= new Date();
		Timestamp ts=new Timestamp(date.getTime());
		Timestamp tsLow=Helpers.getDateFromTime(ts);
		Timestamp tsHigh=new Timestamp(tsLow.getTime());
		this.dataRange=new Range(tsLow,tsHigh,0.0,0.0,0.0,0.0);
		childNodeStack = new Stack<RTreeNode>();
		leaf=null;
	}
	public RTreeNode(TrajectoryObject tObject) {
		this.dataRange=new Range(new MovingPoint(tObject), new MovingPoint(tObject));
		childNodeStack = new Stack<RTreeNode>();
		leaf=tObject;
	}
	public RTreeNode(Range someRange,Stack<RTreeNode> children) {
		this.dataRange= someRange;
		this.childNodeStack = children;
		this.leaf=null;
	}
	
	public RTreeNode getMinimumBoundingNode(RTreeNode someNode) {
		Stack<RTreeNode> children = new Stack<RTreeNode>();
		children.add(this);
		children.add(someNode);
		RTreeNode boundingNode= new RTreeNode(this.dataRange.unionRange(someNode.getDataRange()), children);
		return boundingNode;
	}
	
	public RTreeNode addPoint(TrajectoryObject tObject) {
		RTreeNode returnNode=null;
		if(isLeaf()) {
			// This is a leaf node
			returnNode= new RTreeNode(tObject); //create a new leaf node and add to parent
			//System.out.println("Created New Leaf");
		} else {
			//This is an internal node. We will choose a child and insert there :
			//Step1 : If there is a node whose directory rectangle contains the mbb to be inserted, then search the subtree
			//Step2 : Else choose a node such that the enlargement of its directory rectangle is minimal, then search the subtree
			//Step3 : If more than one node satisfy this, choose the one with smallest area
			
			double minEnlargement= this.dataRange.getEnlargment(childNodeStack.get(0).getDataRange()); //Setting to first;
			double minSize = childNodeStack.get(0).getDataRange().getSize();//setting to first;
			int minIndex=0;
			for(int i=1;i<childNodeStack.size();i++) {
				double thisEnlargement=this.dataRange.getEnlargment(childNodeStack.get(i).getDataRange());
				double thisSize = childNodeStack.get(i).getDataRange().getSize();
				if(thisEnlargement < minEnlargement) {
					minEnlargement= thisEnlargement;
					minSize = thisSize;
					minIndex=i;
				} else if(thisEnlargement == minEnlargement) {
					//Step3 : If more than one node satisfy this, choose the one with smallest area
					if(thisSize < minSize) {
						minEnlargement= thisEnlargement;
						minSize = thisSize;
						minIndex=i;
					}
				}
			}
			returnNode=childNodeStack.get(minIndex).addPoint(tObject);
			dataRange.expandRange(tObject);
			//System.out.println("Inserted In Internal Node");
			if(returnNode!=null) {
				//System.out.println("Child Node resulted in split");
				childNodeStack.add(returnNode);
				returnNode=null;
				if(childNodeStack.size() >Constants.RTreeMaxNodeSize) {
					//This is where split the childNode into two and propagate the new split node through returnNode;
					//Step 1 : Pick two “seed” entries e1 and e2 far from each other, that is to maximize size(mbb(e1,e2)) –size(e1) –size(e2) 
					//Here mbb(e1,e2) is the mbb containing both e1 and e2 , complexity O((M+1)^2 )
					//Step 2: Insert the remaining (M-1) entries into the two groups such that sum of the sizes of the two partitions remain minimum;
					//System.out.println("This Node resulted in split");
					RTreeNode seed1=null;
					RTreeNode seed2=null;
					double index1=0;
					double index2=0;
					double maxSeparation =0; // setting max to 0
					for(int i=0; i < (childNodeStack.size())-1;i++) {
						for(int j=i+1; j < childNodeStack.size();j++ ) {
							RTreeNode e1 = childNodeStack.get(i);
							RTreeNode e2 = childNodeStack.get(j);
							double separation = (e1.getDataRange().unionRange(e2.getDataRange())).getSize() - e1.getDataRange().getSize()-e2.getDataRange().getSize();
							if((maxSeparation < separation) || (seed1==null)) {
								seed1=e1;
//								MovingPoint low=new MovingPoint(Helpers.parseTime("2007-05-28 13:36:47.846"),1308.0,1308.0);
//								MovingPoint high=new MovingPoint(Helpers.parseTime("2007-05-28 13:36:49.846"),1308.0,1310.2);
//								Range testRange= new Range(low,high);
//								if(testRange.smallerThanThresh()) System.out.println("Smaller Than Thresh");
			seed2=e2;
								index1=i;
								index2=j;
							}
						}
					}
					Stack<RTreeNode> partition1 = new Stack<RTreeNode>();
					Stack<RTreeNode> partition2 = new Stack<RTreeNode>();
					partition1.add(seed1);
					partition2.add(seed2);
					Range range1 = seed1.getDataRange();
					Range range2= seed2.getDataRange();
					for(int i=0;i < childNodeStack.size(); i++) {
						if((i!=index1) && (i!=index2)) {
							RTreeNode thisNode = childNodeStack.get(i);
							double size1 = range1.unionRange(thisNode.getDataRange()).getSize() + range2.getSize();
							double size2 = range1.getSize() + range2.unionRange(thisNode.getDataRange()).getSize();
							if(size1<size2) {
								partition1.add(thisNode);
								range1.expandRange(thisNode.getDataRange());
							} else {
								partition2.add(thisNode);
								range2.expandRange(thisNode.getDataRange());
							}
						}
					}
					//split done successfully
					returnNode = new RTreeNode(range2, partition2);
					this.dataRange= range1;
					this.childNodeStack = partition1;
					this.leaf=null;
				}
			}
		}
		
		return returnNode;
	}
	
	public boolean isLeaf() {
		return !(leaf==null);
	}
	@Override
	public List<MovingObject> getMovingObjects() {
		List<MovingObject> retList = new ArrayList<MovingObject>();
		if(isLeaf()) {
			retList.add(leaf);
		} else {
			for(int i=0; i < childNodeStack.size();i++) {
				retList.addAll(childNodeStack.get(i).getMovingObjects());
			}
		}
		return retList;
	}

	@Override
	public List<MovingObject> getMovingObjectsInRange(Range range) {
		List<MovingObject> retList = new ArrayList<MovingObject>();
		if(isLeaf()) {
			if(range.contains(leaf)) retList.add(leaf);
		} else {
			for(int i=0; i < childNodeStack.size();i++) {
				RTreeNode child=childNodeStack.get(i);
				if(child.getDataRange().intersectsWith(range)) retList.addAll(child.getMovingObjectsInRange(range));
			}
		}
		return retList;
	}
	@Override
	public boolean intersectsWith(Range range) {
		return this.dataRange.intersectsWith(range);
	}

	@Override
	public void printNode(String prefix) {
		if(isLeaf()){
			System.out.println(prefix+"LeafNode"+leaf.toString());
		} else {
			System.out.println(prefix+"InternalNode Range:"+this.dataRange.toString());
			for(int i=0; i< childNodeStack.size();i++) {
				childNodeStack.get(i).printNode(" "+prefix);
			}
		}
	}

	@Override
	public void printNode(String prefix, Writer writer) {
		if(isLeaf()){
			try {
				writer.write(prefix+"LeafNode"+leaf.toString()+ System.getProperty( "line.separator" ));
			} catch (IOException e) {
				System.out.println("Node File Write Error");
				e.printStackTrace();
			}
		} else {
			try {
				writer.write(prefix+"InternalNode Range:"+this.dataRange.toString()+ System.getProperty( "line.separator" ));
			} catch (IOException e) {
				System.out.println("Node File Write Error");
				e.printStackTrace();
			}
			for(int i=0; i< childNodeStack.size();i++) {
				childNodeStack.get(i).printNode(" "+prefix,writer);
			}
		}

	}

	@Override
	public int countLeafNodes() {
		int cnt=1;
		if(!isLeaf()) {
			cnt=0;
			for(int i=0;i<childNodeStack.size();i++) cnt= cnt+ childNodeStack.get(i).countLeafNodes();
		}
		return cnt;
	}

	@Override
	public int countInternalNodes() {
		int cnt=0;
		if(!isLeaf()) {
			cnt=1;
			for(int i=0;i<childNodeStack.size();i++) cnt= cnt+ childNodeStack.get(i).countInternalNodes();
		}
		return cnt;
	}

	@Override
	public Stack<Integer> getDepth(int depth, Stack<Integer> depthStack) {
		if(isLeaf()) depthStack.add(1+depth);
		else {
			for(int i=0; i < childNodeStack.size();i++) {
				childNodeStack.get(i).getDepth(1+depth,depthStack);
			}
		}
		return depthStack;
	}

	@Override
	public Stack<Integer> leafNodeSizes(Stack<Integer> sizeStack) {
		if(isLeaf()) sizeStack.add(1);
		else {
			for(int i=0; i < childNodeStack.size();i++) {
				childNodeStack.get(i).leafNodeSizes(sizeStack);
			}
		}
		return sizeStack;
	}

}
