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

    /**
     * Determines an ideal time for the model (and associated models) to run
     * @param actions The combination of actions the thing modeled plans to make
     * @return An integer representing how long the thing modeled will run (and therefore how long other, related models should run
     */
    public int determineBestTime(List<Instruction> actions)
    {
        return 1;
    }

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
