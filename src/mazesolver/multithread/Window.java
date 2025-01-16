package mazesolver.multithread;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Semaphore;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Window extends JFrame {

    protected Semaphore mutex;
    protected JLabel[][] labels;
    protected int[][] maze;
    protected int dimX, dimY;

    public Window(int[][] maze, int dimensionX, int dimensionY) throws HeadlessException {
        super();
        this.mutex = new Semaphore(1);
        this.maze = maze;
        this.dimX = dimensionX;
        this.dimY = dimensionY;
        this.labels = new JLabel[dimensionX][dimensionY];
        for (int i = 0; i < dimensionX; i++) {
            for (int j = 0; j < dimensionY; j++) {
                labels[i][j] = new JLabel();
            }
        }
        initComponents();
    }

    private void initComponents() {
        this.setLayout(null);
        int positionX, positionY;
        positionX = positionY = 0;
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                this.getContentPane().add(labels[i][j]);
                labels[i][j].setOpaque(true);
                labels[i][j].setBounds(positionX, positionY, 12, 12);
                positionX += 12;
            }
            positionY += 12;
            positionX = 0;
        }
        setLabels();
        this.setTitle("Maze Solver Multi-Thread by: Ognjen Vasic (5I-C)");
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(12 * dimY, 12 * dimX + 37); // due to the maze generation algorithm having the opposite logic with x and y, the same opposite logic must apply here
        this.setVisible(true);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int windowWidth = getWidth();
                int windowHeight = getHeight();
                int positionX = 0, positionY = 0;
                try {
                    mutex.acquire();
                } catch (InterruptedException ignored) {
                }
                int calculationX = 0, calculationY = 0;
                for (JLabel[] label : labels) {
                    for (JLabel jLabel : label) {
                        calculationX = windowWidth / dimY;
                        calculationY = windowHeight / dimX;
                        jLabel.setBounds(positionX, positionY, calculationX, calculationY); // same reverse logic
                        positionX += calculationX;
                    }
                    positionY += calculationY;
                    positionX = 0;
                }
                mutex.release();
            }
        });
        new Thread(() -> {
            while (this.isVisible()) {
                try {
                    setLabels();
                    Thread.sleep(75);
                } catch (InterruptedException ex) {
                }
            }
        }).start();
    }

    private void setLabels() {
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                try {
                    mutex.acquire();
                } catch (InterruptedException ignored) {
                }
                switch (maze[i][j]) {
                    case 10 -> {
                        labels[i][j].setBackground(Color.red);
                    }
                    case 11 -> {
                        labels[i][j].setBackground(Color.green);
                    }
                    case 2 -> {
                        labels[i][j].setBackground(Color.yellow);
                    }
                    case 3 -> {
                        labels[i][j].setBackground(Color.blue);
                    }
                    case 4 -> {
                        labels[i][j].setBackground(Color.cyan);
                    }
                    case 5 -> {
                        labels[i][j].setBackground(Color.magenta);
                    }
                    case 6 -> {
                        labels[i][j].setBackground(Color.darkGray);
                    }
                    case 7 -> {
                        labels[i][j].setBackground(Color.pink);
                    }
                    case 8 -> {
                        labels[i][j].setBackground(Color.lightGray);
                    }
                    case 9 -> {
                        labels[i][j].setBackground(Color.orange);
                    }
                    case 0 -> {
                        labels[i][j].setBackground(Color.white);
                    }
                    case 1 -> {
                        labels[i][j].setBackground(Color.black);
                    }
                } // 0 and 1 are reserved to draw the walls
                mutex.release();
            }
        }
    }
}
