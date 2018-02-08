package modeling;

import instructions.Instruction;
import modeling.models.Grid2DChaser;
import modeling.models.Unchanging;
import processTree.ThingNode;

import java.util.ArrayList;
import java.util.List;

public class TempModelAggregator
{

    private Model chosenModel;
    private ThingNode worldCopy;

    public TempModelAggregator(ThingNode thingToBeModeled, ThingNode thingsWorld)
    {
        if(thingToBeModeled.hasAttribute("behavior") && thingToBeModeled.getAttribute("behavior").equalsIgnoreCase("intelligent"))
        {
            chosenModel = new Grid2DChaser(thingToBeModeled);
        } else
        {
            chosenModel = new Unchanging(thingToBeModeled);
        }
        worldCopy = new ThingNode(thingsWorld);
    }

    public List<ThingNode> generateFutureWorlds(List<Instruction> action)
    {
        ThingNode actingWorldCopy = new ThingNode(worldCopy);
        ThingNode futureChosenThing = chosenModel.generateFutureState(1, action);
        int time = chosenModel.determineBestTime(action);
        actingWorldCopy.removeElement(actingWorldCopy.getThing(futureChosenThing.getName()));
//        List<ThingNode> worldElements = worldCopy.getThingElements();
        List<ThingNode> worldElements = new ArrayList<>();
        worldElements.addAll(actingWorldCopy.getThingElements());
        List<ThingNode> futureElements = new ArrayList<>();
        for(ThingNode worldElement : worldElements)
        {
            if(worldElement.isPlural())
            {
                List<ThingNode> singleElements = worldElement.getThingElements();
                List<ThingNode> futureSingleElements = new ArrayList<>();
                for(ThingNode singleElement : singleElements)
                {
                    Model model = selectModel(singleElement);
                    futureSingleElements.add(model.generateFutureState(time));
                }
                worldElement.removeElements();;
                for(ThingNode futureSingleElement : futureSingleElements)
                {
                    worldElement.addElement(futureSingleElement);
                }
                futureElements.add(worldElement);
            } else
            {
                Model model = selectModel(worldElement);
                futureElements.add(model.generateFutureState(time));
            }
        }
        actingWorldCopy.removeElements();
        for(ThingNode element : futureElements)
        {
            actingWorldCopy.addElement(element);
        }
        actingWorldCopy.addElement(futureChosenThing);
        List<ThingNode> futureWorlds = new ArrayList<>();
        futureWorlds.add(actingWorldCopy);
        return futureWorlds;
    }

    private Model selectModel(ThingNode thingToModel)
    {
        if(thingToModel.hasAttribute("behavior") && thingToModel.getAttribute("behavior").equalsIgnoreCase("intelligent"))
        {
            return new Grid2DChaser(thingToModel);
        } else
        {
            return new Unchanging(thingToModel);
        }

    }

}
