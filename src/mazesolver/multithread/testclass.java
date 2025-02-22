package mazesolver.multithread;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class testclass {

    public static void main(String[] args) {
        /*
        IMPORTANT: MAKE SURE TO USE ONLY ODD VALUES OR ELSE THE ALGORITHM WILL NOT WORK CORRECTLY
         */
        while (true) {
            int inputX = 69, inputY = 69;
            if (inputX % 2 == 0)
                inputX++;
            if (inputY % 2 == 0)
                inputY++;
            System.out.println("Generating maze with X-Dimension: " + inputX + " Y-Dimension: " + inputY);

            // generating the shared objects
            MazeGenerator obj = new MazeGenerator(inputY, inputX); // the maze generation has the opposite logic with x and y! 
            TreeNode firstNode = new TreeNode();
            Semaphore mutex = new Semaphore(1);
            int[][] maze = new int[inputX][inputY];
            int[][] newMaze = new int[inputX][inputY];

            // fill the mazes
            int[][] assistant = obj.getMaze();
            for (int i = 0; i < inputX; i++) {
                for (int j = 0; j < inputY; j++) {
                    maze[i][j] = assistant[i][j];
                    newMaze[i][j] = assistant[i][j];
                }
            }

            // generating the thread and the GUI
            PathFinder finder = new PathFinder(new Location(1, 0), maze, firstNode, mutex);
            Window window = new Window(maze, inputX, inputY);
            finder.start();

            // wait for the threads to finish their job
            boolean exitFound = false;
            while (!exitFound) {
                try {
                    mutex.acquire();
                } catch (InterruptedException ex) {
                }
                exitFound = maze[inputX - 2][inputY - 1] != 0;
                mutex.release();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
            }

            // get the nodes that are necessary to build the path
            LinkedList<TreeNode> pathNodes = TreeNode.TraceNode(firstNode, new LinkedList<TreeNode>());

            // count how many duplicates are there and remove them
            HashMap<PathFinder, Integer> map = new HashMap<>();
            for (TreeNode pathNode : pathNodes) {
                if (!map.containsKey(pathNode.thread)) {
                    map.put(pathNode.thread, 1);
                }
            }

            // get the full path to the maze
            LinkedList<Location> fullPathToExit = getPath(map);

            // hide the maze window and create a new one that will only print the way to the exit
            int color = (int) (Math.random() * 10 + 2);
            for (Location location : fullPathToExit) {
                newMaze[location.x][location.y] = color;
            }
            
            window.dispose();
            window = new Window(newMaze, inputX, inputY);
            
            try {
                Thread.sleep(3500);
                window.dispose();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static LinkedList<Location> getPath(HashMap<PathFinder, Integer> map) {
        LinkedList<Location> ret = new LinkedList<Location>();

        for (PathFinder pathFinder : map.keySet()) {
            for (Location location : pathFinder.getHistory()) {
                ret.add(location);
            }
        }

        return ret;
    }
}
