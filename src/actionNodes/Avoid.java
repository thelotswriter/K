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

    // Weights determining direction. Variable sideWeight is multiplied by the magnitude of the and used to make
    // instructions to go left/right.
    private double sideWeight = 0.01;
    private double momentumWeight = 0.1;

    private List<Model> models;

    public Avoid(CommandNode root, ActionNode parent, ThingNode subject, ThingNode directObject, ThingNode indirectObject,
                 List<Adverb> adverbs, List<ActionElement> elements, double confidence, double priority, double urgency)
            throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, parent, subject, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
    }

    @Override
    public void initialize() throws NotAnActionNodeException, UnknownActionException, UnreadableActionNodeException, IOException {
        super.initialize();
        // Determine which sort of model should be used to avoid. Currently we only have one model to use, so we'll use that.
        // In the future, this may be expanded to use a search tree or some other method to select the best model
        models = new ArrayList<>();
        if(getDirectObject().isPlural())
        {
            for(ProcessNode singleObject : getDirectObject().getElements())
            {
                models.add(ModelPicker.getInstance().getModel((ThingNode) getDirectObject().getParent(), getSubject(),
                        (ThingNode) singleObject, getIndirectObject()));
            }
        } else
        {
            models.add(ModelPicker.getInstance().getModel((ThingNode) getDirectObject().getParent(), getSubject(), getDirectObject(), getIndirectObject()));
        }
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
        double urgencyTotal = 0;
        if(getDirectObject().isPlural())
        {
            for(ThingNode singleThing : getDirectObject().getThingElements())
            {
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
                    urgencyTotal += 1 / (dist * dist);
                }
            }
        } else
        {
            int[] thingLocation = AttributeConverter.convertToIntArray(getDirectObject().getAttribute("location"));
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
                urgencyTotal += 1 / (dist * dist);
            }
        }
        setUrgencey(urgencyTotal);
        return instructionPackets;
    }

}
