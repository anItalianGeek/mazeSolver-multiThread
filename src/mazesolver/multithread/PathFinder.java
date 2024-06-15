package mazesolver.multithread;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class PathFinder extends Thread {
    
    protected Location position; // struct storing the current position of the thread in the maze
    protected TreeNode paths; // useful tree data structure - **the purpose will be explained later**
    protected int[][] maze; // pretty obvious
    protected LinkedList<Location> history;
    protected boolean right, left, up, down, isStuck, exitFound; // support booleans that indicate the available moves
    protected int lastMoveMade; // support integer storing the index of the last move that the thread has made (0 = right, 1 = down, 2 = left, 3 = right)
    protected int painting;
    protected Semaphore mutex;
    
    public PathFinder(Location startingPosition, int[][] maze, TreeNode paths, Semaphore mutex) {
        this.painting = (int)(Math.random() * 10 + 2);
        this.position = startingPosition;
        this.history = new LinkedList<>();
        this.paths = paths;
        this.maze = maze;
        this.isStuck = false;
        this.exitFound = false;
        this.lastMoveMade = -1; // as the only way to go in the very begging is right, i can set the variable to 0, so that the program will NOT go left for any reason
        if (this.paths.thread == null) // look at the detailed explanation of the tree structure to understand the logic of this
            this.paths.thread = this;
        this.mutex = mutex;
        up = down = right = left = true;
    }

    @Override
    public void run() {
        while (!isStuck){
            try { mutex.acquire(); } catch (InterruptedException ex) {}
            maze[position.x][position.y] = painting;
            mutex.release();
            try { Thread.sleep(100); } catch (InterruptedException ie) {}
            history.add(new Location(position.x, position.y));
            
            int movements = checkMovements();
            
            if (movements == 0){ // if i got no more available moves then stop the execution of the current thread, he did his job
                isStuck = true;
                try { mutex.acquire(); } catch (InterruptedException ex) {}
                maze[position.x][position.y] = painting; // anyways don't forget to color the last place as well!!
                mutex.release();
                continue;
            }
            
            if (movements >= 2){ // multiple ways management - whenever i have more options i run a new thread in order to check that direction
                switch (lastMoveMade) {
                    case 0, 2 -> {
                        if (movements == 2){
                            TreeNode centralNode = new TreeNode();
                            PathFinder newCentralThread = new PathFinder(new Location(position.x, position.y), maze, centralNode, mutex);
                            paths.center = centralNode;
                            if (up){
                                TreeNode newNode = new TreeNode();
                                PathFinder newThread = new PathFinder(new Location(position.x - 1, position.y), maze, newNode, mutex);
                                paths.left = newNode;
                                up = false; 
                                newThread.start();
                            }
                            if (down) {
                                TreeNode newNode = new TreeNode();
                                PathFinder newThread = new PathFinder(new Location(position.x + 1, position.y), maze, newNode, mutex);
                                paths.right = newNode;
                                down = false;
                                newThread.start();
                            }
                            
                            newCentralThread.start();
                            isStuck = true;
                        } else {
                            
                            TreeNode nodeUp = new TreeNode();
                            TreeNode nodeDown = new TreeNode();
                            
                            PathFinder thread_right = new PathFinder(new Location(position.x, position.y + 1), maze, nodeUp, mutex);
                            PathFinder thread_left = new PathFinder(new Location(position.x, position.y - 1), maze, nodeDown, mutex);
                            
                            paths.left = nodeUp;
                            paths.right = nodeDown;
                            
                            TreeNode nodeCenter = new TreeNode();
                            PathFinder thread_center = new PathFinder(new Location(position.x, position.y), maze, nodeCenter, mutex);
                            paths.center = nodeCenter;
                            thread_center.start();
                            isStuck = true;
                            
                            thread_left.start();
                            thread_right.start();
                            
                        }
                    }
                    case 1, 3 -> {
                        if (movements == 2){
                            TreeNode centralNode = new TreeNode();
                            PathFinder newCentralThread = new PathFinder(new Location(position.x, position.y), maze, centralNode, mutex);
                            paths.center = centralNode;
                            if (right){
                                TreeNode newNode = new TreeNode();
                                PathFinder newThread = new PathFinder(new Location(position.x, position.y + 1), maze, newNode, mutex);
                                paths.right = newNode;
                                right = false;
                                newThread.start();
                            }
                            if (left) {
                                TreeNode newNode = new TreeNode();
                                PathFinder newThread = new PathFinder(new Location(position.x, position.y - 1), maze, newNode, mutex);
                                paths.left = newNode;
                                left = false;
                                newThread.start();
                            }
                            
                            newCentralThread.start();
                            isStuck = true;
                        } else {
                            
                            TreeNode nodeUp = new TreeNode();
                            TreeNode nodeDown = new TreeNode();
                            
                            PathFinder thread_right = new PathFinder(new Location(position.x, position.y + 1), maze, nodeUp, mutex);
                            PathFinder thread_left = new PathFinder(new Location(position.x, position.y - 1), maze, nodeDown, mutex);
                            
                            paths.left = nodeUp;
                            paths.right = nodeDown;
                            
                            TreeNode nodeCenter = new TreeNode();
                            PathFinder thread_center = new PathFinder(new Location(position.x, position.y), maze, nodeCenter, mutex);
                            paths.center = nodeCenter;
                            thread_center.start();
                            isStuck = true;
                            
                            thread_left.start();
                            thread_right.start();
                            
                        }
                    }
                }
            }
            
            if (up){
                position.x--;
                lastMoveMade = 3;
            }
            if (down) {
                position.x++;
                lastMoveMade = 1;
            }
            if (left) {
                position.y--;
                lastMoveMade = 2;
            } 
            if (right) {
                position.y++;
                lastMoveMade = 0;
            }
        }
        
        try { // if the thread has reached the exit simply deny that it is stuck, as it has found the way out
            int value = maze[position.x][position.y + 1]; // this is supposed to throw an exception, the condition has no relevance, if no exception is thrown, the exit hasn't been found yet
        } catch (IndexOutOfBoundsException iobe) {
            exitFound = true;
            paths.hasExited = true;
        }
     
    }

    public boolean isStuck() { return isStuck; }
    public boolean hasExited() { return exitFound; }
    public LinkedList<Location> getHistory() { return history; } 
    
    private int checkMovements() { 
        int moves = 4;
        up = down = left = right = true;

        try {
            if (right && (lastMoveMade == 2 || maze[position.x][position.y + 1] != 0)){
                right = false;
                moves--;
            } 
        } catch (Exception e) {right = false; moves--;}
        try {
            if (left && (lastMoveMade == 0 || maze[position.x][position.y - 1] != 0)){
                left = false;
                moves--;
            }
        } catch (Exception e) {left = false; moves--;}
        try {
            if (up && (lastMoveMade == 1 || maze[position.x - 1][position.y] != 0)){
                up = false;
                moves--;
            }
        } catch (Exception e) {up = false; moves--;}
        try {
            if (down && (lastMoveMade == 3 || maze[position.x + 1][position.y] != 0)){
                down = false;
                moves--;
            }
        } catch (Exception e) {down = false; moves--;}
        return moves;
        
    }
    
}
