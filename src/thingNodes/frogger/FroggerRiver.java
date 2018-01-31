package thingNodes.frogger;

import frogger.FroggerMain;
import processTree.ProcessNode;
import processTree.ThingNode;

import java.util.List;
import java.util.Map;

public class FroggerRiver extends ThingNode
{
    public FroggerRiver(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("river");
        addCategory("obstacle");
        setAttribute("dimensions", FroggerMain.WORLD_WIDTH + ","
        + (FroggerMain.BLOCK_SIZE * 7));
        setAttribute("boundaries", (FroggerMain.BLOCK_SIZE * 1) + ","
        + (FroggerMain.BLOCK_SIZE * 7));//format top_boundary,bottom_boundary
        setAttribute("location", (FroggerMain.BLOCK_SIZE * 7) + ","
        + ((FroggerMain.BLOCK_SIZE * 1)));
    }
}
