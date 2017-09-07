package miniMain;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

public class DiscreteSearchModel implements MiniMain
{
	
	static Pacman pMan;

	public static void main(String[] args) 
	{
		pMan = new Pacman(new DiscreteSearchModel());
	}

	public void paused() 
	{
		PacmanGame pacmanWorld = new PacmanGame(pMan);
		PacmanGhost ghost = (PacmanGhost) pacmanWorld.getThing("ghost");
//		Discrete2DSpatialModel discrete2DSpatialModel = new Discrete2DSpatialModel(ghost, pacmanWorld);
//		double[][] probabilityMap = discrete2DSpatialModel.generateProbabilityMap();
		pMan.drawTile(1,1);

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
