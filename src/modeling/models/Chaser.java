package modeling.models;

import instructions.Instruction;
import modeling.Model;
import processTree.ThingNode;

import java.util.List;

public class Chaser extends Model
{

    public Chaser(ThingNode thingToModel)
    {
        super(thingToModel);
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
