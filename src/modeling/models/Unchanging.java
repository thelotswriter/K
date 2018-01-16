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
    public ThingNode generateFutureState(List<Instruction> actions)
    {
        return new ThingNode(getThingBeingModeled());
    }

    @Override
    public ThingNode generateFutureWorldState(List<ThingNode> changedThings) {
        return new ThingNode(getThingBeingModeled());
    }

}
