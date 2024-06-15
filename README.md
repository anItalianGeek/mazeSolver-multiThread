# MazeSolver MultiThread

MazeSolver MultiThread is a Java project designed to solve mazes using multithreading. This project leverages concurrency to explore paths within a maze, providing an efficient and visually engaging solution. Below is an overview of the project and instructions for running it.

## Project Description

MazeSolver MultiThread uses multiple threads to solve a randomly generated maze. The main components of the project include:

1. **MazeGenerator**: Class responsible for generating the maze.
2. **PathFinder**: Class representing the threads that explore paths in the maze.
3. **TreeNode**: Node representing a position in the maze and tracking the path.
4. **Window**: Class for graphical visualization of the maze and the solved path.

The maze-solving process follows these steps:

1. **Maze Generation**: The maze is generated using `MazeGenerator`.
2. **PathFinder Initialization**: A `PathFinder` thread is created to start exploring the maze.
3. **Graphical Visualization**: A GUI window displays the generated maze and, subsequently, the solved path.
4. **Path Tracking**: Nodes explored by the threads are tracked, and the path to the exit is determined and displayed.

## Test Class

The following test class demonstrates the functionality of the project:

```
package mazesolver.multithread;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class testclass {

    public static void main(String[] args) {
        int inputX, inputY;
        Scanner input = new Scanner(System.in);
        System.out.print("Input the size of the maze (X, Y) -> ");
        do {
            inputX = input.nextInt();
            inputY = input.nextInt();
            if (inputX % 2 == 0 || inputY % 2 == 0)
                System.out.print("Sorry, only even values will work! Try again -> ");
        } while (inputX % 2 == 0 || inputY % 2 == 0);

        // generating the shared objects
        MazeGenerator obj = new MazeGenerator(inputY, inputX);
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
        new Window(maze, inputX, inputY);
        finder.start();

        // wait for the threads to finish their job
        boolean exitFound = false;
        while (!exitFound) {
            try { mutex.acquire(); } catch (InterruptedException ex) {}
            exitFound = maze[inputX - 2][inputY - 1] != 0;
            mutex.release();
            try { Thread.sleep(500); } catch (InterruptedException ex) {}
        }

        // get the nodes that are necessary to build the path
        LinkedList<TreeNode> pathNodes = TreeNode.TraceNode(firstNode, new LinkedList<TreeNode>());

        // count how many duplicates are there and remove them
        HashMap<PathFinder, Integer> map = new HashMap<>();
        for (TreeNode pathNode : pathNodes)
            if (!map.containsKey(pathNode.thread))
                map.put(pathNode.thread, 1);

        // get the full path to the maze
        LinkedList<Location> fullPathToExit = getPath(map);

        // hide the maze window and create a new one that will only print the way to the exit
        int color = (int)(Math.random() * 10 + 2);
        for (Location location : fullPathToExit)
            newMaze[location.x][location.y] = color;
        new Window(newMaze, inputX, inputY);
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
```

## Instructions for Running

It is recommended to run the project using NetBeans for easier setup and execution. Follow these steps:

1. **Clone the Repository**:
    ```
    git clone https://github.com/your-username/mazeSolver-multiThread.git
    ```

2. **Open the Project in NetBeans**:
    - Open NetBeans.
    - Select `File` > `Open Project`.
    - Navigate to the cloned folder and select `mazeSolver-multiThread`.

3. **Build and Run**:
    - Build the project using `Build Project`.
    - Run the project using `Run Project`.

4. **Interacting with the Application**:
    - Enter the maze dimensions when prompted.
    - Observe the maze-solving process in real-time.

Happy maze-solving!
