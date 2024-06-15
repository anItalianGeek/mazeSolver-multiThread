package mazesolver.multithread;

import java.util.LinkedList;

class TreeNode {

    // public attributes 
    PathFinder thread; // the data of each node is a pointer to their respective thread
    TreeNode left, right, center; // pointers to the next node
    boolean hasExited;
    
    public TreeNode() {
        thread = null;
        left = right = center = null;
        hasExited = false;
    }
    
    public TreeNode(PathFinder thread) {
        this.thread = thread;
        left = right = center = null;
        hasExited = false;
    }
    
    // get the nodes that build the way to the exit
    static public LinkedList<TreeNode> TraceNode(TreeNode root, LinkedList<TreeNode> list){
        list.add(root);
        
        if (Finder(root.right)){
            list.get(list.size() - 1).center = null;
            list.get(list.size() - 1).left = null;
            TraceNode(root.right, list);
        }
        if (Finder(root.left)){
            list.get(list.size() - 1).center = null;
            list.get(list.size() - 1).right = null;
            TraceNode(root.left, list);
        }
        if (Finder(root.center)){
            list.get(list.size() - 1).right = null;
            list.get(list.size() - 1).left = null;
            TraceNode(root.center, list);
            
        }
        
        return list;
    }
    
    // find if a branch/node contains the thread that reached the exit
    static public boolean Finder(TreeNode root){
        if (root == null)
            return false;
        
        if (root.hasExited)
            return true;
        
        if (Finder(root.right))
            return true;
        if (Finder(root.left))
            return true;
        if (Finder(root.center))
            return true;
        
        return false;
    }
    
}