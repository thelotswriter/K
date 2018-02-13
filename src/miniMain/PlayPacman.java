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
        PacmanGame game = new PacmanGame(null, null, null, null, 1);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        try
        {
            Play playNode = new Play(null, null, null, game, null, null, null, 1, 1, 1);
            playNode.initialize();
            Instruction instruction = new Instruction(InstructionType.START, null);
//            Robot robot = new Robot();
            Pacman pMan = game.getGame();
            while(instruction.getType() != InstructionType.FINISH)
            {
                game.update();
                List<InstructionPacket> instructionPackets = playNode.run();
                double[] direction = new double[2];
//                if(instructionPackets.get(1).getInstruction().getType().equals(InstructionType.MOVE))
//                {
//                    int x = 0;
//                }
                for(InstructionPacket instructionPacket : instructionPackets)
                {
                    if(instructionPacket.getInstruction().getType().equals(InstructionType.MOVE))
                    {
                        double urgency = instructionPacket.getOriginNode().getUrgency();
                        System.out.println("Urgency: " + urgency);
                        direction[0] += Double.parseDouble(instructionPacket.getInstruction().getParameters().get(0)) * urgency;
                        direction[1] += Double.parseDouble(instructionPacket.getInstruction().getParameters().get(1)) * urgency;
                        StringBuilder bob = new StringBuilder();
                        bob.append("Single: ");
                        bob.append((int) direction[0]);
                        bob.append(",");
                        bob.append((int) direction[1]);
                        System.out.println(bob.toString());
                    }
                }
                StringBuilder bob = new StringBuilder();
                bob.append((int) direction[0]);
                bob.append(",");
                bob.append((int) direction[1]);
                System.out.println(bob.toString());
                if(Math.abs(direction[0]) > Math.abs(direction[1]))
                {
                    if(direction[0] > 0)
                    {
                        pMan.directionGiven(KeyEvent.VK_RIGHT);
                    } else
                    {
                        pMan.directionGiven(KeyEvent.VK_LEFT);
                    }
                } else if(Math.abs(direction[0]) < Math.abs(direction[1]))
                {
                    if(direction[1] > 0)
                    {
                        pMan.directionGiven(KeyEvent.VK_DOWN);
                    } else
                    {
                        pMan.directionGiven(KeyEvent.VK_UP);
                    }
                } else
                {

                }
//                double[] probabilityVector = generateProbabilityVector(game.getAttribute("dimensions").split(",").length,
//                        instructionPackets);
//                double rand = Math.random();
////                System.out.println("Just ran");
//                if(rand < probabilityVector[0])
//                {
//                    pMan.directionGiven(KeyEvent.VK_RIGHT);
////                    robot.keyPress(KeyEvent.VK_RIGHT);
////                    System.out.println("Right");
//                } else if(rand < probabilityVector[0] + probabilityVector[1])
//                {
//                    pMan.directionGiven(KeyEvent.VK_LEFT);
////                    robot.keyPress(KeyEvent.VK_LEFT);
////                    System.out.println("Left");
//                } else if(rand < probabilityVector[0] + probabilityVector[1] + probabilityVector[2])
//                {
//                    pMan.directionGiven(KeyEvent.VK_DOWN);
////                    robot.keyPress(KeyEvent.VK_DOWN);
////                    System.out.println("Down");
//                } else
//                {
//                    pMan.directionGiven(KeyEvent.VK_UP);
////                    robot.keyPress(KeyEvent.VK_UP);
////                    System.out.println("Up");
//                }
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

    public static double[] generateProbabilityVector(int nDimensions, List<InstructionPacket> instructionPackets)
    {
        double[] probVector = new double[nDimensions * 2];
        for(int i = 0; i < probVector.length; i++)
        {
            probVector[i] =0;
        }
        for(InstructionPacket packet : instructionPackets)
        {
            List<String> params = packet.getInstruction().getParameters();
//            StringBuilder paramPrint = new StringBuilder();
//            paramPrint.append(params.get(0));
//            paramPrint.append(",");
//            paramPrint.append(params.get(1));
//            System.out.println(paramPrint.toString());
            if(params != null)
            {
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
//                System.out.print("Urgency: " + packet.getOriginNode().getUrgency() + " | ");
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
