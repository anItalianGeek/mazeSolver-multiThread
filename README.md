# MazeSolver MultiThread

> Second take on the same problem. The first
> ([maze-solver](https://github.com/anItalianGeek/maze-solver), C#, 2023) solved mazes
> single-threaded with backtracking. This one, written a year later right after the
> concurrent programming course, explores in parallel: each `PathFinder` extends `Thread`,
> shared state is guarded by a `Semaphore`, and the GUI shows the search as it happens.

MazeSolver MultiThread is an educational Java application developed following a high school concurrent programming course to explore parallel search strategies. Building upon an earlier single-threaded backtracking maze solver, this project implements concurrent exploration where multiple threads traverse branches simultaneously and visualize their progress in real time via Java Swing.

## Project Architecture

The application is structured around five primary classes:

1. **`MazeGenerator`**: Generates a randomized maze matrix using odd dimension constraints required for wall and corridor alignment.
2. **`PathFinder`**: Worker class extending `java.lang.Thread`. Each instance is assigned a unique display color, inspects adjacent open cells, paints visited positions, and spawns new `PathFinder` child threads when encountering multi-way intersections.
3. **`TreeNode`**: Hierarchical data structure tracking parent-child thread relationships and path histories to reconstruct the final route to the exit.
4. **`Window`**: Swing `JFrame` displaying an interactive grid of `JLabel` cells representing the maze walls, open corridors, and real-time thread positions.
5. **`testclass`**: The application entry point orchestrating continuous maze generation, thread execution, exit detection, and solution presentation.

## Execution Flow & Entry Point

The program entry point is implemented in [`src/mazesolver/multithread/testclass.java`](src/mazesolver/multithread/testclass.java). Rather than requiring interactive console input, it runs an automated visual loop:

1. **Maze Initialization**: Sets default dimensions to 69x69 (enforcing odd dimensions to ensure valid corridor generation) and initializes the shared integer grid from `MazeGenerator`.
2. **Search Launch**: Instantiates a shared binary `Semaphore(1)` mutex, creates the root `TreeNode`, launches the initial `PathFinder` thread at the maze entrance `(1, 0)`, and opens the real-time Swing visualization `Window`.
3. **Exit Monitoring**: The main thread polls the exit coordinate `(inputX - 2, inputY - 1)` under mutex protection at 500 ms intervals until a thread reaches the goal.
4. **Path Reconstruction & Display**: Once the exit is reached, `TreeNode.TraceNode` traces back the active nodes, filters distinct thread histories via a `HashMap`, renders the winning path in a separate window for 3.5 seconds, and then repeats the cycle with a newly generated maze.

## Concurrency & Architecture Notes

This project was built as a practical classroom exploration of concurrency primitives:

- **Thread-per-Branch Model**: Parallel exploration is achieved by directly subclassing `Thread` and invoking `.start()` for each alternative branch at a junction, without an executor pool. On wide or open mazes, this can lead to unbounded thread creation.
- **Synchronization Boundaries**: `PathFinder` workers share a binary `Semaphore(1)` (`mutex`) for access to the maze grid; the renderer uses a separate semaphore, as described below.
- **Swing Threading & Decoupled Mutex**: The `Window` class instantiates its own internal `Semaphore(1)` instead of sharing the mutex passed to `PathFinder`, and refreshes cell colors via a background loop without delegating updates through the Swing Event Dispatch Thread (EDT) via `SwingUtilities.invokeLater`.

## Requirements

- **Java Development Kit (JDK)**: Version 21 or higher (configured in `nbproject/project.properties` with `javac.source=21` and `javac.target=21`).
- **Build Tool / IDE**: Apache Ant or Apache NetBeans (JDK 21 compatible).

## Instructions for Running

### 1. Clone the Repository
```bash
git clone https://github.com/anItalianGeek/mazeSolver-multiThread.git
cd mazeSolver-multiThread
```

### 2. Run the Prebuilt JAR
If you have JDK 21+ installed, run the packaged distribution directly:
```bash
java -jar dist/mazeSolver-MultiThread.jar
```

### 3. Build and Run with Apache Ant
```bash
ant compile
ant run
```

### 4. Run in NetBeans IDE
1. Open Apache NetBeans (with a JDK 21 Java platform configured).
2. Select `File > Open Project` and open `mazeSolver-multiThread`.
3. Verify that the main class is set to `mazesolver.multithread.testclass`.
4. Click `Run Project` (or press `F6`) to start the visual simulation.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
