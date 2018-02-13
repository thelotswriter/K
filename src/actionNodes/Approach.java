package actionNodes;

import instructions.InstructionPacket;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ThingNode;
import processTree.subActionNodes.PlannableActionNode;
import processTree.toolNodes.AttributeConverter;
import words.Adverb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Approach extends PlannableActionNode
{

    public Approach(CommandNode root, ActionNode parent, ThingNode subject, ThingNode directObject, ThingNode indirectObject,
                 List<Adverb> adverbs, List<ActionElement> elements, double confidence, double priority, double urgency)
            throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, parent, subject, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
    }

    public double getMaxUrgency()
    {
        return 100;
    }

    @Override
    public List<InstructionPacket> planningRun()
    {
        List<InstructionPacket> instructionPackets = new ArrayList<>();
        int[] subjectLocation = AttributeConverter.convertToIntArray(getSubject().getAttribute("location"));
        double speed = 1;
        if(getSubject().hasAttribute("speed"))
        {
            speed = AttributeConverter.convertToInt(getSubject().getAttribute("speed"));
            if(speed == 0)
            {
                speed = 1;
            }
        }
        double urgencyTotal = 0;
        if(getDirectObject().isPlural())
        {
            double nThings = getDirectObject().getThingElements().size();
            if(nThings <= 0)
            {
                nThings = 1;
            }
            for(ThingNode singleThing : getDirectObject().getThingElements())
            {
                double thingSpeed = 1;
                if(singleThing.hasAttribute("speed"))
                {
                    thingSpeed = AttributeConverter.convertToInt(singleThing.getAttribute("speed"));
                    if(thingSpeed == 0)
                    {
                        thingSpeed = 1;
                    }
                }
                int[] thingLocation = AttributeConverter.convertToIntArray(singleThing.getAttribute("location"));
                double dist = 0;
                for(int i = 0; i < subjectLocation.length; i++)
                {
                    dist += Math.abs(subjectLocation[i] - thingLocation[i]);
                }
                if(dist == 0)
                {
                    urgencyTotal = Double.MAX_VALUE;
                } else
                {
                    urgencyTotal += 1 - thingSpeed / (speed * dist * dist);
                }
            }
            urgencyTotal /= nThings;
        } else
        {
            int[] thingLocation = AttributeConverter.convertToIntArray(getDirectObject().getAttribute("location"));
            double thingSpeed = 1;
            if(getDirectObject().hasAttribute("speed"))
            {
                thingSpeed = AttributeConverter.convertToInt(getDirectObject().getAttribute("speed"));
                if(thingSpeed == 0)
                {
                    thingSpeed = 1;
                }
            }
            double dist = 0;
            for(int i = 0; i < subjectLocation.length; i++)
            {
                dist += Math.abs(subjectLocation[i] - thingLocation[i]);
            }
            if(dist == 0)
            {
                urgencyTotal = 0;
            } else
            {
                urgencyTotal += 1 - (thingSpeed / (speed * dist * dist));
            }
        }
        setUrgencey(urgencyTotal);
        return instructionPackets;
    }
}
