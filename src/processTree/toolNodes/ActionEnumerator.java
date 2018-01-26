package processTree.toolNodes;

import instructions.Instruction;
import instructions.InstructionType;
import processTree.ThingNode;

import java.util.ArrayList;
import java.util.List;

public class ActionEnumerator
{

    private static ActionEnumerator SINGLETON = null;

    private ActionEnumerator()
    {

    }

    public static ActionEnumerator getInstance()
    {
        if(SINGLETON == null)
        {
            SINGLETON = new ActionEnumerator();
        }
        return SINGLETON;
    }

    public List<List<Instruction>> enumeratePossibleActions(ThingNode thing)
    {
        List<Instruction> moveUp = new ArrayList<>();
        List<Instruction> moveDown = new ArrayList<>();
        List<Instruction> moveLeft = new ArrayList<>();
        List<Instruction> moveRight = new ArrayList<>();
        List<Instruction> nonMove = new ArrayList<>();
        List<String> upParam = new ArrayList<>();
        List<String> downParam = new ArrayList<>();
        List<String> leftParam = new ArrayList<>();
        List<String> rightParam = new ArrayList<>();
        List<String> nonMoveParam = new ArrayList<>();
        upParam.add("0");
        upParam.add("-1");
        downParam.add("0");
        downParam.add("1");
        leftParam.add("-1");
        leftParam.add("0");
        rightParam.add("1");
        rightParam.add("0");
        nonMoveParam.add("0");
        nonMoveParam.add("0");
        moveUp.add(new Instruction(InstructionType.MOVE, upParam));
        moveDown.add(new Instruction(InstructionType.MOVE, downParam));
        moveLeft.add(new Instruction(InstructionType.MOVE, leftParam));
        moveRight.add(new Instruction(InstructionType.MOVE, rightParam));
        nonMove.add(new Instruction(InstructionType.MOVE, nonMoveParam));
        List<List<Instruction>> aggregateList = new ArrayList<>();
        aggregateList.add(moveUp);
        aggregateList.add(moveDown);
        aggregateList.add(moveLeft);
        aggregateList.add(moveRight);
        aggregateList.add(nonMove);
        return aggregateList;
    }

}
