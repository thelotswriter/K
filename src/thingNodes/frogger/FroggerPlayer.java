package thingNodes.frogger;

import processTree.ProcessNode;
import processTree.ThingNode;

import java.util.List;
import java.util.Map;

public class FroggerPlayer extends ThingNode
{

    public FroggerPlayer(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("Player");
        setAttribute("move", "both,both");
    }
}
