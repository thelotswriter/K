package thingNodes.CategoryNodes;

import processTree.ProcessNode;
import processTree.ThingNode;

import java.util.List;
import java.util.Map;

public abstract class GameNode extends ThingNode
{

    public GameNode(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        super.addCategory("world");
        super.addCategory("gameNode");
    }

    /**
     * Starts the associated game
     */
    public abstract void startGame();

}
