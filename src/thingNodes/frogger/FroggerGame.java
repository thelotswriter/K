package thingNodes.frogger;

import frogger.Main;
import processTree.ProcessNode;
import processTree.ThingNode;
import thingNodes.CategoryNodes.GameNode;

import java.util.List;
import java.util.Map;

public class FroggerGame extends GameNode
{

    public FroggerGame(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("FroggerGame");
    }


    public void startGame()
    {

    }

    public void setAttributes()
    {
        setAttribute("goal", "approach lilypads");
        setAttribute("dimensions", Main.WORLD_WIDTH + "," + Main.WORLD_HEIGHT);
    }
}
