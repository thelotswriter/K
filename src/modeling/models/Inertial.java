package modeling.models;

import instructions.Instruction;
import modeling.Model;
import processTree.ThingNode;
import processTree.toolNodes.AttributeConverter;

import java.util.List;

public class Inertial extends Model
{

    private int[] momentum;

    public Inertial(ThingNode thingToModel)
    {
        super(thingToModel);
        if(thingToModel.hasAttribute("momentum"))
        {
            momentum = AttributeConverter.convertToIntArray(thingToModel.getAttribute("momentum"));
        } else if(thingToModel.hasAttribute("location"))
        {
            int[] location = AttributeConverter.convertToIntArray(thingToModel.getAttribute("location"));
            momentum = new int[location.length];
        } else
        {
            momentum = new int[1];
        }
    }

    @Override
    public ThingNode generateFutureState(int time, List<Instruction> actions)
    {
        ThingNode futureThing = new ThingNode(getThingBeingModeled());
        if(getThingBeingModeled().hasAttribute("location"))
        {
            int speed = 1;
            if(getThingBeingModeled().hasAttribute("speed"))
            {
                speed = AttributeConverter.convertToInt(getThingBeingModeled().getAttribute("speed"));
            }
            int[] newLocation = AttributeConverter.convertToIntArray(getThingBeingModeled().getAttribute("location"));
            for(int i = 0; i < momentum.length; i++)
            {
                newLocation[i] += momentum[i] * speed;
            }
            futureThing.setAttribute("location", AttributeConverter.convertToAttribute(newLocation));
        }
        return futureThing;
    }

    @Override
    public ThingNode generateFutureState(int time)
    {
        ThingNode futureThing = new ThingNode(getThingBeingModeled());
        if(getThingBeingModeled().hasAttribute("location"))
        {
            int speed = 1;
            if(getThingBeingModeled().hasAttribute("speed"))
            {
                speed = AttributeConverter.convertToInt(getThingBeingModeled().getAttribute("speed"));
            }
            int[] newLocation = AttributeConverter.convertToIntArray(getThingBeingModeled().getAttribute("location"));
            for(int i = 0; i < momentum.length; i++)
            {
                newLocation[i] += momentum[i] * speed;
            }
            futureThing.setAttribute("location", AttributeConverter.convertToAttribute(newLocation));
        }
        return futureThing;
    }
}
