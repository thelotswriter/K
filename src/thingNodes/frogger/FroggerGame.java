package thingNodes.frogger;

import frogger.*;
import jig.engine.util.Vector2D;
import processTree.ProcessNode;
import processTree.ThingNode;
import thingNodes.CategoryNodes.GameNode;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FroggerGame extends GameNode
{

    FroggerMain frogger;
    FroggerPlayer player;
    FroggerCars cars;
    FroggerRiver river;
    FroggerLogs logs;
    FroggerLilyPads lilyPads;

    FroggerHooks hooks;

    public FroggerGame(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("FroggerGame");
        frogger = null;
        setAttributes();
    }


    public void startGame()
    {

        if (frogger == null)
        {
            frogger = new FroggerMain();
        }
        hooks = frogger.getFroggerHooks();
        EventQueue.invokeLater(() -> {
            frogger.run();
        });
        try
        {
            Thread.sleep(1000);
            addElements();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        //addElements need to add the game elements to the wide world
    }

    public void setAttributes()
    {
        setAttribute("goal", "avoid car");
        setAttribute("dimensions", (FroggerMain.WORLD_WIDTH) + "," + (FroggerMain.WORLD_HEIGHT));
        setAttribute("grid", "13,13");
    }

    private void addElements()
    {
        player = new FroggerPlayer(this, null, null, getPlayerAttributes(), 1);
        String dimensions = FroggerMain.BLOCK_SIZE + "," + FroggerMain.BLOCK_SIZE;
        player.setAttribute("dimensions", dimensions);
        addElement(player);
//        river = new FroggerRiver(this, null, null, null, 1);
//        addElement(river);
        addMovingEntities();
    }

    public void update()
    {
        Map<String, String> pAttributes = getPlayerAttributes();
        for(String key : pAttributes.keySet())
        {
            player.setAttribute(key, pAttributes.get(key));
        }

        addMovingEntities();

        //addElement(river);
    }

    private Map<String, String> getPlayerAttributes()
    {
        Map<String, String> attributes = new HashMap();
        attributes.put("location", hooks.getPlayerLocation());
//        attributes.put("speed", hooks.getPlayerSpeed());
        return attributes;
    }

    private void addMovingEntities()
    {
        cars = new FroggerCars(this, null, null, null, 1);
        logs = new FroggerLogs(this, null, null, null, 1);
        lilyPads = new FroggerLilyPads(this, null, null, null, 1);
        for (MovingEntity me : hooks.getObjects()) {
            String speed = Double.toString(me.getVelocity().getX());
            if (me.getPosition().getX() < 0 || me.getPosition().getX() > FroggerMain.WORLD_WIDTH ||
                    me.getPosition().getY() < 0 || me.getPosition().getY() > FroggerMain.WORLD_HEIGHT) {
                continue;
            }
            String location = Math.round(me.getPosition().getX()) +
                    "," + Math.round(me.getPosition().getY());
            ThingNode thingNode = null;

            if (me instanceof CopCar) {
                thingNode = new FroggerCopCar(cars, null, null, null, 1);
                cars.addElement(thingNode);

            } else if (me instanceof Car) {
                thingNode = new FroggerCar(cars, null, null, null, 1);
                cars.addElement(thingNode);

            } else if (me instanceof Truck) {
                thingNode = new FroggerTruck(cars, null, null, null, 1);
                cars.addElement(thingNode);

            } else if (me instanceof ShortLog) {
                thingNode = new FroggerShortLog(logs, null, null, null, 1);
                logs.addElement(thingNode);

            } else if (me instanceof LongLog) {
                thingNode = new FroggerLongLog(logs, null, null, null, 1);
                logs.addElement(thingNode);

            } else if (me instanceof Goal) {
                thingNode = new FroggerLilyPad(lilyPads, null, null, null, 1);
                lilyPads.addElement(thingNode);
            }

            if (thingNode != null) {
                thingNode.setAttribute("speed", speed);
                thingNode.setAttribute("location", location);
                addElement(thingNode);
            }
        }
//        addElement(cars);
//        addElement(logs);
//        addElement(lilyPads);
    }

    public FroggerMain getGame() {
        return frogger;
    }
}
