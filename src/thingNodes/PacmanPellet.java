package thingNodes;

import processTree.ProcessNode;
import processTree.ThingNode;

import java.util.List;
import java.util.Map;

public class PacmanPellet extends ThingNode
{

    public PacmanPellet(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("pellet");
        addCategory("coin");
    }

}
