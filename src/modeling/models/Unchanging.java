package modeling.models;

import instructions.Instruction;
import modeling.Model;
import processTree.ThingNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Unchanging extends Model
{

    public Unchanging(ThingNode thingToModel)
    {
        super(thingToModel);
    }

    @Override
    public ThingNode generateFutureState(int time, List<Instruction> actions)
    {
        return new ThingNode(getThingBeingModeled());
    }

    @Override
    public ThingNode generateFutureState(int time)
    {
        return new ThingNode(getThingBeingModeled());
    }


}
