package modeling.models;

import instructions.Instruction;
import instructions.InstructionType;
import modeling.Model;
import processTree.ThingNode;

import java.util.List;

public class Grid2DChaser extends Grid2D
{

    public Grid2DChaser(ThingNode thingToModel)
    {
        super(thingToModel);
    }

    @Override
    public ThingNode generateFutureState(int time)
    {
        return null;
    }

}
