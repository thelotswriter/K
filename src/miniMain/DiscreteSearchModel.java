package miniMain;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import instructions.Instruction;
import instructions.InstructionType;
import pacman.Pacman;
import processTree.Model;
import processTree.ThingNode;
import subModel.Discrete2DSpatialModel;
import thingNodes.PacmanGame;
import thingNodes.PacmanGhost;
import thingNodes.PacmanGhosts;

public class DiscreteSearchModel implements MiniMain
{
	
	static Pacman pMan;

	public static void main(String[] args) 
	{
		pMan = new Pacman(new DiscreteSearchModel());
	}

	public void running()
	{
        PacmanGame pacmanWorld = new PacmanGame(pMan);
        PacmanGhosts ghosts = (PacmanGhosts) pacmanWorld.getThing("ghosts");
        List<ThingNode> ghostList = ghosts.getElements();
        List<double[][]> probabilityMaps = new ArrayList<>();
        for(int i = 0; i < ghostList.size(); i++)
        {
            Discrete2DSpatialModel discrete2DSpatialModel = new Discrete2DSpatialModel(ghostList.get(i), pacmanWorld);
            double[][] probabilityMap = discrete2DSpatialModel.generateProbabilityMap();
            probabilityMaps.add(probabilityMap);
        }
		double[][] finalProbabilityMap = probabilityMaps.get(0);
        for(int i = 1; i < probabilityMaps.size(); i++)
        {
            double[][] probabilityMap = probabilityMaps.get(i);
            for(int j = 0; j < finalProbabilityMap.length; j++)
            {
                for(int k = 0; k < finalProbabilityMap[0].length; k++)
                {
                    finalProbabilityMap[j][k] += probabilityMap[j][k];
                }
            }
        }
        double maxProbability = 0;
        for(int i = 0; i < finalProbabilityMap.length; i++)
        {
            for(int j = 0; j < finalProbabilityMap[0].length; j++)
            {
                if(finalProbabilityMap[i][j] > maxProbability)
                {
                    maxProbability = finalProbabilityMap[i][j];
                }
            }
        }
        ArrayList<ColorCoordinate> coloredCoordinates = new ArrayList<>();
        for(int i = 0; i < finalProbabilityMap.length; i++)
        {
            for(int j = 0; j < finalProbabilityMap[0].length; j++)
            {
                if(finalProbabilityMap[i][j] > 0)
                {
                    double probability = finalProbabilityMap[i][j] / maxProbability;
                    double blueVal = Math.max(1 - 2 * probability, 0);
                    double greenVal = 0;
                    if(probability < 0.5)
                    {
                        greenVal = 2 * probability;
                    } else
                    {
                        greenVal = 2 - 2 * probability;
                    }
                    double redVal = Math.max(2 * probability - 1, 0);
//                    double blueVal = 1 - probability;
//                    double greenVal = 0;
//                    if(probability < 0.5)
//                    {
//                        greenVal = 2 * probability;
//                    } else
//                    {
//                        greenVal = 2 - 2 * probability;
//                    }
//                    double redVal = probability;
                	Color probabilityColor = new Color((float) redVal,
                            (float) greenVal,
                            (float) blueVal);
                    coloredCoordinates.add(new ColorCoordinate(i, j, probabilityColor));
                }
            }
        }
        pMan.drawRunningTiles(coloredCoordinates);
	}

	public void paused() 
	{
		PacmanGame pacmanWorld = new PacmanGame(pMan);
		PacmanGhost ghost = (PacmanGhost) pacmanWorld.getThing("ghost");
		Discrete2DSpatialModel discrete2DSpatialModel = new Discrete2DSpatialModel(ghost, pacmanWorld);
		double[][] probabilityMap = discrete2DSpatialModel.generateProbabilityMap();
		double maxProbability = 0;
        for(int i = 0; i < probabilityMap.length; i++)
        {
            for(int j = 0; j < probabilityMap[0].length; j++)
            {
                if(probabilityMap[i][j] > maxProbability)
                {
                    maxProbability = probabilityMap[i][j];
                }
            }
        }
		ArrayList<ColorCoordinate> coloredCoordinates = new ArrayList<>();
		for(int i = 0; i < probabilityMap.length; i++)
		{
			for(int j = 0; j < probabilityMap[0].length; j++)
			{
				if(probabilityMap[i][j] > 0)
				{
                    double probability = probabilityMap[i][j] / maxProbability;
                    double blueVal = Math.max(1 - 2 * probability, 0);
                    double greenVal = 0;
                    if(probability < 0.5)
                    {
                        greenVal = 2 * probability;
                    } else
                    {
                        greenVal = 2 - 2 * probability;
                    }
                    double redVal = Math.max(2 * probability - 1, 0);
                    Color probabilityColor = new Color((float) redVal,
                            (float) greenVal,
                            (float) blueVal);
                    coloredCoordinates.add(new ColorCoordinate(i, j, probabilityColor));
				}
			}
		}
		pMan.drawTile(coloredCoordinates);

//		Model model = new Model(ghost, pacmanWorld);
//		List<Collection<Instruction>> instructionCollectionList = model.generateActionSequence();
//		int cursorX = pMan.getLocationOnScreen().x + pMan.getGhostX(0) + 16;
//		int cursorY = pMan.getLocationOnScreen().y + pMan.getGhostY(0) + 40;
//		try {
//			Robot robot = new Robot();
//			robot.mouseMove(cursorX, cursorY);
//			for(int i = 0; i < instructionCollectionList.size(); i++)
//			{
//				Collection<Instruction> collection = instructionCollectionList.get(i);
//				for(Instruction instruction : collection)
//				{
//					if(instruction.getType().equals(InstructionType.MOVE))
//					{
//						List<String> moves = instruction.getParameters();
//						cursorX += Integer.parseInt(moves.get(0));
//						cursorY += Integer.parseInt(moves.get(1));
//						System.out.print(cursorX);
//						System.out.print(", ");
//						System.out.println(cursorY);
//						robot.mouseMove(cursorX, cursorY);
//						Thread.sleep(16);
//					} else
//					{
//						System.out.println("Check");
//					}
//				}
//			}
//		} catch (AWTException e)
//		{
//			e.printStackTrace();
//		} catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		}
	}
	
}
