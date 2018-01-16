package modeling;

import processTree.ThingNode;

public class TempModelAggregator
{

    private static TempModelAggregator SINGLETON = null;

    private TempModelAggregator()
    {

    }

    public static TempModelAggregator getInstance()
    {
        if(SINGLETON == null)
        {
            SINGLETON = new TempModelAggregator();
        }
        return SINGLETON;
    }

    public Model getModel(ThingNode thingToModel)
    {
        return null;
    }
}
