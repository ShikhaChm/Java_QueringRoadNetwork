import java.util.*;
import java.io.*;

public abstract class TreeNode {
	public abstract List<MovingObject> getMovingObjects();
	public abstract boolean intersectsWith(Range range);
	public abstract List<MovingObject> getMovingObjectsInRange(Range range);
	public abstract void printNode(String prefix) ;
	public abstract void printNode(String prefix,Writer writer) ;
	public abstract int countLeafNodes();
	public abstract int countInternalNodes();
	public abstract Stack<Integer> getDepth(int depth,Stack<Integer> depthStack);
	public abstract Stack<Integer> leafNodeSizes(Stack<Integer> sizeStack);
}
