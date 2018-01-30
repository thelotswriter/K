package thingNodes.frogger;

import frogger.FroggerMain;
import processTree.ProcessNode;
import processTree.ThingNode;

import java.util.List;
import java.util.Map;

public class FroggerCar extends ThingNode
{

    public FroggerCar(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("car");
        addCategory("enemy");
        setAttribute("move", "both,neither");
        setAttribute("dimensions",
                FroggerMain.BLOCK_SIZE + "," + FroggerMain.BLOCK_SIZE);
        setAttribute("goal","approach Player");
    }
}
