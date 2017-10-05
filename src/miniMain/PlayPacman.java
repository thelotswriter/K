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

    public static void main(String[] args) throws AWTException {
        PacmanGame game = new PacmanGame();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        try
        {
            Play playNode = new Play(null, game, null, null, null, 1, 1, 1);
            playNode.initialize();
            Instruction instruction = new Instruction(InstructionType.START, null);
            Robot robot = new Robot();
            while(instruction.getType() != InstructionType.FINISH)
            {
                game.update();
                List<InstructionPacket> instructionPackets = playNode.run();
                double[] probabilityVector = generateProbabilityVector(game.getAttribute("dimensions").split(",").length,
                        instructionPackets);
                double rand = Math.random();
                if(rand < Math.abs(probabilityVector[0]))
                {
                    if(probabilityVector[0] > 0)
                    {
                        robot.keyPress(KeyEvent.VK_RIGHT);
                        System.out.println("Right");
                    } else
                    {
                        robot.keyPress(KeyEvent.VK_LEFT);
                        System.out.println("Left");
                    }
                } else
                {
                    if(probabilityVector[0] > 0)
                    {
                        robot.keyPress(KeyEvent.VK_DOWN);
                        System.out.println("Down");
                    } else
                    {
                        robot.keyPress(KeyEvent.VK_UP);
                        System.out.println("Up");
                    }
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
        double[] probVector = new double[nDimensions];
        for(int i = 0; i < probVector.length; i++)
        {
            probVector[i] =0;
        }
        for(InstructionPacket packet : instructionPackets)
        {
            List<String> params = packet.getInstruction().getParameters();
            for(int i = 0; i < probVector.length; i++)
            {
                double paramVal = Double.parseDouble(params.get(i));;
                probVector[i] += paramVal;
            }
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
