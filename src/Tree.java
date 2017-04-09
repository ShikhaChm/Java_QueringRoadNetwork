
public abstract class Tree {
	protected TreeNode root;
	public TreeNode getRoot() {
		return root;
	}
	public void setRoot(TreeNode root) {
		this.root = root;
	}
	public abstract void insertPoint(double t, double x , double y) ;
	public abstract TreeNode[] rangeQuery(Range range) ;
	public abstract void createTreeFromDataFile(String fileName);
}
