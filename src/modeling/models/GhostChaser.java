package modeling.models;

import instructions.Instruction;
import modeling.Model;
import processTree.ThingNode;

import java.util.List;

public class GhostChaser extends Model
{

    public GhostChaser(ThingNode thingToBeModeled)
    {
        super(thingToBeModeled);
    }

    @Override
    public ThingNode generateFutureState(int time, List<Instruction> actions) {
        return null;
    }

    @Override
    public ThingNode generateFutureState(int time) {
        return null;
    }
}
