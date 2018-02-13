package thingNodes;

import processTree.ProcessNode;
import processTree.ThingNode;
import processTree.ThingsNode;

import java.util.List;
import java.util.Map;

public class PacmanPellets extends ThingsNode
{

    public PacmanPellets(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("pellets");
        addCategory("coin");
    }

}
