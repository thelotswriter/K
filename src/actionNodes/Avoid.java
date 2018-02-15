package actionNodes;

import instructions.Instruction;
import instructions.InstructionPacket;
import instructions.InstructionType;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ProcessNode;
import processTree.ThingNode;
import processTree.subActionNodes.PlannableActionNode;
import processTree.toolNodes.AttributeConverter;
import processTree.toolNodes.Model;
import processTree.toolNodes.ModelPicker;
import subModel.Discrete2DSpatialModel;
import words.Adverb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Avoid extends PlannableActionNode
{

    public Avoid(CommandNode root, ActionNode parent, ThingNode subject, ThingNode directObject, ThingNode indirectObject,
                 List<Adverb> adverbs, List<ActionElement> elements, double confidence, double priority, double urgency)
            throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, parent, subject, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
    }

    @Override
    public void initialize() throws NotAnActionNodeException, UnknownActionException, UnreadableActionNodeException, IOException
    {
        super.initialize();
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
        double closestWeightedDist = Double.MAX_VALUE;
        if(getDirectObject().isPlural())
        {
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
                    urgencyTotal += thingSpeed / (speed * dist * dist);
                }
                if(closestWeightedDist > dist * speed / thingSpeed)
                {
                    closestWeightedDist = dist * speed / thingSpeed;
                }
            }
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
                urgencyTotal = Double.MAX_VALUE;
            } else
            {
                urgencyTotal += thingSpeed / (speed * dist * dist);
            }
            closestWeightedDist = dist * speed / thingSpeed;
        }
        setUrgencey(sigmoid(closestWeightedDist, 24*5, 24));
//        setUrgencey(urgencyTotal);
        return instructionPackets;
    }

    private double sigmoid(double x, double safeDist, double spread)
    {
        double s = 1 - (1 / (1 + Math.exp((safeDist - x) / spread)));
        return s;
    }

}
