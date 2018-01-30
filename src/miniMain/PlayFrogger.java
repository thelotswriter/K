package miniMain;

import actionNodes.Play;
import frogger.Frogger;
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

                int[] direction = new int[2];
                direction[0] = Integer.parseInt(instructionPackets.get(0).getInstruction().getParameters().get(0));
                direction[1] = Integer.parseInt(instructionPackets.get(0).getInstruction().getParameters().get(1));
                StringBuilder bob = new StringBuilder();
                bob.append(direction[0]);
                bob.append(",");
                bob.append(direction[1]);
                System.out.println(bob.toString());
                if(Math.abs(direction[0]) > Math.abs(direction[1]))
                {
                    if(direction[0] > 0)
                    {
                        froggerMain.setPendingAction(FroggerAction.RIGHT);
                    } else
                    {
                        froggerMain.setPendingAction(FroggerAction.LEFT);
                    }
                } else if(Math.abs(direction[0]) < Math.abs(direction[1]))
                {
                    if(direction[1] > 0)
                    {
                        froggerMain.setPendingAction((FroggerAction.DOWN));
                    } else
                    {
                        froggerMain.setPendingAction(FroggerAction.UP);
                    }
                } else
                {

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
