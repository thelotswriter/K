package thingNodes.frogger;

import frogger.FroggerMain;
import processTree.ProcessNode;
import processTree.ThingNode;

import java.util.List;
import java.util.Map;

public class FroggerLongLog extends ThingNode
{
    public FroggerLongLog(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("log");
        addCategory("platform");
        setAttribute("move", "both,neither");
        setAttribute("dimensions",
                (FroggerMain.BLOCK_SIZE * 4) + "," + FroggerMain.BLOCK_SIZE);
    }
}
