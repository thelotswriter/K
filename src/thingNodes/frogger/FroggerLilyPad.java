package thingNodes.frogger;

import frogger.FroggerMain;
import processTree.ProcessNode;
import processTree.ThingNode;

import java.util.List;
import java.util.Map;

public class FroggerLilyPad extends ThingNode
{

    public FroggerLilyPad(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("lilypad");
        addCategory("goal");
        setAttribute("move", "neither,neither");
        setAttribute("dimensions",
                FroggerMain.BLOCK_SIZE + "," + FroggerMain.BLOCK_SIZE);
    }
}
