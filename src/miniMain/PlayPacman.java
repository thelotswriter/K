package miniMain;

import actionNodes.Play;
import instructions.Instruction;
import instructions.InstructionType;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import pacman.Pacman;
import thingNodes.PacmanGame;

import java.io.IOException;

public class PlayPacman implements MiniMain
{

    public static void main(String[] args)
    {
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
            while(instruction.getType() != InstructionType.FINISH)
            {

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

    @Override
    public void running()
    {

    }

    @Override
    public void paused()
    {

    }
}
