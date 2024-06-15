package mazesolver.multithread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MazeGenerator {
    protected final int width;
    protected final int height;
    protected final int[][] maze;
    protected final Random rand = new Random();

    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.maze = new int[height][width];
        generateMaze();
    }

    private void generateMaze() {
        // init the maze with walls 
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x] = 1;
            }
        }

        // create the maze using a backtracking algorithm
        int startX = rand.nextInt((width - 1) / 2) * 2 + 1;
        int startY = rand.nextInt((height - 1) / 2) * 2 + 1;
        maze[startY][startX] = 0;
        carvePassagesFrom(startX, startY);

        // close the sides
        for (int y = 0; y < height; y++) {
            maze[y][0] = 1;
            maze[y][width - 1] = 1;
        }
        for (int x = 0; x < width; x++) {
            maze[0][x] = 1;
            maze[height - 1][x] = 1;
        }

        // add an entrance and exit
        maze[1][0] = 0; // entrance
        maze[height - 2][width - 1] = 0; // exit

        // make sure that the exit isn't blocked
        maze[height - 2][width - 2] = 0;
        maze[height - 3][width - 2] = 0;
        maze[height - 2][width - 3] = 0;

        // fix walls in penultimate rows and columns if necessary
        if (width % 2 == 0) {
            for (int y = 1; y < height - 1; y++) {
                if (maze[y][width - 2] == 0) {
                    maze[y][width - 3] = 1;
                } else {
                    maze[y][width - 2] = 0;
                }
            }
        }
        if (height % 2 == 0) {
            for (int x = 1; x < width - 1; x++) {
                if (maze[height - 2][x] == 0) {
                    maze[height - 3][x] = 1;
                } else {
                    maze[height - 2][x] = 0;
                }
            }
        }
    }

    private void carvePassagesFrom(int cx, int cy) {
        int[] dx = {2, -2, 0, 0};
        int[] dy = {0, 0, 2, -2};
        ArrayList<Integer> directions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            directions.add(i);
        }
        Collections.shuffle(directions, rand);

        for (int i : directions) {
            int nx = cx + dx[i];
            int ny = cy + dy[i];
            if (nx > 0 && ny > 0 && nx < width - 1 && ny < height - 1 && maze[ny][nx] == 1) {
                maze[cy + dy[i] / 2][cx + dx[i] / 2] = 0;
                maze[ny][nx] = 0;
                carvePassagesFrom(nx, ny);
            }
        }
    }

    public void printMaze() { // print the maze to console
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(maze[y][x] == 1 ? "██" : "  ");
            }
            System.out.println();
        }
    }

    public int[][] getMaze() { // useful getter
        return maze;
    }
}
