package thingNodes.frogger;

import frogger.FroggerHooks;
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
    FroggerCars cars;
    FroggerShortLog log;

    FroggerHooks hooks;

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
        hooks = frogger.getFroggerHooks();
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
        String dimensions = FroggerMain.BLOCK_SIZE + "," + FroggerMain.BLOCK_SIZE;
        player.setAttribute("dimensions", dimensions);
        addElement(player);

        cars = new FroggerCars(this, null, null, null, 1);


    }

    private Map<String, String> getPlayerAttributes()
    {
        Map<String, String> attributes = new HashMap();
        attributes.put("location", hooks.getPlayerLocation());
        attributes.put("speed", hooks.getPlayerSpeed());
        return attributes;
    }

    private List<FroggerCar> getCars() {
        return null;
    }

    public FroggerMain getGame() {
        return frogger;
    }
}
