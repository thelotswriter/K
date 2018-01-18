package modeling.models;

import instructions.Instruction;
import modeling.Model;
import processTree.ThingNode;

import java.util.List;

public class Escaper extends Model
{

    public Escaper(ThingNode thingToBeModeled)
    {
        super(thingToBeModeled);
    }

    @Override
    public int determineBestTime(List<Instruction> actions) {
        return 0;
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
