package miniMain;

import actionNodes.Play;
import instructions.Instruction;
import instructions.InstructionPacket;
import instructions.InstructionType;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import pacman.Pacman;
import thingNodes.PacmanGame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

public class PlayPacman implements MiniMain
{

    public static boolean pause_play;

    public static void main(String[] args) throws AWTException {
        pause_play = false;
        PacmanGame game = new PacmanGame();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        try
        {
            Play playNode = new Play(null, null, game, null, null, null, 1, 1, 1);
            playNode.initialize();
            Instruction instruction = new Instruction(InstructionType.START, null);
            Robot robot = new Robot();
            int count = 0;
            while(instruction.getType() != InstructionType.FINISH)
            {
                game.update();
                List<InstructionPacket> instructionPackets = playNode.run();
                double[] probabilityVector = generateProbabilityVector(game.getAttribute("dimensions").split(",").length,
                        instructionPackets);
                double rand = Math.random();
                if(rand < probabilityVector[0])
                {
                    robot.keyPress(KeyEvent.VK_RIGHT);
                    System.out.println("Right");
                } else if(rand < probabilityVector[0] + probabilityVector[1])
                {
                    robot.keyPress(KeyEvent.VK_LEFT);
                    System.out.println("Left");
                } else if(rand < probabilityVector[0] + probabilityVector[1] + probabilityVector[2])
                {
                    robot.keyPress(KeyEvent.VK_DOWN);
                    System.out.println("Down");
                } else
                {
                    robot.keyPress(KeyEvent.VK_UP);
                    System.out.println("Up");
                }
            }
        } catch (NotAnActionNodeException e)
        {
            System.err.println("Not an Action Node Exception: " + e.getMessage());
        } catch (UnknownActionException e)
        {
            System.err.println("Unknown Action Exception: " + e.getMessage());
        } catch (IOException e)
        {
            System.err.println("IO Exception: " + e.getMessage());
        } catch (UnreadableActionNodeException e)
        {
            System.err.println("Unreadable Action Node Exception: " + e.getMessage());
        }
        System.out.println("Reached the end!");
    }

    private static double[] generateProbabilityVector(int nDimensions, List<InstructionPacket> instructionPackets)
    {
        double[] probVector = new double[nDimensions * 2];
        for(int i = 0; i < probVector.length; i++)
        {
            probVector[i] =0;
        }
        for(InstructionPacket packet : instructionPackets)
        {
            List<String> params = packet.getInstruction().getParameters();
            double[] paramDoubles = new double[params.size()];
            int indexOfLargest = 0;
            double largestVal = 0;
            for(int i = 0; i < params.size(); i++)
            {
                paramDoubles[i] = Double.parseDouble(params.get(i));
                if(Math.abs(paramDoubles[i]) > Math.abs(largestVal))
                {
                    indexOfLargest = i;
                    largestVal = paramDoubles[i];
                }
            }
            for(int i = 0; i < paramDoubles.length; i++)
            {
                if(i != indexOfLargest)
                {
                    paramDoubles[i] = 0;
                } else if(largestVal != 0)
                {
                    if(largestVal > 0)
                    {
                        probVector[2 * i] += largestVal;
                    } else
                    {
                        probVector[2 * i + 1] -= largestVal;
                    }
                }
            }
            System.out.print("Urgency: " + packet.getOriginNode().getUrgency() + " | ");
        }
        double vectorLength = 0;
        for(int i = 0; i < probVector.length; i++)
        {
            vectorLength += probVector[i] * probVector[i];
        }
        vectorLength = Math.sqrt(vectorLength);
        for(int i = 0; i < probVector.length; i++)
        {
            probVector[i] /= vectorLength;
        }
        return probVector;
    }

    @Override
    public void running()
    {

    }

    @Override
    public void paused()
    {

    }
}
