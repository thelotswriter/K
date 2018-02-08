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
//        String[] subjectLocationStrings = getSubject().getAttribute("location").split(",");
//        int[] subjectLocation = new int[subjectLocationStrings.length];
//        for(int i = 0; i < subjectLocation.length; i++)
//        {
//            subjectLocation[i] = Integer.parseInt(subjectLocationStrings[i]);
////            System.out.print(subjectLocation[i]);
////            System.out.print(", ");
//        }
//        double urgencyTotal = 0;
//        for(Model model : models)
//        {
//            double cUrcency = model.getDistance(subjectLocation);
////            System.out.print("Single Distance: " + cUrcency + " | ");
//            if(cUrcency == 0)
//            {
//                urgencyTotal = Double.MAX_VALUE;
//            } else
//            {
//                urgencyTotal += 1 / cUrcency;
//            }
//        }
////        System.out.println(urgencyTotal);
//        setUrgencey(urgencyTotal);
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
        }
        setUrgencey(urgencyTotal);
        return instructionPackets;
    }

}
