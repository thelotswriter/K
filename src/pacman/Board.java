package pacman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import miniMain.ColorCoordinate;
import miniMain.MiniMain;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener {

    private final int MODE = 1;

    private Dimension d;
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);

    private Image ii;
    private final Color dotColor = new Color(192, 192, 0);
    private Color mazeColor;

    private boolean inGame = false;
    private boolean dying = false;

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int PAC_ANIM_DELAY = 2;
    private final int PACMAN_ANIM_COUNT = 4;
    private final int MAX_GHOSTS = 12;
    private final int PACMAN_SPEED = 6;

    private int pacAnimCount = PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private int N_GHOSTS = 0;
    private int pacsLeft, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image ghost;
    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;

    private int[] pacStart = {0,0};
    private int[] ghostStart = {13,0};

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy, view_dx, view_dy;

    private short levelData[];
    private short freeboard[];

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 3;

    private int currentSpeed = 3;
    private short[] screenData;
    private ArrayList<Point> pellets; // Tracks the pellets left on the board
//    private final boolean[][] wallMap = {
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, true,  true,  true,  false, false, false, false, false, false, false, false, false, false, false},
//            {false, true,  true,  true,  false, false, false, false, false, false, false, false, false, false, false},
//            {false, true,  true,  true,  false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, true,  false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, true,  false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, true,  false, false, false, false, false, true,  false},
//            {true,  false, false, false, true,  true,  true,  true,  true,  true,  true,  false, false, true,  false},
//            {true,  false, false, false, false, false, false, true,  false, false, false, false, false, true,  false},
//            {true,  false, false, false, false, false, false, true,  false, false, false, false, false, true,  false},
//            {true,  false, false, false, false, false, false, true,  false, false, false, false, false, true,  false},
//            {true,  false, false, false, false, false, false, false, false, false, false, false, false, true,  false},
//            {true,  false, false, false, false, false, false, false, false, false, false, false, false, true,  false},
//            {true,  false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true, false, false, false, false, false}
//    };

//    private final boolean[][] wallMap = {
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false},
//            {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false}
//    };

    private final boolean[][] wallMap = {
            {false, false, false,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true},
            {true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true}
    };

    private int[] wallX;
    private int[] wallY;
    private Timer timer;
    private MiniMain miniMain;
    private Graphics graphics;
    private Graphics2D graph2d;
    private boolean drawTiles;
    private List<ColorCoordinate> tilesToDraw;

    public Board() {

        loadImages();
        initVariables();
        initBoard();
        drawTiles = false;
        tilesToDraw = null;
    }
    
    public Board(MiniMain miniMain) {

    	this.miniMain = miniMain;
        loadImages();
        initVariables();
        initBoard();
    }
    
    private void initBoard() {
        
        addKeyListener(new TAdapter());

        setFocusable(true);

        setBackground(Color.black);
        setDoubleBuffered(true);        
    }

    private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        mazeColor = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
        
        pellets = new ArrayList<>();

        levelData = new short[wallMap.length * wallMap[0].length];
        freeboard = new short[wallMap.length * wallMap[0].length];

        List<Point> wallLocations = new ArrayList<>();
        for(int i = 0; i < wallMap.length; i++)
        {
            for(int j = 0; j < wallMap[i].length; j++)
            {

                if(wallMap[i][j]) {
                    levelData[i * wallMap[0].length + j] = 0;
                    wallLocations.add(new Point(j,i));
                    if(j == 0)
                    {
                        levelData[i * wallMap[0].length + j] += 1;
                    }
                    if(i == 0)
                    {
                        levelData[i * wallMap[0].length + j] += 2;
                    }
                    if(j == wallMap[i].length - 1)
                    {
                        levelData[i * wallMap[0].length + j] += 4;
                    }
                    if(i == wallMap.length - 1)
                    {
                        levelData[i * wallMap[0].length + j] += 8;
                    }
                } else {
                    levelData[i * wallMap[0].length + j] = 16;
                    if(j == 0 || wallMap[i][j - 1])
                    {
                        levelData[i * wallMap[0].length + j] += 1;
                    }
                    if(i == 0 || wallMap[i - 1][j])
                    {
                        levelData[i * wallMap[0].length + j] += 2;
                    }
                    if(j == wallMap[i].length - 1 || wallMap[i][j + 1])
                    {
                        levelData[i * wallMap[0].length + j] += 4;
                    }
                    if(i == wallMap.length - 1 || wallMap[i + 1][j])
                    {
                        levelData[i * wallMap[0].length + j] += 8;
                    }
                }
                freeboard[i * wallMap[0].length + j] = 16;
                if(j == 0)
                {
                    freeboard[i * wallMap[0].length + j] += 1;
                }
                if(i == 0)
                {
                    freeboard[i * wallMap[0].length + j] += 2;
                }
                if(j == wallMap[i].length - 1)
                {
                    freeboard[i * wallMap[0].length + j] += 4;
                }
                if(i == wallMap.length - 1)
                {
                    freeboard[i * wallMap[0].length + j] += 8;
                }
            }
        }

        wallX = new int[wallLocations.size()];
        wallY = new int[wallLocations.size()];
        for(int i = 0; i < wallLocations.size(); i++)
        {
            wallX[i] = wallLocations.get(i).x;
            wallY[i] = wallLocations.get(i).y;
        }

        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();

//        initGame();
    }

    private void doAnim() {

        pacAnimCount--;

        if (pacAnimCount <= 0) {
            pacAnimCount = PAC_ANIM_DELAY;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }

    private void playGame(Graphics2D g2d) {

        graph2d = g2d;
        if (dying) {

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {

        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, SCREEN_SIZE / 2 - 30, SCREEN_SIZE - 100, 50);

        String s = "Press s to start.";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (SCREEN_SIZE - metr.stringWidth(s)) / 2, SCREEN_SIZE / 2);
    }

    private void drawScore(Graphics2D g) {

        int i;
        String s;

        g.setFont(smallFont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (i = 0; i < pacsLeft; i++) {
            g.drawImage(pacman3left, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }

    private void checkMaze() {

        short i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i] & 48) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    private void death() {

        pacsLeft--;

        if (pacsLeft == 0) {
            inGame = false;
        }

        continueLevel();
    }

    private void moveGhosts(Graphics2D g2d) {

        short i;

        for (i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0)
            {
                moveGhost(i);
            }

            ghost_x[i] += (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] += (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }

    private void moveGhost(short ghostNumber) {
        switch(MODE)
        {
            case 0:
                moveGhostRandom(ghostNumber);
                break;
            case 1:
                moveGhostAStar(ghostNumber);
                break;
            case 2:
                moveGhostNewRandom(ghostNumber);
                break;
        }
    }

    private void moveGhostRandom(int i) {
        int pos;
        int count;

        pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

        count = 0;

        if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
            dx[count] = -1;
            dy[count] = 0;
            count++;
        }

        if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
            dx[count] = 0;
            dy[count] = -1;
            count++;
        }

        if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
            dx[count] = 1;
            dy[count] = 0;
            count++;
        }

        if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
            dx[count] = 0;
            dy[count] = 1;
            count++;
        }

        if (count == 0) {

            if ((screenData[pos] & 15) == 15) {
                ghost_dx[i] = 0;
                ghost_dy[i] = 0;
            } else {
                ghost_dx[i] = -ghost_dx[i];
                ghost_dy[i] = -ghost_dy[i];
            }

        } else {

            count = (int) (Math.random() * count);

            if (count > 3) {
                count = 3;
            }

            ghost_dx[i] = dx[count];
            ghost_dy[i] = dy[count];
        }
    }

    private void moveGhostAStar(int ghostNumber) {
        LocationNode[][] nodeMatrix = new LocationNode[wallMap[0].length][wallMap.length];
        for(int i = 0; i < nodeMatrix.length; i++)
        {
            for(int j = 0; j < nodeMatrix[i].length; j++)
            {
                if(!wallMap[j][i])
                {
                    nodeMatrix[i][j] = new LocationNode(i, j);
                }
            }
        }
        int playerX = pacman_x / BLOCK_SIZE;
        int playerY = pacman_y / BLOCK_SIZE;
        int ghostX = ghost_x[ghostNumber] / BLOCK_SIZE;
        int ghostY = ghost_y[ghostNumber] / BLOCK_SIZE;
        LocationNode root = nodeMatrix[ghostX][ghostY];
        root.setWeight(calculateManhattanDistance(ghostX, ghostY, playerX, playerY));
        PriorityQueue<LocationNode> nodesToExplore = new PriorityQueue<>();
        nodesToExplore.add(root);
        Set<LocationNode> exploredNodes = new HashSet<>();
        exploredNodes.add(root);
        while(!nodesToExplore.isEmpty())
        {
            LocationNode currentNode = nodesToExplore.poll();
            int currentX = currentNode.x;
            int currentY = currentNode.y;
            if(currentX > 0)
            {
                int nextX = currentX - 1;
                if(nodeMatrix[nextX][currentY] != null)
                {
                    LocationNode nextNode = nodeMatrix[nextX][currentY];
                    if(exploredNodes.add(nextNode))
                    {
                        nextNode.setParent(currentNode);
                        nextNode.setWeight(nextNode.dist + calculateManhattanDistance(nextNode.x, nextNode.y, playerX, playerY));
                        if(nextX == playerX && currentY == playerY)
                        {
                            break;
                        }
                        nodesToExplore.add(nextNode);
                    }
                }
            }
            if(currentX < nodeMatrix.length - 1)
            {
                int nextX = currentX + 1;
                if(nodeMatrix[nextX][currentY] != null)
                {
                    LocationNode nextNode = nodeMatrix[nextX][currentY];
                    if(exploredNodes.add(nextNode))
                    {
                        nextNode.setParent(currentNode);
                        nextNode.setWeight(nextNode.dist + calculateManhattanDistance(nextNode.x, nextNode.y, playerX, playerY));
                        if(nextX == playerX && currentY == playerY)
                        {
                            break;
                        }
                        nodesToExplore.add(nextNode);
                    }
                }
            }
            if(currentY > 0)
            {
                int nextY = currentY - 1;
                if(nodeMatrix[currentX][nextY] != null)
                {
                    LocationNode nextNode = nodeMatrix[currentX][nextY];
                    if(exploredNodes.add(nextNode))
                    {
                        nextNode.setParent(currentNode);
                        nextNode.setWeight(nextNode.dist + calculateManhattanDistance(nextNode.x, nextNode.y, playerX, playerY));
                        if(currentX == playerX && nextY == playerY)
                        {
                            break;
                        }
                        nodesToExplore.add(nextNode);
                    }
                }
            }
            if(currentY < nodeMatrix[currentX].length - 1)
            {
                int nextY = currentY + 1;
                if(nodeMatrix[currentX][nextY] != null)
                {
                    LocationNode nextNode = nodeMatrix[currentX][nextY];
                    if(exploredNodes.add(nextNode))
                    {
                        nextNode.setParent(currentNode);
                        nextNode.setWeight(nextNode.dist + calculateManhattanDistance(nextNode.x, nextNode.y, ghostX, ghostY));
                        if(currentX == playerX && nextY == playerY)
                        {
                            break;
                        }
                        nodesToExplore.add(nextNode);
                    }
                }
            }
        }
        LocationNode destinationNode = nodeMatrix[playerX][playerY];
        if(destinationNode != null && destinationNode.getParent() != null)
        {
            LocationNode parent = destinationNode.getParent();
            while(parent != root)
            {
                destinationNode = parent;
                parent = parent.getParent();
            }
            ghost_dx[ghostNumber] = destinationNode.x - root.x;
            ghost_dy[ghostNumber] = destinationNode.y - root.y;
        } else
        {
            ghost_dx[ghostNumber] = 0;
            ghost_dy[ghostNumber] = 0;
        }
    }

    private void moveGhostNewRandom(int i) { //todo: make not random
        int pos;
        int count;

        pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

        count = 0;

        if ((freeboard[pos] & 1) == 0 && ghost_dx[i] != 1) {
            dx[count] = -1;
            dy[count] = 0;
            count++;
        }

        if ((freeboard[pos] & 2) == 0 && ghost_dy[i] != 1) {
            dx[count] = 0;
            dy[count] = -1;
            count++;
        }

        if ((freeboard[pos] & 4) == 0 && ghost_dx[i] != -1) {
            dx[count] = 1;
            dy[count] = 0;
            count++;
        }

        if ((freeboard[pos] & 8) == 0 && ghost_dy[i] != -1) {
            dx[count] = 0;
            dy[count] = 1;
            count++;
        }

        if (count == 0) {

            if ((freeboard[pos] & 15) == 15) {
                ghost_dx[i] = 0;
                ghost_dy[i] = 0;
            } else {
                ghost_dx[i] = -ghost_dx[i];
                ghost_dy[i] = -ghost_dy[i];
            }

        } else {

            count = (int) (Math.random() * count);

            if (count > 3) {
                count = 3;
            }

            ghost_dx[i] = dx[count];
            ghost_dy[i] = dy[count];
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {

        g2d.drawImage(ghost, x, y, this);
    }

    private void movePacman() {

        int pos;
        short ch;

        if (req_dx == -pacmand_x && req_dy == -pacmand_y) {
            pacmand_x = req_dx;
            pacmand_y = req_dy;
            view_dx = pacmand_x;
            view_dy = pacmand_y;
        }

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                Point pel = null;
                for(Point pellet : pellets)
                {
                	if(pellet.x == BLOCK_SIZE *pos % N_BLOCKS && BLOCK_SIZE * pellet.y == pos / N_BLOCKS)
                	{
                		pel = pellet;
                	}
                }
                if(pel != null)
                {
                	pellets.remove(pel);
                }
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                    view_dx = pacmand_x;
                    view_dy = pacmand_y;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void drawPacman(Graphics2D g2d) {

        if (view_dx == -1) {
            drawPacmanLeft(g2d);
        } else if (view_dx == 1) {
            drawPacmanRight(g2d);
        } else if (view_dy == -1) {
            drawPacmanUp(g2d);
        } else {
            drawPacmanDown(g2d);
        }
    }

    private void drawPacmanUp(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4up, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4down, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanLeft(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4left, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4right, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(mazeColor);
                g2d.setStroke(new BasicStroke(2));

                if ((screenData[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    private void initGame() {

        inGame = true;
        pacsLeft = 3;
        score = 0;
        initLevel();
//        N_GHOSTS = 2;
        currentSpeed = 3;
    }

    private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
            // If the current datapoint includes a pellet, and a point to our pellet tracker
            if((screenData[i] & 48) != 0)
            {
            	pellets.add(new Point(BLOCK_SIZE * i % N_BLOCKS, BLOCK_SIZE * i / N_BLOCKS));
            }
        }

        continueLevel();
    }

    private void continueLevel() {

        short i;
        int dx = 1;
        int random;

        for (i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = ghostStart[1] * BLOCK_SIZE;
            ghost_x[i] = ghostStart[0] * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x = pacStart[0] * BLOCK_SIZE;
        pacman_y = pacStart[1] * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        view_dx = -1;
        view_dy = 0;
        dying = false;
    }

    private void loadImages() {

        ghost = new ImageIcon("pacman_images/ghost.png").getImage();
        pacman1 = new ImageIcon("pacman_images/pacman.png").getImage();
        pacman2up = new ImageIcon("pacman_images/up1.png").getImage();
        pacman3up = new ImageIcon("pacman_images/up2.png").getImage();
        pacman4up = new ImageIcon("pacman_images/up3.png").getImage();
        pacman2down = new ImageIcon("pacman_images/down1.png").getImage();
        pacman3down = new ImageIcon("pacman_images/down2.png").getImage();
        pacman4down = new ImageIcon("pacman_images/down3.png").getImage();
        pacman2left = new ImageIcon("pacman_images/left1.png").getImage();
        pacman3left = new ImageIcon("pacman_images/left2.png").getImage();
        pacman4left = new ImageIcon("pacman_images/left3.png").getImage();
        pacman2right = new ImageIcon("pacman_images/right1.png").getImage();
        pacman3right = new ImageIcon("pacman_images/right2.png").getImage();
        pacman4right = new ImageIcon("pacman_images/right3.png").getImage();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        graphics = g;
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        graph2d = g2d;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        if(drawTiles)
        {
            for(ColorCoordinate colorCoord : tilesToDraw)
            {
                drawTile(colorCoord.getX(), colorCoord.getY(), colorCoord.getColor());
            }
            drawTiles = false;

        }

        drawMaze(g2d);
        drawScore(g2d);
        doAnim();

        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        g2d.drawImage(ii, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    public void directionGiven(int key) {
        if (inGame) {
            if (key == KeyEvent.VK_LEFT) {
                req_dx = -1;
                req_dy = 0;
            } else if (key == KeyEvent.VK_RIGHT) {
                req_dx = 1;
                req_dy = 0;
            } else if (key == KeyEvent.VK_UP) {
                req_dx = 0;
                req_dy = -1;
            } else if (key == KeyEvent.VK_DOWN) {
                req_dx = 0;
                req_dy = 1;
            } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                inGame = false;
            } else if (key == KeyEvent.VK_P) {
                if (timer.isRunning()) {
                    timer.stop();
                    if(miniMain != null)
                    {
                        miniMain.paused();
                    }
                } else {
                    timer.start();
                }
            }
        } else {
            if (key == 's' || key == 'S') {
                initGame();
            }
        }
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } else if (key == KeyEvent.VK_P) {
                    if (timer.isRunning()) {
                        timer.stop();
                        if(miniMain != null)
                        {
                        	miniMain.paused();
                        }
                    } else {
                        timer.start();
                    }
                }
            } else {
                if (key == 's' || key == 'S') {
                    initGame();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) {
                req_dx = 0;
                req_dy = 0;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(inGame && miniMain != null)
        {
            miniMain.running();
        }
        repaint();
    }
    
    //------------------------------------------------Begin custom code (so we can pull pieces out)--------------------------------

    /**
     * Starts the game
     */
    public void play()
    {
        initGame();
    }

    /**
     * Tells whether the game is currently playing
     * @return True if the game is in session, or false if the game hasn't been started, has ended, or is paused
     */
    public boolean isPlaying()
    {
    	return inGame;
    }

    public void drawTiles(List<ColorCoordinate> coloredTiles) {
        tilesToDraw = coloredTiles;
        drawTiles = true;
    }

    public void drawTile(int xCoord, int yCoord, Color color) {
        int x = xCoord * BLOCK_SIZE;
        int y = yCoord * BLOCK_SIZE;
//        graph2d.setColor(Color.white);
//        graph2d.fillRect(0,0,BLOCK_SIZE, BLOCK_SIZE);
        graph2d.setColor(color);
        graph2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
    }
    
    public int getPacmanX()
    {
    	return pacman_x;
    }
    
    public int getPacmanY()
    {
    	return pacman_y;
    }
    
    public int[] getGhostXs() {
        int[] ghostXs = new int[N_GHOSTS];
        for(int i = 0; i < N_GHOSTS; i++)
        {
            ghostXs[i] = ghost_x[i];
        }
    	return ghostXs;
    }
    
    public int getGhostX(int index)
    {
    	return ghost_x[index];
    }
    
    public int[] getGhostYs() {
        int[] ghostYs = new int[N_GHOSTS];
        for(int i = 0; i < N_GHOSTS; i++)
        {
            ghostYs[i] = ghost_y[i];
        }
    	return ghostYs;
    }
    
    public int getGhostY(int index)
    {
    	return ghost_y[index];
    }
    
    public int getLives()
    {
    	return pacsLeft;
    }
    
    public int getScore()
    {
    	return score;
    }
    
    public ArrayList<Point> getPelletLocationss()
    {
    	return pellets;
    }
    
    public int getTileSize()
    {
    	return BLOCK_SIZE;
    }
    
    public int[] getWallX()
    {
    	return wallX;
    }
    
    public int[] getWallY()
    {
    	return wallY;
    }

    public int getPacmanSpeed()
    {
        return PACMAN_SPEED;
    }

    public int[] getPacmanDirection() {
        int[] direction = new int[2];
        direction[0] = pacmand_x;
        direction[1] = pacmand_y;
        return direction;
    }

    public int[] getGhostSpeeds() {
        int[] speeds = new int[N_GHOSTS];
        for(int i = 0; i < N_GHOSTS; i++) {
            speeds[i] = ghostSpeed[i];
        }
        return speeds;
    }

    private int calculateManhattanDistance(int x1, int y1, int x2, int y2) {
        int dist = 0;
        dist += Math.abs(x1 - x2);
        dist += Math.abs(y1 - y2);
        return dist;
    }

    private class LocationNode implements Comparable {

        private LocationNode parent;
        public int x;
        public int y;
        public int weight;
        public int dist;

        public LocationNode(int x, int y) {
            this.x = x;
            this.y = y;
            this.weight = Integer.MAX_VALUE;
            this.dist = 0;
        }

        public void setParent(LocationNode parent) {
            this.parent = parent;
            this.dist = parent.dist + 1;
        }

        public void setWeight(int newWeight)
        {
            this.weight = newWeight;
        }

        public LocationNode getParent()
        {
            return parent;
        }

        @Override
        public int compareTo(Object otherNode)
        {
            if(otherNode == null)
            {
                return 0;
            } else if(otherNode instanceof LocationNode)
            {
                int otherWeight = ((LocationNode) otherNode).weight;
                return Integer.compare(weight, otherWeight);
            } else
            {
                return 0;
            }
        }
    }

}