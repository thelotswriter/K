package thingNodes.frogger;

import frogger.FroggerMain;
import processTree.ProcessNode;
import processTree.ThingNode;
import thingNodes.CategoryNodes.GameNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FroggerGame extends GameNode
{

    FroggerMain frogger;
    FroggerPlayer player;
    FroggerCar car;
    FroggerLog log;

    public FroggerGame(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("FroggerGame");
        frogger = null;
    }


    public void startGame()
    {

        if (frogger == null)
        {
            frogger = new FroggerMain();
        }

        frogger.run();
        //addElements need to add the game elements to the wide world
    }

    public void setAttributes()
    {
        setAttribute("goal", "approach lilypads");
        setAttribute("dimensions", FroggerMain.WORLD_WIDTH + "," + FroggerMain.WORLD_HEIGHT);
        setAttribute("grid", "13,13");
    }

    private void addElements()
    {
        player = new FroggerPlayer(this, null, null, getPlayerAttributes(), 1);

    }

    private Map<String, String> getPlayerAttributes()
    {
        Map<String, String> attributes = new HashMap();
        String dimensions = FroggerMain.BLOCK_SIZE + "," + FroggerMain.BLOCK_SIZE;
        attributes.put("dimensions", dimensions);
        attributes.put()
        return attributes;
    }

    public FroggerMain getGame() {
        return frogger;
    }
}
