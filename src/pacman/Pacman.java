package pacman;

import java.awt.EventQueue;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import miniMain.ColorCoordinate;
import miniMain.MiniMain;

// Borrowed heavily from: http://zetcode.com/tutorials/javagamestutorial/pacman/

@SuppressWarnings("serial")
public class Pacman extends JFrame 
{
	
	private Board board;

    public Pacman() 
    {
        initUI();
    }
    
    public Pacman(MiniMain miniMain)
    {
    	initUI(miniMain);
    }
    
    private void initUI() 
    {
        board = new Board();
        add(board);
        
        setTitle("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 420);
        setLocationRelativeTo(null);
        setVisible(true);        
    }
    
    private void initUI(MiniMain miniMain) 
    {
        board = new Board(miniMain);
        add(board);
        
        setTitle("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 420);
        setLocationRelativeTo(null);
        setVisible(true);        
    }

    public static void main(String[] args) 
    {
    	
        EventQueue.invokeLater(() -> {
            Pacman ex = new Pacman();
            ex.setVisible(true);
        });
    }
    
    public boolean isPlaying()
    {
    	return board.isPlaying();
    }
    
    public int getPacmanX()
    {
    	return board.getPacmanX();
    }
    
    public int getPacmanY()
    {
    	return board.getPacmanY();
    }
    
    public int[] getGhostXs()
    {
    	return board.getGhostXs();
    }
    
    public int getGhostX(int index)
    {
    	return board.getGhostX(index);
    }
    
    public int[] getGhostYs()
    {
    	return board.getGhostYs();
    }
    
    public int getGhostY(int index)
    {
    	return board.getGhostY(index);
    }
    
    public int getLives()
    {
    	return board.getLives();
    }
    
    public int getScore()
    {
    	return board.getScore();
    }
    
    public ArrayList<Point> getPelletLocationss()
    {
    	return board.getPelletLocationss();
    }
    
    public int getTileSize()
    {
    	return board.getTileSize();
    }
    
    public int[] getWallX()
    {
    	return board.getWallX();
    }
    
    public int[] getWallY()
    {
    	return board.getWallY();
    }

    public int getPacmanSpeed()
    {
        return board.getPacmanSpeed();
    }

    public int getGhostSpeed()
    {
        return board.getGhostSpeed();
    }

    public void drawTile(List<ColorCoordinate> coloredTiles)
    {
        board.drawTiles(coloredTiles);
//        board.drawTile(xCoord, yCoord);
        Timer tileTimer = new Timer(0,board);
        tileTimer.setRepeats(false);
        tileTimer.start();
    }

    public void drawRunningTiles(List<ColorCoordinate> coloredTiles)
    {
        board.drawTiles(coloredTiles);
//        board.drawTile(xCoord, yCoord);
//        Timer tileTimer = new Timer(0,board);
//        tileTimer.setRepeats(false);
//        tileTimer.start();
    }
    
}