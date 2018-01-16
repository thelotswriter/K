package modeling;

import instructions.Instruction;
import processTree.ThingNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a model which can be used to predict future model states
 */
public abstract class Model
{

    private ThingNode thingModeled;

    public Model(ThingNode thingToModel)
    {
        this.thingModeled = thingToModel;
    }

    public abstract int determineBestTime(List<Instruction> actions);

    public abstract ThingNode generateFutureState(int time, List<Instruction> actions);

    public abstract ThingNode generateFutureState(int time);

    public List<ThingNode> modelElements(int time, List<ThingNode> children)
    {
        List<ThingNode> modelChildren = new ArrayList<>();
        TempModelAggregator aggregator = TempModelAggregator.getInstance();
        for(ThingNode child : children)
        {
            Model childModel = aggregator.getModel(child);
            modelChildren.add(childModel.generateFutureState(time));
        }
        return modelChildren;
    }

    public ThingNode getThingBeingModeled()
    {
        return thingModeled;
    }
}
