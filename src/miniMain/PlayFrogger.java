package miniMain;

import actionNodes.Play;
import frogger.FroggerAction;
import frogger.FroggerMain;
import instructions.Instruction;
import instructions.InstructionPacket;
import instructions.InstructionType;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import thingNodes.frogger.FroggerGame;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

public class PlayFrogger implements MiniMain
{

    public static void main(String args[])
    {
        FroggerGame froggerGame = new FroggerGame(null, null, null, null, 1);


        try
        {

            Play playNode = new Play(null, null, null, froggerGame, null, null, null, 1, 1, 1);
            playNode.initialize();
            Instruction instruction = new Instruction(InstructionType.START, null);
            int count = 0;
            FroggerMain froggerMain = froggerGame.getGame();

            while (instruction.getType() != InstructionType.FINISH)
            {
                count++;
                froggerGame.update();
                List<InstructionPacket> instructionPackets = playNode.run();

                double[] probabilityVector = PlayPacman.generateProbabilityVector(froggerGame.getAttribute("dimensions").split(",").length,
                        instructionPackets);
                double rand = Math.random();
                if(rand < probabilityVector[0])
                {
                    froggerMain.setPendingAction(FroggerAction.RIGHT);
                    System.out.println("Right");
                } else if(rand < probabilityVector[0] + probabilityVector[1])
                {
                    froggerMain.setPendingAction(FroggerAction.LEFT);
//                    robot.keyPress(KeyEvent.VK_LEFT);
                    System.out.println("Left");
                } else if(rand < probabilityVector[0] + probabilityVector[1] + probabilityVector[2])
                {
                    froggerMain.setPendingAction(FroggerAction.DOWN);
//                    robot.keyPress(KeyEvent.VK_DOWN);
                    System.out.println("Down");
                } else
                {
                    froggerMain.setPendingAction(FroggerAction.UP);
//                    robot.keyPress(KeyEvent.VK_UP);
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

    }

    public static FroggerAction makeRandomMove() {
        return FroggerAction.UP;
    }

    public int getBoardHeight() {
        return FroggerMain.WORLD_HEIGHT;
    }

    public int getBoardWidth() {
        return FroggerMain.WORLD_WIDTH;
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
