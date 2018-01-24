package thingNodes.frogger;

import processTree.ProcessNode;
import processTree.ThingNode;

import java.util.List;
import java.util.Map;

public class FroggerLogs extends ThingNode
{

    public FroggerLogs (ProcessNode parent, List<ThingNode> elements,
                        java.util.List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("logs");
        addCategory("platform");
    }
}
